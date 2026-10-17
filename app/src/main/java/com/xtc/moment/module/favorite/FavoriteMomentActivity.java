package com.xtc.moment.module.favorite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseInteractActivity;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.bean.CommentEvent;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 收藏的动态页面：纯本地数据，不请求网络。
 *
 * <p>与主列表不同，这里动态被删除后条目仍然保留，卡片下方提示「该动态已被删除」，点赞与评论都不再
 * 响应，但最后一次的点赞数与评论列表照常展示（数据来自收藏时留下的快照）。
 */
public class FavoriteMomentActivity extends BaseInteractActivity<IFavoriteMomentView, FavoriteMomentPresenter>
        implements IFavoriteMomentView {

    private static final String TAG = "FavoriteMomentActivity";

    private RecyclerView rvFavorite;
    private FavoriteMomentAdapter adapter;
    private VerticallyLinearLayoutManager layoutManager;
    private View footerView;
    private TextView tvLoadMore;

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, FavoriteMomentActivity.class));
    }

    @Override
    public FavoriteMomentPresenter createPresenter() {
        return new FavoriteMomentPresenter(getApplicationContext());
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    /** 纯本地读取收藏数据，不申请运行时权限（与搜索页、详情页一致）。 */
    @Override
    protected void dealPermission() {
        LogUtil.d(TAG, "收藏的动态界面无需申请权限");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_favorite_moment);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    public void beforeDealPermission() {
    }

    @Override
    public void requestPermission() {
    }

    @Override
    public void refusePermission() {
        finish();
    }

    @Override
    public void afterDealPermission() {
    }

    @Override
    public void initView() {
        this.etHint = (EditText) findViewById(R.id.et_hint);
        this.rvFavorite = (RecyclerView) findViewById(R.id.rv_favorite_moment);
        initEditText();
        initRecyclerView();
    }

    @Override
    public void initData() {
        this.presenter.loadFavorites();
    }

    private void initRecyclerView() {
        this.layoutManager = new VerticallyLinearLayoutManager(this);
        this.rvFavorite.setLayoutManager(this.layoutManager);
        this.adapter = new FavoriteMomentAdapter(this, this.layoutManager,
                this.presenter.getLikeDrawableCache());
        this.rvFavorite.setAdapter(this.adapter);
        this.adapter.initSwitch();
        initCommonRvListener(this.adapter);
        initShareViewRvListener(this.adapter);
        setHeaderView();
        setFooterView();
        setEmptyView();
    }

    /** 二级页面没有标题栏，标题用列表头部的圆形标题条（与个人动态页一致）。 */
    private void setHeaderView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.header_recycle_share, this.rvFavorite, false);
        this.adapter.setHeaderView(headerView);
        TextView tvHeader = (TextView) headerView.findViewById(R.id.tv_header);
        if (tvHeader != null) {
            tvHeader.setText(R.string.favorite_moment);
        }
    }

    private void setFooterView() {
        this.footerView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.footer_recycle_moment, this.rvFavorite, false);
        this.adapter.setFooterView(this.footerView);
        this.tvLoadMore = (TextView) this.footerView.findViewById(R.id.tv_load_more);
        // 一次性读完，不需要「加载中」，只在有数据时显示「没有更多了」。
        this.footerView.setVisibility(View.GONE);
    }

    private void setEmptyView() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.view_favorite_moment_empty,
                this.rvFavorite, false);
        this.adapter.setEmptyView(emptyView);
    }

    @Override
    public void onFavoritesLoaded(List<DbMoment> moments, Map<String, List<DbLikeMessage>> praiseRecords,
            Set<String> deletedIds) {
        LogUtil.i(TAG, "onFavoritesLoaded: size = " + (moments == null ? 0 : moments.size()));
        this.adapter.setFavorites(moments, praiseRecords, deletedIds);
        if (moments != null && !moments.isEmpty()) {
            if (this.tvLoadMore != null) {
                this.tvLoadMore.setText(R.string.moment_nomore);
            }
            this.footerView.setVisibility(View.VISIBLE);
            return;
        }
        this.footerView.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        if (eventData.getType() != EventData.CHANGE_FAVORITE) {
            return;
        }
        if (!(eventData.getData() instanceof DbMoment)) {
            return;
        }
        // 收藏/取消收藏后重排列表，保证顺序与内容与库里一致。
        this.presenter.loadFavorites();
    }

    @Override
    public void loadCommentSuccess(DbMoment moment) {
        if (moment != null && moment.getMomentId() != null) {
            this.adapter.notifyItemByMomentId(moment.getMomentId());
        }
    }

    @Override
    public void likeSuccess(DbLikeMessage likeMessage) {
        if (likeMessage == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = FavoriteMomentActivity.this.presenter
                        .getMomentById(likeMessage.getMomentId());
                final List<DbLikeMessage> likeMessages = FavoriteMomentActivity.this.presenter
                        .getLikeMessageByMomentId(likeMessage.getMomentId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moments != null && !moments.isEmpty()) {
                            FavoriteMomentActivity.this.adapter.refreshData(moments.get(0));
                        }
                        if (likeMessages != null && !likeMessages.isEmpty()) {
                            Map<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                            likeMap.put(likeMessage.getMomentId(), likeMessages);
                            FavoriteMomentActivity.this.adapter.refreshPraiseRecordMap(likeMap);
                        }
                        FavoriteMomentActivity.this.adapter
                                .notifyItemByMomentId(likeMessage.getMomentId());
                    }
                });
            }
        });
    }

    @Override
    public void cancelLikeSuccess(final DbMoment moment) {
        if (moment == null) {
            return;
        }
        this.adapter.setLastCancelTime(System.currentTimeMillis());
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = FavoriteMomentActivity.this.presenter
                        .getMomentById(moment.getMomentId());
                final List<DbLikeMessage> likeMessages = FavoriteMomentActivity.this.presenter
                        .getLikeMessageByMomentId(moment.getMomentId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moments != null && !moments.isEmpty()) {
                            FavoriteMomentActivity.this.adapter.refreshData(moments.get(0));
                        }
                        if (likeMessages != null && !likeMessages.isEmpty()) {
                            Map<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                            likeMap.put(moment.getMomentId(), likeMessages);
                            FavoriteMomentActivity.this.adapter.refreshPraiseRecordMap(likeMap);
                        }
                        FavoriteMomentActivity.this.adapter.refreshPraiseRecordMapCancel(
                                MomentApp.getWatchId());
                        FavoriteMomentActivity.this.adapter
                                .notifyItemByMomentId(moment.getMomentId());
                    }
                });
            }
        });
    }

    @Override
    public void likeError() {
        dealLikeError(null);
    }

    @Override
    public void likeError(String message) {
        dealLikeError(message);
    }

    @Override
    public void publishSuccess(DbMomentComment comment, String extra) {
        // 和主列表/详情页一致：先切到成功动画，动画结束后自动关掉「发布中」弹窗。
        showLoadingSuccess();
        this.adapter.addCommentData(comment);
        // 通知主列表把这条评论补上，返回时不会还是旧数据。
        EventBus.getDefault().post(new CommentEvent(extra, comment));
    }

    @Override
    public void publishFail(String message) {
        dismissLoading();
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void publishLimited() {
        dismissLoading();
        ToastUtil.showShort(MomentApp.getAppContext(), R.string.publish_comment_limit);
    }

    @Override
    public void publishInvalidate() {
        dismissLoading();
        ToastUtil.showShort(MomentApp.getAppContext(), R.string.publish_invalidate);
    }

    @Override
    public void removeComment(DbMomentComment comment) {
        this.adapter.removeComment(comment);
    }

    @Override
    public void removeCommentFail() {
        ToastUtil.showShortCover(this, getString(R.string.delete_fail));
    }

    @Override
    public void removeMoment(DbMoment moment) {
        // 收藏列表不提供删除入口，长按也已禁用，这里只做兜底。
        LogUtil.i(TAG, "removeMoment: ignore in favorite list");
    }

    @Override
    public void removeFail() {
        ToastUtil.showShortCover(this, getString(R.string.delete_fail));
    }

    @Override
    public void momentAlreadyDeleted() {
        dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.moment_already_deleted));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}

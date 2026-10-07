package com.xtc.moment.module.share;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseInteractActivity;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.CommentEvent;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.GlideUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分享到好友圈动态列表页面。
 */
public class ShareActivity extends BaseInteractActivity<IShareView, SharePresenter> implements IShareView {

    private static final long COUNT = 10;
    private static final String TAG = LogTag.tag("ShareActivity");

    AnimationDrawable animDrawable;
    boolean isCanceled = false;

    private long enterTime;
    private List<Friend> friendList;
    private long leaveTime;
    private ImageView loading;
    private View mFooterView;
    private boolean mFromMoment;
    private String mIconPath;
    private boolean mIsSelf;
    private String mName;
    private RecyclerView mRv;
    private ShareAdapter mRvAdapter;
    private TextView mTvHeader;
    private TextView mTvLoadMore;
    private String mWatchId;
    private long offset = 0;
    private boolean isRunning = false;

    @Override
    public SharePresenter createPresenter() {
        return new SharePresenter(getApplicationContext());
    }

    @Override
    public void loadCommentSuccess(DbMoment moment) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void beforeDealPermission() {
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_share);
        getWindow().setBackgroundDrawable(null);
        this.enterTime = System.currentTimeMillis();
        EventBus.getDefault().register(this);
        this.mWatchId = getIntent().getStringExtra(Constants.INTENT_EXTRA_WATCH_ID);
        this.mName = getIntent().getStringExtra(Constants.INTENT_EXTRA_NAME);
        this.mIconPath = getIntent().getStringExtra(Constants.INTENT_EXTRA_ICON_PATH);
        this.mIsSelf = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_IS_SELF, false);
        this.mFromMoment = getIntent().getBooleanExtra(Constants.INTENT_EXTRA_START_FROM_MOMENT, false);
        MomentBehavior.clickHeadState(this, this.mIsSelf ? 1 : 2);
        LogUtil.i(TAG, "mWatchId = " + this.mWatchId + ",mName = " + this.mName + ",mIconPath = " + this.mIconPath
                + ",mIsSelf = " + this.mIsSelf + ",mFromMoment = " + this.mFromMoment);
        initView();
    }

    @Override
    public void requestPermission() {
        requestRunTimePermission(PermissionStringUtils.SEND_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                PermissionStringUtils.checkPermissionForReTryBaseUrl(ShareActivity.this);
                ShareActivity.this.initData();
                LogUtil.d(TAG, "onGranted: requestPermission");
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                ShareActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
        if (this.mWatchId == null) {
            LogUtil.e("moment", "watchId信息为空");
            showNoMore();
            return;
        }
        LogUtil.d(TAG, "mRvAdapter = " + this.mRvAdapter);
        loadShareData(true);
    }

    private void loadShareData(boolean initSwitch) {
        if (initSwitch) {
            this.mRvAdapter.initSwitch();
        }
        this.presenter.loadMomentsFromNet(this.offset, COUNT, this.mWatchId, this.mRvAdapter.getLastData());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.leaveTime = System.currentTimeMillis();
        MomentBehavior.shareEnter(getApplication(), this.enterTime, this.leaveTime);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EventType eventType) {
        if (eventType.getType() == 2) {
            LogUtil.i(TAG, "receive contace update event");
            notifyAdapterContactChange();
        }
    }

    @Override
    public void notifyAdapterContactChange() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (friendList == null || friendList.size() <= 0) {
                    LogUtil.d(TAG, "friendList == null || friendList.size() <= 0");
                    mRvAdapter.notifyDataSetChanged();
                    return;
                }
                for (int i = 0; i < friendList.size(); i++) {
                    Friend friend = friendList.get(i);
                    if (mWatchId != null && mWatchId.equals(friend.getWatchId())) {
                        mIconPath = friend.getFriendIcon();
                        mName = friend.getName();
                        mRvAdapter.updateInfo(mIconPath, mName);
                        mRvAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        if (eventData.getType() == 1) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMoment)) {
                return;
            }
            LogUtil.i(TAG, "receive delete moment event:" + eventData.toString());
            removeMoment((DbMoment) eventData.getData());
            return;
        }
        if (eventData.getType() == 2) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            LogUtil.i(TAG, "receive delete moment event:" + eventData.toString());
            removeMomentComment((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == 3) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            LogUtil.i(TAG, "receive delete moment event:" + eventData.toString());
            addMomentComment((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == 12) {
            DbLikeMessage likeMessage = (DbLikeMessage) eventData.getData();
            if (likeMessage == null) {
                return;
            }
            List<DbLikeMessage> likeMaps = this.mRvAdapter.getLikeMaps(likeMessage.getMomentId());
            LogUtil.i(TAG, "现有 " + likeMessage);
            LogUtil.i(TAG, "现有 " + likeMaps);
            if (likeMaps == null) {
                new ArrayList<>();
                return;
            }
            for (int i = 0; i < likeMaps.size(); i++) {
                DbLikeMessage cachedLike = likeMaps.get(i);
                if (likeMessage.getMomentId().equals(cachedLike.getMomentId())
                        && likeMessage.getWatchId().equals(cachedLike.getWatchId())) {
                    likeMaps.remove(i);
                    break;
                }
            }
            HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
            likeMap.put(likeMessage.getMomentId(), likeMaps);
            this.mRvAdapter.refreshPraiseRecordMap(likeMap);
            LogUtil.i(TAG, "现有 " + likeMap.get(likeMessage.getMomentId()));
            this.mRvAdapter.notifyItemByMomentId(likeMessage.getMomentId());
            return;
        }
        if (eventData.getType() == 18) {
            if (eventData.getData() instanceof DbMoment) {
                DbMoment moment = (DbMoment) eventData.getData();
                LogUtil.i(TAG, "receive visibleChange = " + moment);
                this.mRvAdapter.refreshVisible(moment);
            }
            return;
        }
        if (eventData.getType() == 22) {
            if (eventData.getData() instanceof DbMoment) {
                DbMoment moment = (DbMoment) eventData.getData();
                LogUtil.d(ReminderHelper.M_TAG, "个人动态更新温馨提示：" + moment);
                this.mRvAdapter.refreshReminder(moment);
            }
            return;
        }
        if (eventData.getType() == 21) {
            LogUtil.i(ReminderHelper.M_TAG, "温馨提示配置结束, check share reminder");
            checkUnExecutedReminder();
        }
    }

    private void addMomentComment(DbMomentComment comment) {
        this.mRvAdapter.addComment(comment);
    }

    private void removeMomentComment(DbMomentComment comment) {
        this.mRvAdapter.removeComment(comment);
    }

    @Override
    public void initView() {
        this.mRv = (RecyclerView) findViewById(R.id.rv);
        this.mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager.getChildCount() > 0 && newState == 0) {
                    if (recyclerView.getChildLayoutPosition(recyclerView.getChildAt(recyclerView.getChildCount() - 1))
                            >= layoutManager.getItemCount() - 1) {
                        showLoadMore();
                        if (!isRunning) {
                            loadMoreData();
                        } else {
                            LogUtil.i(TAG, TAG + " 加载中...");
                        }
                    } else {
                        showNoFooter();
                    }
                }
                GlideUtils.setImageDelayedLoad(ShareActivity.this, newState);
            }
        });
        initRv();
        setHeaderView();
        setFooterView();
        initEditText();
    }

    @Override
    protected void initEditText() {
        this.etHint = (EditText) findViewById(R.id.et_hint);
        super.initEditText();
    }

    public void initRv() {
        VerticallyLinearLayoutManager layoutManager = new VerticallyLinearLayoutManager(this);
        this.mRv.setLayoutManager(layoutManager);
        this.mRvAdapter = new ShareAdapter(this, this.mIsSelf, this.mName, this.mIconPath, layoutManager,
                this.mWatchId, this.presenter.getLikeDrawableCache());
        this.mRv.setAdapter(this.mRvAdapter);
        initCommonRvListener(this.mRvAdapter);
        initShareViewRvListener(this.mRvAdapter);
    }

    private void setHeaderView() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.header_recycle_share, this.mRv, false);
        this.mRvAdapter.setHeaderView(headerView);
        this.mTvHeader = (TextView) headerView.findViewById(R.id.tv_header);
        this.mTvHeader.setText(getString(this.mIsSelf ? R.string.my_public : R.string.friend_public));
    }

    private void setFooterView() {
        this.mFooterView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.footer_recycle_moment, this.mRv, false);
        this.mRvAdapter.setFooterView(this.mFooterView);
        this.loading = (ImageView) this.mFooterView.findViewById(R.id.iv_loading);
        this.mTvLoadMore = (TextView) this.mFooterView.findViewById(R.id.tv_load_more);
        this.mRvAdapter.setFooterView(this.mFooterView);
    }

    @Override
    public void loadSuccess(List<DbMoment> moments) {
        LogUtil.i(TAG, "loadSuccess offset:" + this.offset + ",data size:" + (moments == null ? 0 : moments.size()));
        this.isRunning = false;
        if (moments == null || moments.isEmpty()) {
            showNoMore();
            return;
        }
        int size = moments.size();
        this.mRvAdapter.addData(moments);
        if (!TextUtils.isEmpty(this.mWatchId)) {
            ArrayList<String> momentIds = new ArrayList<>();
            for (int i = 0; i < moments.size(); i++) {
                DbMoment moment = moments.get(i);
                if (!TextUtils.isEmpty(moment.getMomentId())) {
                    momentIds.add(moment.getMomentId());
                }
            }
            this.presenter.getPraiseRecord(momentIds, this.mWatchId);
        }
        if (size % COUNT != 0) {
            LogUtil.d(TAG, "showNoMore");
            showNoMore();
        } else {
            showLoadMore();
        }
        this.offset += size;
        if (this.mFromMoment) {
            checkUnExecutedReminder();
        } else {
            this.presenter.getReminderConfig(Constants.RCType.INIT, 21);
        }
    }

    private void checkUnExecutedReminder() {
        this.presenter.dealUnExecutedImReminder(true);
    }

    private void loadMoreData() {
        LogUtil.d(TAG, "loadMoreData() called");
        this.isRunning = true;
        this.mRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadShareData(false);
            }
        }, 1000L);
    }

    private void showLoadMore() {
        this.mTvLoadMore.setText(R.string.moment_loading);
        this.loading.setVisibility(View.VISIBLE);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.7f, 100);
        this.loading.setBackground(this.animDrawable);
        this.animDrawable.start();
        this.mFooterView.setVisibility(View.VISIBLE);
    }

    private void showNoFooter() {
        AnimationDrawable animationDrawable = this.animDrawable;
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        this.loading.setVisibility(View.GONE);
    }

    @Override
    public void showNoMore() {
        AnimationDrawable animationDrawable = this.animDrawable;
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        this.loading.setVisibility(View.GONE);
        this.mTvLoadMore.setText(R.string.moment_nomore);
    }

    @Override
    public void loadError() {
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showNoConnected(this);
        } else {
            showNoMore();
        }
    }

    @Override
    public void startLoad() {
        LogUtil.d(TAG, "startLoad");
        showLoadMore();
    }

    @Override
    public void loadComplete() {
        LogUtil.d(TAG, "loadComplete");
    }

    @Override
    public void loadLocalData(long offset, long count, String watchId) {
        this.presenter.loadMomentsFromDb(offset, count, watchId, false);
    }
    @Override
    public void likeSuccess(final DbLikeMessage likeMessage) {
        LogUtil.d("moment", "点赞数据成功回调 —— dbLikeMessage = " + likeMessage);
        EventBus.getDefault().post(likeMessage);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = presenter.getMomentById(likeMessage.getMomentId());
                final List<DbLikeMessage> likeMessages = presenter.getLikeMessageByMomentId(likeMessage.getMomentId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moments != null && moments.size() > 0) {
                            mRvAdapter.refreshData(moments.get(0));
                        }
                        if (likeMessages != null && likeMessages.size() > 0) {
                            HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                            if (isCanceled) {
                                List<DbLikeMessage> cachedLikes = mRvAdapter.getLikeMaps(likeMessage.getMomentId());
                                LogUtil.i(TAG, "现有 " + cachedLikes);
                                LogUtil.i(TAG, "拉到 " + likeMessages);
                                if (cachedLikes == null) {
                                    cachedLikes = new ArrayList<>();
                                }
                                cachedLikes.addAll(likeMessages);
                                likeMap.put(likeMessage.getMomentId(), cachedLikes);
                                mRvAdapter.refreshPraiseRecordMap(likeMap);
                            } else {
                                likeMap.put(likeMessage.getMomentId(), likeMessages);
                                mRvAdapter.refreshPraiseRecordMap(likeMap);
                            }
                        }
                        if (moments == null || moments.isEmpty() || likeMessages == null || likeMessages.isEmpty()) {
                            return;
                        }
                        int holderPosition = mRvAdapter.getHolderPosition(moments.get(0));
                        LinearLayoutManager layoutManager = (LinearLayoutManager) mRv.getLayoutManager();
                        int firstVisible = layoutManager.findFirstVisibleItemPosition();
                        int lastVisible = layoutManager.findLastVisibleItemPosition();
                        if (holderPosition == -1 || firstVisible > holderPosition || holderPosition > lastVisible) {
                            return;
                        }
                        mRvAdapter.notifyItemChanged(holderPosition, AbsInteractionAdapter.PART_REFRESH_PRAISE);
                    }
                });
            }
        });
    }

    @Override
    public void cancelLikeSuccess(final DbMoment moment) {
        this.mRvAdapter.setLastCancelTime(System.currentTimeMillis());
        EventBus.getDefault().post(new EventData(11, moment));
        this.isCanceled = true;
        final List<DbMoment> moments = presenter.getMomentById(moment.getMomentId());
        final List<DbLikeMessage> likeMessages = presenter.getLikeMessageByMomentId(moment.getMomentId());
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (moments != null && moments.size() > 0) {
                    mRvAdapter.refreshData(moments.get(0));
                }
                if (likeMessages != null && likeMessages.size() > 0) {
                    HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                    likeMap.put(moment.getMomentId(), likeMessages);
                    LogUtil.i(TAG, "点赞数据   ++ " + likeMap.get(moment.getMomentId()));
                }
                mRvAdapter.refreshPraiseRecordMapCancel(MomentApp.getWatchId());
                if (CollectionUtil.isEmpty(moments)) {
                    return;
                }
                int holderPosition = mRvAdapter.getHolderPosition(moments.get(0));
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRv.getLayoutManager();
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (holderPosition == -1 || firstVisible > holderPosition || holderPosition > lastVisible) {
                    return;
                }
                mRvAdapter.notifyItemChanged(holderPosition, AbsInteractionAdapter.PART_REFRESH_PRAISE);
            }
        });
    }

    @Override
    public void likeError(String message) {
        dealLikeError(message);
    }

    @Override
    public void likeError() {
        if (NetworkUtils.isConnected(this)) {
            return;
        }
        Toast.makeText(this, R.string.net_work_exception, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadPraiseRecordSuccess(Map<String, List<DbLikeMessage>> likeMap) {
        this.mRvAdapter.refreshPraiseRecordMap(likeMap);
        this.mRvAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadPraiseRecordFailed() {
        refreshLocalLikeMessage();
    }

    private void refreshLocalLikeMessage() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                List<DbMoment> data = mRvAdapter.getData();
                if (data == null || data.isEmpty() || TextUtils.isEmpty(mWatchId)) {
                    return;
                }
                ArrayList<String> momentIds = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    DbMoment moment = data.get(i);
                    if (!TextUtils.isEmpty(moment.getMomentId())) {
                        momentIds.add(moment.getMomentId());
                    }
                }
                final Map<String, List<DbLikeMessage>> likeMap = presenter.getPraiseRecordFromDb(momentIds);
                if (likeMap == null || likeMap.isEmpty()) {
                    return;
                }
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mRvAdapter.refreshPraiseRecordMap(likeMap);
                        mRvAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public View getBackgroundView() {
        return this.mRv;
    }

    @Override
    public void removeMoment(DbMoment moment) {
        this.mRvAdapter.removeData(moment);
    }

    @Override
    public void removeFail() {
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
        } else {
            ToastUtil.showShortCover(this, getString(R.string.delete_fail));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "onNewIntent: ");
        dealPermission();
        if (this.ctaPermissionDialog != null) {
            this.ctaPermissionDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy() called");
        this.presenter.getLikeDrawableCache().clear();
    }

    @Override
    public void removeComment(DbMomentComment comment) {
        EventBus.getDefault().post(new EventData(2, comment));
    }

    @Override
    public void publishSuccess(DbMomentComment comment, String content) {
        showLoadingSuccess();
        this.mRvAdapter.addCommentData(comment);
        EventBus.getDefault().post(new CommentEvent(content, comment));
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
    public void publishFail(String message) {
        dismissLoading();
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void momentAlreadyDeleted() {
        ToastUtil.showShortCover(this, getString(R.string.moment_already_deleted));
        dismissLoading();
        if (CollectionUtil.isEmpty(this.mRvAdapter.getData())) {
            finish();
        }
    }

    @Override
    public void removeCommentFail() {
        if (!NetworkUtils.isConnected(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
        } else {
            ToastUtil.showShortCover(this, getString(R.string.delete_fail));
        }
    }

    @Override
    public String getLogTag() {
        return TAG;
    }
}
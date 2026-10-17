package com.xtc.moment.module.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.GlideUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索动态页面：顶部固定搜索框 + 搜索结果列表。
 *
 * <p>结果卡片复用个人动态的 {@link SearchMomentAdapter}，所以点赞、评论、进详情、预览等
 * 交互与其它列表页完全一致；检索本身走本地数据库（见 {@link SearchMomentPresenter}）。
 */
public class SearchMomentActivity extends BaseInteractActivity<ISearchMomentView, SearchMomentPresenter>
        implements ISearchMomentView {

    private static final String TAG = "SearchMomentActivity";

    /** 触发搜索的最小间隔，避免回车键与编辑器动作重复触发同一次搜索。 */
    private static final long SEARCH_TRIGGER_INTERVAL = 500L;

    /** 进页后延时拉起输入法，等首帧布局完成。 */
    private static final long SHOW_IME_DELAY = 300L;

    private EditText etSearchKeyword;
    private TextView tvSearchAction;
    private TextView tvSearchEmpty;
    private TextView tvSearchEmptyHint;
    private RecyclerView rvSearch;

    private SearchMomentAdapter adapter;
    private VerticallyLinearLayoutManager layoutManager;
    private View footerView;
    private ImageView loading;
    private TextView tvLoadMore;
    private AnimationDrawable animDrawable;

    private long lastTriggerTime;
    private boolean isRunning;

    public static void start(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, SearchMomentActivity.class));
    }

    @Override
    public SearchMomentPresenter createPresenter() {
        return new SearchMomentPresenter(getApplicationContext());
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    /**
     * 纯本地检索，不申请运行时权限（与动态详情页一致），这里直接搭建页面。
     */
    @Override
    protected void dealPermission() {
        LogUtil.d(TAG, "搜索动态界面无需申请权限");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_search_moment);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initData() {
        // 纯本地检索，进页无需请求数据。
    }

    @Override
    public void initView() {
        this.etSearchKeyword = (EditText) findViewById(R.id.et_search_keyword);
        this.tvSearchAction = (TextView) findViewById(R.id.tv_search_action);
        this.rvSearch = (RecyclerView) findViewById(R.id.rv_search_moment);
        this.etHint = (EditText) findViewById(R.id.et_hint);
        super.initEditText();
        initSearchBox();
        initRecyclerView();
        focusSearchBox();
    }

    /** 搜索框：软键盘「搜索」键、回车键与「搜索」按钮三条路径共用一个提交入口。 */
    private void initSearchBox() {
        this.tvSearchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSearch();
            }
        });
        this.etSearchKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_SEND) {
                    submitSearch();
                    return true;
                }
                return false;
            }
        });
        this.etSearchKeyword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode != KeyEvent.KEYCODE_ENTER || event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                submitSearch();
                return true;
            }
        });
        // 清空输入时回到「搜索好友的动态」初始态，避免残留上次结果。
        this.etSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() == 0) {
                    resetToIdleState();
                }
            }
        });
    }

    private void initRecyclerView() {
        this.layoutManager = new VerticallyLinearLayoutManager(this);
        this.rvSearch.setLayoutManager(this.layoutManager);
        this.adapter = new SearchMomentAdapter(this, false, null, null, this.layoutManager, null,
                this.presenter.getLikeDrawableCache());
        this.rvSearch.setAdapter(this.adapter);
        this.adapter.initSwitch();
        initCommonRvListener(this.adapter);
        initShareViewRvListener(this.adapter);
        setFooterView();
        setEmptyView();
        this.rvSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                if (manager != null && manager.getChildCount() > 0 && newState == 0
                        && recyclerView.getChildLayoutPosition(
                                recyclerView.getChildAt(manager.getChildCount() - 1)) >= manager.getItemCount() - 1) {
                    loadMoreData();
                }
                GlideUtils.setImageDelayedLoad(SearchMomentActivity.this, newState);
            }
        });
    }

    private void setFooterView() {
        this.footerView = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.footer_recycle_moment, this.rvSearch, false);
        this.adapter.setFooterView(this.footerView);
        this.loading = (ImageView) this.footerView.findViewById(R.id.iv_loading);
        this.tvLoadMore = (TextView) this.footerView.findViewById(R.id.tv_load_more);
        // 进页没有加载动作，footer 默认收起，避免空态下方挂一个「加载中」。
        this.footerView.setVisibility(View.GONE);
    }

    /** 空态：图 + 主文案 + 副文案（未搜索与搜索无结果共用，文案不同）。 */
    private void setEmptyView() {
        View emptyView = LayoutInflater.from(this).inflate(R.layout.view_search_moment_empty, this.rvSearch, false);
        this.adapter.setEmptyView(emptyView);
        this.tvSearchEmpty = (TextView) emptyView.findViewById(R.id.tv_search_empty);
        this.tvSearchEmptyHint = (TextView) emptyView.findViewById(R.id.tv_search_empty_hint);
    }

    /** 进页自动聚焦并拉起输入法，符合「进来就是要搜」的预期。 */
    private void focusSearchBox() {
        this.etSearchKeyword.requestFocus();
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                getInputMethodManager().showSoftInput(SearchMomentActivity.this.etSearchKeyword, 0);
            }
        }, SHOW_IME_DELAY);
    }

    /** 提交搜索：空关键词提示；短时间内的重复触发（回车 + 编辑器动作）只处理一次。 */
    private void submitSearch() {
        String keyword = this.etSearchKeyword.getText() == null ? "" : this.etSearchKeyword.getText().toString();
        if (TextUtils.isEmpty(keyword.trim())) {
            ToastUtil.showShortCover(this, getString(R.string.search_moment_keyword_empty));
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastTriggerTime < SEARCH_TRIGGER_INTERVAL) {
            LogUtil.d(TAG, "submitSearch: ignore duplicated trigger");
            return;
        }
        this.lastTriggerTime = now;
        hideSoftInput();
        this.presenter.search(keyword);
    }

    private void hideSoftInput() {
        getInputMethodManager().hideSoftInputFromWindow(this.etSearchKeyword.getWindowToken(), 0);
    }

    /** 滚到底自动续搜。 */
    private void loadMoreData() {
        if (this.isRunning || this.presenter.isReachedEnd()) {
            LogUtil.i(TAG, "loadMoreData: skip, running = " + this.isRunning);
            return;
        }
        this.isRunning = true;
        showLoadMore();
        this.rvSearch.postDelayed(new Runnable() {
            @Override
            public void run() {
                SearchMomentActivity.this.presenter.loadMore();
            }
        }, 300L);
    }

    private void showLoadMore() {
        if (this.tvLoadMore == null) {
            return;
        }
        this.tvLoadMore.setText(R.string.moment_loading);
        this.loading.setVisibility(View.VISIBLE);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.7f, 100);
        this.loading.setBackground(this.animDrawable);
        this.animDrawable.start();
        this.footerView.setVisibility(View.VISIBLE);
    }

    private void showNoFooter() {
        AnimationDrawable drawable = this.animDrawable;
        if (drawable != null) {
            drawable.stop();
        }
        if (this.loading != null) {
            this.loading.setVisibility(View.GONE);
        }
    }

    private void showNoMore() {
        showNoFooter();
        if (this.tvLoadMore != null) {
            this.tvLoadMore.setText(R.string.moment_nomore);
        }
        if (this.footerView != null) {
            this.footerView.setVisibility(View.VISIBLE);
        }
    }

    /** 结果为空时整条 footer 收起，只留空态文案。 */
    private void hideFooter() {
        showNoFooter();
        if (this.footerView != null) {
            this.footerView.setVisibility(View.GONE);
        }
    }

    /** 回到未搜索的初始态（输入框被清空时）。 */
    private void resetToIdleState() {
        if (this.adapter == null) {
            return;
        }
        boolean hasResult = this.adapter.getData() != null && !this.adapter.getData().isEmpty();
        if (!hasResult && !this.isRunning) {
            return;
        }
        LogUtil.d(TAG, "resetToIdleState");
        if (hasResult) {
            this.adapter.getData().clear();
            this.adapter.notifyDataSetChanged();
        }
        // 让可能还在跑的后台扫描结果作废，避免清空后又被回填。
        this.presenter.cancelSearch();
        this.isRunning = false;
        hideFooter();
        if (this.tvSearchEmpty != null) {
            this.tvSearchEmpty.setText(R.string.search_moment_idle);
            this.tvSearchEmptyHint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchStart(String keyword) {
        LogUtil.i(TAG, "onSearchStart: " + keyword);
        this.isRunning = true;
        if (this.adapter.getData() != null && !this.adapter.getData().isEmpty()) {
            this.adapter.getData().clear();
            this.adapter.notifyDataSetChanged();
        }
        if (this.tvSearchEmpty != null) {
            // 列表还空着，用空态区域当加载提示，避免闪出「没有找到相关动态」。
            this.tvSearchEmpty.setText(R.string.moment_loading);
            this.tvSearchEmptyHint.setVisibility(View.GONE);
        }
        hideFooter();
    }

    @Override
    public void onSearchPage(List<DbMoment> moments, Map<String, List<DbLikeMessage>> praiseRecords,
            boolean firstPage, boolean reachedEnd) {
        this.isRunning = false;
        int size = moments == null ? 0 : moments.size();
        LogUtil.i(TAG, "onSearchPage: size = " + size + ", reachedEnd = " + reachedEnd);
        if (size > 0) {
            this.adapter.addData(moments);
            this.adapter.refreshPraiseRecordMap(praiseRecords);
            if (reachedEnd) {
                showNoMore();
            } else {
                hideFooter();
            }
            return;
        }
        // 没攒到结果只可能是已经扫到底（见 Presenter 的扫描循环）。
        if (this.tvSearchEmpty != null) {
            this.tvSearchEmpty.setText(R.string.search_moment_empty);
            this.tvSearchEmptyHint.setVisibility(View.VISIBLE);
        }
        hideFooter();
    }

    @Override
    public void onSearchKeywordEmpty() {
        this.isRunning = false;
        ToastUtil.showShortCover(this, getString(R.string.search_moment_keyword_empty));
    }

    @Override
    public View getBackgroundView() {
        return this.rvSearch;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        if (eventData.getType() == EventData.DELETE_MOMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMoment)) {
                return;
            }
            removeMoment((DbMoment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.DELETE_COMMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            this.adapter.removeComment((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.PUBLISH_COMMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            this.adapter.addComment((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.CHANGE_FAVORITE) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMoment)) {
                return;
            }
            this.adapter.refreshFavoriteData((DbMoment) eventData.getData());
        }
    }

    @Override
    public void removeMoment(DbMoment moment) {
        LogUtil.i(TAG, "removeMoment: " + moment);
        if (this.adapter.getData() == null || this.adapter.getData().isEmpty()) {
            return;
        }
        this.adapter.removeData(moment);
        if (this.adapter.getData().isEmpty()) {
            // 最后一条被删掉：只留空态文案，不留「没有更多了」。
            hideFooter();
        }
    }

    @Override
    public void removeFail() {
        ToastUtil.showShortCover(this, getString(R.string.delete_fail));
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
    public void momentAlreadyDeleted() {
        ToastUtil.showShortCover(this, getString(R.string.moment_already_deleted));
    }

    @Override
    public void loadCommentSuccess(DbMoment moment) {
        if (moment != null && moment.getMomentId() != null) {
            this.adapter.notifyItemByMomentId(moment.getMomentId());
        }
    }

    @Override
    public void publishSuccess(DbMomentComment comment, String extra) {
        // 和其它页面一致：先切成功动画，动画结束后自动关掉「发布中」弹窗，
        // 再补上这条评论，最后广播出去让主列表/详情页同步。
        showLoadingSuccess();
        this.adapter.addCommentData(comment);
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
    public void likeSuccess(final DbLikeMessage likeMessage) {
        LogUtil.d(TAG, "likeSuccess: " + likeMessage);
        if (likeMessage == null) {
            return;
        }
        EventBus.getDefault().post(likeMessage);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = SearchMomentActivity.this.presenter
                        .getMomentById(likeMessage.getMomentId());
                final List<DbLikeMessage> likeMessages = SearchMomentActivity.this.presenter
                        .getLikeMessageByMomentId(likeMessage.getMomentId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moments != null && !moments.isEmpty()) {
                            SearchMomentActivity.this.adapter.refreshData(moments.get(0));
                        }
                        if (likeMessages != null && !likeMessages.isEmpty()) {
                            HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                            likeMap.put(likeMessage.getMomentId(), likeMessages);
                            SearchMomentActivity.this.adapter.refreshPraiseRecordMap(likeMap);
                        }
                        SearchMomentActivity.this.refreshMomentItem(moments);
                    }
                });
            }
        });
    }

    @Override
    public void cancelLikeSuccess(final DbMoment moment) {
        LogUtil.d(TAG, "cancelLikeSuccess: " + moment);
        if (moment == null) {
            return;
        }
        this.adapter.setLastCancelTime(System.currentTimeMillis());
        EventBus.getDefault().post(new EventData(EventData.CANCEL_LIKE_CHANGE_MOMENTADAPTER, moment));
        final List<DbMoment> moments = this.presenter.getMomentById(moment.getMomentId());
        final List<DbLikeMessage> likeMessages = this.presenter.getLikeMessageByMomentId(moment.getMomentId());
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (moments != null && !moments.isEmpty()) {
                    SearchMomentActivity.this.adapter.refreshData(moments.get(0));
                }
                if (likeMessages != null && !likeMessages.isEmpty()) {
                    HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
                    likeMap.put(moment.getMomentId(), likeMessages);
                    SearchMomentActivity.this.adapter.refreshPraiseRecordMap(likeMap);
                }
                SearchMomentActivity.this.adapter.refreshPraiseRecordMapCancel(MomentApp.getWatchId());
                SearchMomentActivity.this.refreshMomentItem(moments);
            }
        });
    }

    /** 只刷新可见范围内的那一条，避免整列表重绘。 */
    private void refreshMomentItem(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty() || this.adapter == null) {
            return;
        }
        int holderPosition = this.adapter.getHolderPosition(moments.get(0));
        RecyclerView.LayoutManager manager = this.rvSearch.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager) || holderPosition == -1) {
            return;
        }
        LinearLayoutManager linearManager = (LinearLayoutManager) manager;
        int firstVisible = linearManager.findFirstVisibleItemPosition();
        int lastVisible = linearManager.findLastVisibleItemPosition();
        if (firstVisible > holderPosition || holderPosition > lastVisible) {
            return;
        }
        this.adapter.notifyItemChanged(holderPosition, AbsInteractionAdapter.PART_REFRESH_PRAISE);
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
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}

package com.xtc.moment.module.details;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseInteractActivity;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.bean.CommentEvent;
import com.xtc.moment.module.details.adapter.DetailsAdapter;
import com.xtc.moment.module.playvideo.PlayVideoActivity;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.StartWebUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

/**
 * 动态详情页面：展示单条动态与其评论，支持点赞、评论、删除与可见范围查看。
 */
public class MomentDetailsActivity extends BaseInteractActivity<IMomentDetailsView, MomentDetailsPresenter>
        implements IMomentDetailsView {

    private static final String TAG = "MomentDetailsActivity";

    public static final String INTENT_MOMENT_ID_KEY = "momentId";
    public static final String INTENT_WATCH_ID_KEY = "watchId";
    public static final String INTENT_MOMENT_KEY = "momentData";
    public static final String INTENT_NEED_SCROLL = "need_scroll";

    private static final int COUNT = 10;
    private static final int MIN_SCROLL_HEIGHT = 100;
    private static final int LOCATION_SCROLL_HEIGHT = 200;
    private static final int PHOTO_SCROLL_HEIGHT = 300;
    private static final int SCROLL_OFFSET = 17;

    private RecyclerView rvDetailsRecyclerView;
    private DetailsAdapter detailsAdapter;
    private LinearLayout llPlayLoading;
    private View viewLoading;
    private AnimationDrawable loadingAnim;
    private LoadingPupWindowHolder loadingPupWindowHolder;

    private View mFooterView;
    private ImageView ivLoading;
    private TextView tvLoadMore;
    private AnimationDrawable animDrawable;

    private String detailsMomentId;
    private String detailsWatchId;
    private boolean isNeedScroll;
    private DbMoment mDbMoment;
    private int pageNum = 1;
    private InputMethodManager inputManager;

    @Override
    public String getLogTag() {
        return TAG;
    }

    @Override
    public void likeError() {
    }

    @Override
    public void requestPermission() {
    }

    @Override
    public MomentDetailsPresenter createPresenter() {
        return new MomentDetailsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void dealPermission() {
        LogUtil.d(TAG, "动态详情界面无需申请权限");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_moment_deails);
        EventBus.getDefault().register(this);
        this.inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        initView();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (this.inputManager == null) {
            this.inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        }
        initView();
        initData();
    }

    @Override
    public void initView() {
        this.rvDetailsRecyclerView = (RecyclerView) findViewById(R.id.rv_details);
        this.rvDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.llPlayLoading = (LinearLayout) findViewById(R.id.ll_cover);
        this.viewLoading = findViewById(R.id.loading_ll);
        this.llPlayLoading.setVisibility(View.VISIBLE);
        this.loadingAnim = new LoadingAnim(this).createAnim();
        this.viewLoading.setBackground(this.loadingAnim);
        this.detailsAdapter = new DetailsAdapter(this);
        this.rvDetailsRecyclerView.setItemAnimator(null);
        this.rvDetailsRecyclerView.setAdapter(this.detailsAdapter);
        initRvFooterView();
        initEditText();
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                MomentDetailsActivity.this.loadingPupWindowHolder.dismissLoading();
            }
        });
        this.detailsAdapter.setOnPreviewMomentListener(new AbsInteractionAdapter.OnPreviewMomentListener() {
            @Override
            public void onPreviewMoment(DbMoment moment) {
                if (moment.isPreviewed()) {
                    return;
                }
                MomentDetailsActivity.this.presenter.checkPreviewBehavior(MomentDetailsActivity.this, moment);
            }

            @Override
            public void preViewH5(String url, String title) {
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                StartWebUtils.startH5Activity(MomentDetailsActivity.this, url);
            }

            @Override
            public void preViewVideo(String videoPath, boolean hasToken) {
                if (TextUtils.isEmpty(videoPath)) {
                    return;
                }
                LogUtil.i(TAG, "preViewVideo :" + videoPath);
                Intent intent = new Intent(MomentDetailsActivity.this, PlayVideoActivity.class);
                intent.putExtra(PlayVideoActivity.VIDEO_MSG_DATA, videoPath);
                intent.putExtra(PlayVideoActivity.HAS_VIDEO_TOKEN_OR_KEY, hasToken);
                MomentDetailsActivity.this.startActivity(intent);
            }
        });
        initCommonRvListener(this.detailsAdapter);
    }

    @Override
    public void initEditText() {
        this.etHint = (EditText) findViewById(R.id.et_hint);
        super.initEditText();
    }

    private void initRvFooterView() {
        this.mFooterView = AsyncLayoutLoader.getInstance().inflateView(R.layout.footer_recycle_moment,
                LayoutInflater.from(this), this.rvDetailsRecyclerView);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.7f, 100);
        this.ivLoading = (ImageView) this.mFooterView.findViewById(R.id.iv_loading);
        this.tvLoadMore = (TextView) this.mFooterView.findViewById(R.id.tv_load_more);
        this.detailsAdapter.setFooterView(this.mFooterView);
    }

    private void showLoadMore() {
        this.tvLoadMore.setText(R.string.moment_loading);
        this.ivLoading.setVisibility(View.VISIBLE);
        this.ivLoading.setBackground(this.animDrawable);
        this.animDrawable.start();
        this.mFooterView.setVisibility(View.VISIBLE);
    }

    private void showNoFooter() {
        AnimationDrawable animation = this.animDrawable;
        if (animation != null) {
            animation.stop();
        }
        this.ivLoading.setVisibility(View.INVISIBLE);
        this.mFooterView.setVisibility(View.INVISIBLE);
    }

    public void showNoMore() {
        AnimationDrawable animation = this.animDrawable;
        if (animation != null) {
            animation.stop();
        }
        this.ivLoading.setVisibility(View.GONE);
        this.tvLoadMore.setText(R.string.moment_nomore);
    }

    @Override
    public void initData() {
        final String momentJson = getIntent().getStringExtra(INTENT_MOMENT_KEY);
        this.detailsMomentId = getIntent().getStringExtra(INTENT_MOMENT_ID_KEY);
        this.detailsWatchId = getIntent().getStringExtra(INTENT_WATCH_ID_KEY);
        this.isNeedScroll = getIntent().getBooleanExtra(INTENT_NEED_SCROLL, true);
        this.loadingAnim.start();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                MomentDetailsActivity.this.presenter.getCommentByMomentId(MomentDetailsActivity.this.pageNum,
                        COUNT, MomentDetailsActivity.this.detailsMomentId,
                        MomentDetailsActivity.this.detailsWatchId, momentJson);
            }
        });
    }
    @Override
    public void loadCommentSuccess(DbMoment moment) {
        this.mDbMoment = moment;
        showNoMore();
        moment.setCommentsTotalCount(moment.getComments() != null ? moment.getComments().size() : 0);
        this.detailsAdapter.setMoment(moment);
        this.detailsAdapter.setDbMomentComments(moment.getComments());
        this.detailsAdapter.notifyDataSetChanged();
        scrollByView(moment.getType().intValue(), this.isNeedScroll);
    }

    /** 根据动态类型决定详情页首屏滚动位置。 */
    private void scrollByView(final int momentType, boolean needScroll) {
        if (!needScroll) {
            AnimationDrawable animation = this.loadingAnim;
            if (animation == null || !animation.isRunning()) {
                return;
            }
            this.loadingAnim.stop();
            this.llPlayLoading.setVisibility(View.GONE);
            return;
        }
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                int contentHeight = MomentDetailsActivity.this.detailsAdapter != null
                        ? MomentDetailsActivity.this.detailsAdapter.getMomentContentHeight() : 0;
                LogUtil.d(TAG, "scrollByView: " + contentHeight);
                if (contentHeight == 0) {
                    if (momentType == 22 || momentType == 23 || momentType == 25) {
                        contentHeight = PHOTO_SCROLL_HEIGHT;
                    } else if (momentType == 2) {
                        contentHeight = LOCATION_SCROLL_HEIGHT;
                    } else if (momentType == 4 || momentType == 5 || momentType == 6 || momentType == 8
                            || momentType == 9 || momentType == 11 || momentType == 12 || momentType == 13
                            || momentType == 14) {
                        contentHeight = PHOTO_SCROLL_HEIGHT;
                    } else {
                        contentHeight = MIN_SCROLL_HEIGHT;
                    }
                }
                MomentDetailsActivity.this.rvDetailsRecyclerView.scrollBy(0,
                        DimenUtil.dp2px(MomentDetailsActivity.this, contentHeight - SCROLL_OFFSET));
                if (MomentDetailsActivity.this.loadingAnim == null
                        || !MomentDetailsActivity.this.loadingAnim.isRunning()) {
                    return;
                }
                MomentDetailsActivity.this.loadingAnim.stop();
                MomentDetailsActivity.this.llPlayLoading.setVisibility(View.GONE);
            }
        }, 100L);
    }

    @Override
    public void loadCommentError(DbMoment moment) {
        showNoMore();
        moment.setCommentsTotalCount(moment.getComments() != null ? moment.getComments().size() : 0);
        this.detailsAdapter.setMoment(moment);
        this.detailsAdapter.setDbMomentComments(moment.getComments());
        this.detailsAdapter.notifyDataSetChanged();
        scrollByView(moment.getType().intValue(), this.isNeedScroll);
    }

    @Override
    public void contactRemove(ContactBean contactBean) {
        if (TextUtils.isEmpty(contactBean.getFriendWatchId()) || !TextUtils.isEmpty(this.detailsWatchId)) {
            return;
        }
        LogUtil.d(TAG, "contactRemove: 删除好友 退出页面");
        finish();
    }

    @Override
    public void contactUpdate(ContactBean contactBean) {
        if (TextUtils.isEmpty(contactBean.getFriendWatchId()) || !TextUtils.isEmpty(this.detailsWatchId)) {
            return;
        }
        LogUtil.d(TAG, "contactUpdate: 好友数据更改 刷新" + contactBean);
        DbMoment moment = this.mDbMoment;
        if (moment == null) {
            return;
        }
        moment.setName(contactBean.getName());
        this.mDbMoment.setIconPath(contactBean.getFriendIcon());
        this.detailsAdapter.setMoment(this.mDbMoment);
        this.detailsAdapter.notifyDataSetChanged();
    }

    @Override
    public void publishSuccess(DbMomentComment comment, String extra) {
        showLoadingSuccess();
        this.detailsAdapter.addCommentData(comment);
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
    public void momentAlreadyDeleted() {
        finish();
    }

    @Override
    public void publishLimited() {
        dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_comment_limit));
    }

    @Override
    public void publishInvalidate() {
        dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_invalidate));
    }

    @Override
    public void likeSuccess(DbLikeMessage likeMessage) {
        LogUtil.d("moment", "点赞数据成功回调 —— dbLikeMessage = " + likeMessage);
        this.detailsAdapter.refreshLikeView(true);
        EventBus.getDefault().post(likeMessage);
    }

    @Override
    public void cancelLikeSuccess(DbMoment moment) {
        this.detailsAdapter.setCancelLikeTime(System.currentTimeMillis());
        this.detailsAdapter.refreshLikeView(false);
        EventBus.getDefault().post(new EventData(EventData.CANCEL_LIKE_CHANGE_MOMENTADAPTER, moment));
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
    public void removeMoment(DbMoment moment) {
        LogUtil.d(TAG, "removeMoment = " + moment);
        EventBus.getDefault().post(new EventData(EventData.DELETE_MOMENT, moment));
        finish();
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
    public void removeComment(DbMomentComment comment) {
        EventBus.getDefault().post(new EventData(EventData.DELETE_COMMENT, comment));
    }

    @Override
    public void likeError(String message) {
        dealLikeError(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        if (eventData.getType() == EventData.DELETE_MOMENT
                || eventData.getType() == EventData.DELETE_INVALIDATE_MOMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMoment)) {
                return;
            }
            LogUtil.i(TAG, "receive delete moment event:" + eventData.toString());
            if (((DbMoment) eventData.getData()).getMomentId().equals(this.detailsMomentId)) {
                finish();
            }
            return;
        }
        if (eventData.getType() == EventData.DELETE_COMMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            LogUtil.i(TAG, "receive delete comment event:" + eventData.toString());
            this.detailsAdapter.removeCommentData((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.PUBLISH_COMMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMomentComment)) {
                return;
            }
            LogUtil.i(TAG, "receive public comment event:" + eventData.toString());
            this.detailsAdapter.addCommentData((DbMomentComment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.LIKE_MOMENT) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbLikeMessage)) {
                return;
            }
            LogUtil.i(TAG, "receive like moment event:" + eventData.toString());
            this.detailsAdapter.refreshPushLikeData();
            return;
        }
        if (eventData.getType() == EventData.CONTACT_DEL) {
            if (eventData.getData() == null || !(eventData.getData() instanceof String)) {
                return;
            }
            LogUtil.i(TAG, "receive friend del:" + eventData.toString());
            String friendWatchId = (String) eventData.getData();
            DetailsAdapter adapter = this.detailsAdapter;
            if (adapter != null) {
                adapter.deleteFriendInfo(friendWatchId);
            }
            DbMoment moment = this.mDbMoment;
            if (moment != null && Objects.equals(moment.getWatchId(), friendWatchId)) {
                finish();
            }
            return;
        }
        if (eventData.getType() == EventData.UPDATE_PIC_LOCAL_PATH) {
            if (eventData.getData() == null || !(eventData.getData() instanceof DbMoment)) {
                return;
            }
            LogUtil.i(TAG, "receive update pic local path event:" + eventData.toString());
            this.detailsAdapter.refreshLocalPathData((DbMoment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.CANCEL_LIKE_CHANGE_MOMENT_ADAPTER_MOMENT) {
            DbMoment moment = (DbMoment) eventData.getData();
            moment.setDataUrl(null);
            this.detailsAdapter.refreshLikeData(moment);
        } else if (eventData.getType() == EventData.CHANGE_VISIBLE_MOMENT
                && (eventData.getData() instanceof DbMoment)) {
            DbMoment moment = (DbMoment) eventData.getData();
            LogUtil.i(TAG, " " + moment);
            this.detailsAdapter.refreshVisiblePicData(moment);
        }
    }
}
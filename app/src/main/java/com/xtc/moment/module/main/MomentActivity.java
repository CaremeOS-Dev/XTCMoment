package com.xtc.moment.module.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.opensource.svgaplayer.SVGAImageView;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.assistantapi.DeviceModuleManager;
import com.xtc.assistantapi.core.IntentAction;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseInteractActivity;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.dress.HeadDressManager;
import com.xtc.moment.event.IMMomentMsgData;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.helper.UnreadHelper;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.MomentAdapter;
import com.xtc.moment.module.StringConstant;
import com.xtc.moment.module.assistant.ApiConstants;
import com.xtc.moment.module.assistant.MomentDeviceModule;
import com.xtc.moment.module.assistant.message.PostStatusPayload;
import com.xtc.moment.module.bean.CommentEvent;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.LbsStarEvent;
import com.xtc.moment.module.bean.SendVideoParam;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.widget.HintIllegalContentDialog;
import com.xtc.moment.module.illegal.widget.HintSensitiveContentDialog;
import com.xtc.moment.module.like.NewLikeActivity;
import com.xtc.moment.module.playvideo.PlayVideoActivity;
import com.xtc.moment.module.publish.PubVideoOrPhotoServer;
import com.xtc.moment.module.publish.PublishActivity;
import com.xtc.moment.module.publish.multi.PushPictureActivity;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.text.PublishTextBean;
import com.xtc.moment.module.publish.text.PushTextActivity;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.search.SearchMomentActivity;
import com.xtc.moment.module.share.ShareActivity;
import com.xtc.moment.module.viewholder.AbsViewHolder;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.NumTipSeekBar;
import com.xtc.moment.module.widget.OnItemVisibleListener;
import com.xtc.moment.module.widget.VerticallyLinearLayoutManager;
import com.xtc.moment.module.widget.livephotoView.LivePhotoDelayTimeUtil;
import com.xtc.moment.net.bean.ImMyNameBean;
import com.xtc.moment.net.bean.ImMyNameInfoBean;
import com.xtc.moment.prerogative.InitPrerogativeCallback;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.receiver.IConChangeReceiver;
import com.xtc.moment.receiver.StartWebReceiver;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.DressProxy;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.bean.WatchAccountInfo;
import com.xtc.moment.serve.impl.DressServeImpl;
import com.xtc.moment.serve.interfaces.IDressServe;
import com.xtc.moment.service.PublishService;
import com.xtc.moment.service.PublishVideoOrPhotoCallback;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.AppProcessUtil;
import com.xtc.moment.util.BroadcastReceiverUtil;
import com.xtc.moment.util.DressUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.GlideUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ModuleSwitch;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.StartWebUtils;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.TextSizeUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.moment.widget.LbsLayout;
import com.xtc.moment.widget.SwipeRefreshLayout;
import com.xtc.personalitydress.aidl.IDressServiceBinder;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.ui.widget.dialog.LongSolidBtnDialog;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnBean;
import com.xtc.ui.widget.scalablecontainer.AppLinearLayout;
import com.xtc.ui.widget.textview.UnreadView;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;
import com.xtc.utils.ui.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 好友圈主页 Activity。
 *
 * <p>承载动态列表的展示与刷新、下拉刷新、上拉加载更多、发布入口、点赞/评论提示、
 * 装扮服务绑定以及语音助手/微信入口等逻辑。
 */
public class MomentActivity extends BaseInteractActivity<IMomentActivityView, MomentPresenter>
        implements RequestListener<GifDrawable>, MomentAdapter.OnViewAttachedToWindowListener, IMomentActivityView {

    private static final String TAG = "XTC_MOMENT_MomentActivity";

    private static final long COUNT = 10;
    private static final String DEFALUT_ID = "0";
    private static final int MAX_UNREAD_NAME = 100;

    private IAccountInfoServe accountInfoServe;
    private AnimationDrawable animDrawable;
    private volatile boolean bindDressResult;
    private volatile boolean bindService;
    private LongSolidBtnDialog downVersionDialog;
    private long firstScrollTime;
    private boolean flag;
    private HeadDressManager headDressManager;
    private IConChangeReceiver iConChangeReceiver;
    private IllegalMessageHandler illegalMessageHandler;
    private boolean init;
    private InputMethodManager inputManager;
    private TextView ivBanner;
    private ImageView ivChampion;
    private ImageView ivPublishAdd;
    private SVGAImageView ivSvgaAvatarDress;
    private long latestOnRefreshTime;
    private long leaveTime;
    private ViewGroup llRoot;
    private ImageView loading;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private ServiceConnection mConnection;
    private IBinder.DeathRecipient mDeathRecipient;
    private ServiceConnection mDressConnection;
    private IDressServe mDressServe;
    private ViewGroup mFlPublishProgress;
    private View mFooterView;
    private List<Friend> mFriends;
    private ImageView mIvArrow;
    private PublishService.PublishBinder mPublishBinder;
    private PublishVideoOrPhotoCallback mPublishVideoOrPhotoCallback;
    private AnimationDrawable mRefreshDrawable;
    private SwipeRefreshLayout mRefreshFrameLayout;
    private ImageView mRefreshLoadingView;
    private NumTipSeekBar mSbProgress;
    private TextView mTvLoadMore;
    private TextView mTvPublishTip;
    private UnreadView mUnreadView;
    private ViewStub mVsPublishProgress;
    private ViewStub mVsViewNewLike;
    private MomentDeviceModule momentDeviceModule;
    private TextView momentUserNameTv;
    private AppLinearLayout newLikeRL;
    private LinkedList<String> pendingLbsAnmis;
    private AppLinearLayout publishLl;
    private View rootView;
    private RecyclerView rv;
    private MomentAdapter rvAdapter;
    private boolean scrollToLocation;
    private StartWebReceiver startWebReceiver;
    private TextView tvPublishAdd;

    private long offset = 0;
    private Constants.RCType mRcType = Constants.RCType.INIT;
    private boolean scrollState = false;
    private boolean isNeedHidePoint = false;
    private int lastPosition = 0;
    private boolean isRunning = false;
    private boolean isInitData = false;
    private volatile int screenHeight = 360;
    private volatile boolean pullDownRefresh = false;
    boolean isInStartStartPublishMomentMethod = false;

    /** 好友头像装扮数据加载完成回调。 */
    private DressServeImpl.ICallback<Boolean> remoteHeadCallBack = new DressServeImpl.ICallback<Boolean>() {
        @Override
        public void callback(Boolean value) {
            LogUtil.i(TAG, "getHeadById callback");
            loadHeadFromFriendList(MomentActivity.this.mFriends);
        }
    };

    /** 好友昵称装扮数据加载完成回调。 */
    private DressServeImpl.ICallback<Boolean> remoteNickNameCallBack = new DressServeImpl.ICallback<Boolean>() {
        @Override
        public void callback(Boolean value) {
            LogUtil.i(TAG, "getNickNameById callback");
            loadNicknameFromFriendList(MomentActivity.this.mFriends);
        }
    };

    /** 按 watchId 拉取到头像 id 后的回调。 */
    private DressServeImpl.ICallback<HashMap> loadHeadCallBack = new DressServeImpl.ICallback<HashMap>() {
        @Override
        public void callback(HashMap map) {
            LogUtil.i(TAG, "loadHeadFromFriendList: " + map);
            if (map == null || map.size() <= 0) {
                return;
            }
            getRemoteHeads(map);
        }
    };

    /** 按 watchId 拉取到昵称 id 后的回调。 */
    private DressServeImpl.ICallback<HashMap> loadNickNameCallBack = new DressServeImpl.ICallback<HashMap>() {
        @Override
        public void callback(HashMap map) {
            LogUtil.i(TAG, "loadNicknameFromFriendList: " + map);
            if (map == null || map.size() <= 0) {
                return;
            }
            getRemoteNicknames(map);
        }
    };

    /** 根据星级返回对应的表情资源。 */
    private int getStarEmoji(int starLevel) {
        if (starLevel == 1) {
            return R.drawable.big_face_emoji_014;
        }
        if (starLevel == 2) {
            return R.drawable.big_face_emoji_019;
        }
        if (starLevel == 3) {
            return R.drawable.big_face_emoji_013;
        }
        return (starLevel == 4 || starLevel != 5) ? R.drawable.big_face_emoji_002 : R.drawable.big_face_emoji_008;
    }

    private void jumpToPrompt() {
    }

    private static void onSensitiveDialogConfirmed() {
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    @Override
    public void likeError() {
    }

    @Override
    public void refreshMomentLikes(Map<String, List<DbLikeMessage>> likeMap) {
    }

    @Override
    public View getBackgroundView() {
        return this.rv;
    }

    @Override
    public MomentPresenter createPresenter() {
        return new MomentPresenter(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("moment", "resultCode = " + resultCode + "\nrequestCode = " + requestCode);
        if (resultCode != RESULT_OK) {
            if (resultCode == RESULT_CANCELED) {
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        if (SharedTool.getFlagFromOS(getApplicationContext()) == 1) {
                            SharedTool.saveFlagFromOS(getApplicationContext(), 0);
                        }
                    }
                });
            }
            return;
        }
        if (requestCode == Constants.PUBLISH_REQUEST_CODE && SharedTool.getFlagFromOS(this) == 1) {
            SharedTool.saveFlagFromOS(this, 0);
            LogUtil.d(TAG, "smoothScrollToPosition");
            this.rv.smoothScrollToPosition(1);
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(1, 0);
        }
    }

    @Override
    public void requestPermission() {
        requestRunTimePermission(PermissionStringUtils.SEND_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                MomentActivity.this.grantPermission();
            }

            @Override
            public void onPartPermissionDenied(List<String> deniedPermissions, List<String> deniedForeverPermissions) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                if (!deniedForeverPermissions.contains("android.permission.READ_CONTACTS")) {
                    MomentActivity.this.grantPermission();
                } else {
                    MomentActivity.this.finish();
                }
            }
        });
    }

    private void grantPermission() {
        if (this.isInitData) {
            LogUtil.d(TAG, "onGranted: isInitData = true");
            return;
        }
        long startTime = SystemClock.elapsedRealtime();
        PermissionStringUtils.checkPermissionForReTryBaseUrl(this);
        this.presenter.initContactManager();
        this.isInitData = true;
        initView();
        initData();
        LogUtil.i(TAG, "onCreate:  " + (SystemClock.elapsedRealtime() - startTime));
        initPrerogativeResource();
        LogUtil.d(TAG, "onGranted: requestPermission");
    }

    /** 初始化特权装扮资源，成功或失败都刷新点赞动画。 */
    private void initPrerogativeResource() {
        MomentPrerogativeServeImpl.getInstance(this).initPrerogativeResource(new InitPrerogativeCallback() {
            @Override
            public void initSuccess() {
                if (MomentActivity.this.rvAdapter != null) {
                    MomentActivity.this.rvAdapter.refreshPrerogativeLike();
                }
            }

            @Override
            public void initFail() {
                if (MomentActivity.this.rvAdapter != null) {
                    MomentActivity.this.rvAdapter.refreshPrerogativeLike();
                }
            }
        });
    }

    private void initDressServe() {
        if (this.mDressServe != null) {
            return;
        }
        this.mDressServe = new DressServeImpl(getApplicationContext()) {
            @Override
            public void bindService() {
                MomentActivity.this.dealBindDress();
            }
        };
    }

    private void dealBindDress() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                releaseDressConnect();
                bindDressService();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "onNewIntent");
        dealPermission();
        dealAssistant(intent);
    }

    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "onDestroy.");
        AnimationDrawable animationDrawable = this.animDrawable;
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        releaseBgSendCallBackAndUnBind();
        PubVideoOrPhotoServer.getInstance().removePublishVideoOrPhotoCallback(this.mPublishVideoOrPhotoCallback);
        IConChangeReceiver receiver = this.iConChangeReceiver;
        if (receiver != null) {
            receiver.unRegister(this);
        }
        StartWebReceiver webReceiver = this.startWebReceiver;
        if (webReceiver != null && this.flag) {
            this.flag = false;
            BroadcastReceiverUtil.unregisterReceiver(this, webReceiver);
        }
        SharedTool.saveIsRunning(this, false);
        LongSolidBtnDialog dialog = this.downVersionDialog;
        if (dialog != null) {
            DialogUtil.dismissDialog(dialog);
            this.downVersionDialog = null;
        }
        releaseDressConnect();
        HeadDressManager dressManager = this.headDressManager;
        if (dressManager != null) {
            dressManager.clearAllVisibleImageViews();
        }
        MomentDeviceModule deviceModule = this.momentDeviceModule;
        if (deviceModule != null) {
            deviceModule.release();
        }
        LivePhotoDelayTimeUtil.resetFirstPrePlayFlag();
        clean();
        IllegalMessageHandler handler = this.illegalMessageHandler;
        if (handler != null) {
            handler.removeActiveChangeIllegalStateListener();
        }
        dealPendingLbsAnims();
        this.mDeathRecipient = null;
        endRefresh();
        super.onDestroy();
    }

    /** 退出时把本次展示过的位置动画动态持久化，下次不再重复播放。 */
    private void dealPendingLbsAnims() {
        if (CollectionUtil.isEmpty(this.pendingLbsAnmis)) {
            return;
        }
        List<String> lbsAnimList = SharedTool.getLbsAnimList(this);
        LinkedList<String> merged = new LinkedList<>(this.pendingLbsAnmis);
        merged.addAll(lbsAnimList);
        SharedTool.saveLbsAnimList(this, merged.subList(0, Math.min(merged.size(), MAX_UNREAD_NAME)));
    }

    @Override
    public void initEditText() {
        this.etHint = (EditText) findViewById(R.id.et_hint);
        super.initEditText();
    }

    /** 注册语音助手“发布状态”指令监听。 */
    private void initAssistant() {
        this.momentDeviceModule = new MomentDeviceModule();
        this.momentDeviceModule.addMomentListener(new MomentDeviceModule.IMomentListener() {
            @Override
            public void onReceivePostStatus(PostStatusPayload postStatusPayload) {
                handlePostStatus(postStatusPayload);
            }
        });
        DeviceModuleManager.getInstance().registerModule(this.momentDeviceModule);
    }

    /** 处理来自系统/微信/助手的跳转参数。 */
    private void dealAssistant(Intent intent) {
        if (intent == null) {
            return;
        }
        if (!this.isInitData) {
            LogUtil.d(TAG, "dealAssistant: startPublishMoment ret");
            return;
        }
        if (!this.hintPermission) {
            LogUtil.d(TAG, "dealAssistant: not requestPermission");
            return;
        }
        int startType = intent.getIntExtra(Constants.MomentStartExtraName.START_FROM_OS_WEI_CHAT,
                Constants.MomentStartType.DEFAULT_TYPE);
        LogUtil.d(TAG, "fromOSWeiChat starType = " + startType);
        if (startType == Constants.MomentStartType.START_PUBLISH_ACTIVITY) {
            // 从微信拉起时先把页面缩成 1dp，避免闪现主页。
            SharedTool.saveFlagFromOS(this, Constants.FLAG_FROM_OS);
            ViewGroup root = this.llRoot;
            if (root != null) {
                ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f,
                        getResources().getDisplayMetrics());
                layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.0f,
                        getResources().getDisplayMetrics());
                this.llRoot.setLayoutParams(layoutParams);
            }
            startPublishMoment(null, StringConstant.StartType.START_TYPE_FOR_WEICHAT);
        } else if (startType == Constants.MomentStartType.START_NEW_MESSAGE_ACTIVITY) {
            startActivity(new Intent(this, NewLikeActivity.class));
        } else if (startType == Constants.MomentStartType.START_FRIEND_HOME_PAGE_ACTIVITY) {
            String friendId = intent.getStringExtra(Constants.MomentStartExtraName.FRIEND_ID);
            ContactBean contactBean = ContactManager.getInstance(this)
                    .getContactWithoutShortNumberByWatchIdSync(friendId);
            LogUtil.d(TAG, "dealAssistant: friendId = " + friendId + "; contactBean = " + contactBean);
            if (contactBean != null) {
                Intent shareIntent = new Intent(this, ShareActivity.class);
                shareIntent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, friendId);
                shareIntent.putExtra(Constants.INTENT_EXTRA_IS_SELF, false);
                shareIntent.putExtra(Constants.INTENT_EXTRA_ICON_PATH, contactBean.getPhotoPath());
                shareIntent.putExtra(Constants.INTENT_EXTRA_NAME, contactBean.getName());
                startActivity(shareIntent);
            }
        }
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (IntentAction.ACTION_DIRECTIVE.equals(action)) {
            DeviceModuleManager.getInstance().getMessageSender().sendMessage(this, "", "000001");
            String directive = intent.getStringExtra(IntentAction.META_DATA_DIRECTIVE);
            LogUtil.d(TAG, "dealAssistant: code = " + DeviceModuleManager.getInstance().handleDirective(directive)
                    + "; directive = " + directive);
        }
        LogUtil.d(TAG, "dealAssistant: action = " + action);
    }

    private void handlePostStatus(PostStatusPayload postStatusPayload) {
        if (postStatusPayload != null) {
            startPublishMoment(postStatusPayload, StringConstant.StartType.START_TYPE_DUER);
        }
    }
    private void initRv() {
        VerticallyLinearLayoutManager layoutManager = new VerticallyLinearLayoutManager(this);
        this.rvAdapter = new MomentAdapter(this, layoutManager);
        this.rv.setAdapter(this.rvAdapter);
        ((SimpleItemAnimator) this.rv.getItemAnimator()).setSupportsChangeAnimations(false);
        this.rv.setLayoutManager(layoutManager);
        initRvHeaderView();
        initRvFooterView();
        initCommonRvListener(this.rvAdapter);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.6f, 100);
        this.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
                int childCount = layout.getChildCount();
                if (childCount <= 0 || newState != RecyclerView.SCROLL_STATE_IDLE) {
                    if (childCount > 0 && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        if (MomentActivity.this.isNeedHidePoint) {
                            ((MomentPresenter) MomentActivity.this.presenter).checkAllPublishedMoment();
                        }
                        MomentActivity.this.firstScrollTime = System.currentTimeMillis();
                        MomentActivity.this.scrollState = true;
                    }
                } else if (recyclerView.getChildLayoutPosition(
                        recyclerView.getChildAt(recyclerView.getChildCount() - 1)) >= layout.getItemCount() - 1) {
                    // 已滚到底部：触发加载更多。
                    LogUtil.d(TAG, "加载更多 —— 正在加载");
                    if (!MomentActivity.this.isRunning) {
                        MomentActivity.this.showLoadMore();
                        MomentActivity.this.loadMoreData();
                    }
                } else {
                    LogUtil.d(TAG, "加载更多 —— 没有更多数据");
                    MomentActivity.this.showNoFooter();
                }
                GlideUtils.setImageDelayedLoad(MomentActivity.this, newState);
            }
        });
        this.rvAdapter.setOnPreviewMomentListener(new AbsInteractionAdapter.OnPreviewMomentListener() {
            @Override
            public void onPreviewMoment(DbMoment moment) {
                if (moment.isPreviewed()) {
                    return;
                }
                ((MomentPresenter) MomentActivity.this.presenter).checkPreviewBehavior(MomentActivity.this, moment);
            }

            @Override
            public void preViewH5(String url, String title) {
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                StartWebUtils.startH5Activity(MomentActivity.this, url);
            }

            @Override
            public void preViewVideo(String videoUrl, boolean hasTokenOrKey) {
                if (TextUtils.isEmpty(videoUrl)) {
                    return;
                }
                LogUtil.i(TAG, "preViewVideo :" + videoUrl);
                Intent intent = new Intent(MomentActivity.this, PlayVideoActivity.class);
                intent.putExtra(PlayVideoActivity.VIDEO_MSG_DATA, videoUrl);
                intent.putExtra(PlayVideoActivity.HAS_VIDEO_TOKEN_OR_KEY, hasTokenOrKey);
                MomentActivity.this.startActivity(intent);
            }
        });
        // 监听可见项变化：对官方动态上报跳过行为。
        this.rv.addOnScrollListener(new OnItemVisibleListener(layoutManager) {
            @Override
            protected void onItemVisible(int position) {
                if (position < 0) {
                    LogUtil.d(TAG, "position < 0");
                    return;
                }
                int start = position <= MomentActivity.this.lastPosition ? position : MomentActivity.this.lastPosition;
                int end = position <= MomentActivity.this.lastPosition ? MomentActivity.this.lastPosition : position;
                for (int i = start; i <= end; i++) {
                    DbMoment moment = MomentActivity.this.rvAdapter.getData(i);
                    if (moment != null && !moment.isSkiped()) {
                        ((MomentPresenter) MomentActivity.this.presenter).checkSkipBehavior(MomentActivity.this, moment);
                    }
                }
                MomentActivity.this.lastPosition = position;
            }
        });
    }

    @Override
    public void initView() {
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.fragment_moment);
        getWindow().setBackgroundDrawable(null);
        this.llRoot = (ViewGroup) findViewById(R.id.ll_root);
        View cachedRoot = this.rootView;
        if (cachedRoot == null) {
            this.mVsPublishProgress = (ViewStub) findViewById(R.id.vs_publish_progress);
            this.rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            this.rv = (RecyclerView) this.rootView.findViewById(R.id.rv_moment);
            initRv();
            if (this.hintPermission) {
                LogUtil.d(TAG, "onCreate: requestPermission");
            }
        } else {
            ViewGroup parent = (ViewGroup) cachedRoot.getParent();
            if (parent != null) {
                parent.removeView(this.rootView);
            }
        }
        initSwipeRefresh();
        initEditText();
    }

    /** 滚动停止时触发屏幕内动态的位置星级动画。 */
    private void dealScrollIdle(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        for (int i = firstVisible; i <= lastVisible; i++) {
            View itemView = layoutManager.findViewByPosition(i);
            if (itemView == null) {
                continue;
            }
            LbsLayout lbsLayout = (LbsLayout) itemView.findViewById(R.id.ll_lbs);
            if (lbsLayout == null) {
                continue;
            }
            DbMoment moment = this.rvAdapter.getData(i);
            if (moment == null || !moment.isShowLbsAnim()) {
                continue;
            }
            int[] location = new int[2];
            lbsLayout.getLocationOnScreen(location);
            if (location[1] > 0 && location[1] < this.screenHeight) {
                dealLbsAnim(lbsLayout, moment);
                if (this.pendingLbsAnmis == null) {
                    this.pendingLbsAnmis = new LinkedList<>();
                }
                this.pendingLbsAnmis.addFirst(moment.getMomentId());
                moment.setShowLbsAnim(false);
            }
        }
    }


    /** 播放位置星级表情动画（图标由星级决定）。 */
    private void dealLbsAnim(LbsLayout lbsLayout, DbMoment moment) {
        ViewParent parent = lbsLayout.getParent();
        if (parent == null) {
            LogUtil.i(TAG, "dealLbsAnim() parent == null");
            return;
        }
        ((ViewGroup) parent).setClipChildren(false);
        final ImageView emojiView = (ImageView) lbsLayout.findViewById(R.id.iv_emoji);
        emojiView.setVisibility(View.VISIBLE);
        emojiView.setAlpha(0.0f);
        emojiView.setTranslationY(0.0f);
        Glide.with(this)
                .asGif()
                .load(getStarEmoji(moment.getMomentLbs().getStar()))
                .listener(this)
                .into(emojiView);
        emojiView.animate()
                .alpha(1.0f)
                .translationY(DimenUtil.dp2px(this, -25.0f))
                .setDuration(3000L)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        emojiView.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void initData() {
        if (!this.isInitData) {
            return;
        }
        if (MomentApp.isSendingVideo()) {
            addPublishVideoOrPhotoCallback();
        }
        this.startWebReceiver = new StartWebReceiver();
        initAssistant();
        dealAssistant(getIntent());
        this.scrollToLocation = getIntent().getBooleanExtra(Constants.POSITION_LOCATE, false);
        this.headDressManager = HeadDressManager.getInstance();
        this.iConChangeReceiver = new IConChangeReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }
                LogUtil.i(TAG, "moment receive action:" + action);
                if (IConChangeReceiver.ICON_CHANGE_ACTION.equals(action)) {
                    boolean downloadSuccess = intent.getBooleanExtra(IConChangeReceiver.DOWNLOAD_STATUS, false);
                    LogUtil.i(TAG, "isSuccess:" + downloadSuccess);
                    if (downloadSuccess) {
                        changeMyHeadIcon();
                    }
                }
            }
        };
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        this.accountInfoServe = AccountInfoServerImpl.getInstance(getApplicationContext());
        this.iConChangeReceiver.register(this);
        this.flag = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(StartWebReceiver.ACTION_FLAG_START_WEB_PROCESS);
        BroadcastReceiverUtil.registerReceiver(this, this.startWebReceiver, intentFilter);
        getMomentData(Constants.RCType.INIT);
    }

    private void getMomentData(Constants.RCType refreshType) {
        this.mRcType = refreshType;
        refreshData(refreshType == Constants.RCType.INIT ? this.rvAdapter.getLastData() : null);
    }

    @Override
    public void refreshData(final DbMoment lastMoment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                doRefreshData(lastMoment);
            }
        });
    }

    private void doRefreshData(DbMoment lastMoment) {
        initLoadDataFromNet(lastMoment);
        initDressServe();
        this.screenHeight = ScreenUtils.getScreenHeight(this);
        this.rvAdapter.refreshSelfInfo();
        final boolean lbsSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
        this.isNeedHidePoint = ((MomentPresenter) this.presenter).getUnCheckPublishedMomentCount() > 0;
        final long likeCount = ((MomentPresenter) this.presenter).loadNewLikeMessageCountAboutMine();
        final long commentCount = ((MomentPresenter) this.presenter).loadNewCommentCountAboutMine();
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.refreshMyName(true);
                if (lbsSwitch) {
                    MomentActivity.this.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                MomentActivity.this.dealScrollIdle(recyclerView);
                            }
                        }
                    });
                }
                MomentActivity.this.refreshLikeMessageInfo(likeCount, commentCount);
                MomentActivity.this.init = true;
            }
        });
        ((MomentPresenter) this.presenter).dealIllegal();
        loadFriendInfo();
    }

    private IllegalMessageHandler getIllegalMessageHandler() {
        if (this.illegalMessageHandler == null) {
            this.illegalMessageHandler = IllegalMessageHandler.getInstance(getApplicationContext());
        }
        return this.illegalMessageHandler;
    }

    /** 加载好友列表并刷新昵称、头像装扮。 */
    private void loadFriendInfo() {
        final List<Friend> friends = ((MomentPresenter) this.presenter).getFriendInfo();
        this.mFriends = friends;
        loadNicknameFromFriendList(friends);
        loadHeadFromFriendList(friends);
        LogUtil.i(TAG, "initFriendInfo:" + friends);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.refreshFriendList(friends);
                MomentActivity.this.init = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
        MomentAdapter adapter = this.rvAdapter;
        if (adapter != null) {
            adapter.onResume();
        }
        UnreadHelper.getInstance(this).refreshRedPoint();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SharedTool.getFlagFromOS(this) == Constants.FLAG_FROM_OS) {
            refreshViewState();
        }
    }

    private void initLoadDataFromNet(DbMoment lastMoment) {
        if (this.isRunning) {
            LogUtil.d(TAG, "isRunning == true !!");
            return;
        }
        this.isRunning = true;
        ((MomentPresenter) this.presenter).loadMomentsFromNet(0L, COUNT, lastMoment, false);
    }

    private void refreshMyName(boolean showDefault) {
        String myName = ((MomentPresenter) this.presenter).getMyName(this);
        if (!TextUtils.isEmpty(myName)) {
            TextSizeUtil.setUpdateText(this, this.momentUserNameTv, myName);
        } else if (showDefault) {
            this.momentUserNameTv.setText(getString(R.string.unknown_watch));
        }
    }

    private void refreshFriendList(List<Friend> friends) {
        List<Friend> target = friends;
        if (target == null || target.isEmpty()) {
            LogUtil.w(TAG, "initFriendInfo friendList is null");
            target = new ArrayList<>();
        }
        MomentAdapter adapter = this.rvAdapter;
        if (adapter != null) {
            adapter.setFriendList(target);
        }
    }
    private void refreshMyHeadIcon() {
        FileManager.setMyIconPath(this);
        String myHeadIconPath = ((MomentPresenter) this.presenter).getMyHeadIconPath();
        LogUtil.i(TAG, "refreshMyHeadIcon() myHeadIconPath=" + myHeadIconPath);
        updateMyHeadIcon(myHeadIconPath);
    }

    private void updateMyHeadIcon(String iconPath) {
        if (isDestroyed()) {
            LogUtil.e(TAG, "updateMyHeadIcon: activity is destroyed.");
            return;
        }
        RequestOptions options = new RequestOptions()
                .signature(new ObjectKey(String.valueOf(Math.random())))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.default_custom_default)
                .transform((Transformation<Bitmap>) new CircleCrop());
        LogUtil.d(TAG, "myIcon=" + iconPath);
        Glide.with(this).load(iconPath).apply(options).into(this.ivChampion);
    }

    /** 展示新的点赞/评论提示数量，没有新消息时刷新桌面角标。 */
    private void refreshLikeMessageInfo(long likeCount, long commentCount) {
        long total = (likeCount > 0 ? likeCount : 0L) + (commentCount > 0 ? commentCount : 0L);
        LogUtil.d(TAG, "mineLikeListSize: " + likeCount + " commentListSize: " + commentCount);
        if (total > 0) {
            showNewLikeView((int) total);
        } else {
            notifyAppIconUnreadCount();
        }
    }

    private void showNewLikeView(int count) {
        if (this.newLikeRL == null) {
            View inflated = this.mVsViewNewLike.inflate();
            this.newLikeRL = (AppLinearLayout) inflated.findViewById(R.id.ll_new_like);
            this.newLikeRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemUtil.isFastDoubleClick()) {
                        LogUtil.i(TAG, "onClick: newLikeRL click too fast.");
                        return;
                    }
                    MomentActivity.this.startActivity(new Intent(view.getContext(), NewLikeActivity.class));
                    HandlerUtil.runOnUIThreadDelay(new Runnable() {
                        @Override
                        public void run() {
                            MomentActivity.this.newLikeRL.setVisibility(View.GONE);
                        }
                    }, 1500L);
                }
            });
            this.mUnreadView = (UnreadView) inflated.findViewById(R.id.iv_unread_view);
        }
        this.newLikeRL.setVisibility(View.VISIBLE);
        this.mUnreadView.showNumber(Math.min(count, MAX_UNREAD_NAME));
    }

    private void notifyAppIconUnreadCount() {
        LogUtil.d(TAG, "notifyAppIconUnreadCount: ");
        UnreadHelper.getInstance(this).showUnreadNumber();
    }

    private void initRvHeaderView() {
        View headerView = AsyncLayoutLoader.getInstance().inflateView(R.layout.header_recycle_moment,
                LayoutInflater.from(this), this.rv);
        this.rvAdapter.setHeaderView(headerView);
        this.rvAdapter.setViewAttachedToWindow(this);
        this.ivChampion = (ImageView) headerView.findViewById(R.id.iv_champion);
        this.mVsViewNewLike = (ViewStub) headerView.findViewById(R.id.vs_view_new_like);
        this.ivBanner = (TextView) headerView.findViewById(R.id.tv_moment_banner);
        headerView.findViewById(R.id.iv_moment_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast.");
                    return;
                }
                SearchMomentActivity.start(MomentActivity.this);
            }
        });
        Glide.with(this).load(R.drawable.bg_head_view).into((ImageView) headerView.findViewById(R.id.iv_head_bg));
        refreshMyHeadIcon();
        this.publishLl = (AppLinearLayout) headerView.findViewById(R.id.ll_publish_btn);
        this.ivPublishAdd = (ImageView) headerView.findViewById(R.id.ivPublishAdd);
        this.tvPublishAdd = (TextView) headerView.findViewById(R.id.tvPublishAdd);
        setPublishBgDefault();
        ((ImageView) headerView.findViewById(R.id.iv_avatar_bg)).setBackgroundResource(R.drawable.bg_self_avatar);
        this.momentUserNameTv = (TextView) headerView.findViewById(R.id.moment_name_tv);
        refreshMyName(false);
        this.ivSvgaAvatarDress = (SVGAImageView) headerView.findViewById(R.id.iv_child_moment_avatar_dress);
        if (!((MomentPresenter) this.presenter).isWatchBind()) {
            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    judgeCardAndBind();
                }
            });
        }
        this.ivChampion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast.");
                    return;
                }
                if (!((MomentPresenter) MomentActivity.this.presenter).isWatchBind()) {
                    MomentActivity.this.judgeCardAndBind();
                    return;
                }
                WatchAccountInfo accountInfo = ((MomentPresenter) MomentActivity.this.presenter).getWatchAccountInfo();
                if (accountInfo == null) {
                    return;
                }
                Intent intent = new Intent(MomentActivity.this, ShareActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_WATCH_ID, accountInfo.getWatchId(MomentActivity.this));
                intent.putExtra(Constants.INTENT_EXTRA_IS_SELF, true);
                intent.putExtra(Constants.INTENT_EXTRA_ICON_PATH,
                        ((MomentPresenter) MomentActivity.this.presenter).getMyHeadIconPath());
                intent.putExtra(Constants.INTENT_EXTRA_NAME, accountInfo.getName(MomentActivity.this));
                intent.putExtra(Constants.INTENT_EXTRA_START_FROM_MOMENT, true);
                MomentActivity.this.startActivity(intent);
            }
        });
        this.publishLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemUtil.isFastDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast.");
                    return;
                }
                if (MomentActivity.this.getIllegalMessageHandler().checkNeedDisableSend()) {
                    MomentActivity.this.getIllegalMessageHandler().showDisableSendMessageHintDialog(
                            MomentActivity.this, new HintIllegalContentDialog.HintClickListener() {
                                @Override
                                public void onAppealClick() {
                                }

                                @Override
                                public void onConfirmClick() {
                                }
                            });
                    return;
                }
                MomentActivity.this.startPublishMoment(null, StringConstant.StartType.START_TYPE_CLICK);
            }
        });
    }

    /** 进入发布流程：优先恢复未完成的文本/图文草稿。 */
    private void startPublishMoment(final PostStatusPayload postStatusPayload, final String startType) {
        if (StringConstant.StartType.START_TYPE_FOR_WEICHAT.equals(startType)
                && this.isInStartStartPublishMomentMethod) {
            LogUtil.d(TAG, "startPublishMoment: ");
            return;
        }
        this.isInStartStartPublishMomentMethod = true;
        LogUtil.d(TAG, "startPublishMoment: " + postStatusPayload + startType);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                boolean hasDynamicData = SaveDynamic.hasDynamicData(MomentActivity.this, "");
                PublishTextBean publishTextBean = SaveDynamic.getSavePublishTextBean(MomentActivity.this);
                LogUtil.d(TAG, "contentVideoPhoto: " + hasDynamicData + "      publishTextBean: " + publishTextBean);
                if (publishTextBean != null) {
                    MomentActivity.this.startActivity(new Intent(MomentActivity.this, PushTextActivity.class));
                } else if (!hasDynamicData) {
                    MomentActivity.this.isWatchBind(postStatusPayload, startType);
                } else {
                    MomentActivity.this.startActivity(new Intent(MomentActivity.this, PushPictureActivity.class));
                }
            }
        });
    }

    private void isWatchBind(PostStatusPayload postStatusPayload, String startType) {
        if (!((MomentPresenter) this.presenter).isWatchBind()) {
            judgeCardAndBind();
            return;
        }
        Intent intent = new Intent(this, PublishActivity.class);
        if (postStatusPayload != null && StringConstant.StartType.START_TYPE_DUER.equals(startType)) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ApiConstants.NAME, postStatusPayload);
            intent.putExtras(bundle);
        }
        try {
            startActivityForResult(intent, Constants.PUBLISH_REQUEST_CODE);
        } catch (Exception e) {
            LogUtil.e(TAG, "isWatchBind : startActivityForResult : ", e);
        }
        MomentBehavior.publishClick(this, System.currentTimeMillis(), startType);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        HeadDressManager dressManager = this.headDressManager;
        if (dressManager == null) {
            return;
        }
        if (hasFocus) {
            dressManager.notifyVisibleToWindow();
        } else {
            dressManager.notifyInvisibleToWindow();
        }
        LogUtil.d(TAG, "onWindowFocusChanged: hasFocus = " + hasFocus);
    }

    /** 禁言状态下把发布按钮置为不可用样式。 */
    private void setPublishBgDisable() {
        if (!getIllegalMessageHandler().checkNeedDisableSend()) {
            return;
        }
        LogUtil.d(TAG, "setPublishBgDisable");
        this.publishLl.setBackgroundResource(R.drawable.bg_btn_disable_send);
        Glide.with(this).load(R.drawable.ic_add_not).into(this.ivPublishAdd);
        this.tvPublishAdd.setTextColor(getResources().getColor(R.color.color_bdbdbd));
        this.tvPublishAdd.setShadowLayer(0.0f, 0.0f, 0.0f,
                getResources().getColor(R.color.color_bdbdbd));
    }

    private void setPublishBgDefault() {
        LogUtil.d(TAG, "setPublishBgDefault");
        this.publishLl.setVisibility(View.VISIBLE);
        this.publishLl.setBackgroundResource(R.drawable.bg_btn_push_send);
        Glide.with(this).load(R.drawable.ic_publish_plus).into(this.ivPublishAdd);
        this.tvPublishAdd.setTextColor(getResources().getColor(R.color.color_ffffff));
        this.tvPublishAdd.setShadowLayer(1.0f, 2.0f, 0.0f,
                getResources().getColor(R.color.publish_btn_text_shadow_color));
    }

    private void initRvFooterView() {
        this.mFooterView = AsyncLayoutLoader.getInstance().inflateView(R.layout.footer_recycle_moment,
                LayoutInflater.from(this), this.rv);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.7f, 100);
        this.loading = (ImageView) this.mFooterView.findViewById(R.id.iv_loading);
        this.mTvLoadMore = (TextView) this.mFooterView.findViewById(R.id.tv_load_more);
        this.rvAdapter.setFooterView(this.mFooterView);
    }

    private void judgeCardAndBind() {
        if (!((MomentPresenter) this.presenter).isSimStateAbsent()
                || ((MomentPresenter) this.presenter).isWatchBind()) {
            return;
        }
        jumpToPrompt();
    }

    private void loadMoreData() {
        this.isRunning = true;
        this.rv.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("moment", "加载更多数据 —— offset = " + MomentActivity.this.offset);
                ((MomentPresenter) MomentActivity.this.presenter).loadMomentsFromNet(MomentActivity.this.offset,
                        COUNT, MomentActivity.this.rvAdapter.getLastData(), false);
            }
        }, 1000L);
    }
    @Override
    public void loadSuccess(final List<DbMoment> moments) {
        LogUtil.i(TAG, "loadSuccess offset:" + this.offset + ",data size:" + (moments == null ? 0 : moments.size())
                + ", init = " + this.init);
        this.isRunning = false;
        if (moments == null || moments.isEmpty()) {
            showNoMore();
            return;
        }
        int size = moments.size();
        if (this.init) {
            this.rvAdapter.addData(moments);
            String share = getIntent() != null ? getIntent().getStringExtra("share") : null;
            if (share != null && "share".equals(share)) {
                LogUtil.d(TAG, "scroll share message");
                this.rv.scrollToPosition(1);
            }
        } else {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    final List<Friend> friends = ((MomentPresenter) MomentActivity.this.presenter).getFriendInfo();
                    LogUtil.i(TAG, "initFriendInfo:" + friends);
                    HandlerUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            MomentActivity.this.refreshFriendList(friends);
                            MomentActivity.this.rvAdapter.addData(moments);
                        }
                    });
                }
            });
        }
        if (size % COUNT != 0) {
            LogUtil.d(TAG, "showNoMore");
            showNoMore();
        }
        this.offset += size;
        if (this.scrollToLocation) {
            if (this.rvAdapter.getData().size() > 0) {
                this.rv.smoothScrollToPosition(1);
                ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(1, 0);
            }
            this.scrollToLocation = false;
        }
        List<DbMoment> deleteList = ((MomentPresenter) this.presenter).getDeleteList();
        if (!CollectionUtil.isEmpty(deleteList)) {
            this.rvAdapter.removeDatas(deleteList);
        }
        if (this.pullDownRefresh) {
            endRefresh();
        }
        this.pullDownRefresh = false;
        ((MomentPresenter) this.presenter).getReminderConfig(this.mRcType, 19);
    }

    private void showLoadMore() {
        this.mTvLoadMore.setText(R.string.moment_loading);
        this.loading.setVisibility(View.VISIBLE);
        this.loading.setBackground(this.animDrawable);
        this.animDrawable.start();
        this.mFooterView.setVisibility(View.VISIBLE);
    }

    private void showNoFooter() {
        AnimationDrawable drawable = this.animDrawable;
        if (drawable != null) {
            drawable.stop();
        }
        this.loading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showNoMore() {
        AnimationDrawable drawable = this.animDrawable;
        if (drawable != null) {
            drawable.stop();
        }
        this.loading.setVisibility(View.GONE);
        this.mTvLoadMore.setText(R.string.moment_nomore);
        endRefresh();
    }

    @Override
    public void loadError() {
        LogUtil.d("moment", "加载数据失败回调 —— 没有更多");
        showNoMore();
    }

    @Override
    public void loadLocalData(long offset, long count) {
        ((MomentPresenter) this.presenter).loadMomentsFromDb(offset, count, false, null);
    }

    @Override
    public void likeSuccess(DbLikeMessage likeMessage) {
        LogUtil.d("moment", "点赞数据成功回调 —— dbLikeMessage = " + likeMessage);
        refreshUIWhenLikeSuccess(likeMessage);
    }

    @Override
    public void cancelLikeSuccess(DbMoment moment) {
        this.rvAdapter.setCancelLikeTime(System.currentTimeMillis());
        this.rvAdapter.refreshLikeData(moment);
    }

    @Override
    public void likeError(String message) {
        dealLikeError(message);
    }

    private void dealIMPush(String content, int type) {
        if (type != 30) {
            return;
        }
        LogUtil.d(TAG, "update name :" + content);
        if (content.contains("name")) {
            changeMyName(content);
        }
    }

    private void deleteFriendInfo(String watchId) {
        MomentAdapter adapter = this.rvAdapter;
        if (adapter != null) {
            adapter.deleteFriendInfo(watchId);
        }
    }

    private void handleCommentMoment(final DbMomentComment comment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final boolean aboutMyMoment = ((MomentPresenter) MomentActivity.this.presenter)
                        .isAboutMyMoment(comment.getMomentWatchId());
                final boolean aboutMyComment = ((MomentPresenter) MomentActivity.this.presenter)
                        .isAboutMyComment(comment.getReplyId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (aboutMyMoment || aboutMyComment) {
                            MomentActivity.this.showNewLikeView();
                        }
                        MomentActivity.this.rvAdapter.addCommentData(comment);
                    }
                });
            }
        });
    }

    /** 查询新的点赞与评论数量并刷新提示条。 */
    private void showNewLikeView() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                List<DbLikeMessage> likeMessages = ((MomentPresenter) MomentActivity.this.presenter)
                        .loadNewLikeMessageAboutMine();
                List<DbMomentComment> comments = ((MomentPresenter) MomentActivity.this.presenter)
                        .loadNewCommentAboutMine();
                int likeSize = (likeMessages == null || likeMessages.size() <= 0) ? 0 : likeMessages.size();
                int commentSize = (comments == null || comments.size() <= 0) ? 0 : comments.size();
                LogUtil.d(TAG, "likeSize: " + likeSize + " commentSize: " + commentSize);
                subscriber.onNext(likeSize + commentSize);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "showNewLikeView#e:", throwable);
                    }

                    @Override
                    public void onNext(Integer count) {
                        if (count > 0) {
                            MomentActivity.this.showNewLikeView(count);
                            return;
                        }
                        if (MomentActivity.this.newLikeRL != null) {
                            MomentActivity.this.newLikeRL.setVisibility(View.GONE);
                        }
                        MomentActivity.this.notifyAppIconUnreadCount();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(EventType eventType) {
        LogUtil.d(TAG, "onEvent: eventType = [" + eventType + "]");
        if (eventType == null) {
            return;
        }
        int type = eventType.getType();
        if (type == EventType.CONTACT_UPDATE || type == EventType.SYNC_LAUNCHER_DATA) {
            LogUtil.i(TAG, "同步Launcher旧数据完成后操作或者联系人更新");
            this.isInitData = true;
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    MomentActivity.this.loadFriendInfo();
                }
            });
        } else if (type == EventType.HIGH_RISK_DIALOG) {
            LogUtil.i(TAG, "receive :4");
            showHighDialog();
        } else if (type == EventType.RECEIVE_IM_REMINDER) {
            checkUnExecutedReminder();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DbLikeMessage likeMessage) {
        refreshUIWhenLikeSuccess(likeMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LbsStarEvent lbsStarEvent) {
        List<DbMoment> data = this.rvAdapter.getData();
        int index = data.indexOf(lbsStarEvent.getDbMoment());
        if (index < 0) {
            LogUtil.i(TAG, "onEvent() i < 0 ");
            return;
        }
        data.get(index).setMomentLbs(lbsStarEvent.getDbMoment().getMomentLbs());
        this.rvAdapter.notifyItemChanged(index + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommentEvent<DbMomentComment> commentEvent) {
        if (commentEvent == null || commentEvent.getBean() == null
                || !(commentEvent.getBean() instanceof DbMomentComment)) {
            return;
        }
        if ("3".equals(commentEvent.getResult())) {
            showDownVersionDialog();
        }
        refreshUIWhenCommentSuccess(commentEvent.getBean());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SendVideoParam sendVideoParam) {
        if (sendVideoParam == null) {
            LogUtil.d(TAG, "onEvent: sendVideoParam is null");
        } else if (MomentApp.isSendingVideo()) {
            ToastUtil.showShort(MomentApp.getAppContext(), R.string.sending_video_tip);
        } else {
            bindPublishService(sendVideoParam);
        }
    }
    /** 绑定发布服务：后台把视频/图片交给 Service 上传。 */
    private void bindPublishService(final SendVideoParam sendVideoParam) {
        LogUtil.d(TAG, "bindPublishService");
        MomentApp.setIsSendingVideo(true);
        showSendingVideoView();
        final Intent intent = new Intent(this, PublishService.class);
        intent.setAction(PublishService.ACTION_BIND);
        this.mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                LogUtil.d(TAG, "onServiceConnected success");
                MomentActivity.this.mPublishBinder = (PublishService.PublishBinder) binder;
                if (sendVideoParam == null) {
                    LogUtil.d(TAG, "sendVideoMsg: sendVideoParam is null");
                    return;
                }
                MomentActivity.this.addPublishVideoOrPhotoCallback();
                MomentActivity.this.mPublishBinder.sendVideoMsg(sendVideoParam.getThumbnailPath(),
                        sendVideoParam.isFromAlbum(), sendVideoParam.getVideoName(),
                        sendVideoParam.getShareVideoMoment(), sendVideoParam.getVideoText(),
                        sendVideoParam.getPoiBean(), sendVideoParam.getVideoTokenVoResponse(),
                        sendVideoParam.getFriendsVisibleBean());
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                MomentApp.setIsSendingVideo(false);
                ToastUtil.showShort(MomentApp.getAppContext(), R.string.publish_fail);
            }
        };
        if (AppProcessUtil.isAppProcess(getApplicationContext())) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    MomentActivity.this.bindService = bindService(intent, mConnection, BIND_AUTO_CREATE);
                }
            });
        }
    }

    /** 发布视频/图片的回调实现。 */
    private class PublishCallback implements PublishVideoOrPhotoCallback {

        @Override
        public void onPublishSuccess(final DbMoment moment) {
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    releaseBgSendCallBackAndUnBind();
                    if (MomentActivity.this.isFinishing() || MomentActivity.this.isDestroyed()) {
                        return;
                    }
                    MomentActivity.this.showPublishSuccessView(moment);
                }
            });
        }

        @Override
        public void onProgress(int progress) {
            if (MomentActivity.this.isFinishing() || MomentActivity.this.isDestroyed()) {
                return;
            }
            if (MomentActivity.this.mSbProgress == null) {
                MomentActivity.this.showSendingVideoView();
            }
            MomentActivity.this.mSbProgress.setSelectProgress(progress);
        }

        @Override
        public void onFail(final Throwable throwable) {
            releaseBgSendCallBackAndUnBind();
            LogUtil.e(TAG, "onSendVideoMsg fail, ", throwable);
            if (MomentActivity.this.isFinishing() || MomentActivity.this.isDestroyed()) {
                return;
            }
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    MomentActivity.this.dismissPublishView();
                }
            });
            String message = throwable == null ? null : throwable.getMessage();
            if (Objects.equals("000008", message)) {
                publishInvalidateWithTip();
                return;
            }
            if ("000060".equals(message)) {
                ToastUtil.showShort(MomentApp.getAppContext(), R.string.publish_limit);
                return;
            }
            if (!TextUtils.isEmpty(message) && (message.contains("1003") || message.contains("1002"))) {
                ToastUtil.showShort(MomentApp.getAppContext(), R.string.frequent_request);
            } else if (!TextUtils.isEmpty(message) && message.contains("000061")) {
                ToastUtil.showShort(MomentApp.getAppContext(), R.string.publish_invalidate);
            } else {
                // 兜底走统一映射，保证 000007 账号异常等错误码有对应文案；同时避免 message 为 null 时崩溃。
                ToastUtil.showShort(MomentApp.getAppContext(), PublishErrorUtil.getFailMessageRes(message));
            }
        }
    }

    public void addPublishVideoOrPhotoCallback() {
        if (this.mPublishVideoOrPhotoCallback == null) {
            this.mPublishVideoOrPhotoCallback = new PublishCallback();
            PubVideoOrPhotoServer.getInstance().setPublishVideoOrPhotoCallback(this.mPublishVideoOrPhotoCallback);
        }
    }

    private void releaseBgSendCallBackAndUnBind() {
        if (this.mConnection == null || !this.bindService) {
            return;
        }
        PublishService.PublishBinder binder = this.mPublishBinder;
        if (binder != null && binder.getService() != null) {
            binder.getService().setCallback(null);
            this.mPublishBinder = null;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (MomentActivity.this.bindService) {
                    try {
                        unbindService(MomentActivity.this.mConnection);
                    } catch (Exception e) {
                        LogUtil.e(TAG, "unbindService error: ", e);
                    }
                    MomentActivity.this.bindService = false;
                }
            }
        });
    }

    /** 发布内容命中敏感词时的提示弹窗。 */
    public void publishInvalidateWithTip() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                HintSensitiveContentDialog dialog = new HintSensitiveContentDialog(MomentActivity.this);
                dialog.setHintClickListener(new HintSensitiveContentDialog.HintClickListener() {
                    @Override
                    public void onConfirmClick() {
                        onSensitiveDialogConfirmed();
                    }
                });
                dialog.setDialogContent(getString(R.string.publish_sensitive_dialog_content),
                        getString(R.string.high_risk_hint_tittle));
                dialog.show();
            }
        });
        LogUtil.d(TAG, "发布内容包含铭感内容");
    }

    private void showSendingVideoView() {
        LogUtil.d(TAG, "showSendingVideoView");
        if (this.mFlPublishProgress == null) {
            View inflated = this.mVsPublishProgress.inflate();
            this.mTvPublishTip = (TextView) inflated.findViewById(R.id.tv_publish_tip);
            this.mIvArrow = (ImageView) inflated.findViewById(R.id.iv_arrow);
            this.mFlPublishProgress = (ViewGroup) inflated.findViewById(R.id.fl_publish_progress);
            this.mSbProgress = (NumTipSeekBar) inflated.findViewById(R.id.sb_progress);
        }
        this.mFlPublishProgress.setVisibility(View.VISIBLE);
        this.mSbProgress.setVisibility(View.VISIBLE);
        this.mSbProgress.setSelectProgress(0);
        this.mTvPublishTip.setText(R.string.sending_video_top_tip);
        this.mTvPublishTip.setOnClickListener(null);
        this.mTvPublishTip.setTextColor(ContextCompat.getColor(this, R.color.white));
        this.mIvArrow.setVisibility(View.GONE);
        ViewCompat.setTranslationY(this.mFlPublishProgress, -((int) getResources().getDimension(R.dimen.dp_36)));
        ViewCompat.setAlpha(this.mFlPublishProgress, 0.0f);
        ViewCompat.animate(this.mFlPublishProgress).cancel();
        ViewCompat.animate(this.mFlPublishProgress).alpha(1.0f).translationY(0.0f).setDuration(400L);
    }

    /** 发布成功后的提示条：点击可滚动到刚发布的动态。 */
    private void showPublishSuccessView(final DbMoment moment) {
        if (this.mFlPublishProgress == null) {
            return;
        }
        LogUtil.d(TAG, "showPublishSuccessView");
        this.mIvArrow.setVisibility(View.VISIBLE);
        this.mSbProgress.setVisibility(View.GONE);
        this.mSbProgress.setSelectProgress(0);
        String tip = getString(R.string.sending_video_top_success_tip);
        int newlineIndex = tip.indexOf("\n");
        String flatTip = tip.replace("\n", "");
        this.mTvPublishTip.setText(flatTip);
        SpannableStringBuilder builder = new SpannableStringBuilder(flatTip);
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_FF9E11)),
                newlineIndex, flatTip.length(), 33);
        this.mTvPublishTip.setText(builder);
        this.mTvPublishTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToPublishedMoment(moment);
            }
        });
        this.mFlPublishProgress.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                MomentActivity.this.mFlPublishProgress.setVisibility(View.GONE);
                LogUtil.d(TAG, "dismissPublishSuccessView");
            }
        }, com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
        if (TextUtils.isEmpty(moment.getLocation()) || SaveDynamic.hasFirstPublished(this)) {
            return;
        }
        SaveDynamic.saveFirstPublished(this);
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                ToastUtil.showShort(MomentApp.getAppContext(), R.string.first_lbs_tip);
            }
        }, 1500L);
    }

    private void scrollToPublishedMoment(DbMoment moment) {
        MomentAdapter adapter;
        if (moment == null || (adapter = this.rvAdapter) == null || this.rv == null) {
            return;
        }
        int index = adapter.indexOf(moment);
        if (index == -1) {
            LogUtil.d(TAG, "indexOf dbMoment not find");
            return;
        }
        if (this.rvAdapter.getHeaderView() != null) {
            index++;
        }
        if (index >= 0 && index < this.rvAdapter.getItemCount()) {
            LogUtil.d(TAG, "smoothScrollToPosition: index = " + index);
            this.rvAdapter.notifyDataSetChanged();
            this.rv.smoothScrollToPosition(index);
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(index, 0);
            return;
        }
        LogUtil.d(TAG, "index invalid");
    }

    private void dismissPublishView() {
        if (this.mFlPublishProgress == null) {
            return;
        }
        LogUtil.d(TAG, "dismissPublishView");
        this.mFlPublishProgress.setVisibility(View.GONE);
        this.mSbProgress.setVisibility(View.GONE);
        this.mIvArrow.setVisibility(View.GONE);
        this.mSbProgress.setSelectProgress(0);
    }

    private void showDownVersionDialog() {
        if (DialogUtil.isDialogShowing(this.downVersionDialog)) {
            DialogUtil.dismissDialog(this.downVersionDialog);
        }
        this.downVersionDialog = DialogUtil.makeLongSolidBtnDialog(this,
                new LongSolidBtnBean(this, true, getString(R.string.down_version), R.string.i_known));
        LongSolidButton bottomBtn = this.downVersionDialog.getBottomBtn();
        bottomBtn.setBgColorIdArray(new int[]{R.color.color_yellow_ffbb38, R.color.color_yellow_fd9316});
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(MomentActivity.this.downVersionDialog);
            }
        });
        this.downVersionDialog.getTvTitle().setGravity(GravityCompat.START);
        DialogUtil.showDialog(this.downVersionDialog);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final DbMoment moment) {
        LogUtil.d(TAG, "dbMoment:" + (moment == null ? "" : moment.getMomentId()));
        if (moment == null) {
            return;
        }
        int index = this.rvAdapter.addData(0, moment);
        int type = moment.getType().intValue();
        boolean skipScroll = type == 6 || type == 24 || type == 27;
        if (index != -1 && !skipScroll) {
            LogUtil.d(TAG, "smoothScrollToPosition");
            this.rv.smoothScrollToPosition(index);
            LogUtil.d(TAG, "scrollToPositionWithOffset");
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(index, 0);
        }
        HandlerUtil.runOnBackgroundDelay(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.offset = 0L;
                MomentActivity.this.isRunning = true;
                ((MomentPresenter) MomentActivity.this.presenter).clearRecordMap();
                ((MomentPresenter) MomentActivity.this.presenter).loadMomentsFromNet(0L, COUNT, moment, true);
            }
        }, 1000L);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCancelLike(EventData eventData) {
        if (eventData.getType() == EventData.CANCEL_LIKE_CHANGE_MOMENTADAPTER) {
            this.rvAdapter.refreshLikeData((DbMoment) eventData.getData());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        if (eventData.getType() == EventData.DELETE_MOMENT) {
            if (eventData.getData() instanceof DbMoment) {
                LogUtil.i(TAG, "receive delete moment event:" + eventData);
                removeMoment((DbMoment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.DELETE_INVALIDATE_MOMENT) {
            if (eventData.getData() instanceof DbMoment) {
                LogUtil.i(TAG, "receive delete invalidate moment event:" + eventData);
                deleteInvalidateMoment((DbMoment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.DELETE_COMMENT) {
            if (eventData.getData() instanceof DbMomentComment) {
                LogUtil.i(TAG, "receive delete comment event:" + eventData);
                removeMomentComment((DbMomentComment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.PUBLISH_COMMENT) {
            if (eventData.getData() instanceof DbMomentComment) {
                LogUtil.i(TAG, "receive public comment event:" + eventData);
                handleCommentMoment((DbMomentComment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.LIKE_MOMENT) {
            if (eventData.getData() instanceof DbLikeMessage) {
                LogUtil.i(TAG, "receive like moment event:" + eventData);
                handleLikeMoment((DbLikeMessage) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.PUBLISH_MOMENT) {
            if (eventData.getData() instanceof DbMoment) {
                LogUtil.i(TAG, "receive publish moment event:" + eventData);
                handleMomentPublished((DbMoment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.CONTACT_DEL) {
            if (eventData.getData() instanceof String) {
                LogUtil.i(TAG, "receive friend del:" + eventData);
                deleteFriendInfo((String) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.UPDATE_PIC_LOCAL_PATH) {
            if (eventData.getData() instanceof DbMoment) {
                LogUtil.i(TAG, "receive update pic local path event:" + eventData);
                refreshLocalPathData((DbMoment) eventData.getData());
            }
            return;
        }
        if (eventData.getType() == EventData.CHANGE_TO_REPORTED) {
            return;
        }
        if (eventData.getType() == EventData.CANCEL_LIKE_CHANGE_MOMENT_ADAPTER_MOMENT) {
            showNewLikeView();
            DbMoment moment = (DbMoment) eventData.getData();
            moment.setDataUrl(null);
            this.rvAdapter.refreshLikeData(moment);
            return;
        }
        if (eventData.getType() == EventData.REFRESH_MOMENT_COMMENTS) {
            this.rvAdapter.refreshMomentCommentData((DbMoment) eventData.getData());
            return;
        }
        if (eventData.getType() == EventData.REFRESH_MOMENT_NEW_MESSAGE_VIEW) {
            showNewLikeView();
            return;
        }
        if (eventData.getType() == EventData.BATCH_DELETE_MOMENT) {
            if (eventData.getData() instanceof List) {
                List<DbMoment> moments = (List) eventData.getData();
                LogUtil.i(TAG, "receive batch delete moment, moments = " + moments);
                this.rvAdapter.removeDatas(moments);
            }
            return;
        }
        if (eventData.getType() == EventData.CHANGE_VISIBLE_MOMENT) {
            if (eventData.getData() instanceof DbMoment) {
                DbMoment moment = (DbMoment) eventData.getData();
                LogUtil.i(TAG, "receive visibleChange = " + moment);
                this.rvAdapter.refreshVisiblePicData(moment);
            }
            return;
        }
        if (eventData.getType() == EventData.SHOW_MOMENT_REMINDER) {
            if (eventData.getData() instanceof DbMoment) {
                DbMoment moment = (DbMoment) eventData.getData();
                LogUtil.d(ReminderHelper.M_TAG, "刷新温馨提醒 —— dbMoment = " + moment.getMomentId());
                this.rvAdapter.refreshMomentReminder(moment);
            }
            return;
        }
        if (eventData.getType() == EventData.CHECK_MOMENT_REMINDER) {
            LogUtil.i(ReminderHelper.M_TAG, "温馨提示配置结束, check moment reminder");
            checkUnExecutedReminder();
        }
    }

    private void removeMomentComment(DbMomentComment comment) {
        this.rvAdapter.removeCommentData(comment);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMMomentMsgData msgData) {
        int type = msgData.getType();
        String content = msgData.getContent();
        LogUtil.i(TAG, "moment im type:" + type + ",content:" + content);
        dealIMPush(content, type);
    }

    private void refreshViewState() {
        ViewGroup.LayoutParams layoutParams = this.llRoot.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        this.llRoot.setLayoutParams(layoutParams);
    }

    private void refreshUIWhenLikeSuccess(final DbLikeMessage likeMessage) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> moments = ((MomentPresenter) MomentActivity.this.presenter)
                        .getMomentById(likeMessage.getMomentId());
                if (moments == null || moments.isEmpty()) {
                    return;
                }
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<DbMoment> iterator = moments.iterator();
                        while (iterator.hasNext()) {
                            MomentActivity.this.rvAdapter.refreshLikeData(iterator.next());
                        }
                    }
                });
            }
        });
    }

    private void refreshUIWhenCommentSuccess(DbMomentComment comment) {
        if (comment == null) {
            return;
        }
        this.rvAdapter.addCommentData(comment);
    }

    @Override
    public void onViewAttachedToWindow(AbsViewHolder viewHolder) {
        View headerView = this.rvAdapter.getHeaderView();
        if (viewHolder == null || headerView == null || this.ivSvgaAvatarDress == null
                || viewHolder.itemView != headerView) {
            return;
        }
        this.headDressManager.viewAttachedToWindow(this.ivSvgaAvatarDress);
    }

    @Override
    public void onViewDetachedFromWindow(AbsViewHolder viewHolder) {
        View headerView = this.rvAdapter.getHeaderView();
        if (viewHolder == null || headerView == null || this.ivSvgaAvatarDress == null
                || viewHolder.itemView != headerView) {
            return;
        }
        this.headDressManager.viewDetachedFromWindow(this.ivSvgaAvatarDress);
    }

    @Override
    public boolean onLoadFailed(GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
        LogUtil.e(TAG, "onLoadFailed()", e);
        return false;
    }

    @Override
    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target,
            DataSource dataSource, boolean isFirstResource) {
        resource.stop();
        resource.start();
        resource.setLoopCount(1);
        return false;
    }

    private void changeMyName(String content) {
        LogUtil.d(TAG, "changeMyName: content: " + content);
        ImMyNameBean myNameBean = (ImMyNameBean) JSONUtil.fromJSON(content, ImMyNameBean.class);
        if (myNameBean == null || myNameBean.getData() == null || myNameBean.getData().getData() == null) {
            return;
        }
        ImMyNameInfoBean nameInfo = (ImMyNameInfoBean) JSONUtil.fromJSON(myNameBean.getData().getData(),
                ImMyNameInfoBean.class);
        LogUtil.d(TAG, "imMyNameInfoBean:" + nameInfo);
        if (nameInfo == null) {
            return;
        }
        String name = nameInfo.getName();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        ((MomentPresenter) this.presenter).setMyName(name);
        TextSizeUtil.setUpdateText(this, this.momentUserNameTv, name);
    }

    private void changeMyHeadIcon() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.accountInfoServe.refreshMyIconPath(MomentActivity.this.getApplicationContext());
                String myHeadIconPath = MomentActivity.this.accountInfoServe.getMyHeadIconPath();
                LogUtil.d(TAG, "initMyIcon successfully,icon path:" + myHeadIconPath);
                MomentActivity.this.updateMyHeadIcon(myHeadIconPath);
                MomentActivity.this.rvAdapter.notifyDataSetChanged();
            }
        });
    }

    /** 删除被服务端判定失效的动态，自己的动态额外提示敏感内容。 */
    private void deleteInvalidateMoment(DbMoment moment) {
        LogUtil.d(TAG, "dealDeleteInvalidateMoment#dbMoment:" + moment);
        this.rvAdapter.removeData(moment);
        if (!MomentApp.getWatchId().equals(moment.getWatchId())) {
            return;
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                HintSensitiveContentDialog dialog = new HintSensitiveContentDialog(MomentActivity.this);
                dialog.setHintClickListener(new HintSensitiveContentDialog.HintClickListener() {
                    @Override
                    public void onConfirmClick() {
                    }
                });
                dialog.setDialogContent(MomentActivity.this.getString(R.string.publish_sensitive_dialog_content),
                        MomentActivity.this.getString(R.string.high_risk_hint_tittle));
                dialog.show();
            }
        });
    }

    private void handleMomentPublished(DbMoment moment) {
        moment.setShowLbsAnim(true);
        this.rvAdapter.addData(0, moment);
        if (this.rvAdapter.contains(moment)) {
            this.isNeedHidePoint = true;
        }
    }

    private void handleLikeMoment(final DbLikeMessage likeMessage) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final boolean aboutMyMoment = ((MomentPresenter) MomentActivity.this.presenter)
                        .isAboutMyMoment(likeMessage.getMomentWatchId());
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (aboutMyMoment) {
                            MomentActivity.this.showNewLikeView();
                        }
                        DbMoment moment = MomentActivity.this.rvAdapter.getMoment(likeMessage.getMomentId());
                        if (moment != null) {
                            moment.setLikeTotal(moment.getLikeTotal() + 1);
                            MomentActivity.this.rvAdapter.refreshPushLikeData(moment);
                        }
                    }
                });
            }
        });
    }

    private void refreshLocalPathData(DbMoment moment) {
        if (moment == null) {
            LogUtil.w(TAG, "refreshLocalPathData: dbMoment is null!");
        } else {
            this.rvAdapter.refreshLocalPathData(moment);
        }
    }

    @Override
    public void removeMoment(DbMoment moment) {
        this.rvAdapter.removeData(moment);
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
    public void removeComment(DbMomentComment comment) {
        this.rvAdapter.removeCommentData(comment);
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
    public void loadCommentSuccess(DbMoment moment) {
        this.rvAdapter.refreshMomentCommentData(moment);
    }

    @Override
    public void dealIllegal() {
        LogUtil.d(TAG, "dealIllegal");
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.publishLl.setVisibility(View.VISIBLE);
                if (MomentActivity.this.rvAdapter != null) {
                    MomentActivity.this.rvAdapter.notifyDataSetChanged();
                }
                MomentActivity.this.setPublishBgDisable();
            }
        });
    }
    /** 绑定个性装扮服务（需系统支持）。 */
    private void bindDressService() {
        if (!FuncUtil.supportPersonalityDress()) {
            return;
        }
        Intent intent = new Intent(Constants.BindDressInfo.BIND_ACTION);
        intent.setPackage(Constants.BindDressInfo.BIND_PACKAGE);
        this.mDressConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                try {
                    binder.linkToDeath(MomentActivity.this.mDeathRecipient, 0);
                } catch (RemoteException e) {
                    LogUtil.e(TAG, "bindDressService error, RemoteException", e);
                } catch (Exception e) {
                    LogUtil.e(TAG, "bindDressService error, Exception", e);
                }
                IDressServiceBinder dressBinder = IDressServiceBinder.Stub.asInterface(binder);
                if (MomentActivity.this.mDressServe != null) {
                    MomentActivity.this.mDressServe.setProxy(new DressProxy(dressBinder));
                }
                loadNicknameFromFriendList(MomentActivity.this.mFriends);
                loadHeadFromFriendList(MomentActivity.this.mFriends);
                MomentActivity.this.bindDressResult = true;
                LogUtil.d(TAG, "Dress service connect success!");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                LogUtil.d(TAG, "Dress service Disconnected!");
                MomentActivity.this.bindDressResult = false;
            }
        };
        this.mDeathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                releaseDressConnect();
                bindDressService();
                LogUtil.i(TAG, "dress binder death, link to death");
            }
        };
        this.bindDressResult = bindService(intent, this.mDressConnection, BIND_AUTO_CREATE);
        LogUtil.d(TAG, "bindService result: " + this.bindDressResult);
    }

    public void releaseDressConnect() {
        IDressServe dressServe = this.mDressServe;
        IBinder.DeathRecipient deathRecipient = this.mDeathRecipient;
        if (dressServe == null || deathRecipient == null) {
            LogUtil.i(TAG, "not releaseDressConnect --> mDressServe = " + this.mDressServe
                    + ", mDeathRecipient = " + this.mDeathRecipient);
            return;
        }
        dressServe.releaseDeathRecipient(deathRecipient);
        try {
            if (this.mDressConnection != null && this.bindDressResult) {
                unbindService(this.mDressConnection);
                this.bindDressResult = false;
                LogUtil.i(TAG, "releaseDressConnect -->");
            } else {
                LogUtil.i(TAG, "not releaseDressConnect --> mDressConnection = " + this.mDressConnection
                        + ", bindDressResult = " + this.bindDressResult);
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "releaseDressConnect error ex:" + e);
        } finally {
            this.bindDressResult = false;
        }
    }

    private void loadNicknameFromFriendList(final List<Friend> friends) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (!FuncUtil.supportPersonalityDress() || MomentActivity.this.mDressServe == null) {
                    return;
                }
                List<String> watchIds = MomentActivity.this.getNeedLoadDressWatchId(friends);
                if (watchIds.isEmpty()) {
                    return;
                }
                MomentActivity.this.mDressServe.getNicknameIdByWatchId(watchIds,
                        MomentActivity.this.loadNickNameCallBack);
            }
        });
    }

    private void loadHeadFromFriendList(final List<Friend> friends) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (FuncUtil.supportPersonalityDress() && ModuleSwitch.getDressHeadSwitch(getApplication())
                        && MomentActivity.this.mDressServe != null) {
                    List<String> watchIds = MomentActivity.this.getNeedLoadDressWatchId(friends);
                    if (watchIds.isEmpty()) {
                        return;
                    }
                    MomentActivity.this.mDressServe.getHeadIdByWatchId(watchIds,
                            MomentActivity.this.loadHeadCallBack);
                    return;
                }
                LogUtil.i(TAG, "mDressServe 111" + MomentActivity.this.mDressServe);
            }
        });
    }

    /** 收集需要拉取装扮的好友 watchId（去重后加上自己）。 */
    private List<String> getNeedLoadDressWatchId(List<Friend> friends) {
        ArrayList<String> watchIds = new ArrayList<>();
        if (friends == null || this.mDressServe == null) {
            LogUtil.i(TAG, "dbFriends is null || mDressServe is null");
            return watchIds;
        }
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);
            if (friend.getWatchId() != null && !watchIds.contains(friend.getWatchId())) {
                watchIds.add(friend.getWatchId());
            }
        }
        watchIds.add(this.accountInfoServe.getWatchAccountInfo().getWatchId(this));
        LogUtil.i(TAG, "need load watchIds: " + watchIds);
        return watchIds;
    }

    /** 根据昵称 id 拉取昵称装扮数据并应用。 */
    private void getRemoteNicknames(HashMap<String, String> nicknameIdMap) {
        if (this.mDressServe == null) {
            LogUtil.i(TAG, "getRemoteNicknames mDressServe is null");
            return;
        }
        ArrayList<String> nicknameIds = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = nicknameIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String nicknameId = iterator.next().getValue();
            if (!nicknameIds.contains(nicknameId) && !DEFALUT_ID.equals(nicknameId)) {
                nicknameIds.add(nicknameId);
            }
        }
        LogUtil.i(TAG, "getRemoteNicknames: " + nicknameIds);
        List<DbNickname> nicknames = this.mDressServe.getNicknameById(nicknameIds, this.remoteNickNameCallBack);
        final HashMap<String, DbNickname> nicknameMap = new HashMap<>();
        if (nicknames != null) {
            for (Map.Entry<String, String> entry : nicknameIdMap.entrySet()) {
                String nicknameId = entry.getValue();
                for (int i = 0; i < nicknames.size(); i++) {
                    if (nicknames.get(i).getNicknameId().equals(nicknameId)) {
                        nicknameMap.put(entry.getKey(), nicknames.get(i));
                        break;
                    }
                }
            }
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DressUtil.updateDbNickName(nicknameMap);
                MomentActivity.this.setNameDress();
            }
        });
    }

    private void setNameDress() {
        String watchId = this.accountInfoServe.getWatchAccountInfo().getWatchId(this);
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.w(TAG, "setNicknameSource watchId is null");
            return;
        }
        if (this.momentUserNameTv != null) {
            LogUtil.d(TAG, "setNicknameSource text = " + this.momentUserNameTv.getText().toString());
            DressUtil.setNicknameSource(watchId, this.momentUserNameTv);
        }
    }

    @Override
    public void publishSuccess(DbMomentComment comment, String extra) {
        showLoadingSuccess();
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
        ToastUtil.showShortCover(this, getString(R.string.moment_already_deleted));
        dismissLoading();
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

    private void checkUnExecutedReminder() {
        ((MomentPresenter) this.presenter).dealUnExecutedImReminder(false);
    }

    /** 根据头像 id 拉取头像装扮数据并应用。 */
    private void getRemoteHeads(HashMap<String, String> headIdMap) {
        if (this.mDressServe == null) {
            LogUtil.i(TAG, "getRemoteHeads mDressServe is null");
            return;
        }
        ArrayList<String> headIds = new ArrayList<>();
        Iterator<Map.Entry<String, String>> iterator = headIdMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String headId = iterator.next().getValue();
            if (!headIds.contains(headId) && !DEFALUT_ID.equals(headId)) {
                headIds.add(headId);
            }
        }
        List<DbHead> heads = this.mDressServe.getHeadById(headIds, this.remoteHeadCallBack);
        LogUtil.i(TAG, "mDressServe: " + heads);
        final HashMap<String, DbHead> headMap = new HashMap<>();
        if (heads != null) {
            for (Map.Entry<String, String> entry : headIdMap.entrySet()) {
                String headId = entry.getValue();
                for (int i = 0; i < heads.size(); i++) {
                    if (heads.get(i).getHeadId().equals(headId)) {
                        headMap.put(entry.getKey(), heads.get(i));
                        break;
                    }
                }
            }
        }
        LogUtil.i(TAG, "getRemoteHeads: " + headMap);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                DressUtil.updateDbHead(MomentActivity.this, headMap);
                MomentActivity.this.setMyHeadDress();
            }
        });
    }

    private void setMyHeadDress() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                String watchId = MomentActivity.this.accountInfoServe.getWatchAccountInfo()
                        .getWatchId(MomentActivity.this);
                if (TextUtils.isEmpty(watchId)) {
                    LogUtil.w(TAG, "setMyHeadDress watchId is null");
                    return;
                }
                DressUtil.setDressHead(MomentActivity.this.getApplicationContext(), watchId,
                        MomentActivity.this.ivSvgaAvatarDress);
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DressUtil.removeDressHead(this.ivSvgaAvatarDress);
    }

    /** 释放装扮相关的回调引用。 */
    public void clean() {
        this.remoteNickNameCallBack = null;
        this.remoteHeadCallBack = null;
        this.loadHeadCallBack = null;
        this.loadNickNameCallBack = null;
    }

    private void showHighDialog() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MomentActivity.this.getIllegalMessageHandler().showHighRiskHintDialog(MomentActivity.this,
                        new HintIllegalContentDialog.HintClickListener() {
                            @Override
                            public void onAppealClick() {
                            }

                            @Override
                            public void onConfirmClick() {
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            if (!"Can not perform this action after onSaveInstanceState".equals(e.getMessage())) {
                throw e;
            }
            finish();
        }
    }

    private void initSwipeRefresh() {
        this.mRefreshFrameLayout = (SwipeRefreshLayout) this.rootView.findViewById(R.id.moment_ref_header);
        this.mRefreshLoadingView = (ImageView) this.rootView.findViewById(R.id.moment_iv_loading);
        this.mRefreshFrameLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onLoose() {
            }

            @Override
            public void onNormal() {
            }

            @Override
            public void onRefresh() {
                LogUtil.i(TAG, "onRefresh ");
                if (System.currentTimeMillis() - MomentActivity.this.latestOnRefreshTime < 10) {
                    LogUtil.d(TAG, "距离上次刷新时间过短，此次不刷新");
                    return;
                }
                MomentActivity.this.pullDownRefresh = true;
                MomentActivity.this.latestOnRefreshTime = System.currentTimeMillis();
                MomentActivity.this.setDragEnabled();
                MomentActivity.this.mRefreshLoadingView.setVisibility(View.VISIBLE);
                if (MomentActivity.this.mRefreshDrawable != null) {
                    MomentActivity.this.mRefreshDrawable.start();
                }
                if (MomentActivity.this.rvAdapter != null) {
                    MomentActivity.this.rvAdapter.clearData();
                }
                ((MomentPresenter) MomentActivity.this.presenter).clearRecordMap();
                MomentActivity.this.offset = 0L;
                MomentActivity.this.getMomentData(Constants.RCType.REFRESH);
            }
        });
        intiLoadAnim();
    }

    private void setDragEnabled() {
        this.mRefreshFrameLayout.setEnabled(false);
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                MomentActivity.this.mRefreshFrameLayout.setEnabled(true);
            }
        }, 1500L);
    }

    private void intiLoadAnim() {
        this.mRefreshDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.6f, 100);
        this.mRefreshLoadingView.setBackground(this.mRefreshDrawable);
    }

    public void endRefresh() {
        LogUtil.d(TAG, "endRefresh()");
        SwipeRefreshLayout refreshLayout = this.mRefreshFrameLayout;
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            this.mRefreshFrameLayout.stopRefresh();
        }
        AnimationDrawable drawable = this.mRefreshDrawable;
        if (drawable != null && drawable.isRunning()) {
            this.mRefreshDrawable.stop();
        }
        ImageView refreshView = this.mRefreshLoadingView;
        if (refreshView != null) {
            refreshView.setVisibility(View.GONE);
        }
    }
}
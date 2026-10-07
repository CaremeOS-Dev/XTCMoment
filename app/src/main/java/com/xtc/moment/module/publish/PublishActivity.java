package com.xtc.moment.module.publish;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.xtc.aitext.manager.AITextManager;
import com.xtc.aitext.weight.callback.AISuccessCallback;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.bigdata.common.utils.NetUtils;
import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseCtaPermissionActivity;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.constant.SystemPropertyConstant;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.assistant.ApiConstants;
import com.xtc.moment.module.assistant.MomentDeviceModule;
import com.xtc.moment.module.assistant.message.PostStatusPayload;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.MaxTextLengthBean;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.community.AboutMomentActivity;
import com.xtc.moment.module.community.CommunityConversationActivity;
import com.xtc.moment.module.illegal.config.IllegalConfigHandler;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.widget.HintIllegalContentDialog;
import com.xtc.moment.module.illegal.widget.HintSensitiveContentDialog;
import com.xtc.moment.module.main.MomentActivity;
import com.xtc.moment.module.publish.moodorstate.MoodOrStateActivity;
import com.xtc.moment.module.publish.multi.PushPictureActivity;
import com.xtc.moment.module.publish.text.PushTextActivity;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.MaxLengthWatcher;
import com.xtc.moment.module.widget.PublishAgreementDialog;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.LivePhotoServe;
import com.xtc.moment.service.PublishService;
import com.xtc.moment.third.bean.PushVideoBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.AppProcessUtil;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.IntentUtils;
import com.xtc.moment.util.ModuleSwitch;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.EditableDoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.system.SystemPropertyUtil;
import com.xtc.web.core.CoreConstants;

import java.util.ArrayList;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 动态发布入口页：展示可用的发布类型入口，并把拍照/相册/视频选择的结果交给
 * {@link PublishPresenter} 处理。
 */
public class PublishActivity extends BaseCtaPermissionActivity<IPublishView, PublishPresenter>
        implements IPublishView {

    private static final String ALBUM_NUM = "album_num";
    private static final int DELAY_OPEN_INPUTMETHOD_TIME = 100;
    private static final String IS_FUN_VIDEO = "isFunVideo";
    private static final String SELECT_PHOTO_LIST = "com.xtc.camera.SELECT_PHOTO_LIST";
    private static final String SELECT_PHOTO_NUM = "com.xtc.camera.SELECT_PHOTO_NUM";
    private static final String TAG = PublishActivity.class.getSimpleName();
    private static final String TARGET_APP = "targetApp";

    /** 发布类型：文字动态。 */
    private static final int MEDIA_TYPE_TEXT = 3;
    /** 发布类型：助手来源的文字动态。 */
    private static final int MEDIA_TYPE_ASSISTANT_TEXT = 103;
    /** 发布类型：AI 文案。 */
    private static final int MEDIA_TYPE_AI_TEXT = 29;
    /** 发布入口列表请求码。 */
    private static final int REQUEST_PUBLISH_ENTRY = 66;
    /** 最大可选相册数量。 */
    private static final int DEFAULT_MAX_ALBUM_NUM = 9;
    /** 视频内容不合法时的服务端错误码。 */
    private static final String ERROR_CODE_VIDEO_INVALID = "000008";

    private String aiContent;
    private String assistantContent;
    private boolean bindSucceed;
    private Intent cacheIntent;
    private boolean combinedDynamic;
    private FrameLayout flParent;
    boolean isAlbumForbid;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private PublishAdapter mAdapter;
    private ServiceConnection mConnection;
    private PublishService.PublishBinder mPublishBinder;
    private RecyclerView mRv;
    private View mask;
    private EditableDoubleFlatBtnDialog publishContentDialog;
    private PublishAgreementDialog showProtocolDialog;
    private volatile int maxAlbumNum = DEFAULT_MAX_ALBUM_NUM;

    /** AI 文案生成结果的回调。 */
    private final AISuccessCallback aiSuccessCallback = new AISuccessCallback() {
        @Override
        public void clickLeft() {
        }

        @Override
        public void clickRight(String text) {
            LogUtil.d(TAG, "clickRight:aiContent = " + PublishActivity.this.aiContent);
            PublishActivity.this.aiContent = text;
        }
    };

    @Override
    public void afterDealPermission() {
    }

    @Override
    public void beforeDealPermission() {
    }

    @Override
    public PublishPresenter createPresenter() {
        return new PublishPresenter(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult requestCode = " + requestCode + "resultCode = " + resultCode);
        if (resultCode != RESULT_OK) {
            return;
        }
        this.mRv.setVisibility(View.GONE);
        this.mask.setVisibility(View.GONE);
        if (requestCode == REQUEST_PUBLISH_ENTRY) {
            setResult(RESULT_OK);
            finish();
        } else {
            readPermission(requestCode, data);
        }
    }

    /**
     * 申请文件权限后根据请求码解析选择结果。
     */
    private void readPermission(final int requestCode, final Intent data) {
        requestRunTimePermission(PermissionStringUtils.FILE_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtil.d(TAG, "onGranted: requestPermission");
                DigitalManager.getInstance().clearDigitalEntity();
                DigitalManager.getInstance().getDigitalEntity().startPushTime = SystemClock.elapsedRealtime();
                int source = requestCode;
                if (source == 1) {
                    handleCameraResult(data);
                    return;
                }
                if (source == 2) {
                    handleAlbumResult(data);
                    return;
                }
                if (source == 3) {
                    handleVideoRecordResult(data);
                    return;
                }
                if (source == 4) {
                    handleCameraVideoResult(data);
                }
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                PublishActivity.this.finish();
            }
        });
    }

    /**
     * 处理系统相机拍照返回。
     */
    private void handleCameraResult(Intent data) {
        Bundle extras = data.getExtras();
        if (checkIsSupport()) {
            startPushPictureActivity("picture_photo_path", extras, "isFromAlbum", false, false);
            MomentBehavior.pushDynamicEntrace(this, 1);
            return;
        }
        String photoPath = extras != null ? extras.getString("output", null) : null;
        if (toastNoNet()) {
            return;
        }
        LogUtil.d(TAG, "onActivityResult: photoPath = " + photoPath);
        if (TextUtils.isEmpty(photoPath)) {
            finish();
        }
        this.mask.setVisibility(View.VISIBLE);
        PhotoMsg photoMsg = BeanConverterUtil.toPhotoMsg(photoPath);
        showLoading();
        savePhotoSuccess(photoMsg);
    }

    /**
     * 处理相册选择返回，可能包含实况照片或多图。
     */
    private void handleAlbumResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null) {
            LogUtil.i(TAG, "open album return, bundle is null");
            return;
        }
        int photoType = extras.getInt("com.xtc.camera.EXTRA_PHOTO_TYPE");
        LogUtil.i(TAG, "type = " + photoType);
        if (!checkIsSupport() || 2 == photoType) {
            if (checkIsSupport() && 2 == photoType) {
                dealMultiLivePhoto(extras);
                return;
            }
            String photoPath = extras.getString("output", null);
            int selectedPhotoType = extras.getInt("com.xtc.camera.EXTRA_PHOTO_TYPE");
            if (toastNoNet()) {
                LogUtil.i(TAG, "open album return, net unvalid");
                return;
            }
            LogUtil.d(TAG, selectedPhotoType + "onActivityResult: photoPath = " + photoPath);
            if (TextUtils.isEmpty(photoPath)) {
                return;
            }
            if (selectedPhotoType != 0) {
                if (1 == selectedPhotoType) {
                    if (!TextUtils.isEmpty(null)) {
                        dealFunVideo(extras);
                    } else {
                        chooseVideoItemSuccess(true, photoPath);
                    }
                }
                return;
            }
            boolean isLivePhoto = LivePhotoServe.isLivePhotoFile(photoPath);
            String videoFilePath = LivePhotoServe.getVideoFilePath(photoPath);
            if (isLivePhoto && !TextUtils.isEmpty(videoFilePath)) {
                LogUtil.i(TAG, "当前选择的是实况照片");
                chooseAlbumItemSuccess(BeanConverterUtil.toLivePhotoMsg(photoPath, videoFilePath));
            } else {
                chooseAlbumItemSuccess(BeanConverterUtil.toPhotoMsg(photoPath));
            }
            return;
        }
        startPushPictureActivity(1 == photoType ? "video_path" : "photo_path", extras,
                "isFromAlbum", true, false);
        MomentBehavior.pushDynamicEntrace(this, 2);
    }

    /**
     * 处理相册选择的视频返回。
     */
    private void handleVideoRecordResult(Intent data) {
        Bundle extras = data.getExtras();
        if (checkIsSupport()) {
            startPushPictureActivity("video_path", extras, "isFromAlbum", false, false);
            MomentBehavior.pushDynamicEntrace(this, 3);
            return;
        }
        if (extras != null) {
            String videoPath = extras.getString("output");
            LogUtil.d(TAG, "videoPath:" + videoPath);
            if (toastNoNet()) {
                return;
            }
            chooseVideoItemSuccess(false, videoPath);
        }
    }

    /**
     * 处理系统相机的视频拍摄返回。
     */
    private void handleCameraVideoResult(Intent data) {
        Bundle extras = data.getExtras();
        if (checkIsSupport()) {
            startPushPictureActivity("video_path", extras, "isFromAlbum", false, true);
            MomentBehavior.pushDynamicEntrace(this, 3);
            return;
        }
        if (extras != null) {
            dealFunVideo(extras);
        }
    }
    private void dealMultiLivePhoto(Bundle bundle) {
        LogUtil.i(TAG, "dealMultiLivePhoto");
        ArrayList<String> photoPaths = bundle.getStringArrayList(SELECT_PHOTO_LIST);
        if (CollectionUtil.isEmpty(photoPaths)) {
            LogUtil.i(TAG, "photoPath is null");
            finish();
            return;
        }
        String photoPath = photoPaths.get(0);
        String videoFilePath = LivePhotoServe.getVideoFilePath(photoPath);
        if (!LivePhotoServe.isLivePhotoFile(photoPath) || TextUtils.isEmpty(videoFilePath)) {
            return;
        }
        chooseAlbumItemSuccess(BeanConverterUtil.toLivePhotoMsg(photoPath, videoFilePath));
    }

    private void startPushPictureActivity(String extraKey, Bundle bundle, String albumFlagKey,
                                          boolean fromAlbum, boolean isFunVideo) {
        Intent intent = new Intent(this, PushPictureActivity.class);
        intent.putExtra(extraKey, bundle);
        intent.putExtra(albumFlagKey, fromAlbum);
        intent.putExtra(IS_FUN_VIDEO, isFunVideo);
        startActivity(intent);
    }

    private boolean checkIsSupport() {
        return this.combinedDynamic;
    }

    /**
     * 处理第三方分享视频的返回。
     */
    private void dealFunVideo(Bundle bundle) {
        String videoPath = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PATH);
        String funVideoParam = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PARAM);
        String packageName = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_PACK_NAME);
        long videoLength = bundle.getLong(Constants.ShareVideoKey.FUN_VIDEO_LENGTH, 0L);
        byte[] appIcon = bundle.getByteArray(Constants.ShareVideoKey.FUN_VIDEO_APP_ICON);
        String appName = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_NAME);
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = bundle.getString("output", null);
        }
        LogUtil.d(TAG, "dealFunVideo videoPath:" + videoPath + videoLength);
        if (toastNoNet()) {
            return;
        }
        chooseShareVideoItemSuccess(false, videoPath, packageName, funVideoParam, appIcon, appName, videoLength);
    }

    private void sendVideoRecord() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                PushVideoBean pushVideoBean = new PushVideoBean();
                pushVideoBean.setWatchId(MomentApp.getWatchId());
                pushVideoBean.setTime(String.valueOf(System.currentTimeMillis()));
                pushVideoBean.setType(String.valueOf(1));
                MomentBehavior.sendVideoAndSuccessSend(PublishActivity.this, pushVideoBean);
            }
        });
    }

    private boolean toastNoNet() {
        if (NetworkUtils.isConnected(this)) {
            return false;
        }
        ToastUtil.showNoConnected(this);
        this.mRv.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void initData() {
        LogUtil.d(TAG, "initData");
        this.isAlbumForbid = SystemPropertyUtil.getBoolean(SystemPropertyConstant.PROPERTY_ALBUM_FORBID, false);
        this.presenter.getMomentTemplateFromNet();
        this.presenter.updateBanState();
        this.combinedDynamic = ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MULTI_TYPE_COMBINED_DYNAMIC, false);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                getMaxAlbumNum();
            }
        });
        LogUtil.i(TAG, "combinedDynamic queryModuleSwitchByBoolean  " + this.combinedDynamic);
    }

    /**
     * 从模块开关扩展参数中读取相册最大可选数量。
     */
    private void getMaxAlbumNum() {
        String extra = WatchAccountBase.queryModuleSwitchExtraByInt(this,
                ModuleSwitchConstant.MULTI_TYPE_COMBINED_DYNAMIC, "");
        if (TextUtils.isEmpty(extra)) {
            return;
        }
        this.maxAlbumNum = ((Integer) JSONUtil.getJSONValue(extra, ALBUM_NUM)).intValue();
    }

    @Override
    public void requestPermission() {
        requestRunTimePermission(PermissionStringUtils.PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                PermissionStringUtils.checkPermissionForReTryBaseUrl(PublishActivity.this);
                PublishActivity.this.initView();
                PublishActivity.this.initData();
                LogUtil.d(TAG, "onGranted: requestPermission");
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                PublishActivity.this.finish();
            }
        });
    }

    @Override
    public void refusePermission() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop: ");
        SystemUtil.resetEnterFuncFlag();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume: ");
        dealSendAIContent();
        SystemUtil.resetEnterFuncFlag();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dealActivityResult(intent);
    }

    /**
     * 处理外部通过 newIntent 传入的发布请求（相机回调或助手文案）。
     */
    private void dealActivityResult(Intent intent) {
        if (intent == null) {
            return;
        }
        LogUtil.d(TAG, "dealActivityResult: intent = " + intent.toURI());
        if (this.mPublishBinder == null) {
            this.cacheIntent = intent;
            return;
        }
        LogUtil.d(TAG, "dealActivityResult: intent = " + intent.toURI());
        String targetApp = intent.getStringExtra(TARGET_APP);
        Bundle extras = getIntent().getExtras();
        PostStatusPayload postStatusPayload = extras != null
                ? (PostStatusPayload) extras.getParcelable(ApiConstants.NAME) : null;
        LogUtil.d(TAG, "dealActivityResult: targetApp = " + targetApp);
        if (!TextUtils.isEmpty(targetApp) && PublishActivity.class.getSimpleName().equals(targetApp)) {
            onActivityResult(intent.getIntExtra(CoreConstants.TakePhotoConstant.REQUEST, -1),
                    intent.getIntExtra("RESULT", -1), intent);
            return;
        }
        if (postStatusPayload == null) {
            return;
        }
        if (TextUtils.isEmpty(postStatusPayload.getMessageType())
                || MomentDeviceModule.MESSAGE_TYPE_TEXT.equals(postStatusPayload.getMessageType())) {
            this.assistantContent = postStatusPayload.getContent();
            if (ModuleSwitch.isContentSupervision(this) && SharedTool.isBanToPublish(this)) {
                showBannedPublish();
            } else if (SharedTool.isFirstUseMomentText(this)) {
                showMomentProtocol(MEDIA_TYPE_ASSISTANT_TEXT);
                SharedTool.saveFirstUseMomentText(this, false);
            } else {
                startActivityByMediaType(MEDIA_TYPE_ASSISTANT_TEXT);
            }
        }
    }

    public void showInput(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        EditableDoubleFlatBtnDialog dialog = this.publishContentDialog;
        if (dialog == null) {
            this.publishContentDialog = DialogUtil.makeEditableDoubleFlatBtnDialog(this,
                    new DoubleFlatBtnBean(this, true, content, R.string.cancel, R.string.publish));
            DoubleFlatButton bottomBtn = this.publishContentDialog.getBottomBtn();
            bottomBtn.getLeftButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissPublishContentDialog();
                }
            });
            this.publishContentDialog.setImeOptions(4);
            EditText editableContent = this.publishContentDialog.getEtEditableContent();
            editableContent.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(MaxTextLengthBean.getInstance(this).getMaxLength())});
            editableContent.addTextChangedListener(new MaxLengthWatcher(this,
                    MaxTextLengthBean.getInstance(this).getMaxLength(), getString(R.string.content_is_over)));
            bottomBtn.setRightBgColorIdArray(UiConstants.Color.BLUE);
            bottomBtn.getRightArea().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dealSend(publishContentDialog.getContent());
                }
            });
            this.publishContentDialog.setOnEditListener(new EditableDoubleFlatBtnDialog.OnEditListener() {
                @Override
                public void onEdit(String text) {
                    dealSend(text);
                }
            });
        } else {
            dialog.setContent(content);
        }
        DialogUtil.showDialog(this.publishContentDialog);
    }

    public void dealSend(String content) {
        final IllegalMessageHandler illegalMessageHandler =
                IllegalMessageHandler.getInstance(getApplicationContext());
        if (!illegalMessageHandler.isInitHandler()) {
            Observable.just(false)
                    .map(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean value) {
                            illegalMessageHandler.initIllegalConfig(PublishActivity.this.getApplicationContext(),
                                    IllegalConfigHandler.obtainConfig(PublishActivity.this.getApplicationContext()),
                                    new IllegalMessageHandler.InitIllegalListener() {
                                        @Override
                                        public void onIIllegalFinish() {
                                            HandlerUtil.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    judgeCanSend(illegalMessageHandler, content);
                                                }
                                            });
                                        }
                                    });
                            return true;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        } else {
            judgeCanSend(illegalMessageHandler, content);
        }
    }

    private void judgeCanSend(IllegalMessageHandler illegalMessageHandler, String content) {
        if (!illegalMessageHandler.isDisableSend()) {
            publishShedContent(content);
        } else {
            illegalMessageHandler.showDisableSendMessageHintDialog(this,
                    new HintIllegalContentDialog.HintClickListener() {
                        @Override
                        public void onAppealClick() {
                        }

                        @Override
                        public void onConfirmClick() {
                        }
                    });
        }
    }

    private void publishShedContent(String content) {
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, R.string.content_is_null, Toast.LENGTH_SHORT).show();
            DialogUtil.dismissDialog(this.publishContentDialog);
            return;
        }
        if (NetUtils.isConnected(this)) {
            this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
            this.presenter.publishMoment(content, MEDIA_TYPE_TEXT, MEDIA_TYPE_TEXT);
        } else {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
            DialogUtil.dismissDialog(this.publishContentDialog);
        }
        dismissPublishContentDialog();
    }

    private void dismissPublishContentDialog() {
        DialogUtil.dismissDialog(this.publishContentDialog);
    }

    private void initLoadingPupWindowHolder() {
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                PublishActivity.this.setResult(RESULT_OK);
                PublishActivity.this.finish();
            }
        });
    }

    private void bindPublishService() {
        Intent intent = new Intent(this, PublishService.class);
        intent.setAction(PublishService.ACTION_BIND);
        this.mConnection = new PublishServiceConnection();
        if (AppProcessUtil.isAppProcess(getApplicationContext())) {
            this.bindSucceed = bindService(intent, this.mConnection, BIND_AUTO_CREATE);
        }
    }
    /**
     * 发布服务连接：连接成功后注册上传回调。
     */
    private final class PublishServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            PublishActivity.this.mPublishBinder = (PublishService.PublishBinder) binder;
            if (PublishActivity.this.cacheIntent != null) {
                Intent intent = new Intent(PublishActivity.this.cacheIntent);
                PublishActivity.this.cacheIntent = null;
                PublishActivity.this.dealActivityResult(intent);
            }
            PublishActivity.this.mPublishBinder.getService().setCallback(new PublishService.Callback() {
                @Override
                public void onUploadPhotoSuccess(PhotoMsg photoMsg, VideoTokenVoResponse videoTokenVoResponse) {
                    LogUtil.i(TAG, "onUploadPhotoSuccess");
                    PublishActivity.this.presenter.publishMoment(photoMsg, videoTokenVoResponse,
                            (FriendsVisibleBean) null);
                }

                @Override
                public void onUploadLivePhotoSuccess(LivePhotoMsg livePhotoMsg) {
                    LogUtil.i(TAG, "上传实况照片成功 LivePhotoMsg：" + livePhotoMsg);
                    PublishActivity.this.presenter.publishMoment(livePhotoMsg);
                }

                @Override
                public void onFail(final Throwable throwable) {
                    LogUtil.d(TAG, "onServiceConnected#onFail:" + throwable);
                    if (ERROR_CODE_VIDEO_INVALID.equals(throwable.getMessage())) {
                        PublishActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.d(TAG, "publish sensitive photo :" + throwable);
                                PublishActivity.this.publishInvalidate();
                            }
                        });
                    } else {
                        PublishActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PublishActivity.this.mask.setVisibility(View.GONE);
                                PublishActivity.this.mRv.setVisibility(View.VISIBLE);
                                PublishActivity.this.submitFail(throwable.getMessage());
                            }
                        });
                    }
                }
            });
        }
    }

    public PublishService.PublishBinder getPublishBinder() {
        return this.mPublishBinder;
    }

    private void initRecyclerView() {
        this.mask = findViewById(R.id.mask);
        this.mRv = (RecyclerView) findViewById(R.id.rv);
        this.flParent = (FrameLayout) findViewById(R.id.rl_publish);
        this.mAdapter = new PublishAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return PublishAdapter.ITEM_TYPE_NORMAL == PublishActivity.this.mAdapter
                        .getItemViewType(position) ? 1 : 2;
            }
        });
        this.mRv.setLayoutManager(gridLayoutManager);
        this.mRv.setAdapter(this.mAdapter);
        this.mAdapter.setOnItemClickListener(new PublishAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int type) {
                if (SystemUtil.isFastLongDoubleClick()) {
                    LogUtil.i(TAG, "onClick: click too fast. type = " + type);
                    return;
                }
                if (IllegalMessageHandler.getInstance(PublishActivity.this).checkNeedDisableSend()) {
                    IllegalMessageHandler.getInstance(PublishActivity.this).showDisableSendMessageHintDialog(
                            PublishActivity.this, new HintIllegalContentDialog.HintClickListener() {
                                @Override
                                public void onAppealClick() {
                                }

                                @Override
                                public void onConfirmClick() {
                                }
                            });
                    return;
                }
                if (ModuleSwitch.isContentSupervision(PublishActivity.this)
                        && SharedTool.isBanToPublish(PublishActivity.this)) {
                    PublishActivity.this.showBannedPublish();
                    return;
                }
                if (type == MEDIA_TYPE_AI_TEXT) {
                    AITextManager.getInstance(PublishActivity.this)
                            .setConfig(PublishActivity.this.presenter.getAiTextConfig())
                            .setSuccessCallback(PublishActivity.this.aiSuccessCallback)
                            .startEditedActivity();
                    return;
                }
                if (type != MEDIA_TYPE_ASSISTANT_TEXT) {
                    int forbidTipRes = R.string.text_camera_forbid_tip;
                    switch (type) {
                        case 0:
                            PublishActivity.this.startActivityByMediaType(0);
                            break;
                        case 1:
                            PublishActivity.this.startActivityByMediaType(1);
                            break;
                        case 2:
                            PublishActivity.this.startActivityByMediaType(2);
                            break;
                        case MEDIA_TYPE_TEXT:
                            if (SharedTool.isFirstUseMomentText(PublishActivity.this)) {
                                PublishActivity.this.showMomentProtocol(MEDIA_TYPE_TEXT);
                                SharedTool.saveFirstUseMomentText(PublishActivity.this, false);
                            } else {
                                PublishActivity.this.startActivityByMediaType(MEDIA_TYPE_TEXT);
                            }
                            break;
                        case 4:
                            LogUtil.d(TAG, " isAlbumForbid = " + PublishActivity.this.isAlbumForbid);
                            if (PublishActivity.this.isAlbumForbid) {
                                ToastUtil.showShort(PublishActivity.this,
                                        PublishActivity.this.getString(R.string.text_camera_forbid_tip));
                            } else if (SharedTool.isFirstUseMomentCamera(PublishActivity.this)) {
                                PublishActivity.this.showMomentProtocol(4);
                                SharedTool.saveFirstUseMomentCamera(PublishActivity.this, false);
                            } else {
                                PublishActivity.this.startActivityByMediaType(4);
                            }
                            break;
                        case 5:
                            LogUtil.i(TAG, "isFirstUseMomentPhoto =?"
                                    + SharedTool.isFirstUseMomentPhoto(PublishActivity.this));
                            if (SharedTool.isFirstUseMomentPhoto(PublishActivity.this)) {
                                PublishActivity.this.showMomentProtocol(5);
                                SharedTool.saveFirstUseMomentPhoto(PublishActivity.this, false);
                            } else {
                                PublishActivity.this.startActivityByMediaType(5);
                            }
                            break;
                        case 6:
                            if (MomentApp.isSendingVideo()) {
                                ToastUtil.showShort(MomentApp.getAppContext(), R.string.sending_video_tip);
                            } else if (PublishActivity.this.isVideoForbid(PublishActivity.this.isAlbumForbid)) {
                                if (!FuncUtil.supportCameraIntegration()) {
                                    forbidTipRes = R.string.text_video_forbid_tip;
                                }
                                ToastUtil.showShort(PublishActivity.this,
                                        PublishActivity.this.getString(forbidTipRes));
                            } else if (SharedTool.isFirstUseMomentVideo(PublishActivity.this)) {
                                PublishActivity.this.showMomentProtocol(6);
                                SharedTool.saveFirstUseMomentVideo(PublishActivity.this, false);
                            } else {
                                PublishActivity.this.startActivityByMediaType(6);
                            }
                            break;
                        default:
                            break;
                    }
                    return;
                }
                if (ModuleSwitch.isContentSupervision(PublishActivity.this)
                        && SharedTool.isBanToPublish(PublishActivity.this)) {
                    PublishActivity.this.showBannedPublish();
                } else if (SharedTool.isFirstUseMomentText(PublishActivity.this)) {
                    PublishActivity.this.showMomentProtocol(MEDIA_TYPE_ASSISTANT_TEXT);
                    SharedTool.saveFirstUseMomentText(PublishActivity.this, false);
                } else {
                    PublishActivity.this.startActivityByMediaType(MEDIA_TYPE_ASSISTANT_TEXT);
                }
            }

            @Override
            public void onAboutMomentClick() {
                AboutMomentActivity.jumpIntent(PublishActivity.this);
            }

            @Override
            public void onCommunityConversationClick() {
                CommunityConversationActivity.start(PublishActivity.this);
            }
        });
    }

    private boolean isVideoForbid(boolean albumForbid) {
        boolean forbid = albumForbid;
        if (!FuncUtil.supportCameraIntegration()) {
            forbid = SystemPropertyUtil.getBoolean(SystemPropertyConstant.PERSIST_SYS_FORBID_VIDEO, false);
        }
        LogUtil.d(TAG, "isVideoForbid() called with: isVideoForbid = [" + forbid + "]");
        return forbid;
    }

    private void showBannedPublish() {
        Toast.makeText(this, getString(R.string.moment_banned_publish), Toast.LENGTH_SHORT).show();
    }

    /**
     * 首次发布时展示动态发布协议。
     */
    private void showMomentProtocol(final int mediaType) {
        this.showProtocolDialog = new PublishAgreementDialog(this);
        this.showProtocolDialog.setTvTitle(getString(R.string.moment_protocol));
        this.showProtocolDialog.setTvContent(getString(R.string.moment_protocol_content));
        this.showProtocolDialog.setButtonText(getString(R.string.i_agree));
        this.showProtocolDialog.setOnClickListener(new PublishAgreementDialog.IDialogOnClickListener() {
            @Override
            public void onSureClick() {
                int type = mediaType;
                if (type == MEDIA_TYPE_TEXT) {
                    PublishActivity.this.startActivityByMediaType(MEDIA_TYPE_TEXT);
                } else if (type == 4) {
                    PublishActivity.this.startActivityByMediaType(4);
                } else if (type == 5) {
                    PublishActivity.this.startActivityByMediaType(5);
                } else if (type == 6) {
                    PublishActivity.this.startActivityByMediaType(6);
                } else if (type == MEDIA_TYPE_ASSISTANT_TEXT) {
                    PublishActivity.this.startActivityByMediaType(MEDIA_TYPE_ASSISTANT_TEXT);
                }
                DialogUtil.dismissDialog(PublishActivity.this.showProtocolDialog);
            }

            @Override
            public void onCancelClick() {
                DialogUtil.dismissDialog(PublishActivity.this.showProtocolDialog);
            }
        });
        DialogUtil.showDialog(this.showProtocolDialog);
    }
    private void startActivityByMediaType(int mediaType) {
        if (SystemUtil.hasEnterFunTimeOut()) {
            LogUtil.i(TAG, "onClick: click too fast.");
            return;
        }
        if (mediaType == -1) {
            LogUtil.d(TAG, "mediaType==-1错误");
            return;
        }
        if (mediaType == MEDIA_TYPE_ASSISTANT_TEXT) {
            if (this.assistantContent.length() > MaxTextLengthBean.getInstance(this).getMaxLength()) {
                showInput(this.assistantContent.substring(0,
                        MaxTextLengthBean.getInstance(this).getMaxLength()));
                ToastUtil.showShort(this, getString(R.string.content_is_over));
            } else {
                showInput(this.assistantContent);
            }
        } else {
            switch (mediaType) {
                case 0:
                    SystemUtil.setEnterFuncFlag();
                    startActivityForResult(MoodOrStateActivity.class, 0);
                    break;
                case 1:
                    SystemUtil.setEnterFuncFlag();
                    startActivityForResult(MoodOrStateActivity.class, 1);
                    break;
                case 2:
                    goPublishText();
                    break;
                case MEDIA_TYPE_TEXT:
                    startActivityForResult(PushTextActivity.class, MEDIA_TYPE_TEXT);
                    break;
                case 4:
                    startCameraCapture();
                    break;
                case 5:
                    startAlbumPick();
                    break;
                case 6:
                    HandlerUtil.runOnBackground(new Runnable() {
                        @Override
                        public void run() {
                            IntentUtils.go2VideoTarget(PublishActivity.this);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        MomentBehavior.clickFunctionRecord(this, mediaType);
    }

    /**
     * 打开系统相机拍照。
     */
    private void startCameraCapture() {
        SystemUtil.setEnterFuncFlag();
        Intent captureIntent = new Intent();
        captureIntent.setAction("android.media.action.IMAGE_CAPTURE");
        captureIntent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, getString(R.string.cancel));
        if (checkIsSupport()) {
            captureIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT,
                    getString(R.string.complete));
        } else {
            captureIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT,
                    getString(R.string.publish));
        }
        Intent targetIntent = new Intent(this, PublishActivity.class);
        targetIntent.putExtra(TARGET_APP, PublishActivity.class.getSimpleName());
        targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 1);
        captureIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
        startActivityForResult(captureIntent, 1);
    }

    /**
     * 打开相册选择图片，支持时使用多选。
     */
    private void startAlbumPick() {
        if (!checkIsSupport()) {
            SystemUtil.setEnterFuncFlag();
            Intent pickIntent = new Intent();
            pickIntent.setAction("android.intent.action.GET_CONTENT");
            if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                    ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false)) {
                pickIntent.setType("file/*");
            } else {
                pickIntent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
            }
            pickIntent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, getString(R.string.cancel));
            pickIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, getString(R.string.publish));
            Intent targetIntent = new Intent(this, PublishActivity.class);
            targetIntent.putExtra(TARGET_APP, PublishActivity.class.getSimpleName());
            targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 2);
            pickIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
            startActivityForResult(pickIntent, 2);
            return;
        }
        SystemUtil.setEnterFuncFlag();
        Intent pickIntent = new Intent();
        pickIntent.setAction("android.intent.action.GET_CONTENT");
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false)) {
            pickIntent.setType("file/*");
        } else {
            pickIntent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        }
        pickIntent.putExtra(SELECT_PHOTO_NUM, this.maxAlbumNum);
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, getString(R.string.complete));
        Intent targetIntent = new Intent(this, PublishActivity.class);
        targetIntent.putExtra(TARGET_APP, PublishActivity.class.getSimpleName());
        targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 2);
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
        startActivityForResult(pickIntent, 2);
    }

    private void goPublishText() {
        startActivityForResult(PushTextActivity.class, 2);
    }

    private void startActivityForResult(Class<?> activityClass, int publishType) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(Constants.INTENT_EXTRA_PUBLISH_TYPE, publishType);
        startActivityForResult(intent, REQUEST_PUBLISH_ENTRY);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().unregister(PublishActivity.this);
                PublishActivity.this.presenter.unregister();
            }
        });
        if (this.mConnection != null && this.bindSucceed) {
            releaseCallBack();
            unbindService(this.mConnection);
            this.bindSucceed = false;
        }
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.clean();
            LogUtil.d(TAG, "onDestroy#loadingPupWindowHolder.dismissLoading()");
            this.loadingPupWindowHolder.dismissLoading();
            this.loadingPupWindowHolder = null;
        }
        PublishAgreementDialog protocolDialog = this.showProtocolDialog;
        if (protocolDialog != null && protocolDialog.isShowing()) {
            this.showProtocolDialog.dismiss();
        }
        this.showProtocolDialog = null;
        dismissPublishContentDialog();
        super.onDestroy();
    }

    private void releaseCallBack() {
        PublishService.PublishBinder publishBinder = this.mPublishBinder;
        if (publishBinder == null || publishBinder.getService() == null) {
            return;
        }
        this.mPublishBinder.getService().setCallback(null);
        this.mPublishBinder = null;
    }

    @Override
    public void publishSuccess(DbMoment dbMoment) {
        LogUtil.d(TAG, "publishSuccess#dbMoment:" + dbMoment);
        dismissPublishContentDialog();
        if (isFinishing() || isDestroyed()) {
            return;
        }
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.showSuccess();
        }
        RecyclerView recyclerView = this.mRv;
        if (recyclerView == null || this.mask == null) {
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        this.mask.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventData eventData) {
        LogUtil.i(TAG, "onEvent2 data " + eventData);
        if (5 == eventData.getType() && eventData.getData() != null
                && (eventData.getData() instanceof DbMoment)) {
            publishSuccess((DbMoment) eventData.getData());
        }
    }

    @Override
    public void chooseAlbumItemSuccess(final PhotoMsg photoMsg) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                PublishActivity.this.loadingPupWindowHolder.showLoading(
                        PublishActivity.this.getWindow().getDecorView());
                PublishActivity.this.presenter.sendPhotoMsg(PublishActivity.this.getPublishBinder(), photoMsg);
            }
        });
    }

    @Override
    public void chooseAlbumItemSuccess(final LivePhotoMsg livePhotoMsg) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                PublishActivity.this.loadingPupWindowHolder.showLoading(
                        PublishActivity.this.getWindow().getDecorView());
                PublishActivity.this.presenter.sendLivePhotoMsg(PublishActivity.this.getPublishBinder(),
                        livePhotoMsg);
            }
        });
    }

    public void chooseVideoItemSuccess(final boolean fromAlbum, final String videoPath) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "chooseVideoItemSuccess");
                PublishActivity.this.presenter.sendVideoMsg(fromAlbum,
                        PublishActivity.this.getPublishBinder(), videoPath, "");
                PublishActivity.this.sendVideoRecord();
            }
        });
    }

    public void chooseShareVideoItemSuccess(final boolean fromAlbum, final String videoPath,
                                            final String packageName, final String funVideoParam,
                                            final byte[] appIcon, final String appName,
                                            final long videoLength) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "chooseVideoItemSuccess" + PublishActivity.this.getPublishBinder()
                        + fromAlbum);
                PublishActivity.this.loadingPupWindowHolder.showLoading(
                        PublishActivity.this.getWindow().getDecorView());
                PublishActivity.this.presenter.sendShareVideoMsg(fromAlbum,
                        PublishActivity.this.getPublishBinder(), videoPath, packageName, funVideoParam,
                        appIcon, appName, videoLength, null, null, null);
                PublishActivity.this.sendVideoRecord();
            }
        });
    }

    @Override
    public void showLoading() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LoadingPupWindowHolder holder = PublishActivity.this.loadingPupWindowHolder;
                if (holder != null) {
                    holder.showLoading(PublishActivity.this.getWindow().getDecorView());
                }
            }
        });
    }

    @Override
    public void dismissLoading() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LoadingPupWindowHolder holder = PublishActivity.this.loadingPupWindowHolder;
                if (holder != null) {
                    holder.dismissLoading();
                }
            }
        });
    }

    @Override
    public void toMomentActivity() {
        startActivity(new Intent(this, MomentActivity.class));
        finish();
    }

    @Override
    public void savePhotoSuccess(PhotoMsg photoMsg) {
        this.presenter.sendPhotoMsg(getPublishBinder(), photoMsg);
    }

    @Override
    public void publishFail(String message) {
        LogUtil.d(TAG, "publishFail msg = " + message);
        if (isFinishing() || isDestroyed()) {
            return;
        }
        dismissPublishContentDialog();
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.dismissLoading();
        }
        View maskView = this.mask;
        if (maskView == null || this.mRv == null) {
            return;
        }
        maskView.setVisibility(View.GONE);
        this.mRv.setVisibility(View.VISIBLE);
        if (!NetworkUtils.isConnected(this)) {
            toastNoNet();
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void publishLimited() {
        dismissPublishContentDialog();
        this.mRv.setVisibility(View.VISIBLE);
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_limit));
        LogUtil.d(TAG, "publishLimited");
    }

    @Override
    public void publishInvalidate() {
        dismissPublishContentDialog();
        this.mRv.setVisibility(View.VISIBLE);
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.dismissLoading();
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                HintSensitiveContentDialog dialog = new HintSensitiveContentDialog(PublishActivity.this);
                dialog.setHintClickListener(new HintSensitiveContentDialog.HintClickListener() {
                    @Override
                    public void onConfirmClick() {
                    }
                });
                dialog.setDialogContent(getString(R.string.publish_sensitive_dialog_content),
                        getString(R.string.high_risk_hint_tittle));
                dialog.show();
            }
        });
        LogUtil.d(TAG, "publishInvalidate");
    }

    public void submitFail(String message) {
        dismissPublishContentDialog();
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.dismissLoading();
        }
        if (!NetworkUtils.isConnected(this)) {
            toastNoNet();
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void initView() {
        LogUtil.d(TAG, "initView");
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_publish);
        getWindow().setBackgroundDrawable(null);
        initRecyclerView();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                PublishActivity.this.presenter.register();
                EventBus.getDefault().register(PublishActivity.this);
            }
        });
        try {
            bindPublishService();
        } catch (Exception e) {
            LogUtil.e(TAG, "bindPublishService#e:", e);
        }
        initLoadingPupWindowHolder();
        dealActivityResult(getIntent());
    }

    /**
     * 把 AI 生成的文案带入文字发布页。
     */
    private void dealSendAIContent() {
        if (TextUtils.isEmpty(this.aiContent)) {
            return;
        }
        if (this.aiContent.length() > MaxTextLengthBean.getInstance(this).getMaxLength()) {
            this.aiContent = this.aiContent.substring(0,
                    MaxTextLengthBean.getInstance(this).getMaxLength());
        }
        Intent intent = new Intent(this, PushTextActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_PUBLISH_TYPE, MEDIA_TYPE_TEXT);
        intent.putExtra(Constants.INTENT_EXTRA_TEXT_CONTENT, this.aiContent);
        startActivityForResult(intent, REQUEST_PUBLISH_ENTRY);
        this.aiContent = "";
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
}
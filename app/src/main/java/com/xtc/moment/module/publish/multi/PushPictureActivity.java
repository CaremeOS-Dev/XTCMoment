package com.xtc.moment.module.publish.multi;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.base.BaseCtaPermissionActivity;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.MaxTextLengthBean;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.illegal.config.IllegalConfigHandler;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.widget.HintIllegalContentDialog;
import com.xtc.moment.module.illegal.widget.HintSensitiveContentDialog;
import com.xtc.moment.module.main.MomentActivity;
import com.xtc.moment.module.playvideo.PlayVideoActivity;
import com.xtc.moment.module.publish.IPublishView;
import com.xtc.moment.module.publish.PublishPresenter;
import com.xtc.moment.module.publish.location.PublishPoiActivity;
import com.xtc.moment.module.publish.multi.adapter.BaseOverlayPageAdapter;
import com.xtc.moment.module.publish.multi.bean.FunVideoParms;
import com.xtc.moment.module.publish.multi.bean.PhotoBean;
import com.xtc.moment.module.publish.multi.bean.PhotoEvent;
import com.xtc.moment.module.publish.multi.util.GlideRoundTransform;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.module.publish.multi.view.ThreeIconDialog;
import com.xtc.moment.module.publish.visible.VisibleTypeActivity;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.MaxLengthWatcher;
import com.xtc.moment.module.widget.MomentVisibleShowUtil;
import com.xtc.moment.module.widget.PublishAgreementDialog;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.service.PublishService;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.third.bean.PushVideoBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.AppProcessUtil;
import com.xtc.moment.util.ClickUtils;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.EditableDoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.scalablecontainer.AppRelativeLayout;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.DimenUtil;
import com.xtc.web.core.CoreConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 多图/视频动态发布页：支持多图、视频、分享视频以及文字混排的动态发布。
 */
public class PushPictureActivity extends BaseCtaPermissionActivity<IPublishView, PublishPresenter>
        implements IPublishView {

    private static final String ALBUM_NUM = "album_num";
    public static final String EXTRA_DATA_CHANGE_TAG = "extra_data_change_tag";
    public static final String EXTRA_PHOTO_LISTS = "extra_photo_lists";
    private static final String EXTRA_PHOTO_TYP = "com.xtc.camera.EXTRA_PHOTO_TYPE";
    private static final String IS_FUN_VIDEO = "isFunVideo";
    private static final String SELECT_PHOTO_LIST = "com.xtc.camera.SELECT_PHOTO_LIST";
    private static final int SHOW_FRIENDS_NAME_MAX = 3;
    private static final String TAG = "PushPictureActivity";
    private static final String TARGET_APP = "targetApp";
    public static final String VIDEO_PHOTO_DATA = "video_photo";

    /** 视频内容不合法时的服务端错误码。 */
    private static final String ERROR_CODE_VIDEO_INVALID = "000008";
    /** 相册最大可选数量默认值。 */
    private static final int DEFAULT_ALBUM_MAX_COUNT = 9;
    /** 位置选择请求码。 */
    private static final int REQUEST_POI = 5;
    /** 可见范围选择请求码。 */
    private static final int REQUEST_VISIBLE_RANGE = 6;

    private BaseOverlayPageAdapter adapter;
    private ConstraintLayout add;
    private volatile boolean bindService;
    private Context context;
    private Dialog ctaPermissionDialog;
    private NormalIconDialog deleteDialog;
    private DoubleFlatBtnWithTitleDialog dialog;
    private PointerViewPager dynamicViewPager;
    FriendsVisibleBean friendsVisibleBean;
    private boolean hintPermission;
    private View icon;
    private boolean isFromAlbum;
    private boolean isFunVideo;
    private ImageView ivVisibleRange;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private volatile ServiceConnection mConnection;
    private PublishService.PublishBinder mPublishBinder;
    private String mVideoPath;
    private boolean needPreSaveThumbnailAndToken;
    private PhotoBean photoBean;
    private PoiBean poiBean;
    private EditableDoubleFlatBtnDialog publishContentDialog;
    private Button push;
    private AppRelativeLayout rlVisibleRange;
    private EditText shareText;
    private PublishAgreementDialog showProtocolDialog;
    private TextView tvLocation;
    private TextView tvVisibleRange;
    private TextView tvVisibleRangeDetails;
    private Bundle videoBundle;
    private ImageView videoImage;
    private RelativeLayout videoRoot;
    private final ArrayList<String> dataLists = new ArrayList<>();
    private int ALBUM_MAX_COUNT = DEFAULT_ALBUM_MAX_COUNT;

    @Override
    public void beforeDealPermission() {
    }

    private void setAddIconPosition(boolean centered) {
        ConstraintLayout addLayout = this.add;
        if (addLayout == null) {
            return;
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) addLayout.getLayoutParams();
        layoutParams.width = (int) DimenUtil.dp2pxFloat(this, centered ? 80.0f : 43.0f);
        layoutParams.height = (int) DimenUtil.dp2pxFloat(this, centered ? 90.0f : 53.0f);
        layoutParams.leftMargin = (int) DimenUtil.dp2pxFloat(this, centered ? 0.0f : 100.0f);
        this.add.setLayoutParams(layoutParams);
    }

    private void setCenterViewPager() {
        if (this.dynamicViewPager == null) {
            return;
        }
        boolean sizeLimited = isPicSizeLimit();
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) this.dynamicViewPager.getLayoutParams();
        float marginDp;
        if (this.ALBUM_MAX_COUNT == 1) {
            marginDp = DimenUtil.dp2pxFloat(this, sizeLimited ? 38.0f : 10.0f);
        } else {
            marginDp = DimenUtil.dp2pxFloat(this, sizeLimited ? 30.0f : 10.0f);
        }
        layoutParams.leftMargin = (int) marginDp;
        this.dynamicViewPager.setLayoutParams(layoutParams);
    }

    @Override
    public void requestPermission() {
        requestRunTimePermission(PermissionStringUtils.SEND_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                PermissionStringUtils.checkPermissionForReTryBaseUrl(PushPictureActivity.this);
                LogUtil.d(TAG, "onGranted: requestPermission");
                PushPictureActivity.this.dealInitDisable();
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                if (!denied.contains("android.permission.READ_CONTACTS")) {
                    PushPictureActivity.this.dealInitDisable();
                } else {
                    PushPictureActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void afterDealPermission() {
        getWindow().getDecorView().setBackground(null);
    }

    @Override
    public void refusePermission() {
        finish();
    }

    /**
     * 检查并初始化违规内容处理器，未初始化时先拉取配置。
     */
    private void dealInitDisable() {
        final IllegalMessageHandler illegalMessageHandler =
                IllegalMessageHandler.getInstance(getApplicationContext());
        if (!illegalMessageHandler.isInitHandler()) {
            Observable.just(false)
                    .map(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean value) {
                            illegalMessageHandler.initIllegalConfig(
                                    PushPictureActivity.this.getApplicationContext(),
                                    IllegalConfigHandler.obtainConfig(
                                            PushPictureActivity.this.getApplicationContext()),
                                    new IllegalMessageHandler.InitIllegalListener() {
                                        @Override
                                        public void onIIllegalFinish() {
                                            HandlerUtil.runOnUIThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    LogUtil.d(TAG, "!instance.isInitHandler");
                                                    PushPictureActivity.this.initDisable(illegalMessageHandler);
                                                }
                                            });
                                        }
                                    });
                            return true;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean value) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtil.e(TAG, "dealInitDisable", throwable);
                        }
                    });
        } else {
            LogUtil.d(TAG, "instance.isInitHandler");
            initDisable(illegalMessageHandler);
        }
    }

    private void initDisable(IllegalMessageHandler illegalMessageHandler) {
        if (illegalMessageHandler.isDisableSend()) {
            illegalMessageHandler.showDisableSendMessageHintDialog(this,
                    new HintIllegalContentDialog.HintClickListener() {
                        @Override
                        public void onAppealClick() {
                        }

                        @Override
                        public void onConfirmClick() {
                            PushPictureActivity.this.startActivity(
                                    new Intent(PushPictureActivity.this, MomentActivity.class));
                            PushPictureActivity.this.finish();
                        }
                    });
        } else {
            init();
        }
    }

    private void init() {
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_push_picture);
        this.context = this;
        initView();
        initData();
        bindPublishService();
        initLoadingPupWindowHolder();
        EventBus.getDefault().register(this);
    }
    private void initOnclickListener() {
        this.push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ClickUtils.isFastClick()) {
                    LogUtil.i(TAG, "click isFastClick");
                    return;
                }
                if (!PushPictureActivity.this.isConnected()) {
                    ToastUtil.showLong(PushPictureActivity.this,
                            PushPictureActivity.this.getString(R.string.net_work_exception));
                    return;
                }
                DigitalManager.getInstance().clearDigitalEntity();
                DigitalManager.getInstance().getDigitalEntity().startPushTime = SystemClock.elapsedRealtime();
                LogUtil.i(TAG, "push onclick clear cache data");
                if (TextUtils.isEmpty(PushPictureActivity.this.photoBean.getShareVideoPath())) {
                    publishPhotoOrText();
                    return;
                }
                LogUtil.i(TAG, "isFromAlbum = " + PushPictureActivity.this.isFromAlbum
                        + " publish video " + PushPictureActivity.this.photoBean.getShareVideoPath());
                if (PushPictureActivity.this.isFunVideo) {
                    PushPictureActivity.this.onFunVideoClick();
                } else {
                    PushPictureActivity.this.chooseVideoItemSuccess(PushPictureActivity.this.isFromAlbum,
                            PushPictureActivity.this.photoBean.getShareVideoPath(),
                            PushPictureActivity.this.shareText.getText().toString());
                }
            }
        });
        setLongClickDeleteItemListener();
    }

    /**
     * 无视频时点击发布：按“多图 / 纯文字 / 纯位置”三种情况分发。
     */
    private void publishPhotoOrText() {
        if (!CollectionUtil.isEmpty(this.adapter.getDataLists())) {
            if (this.adapter != null && this.adapter.getCountSize() > this.ALBUM_MAX_COUNT) {
                ToastUtil.showShort(this.context, String.format(
                        getString(R.string.string_choose_max_pic), this.ALBUM_MAX_COUNT));
                return;
            }
            LogUtil.i(TAG, "publish photo dynamic");
            photoAlbumItemSuccess(this.adapter.getDataLists(), this.shareText.getText().toString(),
                    this.poiBean);
            return;
        }
        String text = this.shareText.getText().toString();
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())) {
            if (this.poiBean != null) {
                DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(2);
                publishMomentLocation();
                return;
            }
            ToastUtil.showLong(this, getString(R.string.string_after_edit));
            return;
        }
        LogUtil.i(TAG, "publish text");
        DigitalManager.getInstance().getDigitalEntity().momentType = String.valueOf(3);
        publishMomentText(this.shareText.getText().toString());
    }

    private void publishMomentLocation() {
        if (this.poiBean == null) {
            return;
        }
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        this.presenter.publishMoment(this.poiBean, this.friendsVisibleBean);
        clearCache();
    }

    private void onFunVideoClick() {
        LogUtil.i(TAG, "onFunVideoClick");
        Bundle bundle = this.videoBundle;
        if (bundle == null) {
            LogUtil.i(TAG, "onFunVideoClick, videoBundle is null");
            getDynamicFunParams();
            return;
        }
        String videoPath = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PATH);
        String funVideoParam = this.videoBundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PARAM);
        String packageName = this.videoBundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_PACK_NAME);
        long videoLength = this.videoBundle.getLong(Constants.ShareVideoKey.FUN_VIDEO_LENGTH, 0L);
        byte[] appIcon = this.videoBundle.getByteArray(Constants.ShareVideoKey.FUN_VIDEO_APP_ICON);
        String appName = this.videoBundle.getString(Constants.ShareVideoKey.FUN_VIDEO_APP_NAME);
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = this.videoBundle.getString("output", null);
        }
        LogUtil.d(TAG, "dealFunVideo videoPath:" + videoPath + videoLength);
        if (isNetValid()) {
            return;
        }
        chooseShareVideoItemSuccess(false, videoPath, packageName, funVideoParam, appIcon, appName,
                videoLength, this.shareText.getText().toString());
    }

    /**
     * 视频 bundle 丢失时，从本地缓存恢复趣味视频参数。
     */
    private void getDynamicFunParams() {
        LogUtil.i(TAG, "getDynamicFunParams");
        FunVideoParms funVideoParms = (FunVideoParms) JSONUtil.fromJSON(
                SaveDynamic.getFunParms(this), FunVideoParms.class);
        if (funVideoParms == null) {
            ToastUtil.showLong(this, getString(R.string.string_anvalid_params));
        } else {
            chooseShareVideoItemSuccess(false, funVideoParms.getVideoPath(),
                    funVideoParms.getVideoPackageName(), funVideoParms.getVideoParm(),
                    funVideoParms.getVideoIcon(), funVideoParms.getVideoAppName(),
                    funVideoParms.getVideoLength(), this.shareText.getText().toString());
        }
    }

    private void setLongClickDeleteItemListener() {
        BaseOverlayPageAdapter pageAdapter = this.adapter;
        if (pageAdapter != null) {
            pageAdapter.setOnClickReport(new BaseOverlayPageAdapter.onClickReport() {
                @Override
                public void report(int position) {
                    PushPictureActivity.this.adapter.showDeleteDialog(PushPictureActivity.this.context,
                            PushPictureActivity.this.dynamicViewPager.getCurrentItem());
                }
            });
            this.adapter.setLongClickRefreshListener(new BaseOverlayPageAdapter.onLongClickListener() {
                @Override
                public void onLongClickRefresh() {
                    LogUtil.i(TAG, "adapter.getCountSize() = " + PushPictureActivity.this.adapter.getCountSize());
                    PushPictureActivity.this.add.setVisibility(
                            PushPictureActivity.this.isPicSizeLimit() ? View.INVISIBLE : View.VISIBLE);
                    PushPictureActivity.this.setAddIconPosition(
                            PushPictureActivity.this.adapter.getCountSize() == 0);
                    PushPictureActivity.this.setCenterViewPager();
                    PushPictureActivity.this.dealCurrentPushBtnBackground();
                }
            });
        }
    }

    private void dealIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            LogUtil.i(TAG, "deal intent error return");
            return;
        }
        if (isNetValid()) {
            LogUtil.i(TAG, "dealIntent nat error");
            return;
        }
        try {
            this.isFromAlbum = intent.getBooleanExtra("isFromAlbum", false);
            this.isFunVideo = intent.getBooleanExtra(IS_FUN_VIDEO, false);
            LogUtil.i(TAG, "isFromAlbum = " + this.isFromAlbum + " isFunVideo = " + this.isFunVideo);
            this.videoBundle = intent.getBundleExtra("video_path");
            LogUtil.i(TAG, "videoBundle = " + this.videoBundle);
            if (this.videoBundle != null) {
                this.add.setVisibility(View.INVISIBLE);
                dealFunVideo(this.videoBundle);
                return;
            }
            Bundle photoBundle = intent.getBundleExtra("photo_path");
            LogUtil.i(TAG, "photoBundle = " + photoBundle);
            if (photoBundle != null) {
                dealAlbumIntent(photoBundle);
                return;
            }
            Bundle cameraBundle = intent.getBundleExtra("picture_photo_path");
            LogUtil.i(TAG, "camaraBundle = " + cameraBundle);
            if (cameraBundle != null) {
                dealCameraIntent(cameraBundle);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "moment Intent Exception", e);
        }
    }

    private void dealCameraIntent(Bundle bundle) {
        String photoPath = bundle.getString("output", null);
        LogUtil.i(TAG, "dealCameraIntent photoPath = " + photoPath);
        if (TextUtils.isEmpty(photoPath)) {
            return;
        }
        setAddIconPosition(false);
        this.dataLists.clear();
        this.dataLists.add(photoPath);
        this.presenter.preCompressImage(this.dataLists, null);
        this.adapter.refreshView(this.dynamicViewPager, this.dataLists);
        if (this.ALBUM_MAX_COUNT == 1) {
            this.add.setVisibility(View.INVISIBLE);
        }
        setCenterViewPager();
    }

    private void setSwipeBack() {
        ((ConstraintLayoutView) findViewById(R.id.layout_root)).setCallBack(new ConstraintLayoutView.CallBack() {
            @Override
            public synchronized void onSwipeBack() {
                LogUtil.i(TAG, "onSwipeBack");
                final String content = PushPictureActivity.this.shareText.getText().toString();
                final ArrayList<String> photoLists = PushPictureActivity.this.adapter.getDataLists();
                if (TextUtils.isEmpty(content) && CollectionUtil.isEmpty(photoLists)
                        && TextUtils.isEmpty(PushPictureActivity.this.photoBean.getShareVideoPath())
                        && PushPictureActivity.this.poiBean == null
                        && PushPictureActivity.this.friendsVisibleBean == null) {
                    LogUtil.i(TAG, "content is null, show the empty notify");
                    PushPictureActivity.this.showEmpryReminder();
                    return;
                }
                PushPictureActivity.this.dialog = DialogUtil.makeDoubleFlatBtnWithTitleDialog(
                        PushPictureActivity.this, new DoubleFlatBtnWithTitleBean(PushPictureActivity.this,
                                true, PushPictureActivity.this.getString(R.string.exit_remind),
                                PushPictureActivity.this.getString(R.string.whether_retain),
                                R.string.not_retain_bug, R.string.retain_but));
                PushPictureActivity.this.dialog.setWholeBackgroud(R.color.color_000000);
                int[] rightBgColors = {R.color.publish_btn_color_start, R.color.publish_btn_color_end};
                DoubleFlatButton bottomBtn = PushPictureActivity.this.dialog.getBottomBtn();
                TextView leftButton = bottomBtn.getLeftButton();
                bottomBtn.setRightBgColorIdArray(rightBgColors);
                leftButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtil.dismissDialog(PushPictureActivity.this.dialog);
                        PushPictureActivity.this.clearCache();
                        PushPictureActivity.this.startActivity(
                                new Intent(PushPictureActivity.this, MomentActivity.class));
                        PushPictureActivity.this.finish();
                    }
                });
                bottomBtn.getRightArea().setOnClickListener(new SaveDraftOnExitClickListener(content, photoLists));
                if (!PushPictureActivity.this.dialog.isShowing()) {
                    DialogUtil.showDialog(PushPictureActivity.this.dialog);
                }
            }
        });
    }

    /**
     * 退出时保存草稿并返回动态主页的点击监听。
     */
    private final class SaveDraftOnExitClickListener implements View.OnClickListener {

        private final String content;
        private final ArrayList<String> photoLists;

        SaveDraftOnExitClickListener(String content, ArrayList<String> photoLists) {
            this.content = content;
            this.photoLists = photoLists;
        }

        @Override
        public void onClick(View view) {
            DialogUtil.dismissDialog(PushPictureActivity.this.dialog);
            String imagesJson = SaveDynamic.parseImages2Json(PushPictureActivity.this.context, this.photoLists);
            if (TextUtils.isEmpty(imagesJson)) {
                imagesJson = "";
            }
            SaveDynamic.savePhotoDynamic(PushPictureActivity.this.context, imagesJson);
            SaveDynamic.saveTextDynamic(PushPictureActivity.this.context,
                    TextUtils.isEmpty(this.content) ? "" : this.content);
            String shareVideoPath = PushPictureActivity.this.photoBean.getShareVideoPath();
            if (TextUtils.isEmpty(shareVideoPath)) {
                shareVideoPath = "";
            }
            SaveDynamic.saveVideoDynamic(PushPictureActivity.this.context, shareVideoPath);
            SaveDynamic.saveIsFromAlbum(PushPictureActivity.this.context,
                    PushPictureActivity.this.isFromAlbum);
            SaveDynamic.saveIsFromFunVideo(PushPictureActivity.this.context,
                    PushPictureActivity.this.isFunVideo);
            SaveDynamic.savePoi(PushPictureActivity.this, PushPictureActivity.this.poiBean);
            SaveDynamic.saveFriendVisible(PushPictureActivity.this,
                    PushPictureActivity.this.friendsVisibleBean);
            PushPictureActivity.this.saveVideoBundle();
            PushPictureActivity.this.startActivity(new Intent(PushPictureActivity.this, MomentActivity.class));
            PushPictureActivity.this.finish();
        }
    }
    /**
     * 恢复上次未完成的草稿或外部跳转带来的位置信息。
     */
    private void recoverLastState() {
        if (getIntent().getBooleanExtra("is_from_outside_jump", false)) {
            String poiJson = getIntent().getStringExtra("lbs_address_city_poi_bean");
            if (!TextUtils.isEmpty(poiJson)) {
                this.poiBean = (PoiBean) JSONUtil.fromJSON(poiJson, PoiBean.class);
            }
            if (TextUtils.isEmpty(this.poiBean.getPoiName())) {
                return;
            }
            this.tvLocation.setText(this.poiBean.getPoiName());
            return;
        }
        if (!SaveDynamic.hasDynamicData(this, "")) {
            LogUtil.i(TAG, "not  exist dynamic data");
            return;
        }
        this.isFromAlbum = SaveDynamic.getIsFromAlbum(this);
        this.isFunVideo = SaveDynamic.getIsFromFunVideo(this);
        String savedText = SaveDynamic.getSaveTextDynamic(this, null);
        String savedVideo = SaveDynamic.getSaveVideoDynamic(this, null);
        ArrayList<String> photoPaths = SaveDynamic.getPhotoPath(this);
        this.poiBean = SaveDynamic.getSavePoi(this);
        this.friendsVisibleBean = SaveDynamic.getSaveFriendVisible(this);
        if (this.poiBean == null) {
            this.tvLocation.setText(R.string.dont_show_location);
        } else {
            this.tvLocation.setText(this.poiBean.getAddressDesc());
        }
        LogUtil.i(TAG, "text = " + savedText + " video = " + savedVideo + " photo = " + photoPaths);
        this.shareText.setText(savedText);
        if (!CollectionUtil.isEmpty(photoPaths)) {
            this.presenter.preCompressImage(photoPaths, null);
            setAddIconPosition(false);
            this.adapter.refreshView(this.dynamicViewPager, photoPaths);
            setCenterViewPager();
            this.add.setVisibility(isPicSizeLimit() ? View.INVISIBLE : View.VISIBLE);
        }
        if (TextUtils.isEmpty(savedVideo)) {
            return;
        }
        this.photoBean.setShareVideoPath(savedVideo);
        this.videoRoot.setVisibility(View.VISIBLE);
        loadVideoPictures(savedVideo);
        this.add.setVisibility(View.INVISIBLE);
        this.dynamicViewPager.setVisibility(View.INVISIBLE);
    }

    @Override
    public PublishPresenter createPresenter() {
        return new PublishPresenter(this);
    }

    private void initLoadingPupWindowHolder() {
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                PushPictureActivity.this.setResult(RESULT_OK);
                PushPictureActivity.this.startActivity(
                        new Intent(PushPictureActivity.this, MomentActivity.class));
                PushPictureActivity.this.finish();
            }
        });
    }

    @Override
    public void initView() {
        this.add = (ConstraintLayout) findViewById(R.id.add);
        this.icon = findViewById(R.id.icon);
        this.shareText = (EditText) findViewById(R.id.et_push_picture_text);
        this.dynamicViewPager = (PointerViewPager) findViewById(R.id.example_vp);
        this.push = (Button) findViewById(R.id.push_picture);
        this.videoRoot = (RelativeLayout) findViewById(R.id.video_clued);
        this.videoImage = (ImageView) this.videoRoot.findViewById(R.id.chat_msg_item_photo_iv);
        this.tvLocation = (TextView) findViewById(R.id.tv_location);
        LinearLayout llLocation = (LinearLayout) findViewById(R.id.ll_location);
        this.rlVisibleRange = (AppRelativeLayout) findViewById(R.id.ll_visible_range);
        this.tvVisibleRange = (TextView) findViewById(R.id.tv_visible_range);
        this.tvVisibleRangeDetails = (TextView) findViewById(R.id.tv_visible_range_details);
        this.ivVisibleRange = (ImageView) findViewById(R.id.iv_visible_range);
        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushPictureActivity.this.startPublishPoiActivity();
            }
        });
        this.rlVisibleRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisibleTypeActivity.startForResult(PushPictureActivity.this,
                        PushPictureActivity.this.friendsVisibleBean, 1);
            }
        });
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false)) {
            llLocation.setVisibility(View.VISIBLE);
        } else {
            ConstraintLayout.LayoutParams layoutParams =
                    (ConstraintLayout.LayoutParams) this.rlVisibleRange.getLayoutParams();
            layoutParams.topToBottom = R.id.et_push_picture_text;
            layoutParams.topMargin = DimenUtil.dp2px(this.context, 118.0f);
            this.rlVisibleRange.setLayoutParams(layoutParams);
        }
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)) {
            this.rlVisibleRange.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.layout_root).requestFocus();
        this.shareText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MaxTextLengthBean.getInstance(this).getMaxLength())});
        this.shareText.addTextChangedListener(new PushWatcher(this,
                MaxTextLengthBean.getInstance(this).getMaxLength(), getString(R.string.content_is_over)));
        this.photoBean = new PhotoBean();
        setAddIconPosition(true);
        SaveDynamic.saveIsMomentPhotoView(this.context, true);
        this.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.i(TAG, "user click add button");
                if (PushPictureActivity.this.adapter.getCountSize() == 0) {
                    LogUtil.i(TAG, "show all button to choose");
                    new ThreeIconDialog(PushPictureActivity.this,
                            PushPictureActivity.this.ALBUM_MAX_COUNT).show();
                } else if (ClickUtils.isFastClick()) {
                    if (PushPictureActivity.this.isPicSizeLimit()) {
                        ToastUtil.showShort(PushPictureActivity.this.context, String.format(
                                PushPictureActivity.this.getString(R.string.string_choose_max_pic),
                                PushPictureActivity.this.ALBUM_MAX_COUNT));
                    } else {
                        LogUtil.i(TAG, "show all photo to choose");
                        PushPictureActivity.this.photoResult();
                    }
                } else {
                    LogUtil.i(TAG, "click too fast");
                }
            }
        });
    }

    private void startPublishPoiActivity() {
        requestRunTimePermission(PermissionStringUtils.LOCATION_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtil.i(TAG, "onGranted llLocation Permission");
                PublishPoiActivity.startForResult(PushPictureActivity.this,
                        PushPictureActivity.this.poiBean);
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: llLocation Permission");
            }
        });
    }

    @Override
    public void initData() {
        this.adapter = new BaseOverlayPageAdapter(this, false);
        this.dynamicViewPager.setAdapter(this.adapter);
        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                String extra = WatchAccountBase.queryModuleSwitchExtraByInt(PushPictureActivity.this,
                        ModuleSwitchConstant.MULTI_TYPE_COMBINED_DYNAMIC, "");
                if (TextUtils.isEmpty(extra)) {
                    return PushPictureActivity.this.ALBUM_MAX_COUNT;
                }
                return ((Integer) JSONUtil.getJSONValue(extra, ALBUM_NUM)).intValue();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer albumMaxCount) {
                        if (albumMaxCount != null) {
                            PushPictureActivity.this.ALBUM_MAX_COUNT = albumMaxCount.intValue();
                        }
                        LogUtil.i(TAG, "ALBUM_MAX_COUNT = " + PushPictureActivity.this.ALBUM_MAX_COUNT);
                        PushPictureActivity.this.dealIntent();
                        PushPictureActivity.this.setSwipeBack();
                        PushPictureActivity.this.recoverLastState();
                        PushPictureActivity.this.dealCurrentPushBtnBackground();
                        PushPictureActivity.this.initOnclickListener();
                        PushPictureActivity.this.setCenterViewPager();
                        MomentVisibleShowUtil.dealVisibleRange(PushPictureActivity.this.context,
                                PushPictureActivity.this.friendsVisibleBean,
                                PushPictureActivity.this.ivVisibleRange,
                                PushPictureActivity.this.tvVisibleRange,
                                PushPictureActivity.this.tvVisibleRangeDetails);
                        PushPictureActivity.this.adapter.setTransformer(
                                PushPictureActivity.this.dynamicViewPager);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "albumNumJson error: ", throwable);
                    }
                });
    }

    private boolean isPicSizeLimit() {
        BaseOverlayPageAdapter pageAdapter = this.adapter;
        return pageAdapter != null && pageAdapter.getCountSize() >= this.ALBUM_MAX_COUNT;
    }

    private void photoResult() {
        NormalIconDialog dialog = DialogUtil.makeThreeIconBtnDialogAll(this,
                new ThreeIconBtnBean(this, false, UiConstants.Color.GRAY, R.drawable.circle_pictures,
                        R.string.picture, UiConstants.Color.GREED, R.drawable.circle_photo,
                        R.string.photo, R.string.cancel_btn_text, new ThreeIconBtnBean.OnClickListener() {
                    @Override
                    public void onLeftBtnClick(Dialog dialogView, View view) {
                        DialogUtil.dismissDialog(dialogView);
                        PushPictureActivity.this.openCamera();
                        MomentBehavior.addDynamicType(PushPictureActivity.this, 1);
                    }

                    @Override
                    public void onRightBtnClick(Dialog dialogView, View view) {
                        LogUtil.d(TAG, "右键 点击--");
                        DialogUtil.dismissDialog(dialogView);
                        PushPictureActivity.this.openAlbum();
                        MomentBehavior.addDynamicType(PushPictureActivity.this, 2);
                    }

                    @Override
                    public void onBottomBtnClick(Dialog dialogView, View view) {
                        LogUtil.d(TAG, "底部按钮 点击--");
                        DialogUtil.dismissDialog(dialogView);
                    }
                }));
        dialog.setWholeBackgroud(R.color.color_000000);
        dialog.setCancelable(false);
        DialogUtil.showDialog(dialog);
    }

    private void dealAlbumIntent(Bundle bundle) {
        ArrayList<String> photoPaths = bundle.getStringArrayList(SELECT_PHOTO_LIST);
        if (CollectionUtil.isEmpty(photoPaths)) {
            LogUtil.i(TAG, "dealAlbumIntent photoPaths is null");
            String photoPath = bundle.getString("output", null);
            LogUtil.i(TAG, "photoPath = " + photoPath);
            if (TextUtils.isEmpty(photoPath)) {
                return;
            }
            ArrayList<String> singlePath = new ArrayList<>();
            singlePath.add(photoPath);
            photoPaths = singlePath;
        }
        int photoType = bundle.getInt(EXTRA_PHOTO_TYP);
        LogUtil.i(TAG, "type = " + photoType);
        if (2 == photoType) {
            ToastUtil.showLong(this, getString(R.string.string_not_support_live));
            finish();
            return;
        }
        if (photoType == 0) {
            setAddIconPosition(false);
            this.dynamicViewPager.setVisibility(View.VISIBLE);
            this.videoRoot.setVisibility(View.INVISIBLE);
            this.presenter.preCompressImage(photoPaths, null);
            this.adapter.refreshView(this.dynamicViewPager, photoPaths);
            if (isPicSizeLimit()) {
                setCenterViewPager();
                this.add.setVisibility(View.INVISIBLE);
            }
            LogUtil.d(TAG, photoType + "  onActivityResult: photoPath = " + photoPaths);
            this.dataLists.addAll(photoPaths);
        }
        if (1 == photoType) {
            this.add.setVisibility(View.INVISIBLE);
            loadVideoPictures(photoPaths.get(0));
            this.photoBean.setShareVideoPath(photoPaths.get(0));
            LogUtil.i(TAG, "选择视频");
            this.photoBean.setVideoPath(photoPaths.get(0));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventPosition(PhotoEvent photoEvent) {
        if (!photoEvent.isDataChange()) {
            LogUtil.i(TAG, "dealOverlayBack, no data change " + photoEvent.getPosition());
            return;
        }
        ArrayList<String> photoLists = photoEvent.getPhotoLists();
        LogUtil.i(TAG, "photoLists = " + photoLists);
        dealPhotoDynamicViewPager(photoLists);
    }

    private void dealPhotoDynamicViewPager(ArrayList<String> photoLists) {
        this.presenter.preCompressImage(photoLists, null);
        this.adapter.setDataLists(photoLists);
        this.dynamicViewPager.setAdapter(this.adapter);
        setLongClickDeleteItemListener();
        this.adapter.setTransformer(this.dynamicViewPager);
        this.adapter.notifyDataSetChanged();
        if (CollectionUtil.isEmpty(photoLists)) {
            LogUtil.i(TAG, "dealOverlayBack, current state is no photo");
            this.add.setVisibility(View.VISIBLE);
            dealCurrentPushBtnBackground();
            setAddIconPosition(true);
            return;
        }
        this.add.setVisibility(photoLists.size() >= this.ALBUM_MAX_COUNT
                ? View.INVISIBLE : View.VISIBLE);
        setAddIconPosition(false);
        setCenterViewPager();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult requestCode = " + requestCode + " resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (isNetValid()) {
            LogUtil.i(TAG, "onActivityResult network error");
            return;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            LogUtil.i(TAG, "onActivityResult bundle is null");
            return;
        }
        switch (requestCode) {
            case 1:
                handleCameraResult(extras);
                break;
            case 2:
                handleAlbumResult(extras);
                break;
            case 3:
            case 4:
                handleFunVideoResult(requestCode, extras);
                break;
            case REQUEST_POI:
                this.poiBean = (PoiBean) data.getParcelableExtra("poi");
                if (this.poiBean == null) {
                    this.tvLocation.setText(R.string.dont_show_location);
                } else {
                    this.tvLocation.setText(this.poiBean.getAddressDesc());
                }
                break;
            case REQUEST_VISIBLE_RANGE:
                this.friendsVisibleBean = (FriendsVisibleBean) data.getParcelableExtra("visible");
                if (this.friendsVisibleBean == null) {
                    this.friendsVisibleBean = new FriendsVisibleBean();
                }
                LogUtil.d(TAG, "friend bean:" + this.friendsVisibleBean);
                MomentVisibleShowUtil.dealVisibleRange(this.context, this.friendsVisibleBean,
                        this.ivVisibleRange, this.tvVisibleRange, this.tvVisibleRangeDetails);
                break;
            default:
                break;
        }
        dealCurrentPushBtnBackground();
    }

    /**
     * 处理拍照返回的图片。
     */
    private void handleCameraResult(Bundle extras) {
        this.isFromAlbum = false;
        this.isFunVideo = false;
        if (isPicSizeLimit()) {
            ToastUtil.showShort(this.context,
                    String.format(getString(R.string.string_choose_max_pic), this.ALBUM_MAX_COUNT));
            return;
        }
        String photoPath = extras.getString("output", null);
        LogUtil.d(TAG, "onActivityResult:拍照  photoPath = " + photoPath);
        ArrayList<String> photoPaths = new ArrayList<>();
        photoPaths.add(photoPath);
        this.presenter.preCompressImage(photoPaths, null);
        this.adapter.addImgUrl(this.dynamicViewPager, photoPath);
        this.dynamicViewPager.setVisibility(View.VISIBLE);
        this.videoRoot.setVisibility(View.INVISIBLE);
        this.videoImage.setVisibility(View.INVISIBLE);
        setAddIconPosition(false);
        this.add.setVisibility(isPicSizeLimit() ? View.INVISIBLE : View.VISIBLE);
        setCenterViewPager();
    }

    /**
     * 处理相册选择返回的图片或视频。
     */
    private void handleAlbumResult(Bundle extras) {
        this.isFromAlbum = true;
        this.isFunVideo = false;
        if (isPicSizeLimit()) {
            ToastUtil.showShort(this.context,
                    String.format(getString(R.string.string_choose_max_pic), this.ALBUM_MAX_COUNT));
            return;
        }
        int photoType = extras.getInt(EXTRA_PHOTO_TYP);
        if (2 == photoType) {
            ToastUtil.showLong(this, getString(R.string.string_not_support_live));
            return;
        }
        ArrayList<String> photoPaths = extras.getStringArrayList(SELECT_PHOTO_LIST);
        LogUtil.i(TAG, "photoPaths = " + photoPaths);
        if (CollectionUtil.isEmpty(photoPaths)) {
            String singlePath = extras.getString("output", null);
            if (TextUtils.isEmpty(singlePath)) {
                LogUtil.i(TAG, "photoPath is null");
                return;
            }
            ArrayList<String> singlePathList = new ArrayList<>();
            singlePathList.add(singlePath);
            photoPaths = singlePathList;
        }
        LogUtil.i(TAG, "type = " + photoType);
        if (photoType == 0) {
            if (photoPaths.get(0).contains("mp4") && !CollectionUtil.isEmpty(this.adapter.getDataLists())) {
                LogUtil.i(TAG, "please choose the photo, current photo size = " + this.dataLists.size());
                ToastUtil.showLong(this, getString(R.string.string_not_support_video));
                return;
            }
            this.dynamicViewPager.setVisibility(View.VISIBLE);
            this.videoRoot.setVisibility(View.INVISIBLE);
            this.videoImage.setVisibility(View.INVISIBLE);
            setAddIconPosition(false);
            this.presenter.preCompressImage(photoPaths, null);
            ArrayList<String> dataList = this.adapter.getDataLists();
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            dataList.addAll(photoPaths);
            this.adapter.refreshView(this.dynamicViewPager, dataList);
            this.add.setVisibility(isPicSizeLimit() ? View.INVISIBLE : View.VISIBLE);
            setCenterViewPager();
        } else if (1 == photoType) {
            if (!CollectionUtil.isEmpty(this.adapter.getDataLists())) {
                LogUtil.i(TAG, "please choose the photo, current photo size = " + this.dataLists.size());
                ToastUtil.showLong(this, getString(R.string.string_not_support_video));
                return;
            }
            String videoPath = extras.getString("output");
            if (TextUtils.isEmpty(videoPath)) {
                videoPath = photoPaths.get(0);
            }
            this.add.setVisibility(View.INVISIBLE);
            if (photoPaths.size() != 0) {
                this.photoBean.setShareVideoPath(videoPath);
                loadVideoPictures(videoPath);
            }
        }
    }

    /**
     * 处理趣味视频/分享视频返回。
     */
    private void handleFunVideoResult(int requestCode, Bundle extras) {
        this.isFunVideo = 4 == requestCode;
        this.videoBundle = extras;
        this.isFromAlbum = false;
        String videoPath = extras.getString(Constants.ShareVideoKey.FUN_VIDEO_PATH);
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = extras.getString("output");
        }
        if (TextUtils.isEmpty(videoPath)) {
            LogUtil.i(TAG, "video path is null");
            return;
        }
        LogUtil.i(TAG, "videoPath = " + videoPath);
        this.add.setVisibility(View.INVISIBLE);
        this.photoBean.setShareVideoPath(videoPath);
        loadVideoPictures(videoPath);
    }

    /**
     * 加载视频缩略图，并在发布服务未就绪时先记住待处理的视频路径。
     */
    private void loadVideoPictures(final String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            LogUtil.i(TAG, "loadVideoPictures photoPath is null");
            return;
        }
        if (this.mPublishBinder == null) {
            this.needPreSaveThumbnailAndToken = true;
            this.mVideoPath = videoPath;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                PushPictureActivity.this.presenter.preSaveThumbnailAndToken(videoPath,
                        PushPictureActivity.this.isFunVideo, PushPictureActivity.this.isFromAlbum,
                        PushPictureActivity.this.mPublishBinder);
            }
        });
        Glide.with(this)
                .load(videoPath)
                .apply(new RequestOptions()
                        .error(R.drawable.ic_selfie_album_default)
                        .placeholder(R.drawable.ic_selfie_album_default)
                        .transform((Transformation<Bitmap>) new GlideRoundTransform(this, 6))
                        .override(this.videoImage.getWidth(), this.videoImage.getHeight())
                        .dontAnimate()
                        .signature(new ObjectKey(Uri.parse(videoPath))))
                .into(this.videoImage);
        LogUtil.i(TAG, "loadVideoPictures photoPath" + videoPath);
        this.videoRoot.setVisibility(View.VISIBLE);
        this.videoImage.setVisibility(View.VISIBLE);
        this.videoRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PushPictureActivity.this.showDeleteDialog(PushPictureActivity.this, videoPath);
                return false;
            }
        });
        this.videoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PushPictureActivity.this, PlayVideoActivity.class);
                intent.putExtra(VIDEO_PHOTO_DATA, videoPath);
                PushPictureActivity.this.startActivity(intent);
            }
        });
    }

    public PublishService.PublishBinder getPublishBinder() {
        return this.mPublishBinder;
    }

    private void bindPublishService() {
        final Intent intent = new Intent(this, PublishService.class);
        intent.setAction(PublishService.ACTION_BIND);
        this.mConnection = new PublishServiceConnection();
        if (AppProcessUtil.isAppProcess(getApplicationContext())) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    PushPictureActivity.this.bindService =
                            PushPictureActivity.this.bindService(intent, PushPictureActivity.this.mConnection,
                                    BIND_AUTO_CREATE);
                }
            });
        }
    }

    /**
     * 发布服务连接：连接成功后补发待处理的视频缩略图预取请求，并注册上传回调。
     */
    private final class PublishServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            LogUtil.d(TAG, "onServiceConnected success");
            PushPictureActivity.this.mPublishBinder = (PublishService.PublishBinder) binder;
            if (PushPictureActivity.this.needPreSaveThumbnailAndToken) {
                PushPictureActivity.this.needPreSaveThumbnailAndToken = false;
                HandlerUtil.runOnBackground(new Runnable() {
                    @Override
                    public void run() {
                        PushPictureActivity.this.presenter.preSaveThumbnailAndToken(
                                PushPictureActivity.this.mVideoPath, PushPictureActivity.this.isFunVideo,
                                PushPictureActivity.this.isFromAlbum, PushPictureActivity.this.mPublishBinder);
                    }
                });
            }
            PushPictureActivity.this.mPublishBinder.getService().setCallback(new PublishService.Callback() {
                @Override
                public void onUploadPhotoSuccess(PhotoMsg photoMsg, VideoTokenVoResponse videoTokenVoResponse) {
                    LogUtil.i(TAG, "onUploadPhotoSuccess");
                    if (photoMsg != null) {
                        photoMsg.setPoiBean(PushPictureActivity.this.poiBean);
                    }
                    if (videoTokenVoResponse != null) {
                        videoTokenVoResponse.setPoiBean(PushPictureActivity.this.poiBean);
                        BehaviorEvent.publishLbs(PushPictureActivity.this);
                    }
                    PushPictureActivity.this.presenter.publishMoment(photoMsg, videoTokenVoResponse,
                            PushPictureActivity.this.friendsVisibleBean);
                }

                @Override
                public void onUploadLivePhotoSuccess(LivePhotoMsg livePhotoMsg) {
                    LogUtil.i(TAG, "上传实况照片成功 LivePhotoMsg：" + livePhotoMsg);
                    PushPictureActivity.this.presenter.publishMoment(livePhotoMsg);
                }

                @Override
                public void onFail(final Throwable throwable) {
                    LogUtil.e(TAG, "onServiceConnected fail, ", throwable);
                    PushPictureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.d(TAG, "publish sensitive photo :" + throwable);
                            if (ERROR_CODE_VIDEO_INVALID.equals(throwable.getMessage())) {
                                PushPictureActivity.this.publishInvalidate();
                            } else {
                                PushPictureActivity.this.publishNormalFial(throwable.getMessage());
                            }
                        }
                    });
                }
            });
        }
    }
    @Override
    public void publishSuccess(DbMoment dbMoment) {
        if (!TextUtils.isEmpty(dbMoment.getLocation()) && !SaveDynamic.hasFirstPublished(this)) {
            SaveDynamic.saveFirstPublished(this);
            HandlerUtil.runOnUIThreadDelay(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PushPictureActivity.this,
                            PushPictureActivity.this.getString(R.string.first_lbs_tip),
                            Toast.LENGTH_SHORT).show();
                }
            }, 1500L);
        }
        String addressId = getIntent().getStringExtra("lbs_from_launcher_address_id");
        if (!TextUtils.isEmpty(addressId)) {
            MomentBehavior.ShareToPicture(this, addressId);
        }
        dismissPublishContentDialog();
        this.loadingPupWindowHolder.showSuccess();
    }

    @Override
    public void chooseAlbumItemSuccess(final PhotoMsg photoMsg) {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                PushPictureActivity.this.loadingPupWindowHolder.showLoading(
                        PushPictureActivity.this.getWindow().getDecorView());
                if (PushPictureActivity.this.isConnected()) {
                    PushPictureActivity.this.presenter.sendPhotoMsg(
                            PushPictureActivity.this.getPublishBinder(), photoMsg);
                    PushPictureActivity.this.clearCache();
                } else {
                    Toast.makeText(PushPictureActivity.this,
                            PushPictureActivity.this.getString(R.string.net_work_exception),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void photoAlbumItemSuccess(ArrayList<String> photoPaths, String content, PoiBean poiBean) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        if (CollectionUtil.isEmpty(photoPaths)) {
            ToastUtil.showLong(this, getString(R.string.publish_fail));
            return;
        }
        if (TextUtils.isEmpty(photoPaths.get(0))) {
            LogUtil.i(TAG, "photoAlbumItem, photoPath is is empty");
            return;
        }
        if (photoPaths.size() == 1 && TextUtils.isEmpty(content) && poiBean == null) {
            this.presenter.sendPhotosMsh(getPublishBinder(), photoPaths, content, poiBean);
            clearCache();
            return;
        }
        this.presenter.sendPhotosMsh(getPublishBinder(), photoPaths, content, poiBean);
        clearCache();
        if (this.adapter.getCountSize() == 1) {
            MomentBehavior.pushDynamic(this, 26, 1);
        } else {
            MomentBehavior.pushDynamic(this, TextUtils.isEmpty(content) ? 28 : 27,
                    this.adapter.getCountSize());
        }
    }

    @Override
    public void chooseAlbumItemSuccess(final LivePhotoMsg livePhotoMsg) {
        View decorView = getWindow().getDecorView();
        if (decorView == null) {
            LogUtil.i(TAG, "chooseAlbumItemSuccess, but window view destroy");
            return;
        }
        decorView.post(new Runnable() {
            @Override
            public void run() {
                if (PushPictureActivity.this.isConnected()) {
                    PushPictureActivity.this.loadingPupWindowHolder.showLoading(
                            PushPictureActivity.this.getWindow().getDecorView());
                    PushPictureActivity.this.presenter.sendLivePhotoMsg(
                            PushPictureActivity.this.getPublishBinder(), livePhotoMsg);
                    PushPictureActivity.this.clearCache();
                } else {
                    Toast.makeText(PushPictureActivity.this,
                            PushPictureActivity.this.getString(R.string.net_work_exception),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void publishMomentText(String content) {
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        this.presenter.publishMoment(content, 3, 3, this.poiBean);
        clearCache();
    }

    @Override
    public void showLoading() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LoadingPupWindowHolder holder = PushPictureActivity.this.loadingPupWindowHolder;
                if (holder != null) {
                    holder.showLoading(PushPictureActivity.this.getWindow().getDecorView());
                }
            }
        });
    }

    @Override
    public void dismissLoading() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                LoadingPupWindowHolder holder = PushPictureActivity.this.loadingPupWindowHolder;
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
        dismissPublishContentDialog();
        this.loadingPupWindowHolder.dismissLoading();
        if (!NetworkUtils.isConnected(this)) {
            isNetValid();
            return;
        }
        // 000061 单独处理，其余（1003/1002、000007 等）走统一映射；同时避免 message 为 null 时崩溃。
        if (!TextUtils.isEmpty(message) && message.contains("000061")) {
            ToastUtil.showShortCover(this, getString(R.string.publish_invalidate));
            return;
        }
        PublishErrorUtil.showFailMessage(this, message);
    }

    @Override
    public void publishLimited() {
        dismissPublishContentDialog();
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_limit));
        LogUtil.d(TAG, "今天发太多了明天再试吧");
    }

    @Override
    public void publishInvalidate() {
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.dismissLoading();
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                HintSensitiveContentDialog dialog = new HintSensitiveContentDialog(PushPictureActivity.this);
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
        LogUtil.d(TAG, "发布内容包含铭感内容");
    }

    public void publishNormalFial(String message) {
        LoadingPupWindowHolder holder = this.loadingPupWindowHolder;
        if (holder != null) {
            holder.dismissLoading();
        }
        PublishErrorUtil.showFailMessage(this, message);
    }

    public void chooseVideoItemSuccess(boolean fromAlbum, String videoPath, final String videoText) {
        LogUtil.i(TAG, "chooseVideoItemSuccess : " + videoText);
        this.presenter.sendVideoMsg(fromAlbum, getPublishBinder(), videoPath, videoText, this.poiBean,
                this.friendsVisibleBean);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(videoText)) {
                    MomentBehavior.pushDynamic(PushPictureActivity.this, 29);
                }
                PushPictureActivity.this.sendVideoRecord();
                PushPictureActivity.this.clearCache();
            }
        });
    }

    private boolean isNetValid() {
        if (NetworkUtils.isConnected(this)) {
            return false;
        }
        ToastUtil.showNoConnected(this);
        return true;
    }

    private void sendVideoRecord() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                PushVideoBean pushVideoBean = new PushVideoBean();
                pushVideoBean.setWatchId(MomentApp.getWatchId());
                pushVideoBean.setTime(String.valueOf(System.currentTimeMillis()));
                pushVideoBean.setType(String.valueOf(1));
                MomentBehavior.sendVideoAndSuccessSend(PushPictureActivity.this, pushVideoBean);
            }
        });
    }

    private void dismissPublishContentDialog() {
        EditableDoubleFlatBtnDialog contentDialog = this.publishContentDialog;
        if (contentDialog != null) {
            contentDialog.dismiss();
            this.publishContentDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        releaseCallBack();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().unregister(PushPictureActivity.this);
                try {
                    if (PushPictureActivity.this.mConnection == null
                            || !PushPictureActivity.this.bindService) {
                        return;
                    }
                    PushPictureActivity.this.unbindService(PushPictureActivity.this.mConnection);
                    PushPictureActivity.this.bindService = false;
                } catch (Exception e) {
                    LogUtil.d(TAG, "unbindService：", e);
                }
            }
        });
        if (this.loadingPupWindowHolder != null) {
            LogUtil.d(TAG, "onDestroy#loadingPupWindowHolder.dismissLoading()");
            this.loadingPupWindowHolder.dismissLoading();
        }
        PublishAgreementDialog protocolDialog = this.showProtocolDialog;
        if (protocolDialog != null && protocolDialog.isShowing()) {
            this.showProtocolDialog.dismiss();
        }
        this.showProtocolDialog = null;
        dismissPublishContentDialog();
        DoubleFlatBtnWithTitleDialog titleDialog = this.dialog;
        if (titleDialog != null) {
            titleDialog.dismiss();
            this.dialog = null;
        }
        super.onDestroy();
    }

    private void dealFunVideo(Bundle bundle) {
        String videoPath = bundle.getString(Constants.ShareVideoKey.FUN_VIDEO_PATH);
        if (TextUtils.isEmpty(videoPath)) {
            videoPath = bundle.getString("output", videoPath);
        }
        if (!TextUtils.isEmpty(videoPath)) {
            LogUtil.d(TAG, "dealFunVideo videoPath:" + videoPath);
            this.photoBean.setShareVideoPath(videoPath);
        }
        loadVideoPictures(videoPath);
    }

    public void chooseShareVideoItemSuccess(boolean fromAlbum, String videoPath, String packageName,
                                            String funVideoParam, byte[] appIcon, String appName,
                                            long videoLength, String videoText) {
        LogUtil.i(TAG, "chooseVideoItemSuccess 分享视频");
        this.loadingPupWindowHolder.showLoading(getWindow().getDecorView());
        this.presenter.sendShareVideoMsg(fromAlbum, getPublishBinder(), videoPath, packageName,
                funVideoParam, appIcon, appName, videoLength, videoText, this.poiBean,
                this.friendsVisibleBean);
        if (!TextUtils.isEmpty(videoText)) {
            MomentBehavior.pushDynamic(this, 29);
        }
        sendVideoRecord();
        clearCache();
        LogUtil.i(TAG, "videoText   " + videoText);
    }

    private void releaseCallBack() {
        PublishService.PublishBinder publishBinder = this.mPublishBinder;
        if (publishBinder == null || publishBinder.getService() == null) {
            return;
        }
        this.mPublishBinder.getService().setCallback(null);
        this.mPublishBinder = null;
    }

    private void showDeleteDialog(final Context dialogContext, String videoPath) {
        this.deleteDialog = DialogUtil.makeDoubleIconBtnDialog(dialogContext,
                new DoubleIconBtnBean(dialogContext, true, UiConstants.Color.GRAY, 0, R.string.cancel,
                        UiConstants.Color.RED, 2, R.string.delete, true,
                        new DoubleIconBtnBean.OnClickListener() {
                            @Override
                            public void onBottomBtnClick(Dialog dialogView, View view) {
                            }

                            @Override
                            public void onLeftBtnClick(Dialog dialogView, View view) {
                                DialogUtil.dismissDialog(dialogView);
                                LogUtil.i(TAG, "onLeftBtnClick");
                            }

                            @Override
                            public void onRightBtnClick(Dialog dialogView, View view) {
                                DialogUtil.dismissDialog(dialogView);
                                SaveDynamic.removeVideo(PushPictureActivity.this.context);
                                Glide.with(dialogContext).clear(PushPictureActivity.this.videoImage);
                                PushPictureActivity.this.videoRoot.setVisibility(View.INVISIBLE);
                                PushPictureActivity.this.add.setVisibility(View.VISIBLE);
                                PushPictureActivity.this.icon.setVisibility(View.VISIBLE);
                                SaveDynamic.saveVideoDynamic(dialogContext, null);
                                PushPictureActivity.this.photoBean.setShareVideoPath(null);
                                PushPictureActivity.this.setAddIconPosition(true);
                                PushPictureActivity.this.dealCurrentPushBtnBackground();
                                LogUtil.i(TAG, "onRightBtnClick saveVideoDynamic");
                            }
                        }));
        DialogUtil.showDialog(this.deleteDialog);
    }

    private void saveVideoBundle() {
        if (this.videoBundle == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                SaveDynamic.saveFunParms(PushPictureActivity.this.context,
                        PushPictureActivity.this.videoBundle);
            }
        });
    }

    public void openCamera() {
        SystemUtil.setEnterFuncFlag();
        Intent captureIntent = new Intent();
        captureIntent.setAction("android.media.action.IMAGE_CAPTURE");
        captureIntent.putExtra(CoreConstants.TakePhotoConstant.LEFT_BUTTON_TEXT, getString(R.string.cancel));
        captureIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, getString(R.string.complete));
        Intent targetIntent = new Intent(this, PushPictureActivity.class);
        targetIntent.putExtra(TARGET_APP, PushPictureActivity.class.getSimpleName());
        targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 1);
        targetIntent.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        captureIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
        startActivityForResult(captureIntent, 1);
    }

    public void openAlbum() {
        SystemUtil.setEnterFuncFlag();
        Intent pickIntent = new Intent();
        pickIntent.setAction("android.intent.action.GET_CONTENT");
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this,
                ModuleSwitchConstant.MODULE_SWITCH_MOMENT_SEND_VIDEO_FUNCTION, false)) {
            pickIntent.setType("file/*");
        } else {
            pickIntent.setType(CoreConstants.TakePhotoConstant.TYPE_IMAGE);
        }
        BaseOverlayPageAdapter pageAdapter = this.adapter;
        pickIntent.putExtra("com.xtc.camera.SELECT_PHOTO_NUM",
                pageAdapter == null ? this.ALBUM_MAX_COUNT
                        : Math.abs(this.ALBUM_MAX_COUNT - pageAdapter.getCountSize()));
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.RIGHT_BUTTON_TEXT, getString(R.string.complete));
        Intent targetIntent = new Intent(this, PushPictureActivity.class);
        targetIntent.putExtra(TARGET_APP, PushPictureActivity.class.getSimpleName());
        targetIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST, 2);
        targetIntent.setFlags(NotificationFlag.NOTIFICATION_FLAG_BOOT_USE);
        pickIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, targetIntent.toURI());
        startActivityForResult(pickIntent, 2);
    }

    public void clearCache() {
        SaveDynamic.removeVideo(this.context);
        SaveDynamic.removeContent(this.context);
        SaveDynamic.removePhoto(this.context);
        SaveDynamic.removePoi(this.context);
        SaveDynamic.removeFriendBean(this.context);
    }

    public boolean isConnected() {
        return NetworkUtils.isConnected(this);
    }

    private void showEmpryReminder() {
        final DoubleFlatBtnWithTitleDialog emptyDialog = DialogUtil.makeDoubleFlatBtnWithTitleDialog(
                this, new DoubleFlatBtnWithTitleBean(this, true,
                        getString(R.string.exit_remind), getString(R.string.exit_redact),
                        R.string.cancel_btn_text, R.string.exit));
        emptyDialog.setWholeBackgroud(R.color.color_000000);
        int[] rightBgColors = {R.color.publish_btn_color_start, R.color.publish_btn_color_end};
        DoubleFlatButton bottomBtn = emptyDialog.getBottomBtn();
        TextView leftButton = bottomBtn.getLeftButton();
        bottomBtn.setRightBgColorIdArray(rightBgColors);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(emptyDialog);
            }
        });
        bottomBtn.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PushPictureActivity.this.clearCache();
                PushPictureActivity.this.startActivity(
                        new Intent(PushPictureActivity.this, MomentActivity.class));
                PushPictureActivity.this.finish();
            }
        });
        if (emptyDialog.isShowing()) {
            return;
        }
        DialogUtil.showDialog(emptyDialog);
    }

    /**
     * 输入长度监听：文本变化时刷新发布按钮背景。
     */
    private class PushWatcher extends MaxLengthWatcher {

        PushWatcher(Context context, int maxLength, String maxLengthHint) {
            super(context, maxLength, maxLengthHint);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            PushPictureActivity.this.dealCurrentPushBtnBackground();
        }
    }

    private void dealCurrentPushBtnBackground() {
        if (!TextUtils.isEmpty(this.shareText.getText().toString())) {
            this.push.setBackgroundResource(R.drawable.bg_btn_release);
            return;
        }
        if (!CollectionUtil.isEmpty(this.adapter.getDataLists())) {
            this.push.setBackgroundResource(R.drawable.bg_btn_release);
            return;
        }
        if (!TextUtils.isEmpty(this.photoBean.getShareVideoPath())
                && this.videoRoot.getVisibility() == View.VISIBLE) {
            this.push.setBackgroundResource(R.drawable.bg_btn_release);
        } else if (this.poiBean != null) {
            this.push.setBackgroundResource(R.drawable.bg_btn_release);
        } else {
            this.push.setBackgroundResource(R.drawable.bg_btn_gray);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtil.i(TAG, "onNewIntent");
        super.onNewIntent(intent);
        dealActivityResult(intent);
    }

    private void dealActivityResult(Intent intent) {
        if (intent == null) {
            return;
        }
        LogUtil.d(TAG, "dealActivityResult: intent = " + intent.toURI());
        String targetApp = intent.getStringExtra(TARGET_APP);
        LogUtil.d(TAG, "dealActivityResult: targetApp = " + targetApp);
        if (TextUtils.isEmpty(targetApp) || !PushPictureActivity.class.getSimpleName().equals(targetApp)) {
            return;
        }
        onActivityResult(intent.getIntExtra(CoreConstants.TakePhotoConstant.REQUEST, -1),
                intent.getIntExtra("RESULT", -1), intent);
    }
}
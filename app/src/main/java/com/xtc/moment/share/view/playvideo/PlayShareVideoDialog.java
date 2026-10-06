package com.xtc.moment.share.view.playvideo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.event.MomentVideoData;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.ChangePlayStateEvent;
import com.xtc.moment.module.widget.DefaultPlayVideoView;
import com.xtc.moment.module.widget.NumTipSeekBar;
import com.xtc.moment.module.widget.PlayVideoTextureView;
import com.xtc.moment.receiver.RecordVideoReceiver;
import com.xtc.moment.share.view.ShareDialogManager;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.ScreenUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.ui.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 分享视频预览弹窗，支持封面缩略图、播放/暂停与进度显示。
 */
public class PlayShareVideoDialog extends Dialog implements View.OnClickListener, IPlayShareVideoView {

    private static final long CANSTOPVIDEOTIME = 10000;
    private static final int COMPLETION_PROGRESS = 100;
    private static final int DELAY_TIME = 2000;
    private static final String TAG = "PlayVideoDialog";

    private final String tag;
    private boolean canReply;
    private ImageView coverIv;
    private DefaultPlayVideoView defaultPlayVideoView;
    private String filePath;
    private boolean hasInit;
    private boolean isCanPauseState;
    private ImageView ivPlayButton;
    private LinearLayout llPlayLoading;
    private AnimationDrawable loadingAnim;
    private ShareDialogManager mShareDialogManager;
    private String name;
    private NumTipSeekBar numTipSeekBar;
    private PlayVideoTextureView plVideoView;
    private PlayShareVideoPresenter presenter;
    private RecordVideoReceiver recordVideoReceiver;
    private View rootView;
    private ImageView thumbnailIv;
    private String thumbnailPath;
    private Handler uiHandler;
    private long videoDuration;
    private View viewLoading;

    public PlayShareVideoDialog(Context context, ShareDialogManager shareDialogManager, String name,
            String thumbnailPath, long videoDuration, String filePath) {
        super(context, R.style.dialog_default_style_forbidSwipe);
        this.tag = TAG;
        this.uiHandler = new Handler();
        this.canReply = false;
        this.hasInit = false;
        this.isCanPauseState = false;
        this.mShareDialogManager = shareDialogManager;
        this.name = name;
        this.filePath = filePath;
        this.thumbnailPath = thumbnailPath;
        this.videoDuration = videoDuration;
        LogUtil.d(TAG, "filePath = " + this.filePath + " thumbnailPath:" + this.thumbnailPath);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_share_video);
        this.presenter = new PlayShareVideoPresenter(getContext(), this.uiHandler);
        this.presenter.attachView(this);
        registerBus();
        registerMomentVideoBroadcastReceiver();
        this.isCanPauseState = false;
        initView();
        LogUtil.i(TAG, "onCreate called.");
    }

    public void initView() {
        this.numTipSeekBar = (NumTipSeekBar) findViewById(R.id.sb_progress);
        this.numTipSeekBar.setEnabled(false);
        this.numTipSeekBar.setSelectProgress(0);
        this.rootView = findViewById(R.id.rl_root);
        this.ivPlayButton = (ImageView) findViewById(R.id.iv_play_button);
        this.llPlayLoading = (LinearLayout) findViewById(R.id.ll_cover);
        this.viewLoading = findViewById(R.id.loading_ll);
        this.loadingAnim = new LoadingAnim(getContext()).createAnim();
        this.ivPlayButton.setVisibility(View.GONE);
        this.rootView.setOnClickListener(this);
        this.rootView.setClickable(false);
        this.ivPlayButton.setOnClickListener(this);
        this.ivPlayButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    ivPlayButton.setImageAlpha(255);
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ivPlayButton.setImageAlpha(204);
                }
                return false;
            }
        });
        this.thumbnailIv = (ImageView) findViewById(R.id.bg_iv);
        this.coverIv = (ImageView) findViewById(R.id.cover_iv);
        new RequestOptions().placeholder(R.drawable.ic_selfie_album_default).centerCrop()
                .signature(new ObjectKey(String.valueOf(Math.random())));
        this.plVideoView = (PlayVideoTextureView) findViewById(R.id.video_view);
        this.plVideoView.setOnClickListener(this);
        TextView titleView = (TextView) findViewById(R.id.tv_title);
        TextView durationView = (TextView) findViewById(R.id.share_video_duration);
        DecimalFormat decimalFormat = new DecimalFormat(Constants.Share.VIDEO_DURATION_PATTERN);
        long minutes = this.videoDuration / 60000;
        double duration = this.videoDuration;
        double minutesInMillis = 60 * minutes;
        durationView.setText(String.format(getContext().getString(R.string.dialog_share_video_duration),
                decimalFormat.format(minutes),
                decimalFormat.format(Math.round((duration / 1000.0d) - minutesInMillis))));
        titleView.setText(this.name);
        loadPreviewVideo(this.thumbnailPath, this.coverIv, this.filePath);
        initListener();
        setIsPlayingState();
        this.plVideoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                defaultPlayVideoView = new DefaultPlayVideoView(getContext(), plVideoView, surfaceTexture,
                        new Surface(surfaceTexture));
                starLoading();
                presenter.playVideo(filePath, defaultPlayVideoView);
            }
        });
        LogUtil.i(TAG, "playVideo filePath:" + this.filePath);
        LogUtil.i(TAG, "playVideo thumbnailPath:" + this.thumbnailPath);
    }

    private void loadPreviewVideo(String thumbnailPath, ImageView imageView, String videoPath) {
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA);
        if (TextUtils.isEmpty(thumbnailPath)) {
            if (TextUtils.isEmpty(videoPath)) {
                LogUtil.w(TAG, "loadPreviewImage but videoPath is null return");
                return;
            }
            showCreateBitmap(videoPath, options, imageView);
            return;
        }
        Glide.with(getContext()).load(thumbnailPath).apply(options).into(imageView);
    }

    private void showCreateBitmap(final String videoPath, final RequestOptions options, final ImageView imageView) {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return createBitmap(videoPath);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        if (bitmap == null) {
                            LogUtil.w(TAG, "loadPreviewImage but thumbnail is null return");
                        } else {
                            Glide.with(getContext()).load(bitmap).apply(options).into(imageView);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "loadPreviewImage but error = " + throwable.getMessage());
                    }
                });
    }

    protected Bitmap createBitmap(String videoPath) {
        LogUtil.d(TAG, "create bitmap");
        Bitmap videoThumbnail = ChatVideoUtil.getVideoThumbnail(videoPath);
        if (videoThumbnail == null || videoThumbnail.isRecycled()) {
            LogUtil.w(TAG, "loadPreviewImage bitmap is null or bitmap is recycled");
            return null;
        }
        int width = videoThumbnail.getWidth();
        int height = videoThumbnail.getHeight();
        float scale = ImageUtil.calculateInSampleSize(videoThumbnail.getWidth(), videoThumbnail.getHeight(),
                ScreenUtils.screenWidth(getContext()), ScreenUtils.screenHeight(getContext()));
        int targetWidth = (int) (width / scale);
        int targetHeight = (int) (height / scale);
        LogUtil.d(TAG, "max scale = " + scale + ", width = " + width + ", height = " + height);
        return scale > 0.0f ? ImageUtils.scaleBitmap(videoThumbnail, targetWidth, targetHeight) : videoThumbnail;
    }

    private void initListener() {
        DoubleFlatButton doubleFlatButton = (DoubleFlatButton) findViewById(R.id.dfb_share);
        doubleFlatButton.setLeftBgColorIdArray(new int[]{R.color.color_3f4d61, R.color.color_222c3b});
        doubleFlatButton.setRightBgColorIdArray(new int[]{R.color.color_2bdbff, R.color.color_1794fa});
        TextView leftButton = doubleFlatButton.getLeftButton();
        leftButton.setText(R.string.cancel);
        doubleFlatButton.getRightButton().setText(R.string.send);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareDialogManager != null) {
                    mShareDialogManager.shareCallback.cancelShare();
                }
                if (isShowing()) {
                    cancel();
                }
            }
        });
        doubleFlatButton.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareDialogManager == null) {
                    return;
                }
                if (!NetworkUtils.isConnected(MomentApp.getAppContext())) {
                    ToastUtil.showShort(MomentApp.getAppContext(), R.string.net_work_exception);
                } else if (!MomentApp.isSendingVideo()) {
                    mShareDialogManager.showLoadingDial();
                    mShareDialogManager.shareCallback.sendShare();
                } else {
                    ToastUtil.showShort(MomentApp.getAppContext(), R.string.sending_video_tip);
                }
            }
        });
    }

    public void starLoading() {
        this.llPlayLoading.setVisibility(View.VISIBLE);
        this.viewLoading.setBackground(this.loadingAnim);
        this.loadingAnim.start();
    }

    public void stopLoading() {
        this.llPlayLoading.setVisibility(View.GONE);
        AnimationDrawable animationDrawable = this.loadingAnim;
        if (animationDrawable == null || !animationDrawable.isRunning()) {
            return;
        }
        this.loadingAnim.stop();
    }

    private void setIsPlayingState() {
        HandlerUtil.runOnUIThreadDelay(new Runnable() {
            @Override
            public void run() {
                isCanPauseState = true;
            }
        }, CANSTOPVIDEOTIME);
    }

    private void registerBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterBus() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoEvent(MomentVideoData momentVideoData) {
        LogUtil.i(TAG, "play_video page receive momentVideoData:" + momentVideoData);
        if (momentVideoData == null) {
            return;
        }
        if (momentVideoData.getAction() == 1) {
            dismiss();
        } else if (momentVideoData.getAction() == 2) {
            this.presenter.pausePlayVideo();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!this.hasInit && hasFocus) {
            this.hasInit = true;
        }
        LogUtil.d(TAG, "hasFocus = " + hasFocus + " onWindowFocusChanged:");
        if (hasFocus) {
            return;
        }
        this.presenter.pausePlayVideo();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        long startTime = System.currentTimeMillis();
        this.presenter.stopPlayVideo();
        LogUtil.d(TAG, "stopPlayVideo" + (System.currentTimeMillis() - startTime));
        this.presenter.destroyVideoView();
        unregisterBus();
        this.uiHandler.removeCallbacksAndMessages(null);
        unRegisterMomentVideoBroadcastReceiver();
        stopLoading();
        LogUtil.i(TAG, "onDetachedFromWindow");
    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.presenter.dealScreenOff();
        this.presenter.pausePlayVideo();
        LogUtil.i(TAG, "dismiss");
    }

    @Override
    public void playVideo() {
        ChatVideoUtil.keepScreenOn(getWindow());
        this.thumbnailIv.setVisibility(View.GONE);
        if (this.ivPlayButton != null) {
            this.ivPlayButton.setVisibility(View.GONE);
        }
        this.numTipSeekBar.setSelectProgress(0);
    }

    @Override
    public void playVideoComplete() {
        ChatVideoUtil.cancelKeepScreenOn(getWindow());
        int selectProgress = this.numTipSeekBar.getSelectProgress();
        if (selectProgress < COMPLETION_PROGRESS) {
            this.numTipSeekBar.setSelectProgress(COMPLETION_PROGRESS);
            LogUtil.d(TAG, "playVideoComplete: selectProgress = " + selectProgress);
            this.uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    numTipSeekBar.setVisibility(View.GONE);
                    numTipSeekBar.setSelectProgress(0);
                }
            }, 200L);
        } else {
            this.numTipSeekBar.setVisibility(View.GONE);
            this.numTipSeekBar.setSelectProgress(0);
        }
        if (this.canReply) {
            this.ivPlayButton.setVisibility(View.GONE);
            this.presenter.playVideo(this.filePath, this.defaultPlayVideoView);
        } else {
            this.ivPlayButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void pausePlayVideo() {
        ChatVideoUtil.cancelKeepScreenOn(getWindow());
        if (this.canReply) {
            this.ivPlayButton.setVisibility(View.GONE);
        } else {
            this.ivPlayButton.setVisibility(View.VISIBLE);
        }
        LogUtil.i(TAG, "pausePlayVideo");
    }

    @Override
    public void dealScreenOff(Bitmap bitmap) {
        if (bitmap != null) {
            this.thumbnailIv.setVisibility(View.VISIBLE);
            this.thumbnailIv.setImageBitmap(bitmap);
            LogUtil.i(TAG, "thumbnailIv setImageBitmap currentFrameBitmap");
            return;
        }
        LogUtil.i(TAG, "dealScreenOff but currentFrameBitmap is null!");
    }

    @Override
    public void continuePlayVideo(boolean clickScreenOn) {
        LogUtil.d(TAG, "continuePlayVideo: clickScreenOn = " + clickScreenOn + ";coverIv = "
                + (this.coverIv.getVisibility() == View.VISIBLE));
        ChatVideoUtil.keepScreenOn(getWindow());
        this.coverIv.setVisibility(View.INVISIBLE);
        this.thumbnailIv.setBackground(null);
        this.ivPlayButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bufferComplete(String path) {
        this.filePath = path;
    }

    @Override
    public void bufferCompleteThumbnailPath(String path) {
        this.thumbnailPath = path;
    }

    @Override
    public void canPauseVideo() {
        this.isCanPauseState = true;
        this.ivPlayButton.setVisibility(View.INVISIBLE);
        stopLoading();
    }

    @Override
    public void onClick(View view) {
        if (!this.isCanPauseState) {
            LogUtil.i(TAG, "onClick click is invalid");
            return;
        }
        this.numTipSeekBar.setVisibility(View.VISIBLE);
        if (view.getId() == R.id.video_view) {
            if (this.coverIv.getVisibility() != View.VISIBLE) {
                this.presenter.clickVideoView();
            }
        } else if (view.getId() == R.id.iv_play_button) {
            this.presenter.replayOrContinutePlay();
            this.ivPlayButton.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void changePlayState(ChangePlayStateEvent changePlayStateEvent) {
        LogUtil.w(TAG, "changePlayState: " + changePlayStateEvent);
        if (changePlayStateEvent.getPlayState() == 1) {
            this.presenter.replayOrContinutePlay();
            this.ivPlayButton.setVisibility(View.GONE);
        } else if (changePlayStateEvent.getPlayState() == 2 && this.coverIv.getVisibility() != View.VISIBLE) {
            this.presenter.pausePlayVideo();
        }
    }

    @Override
    public void updatePlayingProgress(int progress) {
        if (this.numTipSeekBar == null) {
            return;
        }
        if (progress > 0 && this.coverIv.getVisibility() == View.VISIBLE) {
            this.coverIv.setVisibility(View.INVISIBLE);
        }
        if (progress > 0 && this.numTipSeekBar.getVisibility() != View.VISIBLE) {
            this.numTipSeekBar.setVisibility(View.VISIBLE);
        }
        if (this.isCanPauseState && this.ivPlayButton.getVisibility() == View.VISIBLE) {
            this.ivPlayButton.setVisibility(View.INVISIBLE);
        }
        this.numTipSeekBar.setSelectProgress(progress);
    }

    @Override
    public void startToRecordActivity() {
        dismiss();
    }

    @Override
    public void beginRenderFirstFrame(boolean clickScreenOn) {
        LogUtil.i(TAG, "clickScreenOn:" + clickScreenOn);
        if (!this.rootView.isClickable()) {
            this.rootView.setClickable(true);
        }
        this.uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "run: coverIv = " + (coverIv.getVisibility() == View.VISIBLE));
                coverIv.setVisibility(View.GONE);
            }
        }, 100L);
    }

    @Override
    public void replayVideo(boolean clickScreenOn) {
        LogUtil.i(TAG, "clickScreenOn:" + clickScreenOn + " filePath:" + this.filePath);
        this.numTipSeekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void playVideoFailed(boolean isFirstPlayFail) {
        LogUtil.d(TAG, "play video failed! isFirstPlayFail = " + isFirstPlayFail);
        if (getContext() == null) {
            return;
        }
        if (!NetworkUtils.isConnected(getContext())) {
            ToastUtil.showNoConnected(getContext());
            return;
        }
        stopLoading();
        this.rootView.setClickable(false);
        ToastUtil.showShort(getContext(), getContext().getString(R.string.video_play_failed));
        this.uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isShowing()) {
                    dismiss();
                }
            }
        }, com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
    }

    public void registerMomentVideoBroadcastReceiver() {
        this.recordVideoReceiver = new RecordVideoReceiver();
        getContext().registerReceiver(this.recordVideoReceiver, RecordVideoReceiver.getWeichatCommonFilter());
    }

    public void unRegisterMomentVideoBroadcastReceiver() {
        if (this.recordVideoReceiver != null) {
            getContext().unregisterReceiver(this.recordVideoReceiver);
            this.recordVideoReceiver = null;
        }
    }

    public PlayShareVideoPresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void bufferOnlineVideo(int progress) {
    }

    @Override
    public void enableToPreview() {
    }
}
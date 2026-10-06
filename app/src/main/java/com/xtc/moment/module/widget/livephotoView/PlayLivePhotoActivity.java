package com.xtc.moment.module.widget.livephotoView;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;

/**
 * Full screen player for a live photo: pre-plays the clip, then plays it while the finger is down.
 */
public class PlayLivePhotoActivity extends BaseActivity<IPlayLivePhotoView, PlayLivePhotoPresenter>
        implements IPlayLivePhotoView {

    public static final String VIDEO_FILE_PATH = "video_file_path";
    public static final String VIDEO_THUMNAIL_PATH = "video_thumnail_path";
    public static final String VIDEO_HAS_DOWNLOAD = "video_has_download";
    public static final String VIDEO_IS_SEND = "video_is_send";
    public static final String VIDEO_CAN_REPLY = "video_can_reply";
    public static final String VIDEO_VALID = "video_valid";
    public static final String VIDEO_OUT_PATH = "outPath";

    private static final String TAG = PlayLivePhotoActivity.class.getSimpleName();

    /** Milliseconds the device vibrates when the live photo starts. */
    private static final long VIBRATOR_TIME = 60L;

    private String filePath;
    private String thumnailPath;
    private String outPath;
    private boolean hasVideoDownload;

    private PlayVideoTextureView plVideoTextureView;
    private PhotoView ivVideoMask;
    private LottieAnimationView ivLivePhoto;
    private LinearLayout llPlayLoading;
    private View viewLoading;
    private AnimationDrawable loadingAnim;
    private LivePhotoVideoView livePhotoVideoView;
    private Vibrator vibrator;

    /** Loads the thumbnail into the mask view. */
    private final SimpleTarget<Bitmap> simpleTarget = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
            ivVideoMask.setScaleType(ImageView.ScaleType.FIT_XY);
            ivVideoMask.setImageBitmap(resource);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_live_photo);
        initView();
        initData();
        setVideoMask();
    }

    @Override
    public PlayLivePhotoPresenter createPresenter() {
        return new PlayLivePhotoPresenter(this);
    }

    @Override
    public void initView() {
        this.plVideoTextureView = (PlayVideoTextureView) findViewById(R.id.tt_video_player);
        this.ivVideoMask = (PhotoView) findViewById(R.id.iv_video_first_mask);
        this.ivLivePhoto = (LottieAnimationView) findViewById(R.id.iv_live_photo);
        this.llPlayLoading = (LinearLayout) findViewById(R.id.ll_cover);
        this.viewLoading = findViewById(R.id.loading_ll);
        this.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        this.plVideoTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }

            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                livePhotoVideoView = new LivePhotoVideoView(PlayLivePhotoActivity.this,
                        plVideoTextureView, surface, new Surface(surface));
                if (hasVideoDownload) {
                    prePlayVideo();
                }
            }
        });

        this.ivLivePhoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    LogUtil.i(TAG, "开始播放实况照片");
                    startVibrator();
                    ivLivePhoto.playAnimation();
                    playLivePhoto();
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    ivLivePhoto.cancelAnimation();
                    ivLivePhoto.setProgress(0.0f);
                    stopLivePhoto();
                    LogUtil.i(TAG, "停止播放实况照片");
                }
                return true;
            }
        });

        this.ivVideoMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void startVibrator() {
        Vibrator localVibrator = this.vibrator;
        if (localVibrator == null) {
            return;
        }
        localVibrator.vibrate(VIBRATOR_TIME);
    }

    @Override
    public void initData() {
        this.filePath = getIntent().getStringExtra(VIDEO_FILE_PATH);
        this.thumnailPath = getIntent().getStringExtra(VIDEO_THUMNAIL_PATH);
        this.hasVideoDownload = getIntent().getBooleanExtra(VIDEO_HAS_DOWNLOAD, false);
        this.outPath = getIntent().getStringExtra(VIDEO_OUT_PATH);
        LogUtil.i(TAG, "filePath: " + this.filePath + " thumnailPath:" + this.thumnailPath
                + "  hasVideoDownload:" + this.hasVideoDownload + "  outPath:" + this.outPath);
        dealNeedDownLoadVideo();
    }

    private void dealNeedDownLoadVideo() {
        if (this.hasVideoDownload) {
            return;
        }
        this.ivLivePhoto.setVisibility(View.GONE);
        this.llPlayLoading.setVisibility(View.VISIBLE);
        this.loadingAnim = new LoadingAnim(this).createAnim();
        this.viewLoading.setBackground(this.loadingAnim);
        this.loadingAnim.start();
        this.presenter.startDownload(this.filePath, this.outPath);
    }

    private void setVideoMask() {
        if (isDestroyed()) {
            return;
        }
        Glide.with(this).asBitmap().load(this.thumnailPath)
                .apply(new RequestOptions().error(R.drawable.ic_picture_placeholder))
                .into(this.simpleTarget);
    }

    private void hideVideoMask() {
        if (!this.livePhotoVideoView.isPlaying()) {
            LogUtil.i(TAG, "hideVideoMask but now video not playing return");
            return;
        }
        this.ivVideoMask.setVisibility(View.GONE);
    }

    private void showVideoMask() {
        if (this.livePhotoVideoView.isPlaying()) {
            LogUtil.i(TAG, "showVideoMask but now video playing return");
            return;
        }
        this.ivVideoMask.setVisibility(View.VISIBLE);
    }

    private void playLivePhoto() {
        this.livePhotoVideoView.playVideo(this.filePath, true, new IVideoView.OnPlayVideoListener() {
            @Override
            public void onCompletion() {
            }

            @Override
            public void beginRenderFirstFrame() {
                MainHandlerUtil.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        hideVideoMask();
                        plVideoTextureView.setVisibility(View.VISIBLE);
                        LivePhotoDelayTimeUtil.resetPlayMaskTime();
                    }
                }, LivePhotoDelayTimeUtil.getDelayPlayMaskTime());
            }
        }, null);
    }

    private void stopLivePhoto() {
        LivePhotoVideoView view = this.livePhotoVideoView;
        if (view != null) {
            view.stopVideo();
        }
        showVideoMask();
    }

    private void prePlayVideo() {
        LivePhotoVideoView view = this.livePhotoVideoView;
        if (view == null) {
            LogUtil.i(TAG, "livePhotoVideoView == null");
            return;
        }
        view.prePlayVideo(this.filePath, new IVideoView.OnPlayVideoListener() {
            @Override
            public void beginRenderFirstFrame() {
                MainHandlerUtil.postDelay(new Runnable() {
                    @Override
                    public void run() {
                        plVideoTextureView.setVisibility(View.VISIBLE);
                        hideVideoMask();
                    }
                }, LivePhotoDelayTimeUtil.getDelayPrePlayMaskTime());
            }

            @Override
            public void onCompletion() {
                showVideoMask();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LivePhotoDelayTimeUtil.lengthenPlayMaskTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.livePhotoVideoView.stopVideo();
        this.livePhotoVideoView.destroy();
        AnimationDrawable anim = this.loadingAnim;
        if (anim == null || !anim.isRunning()) {
            return;
        }
        this.loadingAnim.stop();
    }

    @Override
    public void downLoadSuccess(String path) {
        LogUtil.i(TAG, "downLoadSuccess  filePath:" + path);
        this.ivLivePhoto.setVisibility(View.VISIBLE);
        this.llPlayLoading.setVisibility(View.GONE);
        this.filePath = path;
        AnimationDrawable anim = this.loadingAnim;
        if (anim != null && anim.isRunning()) {
            this.loadingAnim.stop();
        }
        prePlayVideo();
    }

    @Override
    public void downLoadError(String message) {
        LogUtil.i(TAG, "downLoadError  filePath:" + message);
        this.llPlayLoading.setVisibility(View.GONE);
        this.ivLivePhoto.setVisibility(View.GONE);
        AnimationDrawable anim = this.loadingAnim;
        if (anim != null && anim.isRunning()) {
            this.loadingAnim.stop();
        }
        ToastUtil.showShort(this, getResources().getString(R.string.net_error));
    }
}
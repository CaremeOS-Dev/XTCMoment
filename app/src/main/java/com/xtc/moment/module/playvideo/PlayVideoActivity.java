package com.xtc.moment.module.playvideo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.event.MomentVideoData;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.barrage.BarrageFragment;
import com.xtc.moment.module.bean.ChangePlayStateEvent;
import com.xtc.moment.module.bean.MultiPhotoContent;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoPlayCompletelyEvent;
import com.xtc.moment.module.publish.multi.PushPictureActivity;
import com.xtc.moment.module.widget.BaseMomentActivity;
import com.xtc.moment.module.widget.DefaultPlayVideoView;
import com.xtc.moment.module.widget.NumTipSeekBar;
import com.xtc.moment.module.widget.PlayVideoTextureView;
import com.xtc.moment.receiver.RecordVideoReceiver;
import com.xtc.moment.third.bean.PushVideoBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BroadcastReceiverUtil;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.system.NetworkUtils;
import com.xtc.utils.system.SystemPropertyUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PlayVideoActivity extends BaseMomentActivity<IPlayVideoView, PlayVideoPresenter>
        implements View.OnClickListener, IPlayVideoView {

    private static final String TAG = "PlayVideoActivity";
    private static final long CANSTOPVIDEOTIME = 10000;
    private static final int COMPLETION_PROGRESS = 100;
    private static final int DELAY_TIME = 2000;
    private static final String PROPERTY_UNLOCK_ATTACH = "sys.unlock.attach";
    private static final String TRANS_ = "trans_";

    public static final String HAS_VIDEO_TOKEN_OR_KEY = "has_video_token_or_key";
    public static final String VIDEO_CAN_REPLY = "video_can_reply";
    public static final String VIDEO_FILE_PATH = "video_file_path";
    public static final String VIDEO_HAS_DOWNLOAD = "video_has_download";
    public static final String VIDEO_IS_SEND = "video_is_send";
    public static final String VIDEO_MSG_DATA = "video_msg_data";
    public static final String VIDEO_MSG_DATA_KEY = "video_msg_data_key";
    public static final String VIDEO_MSG_ID = "msg_id";
    public static final String VIDEO_THUMNAIL_PATH = "video_thumnail_path";
    public static final String VIDEO_VALID = "video_valid";

    private final Handler uiHandler = new Handler();

    private Intent intent;
    private DbMoment dbMoment;
    private VideoMsg videoMsg;
    private VideoKeyOrToken videoKeyOrToken;
    private String filePath;
    private String thumbnailPath;
    private View rootView;
    private ImageView ivPlayButton;
    private LinearLayout llPlayLoading;
    private View viewLoading;
    private AnimationDrawable loadingAnim;
    private NumTipSeekBar numTipSeekBar;
    private ImageView thumbnailIv;
    private ImageView coverIv;
    private PlayVideoTextureView plVideoView;
    private DefaultPlayVideoView defaultPlayVideoView;
    private BarrageFragment barrageFragment;
    private RecordVideoReceiver recordVideoReceiver;

    private boolean canReply = false;
    private boolean hasInit = false;
    private boolean isAddFragment = false;
    private boolean isCanPauseState = false;
    private boolean isStartFirstPlay = false;
    private boolean isFunVideoOrPointVideo = false;

    @Override
    public void bufferOnlineVideo(int progress) {
    }

    @Override
    public void enableToPreview() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_play_video);
        intent = getIntent();
        registerBus();
        registerMomentVideoBroadcastReceiver();
        isCanPauseState = false;
        initView();
        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() {
                initData();
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object value) {
                startFirstPlay();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(TAG, "initData error: ", throwable);
            }
        });
        LogUtil.i(TAG, "onCreate called.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoPlayCompletelyEvent(VideoPlayCompletelyEvent event) {
        if (MomentTypeUtil.isOfficialType(dbMoment.getType().intValue()) && event.isVideoFinish()) {
            PushVideoBean bean = new PushVideoBean();
            bean.setWatchId(MomentApp.getWatchId());
            bean.setMomentId(dbMoment.getMomentId());
            MomentBehavior.advVideoPlayCompletely(this, bean);
        }
    }

    public void initData() {
        String momentJson = intent.getStringExtra(VIDEO_MSG_DATA);
        boolean hasTokenOrKey = intent.getBooleanExtra(HAS_VIDEO_TOKEN_OR_KEY, true);
        filePath = intent.getStringExtra(PushPictureActivity.VIDEO_PHOTO_DATA);
        LogUtil.i(TAG, "initData");
        if (TextUtils.isEmpty(momentJson)) {
            return;
        }
        LogUtil.i(TAG, "play moment data = " + momentJson + ", hasVideoTokenOrKey: " + hasTokenOrKey);
        dbMoment = JSONUtil.fromJSON(momentJson, DbMoment.class);
        if (dbMoment == null) {
            return;
        }
        if (dbMoment.getType().intValue() == 27) {
            if (!TextUtils.isEmpty(dbMoment.getPublishContent())) {
                videoMsg = JSONUtil.fromJSON(dbMoment.getPublishContent(), VideoMsg.class);
            } else {
                MultiPhotoContent multiPhotoContent = JSONUtil.fromJSON(dbMoment.getContent(), MultiPhotoContent.class);
                if (multiPhotoContent == null) {
                    return;
                }
                videoMsg = JSONUtil.fromJSON(multiPhotoContent.getVideoMsgContent(), VideoMsg.class);
            }
            LogUtil.d(TAG, "this content video VideoMsg: " + videoMsg);
        } else {
            videoMsg = JSONUtil.fromJSON(dbMoment.getContent(), VideoMsg.class);
            LogUtil.d(TAG, "this VideoMsg: " + videoMsg);
        }
        if (MomentTypeUtil.isShareVideo(dbMoment.getType().intValue())) {
            ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(dbMoment.getContent(), ShareVideoMoment.class);
            LogUtil.d(TAG, "initData: " + shareVideoMoment);
            if (shareVideoMoment != null && shareVideoMoment.getFunVideoParam() != null
                    && MomentTypeUtil.isFunVideoOrPointVideo(shareVideoMoment.getFunVideoParam().getModelType())) {
                isFunVideoOrPointVideo = true;
            }
        }
        if (hasTokenOrKey) {
            videoKeyOrToken = JSONUtil.fromJSON(dbMoment.getResource(), VideoKeyOrToken.class);
        }
        LogUtil.i(TAG, "play moment videoKeyOrToken= " + videoKeyOrToken + ", videoMsg: " + videoMsg);
        if (videoMsg != null && filePath == null) {
            filePath = videoMsg.getLocalVideoPath();
            thumbnailPath = videoMsg.getLocalThumbnailPath();
        }
        PushVideoBean bean = new PushVideoBean();
        bean.setWatchId(MomentApp.getWatchId());
        bean.setType(String.valueOf(System.currentTimeMillis()));
        bean.setMomentId(dbMoment.getMomentId());
        MomentBehavior.videoPlayCount(this, bean);
        LogUtil.d(TAG, "filePath = " + filePath + " thumbnailPath:" + thumbnailPath);
    }

    public void initView() {
        LogUtil.d(TAG, "initView");
        numTipSeekBar = (NumTipSeekBar) findViewById(R.id.sb_progress);
        numTipSeekBar.setEnabled(false);
        numTipSeekBar.setSelectProgress(0);
        rootView = findViewById(R.id.rl_root);
        ivPlayButton = (ImageView) findViewById(R.id.iv_play_button);
        llPlayLoading = (LinearLayout) findViewById(R.id.ll_cover);
        viewLoading = findViewById(R.id.loading_ll);
        loadingAnim = new LoadingAnim(this).createAnim();
        ivPlayButton.setVisibility(View.GONE);
        rootView.setOnClickListener(this);
        rootView.setClickable(false);
        ivPlayButton.setOnClickListener(this);
        ivPlayButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ivPlayButton.setImageAlpha(255);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ivPlayButton.setImageAlpha(204);
                }
                return false;
            }
        });
        thumbnailIv = (ImageView) findViewById(R.id.bg_iv);
        coverIv = (ImageView) findViewById(R.id.cover_iv);
        plVideoView = (PlayVideoTextureView) findViewById(R.id.video_view);
        plVideoView.setOnClickListener(this);
        plVideoView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
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
                LogUtil.d(TAG, "onSurfaceTextureAvailable");
                defaultPlayVideoView = new DefaultPlayVideoView(PlayVideoActivity.this, plVideoView, surfaceTexture,
                        new Surface(surfaceTexture));
                startFirstPlay();
            }
        });
    }

    public void startFirstPlay() {
        if (isStartFirstPlay) {
            return;
        }
        if (defaultPlayVideoView == null) {
            LogUtil.d(TAG, "defaultPlayVideoView == null");
            return;
        }
        if (TextUtils.isEmpty(filePath) && videoMsg == null && videoKeyOrToken == null) {
            LogUtil.d(TAG, "startFirstPlay wait init");
            return;
        }
        isStartFirstPlay = true;
        LogUtil.d(TAG, "startFirstPlay");
        isCanPauseState = true;
        ((PlayVideoPresenter) presenter).registerBatteryChange(getApplicationContext());
        if (!SystemPropertyUtil.getBooleanLegacy(PROPERTY_UNLOCK_ATTACH, false)) {
            boolean needLoadUrl = true;
            if (!TextUtils.isEmpty(filePath) && FileUtils.exists(filePath)) {
                needLoadUrl = false;
            }
            if (needLoadUrl) {
                if (checkNeedUpLoadUrl()) {
                    LogUtil.d(TAG, "checkNeedUpLoadUrl");
                    filePath = null;
                } else if (videoMsg != null && videoMsg.getSource() != null) {
                    LogUtil.i(TAG, "getDownloadUrl source:" + videoMsg.getSource().getDownloadUrl());
                    filePath = videoMsg.getSource().getDownloadUrl();
                }
            }
            if (!FileUtils.exists(thumbnailPath) && videoMsg != null && videoMsg.getIcon() != null) {
                LogUtil.i(TAG, "getDownloadUrl icon: " + videoMsg.getIcon().getDownloadUrl());
                thumbnailPath = videoMsg.getIcon().getDownloadUrl();
            }
        } else {
            LogUtil.d(TAG, "initView: 当前锁屏，不主动播放");
        }
        RequestOptions options = new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random())));
        if (TextUtils.isEmpty(thumbnailPath) && !TextUtils.isEmpty(filePath)
                && (filePath.contains(FileManager.WEBP_FORMAT) || filePath.contains(FileManager.JPG_FORMAT)
                || filePath.contains(FileManager.PNG_FORMAT))) {
            Glide.with(getApplicationContext()).load(filePath).apply(options).into(coverIv);
        } else {
            Glide.with(getApplicationContext()).load(thumbnailPath).apply(options).into(coverIv);
        }
        starLoading();
        LogUtil.d(TAG, "playVideo: filePath = [" + filePath + "], thumbnailPath = [" + thumbnailPath + "]");
        ((PlayVideoPresenter) presenter).playVideo(filePath, defaultPlayVideoView);
    }

    private boolean checkNeedUpLoadUrl() {
        if (videoKeyOrToken == null || videoMsg == null || videoMsg.getSource() == null) {
            return false;
        }
        String videoKey = videoKeyOrToken.getVideoKey();
        if (TextUtils.isEmpty(videoKey)) {
            return false;
        }
        if (videoKeyOrToken.getTransferCode() == 0 || videoKey.startsWith(TRANS_)) {
            return !Objects.equals(videoMsg.getSource().getKey(), videoKey);
        }
        return false;
    }

    public void starLoading() {
        llPlayLoading.setVisibility(View.VISIBLE);
        viewLoading.setBackground(loadingAnim);
        loadingAnim.start();
    }

    public void stopLoading() {
        llPlayLoading.setVisibility(View.GONE);
        if (loadingAnim != null && loadingAnim.isRunning()) {
            loadingAnim.stop();
        }
    }

    private void registerBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterBus() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEvent(EventData eventData) {
        if (eventData == null) {
            return;
        }
        if ((eventData.getType() == 1 || eventData.getType() == 9) && eventData.getData() instanceof DbMoment) {
            LogUtil.i(TAG, "receive delete moment event:" + eventData.toString());
            DbMoment moment = (DbMoment) eventData.getData();
            if (moment != null && moment.getMomentId().equals(dbMoment.getMomentId())) {
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoEvent(MomentVideoData videoData) {
        LogUtil.i(TAG, "play_video page receive momentVideoData:" + videoData);
        if (videoData == null) {
            return;
        }
        if (videoData.getAction() == 1) {
            finish();
        } else if (videoData.getAction() == 2) {
            ((PlayVideoPresenter) presenter).pausePlayVideo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishCurrentPage(String action) {
        if (Constants.FINISH_PLAY_VIDEO_ACTIVITY.equals(action)) {
            LogUtil.d(TAG, "finishCurrentPage: getVideoParam Success Finish Current Page !!!");
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "onNewIntent.");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasInit && hasFocus) {
            hasInit = true;
        }
        LogUtil.d(TAG, "hasFocus = " + hasFocus + "onWindowFocusChanged:");
        if (!hasFocus) {
            ((PlayVideoPresenter) presenter).pausePlayVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        long startTime = System.currentTimeMillis();
        ((PlayVideoPresenter) presenter).stopPlayVideo();
        LogUtil.d(TAG, "stopPlayVideo" + (System.currentTimeMillis() - startTime));
        ((PlayVideoPresenter) presenter).destroyVideoView();
        unregisterBus();
        uiHandler.removeCallbacksAndMessages(null);
        unRegisterMomentVideoBroadcastReceiver();
        stopLoading();
        LogUtil.i(TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((PlayVideoPresenter) presenter).dealScreenOff();
        LogUtil.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!SystemUtil.isActivityOnTop(getApplicationContext(), PlayVideoActivity.class)) {
            LogUtil.d(TAG, "finish");
            finish();
            plVideoView.setVisibility(View.GONE);
        }
        LogUtil.i(TAG, "onStop");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public PlayVideoPresenter createPresenter() {
        LogUtil.i(TAG, "createPresenter called.");
        return new PlayVideoPresenter(getApplicationContext(), uiHandler);
    }

    @Override
    public void playVideo() {
        ChatVideoUtil.keepScreenOn(getWindow());
        thumbnailIv.setVisibility(View.GONE);
        if (ivPlayButton != null) {
            ivPlayButton.setVisibility(View.GONE);
        }
        numTipSeekBar.setSelectProgress(0);
    }

    @Override
    public void playVideoComplete() {
        ChatVideoUtil.cancelKeepScreenOn(getWindow());
        int selectProgress = numTipSeekBar.getSelectProgress();
        if (selectProgress < COMPLETION_PROGRESS) {
            numTipSeekBar.setSelectProgress(COMPLETION_PROGRESS);
            LogUtil.d(TAG, "playVideoComplete: selectProgress = " + selectProgress);
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    numTipSeekBar.setVisibility(View.GONE);
                    numTipSeekBar.setSelectProgress(0);
                }
            }, 200L);
        } else {
            numTipSeekBar.setVisibility(View.GONE);
            numTipSeekBar.setSelectProgress(0);
        }
        if (canReply) {
            ivPlayButton.setVisibility(View.GONE);
            ((PlayVideoPresenter) presenter).playVideo(filePath, defaultPlayVideoView);
        } else {
            ivPlayButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void pausePlayVideo() {
        if (ivPlayButton == null) {
            return;
        }
        ChatVideoUtil.cancelKeepScreenOn(getWindow());
        if (canReply) {
            ivPlayButton.setVisibility(View.GONE);
        } else {
            ivPlayButton.setVisibility(View.VISIBLE);
        }
        LogUtil.i(TAG, "pausePlayVideo");
    }

    @Override
    public void dealScreenOff(Bitmap bitmap) {
        if (bitmap == null) {
            LogUtil.i(TAG, "dealScreenOff but currentFrameBitmap is null!");
            return;
        }
        thumbnailIv.setVisibility(View.VISIBLE);
        thumbnailIv.setImageBitmap(bitmap);
        LogUtil.i(TAG, "thumbnailIv setImageBitmap currentFrameBitmap");
    }

    @Override
    public void continuePlayVideo(boolean clickScreenOn) {
        LogUtil.d(TAG, "continuePlayVideo: clickScreenOn = " + clickScreenOn + ";coverIv = " + (coverIv.getVisibility() == View.VISIBLE));
        ChatVideoUtil.keepScreenOn(getWindow());
        coverIv.setVisibility(View.INVISIBLE);
        thumbnailIv.setBackground(null);
        ivPlayButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bufferComplete(String path) {
        filePath = path;
    }

    @Override
    public void bufferCompleteThumbnailPath(String path) {
        thumbnailPath = path;
    }

    @Override
    public void canPauseVideo() {
        isCanPauseState = true;
        ivPlayButton.setVisibility(View.INVISIBLE);
        stopLoading();
        if (!isAddFragment) {
            dealShowBarrageView();
        }
    }

    private void dealShowBarrageView() {
        if (dbMoment == null || !isFunVideoOrPointVideo) {
            return;
        }
        barrageFragment = new BarrageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BarrageFragment.MOMENT_DATA, JSONUtil.toJSON(dbMoment));
        barrageFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rl_root, barrageFragment);
        transaction.commitAllowingStateLoss();
        isAddFragment = true;
    }

    @Override
    public void onClick(View view) {
        if (!isCanPauseState) {
            LogUtil.i(TAG, "onClick click is invalid");
            return;
        }
        if (!isFunVideoOrPointVideo) {
            numTipSeekBar.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.video_view) {
            if (coverIv.getVisibility() != View.VISIBLE) {
                ((PlayVideoPresenter) presenter).clickVideoView();
            }
        } else if (view.getId() == R.id.iv_play_button) {
            ((PlayVideoPresenter) presenter).replayOrContinutePlay();
            ivPlayButton.setVisibility(View.GONE);
        }
        if (barrageFragment != null) {
            barrageFragment.hideBottomView();
        }
    }

    @Subscribe
    public void changePlayState(ChangePlayStateEvent event) {
        LogUtil.w(TAG, "changePlayState: " + event);
        if (event.getPlayState() == 1) {
            ((PlayVideoPresenter) presenter).replayOrContinutePlay();
            ivPlayButton.setVisibility(View.GONE);
        } else if (event.getPlayState() == 2 && coverIv.getVisibility() != View.VISIBLE) {
            ((PlayVideoPresenter) presenter).pausePlayVideo();
        }
    }

    @Override
    public void updatePlayingProgress(int progress) {
        if (numTipSeekBar == null) {
            return;
        }
        if (progress > 0 && coverIv.getVisibility() == View.VISIBLE) {
            coverIv.setVisibility(View.INVISIBLE);
        }
        if (progress > 0 && numTipSeekBar.getVisibility() != View.VISIBLE && !isFunVideoOrPointVideo) {
            numTipSeekBar.setVisibility(View.VISIBLE);
        }
        if (isCanPauseState && ivPlayButton.getVisibility() == View.VISIBLE) {
            ivPlayButton.setVisibility(View.INVISIBLE);
        }
        numTipSeekBar.setSelectProgress(progress);
    }

    @Override
    public void startToRecordActivity() {
        finish();
    }

    @Override
    public void beginRenderFirstFrame(boolean clickScreenOn) {
        LogUtil.i(TAG, "clickScreenOn:" + clickScreenOn);
        if (!rootView.isClickable()) {
            rootView.setClickable(true);
        }
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "run: coverIv = " + (coverIv.getVisibility() == View.VISIBLE));
                coverIv.setVisibility(View.GONE);
            }
        }, 100L);
    }

    @Override
    public void replayVideo(boolean clickScreenOn) {
        LogUtil.i(TAG, "clickScreenOn:" + clickScreenOn + " filePath:" + filePath);
        if (isFunVideoOrPointVideo) {
            return;
        }
        numTipSeekBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void playVideoFailed(boolean isFirstPlayFail, String httpError) {
        LogUtil.d(TAG, "play video failed! isFirstPlayFail = " + isFirstPlayFail + ", http error = " + httpError);
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showNoConnected(this);
            return;
        }
        LogUtil.i(TAG, "playVideoFailed: " + videoKeyOrToken);
        if (isFirstPlayFail) {
            if (videoKeyOrToken != null) {
                if (videoMsg == null) {
                    videoMsg = new VideoMsg();
                }
                ((PlayVideoPresenter) presenter).loadVideoUrl(videoKeyOrToken.getVideoKey(),
                        videoKeyOrToken.getPicKey(), dbMoment, videoMsg);
                return;
            }
            stopLoading();
            rootView.setClickable(false);
            ToastUtil.showShort(this, getString(R.string.video_play_failed));
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    finish();
                }
            }, com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
            return;
        }
        rootView.setClickable(false);
        if (judgeFreCode(httpError)) {
            ToastUtil.showShort(this, getString(R.string.video_play_failed_tip));
        } else {
            ToastUtil.showShort(this, getString(R.string.video_invalid));
        }
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                finish();
            }
        }, com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
    }

    private boolean judgeFreCode(String error) {
        if (TextUtils.isEmpty(error)) {
            return false;
        }
        return error.contains("1003") || error.contains("1002");
    }

    public void registerMomentVideoBroadcastReceiver() {
        recordVideoReceiver = new RecordVideoReceiver();
        BroadcastReceiverUtil.registerReceiver(this, recordVideoReceiver, RecordVideoReceiver.getWeichatCommonFilter());
    }

    public void unRegisterMomentVideoBroadcastReceiver() {
        if (recordVideoReceiver != null) {
            BroadcastReceiverUtil.unregisterReceiver(this, recordVideoReceiver);
            recordVideoReceiver = null;
        }
    }
}
package com.xtc.moment.share.view.playvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.module.playvideo.IVideoView;
import com.xtc.moment.module.widget.DefaultPlayVideoView;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 分享视频预览播放的 Presenter，负责播放控制与进度刷新。
 */
public class PlayShareVideoPresenter extends MvpBasePresenter<IPlayShareVideoView> {

    private static final long PERIOD_TIME = 20;
    private static final int MEDIA_NOT_READY = -1;

    private final String TAG = "PlayVideoPresenter";
    private DefaultPlayVideoView defaultPlayVideoView;
    private String filePath;
    private final Context mContext;
    private IMomentServe momentServe;
    private Timer progressTimer;
    private final Handler uiHandler;
    private IVideoView videoView;
    private boolean isPausePlayVideo = false;
    private boolean isPlayComplete = true;
    private boolean isPlayFailed = false;
    private boolean screenOff = false;
    private boolean isNet = false;

    public PlayShareVideoPresenter(Context context, Handler handler) {
        this.mContext = context;
        this.uiHandler = handler;
        this.momentServe = MomentServeImpl.getInstance(context);
    }

    private boolean clickScreenOn() {
        return this.screenOff;
    }

    private void resetScreenOff() {
        this.screenOff = false;
    }

    public void setVideoView(IVideoView videoView) {
        this.videoView = videoView;
        videoView.setOnPlayInfoListener(new IVideoView.OnPlayInfoListener() {
            @Override
            public void onPlaying(final int progress) {
                if (uiHandler != null) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (getView() != null) {
                                getView().updatePlayingProgress(progress);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPlayFailed(int what) {
                isPlayFailed = true;
                if (getView() != null) {
                    getView().playVideoFailed(true);
                }
            }
        });
    }

    private void playVideo(String path, boolean loop) {
        if (this.defaultPlayVideoView == null) {
            return;
        }
        if (!TextUtils.isEmpty(path) && (path.contains(FileManager.MP4_FORMAT)
                || path.contains(FileManager.MP4_FORMAT_UPPER_CASE))) {
            this.defaultPlayVideoView.setIsDialogPreview(true).playVideo(path, true, new IVideoView.OnPlayVideoListener() {
                @Override
                public void beginRenderFirstFrame(long duration) {
                    LogUtil.i(TAG, "beginRenderFirstFrame " + duration);
                    startUpdatePlayVideoProgress();
                    if (getView() != null) {
                        getView().canPauseVideo();
                    }
                    resetScreenOff();
                }

                @Override
                public void onCompletion() {
                    LogUtil.i(TAG, "onCompletion: ");
                }

                @Override
                public void onError(int what) {
                    LogUtil.e(TAG, "onError: playError " + what);
                    if (getView() != null) {
                        getView().playVideoFailed(true);
                    }
                }
            }, null);
        } else {
            LogUtil.w(TAG, "playVideo: wrong format");
            getView().playVideoFailed(true);
        }
    }

    public void playVideo(String path, DefaultPlayVideoView playVideoView) {
        if (playVideoView != null) {
            this.defaultPlayVideoView = playVideoView;
        }
        this.isPausePlayVideo = false;
        this.isPlayComplete = false;
        if (getView() == null) {
            return;
        }
        getView().playVideo();
        this.filePath = path;
        playVideo(path, false);
    }

    /** 继续播放或重新开始播放。 */
    public void replayOrContinutePlay() {
        if (this.isPausePlayVideo) {
            continuePlayVideo();
            this.isPausePlayVideo = false;
        } else {
            playVideo(this.filePath, this.defaultPlayVideoView);
            if (getView() != null) {
                getView().replayVideo(clickScreenOn());
            }
        }
    }

    private void startUpdatePlayVideoProgress() {
        stopUpdatePlayVideoProgress();
        this.progressTimer = new Timer();
        this.progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (defaultPlayVideoView == null || defaultPlayVideoView.isPlaying()) {
                        double currentPosition = defaultPlayVideoView.getCurrentPosition() * 100;
                        double progress = (currentPosition * 1.0d) / defaultPlayVideoView.getDuration();
                        final int progressValue = (int) progress;
                        if (progressValue >= 0) {
                            if (uiHandler != null) {
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProgress(progressValue);
                                    }
                                });
                            } else {
                                HandlerUtil.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateProgress(progressValue);
                                    }
                                });
                            }
                        } else if (!isNet) {
                            isNet = true;
                        } else {
                            LogUtil.d(TAG, "连接网络中");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L, PERIOD_TIME);
    }

    private void updateProgress(int progress) {
        DefaultPlayVideoView playVideoView = this.defaultPlayVideoView;
        if (playVideoView != null && playVideoView.isPlaying() && getView() != null) {
            getView().updatePlayingProgress(progress);
            return;
        }
        LogUtil.d(TAG, "mVideoView progress less than zero, progress:" + progress);
    }

    private void stopUpdatePlayVideoProgress() {
        Timer timer = this.progressTimer;
        if (timer != null) {
            timer.cancel();
            this.progressTimer = null;
        }
        getView().updatePlayingProgress(0);
    }

    public void pausePlayVideo() {
        LogUtil.i(TAG, "run: pausePlayVideo + isPausePlayVideo=" + this.isPausePlayVideo + ";isPlayComplete="
                + this.isPlayComplete + ";isPlayFailed=" + this.isPlayFailed);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (isPausePlayVideo || isPlayComplete || isPlayFailed) {
                    if (defaultPlayVideoView != null) {
                        LogUtil.i(TAG, "run: run: pausePlayVideo mediaIsPlaying ==== "
                                + defaultPlayVideoView.mediaIsPlaying());
                        pausePlayVideoNow();
                        return;
                    }
                    if (getView() != null) {
                        getView().pausePlayVideo();
                    }
                    return;
                }
                isPausePlayVideo = true;
                if (defaultPlayVideoView != null) {
                    defaultPlayVideoView.pausePlayVideo();
                }
                if (getView() != null) {
                    getView().pausePlayVideo();
                }
            }
        });
    }

    public void pausePlayVideoNow() {
        LogUtil.e(TAG, "run: pausePlayVideo pausePlayVideoNow + isPausePlayVideo=" + this.isPausePlayVideo
                + ";isPlayComplete=" + this.isPlayComplete + ";isPlayFailed=" + this.isPlayFailed);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (defaultPlayVideoView != null && defaultPlayVideoView.mediaIsPlaying()) {
                    LogUtil.e(TAG, "run: pausePlayVideo pauseP pausePlayVideoNow: ;defaultPlayVideoView.isPlaying= "
                            + (defaultPlayVideoView != null && defaultPlayVideoView.mediaIsPlaying()));
                    defaultPlayVideoView.pausePlayVideo();
                    if (getView() != null) {
                        getView().pausePlayVideo();
                    }
                    return;
                }
                if (getView() != null) {
                    getView().pausePlayVideo();
                }
            }
        });
    }

    public void clickVideoView() {
        boolean mediaIsPlaying = this.defaultPlayVideoView.mediaIsPlaying();
        LogUtil.d(TAG, "clickVideoView: " + mediaIsPlaying);
        if (mediaIsPlaying) {
            pausePlayVideo();
        }
    }

    public boolean isPlaying() {
        return this.isPausePlayVideo;
    }

    /** 处理息屏，保存当前帧用于恢复显示。 */
    public void dealScreenOff() {
        if (this.screenOff) {
            return;
        }
        LogUtil.i(TAG, "playvideo dealScreenOff!");
        this.screenOff = true;
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                if (defaultPlayVideoView != null) {
                    return defaultPlayVideoView.getCurrentFrameBitmap();
                }
                return null;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        if (getView() != null) {
                            getView().dealScreenOff(bitmap);
                        } else {
                            LogUtil.i(TAG, "dealScreenOff but getView() is null!");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getCurrentFrameBitmap error :", throwable);
                    }
                });
    }

    public void continuePlayVideo() {
        if (this.isPausePlayVideo) {
            this.isPausePlayVideo = false;
            DefaultPlayVideoView playVideoView = this.defaultPlayVideoView;
            if (playVideoView != null) {
                playVideoView.continuePlayVideo();
            }
            if (getView() != null) {
                getView().continuePlayVideo(clickScreenOn());
            }
            resetScreenOff();
        }
    }

    public void stopPlayVideo() {
        this.uiHandler.removeCallbacksAndMessages(null);
        DefaultPlayVideoView playVideoView = this.defaultPlayVideoView;
        if (playVideoView != null) {
            playVideoView.stopVideo();
        }
    }

    public void destroyVideoView() {
        DefaultPlayVideoView playVideoView = this.defaultPlayVideoView;
        if (playVideoView != null) {
            playVideoView.destroy();
            this.defaultPlayVideoView = null;
        }
        Timer timer = this.progressTimer;
        if (timer != null) {
            timer.cancel();
        }
    }
}
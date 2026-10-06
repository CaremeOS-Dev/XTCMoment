package com.xtc.moment.module.playvideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoPlayCompletelyEvent;
import com.xtc.moment.module.widget.DefaultPlayVideoView;
import com.xtc.moment.serve.BatteryServe;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PlayVideoPresenter extends MvpBasePresenter<IPlayVideoView> {

    private static final String TAG = "PlayVideoPresenter";
    private static final long PERIOD_TIME = 20;

    private Context mContext;
    private Handler uiHandler;
    private IMomentServe momentServe;
    private IVideoView videoView;
    private DefaultPlayVideoView defaultPlayVideoView;
    private String filePath;
    private Timer progressTimer;
    private boolean isPausePlayVideo = false;
    private boolean isPlayComplete = true;
    private boolean isPlayFailed = false;
    private boolean screenOff = false;
    private boolean isNet = false;
    private final int MEDIA_NOT_READY = -1;
    private final BatteryServe mBatteryServe = BatteryServe.getInstance();

    public PlayVideoPresenter(Context context, Handler handler) {
        mContext = context;
        uiHandler = handler;
        momentServe = MomentServeImpl.getInstance(context);
    }

    private boolean clickScreenOn() {
        return screenOff;
    }

    private void resetScreenOff() {
        screenOff = false;
    }

    public void registerBatteryChange(Context context) {
        if (context == null || mBatteryServe == null) {
            LogUtil.w(TAG, "registerBatteryChange but context or mBatteryServe is null");
            return;
        }
        mBatteryServe.registerBatteryChange(context.getApplicationContext(), new BatteryServe.BatteryChangeCallback() {
            @Override
            public void onBatteryChange(boolean charging) {
                if (!charging) {
                    return;
                }
                LogUtil.i(TAG, "检测到充电状态，停止播放视频");
                if (getView() == null) {
                    return;
                }
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        getView().pausePlayVideo();
                    }
                });
                pausePlayVideo();
            }
        });
    }

    public void unRegisterBatteryChange(Context context) {
        if (context == null || mBatteryServe == null) {
            LogUtil.w(TAG, "unRegisterBatteryChange but context or mBatteryServe is null");
            return;
        }
        mBatteryServe.unRegisterBatteryChange(context.getApplicationContext());
    }

    public void setVideoView(IVideoView iVideoView) {
        videoView = iVideoView;
        iVideoView.setOnPlayInfoListener(new IVideoView.OnPlayInfoListener() {
            @Override
            public void onPlaying(final int position) {
                if (uiHandler == null) {
                    return;
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getView() != null) {
                            getView().updatePlayingProgress(position);
                        }
                    }
                });
            }

            @Override
            public void onPlayFailed(int what) {
                isPlayFailed = true;
                if (getView() != null) {
                    getView().playVideoFailed(true, null);
                }
            }
        });
    }

    private void playVideo(String path, boolean isFirstPlay) {
        if (defaultPlayVideoView == null) {
            return;
        }
        if (path != null && (path.contains(FileManager.MP4_FORMAT) || path.contains(FileManager.MP4_FORMAT_UPPER_CASE))) {
            defaultPlayVideoView.setIsDialogPreview(false).playVideo(path, true, new IVideoView.OnPlayVideoListener() {
                @Override
                public void beginRenderFirstFrame(long time) {
                    LogUtil.i(TAG, "beginRenderFirstFrame " + time);
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
                        getView().playVideoFailed(true, null);
                    }
                }
            }, null);
            return;
        }
        LogUtil.w(TAG, "playVideo: wrong format");
        if (getView() != null) {
            getView().playVideoFailed(true, null);
        }
    }

    public void playVideo(String path, DefaultPlayVideoView videoView) {
        if (videoView != null) {
            defaultPlayVideoView = videoView;
        }
        isPausePlayVideo = false;
        isPlayComplete = false;
        if (getView() == null) {
            return;
        }
        getView().playVideo();
        filePath = path;
        playVideo(path, false);
    }

    public void replayOrContinutePlay() {
        if (isPausePlayVideo) {
            continuePlayVideo();
            isPausePlayVideo = false;
            return;
        }
        playVideo(filePath, defaultPlayVideoView);
        if (getView() != null) {
            getView().replayVideo(clickScreenOn());
        }
    }

    private void startUpdatePlayVideoProgress() {
        final LinkedList<Integer> progressList = new LinkedList<>();
        stopUpdatePlayVideoProgress();
        progressTimer = new Timer();
        progressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (defaultPlayVideoView != null && !defaultPlayVideoView.isPlaying()) {
                        return;
                    }
                    int progress = (int) (defaultPlayVideoView.getCurrentPosition() * 100 * 1.0d
                            / defaultPlayVideoView.getDuration());
                    progressList.add(Integer.valueOf(progress));
                    int size = progressList.size();
                    if (size > 2 && progressList.get(size - 1).intValue() - progressList.get(size - 2).intValue() < 0) {
                        EventBus.getDefault().post(new VideoPlayCompletelyEvent(true));
                        progressList.clear();
                    }
                    if (progress < 0) {
                        if (!isNet) {
                            isNet = true;
                        } else {
                            LogUtil.d(TAG, "连接网络中");
                        }
                        return;
                    }
                    final int current = progress;
                    if (uiHandler != null) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateProgress(current);
                            }
                        });
                    } else {
                        HandlerUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                updateProgress(current);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0L, PERIOD_TIME);
    }

    private void updateProgress(int progress) {
        if (defaultPlayVideoView != null && defaultPlayVideoView.isPlaying() && getView() != null) {
            getView().updatePlayingProgress(progress);
            return;
        }
        LogUtil.d(TAG, "mVideoView progress less than zero, progress:" + progress);
    }

    private void stopUpdatePlayVideoProgress() {
        if (progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        if (getView() != null) {
            getView().updatePlayingProgress(0);
        }
    }

    public void pausePlayVideo() {
        LogUtil.i(TAG, "run: pausePlayVideo + isPausePlayVideo=" + isPausePlayVideo + ";isPlayComplete=" + isPlayComplete
                + ";isPlayFailed=" + isPlayFailed);
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (isPausePlayVideo || isPlayComplete || isPlayFailed) {
                    if (defaultPlayVideoView != null) {
                        LogUtil.i(TAG, "run: run: pausePlayVideo mediaIsPlaying ==== " + defaultPlayVideoView.mediaIsPlaying());
                        pausePlayVideoNow();
                    } else if (getView() != null) {
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
        LogUtil.e(TAG, "run: pausePlayVideo pausePlayVideoNow + isPausePlayVideo=" + isPausePlayVideo + ";isPlayComplete=" + isPlayComplete
                + ";isPlayFailed=" + isPlayFailed);
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
        boolean isPlaying = defaultPlayVideoView.mediaIsPlaying();
        LogUtil.d(TAG, "clickVideoView: " + isPlaying);
        if (isPlaying) {
            pausePlayVideo();
        }
    }

    public boolean isPlaying() {
        return isPausePlayVideo;
    }

    public void dealScreenOff() {
        if (screenOff) {
            return;
        }
        LogUtil.i(TAG, "playvideo dealScreenOff!");
        screenOff = true;
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                if (defaultPlayVideoView != null) {
                    return defaultPlayVideoView.getCurrentFrameBitmap();
                }
                return null;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
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
        if (!isPausePlayVideo) {
            return;
        }
        isPausePlayVideo = false;
        if (defaultPlayVideoView != null) {
            defaultPlayVideoView.continuePlayVideo();
        }
        if (getView() != null) {
            getView().continuePlayVideo(clickScreenOn());
        }
        resetScreenOff();
    }

    public void stopPlayVideo() {
        uiHandler.removeCallbacksAndMessages(null);
        if (defaultPlayVideoView != null) {
            defaultPlayVideoView.stopVideo();
        }
    }

    public void destroyVideoView() {
        if (defaultPlayVideoView != null) {
            defaultPlayVideoView.destroy();
            defaultPlayVideoView = null;
        }
        if (progressTimer != null) {
            progressTimer.cancel();
        }
        unRegisterBatteryChange(mContext);
    }

    public void loadVideoUrl(String videoKey, String picKey, final DbMoment moment, final VideoMsg videoMsg) {
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            ToastUtil.showShort(mContext, mContext.getString(R.string.net_work_exception));
            return;
        }
        MomentPhotoServeImpl photoServe = new MomentPhotoServeImpl(mContext);
        ArrayList<String> keys = new ArrayList<>();
        keys.add(videoKey);
        keys.add(picKey);
        photoServe.getDownloadBatchUrl(new FileBatchUrlParam(keys), moment, videoMsg)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<DownloadUrlVo>() {
                    @Override
                    public void onHttpError(Throwable throwable) {
                        super.onHttpError(throwable);
                        LogUtil.i(TAG, "onHttpError: " + throwable);
                        if (getView() != null) {
                            getView().playVideoFailed(false, throwable.getMessage());
                        }
                    }

                    @Override
                    public void onNext(DownloadUrlVo downloadUrlVo) {
                        super.onNext(downloadUrlVo);
                        if (getView() == null) {
                            return;
                        }
                        EventBus.getDefault().post(new EventData(8, moment));
                        if (videoMsg == null || videoMsg.getSource() == null) {
                            LogUtil.d(TAG, "getDownloadBatchUrl videoMsg invalid");
                            return;
                        }
                        String downloadUrl = videoMsg.getSource().getDownloadUrl();
                        getView().bufferCompleteThumbnailPath(videoMsg.getLocalThumbnailPath());
                        getView().bufferComplete(downloadUrl);
                        playVideo(downloadUrl, (DefaultPlayVideoView) null);
                    }
                });
    }
}
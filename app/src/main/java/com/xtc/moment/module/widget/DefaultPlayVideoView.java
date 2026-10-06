package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.view.Surface;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.playvideo.IVideoView;
import com.xtc.moment.util.VideoPlayManager;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Default {@link IVideoView} used by the play video page.
 *
 * <p>Keeps the screen awake while a clip is playing and delegates the media work to
 * {@link VideoPlayManager}.
 */
public class DefaultPlayVideoView implements IVideoView {

    /** Returned by {@link #getCurrentPosition()} / {@link #getDuration()} when no media is ready. */
    private static final int MEDIA_NOT_READY = -1;

    private final String tag = DefaultPlayVideoView.class.getSimpleName();
    private final SurfaceTexture surfaceTexture;
    private final PlayVideoTextureView textureView;
    private final PowerManager.WakeLock wakeLock;
    private final AudioManager audioManager;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private final long maxLoopPlayTime = Constants.DIFFER_TIME;

    private VideoPlayManager mediaManager;
    private Surface surface;
    private boolean isPlaying;

    private final Runnable delayScreenOffAction = new Runnable() {
        @Override
        public void run() {
            makeScreenOff();
        }
    };

    public DefaultPlayVideoView(Context context, PlayVideoTextureView textureView, SurfaceTexture surfaceTexture,
                                Surface surface) {
        this.textureView = textureView;
        this.surfaceTexture = surfaceTexture;
        this.surface = surface;
        this.mediaManager = new VideoPlayManager(context);
        this.wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, this.tag);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void pausePlayVideoWithReset() {
    }

    @Override
    public void setOnPlayInfoListener(OnPlayInfoListener listener) {
    }

    @Override
    public void playVideo(String dir, String fileName, boolean loop, OnPlayVideoListener listener) {
        if (this.audioManager != null) {
            this.audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.mediaManager.playVideo(dir, fileName, this.surface, loop);
        if (listener != null) {
            listener.beginRenderFirstFrame(0L);
        }
        this.isPlaying = true;
        LogUtil.d(this.tag, "playVideo videoView:" + this + " filePath:" + dir + fileName);
    }

    public DefaultPlayVideoView setIsDialogPreview(boolean isPreview) {
        this.mediaManager.setIsPreview(isPreview);
        return this;
    }

    @Override
    public void playVideo(String path, boolean loop, final OnPlayVideoListener listener,
                          OnBufferingListener bufferingListener) {
        if (this.audioManager != null) {
            this.audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.isPlaying = true;
        this.mediaManager.playVideo(path, this.surface, loop, this.textureView);
        this.mediaManager.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer player, int what, int extra) {
                if (listener == null) {
                    return false;
                }
                listener.onError(what);
                return false;
            }
        });
        this.mediaManager.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer player, int what, int extra) {
                if (what == 3 && listener != null) {
                    listener.beginRenderFirstFrame(0L);
                }
                return false;
            }
        });
        LogUtil.d(this.tag, "playVideo videoView:" + this + " filePath:" + path);
    }

    @Override
    public void stopVideo() {
        if (this.audioManager != null) {
            this.audioManager.abandonAudioFocus(null);
        }
        removeScreenOffMsg();
        makeScreenOff();
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (mediaManager != null) {
                    mediaManager.stopVideo();
                }
                return true;
            }
        }).subscribeOn(Schedulers.computation())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean result) {
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.d(tag, "stopVideo error: ", throwable);
                    }
                });
        this.isPlaying = false;
        LogUtil.d(this.tag, "stopPlayVideo videoView:" + this);
    }

    @Override
    public void pausePlayVideo() {
        if (this.audioManager != null) {
            this.audioManager.abandonAudioFocus(null);
        }
        removeScreenOffMsg();
        makeScreenOff();
        this.mediaManager.pauseVideo();
        this.isPlaying = false;
        LogUtil.d(this.tag, "pausePlayVideo videoView:" + this);
    }

    @Override
    public Bitmap getCurrentFrameBitmap() {
        return this.textureView.getBitmap();
    }

    @Override
    public void continuePlayVideo() {
        if (this.audioManager != null) {
            this.audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.mediaManager.continuePlayVideo();
        this.isPlaying = true;
        LogUtil.d(this.tag, "continutePlayVideo videoView:" + this);
    }

    @Override
    public void destroy() {
        this.isPlaying = false;
        this.surface.release();
        this.surface = null;
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }

    public boolean mediaIsPlaying() {
        return this.mediaManager.isMediaPlaying();
    }

    public void setCoverView(View coverView) {
        if (coverView != null) {
            coverView.setVisibility(View.GONE);
        }
    }

    public void resetVideoView() {
        removeScreenOffMsg();
        makeScreenOff();
    }

    public void makeScreenOff() {
        try {
            if (this.wakeLock.isHeld()) {
                this.wakeLock.release();
                LogUtil.d(this.tag, "makeScreenOff.");
            }
        } catch (Throwable throwable) {
            LogUtil.d(this.tag, "release error: ", throwable);
        }
    }

    public void makeScreenOn() {
        try {
            if (this.wakeLock.isHeld()) {
                return;
            }
            this.wakeLock.acquire();
            LogUtil.d(this.tag, "makeScreenOn.");
        } catch (Throwable throwable) {
            LogUtil.d(this.tag, "acquire error: ", throwable);
        }
    }

    private void delayScreenOff() {
        this.uiHandler.postDelayed(this.delayScreenOffAction, this.maxLoopPlayTime);
    }

    private void removeScreenOffMsg() {
        this.uiHandler.removeCallbacksAndMessages(null);
    }

    public int getCurrentPosition() {
        VideoPlayManager manager = this.mediaManager;
        if (manager == null) {
            return MEDIA_NOT_READY;
        }
        return manager.getCurrentPosition();
    }

    public int getDuration() {
        VideoPlayManager manager = this.mediaManager;
        if (manager == null) {
            return MEDIA_NOT_READY;
        }
        return manager.getDuration();
    }
}
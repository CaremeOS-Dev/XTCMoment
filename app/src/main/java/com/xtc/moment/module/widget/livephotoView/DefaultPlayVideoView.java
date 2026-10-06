package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.view.Surface;
import android.view.View;

import com.xtc.log.LogUtil;

/**
 * Default {@link IVideoView} implementation for live photos.
 *
 * <p>Owns the wake lock that keeps the screen on while the clip plays, and delegates the actual
 * media playback to {@link VideoPlayManager}.
 */
public class DefaultPlayVideoView implements IVideoView {

    /** Milliseconds the screen is kept awake after the last play request. */
    private static final long MAX_LOOP_PLAY_TIME_MS = 60000L;

    public VideoPlayManager mediaManager;
    public Surface surface;

    private final String TAG = DefaultPlayVideoView.class.getSimpleName();
    private final SurfaceTexture surfaceTexture;
    private final PlayVideoTextureView textureView;
    private final PowerManager.WakeLock wakeLock;
    private final AudioManager audioManager;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

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
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, this.TAG);
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void setOnPlayInfoListener(OnPlayInfoListener listener) {
    }

    @Override
    public void playVideo(String dir, String fileName, boolean loop, OnPlayVideoListener listener) {
        AudioManager audio = this.audioManager;
        if (audio != null) {
            audio.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.mediaManager.playVideo(dir, fileName, this.surface, loop, listener);
        this.isPlaying = true;
        LogUtil.d(this.TAG, "playVideo videoView:" + this + " filePath:" + dir + fileName);
    }

    @Override
    public void playVideo(String path, boolean loop, OnPlayVideoListener listener, OnBufferingListener bufferingListener) {
        AudioManager audio = this.audioManager;
        if (audio != null) {
            audio.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.isPlaying = true;
        this.mediaManager.playVideo(path, this.surface, loop, listener);
        LogUtil.d(this.TAG, "playVideo videoView:" + this + " filePath:" + path);
    }

    @Override
    public void stopVideo() {
        AudioManager audio = this.audioManager;
        if (audio != null) {
            audio.abandonAudioFocus(null);
        }
        removeScreenOffMsg();
        makeScreenOff();
        this.mediaManager.stopVideo();
        this.isPlaying = false;
        LogUtil.d(this.TAG, "stopPlayVideo videoView:" + this);
    }

    @Override
    public void pausePlayVideo() {
        AudioManager audio = this.audioManager;
        if (audio != null) {
            audio.abandonAudioFocus(null);
        }
        removeScreenOffMsg();
        makeScreenOff();
        this.mediaManager.pauseVideo();
        this.isPlaying = false;
        LogUtil.d(this.TAG, "pausePlayVideo videoView:" + this);
    }

    @Override
    public Bitmap getCurrentFrameBitmap() {
        return this.textureView.getBitmap();
    }

    @Override
    public void continuePlayVideo() {
        AudioManager audio = this.audioManager;
        if (audio != null) {
            audio.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.mediaManager.continuePlayVideo();
        this.isPlaying = true;
        LogUtil.d(this.TAG, "continutePlayVideo videoView:" + this);
    }

    @Override
    public void destroy() {
        this.isPlaying = false;
        this.surface.release();
        this.surface = null;
    }

    @Override
    public boolean isPlaying() {
        return this.isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    @Override
    public void setCoverView(View coverView) {
        if (coverView != null) {
            coverView.setVisibility(View.GONE);
        }
    }

    @Override
    public void resetVideoView() {
        removeScreenOffMsg();
        makeScreenOff();
    }

    public void makeScreenOff() {
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
            LogUtil.d(this.TAG, "makeScreenOff.");
        }
    }

    public void makeScreenOn() {
        if (this.wakeLock.isHeld()) {
            return;
        }
        this.wakeLock.acquire();
        LogUtil.d(this.TAG, "makeScreenOn.");
    }

    public void delayScreenOff() {
        this.uiHandler.postDelayed(this.delayScreenOffAction, MAX_LOOP_PLAY_TIME_MS);
    }

    public void removeScreenOffMsg() {
        this.uiHandler.removeCallbacksAndMessages(null);
    }
}
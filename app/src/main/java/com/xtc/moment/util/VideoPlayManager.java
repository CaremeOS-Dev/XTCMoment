package com.xtc.moment.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.view.Surface;
import android.view.ViewGroup;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.widget.PlayVideoTextureView;

/**
 * Wraps a single {@link MediaPlayer} instance used by the moment video preview surfaces.
 *
 * <p>All playback entry points are synchronized because the player can be touched from the UI
 * thread and from download/background callbacks at the same time.
 */
public class VideoPlayManager {

    private static final String TAG = "VideoPlayManager";

    /** Returned by {@link #getCurrentPosition()} / {@link #getDuration()} when no media is ready. */
    private static final int MEDIA_NOT_READY = -1;

    private final Context context;

    private MediaPlayer mediaPlayer;
    /** True while playback is suspended and should stay paused after a surface is re-attached. */
    private boolean isPause;
    /** True when the surface is a dialog preview, which flips the aspect fitting rule. */
    private boolean isDialogPreview;
    private int videoDuration;

    public VideoPlayManager(Context context) {
        this.context = context;
    }

    public void setIsPreview(boolean isPreview) {
        this.isDialogPreview = isPreview;
    }

    public void playVideo(String dir, String fileName, Surface surface) {
        playVideo(dir, fileName, surface, false);
    }

    public synchronized void playVideo(String dir, String fileName, Surface surface, boolean loop) {
        String path = dir + fileName;
        if (surface != null && surface.isValid()) {
            playVideo(path, surface, loop, (PlayVideoTextureView) null);
            return;
        }
        LogUtil.e(TAG, "surface is invalid!");
    }

    public synchronized void playVideo(String path, Surface surface, boolean loop, final PlayVideoTextureView textureView) {
        if (surface != null) {
            try {
                if (surface.isValid()) {
                    this.isPause = false;
                    if (this.mediaPlayer == null) {
                        this.mediaPlayer = new MediaPlayer();
                    }
                    try {
                        this.mediaPlayer.reset();
                        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        this.mediaPlayer.setDataSource(path);
                        this.mediaPlayer.setSurface(surface);
                        this.mediaPlayer.setScreenOnWhilePlaying(false);
                        this.mediaPlayer.setLooping(loop);
                        this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer player) {
                                if (textureView != null) {
                                    LogUtil.d(TAG, "动态调整播放播放控件的尺寸");
                                    measureWidthAndHeight(textureView);
                                }
                                player.start();
                                VideoPlayManager.this.videoDuration = player.getDuration();
                                if (VideoPlayManager.this.isPause) {
                                    player.pause();
                                }
                            }
                        });
                        this.mediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e(TAG, "playVideo " + e.toString());
                    }
                    return;
                }
            } catch (Throwable throwable) {
                throw throwable;
            }
        }
        LogUtil.e(TAG, "surface is invalid!");
    }

    /** Resizes the texture view so the video keeps its aspect ratio inside the measured bounds. */
    private void measureWidthAndHeight(PlayVideoTextureView textureView) {
        int videoHeight = this.mediaPlayer.getVideoHeight();
        int videoWidth = this.mediaPlayer.getVideoWidth();
        ViewGroup.LayoutParams params = textureView.getLayoutParams();
        int measuredWidth = textureView.getMeasuredWidth();
        int measuredHeight = textureView.getMeasuredHeight();
        if (videoHeight == 0 || videoWidth == 0) {
            params.width = measuredWidth;
            params.height = measuredHeight;
        } else {
            float videoWidthF = videoWidth;
            float widthRatio = measuredWidth / videoWidthF;
            float videoHeightF = videoHeight;
            float heightRatio = measuredHeight / videoHeightF;
            if (!this.isDialogPreview ? heightRatio < widthRatio : heightRatio >= widthRatio) {
                widthRatio = heightRatio;
            }
            int height = (int) (videoHeightF * widthRatio);
            int width = (int) (videoWidthF * widthRatio);
            params.height = height;
            params.width = width;
            LogUtil.d(TAG, "newWidth = " + width + ", newHeight = " + height);
        }
        LogUtil.d(TAG, "播放控件width = " + measuredWidth + ", height = " + measuredHeight);
        LogUtil.d(TAG, "videoWidth = " + videoWidth + ", videoHeight = " + videoHeight);
        textureView.setLayoutParams(params);
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        MediaPlayer player = this.mediaPlayer;
        if (player != null) {
            player.setOnCompletionListener(listener);
        }
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        MediaPlayer player = this.mediaPlayer;
        if (player != null) {
            player.setOnErrorListener(listener);
        }
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener listener) {
        MediaPlayer player = this.mediaPlayer;
        if (player != null) {
            player.setOnInfoListener(listener);
        }
    }

    public synchronized void stopVideo() {
        long start = SystemClock.elapsedRealtime();
        this.isPause = false;
        if (this.mediaPlayer != null) {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        LogUtil.d(TAG, "stopVideo: " + (SystemClock.elapsedRealtime() - start));
    }

    public synchronized void pauseVideo() {
        if (this.mediaPlayer == null) {
            return;
        }
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
        }
        this.isPause = true;
        this.mediaPlayer.pause();
    }

    public synchronized void continuePlayVideo() {
        if (this.mediaPlayer == null) {
            return;
        }
        LogUtil.w(TAG, "continuePlayVideo: isPause=" + this.isPause);
        if (this.isPause) {
            this.mediaPlayer.start();
            this.isPause = false;
        }
    }

    public synchronized void continuePlayVideo(Surface surface) {
        if (this.mediaPlayer != null && surface != null) {
            if (this.isPause) {
                if (surface.isValid()) {
                    this.mediaPlayer.setSurface(surface);
                    this.mediaPlayer.start();
                    this.isPause = false;
                } else {
                    LogUtil.e(TAG, "suface is null or invalid!");
                    return;
                }
            }
            return;
        }
        LogUtil.e(TAG, "continuePlayVideo return, because mediaPlayer:" + this.mediaPlayer
                + " surfaceHolder" + surface);
    }

    public boolean isPause() {
        return this.isPause;
    }

    public synchronized int getCurrentPosition() {
        if (this.mediaPlayer != null && !this.isPause) {
            return this.mediaPlayer.getCurrentPosition();
        }
        return MEDIA_NOT_READY;
    }

    public synchronized int getDuration() {
        if (this.mediaPlayer != null && !this.isPause) {
            return this.videoDuration;
        }
        return MEDIA_NOT_READY;
    }

    public boolean isMediaPlaying() {
        MediaPlayer player = this.mediaPlayer;
        if (player != null) {
            return player.isPlaying();
        }
        return false;
    }
}
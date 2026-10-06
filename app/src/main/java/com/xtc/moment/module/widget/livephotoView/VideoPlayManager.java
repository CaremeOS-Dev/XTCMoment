package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.Surface;

import com.xtc.log.LogUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Plays the mp4 side of a live photo on a caller supplied {@link Surface}.
 *
 * <p>Two modes are supported: a normal looping play, and a short "pre-play" that seeks near the
 * start, plays for a moment and then reports completion so the caller can freeze the frame.
 */
public class VideoPlayManager {

    private static final String TAG = "VideoPlayManager";

    /** Seek position used before the pre-play, in milliseconds. */
    private static final int PRE_PLAY_SEEK_MS = 600;
    /** Time the pre-play runs before the completion callback is delivered. */
    private static final long PRE_PLAY_DURATION_MS = 900L;

    public static VideoPlayManager instance;

    private final Context context;

    private MediaPlayer mediaPlayer;
    private boolean isPause;

    /** Callback for the pre-play flow. */
    public interface LivePhotoPrePlayCompleteListener {
        void onPrePlayComplete(String path);

        void onPrepared();
    }

    public VideoPlayManager(Context context) {
        this.context = context;
    }

    public void playVideo(String dir, String fileName, Surface surface, IVideoView.OnPlayVideoListener listener) {
        playVideo(dir, fileName, surface, false, listener);
    }

    public synchronized void playVideo(String dir, String fileName, Surface surface, boolean loop,
                                       IVideoView.OnPlayVideoListener listener) {
        String path = dir + fileName;
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.i(TAG, "playVideo but file not exist! the filePath:" + file.getPath());
            return;
        }
        if (surface != null && surface.isValid()) {
            playVideo(path, surface, loop, listener);
            return;
        }
        LogUtil.e(TAG, "surface is invalid!");
    }

    public synchronized void playVideo(String path, Surface surface, boolean loop,
                                       final IVideoView.OnPlayVideoListener listener) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtil.i(TAG, "playVideo but file not exist! the filePath:" + file.getPath());
                return;
            }
            if (surface != null && surface.isValid()) {
                this.isPause = false;
                if (this.mediaPlayer == null) {
                    this.mediaPlayer = new MediaPlayer();
                }
                try {
                    this.mediaPlayer.reset();
                    this.mediaPlayer.setAudioStreamType(3);
                    this.mediaPlayer.setDataSource(path);
                    this.mediaPlayer.setSurface(surface);
                    this.mediaPlayer.setScreenOnWhilePlaying(false);
                    this.mediaPlayer.setLooping(loop);
                    this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer player) {
                            player.start();
                            if (listener != null) {
                                listener.beginRenderFirstFrame();
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
            LogUtil.e(TAG, "surface is invalid!");
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

    public synchronized void prePlayVideo(final String path, Surface surface,
                                          final LivePhotoPrePlayCompleteListener listener) {
        File file = new File(path);
        if (!file.exists()) {
            LogUtil.i(TAG, "playVideo but file not exist! the filePath:" + file.getPath());
            return;
        }
        if (surface != null && surface.isValid()) {
            this.isPause = false;
            if (this.mediaPlayer == null) {
                this.mediaPlayer = new MediaPlayer();
            }
            try {
                this.mediaPlayer.reset();
                this.mediaPlayer.setAudioStreamType(3);
                this.mediaPlayer.setDataSource(path);
                this.mediaPlayer.setSurface(surface);
                this.mediaPlayer.setScreenOnWhilePlaying(false);
                this.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        LogUtil.i(TAG, "mediaPlayer  onPrepared");
                        player.seekTo(PRE_PLAY_SEEK_MS);
                        player.start();
                        if (listener != null) {
                            listener.onPrepared();
                        }
                        Observable.timer(PRE_PLAY_DURATION_MS, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<Long>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                    }

                                    @Override
                                    public void onNext(Long value) {
                                        if (listener != null) {
                                            listener.onPrePlayComplete(path);
                                        } else {
                                            pauseVideo();
                                        }
                                    }
                                });
                    }
                });
                this.mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, "playVideo " + e.toString());
            }
            return;
        }
        LogUtil.e(TAG, "surface is invalid!");
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        MediaPlayer player = this.mediaPlayer;
        if (player != null) {
            player.setOnCompletionListener(listener);
        }
    }

    public synchronized void stopVideo() {
        this.isPause = false;
        if (this.mediaPlayer != null) {
            if (this.mediaPlayer.isPlaying()) {
                this.mediaPlayer.stop();
            }
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    public synchronized void pauseVideo() {
        if (this.mediaPlayer == null) {
            return;
        }
        if (this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.pause();
            this.isPause = true;
        }
    }

    public synchronized void continuePlayVideo() {
        if (this.mediaPlayer == null) {
            return;
        }
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
}
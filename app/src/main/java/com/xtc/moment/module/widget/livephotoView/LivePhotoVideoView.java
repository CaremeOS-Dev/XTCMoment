package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.view.Surface;

import com.xtc.log.LogUtil;

/**
 * Live photo video view that can pre-play a clip and pause on its last frame.
 *
 * <p>Pre-play is used by the photo pager: each page silently warms up its clip, and when the pre-play
 * finishes the clip is paused so the user sees a still frame until they tap the photo.
 */
public class LivePhotoVideoView extends DefaultPlayVideoView {

    private static final String TAG = LivePhotoVideoView.class.getSimpleName();

    /** Path of the clip currently being pre-played. */
    private String currentPrePlayVideoPath;
    /** True while the user is actually watching the clip (not just pre-playing it). */
    private boolean isAllPlay;

    public LivePhotoVideoView(Context context, PlayVideoTextureView textureView, SurfaceTexture surfaceTexture,
                              Surface surface) {
        super(context, textureView, surfaceTexture, surface);
        this.isAllPlay = false;
    }

    /** Pre-plays {@code filePath} and pauses it on the last frame. */
    public void prePlayVideo(String filePath, final IVideoView.OnPlayVideoListener listener) {
        this.isAllPlay = false;
        this.currentPrePlayVideoPath = filePath;
        removeScreenOffMsg();
        makeScreenOn();
        delayScreenOff();
        this.mediaManager.prePlayVideo(filePath, this.surface, new VideoPlayManager.LivePhotoPrePlayCompleteListener() {
            @Override
            public void onPrePlayComplete(String completedPath) {
                if (TextUtils.isEmpty(currentPrePlayVideoPath) || TextUtils.isEmpty(completedPath)) {
                    return;
                }
                LogUtil.i(TAG, "预播放视频完成暂停播放");
                if (!currentPrePlayVideoPath.equals(completedPath) || isAllPlay) {
                    if (isAllPlay) {
                        LogUtil.i(TAG, "当前正在正常播放此实况照片,不做处理");
                        return;
                    }
                    LogUtil.i(TAG, "当前预播放的视频，与回调播放完成视频不一致，不做处理 currentPrePlayVideoPath："
                            + currentPrePlayVideoPath + " filePath:" + completedPath);
                    return;
                }
                pausePlayVideo();
                if (listener != null) {
                    listener.onCompletion();
                }
            }

            @Override
            public void onPrepared() {
                if (listener != null) {
                    LogUtil.d(TAG, "prePlayVideo beginRenderFirstFrame");
                    listener.beginRenderFirstFrame();
                }
            }
        });
        setPlaying(true);
        LogUtil.d(TAG, "playVideo videoView:" + this + " filePath:" + filePath);
    }

    @Override
    public void playVideo(String path, boolean loop, IVideoView.OnPlayVideoListener listener,
                          IVideoView.OnBufferingListener bufferingListener) {
        this.isAllPlay = true;
        super.playVideo(path, loop, listener, bufferingListener);
    }

    @Override
    public void stopVideo() {
        this.isAllPlay = false;
        super.stopVideo();
    }
}
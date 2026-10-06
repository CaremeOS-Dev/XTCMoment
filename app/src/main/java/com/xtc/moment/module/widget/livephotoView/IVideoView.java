package com.xtc.moment.module.widget.livephotoView;

import android.graphics.Bitmap;
import android.view.View;

/**
 * 实况照片视频播放器接口。
 */
public interface IVideoView {

    interface OnPlayVideoListener {
        void beginRenderFirstFrame();

        void onCompletion();
    }

    interface OnBufferingListener {
        void onBufferingUpdate(int progress);

        void onBufferingFinish(String path);
    }

    interface OnPlayInfoListener {
        void onPlaying(int progress);

        void onPlayFailed(int code);
    }

    void playVideo(String videoPath, boolean loop, OnPlayVideoListener listener, OnBufferingListener bufferingListener);

    void playVideo(String videoPath, String coverPath, boolean loop, OnPlayVideoListener listener);

    void continuePlayVideo();

    void pausePlayVideo();

    void stopVideo();

    void resetVideoView();

    void destroy();

    boolean isPlaying();

    Bitmap getCurrentFrameBitmap();

    void setCoverView(View coverView);

    void setOnPlayInfoListener(OnPlayInfoListener listener);
}
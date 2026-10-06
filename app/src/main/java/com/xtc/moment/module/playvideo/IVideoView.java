package com.xtc.moment.module.playvideo;

import android.graphics.Bitmap;

/**
 * 播放页使用的视频视图接口，除基础播放控制外还暴露首帧、错误与进度回调。
 */
public interface IVideoView {

    interface OnBufferingListener {
        void onBufferingFinish(String path);

        void onBufferingUpdate(int progress);
    }

    interface OnPlayInfoListener {
        void onPlayFailed(int code);

        void onPlaying(int progress);
    }

    interface OnPlayVideoListener {
        void beginRenderFirstFrame(long delay);

        void onCompletion();

        void onError(int code);
    }

    void continuePlayVideo();

    void destroy();

    Bitmap getCurrentFrameBitmap();

    void pausePlayVideo();

    void pausePlayVideoWithReset();

    void playVideo(String dir, String fileName, boolean loop, OnPlayVideoListener listener);

    void playVideo(String path, boolean loop, OnPlayVideoListener listener, OnBufferingListener bufferingListener);

    void setOnPlayInfoListener(OnPlayInfoListener listener);

    void stopVideo();
}
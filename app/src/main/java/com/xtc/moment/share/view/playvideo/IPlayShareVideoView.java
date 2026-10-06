package com.xtc.moment.share.view.playvideo;

import android.graphics.Bitmap;

import com.xtc.architecture.mvp.core.MvpView;

/**
 * 分享视频播放视图接口。
 */
public interface IPlayShareVideoView extends MvpView {

    void playVideo();

    void pausePlayVideo();

    void replayVideo(boolean replay);

    void continuePlayVideo(boolean continuePlay);

    void bufferOnlineVideo(int progress);

    void bufferComplete(String path);

    void bufferCompleteThumbnailPath(String path);

    void updatePlayingProgress(int progress);

    void playVideoComplete();

    void playVideoFailed(boolean failed);

    void dealScreenOff(Bitmap bitmap);

    void beginRenderFirstFrame(boolean firstFrame);

    void enableToPreview();

    void canPauseVideo();

    void startToRecordActivity();
}
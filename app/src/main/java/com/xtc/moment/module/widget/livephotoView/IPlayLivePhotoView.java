package com.xtc.moment.module.widget.livephotoView;

import com.xtc.architecture.mvp.core.MvpView;

/**
 * 实况照片播放视图接口。
 */
public interface IPlayLivePhotoView extends MvpView {
    void downLoadSuccess(String path);

    void downLoadError(String message);
}
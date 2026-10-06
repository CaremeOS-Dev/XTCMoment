package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.moment.serve.DownloadServe;

/**
 * Presenter for {@link IPlayLivePhotoView}: downloads the mp4 side of a live photo and reports the
 * result back to the view.
 */
public class PlayLivePhotoPresenter extends MvpBasePresenter<IPlayLivePhotoView> {

    private final Context context;
    private final DownloadServe downloadServe = DownloadServe.getInstance();

    public PlayLivePhotoPresenter(Context context) {
        this.context = context;
    }

    public void startDownload(String url, String savePath) {
        IPlayLivePhotoView view;
        if ((TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath))
                && (view = getView()) != null && isViewAttached()) {
            view.downLoadError(savePath);
        }
        this.downloadServe.startDownload(url, savePath, new DownloadServe.DownLoadInfoListener() {
            @Override
            public void onSuccess(String path) {
                IPlayLivePhotoView view = getView();
                if (view == null || !isViewAttached()) {
                    return;
                }
                view.downLoadSuccess(path);
            }

            @Override
            public void onFail(String message) {
                IPlayLivePhotoView view = getView();
                if (view == null || !isViewAttached()) {
                    return;
                }
                view.downLoadError(message);
            }
        });
    }
}
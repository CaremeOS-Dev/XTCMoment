package com.xtc.moment.module.publish.location;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;

/**
 * 位置发布 Presenter。
 */
class PublishLocationPresenter extends MvpBasePresenter<IPublishLocationView> {

    private static final String TAG = PublishLocationPresenter.class.getSimpleName();

    private final Context mContext;

    public PublishLocationPresenter(Context context) {
        this.mContext = context.getApplicationContext();
    }
}
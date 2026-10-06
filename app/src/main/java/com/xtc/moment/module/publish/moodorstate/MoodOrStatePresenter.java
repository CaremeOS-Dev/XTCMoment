package com.xtc.moment.module.publish.moodorstate;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.IMomentTemplateServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.MomentTemplateServeImpl;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Presenter of the mood/state picker: loads the templates of one type from the local database.
 */
public class MoodOrStatePresenter extends MvpBasePresenter<IMoodOrStateView> {

    private static final String TAG = MoodOrStatePresenter.class.getSimpleName();

    private final Context context;
    private final IMomentServe iMomentServe;
    private final IMomentTemplateServe iMomentTemplateServe;

    public MoodOrStatePresenter(Context context) {
        this.context = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.iMomentTemplateServe = MomentTemplateServeImpl.getInstance(context);
    }

    public void getTemplatesByTypeFromDb(int type) {
        this.iMomentTemplateServe.getTemplatesByTypeFromDb(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DbTemplate>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.d(TAG, "getTemplatesByTypeFromDb onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, throwable);
                        if (!isViewAttached() || getView() == null) {
                            return;
                        }
                        getView().loadFail();
                    }

                    @Override
                    public void onNext(List<DbTemplate> templates) {
                        if (!isViewAttached() || getView() == null) {
                            return;
                        }
                        getView().loadSuccess(templates);
                    }
                });
    }
}
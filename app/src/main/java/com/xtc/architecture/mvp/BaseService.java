package com.xtc.architecture.mvp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xtc.architecture.mvp.core.IInitProcess;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/** Base service that owns a presenter. */
public abstract class BaseService<V extends MvpView, P extends MvpPresenter<V>>
        extends Service
        implements IInitProcess, MvpView {

    protected P presenter;

    public abstract P createPresenter();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.presenter = createPresenter();
        this.presenter.attachView((V) this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.detachView();
    }

    public P getPresenter() {
        return this.presenter;
    }
}

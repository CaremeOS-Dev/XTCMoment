package com.xtc.architecture.mvp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xtc.architecture.mvp.core.IInitProcess;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/** Base fragment that owns a presenter directly. */
public abstract class BaseFragment<V extends MvpView, P extends MvpPresenter<V>>
        extends Fragment
        implements IInitProcess, MvpView {

    protected P presenter;

    public abstract P createPresenter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

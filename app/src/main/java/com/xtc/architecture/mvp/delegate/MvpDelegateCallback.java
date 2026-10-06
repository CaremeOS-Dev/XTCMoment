package com.xtc.architecture.mvp.delegate;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/** The view side of the MVP contract, as seen by the delegate. */
public interface MvpDelegateCallback<V extends MvpView, P extends MvpPresenter<V>> {

    P createPresenter();

    V getMvpView();

    P getPresenter();

    boolean isRetainInstance();

    void setPresenter(P presenter);

    void setRetainInstance(boolean retainInstance);

    boolean shouldInstanceBeRetained();
}

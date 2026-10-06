package com.xtc.architecture.mvp.delegate;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/** Activity-specific additions to {@link MvpDelegateCallback}. */
public interface ActivityMvpDelegateCallback<V extends MvpView, P extends MvpPresenter<V>> extends MvpDelegateCallback<V, P> {

    Object getLastCustomNonConfigurationInstance();

    Object getNonMosbyLastCustomNonConfigurationInstance();

    Object onRetainNonMosbyCustomNonConfigurationInstance();
}

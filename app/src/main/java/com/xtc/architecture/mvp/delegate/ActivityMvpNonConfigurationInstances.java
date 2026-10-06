package com.xtc.architecture.mvp.delegate;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * Carrier that survives a configuration change.
 *
 * <p>Holds the retained presenter plus whatever the view wanted to keep.
 */
class ActivityMvpNonConfigurationInstances<V extends MvpView, P extends MvpPresenter<V>> {

    /** Retained presenter, or null when the view is not retaining. */
    P presenter;

    /** The view's own retained state. */
    Object nonMosbyCustomNonConfigurationInstance;

    ActivityMvpNonConfigurationInstances(P presenter, Object nonMosbyCustomNonConfigurationInstance) {
        this.presenter = presenter;
        this.nonMosbyCustomNonConfigurationInstance = nonMosbyCustomNonConfigurationInstance;
    }
}

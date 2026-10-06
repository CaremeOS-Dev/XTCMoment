package com.xtc.architecture.mvp.delegate;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * Shared MVP glue used by both the activity and fragment delegates: creates the
 * presenter on first attach and binds/unbinds the view.
 */
class MvpInternalDelegate<V extends MvpView, P extends MvpPresenter<V>> {

    protected MvpDelegateCallback<V, P> callback;

    MvpInternalDelegate(MvpDelegateCallback<V, P> callback) {
        if (callback == null) {
            throw new NullPointerException("MvpDelegateCallback is null!");
        }
        this.callback = callback;
    }

    /** Creates the presenter if needed and installs it on the callback. */
    void createPresenter() {
        MvpPresenter presenter = this.callback.getPresenter();
        if (presenter == null) {
            presenter = this.callback.createPresenter();
        }
        if (presenter == null) {
            throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
        }
        this.callback.setPresenter((P) presenter);
    }

    void attachView() {
        getPresenterOrThrow().attachView(this.callback.getMvpView());
    }

    void detachView() {
        getPresenterOrThrow().detachView();
    }

    private P getPresenterOrThrow() {
        P presenter = this.callback.getPresenter();
        if (presenter != null) {
            return presenter;
        }
        throw new NullPointerException("Presenter returned from getPresenter() is null");
    }
}

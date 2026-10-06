package com.xtc.architecture.mvp.core;

/**
 * Base presenter: holds a strong reference to its view.
 *
 * <p>Because the reference is strong, a view must call {@link #detachView()}
 * when it is destroyed to avoid leaking.
 */
public class MvpPresenter<V extends MvpView> {

    private V view;

    public void attachView(V view) {
        this.view = view;
    }

    public V getView() {
        return this.view;
    }

    public void detachView() {
        this.view = null;
    }

    public boolean isViewAttached() {
        return this.view != null;
    }
}

package com.xtc.architecture.mvp.core;

import java.lang.ref.WeakReference;

/**
 * Presenter that holds its view weakly, so it survives a configuration change
 * without leaking the old activity.
 *
 * @deprecated Prefer {@link MvpPresenter}; kept because older screens use it.
 */
@Deprecated
public class MvpBasePresenter<V extends MvpView> extends MvpPresenter<V> {

    private WeakReference<V> viewRef;

    @Override
    public void attachView(V view) {
        this.viewRef = new WeakReference<V>(view);
    }

    @Override
    public V getView() {
        WeakReference<V> ref = this.viewRef;
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    @Override
    public boolean isViewAttached() {
        WeakReference<V> ref = this.viewRef;
        return ref != null && ref.get() != null;
    }

    @Override
    public void detachView() {
        detachView(true);
    }

    @Deprecated
    public void detachView(boolean retainInstance) {
        WeakReference<V> ref = this.viewRef;
        if (ref != null) {
            ref.clear();
            this.viewRef = null;
        }
    }
}

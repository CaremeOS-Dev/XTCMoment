package com.xtc.architecture.mvp.delegate;

import android.os.Bundle;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/**
 * Default {@link ActivityMvpDelegate} used by {@code MvpActivity}.
 *
 * <p>Only creation and destruction touch the presenter; the remaining
 * lifecycle hooks are intentionally empty so subclasses can override them.
 */
public class ActivityMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>> implements ActivityMvpDelegate<V, P> {

    protected MvpInternalDelegate<V, P> internalDelegate;
    protected ActivityMvpDelegateCallback<V, P> callback;

    public ActivityMvpDelegateImpl(ActivityMvpDelegateCallback<V, P> callback) {
        if (callback == null) {
            throw new NullPointerException("MvpDelegateCallback is null!");
        }
        this.callback = callback;
    }

    protected MvpInternalDelegate<V, P> getInternalDelegate() {
        if (this.internalDelegate == null) {
            this.internalDelegate = new MvpInternalDelegate<V, P>(this.callback);
        }
        return this.internalDelegate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ActivityMvpNonConfigurationInstances<V, P> instances =
                (ActivityMvpNonConfigurationInstances<V, P>) this.callback.getLastCustomNonConfigurationInstance();
        if (instances != null && instances.presenter != null) {
            this.callback.setPresenter(instances.presenter);
        } else {
            getInternalDelegate().createPresenter();
        }
        getInternalDelegate().attachView();
    }

    @Override
    public void onDestroy() {
        getInternalDelegate().detachView();
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onRestart() {
    }

    @Override
    public void onContentChanged() {
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        P presenter = this.callback.shouldInstanceBeRetained() ? this.callback.getPresenter() : null;
        Object retained = this.callback.onRetainNonMosbyCustomNonConfigurationInstance();
        if (presenter == null && retained == null) {
            return null;
        }
        return new ActivityMvpNonConfigurationInstances<V, P>(presenter, retained);
    }

    @Override
    public Object getNonMosbyLastCustomNonConfigurationInstance() {
        ActivityMvpNonConfigurationInstances<V, P> instances =
                (ActivityMvpNonConfigurationInstances<V, P>) this.callback.getLastCustomNonConfigurationInstance();
        if (instances == null) {
            return null;
        }
        return instances.nonMosbyCustomNonConfigurationInstance;
    }
}

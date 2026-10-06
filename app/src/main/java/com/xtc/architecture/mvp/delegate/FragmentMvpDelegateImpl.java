package com.xtc.architecture.mvp.delegate;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

/** Default {@link FragmentMvpDelegate} used by {@code MvpFragment}. */
public class FragmentMvpDelegateImpl<V extends MvpView, P extends MvpPresenter<V>> implements FragmentMvpDelegate<V, P> {

    protected MvpDelegateCallback<V, P> callback;
    protected MvpInternalDelegate<V, P> internalDelegate;

    /** Set once the view has been created; guards against headless usage. */
    private boolean viewCreated = false;

    public FragmentMvpDelegateImpl(MvpDelegateCallback<V, P> callback) {
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
    }

    @Override
    public void onCreateView(View view, Bundle savedInstanceState) {
        getInternalDelegate().createPresenter();
        getInternalDelegate().attachView();
        this.viewCreated = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        if (this.viewCreated) {
            return;
        }
        throw new IllegalStateException("It seems that you are using "
                + this.callback.getClass().getCanonicalName()
                + " as headless (UI less) fragment (because onViewCreated() has not been called or maybe delegation misses that part). "
                + "Having a Presenter without a View (UI) doesn't make sense. "
                + "Simply use an usual fragment instead of an MvpFragment if you want to use a UI less Fragment");
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onDestroyView() {
        getInternalDelegate().detachView();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onAttach(Activity activity) {
    }

    @Override
    public void onDetach() {
    }
}

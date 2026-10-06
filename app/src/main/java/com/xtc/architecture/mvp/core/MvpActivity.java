package com.xtc.architecture.mvp.core;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.architecture.mvp.delegate.ActivityMvpDelegate;
import com.xtc.architecture.mvp.delegate.ActivityMvpDelegateCallback;
import com.xtc.architecture.mvp.delegate.ActivityMvpDelegateImpl;
import com.xtc.architecture.mvp.util.PermissionUtil;

/**
 * Base activity that wires the MVP presenter to the lifecycle and exposes a
 * runtime-permission helper.
 */
public abstract class MvpActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends FragmentActivity
        implements IPermissionInfo, MvpView, ActivityMvpDelegateCallback<V, P> {

    private PermissionListener mListener;
    protected ActivityMvpDelegate<V, P> mvpDelegate;
    protected P presenter;
    protected boolean retainInstance;

    @Override
    public abstract P createPresenter();

    @Override
    public String getCtaTitle() {
        return "";
    }

    @Override
    public V getMvpView() {
        return (V) this;
    }

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public boolean needCheckPermissionReminder() {
        return false;
    }

    @Override
    public Object onRetainNonMosbyCustomNonConfigurationInstance() {
        return null;
    }

    @Override
    public void setCheckPermissionReminder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
        PermissionUtil.checkPermissionReminder(this, getIntent(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMvpDelegate().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getMvpDelegate().onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMvpDelegate().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMvpDelegate().onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMvpDelegate().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpDelegate().onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMvpDelegate().onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        getMvpDelegate().onContentChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getMvpDelegate().onPostCreate(savedInstanceState);
    }

    protected ActivityMvpDelegate<V, P> getMvpDelegate() {
        if (this.mvpDelegate == null) {
            this.mvpDelegate = new ActivityMvpDelegateImpl<V, P>(this);
        }
        return this.mvpDelegate;
    }

    @Override
    public P getPresenter() {
        return this.presenter;
    }

    @Override
    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isRetainInstance() {
        return this.retainInstance;
    }

    @Override
    public boolean shouldInstanceBeRetained() {
        return this.retainInstance && isChangingConfigurations();
    }

    @Override
    public void setRetainInstance(boolean retainInstance) {
        this.retainInstance = retainInstance;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        return getMvpDelegate().onRetainCustomNonConfigurationInstance();
    }

    @Override
    public final Object getNonMosbyLastCustomNonConfigurationInstance() {
        return getMvpDelegate().getNonMosbyLastCustomNonConfigurationInstance();
    }

    public void requestRunTimePermission(String[] permissions, PermissionListener permissionListener) {
        this.mListener = permissionListener;
        PermissionUtil.requestPermissions(permissions, permissionListener, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults == null || grantResults.length == 0) {
            finish();
        } else {
            PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this.mListener);
        }
    }
}

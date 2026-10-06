package com.xtc.architecture.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.xtc.architecture.mvp.core.IInitProcess;
import com.xtc.architecture.mvp.core.IPermissionInfo;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.architecture.mvp.util.PermissionUtil;

/**
 * Base activity that owns a presenter directly (no lifecycle delegate).
 *
 * <p>Lighter than {@code MvpActivity}: the presenter is created in
 * {@code onCreate} and detached in {@code onDestroy}.
 */
public abstract class BaseActivity<V extends MvpView, P extends MvpPresenter<V>>
        extends Activity
        implements IInitProcess, IPermissionInfo, MvpView {

    private static final String TAG = "BaseActivity";

    private PermissionListener mListener;
    protected P presenter;

    public abstract P createPresenter();

    @Override
    public String getCtaTitle() {
        return "";
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
    public void setCheckPermissionReminder() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        init();
    }

    private void init() {
        this.presenter = createPresenter();
        this.presenter.attachView((V) this);
        PermissionUtil.checkPermissionReminder(this, getIntent(), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.presenter.detachView();
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

    public P getPresenter() {
        return this.presenter;
    }
}

package com.xtc.moment.share.view;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.share.callback.IShareCallback;
import com.xtc.moment.share.presenter.AbstractSharePresenter;

/**
 * 分享页面基类。
 */
public abstract class AbstractShareActivity<V extends MvpView, P extends AbstractSharePresenter<V>>
        extends BaseActivity<V, P> {

    protected static final String TAG = "Share_Msg_AbstractShareActivity";

    protected ShareDialogManager shareDialogManager;

    protected abstract IShareCallback createShareCallback();

    @Override
    public void initData() {
        this.shareDialogManager = new ShareDialogManager(this, createShareCallback());
    }
}
package com.xtc.moment.serve;

import android.os.RemoteException;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.personalitydress.aidl.IDressCallback;
import com.xtc.personalitydress.aidl.IDressIdCallback;
import com.xtc.personalitydress.aidl.IDressServiceBinder;

import java.util.List;

/**
 * 远程装扮服务的代理，把调用切到后台线程执行。
 */
public class DressProxy {

    private static final String TAG = DressProxy.class.getSimpleName();

    private IDressServiceBinder binder;

    public DressProxy(IDressServiceBinder binder) {
        this.binder = binder;
    }

    public IDressServiceBinder getBinder() {
        return this.binder;
    }

    public void getRemoteDress(final String dressId, final int type, final IDressCallback callback) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d(DressProxy.TAG, "getRemoteDress dressId:" + dressId + "type:" + type);
                    DressProxy.this.binder.getRemoteDress(dressId, type, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getDressIdByWatchId(final List<String> watchIds, final int type, final IDressIdCallback callback) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.d(DressProxy.TAG, "getDressIdByWatchId dressId:" + watchIds + "type:" + type);
                    DressProxy.this.binder.getDressIdByWatchId(watchIds, type, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void notifyDressExpired(final String watchId, final int type) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    DressProxy.this.binder.notifyDressExpired(watchId, type);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
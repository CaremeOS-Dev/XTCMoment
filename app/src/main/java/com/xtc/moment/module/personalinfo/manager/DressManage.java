package com.xtc.moment.module.personalinfo.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.xtc.funlist.util.FuncUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.serve.DressProxy;
import com.xtc.moment.serve.impl.DressServeImpl;
import com.xtc.moment.serve.interfaces.IDressServe;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.personalitydress.aidl.IDressServiceBinder;

import java.lang.ref.WeakReference;

/**
 * 装扮服务连接管理：绑定/解绑远程装扮服务，并在 binder 死亡时自动重连。
 */
public class DressManage {

    private static final String TAG = "DressManage";
    private static volatile DressManage mInstance;

    private volatile boolean bindDressResult;
    private Context mContext;
    private IBinder.DeathRecipient mDeathRecipient;
    private ServiceConnection mDressConnection;
    private IDressServe mDressServe;

    public interface DressServiceListener {
        void onServiceConnected();

        void onServiceDisconnected();
    }

    private DressManage(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDressServe = new DressServeImpl(this.mContext) {
            @Override
            public void bindService() {
                DressManage.this.releaseDressConnect();
                DressManage.this.bindDressService(new DressServiceListener() {
                    @Override
                    public void onServiceDisconnected() {
                    }

                    @Override
                    public void onServiceConnected() {
                        LogUtil.i(DressManage.TAG, "DressServeImpl 装扮服务连接成功");
                    }
                });
            }
        };
    }

    public static DressManage getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DressManage.class) {
                if (mInstance == null) {
                    mInstance = new DressManage(context);
                }
            }
        }
        return mInstance;
    }

    public IDressServe getDressServe() {
        return this.mDressServe;
    }

    public void bindDressService(DressServiceListener listener) {
        if (!FuncUtil.supportPersonalityDress() || listener == null) {
            LogUtil.w(TAG, "don't bindDressService now model maybe not support");
            return;
        }
        if (this.bindDressResult) {
            listener.onServiceConnected();
            LogUtil.d(TAG, "Dress service connect success,callback onServiceConnected : " + listener);
            return;
        }
        final WeakReference<DressServiceListener> listenerRef = new WeakReference<>(listener);
        final Intent intent = new Intent("com.xtc.personalitydress.service.DressService");
        intent.setPackage("com.xtc.theme");
        this.mDressConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    service.linkToDeath(DressManage.this.mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                DressManage.this.mDressServe.setProxy(new DressProxy(IDressServiceBinder.Stub.asInterface(service)));
                DressManage.this.bindDressResult = true;
                LogUtil.d(DressManage.TAG, "Dress service connect success!");
                DressServiceListener callback = listenerRef.get();
                if (callback == null) {
                    LogUtil.d(DressManage.TAG, "onServiceConnected but listener is null");
                } else {
                    callback.onServiceConnected();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.d(DressManage.TAG, "Dress service Disconnected!");
                DressServiceListener callback = listenerRef.get();
                if (callback == null) {
                    LogUtil.d(DressManage.TAG, "onServiceConnected but listener is null");
                } else {
                    callback.onServiceDisconnected();
                }
            }
        };
        this.mDeathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                DressManage.this.releaseDressConnect();
                DressManage.this.bindDressService(new DressServiceListener() {
                    @Override
                    public void onServiceDisconnected() {
                    }

                    @Override
                    public void onServiceConnected() {
                        LogUtil.i(DressManage.TAG, "装扮服务重连成功");
                    }
                });
                LogUtil.i(DressManage.TAG, "dress binder death, link to death");
            }
        };
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DressManage.this.bindDressResult = DressManage.this.mContext.bindService(intent, DressManage.this.mDressConnection, Context.BIND_AUTO_CREATE);
            }
        });
    }

    public boolean isBindDressResult() {
        return this.bindDressResult;
    }

    public void releaseDressConnect() {
        this.mDressServe.releaseDeathRecipient(this.mDeathRecipient);
        try {
            if (this.mDressConnection != null && this.bindDressResult) {
                this.mContext.unbindService(this.mDressConnection);
                this.bindDressResult = false;
            }
        } catch (Exception e) {
            LogUtil.i(TAG, "releaseDressConnect error ex:" + e);
        }
        LogUtil.i(TAG, "releaseDressConnect -->");
    }
}
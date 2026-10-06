package com.xtc.nsfw.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 鉴黄结果回调接口（AIDL 生成的手写版本）。
 */
public interface INsfwCallBackAidlInterface extends IInterface {

    void onCallbackNSFWResult(String bindPackageName, NSFWUpload result, boolean unbindService) throws RemoteException;

    /**
     * 默认实现，便于跨进程调用失败时降级。
     */
    class Default implements INsfwCallBackAidlInterface {

        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onCallbackNSFWResult(String bindPackageName, NSFWUpload result, boolean unbindService) throws RemoteException {
        }
    }

    /**
     * Binder 服务端基类。
     */
    abstract class Stub extends Binder implements INsfwCallBackAidlInterface {

        private static final String DESCRIPTOR = "com.xtc.nsfw.aidl.INsfwCallBackAidlInterface";
        static final int TRANSACTION_onCallbackNSFWResult = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INsfwCallBackAidlInterface asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local != null && (local instanceof INsfwCallBackAidlInterface)) {
                return (INsfwCallBackAidlInterface) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_onCallbackNSFWResult) {
                data.enforceInterface(DESCRIPTOR);
                onCallbackNSFWResult(data.readString(),
                        data.readInt() != 0 ? NSFWUpload.CREATOR.createFromParcel(data) : null,
                        data.readInt() != 0);
                reply.writeNoException();
                return true;
            }
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        /**
         * 客户端代理实现。
         */
        private static class Proxy implements INsfwCallBackAidlInterface {

            private static INsfwCallBackAidlInterface defaultImpl;

            private final IBinder remote;

            Proxy(IBinder remote) {
                this.remote = remote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override
            public IBinder asBinder() {
                return this.remote;
            }

            @Override
            public void onCallbackNSFWResult(String bindPackageName, NSFWUpload result, boolean unbindService) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(bindPackageName);
                    if (result != null) {
                        data.writeInt(1);
                        result.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    data.writeInt(unbindService ? 1 : 0);
                    if (!this.remote.transact(TRANSACTION_onCallbackNSFWResult, data, reply, 0) && defaultImpl != null) {
                        defaultImpl.onCallbackNSFWResult(bindPackageName, result, unbindService);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(INsfwCallBackAidlInterface impl) {
            if (Proxy.defaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl == null) {
                return false;
            }
            Proxy.defaultImpl = impl;
            return true;
        }

        public static INsfwCallBackAidlInterface getDefaultImpl() {
            return Proxy.defaultImpl;
        }
    }
}
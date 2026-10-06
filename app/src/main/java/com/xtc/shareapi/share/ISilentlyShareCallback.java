package com.xtc.shareapi.share;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 静默分享（分享到时光记忆）结果的 AIDL 回调。
 */
public interface ISilentlyShareCallback extends IInterface {

    /** 默认实现，不做任何处理。 */
    class Default implements ISilentlyShareCallback {

        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onResult(int resultCode, String message) throws RemoteException {
        }
    }

    /** 分享结果回调。 */
    void onResult(int resultCode, String message) throws RemoteException;

    /** Binder 桩实现。 */
    abstract class Stub extends Binder implements ISilentlyShareCallback {

        private static final String DESCRIPTOR = "com.xtc.shareapi.share.ISilentlyShareCallback";
        static final int TRANSACTION_ON_RESULT = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        /** 根据 Binder 获取本地或代理实现。 */
        public static ISilentlyShareCallback asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof ISilentlyShareCallback) {
                return (ISilentlyShareCallback) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_ON_RESULT) {
                data.enforceInterface(DESCRIPTOR);
                onResult(data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            }
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        /** 跨进程代理实现。 */
        private static class Proxy implements ISilentlyShareCallback {

            private static ISilentlyShareCallback defaultImpl;
            private final IBinder remote;

            Proxy(IBinder remote) {
                this.remote = remote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public IBinder asBinder() {
                return remote;
            }

            @Override
            public void onResult(int resultCode, String message) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(resultCode);
                    data.writeString(message);
                    boolean handled = remote.transact(TRANSACTION_ON_RESULT, data, reply, 0);
                    if (!handled && defaultImpl != null) {
                        defaultImpl.onResult(resultCode, message);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(ISilentlyShareCallback impl) {
            if (Proxy.defaultImpl != null || impl == null) {
                return false;
            }
            Proxy.defaultImpl = impl;
            return true;
        }

        public static ISilentlyShareCallback getDefaultImpl() {
            return Proxy.defaultImpl;
        }
    }
}
package com.xtc.shareapi.share.manager;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 分享到微聊结果的 AIDL 回调。
 */
public interface IShareCallback extends IInterface {

    /** 分享结果回调。 */
    void onResult(int resultCode, String message) throws RemoteException;

    /** Binder 桩实现。 */
    abstract class Stub extends Binder implements IShareCallback {

        private static final String DESCRIPTOR = "com.xtc.shareapi.share.manager.IShareCallback";
        static final int TRANSACTION_ON_RESULT = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        /** 根据 Binder 获取本地或代理实现。 */
        public static IShareCallback asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof IShareCallback) {
                return (IShareCallback) local;
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
        private static class Proxy implements IShareCallback {

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
                    remote.transact(TRANSACTION_ON_RESULT, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
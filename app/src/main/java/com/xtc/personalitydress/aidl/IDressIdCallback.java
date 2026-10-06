package com.xtc.personalitydress.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.HashMap;
import java.util.Map;

/**
 * 批量获取装扮 ID 的回调接口（AIDL 生成的手写版本）。
 */
public interface IDressIdCallback extends IInterface {

    void onSuccess(Map map) throws RemoteException;

    void onError(String message) throws RemoteException;

    /**
     * Binder 服务端基类。
     */
    abstract class Stub extends Binder implements IDressIdCallback {

        private static final String DESCRIPTOR = "com.xtc.personalitydress.aidl.IDressIdCallback";
        static final int TRANSACTION_onSuccess = 1;
        static final int TRANSACTION_onError = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDressIdCallback asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local != null && (local instanceof IDressIdCallback)) {
                return (IDressIdCallback) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_onSuccess) {
                data.enforceInterface(DESCRIPTOR);
                HashMap map = data.readHashMap(getClass().getClassLoader());
                onSuccess(map);
                reply.writeNoException();
                reply.writeMap(map);
                return true;
            }
            if (code == TRANSACTION_onError) {
                data.enforceInterface(DESCRIPTOR);
                onError(data.readString());
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
        private static class Proxy implements IDressIdCallback {

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
            public void onSuccess(Map map) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeMap(map);
                    this.remote.transact(TRANSACTION_onSuccess, data, reply, 0);
                    reply.readException();
                    reply.readMap(map, getClass().getClassLoader());
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void onError(String message) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(message);
                    this.remote.transact(TRANSACTION_onError, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
package com.xtc.im.transpond;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/** Callback of the IM HTTP-transpond channel. */
public interface ITranspondCallback extends IInterface {

    void onError(String message) throws RemoteException;

    void onSuccess(byte[] body, int code) throws RemoteException;

    /** Local-side binder implementation. */
    abstract class Stub extends Binder implements ITranspondCallback {

        private static final String DESCRIPTOR = "com.xtc.im.transpond.ITranspondCallback";
        static final int TRANSACTION_onError = 2;
        static final int TRANSACTION_onSuccess = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITranspondCallback asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof ITranspondCallback) {
                return (ITranspondCallback) local;
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
                byte[] body = data.createByteArray();
                onSuccess(body, data.readInt());
                reply.writeNoException();
                reply.writeByteArray(body);
                return true;
            }
            if (code != TRANSACTION_onError) {
                if (code == INTERFACE_TRANSACTION) {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
            data.enforceInterface(DESCRIPTOR);
            onError(data.readString());
            reply.writeNoException();
            return true;
        }

        private static class Proxy implements ITranspondCallback {
            private final IBinder remote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder remote) {
                this.remote = remote;
            }

            @Override
            public IBinder asBinder() {
                return this.remote;
            }

            @Override
            public void onSuccess(byte[] body, int code) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeByteArray(body);
                    data.writeInt(code);
                    this.remote.transact(TRANSACTION_onSuccess, data, reply, 0);
                    reply.readException();
                    reply.readByteArray(body);
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
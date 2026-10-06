package com.xtc.im.core.common.listener;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/** 语音分片发送完成监听（跨进程）。 */
public interface OnFinishListener extends IInterface {

    void onSuccess(long syncKey, String desc) throws RemoteException;

    void onCanceled() throws RemoteException;

    void onFailed(String desc) throws RemoteException;

    abstract class Stub extends Binder implements OnFinishListener {

        private static final String DESCRIPTOR = "com.xtc.im.core.common.listener.OnFinishListener";
        static final int TRANSACTION_onSuccess = 1;
        static final int TRANSACTION_onCanceled = 2;
        static final int TRANSACTION_onFailed = 3;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static OnFinishListener asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface != null && (localInterface instanceof OnFinishListener)) {
                return (OnFinishListener) localInterface;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                onSuccess(data.readLong(), data.readString());
                reply.writeNoException();
                return true;
            }
            if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                onCanceled();
                reply.writeNoException();
                return true;
            }
            if (code == 3) {
                data.enforceInterface(DESCRIPTOR);
                onFailed(data.readString());
                reply.writeNoException();
                return true;
            }
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements OnFinishListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override
            public void onSuccess(long syncKey, String desc) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(syncKey);
                    data.writeString(desc);
                    this.mRemote.transact(1, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void onCanceled() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(2, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void onFailed(String desc) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(desc);
                    this.mRemote.transact(3, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
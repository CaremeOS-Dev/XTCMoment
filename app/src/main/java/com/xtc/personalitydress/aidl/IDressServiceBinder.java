package com.xtc.personalitydress.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

/**
 * 个性装扮服务的跨进程接口（AIDL 生成的手写版本）。
 */
public interface IDressServiceBinder extends IInterface {

    RemoteDress getCurBubble() throws RemoteException;

    void notifyDressExpired(String watchId, int type) throws RemoteException;

    void getRemoteDress(String dressId, int type, IDressCallback callback) throws RemoteException;

    void getDressIdByWatchId(List<String> watchIds, int type, IDressIdCallback callback) throws RemoteException;

    /**
     * Binder 服务端基类。
     */
    abstract class Stub extends Binder implements IDressServiceBinder {

        private static final String DESCRIPTOR = "com.xtc.personalitydress.aidl.IDressServiceBinder";
        static final int TRANSACTION_getCurBubble = 1;
        static final int TRANSACTION_notifyDressExpired = 2;
        static final int TRANSACTION_getRemoteDress = 3;
        static final int TRANSACTION_getDressIdByWatchId = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IDressServiceBinder asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local != null && (local instanceof IDressServiceBinder)) {
                return (IDressServiceBinder) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_getCurBubble) {
                data.enforceInterface(DESCRIPTOR);
                RemoteDress curBubble = getCurBubble();
                reply.writeNoException();
                if (curBubble != null) {
                    reply.writeInt(1);
                    curBubble.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            }
            if (code == TRANSACTION_notifyDressExpired) {
                data.enforceInterface(DESCRIPTOR);
                notifyDressExpired(data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            }
            if (code == TRANSACTION_getRemoteDress) {
                data.enforceInterface(DESCRIPTOR);
                getRemoteDress(data.readString(), data.readInt(),
                        IDressCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            }
            if (code == TRANSACTION_getDressIdByWatchId) {
                data.enforceInterface(DESCRIPTOR);
                ArrayList<String> watchIds = data.createStringArrayList();
                getDressIdByWatchId(watchIds, data.readInt(),
                        IDressIdCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeStringList(watchIds);
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
        private static class Proxy implements IDressServiceBinder {

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
            public RemoteDress getCurBubble() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.remote.transact(TRANSACTION_getCurBubble, data, reply, 0);
                    reply.readException();
                    return reply.readInt() != 0 ? RemoteDress.CREATOR.createFromParcel(reply) : null;
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void notifyDressExpired(String watchId, int type) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(watchId);
                    data.writeInt(type);
                    this.remote.transact(TRANSACTION_notifyDressExpired, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void getRemoteDress(String dressId, int type, IDressCallback callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(dressId);
                    data.writeInt(type);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.remote.transact(TRANSACTION_getRemoteDress, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void getDressIdByWatchId(List<String> watchIds, int type, IDressIdCallback callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeStringList(watchIds);
                    data.writeInt(type);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.remote.transact(TRANSACTION_getDressIdByWatchId, data, reply, 0);
                    reply.readException();
                    reply.readStringList(watchIds);
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
package com.xtc.im.core.common.listener;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/** 消息发送结果监听（跨进程）。 */
public interface IMessageListener extends IInterface {

    void onReceive(boolean success, boolean needAck, int code, long syncKey, String desc) throws RemoteException;

    abstract class Stub extends Binder implements IMessageListener {

        private static final String DESCRIPTOR = "com.xtc.im.core.common.listener.IMessageListener";
        static final int TRANSACTION_onReceive = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMessageListener asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface != null && (localInterface instanceof IMessageListener)) {
                return (IMessageListener) localInterface;
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
                onReceive(data.readInt() != 0, data.readInt() != 0, data.readInt(), data.readLong(),
                        data.readString());
                reply.writeNoException();
                return true;
            }
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IMessageListener {
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
            public void onReceive(boolean success, boolean needAck, int code, long syncKey, String desc)
                    throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(success ? 1 : 0);
                    data.writeInt(needAck ? 1 : 0);
                    data.writeInt(code);
                    data.writeLong(syncKey);
                    data.writeString(desc);
                    this.mRemote.transact(1, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
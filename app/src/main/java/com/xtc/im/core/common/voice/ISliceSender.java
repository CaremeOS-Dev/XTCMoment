package com.xtc.im.core.common.voice;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/** 语音分片发送器（跨进程）。 */
public interface ISliceSender extends IInterface {

    String getGroupId() throws RemoteException;

    void sendRequest(int index, byte[] data, int vocTime, boolean isFin, String extra) throws RemoteException;

    abstract class Stub extends Binder implements ISliceSender {

        private static final String DESCRIPTOR = "com.xtc.im.core.common.voice.ISliceSender";
        static final int TRANSACTION_getGroupId = 1;
        static final int TRANSACTION_sendRequest = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ISliceSender asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface != null && (localInterface instanceof ISliceSender)) {
                return (ISliceSender) localInterface;
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
                String groupId = getGroupId();
                reply.writeNoException();
                reply.writeString(groupId);
                return true;
            }
            if (code == 2) {
                data.enforceInterface(DESCRIPTOR);
                sendRequest(data.readInt(), data.createByteArray(), data.readInt(), data.readInt() != 0,
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

        private static class Proxy implements ISliceSender {
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
            public String getGroupId() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(1, data, reply, 0);
                    reply.readException();
                    return reply.readString();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendRequest(int index, byte[] data2, int vocTime, boolean isFin, String extra)
                    throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(index);
                    data.writeByteArray(data2);
                    data.writeInt(vocTime);
                    data.writeInt(isFin ? 1 : 0);
                    data.writeString(extra);
                    this.mRemote.transact(2, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
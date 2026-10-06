package com.xtc.shareapi.share.manager;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 分享到微聊会话的 AIDL 服务接口。
 */
public interface IShareToChat extends IInterface {

    /** 分享到微聊会话。 */
    void shareToChat(Intent intent, int requestCode, String packageName, IShareCallback callback) throws RemoteException;

    /** Binder 桩实现。 */
    abstract class Stub extends Binder implements IShareToChat {

        private static final String DESCRIPTOR = "com.xtc.shareapi.share.manager.IShareToChat";
        static final int TRANSACTION_SHARE_TO_CHAT = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        /** 根据 Binder 获取本地或代理实现。 */
        public static IShareToChat asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof IShareToChat) {
                return (IShareToChat) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_SHARE_TO_CHAT) {
                data.enforceInterface(DESCRIPTOR);
                Intent intent = data.readInt() != 0 ? Intent.CREATOR.createFromParcel(data) : null;
                int requestCode = data.readInt();
                String packageName = data.readString();
                IShareCallback callback = IShareCallback.Stub.asInterface(data.readStrongBinder());
                shareToChat(intent, requestCode, packageName, callback);
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
        private static class Proxy implements IShareToChat {

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
            public void shareToChat(Intent intent, int requestCode, String packageName, IShareCallback callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    if (intent != null) {
                        data.writeInt(1);
                        intent.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    data.writeInt(requestCode);
                    data.writeString(packageName);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    remote.transact(TRANSACTION_SHARE_TO_CHAT, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
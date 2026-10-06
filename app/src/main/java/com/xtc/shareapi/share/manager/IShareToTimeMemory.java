package com.xtc.shareapi.share.manager;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 分享到时光记忆的 AIDL 服务接口（manager 包别名，描述符与原接口保持一致）。
 */
public interface IShareToTimeMemory extends IInterface {

    /** 分享单张图片到时光记忆。 */
    void sharePicture(String picturePath, ISilentlyShareCallback callback) throws RemoteException;

    /** Binder 桩实现。 */
    abstract class Stub extends Binder implements IShareToTimeMemory {

        private static final String DESCRIPTOR = "com.xtc.shareapi.share.IShareToTimeMemory";
        static final int TRANSACTION_SHARE_PICTURE = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        /** 根据 Binder 获取本地或代理实现。 */
        public static IShareToTimeMemory asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local instanceof IShareToTimeMemory) {
                return (IShareToTimeMemory) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == TRANSACTION_SHARE_PICTURE) {
                data.enforceInterface(DESCRIPTOR);
                sharePicture(data.readString(), ISilentlyShareCallback.Stub.asInterface(data.readStrongBinder()));
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
        private static class Proxy implements IShareToTimeMemory {

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
            public void sharePicture(String picturePath, ISilentlyShareCallback callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(picturePath);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    remote.transact(TRANSACTION_SHARE_PICTURE, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
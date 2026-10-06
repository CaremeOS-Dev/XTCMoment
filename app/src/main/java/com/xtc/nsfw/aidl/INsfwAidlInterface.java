package com.xtc.nsfw.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

/**
 * 鉴黄服务接口（AIDL 生成的手写版本）。
 */
public interface INsfwAidlInterface extends IInterface {

    boolean isSupportNSFW(String mimeType) throws RemoteException;

    void registerListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException;

    void unregisterListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException;

    void getNSFWByParcelFileDescriptor(String packageName, ParcelFileDescriptor descriptor, String fileName,
                                       String mimeType, String md5) throws RemoteException;

    void getNSFWByFilePath(String packageName, String filePath, String fileName, String mimeType,
                           String md5) throws RemoteException;

    /**
     * 默认实现，便于跨进程调用失败时降级。
     */
    class Default implements INsfwAidlInterface {

        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public boolean isSupportNSFW(String mimeType) throws RemoteException {
            return false;
        }

        @Override
        public void registerListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException {
        }

        @Override
        public void unregisterListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException {
        }

        @Override
        public void getNSFWByParcelFileDescriptor(String packageName, ParcelFileDescriptor descriptor, String fileName,
                                                  String mimeType, String md5) throws RemoteException {
        }

        @Override
        public void getNSFWByFilePath(String packageName, String filePath, String fileName, String mimeType,
                                      String md5) throws RemoteException {
        }
    }

    /**
     * Binder 服务端基类。
     */
    abstract class Stub extends Binder implements INsfwAidlInterface {

        private static final String DESCRIPTOR = "com.xtc.nsfw.aidl.INsfwAidlInterface";
        static final int TRANSACTION_isSupportNSFW = 1;
        static final int TRANSACTION_registerListener = 2;
        static final int TRANSACTION_unregisterListener = 3;
        static final int TRANSACTION_getNSFWByParcelFileDescriptor = 4;
        static final int TRANSACTION_getNSFWByFilePath = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INsfwAidlInterface asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface local = binder.queryLocalInterface(DESCRIPTOR);
            if (local != null && (local instanceof INsfwAidlInterface)) {
                return (INsfwAidlInterface) local;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_isSupportNSFW:
                    data.enforceInterface(DESCRIPTOR);
                    boolean supported = isSupportNSFW(data.readString());
                    reply.writeNoException();
                    reply.writeInt(supported ? 1 : 0);
                    return true;
                case TRANSACTION_registerListener:
                    data.enforceInterface(DESCRIPTOR);
                    registerListener(data.readString(),
                            INsfwCallBackAidlInterface.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_unregisterListener:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterListener(data.readString(),
                            INsfwCallBackAidlInterface.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getNSFWByParcelFileDescriptor:
                    data.enforceInterface(DESCRIPTOR);
                    getNSFWByParcelFileDescriptor(data.readString(),
                            data.readInt() != 0 ? ParcelFileDescriptor.CREATOR.createFromParcel(data) : null,
                            data.readString(), data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_getNSFWByFilePath:
                    data.enforceInterface(DESCRIPTOR);
                    getNSFWByFilePath(data.readString(), data.readString(), data.readString(), data.readString(),
                            data.readString());
                    reply.writeNoException();
                    return true;
                case INTERFACE_TRANSACTION:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /**
         * 客户端代理实现。
         */
        private static class Proxy implements INsfwAidlInterface {

            private static INsfwAidlInterface defaultImpl;

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
            public boolean isSupportNSFW(String mimeType) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(mimeType);
                    if (!this.remote.transact(TRANSACTION_isSupportNSFW, data, reply, 0) && defaultImpl != null) {
                        return defaultImpl.isSupportNSFW(mimeType);
                    }
                    reply.readException();
                    return reply.readInt() != 0;
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void registerListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(packageName);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.remote.transact(TRANSACTION_registerListener, data, reply, 0) && defaultImpl != null) {
                        defaultImpl.registerListener(packageName, callback);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void unregisterListener(String packageName, INsfwCallBackAidlInterface callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(packageName);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (!this.remote.transact(TRANSACTION_unregisterListener, data, reply, 0) && defaultImpl != null) {
                        defaultImpl.unregisterListener(packageName, callback);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void getNSFWByParcelFileDescriptor(String packageName, ParcelFileDescriptor descriptor,
                                                      String fileName, String mimeType, String md5) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(packageName);
                    if (descriptor != null) {
                        data.writeInt(1);
                        descriptor.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    data.writeString(fileName);
                    data.writeString(mimeType);
                    data.writeString(md5);
                    if (!this.remote.transact(TRANSACTION_getNSFWByParcelFileDescriptor, data, reply, 0) && defaultImpl != null) {
                        defaultImpl.getNSFWByParcelFileDescriptor(packageName, descriptor, fileName, mimeType, md5);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void getNSFWByFilePath(String packageName, String filePath, String fileName, String mimeType,
                                          String md5) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(Stub.DESCRIPTOR);
                    data.writeString(packageName);
                    data.writeString(filePath);
                    data.writeString(fileName);
                    data.writeString(mimeType);
                    data.writeString(md5);
                    if (!this.remote.transact(TRANSACTION_getNSFWByFilePath, data, reply, 0) && defaultImpl != null) {
                        defaultImpl.getNSFWByFilePath(packageName, filePath, fileName, mimeType, md5);
                    } else {
                        reply.readException();
                    }
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(INsfwAidlInterface impl) {
            if (Proxy.defaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl == null) {
                return false;
            }
            Proxy.defaultImpl = impl;
            return true;
        }

        public static INsfwAidlInterface getDefaultImpl() {
            return Proxy.defaultImpl;
        }
    }
}
package com.xtc.im.aidl;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import com.xtc.im.core.common.listener.IMessageListener;
import com.xtc.im.core.common.listener.OnFinishListener;
import com.xtc.im.core.common.voice.ISliceSender;
import com.xtc.im.core.common.voice.entity.VoiceDescEntity;
import com.xtc.im.transpond.ITranspondCallback;

/** IM 桥接服务（运行在 launcher 进程）的 AIDL 接口。 */
public interface IBridgeService extends IInterface {

    boolean transpondHttp(String url, int method, byte[] header, byte[] body,
            ITranspondCallback callback) throws RemoteException;

    byte[] buildFullVoice(String groupId, int lastIndex) throws RemoteException;

    void sendGroupMessage(long dialogId, int msgType, String msgId, byte[] msg, int contentType,
            IMessageListener listener) throws RemoteException;

    void sendGroupVoiceDesc(long dialogId, String groupId, VoiceDescEntity voiceDesc, int contentType,
            IMessageListener listener) throws RemoteException;

    void sendSingleMessage(long receiverId, int msgType, String msgId, byte[] msg, int contentType,
            IMessageListener listener) throws RemoteException;

    void sendSingleVoiceDesc(long receiverId, String groupId, VoiceDescEntity voiceDesc, int contentType,
            IMessageListener listener) throws RemoteException;

    ISliceSender createGroupSliceSender(long dialogId, OnFinishListener listener) throws RemoteException;

    ISliceSender createSingleSliceSender(long receiverId, OnFinishListener listener) throws RemoteException;

    void sendSyncTrigger() throws RemoteException;

    boolean isSupportFunction(int functionId) throws RemoteException;

    void sendInsensitiveGroupMessage(long dialogId, int msgType, String msgId, byte[] msg, int contentType,
            boolean noSensitivity, IMessageListener listener) throws RemoteException;

    void sendInsensitiveSingleMessage(long receiverId, int msgType, String msgId, byte[] msg, int contentType,
            boolean noSensitivity, IMessageListener listener) throws RemoteException;

    abstract class Stub extends Binder implements IBridgeService {

        private static final String DESCRIPTOR = "com.xtc.im.aidl.IBridgeService";
        static final int TRANSACTION_transpondHttp = 1;
        static final int TRANSACTION_buildFullVoice = 2;
        static final int TRANSACTION_sendGroupMessage = 3;
        static final int TRANSACTION_sendGroupVoiceDesc = 4;
        static final int TRANSACTION_sendSingleMessage = 5;
        static final int TRANSACTION_sendSingleVoiceDesc = 6;
        static final int TRANSACTION_createGroupSliceSender = 7;
        static final int TRANSACTION_createSingleSliceSender = 8;
        static final int TRANSACTION_sendSyncTrigger = 9;
        static final int TRANSACTION_isSupportFunction = 10;
        static final int TRANSACTION_sendInsensitiveGroupMessage = 11;
        static final int TRANSACTION_sendInsensitiveSingleMessage = 12;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IBridgeService asInterface(IBinder binder) {
            if (binder == null) {
                return null;
            }
            IInterface localInterface = binder.queryLocalInterface(DESCRIPTOR);
            if (localInterface != null && (localInterface instanceof IBridgeService)) {
                return (IBridgeService) localInterface;
            }
            return new Proxy(binder);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            switch (code) {
                case TRANSACTION_transpondHttp: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean result = transpondHttp(data.readString(), data.readInt(), data.createByteArray(),
                            data.createByteArray(), ITranspondCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                }
                case TRANSACTION_buildFullVoice: {
                    data.enforceInterface(DESCRIPTOR);
                    byte[] result = buildFullVoice(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeByteArray(result);
                    return true;
                }
                case TRANSACTION_sendGroupMessage: {
                    data.enforceInterface(DESCRIPTOR);
                    sendGroupMessage(data.readLong(), data.readInt(), data.readString(), data.createByteArray(),
                            data.readInt(), IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_sendGroupVoiceDesc: {
                    data.enforceInterface(DESCRIPTOR);
                    sendGroupVoiceDesc(data.readLong(), data.readString(),
                            data.readInt() != 0 ? VoiceDescEntity.CREATOR.createFromParcel(data) : null,
                            data.readInt(), IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_sendSingleMessage: {
                    data.enforceInterface(DESCRIPTOR);
                    sendSingleMessage(data.readLong(), data.readInt(), data.readString(), data.createByteArray(),
                            data.readInt(), IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_sendSingleVoiceDesc: {
                    data.enforceInterface(DESCRIPTOR);
                    sendSingleVoiceDesc(data.readLong(), data.readString(),
                            data.readInt() != 0 ? VoiceDescEntity.CREATOR.createFromParcel(data) : null,
                            data.readInt(), IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_createGroupSliceSender: {
                    data.enforceInterface(DESCRIPTOR);
                    ISliceSender sender = createGroupSliceSender(data.readLong(),
                            OnFinishListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeStrongBinder(sender != null ? sender.asBinder() : null);
                    return true;
                }
                case TRANSACTION_createSingleSliceSender: {
                    data.enforceInterface(DESCRIPTOR);
                    ISliceSender sender = createSingleSliceSender(data.readLong(),
                            OnFinishListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    reply.writeStrongBinder(sender != null ? sender.asBinder() : null);
                    return true;
                }
                case TRANSACTION_sendSyncTrigger: {
                    data.enforceInterface(DESCRIPTOR);
                    sendSyncTrigger();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_isSupportFunction: {
                    data.enforceInterface(DESCRIPTOR);
                    boolean result = isSupportFunction(data.readInt());
                    reply.writeNoException();
                    reply.writeInt(result ? 1 : 0);
                    return true;
                }
                case TRANSACTION_sendInsensitiveGroupMessage: {
                    data.enforceInterface(DESCRIPTOR);
                    sendInsensitiveGroupMessage(data.readLong(), data.readInt(), data.readString(),
                            data.createByteArray(), data.readInt(), data.readInt() != 0,
                            IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_sendInsensitiveSingleMessage: {
                    data.enforceInterface(DESCRIPTOR);
                    sendInsensitiveSingleMessage(data.readLong(), data.readInt(), data.readString(),
                            data.createByteArray(), data.readInt(), data.readInt() != 0,
                            IMessageListener.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IBridgeService {
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
            public boolean transpondHttp(String url, int method, byte[] header, byte[] body,
                    ITranspondCallback callback) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(url);
                    data.writeInt(method);
                    data.writeByteArray(header);
                    data.writeByteArray(body);
                    data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_transpondHttp, data, reply, 0);
                    reply.readException();
                    return reply.readInt() != 0;
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public byte[] buildFullVoice(String groupId, int lastIndex) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeString(groupId);
                    data.writeInt(lastIndex);
                    this.mRemote.transact(TRANSACTION_buildFullVoice, data, reply, 0);
                    reply.readException();
                    return reply.createByteArray();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendGroupMessage(long dialogId, int msgType, String msgId, byte[] msg, int contentType,
                    IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(dialogId);
                    data.writeInt(msgType);
                    data.writeString(msgId);
                    data.writeByteArray(msg);
                    data.writeInt(contentType);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendGroupMessage, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendGroupVoiceDesc(long dialogId, String groupId, VoiceDescEntity voiceDesc, int contentType,
                    IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(dialogId);
                    data.writeString(groupId);
                    if (voiceDesc != null) {
                        data.writeInt(1);
                        voiceDesc.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    data.writeInt(contentType);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendGroupVoiceDesc, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendSingleMessage(long receiverId, int msgType, String msgId, byte[] msg, int contentType,
                    IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(receiverId);
                    data.writeInt(msgType);
                    data.writeString(msgId);
                    data.writeByteArray(msg);
                    data.writeInt(contentType);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendSingleMessage, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendSingleVoiceDesc(long receiverId, String groupId, VoiceDescEntity voiceDesc,
                    int contentType, IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(receiverId);
                    data.writeString(groupId);
                    if (voiceDesc != null) {
                        data.writeInt(1);
                        voiceDesc.writeToParcel(data, 0);
                    } else {
                        data.writeInt(0);
                    }
                    data.writeInt(contentType);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendSingleVoiceDesc, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public ISliceSender createGroupSliceSender(long dialogId, OnFinishListener listener)
                    throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(dialogId);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_createGroupSliceSender, data, reply, 0);
                    reply.readException();
                    return ISliceSender.Stub.asInterface(reply.readStrongBinder());
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public ISliceSender createSingleSliceSender(long receiverId, OnFinishListener listener)
                    throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(receiverId);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_createSingleSliceSender, data, reply, 0);
                    reply.readException();
                    return ISliceSender.Stub.asInterface(reply.readStrongBinder());
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendSyncTrigger() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(TRANSACTION_sendSyncTrigger, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public boolean isSupportFunction(int functionId) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(functionId);
                    this.mRemote.transact(TRANSACTION_isSupportFunction, data, reply, 0);
                    reply.readException();
                    return reply.readInt() != 0;
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendInsensitiveGroupMessage(long dialogId, int msgType, String msgId, byte[] msg,
                    int contentType, boolean noSensitivity, IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(dialogId);
                    data.writeInt(msgType);
                    data.writeString(msgId);
                    data.writeByteArray(msg);
                    data.writeInt(contentType);
                    data.writeInt(noSensitivity ? 1 : 0);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendInsensitiveGroupMessage, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }

            @Override
            public void sendInsensitiveSingleMessage(long receiverId, int msgType, String msgId, byte[] msg,
                    int contentType, boolean noSensitivity, IMessageListener listener) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeLong(receiverId);
                    data.writeInt(msgType);
                    data.writeString(msgId);
                    data.writeByteArray(msg);
                    data.writeInt(contentType);
                    data.writeInt(noSensitivity ? 1 : 0);
                    data.writeStrongBinder(listener != null ? listener.asBinder() : null);
                    this.mRemote.transact(TRANSACTION_sendInsensitiveSingleMessage, data, reply, 0);
                    reply.readException();
                } finally {
                    reply.recycle();
                    data.recycle();
                }
            }
        }
    }
}
package com.xtc.im.client;

import android.content.Context;

import com.xtc.im.core.common.listener.IMessageListener;
import com.xtc.im.core.common.listener.OnFinishListener;
import com.xtc.im.core.common.voice.ISliceSender;
import com.xtc.im.core.common.voice.entity.VoiceDescEntity;
import com.xtc.im.transpond.ITranspondCallback;
import com.xtc.log.LogUtil;

/** IM 客户端门面：转发调用到宿主进程的 BridgeService。 */
public class IMClient {

    private static final String TAG = LogTag.tag("IMClient");
    private static volatile IMClient imClient;

    private BridgeServiceManager bridgeServiceManager;
    private Context context;

    public static IMClient getInstance(Context context) {
        if (imClient == null) {
            synchronized (IMClient.class) {
                if (imClient == null) {
                    imClient = new IMClient(context);
                }
            }
        }
        return imClient;
    }

    private IMClient(Context context) {
        this.context = context;
        this.bridgeServiceManager = BridgeServiceManager.getInstance(context);
    }

    public void bindBridgeService() {
        this.bridgeServiceManager.bindBridgeService();
    }

    public void unbindBridgeService() {
        this.bridgeServiceManager.unbindBridgeService();
    }

    /** 通过 IM 通道转发 http 请求。 */
    public boolean transpondHttp(String url, int method, byte[] header, byte[] body,
            ITranspondCallback callback) {
        try {
            return this.bridgeServiceManager.getBridgeService().transpondHttp(url, method, header, body, callback);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** 把语音分片拼接成完整语音。 */
    public byte[] buildFullVoice(String groupId, int lastIndex) {
        try {
            return this.bridgeServiceManager.getBridgeService().buildFullVoice(groupId, lastIndex);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public boolean isSupportFunction(int functionId) {
        try {
            return this.bridgeServiceManager.getBridgeService().isSupportFunction(functionId);
        } catch (Exception e) {
            LogUtil.e(TAG, "isSupportFunction: ", e);
            return false;
        }
    }

    public void sendGroupMessage(long dialogId, int msgType, String msgId, byte[] msg, int contentType,
            IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendGroupMessage(dialogId, msgType, msgId, msg,
                    contentType, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public void sendInsensitiveGroupMessage(long dialogId, int msgType, String msgId, byte[] msg, int contentType,
            boolean noSensitivity, IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendInsensitiveGroupMessage(dialogId, msgType, msgId, msg,
                    contentType, noSensitivity, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public void sendGroupVoiceDesc(long dialogId, String groupId, VoiceDescEntity voiceDesc, int contentType,
            IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendGroupVoiceDesc(dialogId, groupId, voiceDesc,
                    contentType, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public void sendSingleMessage(long receiverId, int msgType, String msgId, byte[] msg, int contentType,
            IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendSingleMessage(receiverId, msgType, msgId, msg,
                    contentType, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public void sendInsensitiveSingleMessage(long receiverId, int msgType, String msgId, byte[] msg, int contentType,
            boolean noSensitivity, IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendInsensitiveSingleMessage(receiverId, msgType, msgId, msg,
                    contentType, noSensitivity, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public void sendSingleVoiceDesc(long receiverId, String groupId, VoiceDescEntity voiceDesc, int contentType,
            IMessageListener listener) {
        try {
            this.bridgeServiceManager.getBridgeService().sendSingleVoiceDesc(receiverId, groupId, voiceDesc,
                    contentType, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    public ISliceSender createGroupSliceSender(long dialogId, OnFinishListener listener) {
        try {
            return this.bridgeServiceManager.getBridgeService().createGroupSliceSender(dialogId, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public ISliceSender createSingleSliceSender(long receiverId, OnFinishListener listener) {
        try {
            return this.bridgeServiceManager.getBridgeService().createSingleSliceSender(receiverId, listener);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public void sendSyncTrigger() {
        try {
            this.bridgeServiceManager.getBridgeService().sendSyncTrigger();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }
}
package com.xtc.moment.module.assistant;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.client.BaseDeviceModule;
import com.xtc.assistantapi.exception.HandleDirectiveException;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.Payload;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.assistant.message.PostStatusPayload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 语音助手中动态相关的指令模块，负责接收发布状态指令并分发给监听者。
 */
public class MomentDeviceModule extends BaseDeviceModule {

    private static final String TAG = LogTag.of("WeichatDeviceModule");

    public static final String MESSAGE_TYPE_TEXT = "TEXT";

    private final List<IMomentListener> momentListeners;

    public interface IMomentListener {
        void onReceivePostStatus(PostStatusPayload postStatusPayload);
    }

    public MomentDeviceModule() {
        super(ApiConstants.NAMESPACE);
        this.momentListeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void handleDirective(Directive directive) throws HandleDirectiveException {
        String directiveName = directive.getName();
        LogUtil.d(TAG, "handleDirective: directiveName = " + directiveName);
        if (ApiConstants.Directives.PostStatus.NAME.equals(directiveName)) {
            handlePostStatusPayload(directive.getPayload());
        }
    }

    private void handlePostStatusPayload(Payload payload) {
        if (payload instanceof PostStatusPayload) {
            fireOnReceivePostStatus((PostStatusPayload) payload);
        } else {
            LogUtil.d(TAG, "handle postStatusPayload: payload no instanceof PostStatusPayload");
        }
    }

    private void fireOnReceivePostStatus(PostStatusPayload postStatusPayload) {
        Iterator<IMomentListener> iterator = this.momentListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onReceivePostStatus(postStatusPayload);
        }
    }

    @Override
    public HashMap<String, Class<?>> supportPayload() {
        HashMap<String, Class<?>> payloadMap = new HashMap<>();
        payloadMap.put(getNameSpace() + ApiConstants.Directives.PostStatus.NAME, PostStatusPayload.class);
        return payloadMap;
    }

    @Override
    public void release() {
        List<IMomentListener> listeners = this.momentListeners;
        if (listeners == null || listeners.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.momentListeners.size(); i++) {
            removeMomentListener(this.momentListeners.get(i));
        }
    }

    public void addMomentListener(IMomentListener momentListener) {
        this.momentListeners.add(momentListener);
    }

    public void removeMomentListener(IMomentListener momentListener) {
        this.momentListeners.remove(momentListener);
    }
}
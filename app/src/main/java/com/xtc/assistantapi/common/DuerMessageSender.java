package com.xtc.assistantapi.common;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.core.IMessageSender;
import com.xtc.assistantapi.message.MessageResult;

/**
 * 度秘消息发送器，通过内容提供者把结果回传给语音助手。
 */
public class DuerMessageSender implements IMessageSender {

    private static final String TAG = LogTag.of("DuerMessageSender");
    private static final Uri VOICESEARCH_ASSISTANT_URI = Uri.parse("content://com.baidu.voicesearch.assistant");
    private static final String METHOD_CALL_VOICESEARCH_ASSISTANT = "callVoicesearchAssistant";

    private final Gson gson;

    private DuerMessageSender() {
        this.gson = new GsonBuilder().create();
    }

    public static DuerMessageSender getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void sendMessage(Context context, String message, String code) {
        Log.d(TAG, "sendEvent: message = " + message + ";code = " + code);
        try {
            context.getContentResolver().call(VOICESEARCH_ASSISTANT_URI, METHOD_CALL_VOICESEARCH_ASSISTANT,
                    gson.toJson(new MessageResult(code, message)), new Bundle());
        } catch (Exception e) {
            Log.d(TAG, "sendEvent: e = " + e);
        }
    }

    private static class Holder {
        private static final DuerMessageSender INSTANCE = new DuerMessageSender();

        private Holder() {
        }
    }
}
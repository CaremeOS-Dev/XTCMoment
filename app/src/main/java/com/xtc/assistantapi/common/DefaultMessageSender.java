package com.xtc.assistantapi.common;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.core.IMessageSender;

/**
 * 默认消息发送器，仅打印日志。
 */
public class DefaultMessageSender implements IMessageSender {

    private static final String TAG = LogTag.of("DefaultMessageSender");

    private final Gson gson;

    private DefaultMessageSender() {
        this.gson = new GsonBuilder().create();
    }

    public static DefaultMessageSender getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void sendMessage(Context context, String message, String code) {
        Log.d(TAG, "sendEvent: message = " + message + ";code = " + code);
    }

    private static class Holder {
        private static final DefaultMessageSender INSTANCE = new DefaultMessageSender();

        private Holder() {
        }
    }
}
package com.xtc.assistantapi.core;

import android.content.Context;

/**
 * 消息发送抽象。
 */
public interface IMessageSender {

    /** 发送消息。 */
    void sendMessage(Context context, String target, String message);
}
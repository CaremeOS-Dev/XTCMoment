package com.xtc.assistantapi;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtc.assistantapi.client.BaseDeviceModule;
import com.xtc.assistantapi.common.DuerMessageSender;
import com.xtc.assistantapi.core.IMessageSender;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.DirectiveDeserializer;
import com.xtc.assistantapi.message.PayloadConfig;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 设备模块管理器，按命名空间分发指令并维护消息发送器。
 */
public class DeviceModuleManager {

    private static final String TAG = LogTag.of("DeviceModuleManager");

    private final HashMap<String, BaseDeviceModule> moduleMap;
    private final Handler mainHandler;
    private final Gson gson;

    private IMessageSender messageSender;

    private DeviceModuleManager() {
        this.moduleMap = new HashMap<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gson = new GsonBuilder().registerTypeAdapter(Directive.class, new DirectiveDeserializer()).create();
    }

    public static DeviceModuleManager getInstance() {
        return DeviceModuleManagerHolder.INSTANCE;
    }

    /** 释放全部模块。 */
    public void release() {
        Iterator<BaseDeviceModule> iterator = this.moduleMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().release();
        }
        this.mainHandler.removeCallbacksAndMessages(null);
    }

    /** 注册设备模块并登记其支持的载荷类型。 */
    public void registerModule(BaseDeviceModule deviceModule) {
        if (deviceModule != null) {
            this.moduleMap.put(deviceModule.getNameSpace(), deviceModule);
            PayloadConfig.getInstance().insertPayload(deviceModule.supportPayload());
        }
    }

    /** 解析并处理指令 JSON。 */
    public int handleDirective(String directiveJson) {
        if (TextUtils.isEmpty(directiveJson)) {
            return 400;
        }
        Directive directive = gson.fromJson(directiveJson, Directive.class);
        if (directive == null) {
            return 400;
        }
        return handleDirective(directive);
    }

    private int handleDirective(final Directive directive) {
        if (directive == null) {
            return 400;
        }
        Log.d(TAG, "handleDirectiveCore:" + directive);
        Log.d(TAG, "handleDirectiveCore:" + directive.getName());
        try {
            final BaseDeviceModule deviceModule = this.moduleMap.get(directive.header.getNamespace());
            if (deviceModule == null) {
                return 404;
            }
            this.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    deviceModule.handleDirective(directive);
                }
            });
            return 200;
        } catch (Exception e) {
            return 500;
        }
    }

    public void setMessageSender(IMessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public IMessageSender getMessageSender() {
        IMessageSender sender = this.messageSender;
        return sender == null ? DuerMessageSender.getInstance() : sender;
    }

    private static class DeviceModuleManagerHolder {
        private static final DeviceModuleManager INSTANCE = new DeviceModuleManager();

        private DeviceModuleManagerHolder() {
        }
    }
}
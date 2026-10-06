package com.xtc.assistantapi.message;

import java.util.HashMap;

/**
 * 指令载荷类型注册表，按「命名空间 + 指令名」映射到具体载荷类。
 */
public class PayloadConfig {

    private final HashMap<String, Class<?>> payloadClass;

    private PayloadConfig() {
        this.payloadClass = new HashMap<>();
    }

    public static PayloadConfig getInstance() {
        return PayloadConfigHolder.INSTANCE;
    }

    /** 清空注册表。 */
    public void release() {
        this.payloadClass.clear();
    }

    void insertPayload(String namespace, String name, Class<?> payloadClass) {
        this.payloadClass.put(namespace + name, payloadClass);
    }

    /** 批量注册载荷类型。 */
    public void insertPayload(HashMap<String, Class<?>> payloadClassMap) {
        if (payloadClassMap == null || payloadClassMap.size() <= 0) {
            return;
        }
        this.payloadClass.putAll(payloadClassMap);
    }

    /** 查找载荷类型。 */
    public Class<?> findPayloadClass(String namespace, String name) {
        return this.payloadClass.get(namespace + name);
    }

    private static class PayloadConfigHolder {
        private static final PayloadConfig INSTANCE = new PayloadConfig();

        private PayloadConfigHolder() {
        }
    }
}
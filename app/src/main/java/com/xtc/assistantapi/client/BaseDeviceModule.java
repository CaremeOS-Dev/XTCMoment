package com.xtc.assistantapi.client;

import com.xtc.assistantapi.exception.HandleDirectiveException;
import com.xtc.assistantapi.message.Directive;

import java.util.HashMap;

/**
 * 设备模块基类，按命名空间处理指令并声明支持的载荷类型。
 */
public abstract class BaseDeviceModule {

    protected final String nameSpace;

    public BaseDeviceModule(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /** 处理指令。 */
    public abstract void handleDirective(Directive directive) throws HandleDirectiveException;

    /** 释放资源。 */
    public abstract void release();

    /** 支持的载荷类型（key 为「命名空间 + 指令名」）。 */
    public abstract HashMap<String, Class<?>> supportPayload();

    public String getNameSpace() {
        return nameSpace;
    }
}
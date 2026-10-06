package com.xtc.assistantapi;

/**
 * 助手 API 日志标签工具。
 */
public class LogTag {

    /** 生成带统一前缀的日志标签。 */
    public static String of(String tag) {
        return "AssistantApi_" + tag;
    }
}
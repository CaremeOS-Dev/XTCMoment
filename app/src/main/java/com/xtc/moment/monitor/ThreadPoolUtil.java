package com.xtc.moment.monitor;

/**
 * 把若干信息片段按监控约定的分隔符拼成一行日志。
 */
public class ThreadPoolUtil {

    public static String getSplitString(Object... infoArray) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.currentTimeMillis());
        builder.append(MonitorConstants.INFO_SPLIT);
        for (Object info : infoArray) {
            builder.append(info);
            if (!MonitorConstants.PLACEHOLDER_NEW_LINE.equals(info)) {
                builder.append(MonitorConstants.INFO_SPLIT);
            }
        }
        return builder.toString();
    }
}
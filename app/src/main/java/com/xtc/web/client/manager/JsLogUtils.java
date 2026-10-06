package com.xtc.web.client.manager;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.request.ReqJsLog;

/** H5 日志转发：把 JS 侧的日志按等级写入原生日志。 */
public class JsLogUtils {

    interface Type {
        String DEBUG = "0";
        String ERROR = "1";
        String INFO = "3";
        String WARNING = "2";
    }

    /** 按 type 字段输出对应等级的日志，未知等级按 debug 处理。 */
    public static void log(ReqJsLog reqJsLog) {
        String type = reqJsLog.getType();
        if (Type.DEBUG.equals(type)) {
            LogUtil.d(reqJsLog.getTag(), reqJsLog.getMsg());
        } else if (Type.ERROR.equals(type)) {
            LogUtil.e(reqJsLog.getTag(), reqJsLog.getMsg());
        } else if (Type.WARNING.equals(type)) {
            LogUtil.w(reqJsLog.getTag(), reqJsLog.getMsg());
        } else if (Type.INFO.equals(type)) {
            LogUtil.i(reqJsLog.getTag(), reqJsLog.getMsg());
        } else {
            LogUtil.d(reqJsLog.getTag(), reqJsLog.getMsg());
        }
    }
}
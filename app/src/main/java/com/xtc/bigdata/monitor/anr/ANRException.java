package com.xtc.bigdata.monitor.anr;

/** 主线程卡顿异常，仅用于日志打印。 */
@Deprecated
public class ANRException extends Exception {

    public ANRException() {
    }

    public ANRException(String message) {
        super(message);
    }
}
package com.xtc.moment.behavior;

/**
 * 七牛上传失败时抛出的异常，附带七牛错误码。
 */
public class QiNiuThrowable extends Throwable {

    private String errorCode;

    public QiNiuThrowable(String message, int errorCode) {
        super(message);
        this.errorCode = String.valueOf(errorCode);
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
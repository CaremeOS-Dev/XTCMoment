package com.xtc.moment.prerogative;

/**
 * 特权资源初始化回调。
 */
public interface InitPrerogativeCallback {
    void initFail();

    void initSuccess();
}
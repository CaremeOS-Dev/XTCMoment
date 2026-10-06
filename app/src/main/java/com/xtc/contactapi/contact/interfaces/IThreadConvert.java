package com.xtc.contactapi.contact.interfaces;

/**
 * 线程切换抽象。
 */
public interface IThreadConvert {

    /** 将任务投递到目标线程执行。 */
    void convert(Runnable runnable);
}
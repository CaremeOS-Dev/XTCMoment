package com.xtc.contactapi.contact.convert;

import android.os.Handler;
import android.os.Looper;

import com.xtc.contactapi.contact.interfaces.IThreadConvert;

/**
 * 主线程转换器，将任务投递到主线程 Handler。
 */
public class MainThreadConvert implements IThreadConvert {

    private final ConvertHandler convertHandler = new ConvertHandler();

    @Override
    public void convert(Runnable runnable) {
        convertHandler.post(runnable);
    }

    /** 绑定主线程 Looper 的 Handler。 */
    public static class ConvertHandler extends Handler {
        public ConvertHandler() {
            super(Looper.getMainLooper());
        }
    }
}
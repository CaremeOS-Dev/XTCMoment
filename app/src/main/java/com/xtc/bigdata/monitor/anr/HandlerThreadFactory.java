package com.xtc.bigdata.monitor.anr;

import android.os.Handler;
import android.os.HandlerThread;

/** ANR 监控用的后台线程池（采样线程 + 写日志线程）。 */
@Deprecated
final class HandlerThreadFactory {

    private static HandlerThreadWrapper sLoopThread = new HandlerThreadWrapper("loop");
    private static HandlerThreadWrapper sWriteLogThread = new HandlerThreadWrapper("writer");

    private HandlerThreadFactory() {
        throw new InstantiationError("Must not instantiate this class");
    }

    public static Handler getTimerThreadHandler() {
        return sLoopThread.getHandler();
    }

    public static Handler getWriteLogThreadHandler() {
        return sWriteLogThread.getHandler();
    }

    private static class HandlerThreadWrapper {
        private Handler handler;

        HandlerThreadWrapper(String name) {
            this.handler = null;
            HandlerThread handlerThread = new HandlerThread("ANR-Tracer-" + name);
            handlerThread.start();
            this.handler = new Handler(handlerThread.getLooper());
        }

        public Handler getHandler() {
            return this.handler;
        }
    }
}
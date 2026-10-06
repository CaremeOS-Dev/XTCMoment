package com.xtc.bigdata.monitor.anr;

import android.os.Debug;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Printer;

import com.xtc.moment.module.Constants;

import java.util.Iterator;

/** 通过 Looper 消息日志监控主线程阻塞。 */
@Deprecated
public class LooperMonitor implements Printer {

    /** 阻塞回调。 */
    public interface BlockListener {
        void onBlock(long blockTime, String stackTrace);
    }

    private BlockListener blockListener;
    private boolean ignoreDebugger;
    private Looper looper;
    private boolean mPrintingStarted = false;
    private long mStartTimestamp = 0;
    private StackSampler stackSampler;
    private long timeout;

    public LooperMonitor(Looper looper, boolean ignoreDebugger, long timeout, BlockListener blockListener) {
        this.ignoreDebugger = true;
        this.timeout = Constants.DIFFER_TIME;
        this.looper = looper;
        this.ignoreDebugger = ignoreDebugger;
        this.timeout = timeout;
        this.blockListener = blockListener;
        this.stackSampler = new StackSampler(looper.getThread(), 1000L);
    }

    @Override
    public void println(String message) {
        if (this.ignoreDebugger && Debug.isDebuggerConnected()) {
            return;
        }
        if (!this.mPrintingStarted) {
            this.mStartTimestamp = SystemClock.uptimeMillis();
            this.mPrintingStarted = true;
            this.stackSampler.start();
        } else {
            long currentTime = SystemClock.uptimeMillis();
            this.mPrintingStarted = false;
            if (isBlock(currentTime)) {
                notifyBlock(currentTime);
            }
            this.stackSampler.stop();
        }
    }

    private long getBlockTime(long endTime) {
        return endTime - this.mStartTimestamp;
    }

    private boolean isBlock(long endTime) {
        return getBlockTime(endTime) >= this.timeout;
    }

    private void notifyBlock(final long endTime) {
        if (this.blockListener != null) {
            final long blockTime = getBlockTime(endTime);
            final long startTimestamp = this.mStartTimestamp;
            HandlerThreadFactory.getWriteLogThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    StringBuilder builder = new StringBuilder();
                    Iterator<String> iterator = stackSampler.getThreadStackEntries(startTimestamp, endTime)
                            .iterator();
                    while (iterator.hasNext()) {
                        builder.append(iterator.next());
                        builder.append("\r\n");
                    }
                    blockListener.onBlock(blockTime, builder.toString());
                }
            });
        }
    }
}
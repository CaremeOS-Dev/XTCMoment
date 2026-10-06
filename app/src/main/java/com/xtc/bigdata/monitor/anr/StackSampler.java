package com.xtc.bigdata.monitor.anr;

import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

/** 主线程堆栈采样器，按时间戳保存最近的堆栈快照。 */
@Deprecated
class StackSampler extends AbstractSampler {

    private static final int DEFAULT_MAX_ENTRY_COUNT = 1;
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private static final int MAX_STACK_LENGTH = 102400;
    private static final LinkedHashMap<Long, String> sStackMap = new LinkedHashMap<>();

    private Thread mCurrentThread;
    private int mMaxEntryCount;

    public StackSampler(Thread thread, long sampleInterval) {
        this(thread, DEFAULT_MAX_ENTRY_COUNT, sampleInterval);
    }

    public StackSampler(Thread thread, int maxEntryCount, long sampleInterval) {
        super(sampleInterval);
        this.mMaxEntryCount = DEFAULT_MAX_ENTRY_COUNT;
        this.mCurrentThread = thread;
        this.mMaxEntryCount = maxEntryCount;
    }

    /** 取指定时间区间内采集到的堆栈。 */
    public ArrayList<String> getThreadStackEntries(long startTime, long endTime) {
        ArrayList<String> entries = new ArrayList<>();
        synchronized (sStackMap) {
            for (Long timestamp : sStackMap.keySet()) {
                if (startTime < timestamp.longValue() && timestamp.longValue() < endTime) {
                    entries.add("\r\n" + sStackMap.get(timestamp));
                }
            }
        }
        return entries;
    }

    @Override
    protected void doSample() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : this.mCurrentThread.getStackTrace()) {
            builder.append(element.toString());
            builder.append("\r\n");
        }
        synchronized (sStackMap) {
            if (sStackMap.size() >= this.mMaxEntryCount && this.mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keySet().iterator().next());
            }
            int length = MAX_STACK_LENGTH;
            if (builder.length() < MAX_STACK_LENGTH) {
                length = builder.length();
            }
            sStackMap.put(Long.valueOf(SystemClock.uptimeMillis()), builder.substring(0, length));
        }
    }
}
package com.xtc.bigdata.collector.utils;

import android.content.ContentValues;
import android.os.Handler;
import android.os.HandlerThread;

import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.collector.encapsulation.interfaces.IEvent;
import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/** Serialises event persistence onto a background thread. */
public class QueueUtils {

    private static final String TAG = "QueueUtils";
    /** Maximum number of events written in one batch. */
    private static final int BATCH_SIZE = 200;

    private static QueueUtils instance;
    private final Handler taskHandler;
    private final ConcurrentMap<Integer, IEvent> pendingEvents = new ConcurrentHashMap<>();
    private final Object timerLock = new Object();
    private boolean isRunning = false;
    private final LinkedBlockingQueue<IEvent> queue = new LinkedBlockingQueue<>();

    public static QueueUtils getInstance() {
        if (instance == null) {
            synchronized (QueueUtils.class) {
                if (instance == null) {
                    instance = new QueueUtils();
                }
            }
        }
        return instance;
    }

    private QueueUtils() {
        HandlerThread handlerThread = new HandlerThread("bigdata_task", 10);
        handlerThread.start();
        this.taskHandler = new Handler(handlerThread.getLooper());
    }

    public void post(Runnable runnable) {
        this.taskHandler.post(runnable);
    }

    public void postDelay(Runnable runnable, long delayMillis) {
        this.taskHandler.postDelayed(runnable, delayMillis);
    }

    public void add(IEvent event) {
        if (this.queue.offer(event)) {
            execTimer();
        }
    }

    private void execTimer() {
        synchronized (this.timerLock) {
            if (this.pendingEvents.size() == 0 && !this.queue.isEmpty() && !this.isRunning) {
                this.isRunning = true;
                getEvent();
            }
        }
    }

    private void getEvent() {
        post(new Runnable() {
            @Override
            public void run() {
                checkToSave();
            }
        });
    }

    private void checkToSave() {
        while (!this.queue.isEmpty()) {
            IEvent event = this.queue.poll();
            if (event != null) {
                this.pendingEvents.put(Integer.valueOf(event.hashCode()), event);
            }
            if (this.pendingEvents.size() >= BATCH_SIZE) {
                break;
            }
        }
        if (this.pendingEvents.size() > 0) {
            makeData();
        }
    }

    private void makeData() {
        ArrayList<IEvent> events = new ArrayList<>();
        Iterator<Map.Entry<Integer, IEvent>> iterator = this.pendingEvents.entrySet().iterator();
        while (iterator.hasNext()) {
            IEvent event = iterator.next().getValue();
            if (event != null) {
                event.makeData();
                events.add(event);
            }
        }
        saveData(events);
    }

    private void saveData(List<IEvent> events) {
        int size = events.size();
        if (size == 1) {
            ShareHelper.getInstance().insert(events.get(0).getContentValues());
        } else {
            ContentValues[] contentValues = new ContentValues[size];
            for (int i = 0; i < size; i++) {
                contentValues[i] = events.get(i).getContentValues();
            }
            LogUtil.i(TAG, "saveData() 批量插入数量为 = " + size);
            ShareHelper.getInstance().insertBulk(contentValues);
        }
        synchronized (this.timerLock) {
            events.clear();
            this.pendingEvents.clear();
            this.isRunning = false;
        }
        execTimer();
    }
}
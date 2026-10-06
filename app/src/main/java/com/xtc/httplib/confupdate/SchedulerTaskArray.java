package com.xtc.httplib.confupdate;

import android.util.ArrayMap;

import com.xtc.httplib.bean.BaseSchedulerTask;

import java.util.Map;
import java.util.Set;

/** Wrapper around the scheduled configuration-update tasks. */
public class SchedulerTaskArray {

    private final ArrayMap<Integer, BaseSchedulerTask> confBeanArrayMap = new ArrayMap<>();

    public Set<Map.Entry<Integer, BaseSchedulerTask>> entrySet() {
        return this.confBeanArrayMap.entrySet();
    }
}
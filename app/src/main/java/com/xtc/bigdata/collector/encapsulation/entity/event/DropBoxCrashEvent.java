package com.xtc.bigdata.collector.encapsulation.entity.event;

import java.util.HashMap;

/**
 * DropBox 采集到的崩溃事件。
 */
public class DropBoxCrashEvent extends ExceptionEvent {

    public String processName;

    @Override
    public int eventType() {
        return 11;
    }

    @Override
    protected String data2Json() {
        HashMap<String, String> map = new HashMap<>();
        map.put("processName", this.processName);
        map.put("reason", this.reason);
        map.put("stack", this.stack);
        return hashMap2Json(map);
    }
}
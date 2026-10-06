package com.xtc.bigdata.collector.encapsulation.entity.event;

import java.util.HashMap;

/**
 * DropBox 采集到的 ANR 事件。
 */
public class DropBoxANREvent extends ExceptionEvent {

    public String activity;
    public String build;
    public String flags;
    public String foreground;
    public String packageInfo;
    public String parentActivity;
    public String parentProcess;
    public String processName;
    public String subject;

    @Override
    public int eventType() {
        return 11;
    }

    @Override
    protected String data2Json() {
        HashMap<String, String> map = new HashMap<>();
        map.put("processName", this.processName);
        map.put("flags", this.flags);
        map.put("packageInfo", this.packageInfo);
        map.put("activity", this.activity);
        map.put("parentProcess", this.parentProcess);
        map.put("parentActivity", this.parentActivity);
        map.put("foreground", this.foreground);
        map.put("subject", this.subject);
        map.put("build", this.build);
        return hashMap2Json(map);
    }

    @Override
    public String toString() {
        return "DropBoxANREvent{processName='" + this.processName + "', flags='" + this.flags + "', packageInfo='" + this.packageInfo + "', activity='" + this.activity + "', parentProcess='" + this.parentProcess + "', parentActivity='" + this.parentActivity + "', foreground='" + this.foreground + "', subject='" + this.subject + "', build='" + this.build + "'}";
    }
}
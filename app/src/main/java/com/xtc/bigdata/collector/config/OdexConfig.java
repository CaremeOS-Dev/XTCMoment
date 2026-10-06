package com.xtc.bigdata.collector.config;

/**
 * Odex 采集配置：采集间隔（小时）与触发次数阈值。
 */
public class OdexConfig {

    public static final int DEFAULT_INTERVAL = 24;
    public static final int DEFAULT_TRIGGER_COUNT = 5;

    private long interval;
    private int triggerCount;

    public OdexConfig(long interval, int triggerCount) {
        this.interval = DEFAULT_INTERVAL;
        this.triggerCount = DEFAULT_TRIGGER_COUNT;
        this.interval = interval;
        this.triggerCount = triggerCount;
    }

    public long getInterval() {
        return this.interval;
    }

    public int getTriggerCount() {
        return this.triggerCount;
    }

    public boolean verify() {
        return this.interval >= 4 && this.triggerCount >= 5;
    }

    public static OdexConfig getDefaultConfig() {
        return new OdexConfig(DEFAULT_INTERVAL, DEFAULT_TRIGGER_COUNT);
    }

    @Override
    public String toString() {
        return "{\"OdexConfig\":{\"interval\":" + this.interval + ",\"triggerCount\":" + this.triggerCount + "}}";
    }
}
package com.xtc.bigdata.collector.exception;

/**
 * 崩溃统计信息：异常类名、次数与最近崩溃日期。
 */
public class CrashInfo {

    private String exceptionClass;
    private int exceptionCount;
    private long exceptionUpdateTime;
    private String lastCrashDate;
    private int todayCrashCount;

    public String getLastCrashDate() {
        return this.lastCrashDate;
    }

    public void setLastCrashDate(String lastCrashDate) {
        this.lastCrashDate = lastCrashDate;
    }

    public int getTodayCrashCount() {
        return this.todayCrashCount;
    }

    public void setTodayCrashCount(int todayCrashCount) {
        this.todayCrashCount = todayCrashCount;
    }

    public int getExceptionCount() {
        return this.exceptionCount;
    }

    public void setExceptionCount(int exceptionCount) {
        this.exceptionCount = exceptionCount;
    }

    public String getExceptionClass() {
        return this.exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public long getExceptionUpdateTime() {
        return this.exceptionUpdateTime;
    }

    public void setExceptionUpdateTime(long exceptionUpdateTime) {
        this.exceptionUpdateTime = exceptionUpdateTime;
    }

    @Override
    public String toString() {
        return "{\"CrashInfo\":{\"exceptionClass\":\"" + this.exceptionClass + "\",\"updateTime\":" + this.exceptionUpdateTime + ",\"count\":" + this.exceptionCount + ",\"lastCrashDate\":" + this.lastCrashDate + ",\"todayCrashcount\":" + this.todayCrashCount + "}}";
    }
}
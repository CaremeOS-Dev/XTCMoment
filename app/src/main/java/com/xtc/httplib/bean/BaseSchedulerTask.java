package com.xtc.httplib.bean;

/** Scheduler bookkeeping entry shared by the alarm helpers. */
public class BaseSchedulerTask {
    private long delayTime;
    private int index;
    private long setTime;
    private boolean toDispatch;
    private int version;

    /** Creates a task with the given schedule parameters. */
    public static BaseSchedulerTask createTask(int index, int version, long setTime, long delayTime) {
        BaseSchedulerTask task = new BaseSchedulerTask();
        task.setIndex(index);
        task.setVersion(version);
        task.setSetTime(setTime);
        task.setDelayTime(delayTime);
        task.setToDispatch(false);
        return task;
    }

    /** Refreshes the schedule parameters. */
    public void updateTask(int version, long setTime, long delayTime, boolean toDispatch) {
        setVersion(version);
        setSetTime(setTime);
        setDelayTime(delayTime);
        setToDispatch(toDispatch);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getSetTime() {
        return this.setTime;
    }

    public void setSetTime(long setTime) {
        this.setTime = setTime;
    }

    public long getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public boolean isToDispatch() {
        return this.toDispatch;
    }

    public void setToDispatch(boolean toDispatch) {
        this.toDispatch = toDispatch;
    }

    @Override
    public String toString() {
        return "BaseSchedulerTask{index=" + this.index + ", version=" + this.version + ", setTime=" + this.setTime
                + ", delayTime=" + this.delayTime + ", toDispatch=" + this.toDispatch + '}';
    }
}
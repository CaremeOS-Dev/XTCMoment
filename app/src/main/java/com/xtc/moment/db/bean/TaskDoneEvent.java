package com.xtc.moment.db.bean;

/** Carries the identifier of a task that just finished. */
public class TaskDoneEvent {

    private String doneInfo;

    public TaskDoneEvent(String doneInfo) {
        this.doneInfo = doneInfo;
    }

    public String getDoneInfo() {
        return this.doneInfo;
    }

    public void setDoneInfo(String doneInfo) {
        this.doneInfo = doneInfo;
    }

    @Override
    public String toString() {
        return "TaskDoneEvent{doneInfo='" + this.doneInfo + "'}";
    }
}

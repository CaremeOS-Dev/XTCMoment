package com.xtc.system.account.bean;

/**
 * 计步数据。
 */
public class StepCounter {

    private String dateToDay;
    private Integer totalDistance;
    private Integer totalStep;

    public String getDateToDay() {
        return this.dateToDay;
    }

    public void setDateToDay(String dateToDay) {
        this.dateToDay = dateToDay;
    }

    public Integer getTotalStep() {
        return this.totalStep;
    }

    public void setTotalStep(Integer totalStep) {
        this.totalStep = totalStep;
    }

    public Integer getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(Integer totalDistance) {
        this.totalDistance = totalDistance;
    }

    @Override
    public String toString() {
        return "StepCounter{dateToDay='" + this.dateToDay + "', totalStep=" + this.totalStep + ", totalDistance=" + this.totalDistance + '}';
    }
}
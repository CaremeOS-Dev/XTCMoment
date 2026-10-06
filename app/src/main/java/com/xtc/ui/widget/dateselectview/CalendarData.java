package com.xtc.ui.widget.dateselectview;

import java.util.GregorianCalendar;

/** 日期选择结果：年、月（1 起）、日。 */
public class CalendarData {

    private GregorianCalendar gregorianCalendar;
    private int pickedDay;
    private int pickedMonthSway;
    private int pickedYear;

    public CalendarData(int year, int month, int day) {
        this.pickedYear = year;
        this.pickedMonthSway = month;
        this.pickedDay = day;
        this.gregorianCalendar = new GregorianCalendar(year, month - 1, day);
    }

    public GregorianCalendar getGregorianCalendar() {
        return this.gregorianCalendar;
    }
}
package com.xtc.bigdata.collector.encapsulation.entity.attr;

import android.content.ContentValues;

import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.common.db.constant.Columns;

/** Per-event attributes. */
public class EventAttr implements IAttr {
    String dataCollectLevel;
    String dataSecurityLevel;
    int eventType;
    String functionName;
    String moduleDetail;
    String page;
    String trigTime;
    String trigValue;

    public EventAttr() {
        this.functionName = "";
        this.dataCollectLevel = "B";
        this.dataSecurityLevel = "C";
        this.eventType = 0;
        this.trigTime = "";
        this.trigValue = "";
        this.page = "";
        this.moduleDetail = "";
    }

    public EventAttr(String functionName, String dataCollectLevel, String dataSecurityLevel, int eventType,
                     String trigTime, String trigValue, String page, String moduleDetail) {
        this();
        this.functionName = functionName;
        this.dataCollectLevel = dataCollectLevel;
        this.dataSecurityLevel = dataSecurityLevel;
        this.eventType = eventType;
        this.trigTime = trigTime;
        this.trigValue = trigValue;
        this.page = page;
        this.moduleDetail = moduleDetail;
    }

    @Override
    public void insert(ContentValues contentValues) {
        contentValues.put(Columns.COLUMN_EA_FUNCTIONNAME, this.functionName);
        contentValues.put(Columns.COLUMN_COLLECTION_LEVEL, this.dataCollectLevel);
        contentValues.put(Columns.COLUMN_SECURITY_LEVEL, this.dataSecurityLevel);
        contentValues.put(Columns.COLUMN_EA_EVENTTYPE, Integer.valueOf(this.eventType));
        contentValues.put(Columns.COLUMN_EA_TTIME, this.trigTime);
        contentValues.put(Columns.COLUMN_EA_TVALUE, this.trigValue);
        contentValues.put(Columns.COLUMN_EA_PAGE, this.page);
        contentValues.put(Columns.COLUMN_EA_MODULEDETAIL, this.moduleDetail);
    }

    @Override
    public IAttr clone() {
        EventAttr eventAttr = new EventAttr();
        eventAttr.functionName = this.functionName;
        eventAttr.dataCollectLevel = this.dataCollectLevel;
        eventAttr.dataSecurityLevel = this.dataSecurityLevel;
        eventAttr.trigValue = this.trigValue;
        eventAttr.page = this.page;
        eventAttr.moduleDetail = this.moduleDetail;
        eventAttr.eventType = this.eventType;
        eventAttr.trigTime = this.trigTime;
        return eventAttr;
    }

    public EventAttr setEventType(int eventType) {
        this.eventType = eventType;
        return this;
    }

    public int getEventType() {
        return this.eventType;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public EventAttr setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public String getDataCollectLevel() {
        return this.dataCollectLevel;
    }

    public EventAttr setDataCollectLevel(String dataCollectLevel) {
        this.dataCollectLevel = dataCollectLevel;
        return this;
    }

    public String getDataSecurityLevel() {
        return this.dataSecurityLevel;
    }

    public EventAttr setDataSecurityLevel(String dataSecurityLevel) {
        this.dataSecurityLevel = dataSecurityLevel;
        return this;
    }

    public EventAttr setTrigTime(String trigTime) {
        this.trigTime = trigTime;
        return this;
    }

    public String getTrigTime() {
        return this.trigTime;
    }

    public EventAttr setTrigValue(String trigValue) {
        this.trigValue = trigValue;
        return this;
    }

    public String getTrigValue() {
        return this.trigValue;
    }

    public String getPage() {
        return this.page;
    }

    public EventAttr setPage(String page) {
        this.page = page;
        return this;
    }

    public EventAttr setModuleDetail(String moduleDetail) {
        this.moduleDetail = moduleDetail;
        return this;
    }

    public String getModuleDetail() {
        return this.moduleDetail;
    }
}
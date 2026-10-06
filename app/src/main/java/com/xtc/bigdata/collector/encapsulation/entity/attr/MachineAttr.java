package com.xtc.bigdata.collector.encapsulation.entity.attr;

import android.content.ContentValues;

import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.common.db.constant.Columns;

/** Machine attributes attached to every event. */
public class MachineAttr implements IAttr {
    private String mId = "";
    private String devName = "";
    private String innerModel = "";
    private String osVer = "";
    private String brand = "";
    private int rooted = 0;

    @Override
    public void insert(ContentValues contentValues) {
        contentValues.put(Columns.COLUMN_MA_MID, this.mId);
        contentValues.put(Columns.COLUMN_MA_OSVERSION, this.osVer);
        contentValues.put(Columns.COLUMN_MA_DEVICE, this.devName);
        contentValues.put(Columns.COLUMN_INNER_MODEL, this.innerModel);
        contentValues.put(Columns.COLUMN_MA_BRAND, this.brand);
        contentValues.put(Columns.COLUMN_ROOTED, Integer.valueOf(this.rooted));
    }

    @Override
    public IAttr clone() {
        MachineAttr machineAttr = new MachineAttr();
        machineAttr.brand = this.brand;
        machineAttr.devName = this.devName;
        machineAttr.innerModel = this.innerModel;
        machineAttr.mId = this.mId;
        machineAttr.osVer = this.osVer;
        machineAttr.rooted = this.rooted;
        return machineAttr;
    }

    public MachineAttr setmId(String mId) {
        this.mId = mId;
        return this;
    }

    public MachineAttr setDevName(String devName) {
        this.devName = devName;
        return this;
    }

    public MachineAttr setOsVersion(String osVer) {
        this.osVer = osVer;
        return this;
    }

    public MachineAttr setInnerModel(String innerModel) {
        this.innerModel = innerModel;
        return this;
    }

    public MachineAttr setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public int getRooted() {
        return this.rooted;
    }

    public MachineAttr setRooted(int rooted) {
        this.rooted = rooted;
        return this;
    }
}
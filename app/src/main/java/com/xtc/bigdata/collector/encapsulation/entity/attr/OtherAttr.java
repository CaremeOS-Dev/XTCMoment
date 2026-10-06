package com.xtc.bigdata.collector.encapsulation.entity.attr;

import android.content.ContentValues;

import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.common.db.constant.Columns;

/** Session and extension attributes attached to every event. */
public class OtherAttr implements IAttr {
    private String daVer;
    private String extend = "";
    private String extendJudgment;
    private String sessionid;

    @Override
    public void insert(ContentValues contentValues) {
        contentValues.put(Columns.COLUMN_EA_SESSIONID, this.sessionid);
        contentValues.put(Columns.COLUMN_OA_EXTEND, this.extend);
        contentValues.put(Columns.COLUMN_OA_DAVER, this.daVer);
        contentValues.put(Columns.COLUMN_OA_EXTENDJUDGMENT, this.extendJudgment);
        contentValues.put(Columns.COLUMN_OA_STATUS, (Integer) 0);
    }

    @Override
    public IAttr clone() {
        OtherAttr otherAttr = new OtherAttr();
        otherAttr.daVer = this.daVer;
        otherAttr.extend = this.extend;
        otherAttr.extendJudgment = this.extendJudgment;
        otherAttr.sessionid = this.sessionid;
        return otherAttr;
    }

    public String getDaVer() {
        return this.daVer;
    }

    public void setDaVer(String daVer) {
        this.daVer = daVer;
    }

    public String getExtend() {
        return this.extend;
    }

    public String getExtendJudgment() {
        return this.extendJudgment;
    }

    public void setExtendJudgment(String extendJudgment) {
        this.extendJudgment = extendJudgment;
    }

    public OtherAttr setExtend(String extend) {
        this.extend = extend;
        return this;
    }

    public OtherAttr setSessionid(String sessionid) {
        this.sessionid = sessionid;
        return this;
    }
}
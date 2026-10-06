package com.xtc.bigdata.collector.encapsulation.entity.event;

import android.content.ContentValues;
import android.text.TextUtils;

import com.xtc.bigdata.collector.encapsulation.BaseAttrManager;
import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.collector.encapsulation.interfaces.IEvent;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.bigdata.collector.utils.Judgment;
import com.xtc.bigdata.common.utils.FormatUtils;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Base class of every collected event. */
public abstract class AEvent implements IEvent {

    private List<IAttr> attrs;
    private String extendJudgment = "";
    private final List<IAttr> extendAttrs = new ArrayList<>();

    protected abstract String getJsonExtend();

    protected abstract EventAttr packageEventAttr();

    protected abstract void packageExtendAttr();

    @Override
    public final List<IAttr> makeData() {
        return getAttrs();
    }

    @Override
    public final synchronized ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        for (IAttr attr : getAttrs()) {
            if (attr != null) {
                attr.insert(contentValues);
            }
        }
        return contentValues;
    }

    public void addAttr(IAttr... attrs) {
        for (IAttr attr : attrs) {
            if (attr != null) {
                this.extendAttrs.add(attr);
            }
        }
    }

    public void useMobileTraffic(boolean useMobileTraffic) {
        this.extendJudgment = Judgment.useMobileTraffic(this.extendJudgment, useMobileTraffic);
    }

    String hashMap2Json(Map map) {
        return JSONUtil.toJSON(map);
    }

    private List<IAttr> getAttrs() {
        if (CollectionUtil.isEmpty(this.attrs)) {
            this.attrs = Collections.synchronizedList(new ArrayList<IAttr>());
            this.attrs.add(getEventAttr());
            this.attrs.add(BaseAttrManager.getInstance().getUserAttr());
            this.attrs.add(BaseAttrManager.getInstance().getApplicationAttr());
            this.attrs.add(BaseAttrManager.getInstance().getMachineAttr());
            this.attrs.add(BaseAttrManager.getInstance().getOtherAttr(getJsonExtend(), this.extendJudgment));
            packageExtendAttr();
            this.attrs.addAll(this.extendAttrs);
        }
        return this.attrs;
    }

    private EventAttr getEventAttr() {
        EventAttr eventAttr = packageEventAttr();
        if (eventAttr == null) {
            eventAttr = new EventAttr();
        }
        eventAttr.setEventType(eventType());
        if (TextUtils.isEmpty(eventAttr.getTrigTime())) {
            eventAttr.setTrigTime(FormatUtils.getDate());
        }
        return eventAttr;
    }

    public final synchronized ContentValues getTempContentValues(List<IAttr> attrs) {
        ContentValues contentValues = new ContentValues();
        for (IAttr attr : attrs) {
            if (attr != null) {
                attr.insert(contentValues);
            }
        }
        return contentValues;
    }
}
package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;

/** Event whose attributes are supplied by the caller. */
public class CustomAttrEvent extends AEvent {
    public EventAttr eventAttr;

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    public int eventType() {
        EventAttr attr = this.eventAttr;
        return attr == null ? 0 : attr.getEventType();
    }

    @Override
    protected EventAttr packageEventAttr() {
        return this.eventAttr;
    }

    @Override
    protected String getJsonExtend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        return hashMap2Json(map);
    }
}
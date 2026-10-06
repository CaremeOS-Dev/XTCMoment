package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;

/** HTTP monitor event. */
public class MonitorURLEvent extends AEvent {
    public static final long serialVersionUID = 1;
    public String dataCollectLevel = "B";
    public String dataSecurityLevel = "C";
    public String functionName;
    public String moduleDetail;
    public String trigValue;

    @Override
    public int eventType() {
        return 14;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setEventType(14)
                .setFunctionName(this.functionName)
                .setModuleDetail(this.moduleDetail)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel)
                .setTrigValue(this.trigValue);
    }

    @Override
    protected String getJsonExtend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        return hashMap2Json(map);
    }
}
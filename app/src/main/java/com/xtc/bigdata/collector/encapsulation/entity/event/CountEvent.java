package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

/** Count event. */
public class CountEvent extends AEvent {
    public static final long serialVersionUID = 1;
    public String activity;
    public String dataCollectLevel;
    public String dataSecurityLevel;
    public Map<String, String> extend;
    public String functionName;
    public String moduleDetail;
    public String trigValue;

    @Override
    public int eventType() {
        return 8;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setFunctionName(this.functionName)
                .setPage(this.activity)
                .setModuleDetail(this.moduleDetail)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel)
                .setTrigValue(this.trigValue);
    }

    @Override
    protected String getJsonExtend() {
        if (this.extend == null) {
            this.extend = new HashMap<>();
        }
        this.extend.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        return hashMap2Json(this.extend);
    }

    public void setExtend(Map extend) {
        this.extend = extend;
    }
}
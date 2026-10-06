package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

/** System-information event. */
public class SystemInfoEvent extends AEvent {
    public String dataCollectLevel;
    public String dataSecurityLevel;
    public Map<String, String> extend;
    public String functionName;

    @Override
    public int eventType() {
        return 10;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setFunctionName(this.functionName)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel);
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
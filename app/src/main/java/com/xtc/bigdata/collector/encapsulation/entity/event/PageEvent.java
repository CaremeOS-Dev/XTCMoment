package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

/** Page-duration event. */
public class PageEvent extends AEvent {
    public String activityName;
    public String dataCollectLevel;
    public String dataSecurityLevel;
    public String duaring;
    public Map<String, String> extend;
    public String functionName;
    public String moduleDetail;

    @Override
    public int eventType() {
        return 2;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setFunctionName(this.functionName)
                .setPage(this.activityName)
                .setTrigValue(this.duaring)
                .setModuleDetail(this.moduleDetail)
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
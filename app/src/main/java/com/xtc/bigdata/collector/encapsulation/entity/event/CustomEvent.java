package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;
import com.xtc.bigdata.common.utils.FormatUtils;

import java.util.HashMap;
import java.util.Map;

/** User-defined event. */
public class CustomEvent extends AEvent {
    public static final long serialVersionUID = 1;
    public String activity;
    public String dataCollectLevel;
    public String dataSecurityLevel;

    @Deprecated
    public String eventName;

    @Deprecated
    public int eventType;
    public Map<String, String> extend;
    public String functionName;
    public String moduleDetail;
    public long trigTime;
    public String trigValue;

    @Override
    public int eventType() {
        return 7;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        EventAttr eventAttr = new EventAttr()
                .setFunctionName(this.functionName)
                .setPage(this.activity)
                .setModuleDetail(this.moduleDetail)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel)
                .setTrigValue(this.trigValue);
        if (this.trigTime > 0) {
            eventAttr.setTrigTime(FormatUtils.getDate(this.trigTime));
        }
        return eventAttr;
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
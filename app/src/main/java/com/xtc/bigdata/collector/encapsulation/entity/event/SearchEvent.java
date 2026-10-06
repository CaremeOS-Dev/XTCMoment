package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

/** Search event. */
public class SearchEvent extends AEvent {
    public static final long serialVersionUID = 1;
    public String activity;
    public String dataCollectLevel;
    public String dataSecurityLevel;
    public String functionName;
    public String keyWrod;
    public String moduleDetail;
    public String resultCount;

    @Override
    public int eventType() {
        return 6;
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
                .setTrigValue(this.keyWrod);
    }

    @Override
    protected String getJsonExtend() {
        return hashMap2Json(getExtend(this.resultCount));
    }

    private Map<String, String> getExtend(String resultCount) {
        HashMap<String, String> map = new HashMap<>();
        map.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        map.put("resultCount", resultCount);
        return map;
    }
}
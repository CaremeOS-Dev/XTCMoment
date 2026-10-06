package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.SystemInfoUtils;

import java.util.HashMap;

/** App-launch event with device capability details. */
public class AppLaunchEvent extends AEvent {
    public static final String FUNCTION_NAME = "BigDataSDK_AppLaunchEvent";

    private String activityName;
    private String extend;
    private String trigValue;
    public String functionName = FUNCTION_NAME;
    public String moduleDetail = "BigDataSDK_AppLaunch";
    public String dataCollectLevel = "B";
    public String dataSecurityLevel = "C";

    @Override
    public int eventType() {
        return 3;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setFunctionName(this.functionName)
                .setPage(this.activityName)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel)
                .setTrigValue(this.trigValue);
    }

    @Override
    protected String getJsonExtend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        map.put("cpuInfo", SystemInfoUtils.getCpuName());
        map.put("memoryInfo", SystemInfoUtils.getTotalRam(ContextUtils.getContext()));
        map.put("deviceCapacity", SystemInfoUtils.getRomTotalSize(ContextUtils.getContext()));
        map.put("screenResolution", SystemInfoUtils.getScreenResolutionRatio(ContextUtils.getContext()));
        map.put("screenSize", String.valueOf(SystemInfoUtils.getScreenInch(ContextUtils.getContext())));
        String json = hashMap2Json(map);
        this.extend = json;
        return json;
    }
}
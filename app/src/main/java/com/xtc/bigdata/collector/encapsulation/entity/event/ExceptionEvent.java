package com.xtc.bigdata.collector.encapsulation.entity.event;

import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.utils.NetworkUtil;

import java.util.HashMap;

/** Exception / crash event. */
public class ExceptionEvent extends AEvent {
    private static final long serialVersionUID = 1;
    public String dataCollectLevel = "B";
    public String dataSecurityLevel = "C";
    public String diskTotal;
    public String diskUsage;
    public String filePath;
    public String functionName;
    public String memTotal;
    public String memUsage;
    public String reason;
    public String sdTotal;
    public String sdUsage;
    public String stack;
    public String sysLog;

    @Override
    public int eventType() {
        return 9;
    }

    @Override
    protected void packageExtendAttr() {
    }

    @Override
    protected EventAttr packageEventAttr() {
        return new EventAttr()
                .setFunctionName(this.functionName)
                .setDataCollectLevel(this.dataCollectLevel)
                .setDataSecurityLevel(this.dataSecurityLevel)
                .setTrigValue(data2Json());
    }

    @Override
    protected String getJsonExtend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("NetWorkType", NetworkUtil.getInstance().getNetworkType());
        map.put("FilePath", this.filePath);
        return hashMap2Json(map);
    }

    protected String data2Json() {
        HashMap<String, String> map = new HashMap<>();
        map.put("reason", this.reason);
        map.put("stack", this.stack);
        map.put("sysLog", this.sysLog);
        map.put("diskTotal", this.diskTotal);
        map.put("diskUsage", this.diskUsage);
        map.put("sdTotal", this.sdTotal);
        map.put("sdUsage", this.sdUsage);
        map.put("memTotal", this.memTotal);
        map.put("memUsage", this.memUsage);
        return hashMap2Json(map);
    }
}
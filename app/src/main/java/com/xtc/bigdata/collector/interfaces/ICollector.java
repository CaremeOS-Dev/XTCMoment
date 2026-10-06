package com.xtc.bigdata.collector.interfaces;

import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.ClickEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CountEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.MonitorURLEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SearchEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SystemInfoEvent;

import java.util.Map;

/** Collector facade. */
public interface ICollector {
    void appLaunch();

    void clickEvent(ClickEvent clickEvent);

    void countEvent(CountEvent countEvent);

    void customEvent(CustomEvent customEvent);

    void exceptionEvent(ExceptionEvent exceptionEvent);

    String getBehaviorVersion();

    void initUserInfo(UserAttr userAttr);

    void monitorURLEvent(MonitorURLEvent monitorURLEvent);

    void pageBegin(String pageName);

    boolean pageEnd(String pageName);

    boolean pageEnd(String pageName, String functionName, String moduleDetail);

    boolean pageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                    String dataSecurityLevel, Map<String, String> extend);

    void realTime2Upload();

    void searchEvent(SearchEvent searchEvent);

    void systemInfoEvent(SystemInfoEvent systemInfoEvent);

    void uploadSystemCrash();
}
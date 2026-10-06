package com.xtc.bigdata.collector;

import android.os.SystemClock;

import com.xtc.bigdata.collector.encapsulation.BaseAttrManager;
import com.xtc.bigdata.collector.encapsulation.entity.attr.EventAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.AppLaunchEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.ClickEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CountEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomAttrEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.MonitorURLEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.PageEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SearchEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SystemInfoEvent;
import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.collector.encapsulation.interfaces.IEvent;
import com.xtc.bigdata.collector.interfaces.ICollector;
import com.xtc.bigdata.collector.utils.QueueUtils;
import com.xtc.bigdata.common.error.ErrorCode;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.model.I18n;

import java.util.HashMap;
import java.util.Map;

/** Records events into the big-data provider. */
public class CollectionManager implements ICollector {

    private static final String FUNCTION_NAME_APP_LIFE_CYCLE_ON_ACTIVITY_PAUSED = "AppLifeCycle_onActivityPaused";
    private static final String TAG = "CollectionManager";

    private static CollectionManager instance;
    private final Map<String, Long> pageBeginTimeMap = new HashMap<>();

    public static CollectionManager getInstance() {
        CollectionManager manager = instance;
        if (manager != null) {
            return manager;
        }
        synchronized (CollectionManager.class) {
            if (instance == null) {
                instance = new CollectionManager();
            }
        }
        return instance;
    }

    private CollectionManager() {
    }

    public void exit() {
    }

    @Override
    public String getBehaviorVersion() {
        return "";
    }

    @Override
    public void uploadSystemCrash() {
    }

    @Override
    public void appLaunch() {
        insert(new AppLaunchEvent());
    }

    @Override
    public void clickEvent(ClickEvent clickEvent) {
        insert(clickEvent);
    }

    @Override
    public void customEvent(CustomEvent customEvent) {
        insert(customEvent);
    }

    @Override
    public void searchEvent(SearchEvent searchEvent) {
        insert(searchEvent);
    }

    @Override
    public void pageBegin(String pageName) {
        if (this.pageBeginTimeMap.containsKey(pageName)) {
            return;
        }
        this.pageBeginTimeMap.put(pageName, Long.valueOf(SystemClock.elapsedRealtime()));
    }

    @Override
    public boolean pageEnd(String pageName) {
        return pageEnd(pageName, FUNCTION_NAME_APP_LIFE_CYCLE_ON_ACTIVITY_PAUSED, null, "B", "C", null);
    }

    @Override
    public boolean pageEnd(String pageName, String functionName, String moduleDetail) {
        return pageEnd(pageName, functionName, moduleDetail, "B", "C", null);
    }

    @Override
    public boolean pageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                           String dataSecurityLevel, Map<String, String> extend) {
        if (!this.pageBeginTimeMap.containsKey(pageName)) {
            LogUtil.w(TAG, ErrorCode.CONTROL_PAGE_ORDER);
            return false;
        }
        long duration = SystemClock.elapsedRealtime() - this.pageBeginTimeMap.remove(pageName).longValue();
        LogUtil.d(TAG, "界面[" + pageName + "]停留时间：" + duration + I18n.Language.MALAY);
        PageEvent pageEvent = new PageEvent();
        pageEvent.activityName = pageName;
        pageEvent.functionName = functionName;
        pageEvent.moduleDetail = moduleDetail;
        pageEvent.dataCollectLevel = dataCollectLevel;
        pageEvent.dataSecurityLevel = dataSecurityLevel;
        pageEvent.extend = extend;
        pageEvent.duaring = String.valueOf(duration);
        insert(pageEvent);
        return true;
    }

    @Override
    public void countEvent(CountEvent countEvent) {
        insert(countEvent);
    }

    @Override
    public void exceptionEvent(ExceptionEvent exceptionEvent) {
        insert(exceptionEvent);
    }

    @Override
    public void monitorURLEvent(MonitorURLEvent monitorURLEvent) {
        insert(monitorURLEvent);
    }

    @Override
    public void initUserInfo(UserAttr userAttr) {
        BaseAttrManager.getInstance().setUserAttr(userAttr);
    }

    @Override
    public void systemInfoEvent(SystemInfoEvent systemInfoEvent) {
        insert(systemInfoEvent);
    }

    public UserAttr getUserInfo() {
        return BaseAttrManager.getInstance().getUserAttr();
    }

    @Override
    public void realTime2Upload() {
        ShareHelper.getInstance().realTimeNotify();
    }

    public void event(IEvent event) {
        insert(event);
    }

    private void insert(IEvent event) {
        if (ContextUtils.isEmpty()) {
            LogUtil.w(TAG, ErrorCode.CONTROL_INIT_INSERT);
        } else if (event == null) {
            LogUtil.w(TAG, ErrorCode.CONTROL_NULL_POINTER_IEVENT);
        } else {
            QueueUtils.getInstance().add(event);
        }
    }

    private CustomAttrEvent createCustomAttrEvent(int eventType, String unused, EventAttr eventAttr, IAttr... attrs) {
        if (eventAttr == null) {
            LogUtil.w(TAG, ErrorCode.CONTROL_NULL_POINTER_EVENT_ATTR);
            return null;
        }
        eventAttr.setEventType(eventType);
        CustomAttrEvent customAttrEvent = new CustomAttrEvent();
        customAttrEvent.eventAttr = eventAttr;
        customAttrEvent.addAttr(attrs);
        return customAttrEvent;
    }
}
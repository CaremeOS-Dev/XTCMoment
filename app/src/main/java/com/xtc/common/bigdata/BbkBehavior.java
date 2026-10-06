package com.xtc.common.bigdata;

import android.content.Context;

import com.xtc.bigdata.collector.BehaviorCollector;
import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.ClickEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CountEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SearchEvent;
import com.xtc.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/** Default {@link Behavior} implementation backed by the collector. */
public class BbkBehavior extends Behavior {

    private static final String TAG = "BbkBehavior";

    @Override
    public void setDebugMode(boolean debug) {
    }

    @Override
    public void userSingOut(Context context) {
    }

    @Override
    public void setSessionTimeout(long timeout) {
        ConfigAgent.getBehaviorConfig().sessionTimeout = timeout;
    }

    @Override
    public void onResume(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
        } else {
            LogUtil.i(TAG, context.getClass().getSimpleName());
        }
    }

    @Override
    public void onPause(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
        } else {
            LogUtil.i(TAG, context.getClass().getSimpleName());
        }
    }

    @Override
    public void onPageStart(String pageName) {
        BehaviorCollector.getInstance().pageBegin(pageName);
    }

    @Override
    public void onPageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                          String dataSecurityLevel, Map extend) {
        BehaviorCollector.getInstance().pageEnd(pageName, functionName, moduleDetail, dataCollectLevel,
                dataSecurityLevel, extend);
    }

    @Override
    public void clickEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                           String dataSecurityLevel, HashMap<String, String> extend) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
            return;
        }
        ClickEvent clickEvent = new ClickEvent();
        clickEvent.activity = context.getClass().getName();
        clickEvent.functionName = functionName;
        clickEvent.moduleDetail = moduleDetail;
        clickEvent.dataCollectLevel = dataCollectLevel;
        clickEvent.dataSecurityLevel = dataSecurityLevel;
        clickEvent.setExtend(extend);
        BehaviorCollector.getInstance().clickEvent(clickEvent);
    }

    @Override
    public void countEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                           String dataSecurityLevel, String trigValue, HashMap<String, String> extend) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
            return;
        }
        CountEvent countEvent = new CountEvent();
        countEvent.functionName = functionName;
        countEvent.moduleDetail = moduleDetail;
        countEvent.dataCollectLevel = dataCollectLevel;
        countEvent.dataSecurityLevel = dataSecurityLevel;
        countEvent.trigValue = trigValue;
        countEvent.activity = context.getClass().getName();
        countEvent.setExtend(extend);
        BehaviorCollector.getInstance().countEvent(countEvent);
    }

    @Override
    public void searchEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                            String dataSecurityLevel, String keyWord, String resultCount) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
            return;
        }
        SearchEvent searchEvent = new SearchEvent();
        searchEvent.functionName = functionName;
        searchEvent.moduleDetail = moduleDetail;
        searchEvent.dataCollectLevel = dataCollectLevel;
        searchEvent.dataSecurityLevel = dataSecurityLevel;
        searchEvent.keyWrod = keyWord;
        searchEvent.resultCount = resultCount;
        searchEvent.activity = context.getClass().getName();
        BehaviorCollector.getInstance().searchEvent(searchEvent);
    }

    @Override
    public void customEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                            String dataSecurityLevel, String trigValue, HashMap<String, String> extend) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
            return;
        }
        CustomEvent customEvent = new CustomEvent();
        customEvent.functionName = functionName;
        customEvent.moduleDetail = moduleDetail;
        customEvent.dataCollectLevel = dataCollectLevel;
        customEvent.dataSecurityLevel = dataSecurityLevel;
        customEvent.trigValue = trigValue;
        customEvent.activity = context.getClass().getName();
        customEvent.setExtend(extend);
        BehaviorCollector.getInstance().customEvent(customEvent);
    }

    @Override
    public void customEventByCustomTime(Context context, String functionName, String moduleDetail,
                                        String dataCollectLevel, String dataSecurityLevel, long trigTime,
                                        HashMap<String, String> extend) {
        if (context == null) {
            LogUtil.e(TAG, "context is null");
            return;
        }
        CustomEvent customEvent = new CustomEvent();
        customEvent.functionName = functionName;
        customEvent.moduleDetail = moduleDetail;
        customEvent.dataCollectLevel = dataCollectLevel;
        customEvent.dataSecurityLevel = dataSecurityLevel;
        customEvent.trigTime = trigTime;
        customEvent.activity = context.getClass().getName();
        customEvent.setExtend(extend);
        BehaviorCollector.getInstance().customEvent(customEvent);
    }

    @Override
    public void userSingIn(Context context, UserAttr userAttr) {
        if (context == null || userAttr == null) {
            return;
        }
        BehaviorCollector.getInstance().initUserInfo(userAttr);
    }
}
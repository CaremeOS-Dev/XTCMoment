package com.xtc.common.bigdata;

import android.content.Context;

import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;

import java.util.HashMap;
import java.util.Map;

/** Pluggable behaviour-reporting backend. */
public abstract class Behavior {
    public abstract void clickEvent(Context context, String functionName, String moduleDetail,
                                    String dataCollectLevel, String dataSecurityLevel, HashMap<String, String> extend);

    public abstract void countEvent(Context context, String functionName, String moduleDetail,
                                    String dataCollectLevel, String dataSecurityLevel, String trigValue,
                                    HashMap<String, String> extend);

    public abstract void customEvent(Context context, String functionName, String moduleDetail,
                                     String dataCollectLevel, String dataSecurityLevel, String trigValue,
                                     HashMap<String, String> extend);

    public abstract void customEventByCustomTime(Context context, String functionName, String moduleDetail,
                                                 String dataCollectLevel, String dataSecurityLevel, long trigTime,
                                                 HashMap<String, String> extend);

    public abstract void onPageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                                   String dataSecurityLevel, Map extend);

    public abstract void onPageStart(String pageName);

    public abstract void onPause(Context context);

    public abstract void onResume(Context context);

    public abstract void searchEvent(Context context, String functionName, String moduleDetail,
                                     String dataCollectLevel, String dataSecurityLevel, String keyWord, String resultCount);

    public abstract void setDebugMode(boolean debug);

    public abstract void setSessionTimeout(long timeout);

    public abstract void userSingIn(Context context, UserAttr userAttr);

    public abstract void userSingOut(Context context);
}
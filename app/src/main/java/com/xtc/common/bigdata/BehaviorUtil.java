package com.xtc.common.bigdata;

import android.content.Context;

import com.xtc.bigdata.collector.BehaviorCollector;
import com.xtc.bigdata.collector.config.BehaviorConfig;
import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.SystemInfoEvent;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.log.LogUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/** Static facade over the behaviour collector. */
public class BehaviorUtil {

    public static final int BEHAVIOR_MODE_ALL = 2;
    public static final int BEHAVIOR_MODE_BBK = 1;
    public static final int BEHAVIOR_MODE_UMENG = 0;

    private static final String TAG = "BehaviorUtil";

    private static Behavior bbkBehavior = null;
    private static int behaviorMode = 0;
    private static boolean isInit = false;

    /** Data collection level annotations. */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CollectionLevel {
        String LEVEL_A = "A";
        String LEVEL_B = "B";
        String LEVEL_C = "C";
    }

    /** Data security level annotations. */
    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SecurityLevel {
        String LEVEL_A = "A";
        String LEVEL_B = "B";
        String LEVEL_C = "C";
    }

    private BehaviorUtil() {
    }

    public static void init(BehaviorConfig behaviorConfig) {
        isInit = true;
        BehaviorCollector.getInstance().init(behaviorConfig);
        setBehaviorMode(BEHAVIOR_MODE_BBK);
    }

    public static void initUserInfo(String userId) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else if (Constants.deviceType.equals(Constants.WATCH)) {
            UserAttr userAttr = new UserAttr();
            userAttr.setUserId(userId);
            BehaviorCollector.getInstance().initUserInfo(userAttr);
        }
    }

    public static void setWatchConfig(String config) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            BehaviorCollector.getInstance().saveWatchConfig(config);
        }
    }

    public static void loadFilterFunctions() {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        }
    }

    public static void saveFilterFunctions(String functions) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        }
    }

    public static String getFilterFunctionItemJson() {
        if (!isInit) {
            LogUtil.e("BehaviorUtil don't have init.");
            return "";
        }
        return "";
    }

    public static void setSystemInfo(String functionName, Map<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            setSystemInfo(functionName, "B", "C", extend);
        }
    }

    public static void setSystemInfo(String functionName, String dataCollectLevel, String dataSecurityLevel,
                                     Map<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        SystemInfoEvent systemInfoEvent = new SystemInfoEvent();
        systemInfoEvent.functionName = functionName;
        systemInfoEvent.dataCollectLevel = dataCollectLevel;
        systemInfoEvent.dataSecurityLevel = dataSecurityLevel;
        systemInfoEvent.setExtend(extend);
        BehaviorCollector.getInstance().systemInfoEvent(systemInfoEvent);
    }

    public static void uplaodSystemCrashInfo() {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else if (ConfigAgent.getBehaviorConfig().isUploadSysLog) {
            BehaviorCollector.getInstance().uploadSystemCrash();
        }
    }

    public static void setBehaviorMode(int mode) {
        behaviorMode = mode;
        if (mode == BEHAVIOR_MODE_UMENG) {
            ConfigAgent.getBehaviorConfig().usable = false;
            return;
        }
        if (mode == BEHAVIOR_MODE_BBK) {
            ConfigAgent.getBehaviorConfig().usable = true;
            getBbkBehavior();
        } else if (mode == BEHAVIOR_MODE_ALL) {
            ConfigAgent.getBehaviorConfig().usable = true;
            getBbkBehavior();
        }
    }

    private static Behavior getBbkBehavior() {
        if (bbkBehavior == null) {
            bbkBehavior = new BbkBehavior();
        }
        return bbkBehavior;
    }

    public static void setChannelId(String key, String channelId) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.PHONE_CHANNEL_ID, channelId);
        }
    }

    public static void setSessionTimeout(long timeout) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            ConfigAgent.getBehaviorConfig().sessionTimeout = timeout;
        }
    }

    public static void setDebug(boolean debug) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            Constants.setDebug(debug);
        }
    }

    public static void onResume(Context context) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().onResume(context);
        }
    }

    public static void onPause(Context context) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().onPause(context);
        }
    }

    public static void onPageStart(String pageName) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().onPageStart(pageName);
        }
    }

    public static void onPageEnd(String pageName) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            pageEnd(pageName, null, null, null);
        }
    }

    public static void pageEnd(String pageName, String functionName, String moduleDetail, Map extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            onPageEnd(pageName, functionName, moduleDetail, "B", "C", extend);
        }
    }

    public static void onPageEnd(String pageName, String functionName, String moduleDetail, String dataCollectLevel,
                                 String dataSecurityLevel, Map extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().onPageEnd(pageName, functionName, moduleDetail, dataCollectLevel, dataSecurityLevel, extend);
        }
    }

    public static void clickEvent(Context context, String functionName) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            clickEvent(context, functionName, null, null);
        }
    }

    public static void clickEvent(Context context, String functionName, String moduleDetail,
                                  HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            clickEvent(context, functionName, moduleDetail, "B", "C", extend);
        }
    }

    public static void clickEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                                  String dataSecurityLevel, HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().clickEvent(context, functionName, moduleDetail, dataCollectLevel, dataSecurityLevel, extend);
        }
    }

    public static void countEvent(Context context, String functionName, String trigValue) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            countEvent(context, functionName, null, trigValue, null);
        }
    }

    public static void countEvent(Context context, String functionName, String moduleDetail, String trigValue,
                                  HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            countEvent(context, functionName, moduleDetail, "B", "C", trigValue, extend);
        }
    }

    public static void countEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                                  String dataSecurityLevel, String trigValue, HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().countEvent(context, functionName, moduleDetail, dataCollectLevel, dataSecurityLevel,
                    trigValue, extend);
        }
    }

    public static void searchEvent(Context context, String functionName, String moduleDetail, String keyWord,
                                   String resultCount) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            searchEvent(context, functionName, moduleDetail, "B", "C", keyWord, resultCount);
        }
    }

    public static void searchEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                                   String dataSecurityLevel, String keyWord, String resultCount) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().searchEvent(context, functionName, moduleDetail, dataCollectLevel, dataSecurityLevel,
                    keyWord, resultCount);
        }
    }

    public static void customEvent(Context context, String functionName, HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            customEvent(context, functionName, null, null, extend);
        }
    }

    public static void customEvent(Context context, String functionName, HashMap<String, String> extend,
                                   String dataCollectLevel, String dataSecurityLevel) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            customEvent(context, functionName, null, dataCollectLevel, dataSecurityLevel, null, extend);
        }
    }

    public static void customEventByCustomTime(Context context, String functionName, long trigTime,
                                               HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().customEventByCustomTime(context, functionName, null, "C", "C", trigTime, extend);
        }
    }

    public static void customEvent(Context context, String functionName, String moduleDetail, String trigValue,
                                   HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            customEvent(context, functionName, moduleDetail, "B", "C", trigValue, extend);
        }
    }

    public static void customEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                                   String dataSecurityLevel, String trigValue, HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().customEvent(context, functionName, moduleDetail, dataCollectLevel, dataSecurityLevel,
                    trigValue, extend);
        }
    }

    public static void realTimeEvent(Context context, String functionName, String moduleDetail, String trigValue,
                                     HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
        } else {
            realTimeEvent(context, functionName, moduleDetail, "B", "C", trigValue, extend);
        }
    }

    public static void realTimeEvent(Context context, String functionName, String moduleDetail, String dataCollectLevel,
                                     String dataSecurityLevel, String trigValue, HashMap<String, String> extend) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
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
    }

    public static void userSingIn(Context context, UserAttr userAttr) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().userSingIn(context, userAttr);
        }
    }

    public static UserAttr getUserInfo() {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return null;
        }
        return BehaviorCollector.getInstance().getUserInfo();
    }

    public static void userSingOut(Context context) {
        if (!isInit) {
            LogUtil.e(TAG, "BehaviorUtil don't have init.");
            return;
        }
        if (behaviorMode == BEHAVIOR_MODE_BBK || behaviorMode == BEHAVIOR_MODE_ALL) {
            getBbkBehavior().userSingOut(context);
        }
    }
}
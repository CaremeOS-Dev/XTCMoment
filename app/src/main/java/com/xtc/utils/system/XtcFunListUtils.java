package com.xtc.utils.system;

import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

/**
 * Resolves the {@code XtcFunList_*} class matching the current app config,
 * model and region, then reads its {@code funs} field.
 */
public final class XtcFunListUtils {

    private static final String TAG = "XtcFunListUtils";
    private static final String FUN_LIST_CLASS_PREFIX = "com.xtc.funlist.XtcFunList";
    private static final String GROUP_SEGMENT = "_g";
    private static final String MODEL_SEGMENT = "_m";
    private static final String REGION_SEGMENT = "_r";
    private static final String FUNS_FIELD = "funs";

    private static final Set<Integer> FUN_CODES = loadFunCodes();

    /** @return true when {@code code} is part of the current fun list. */
    public static boolean isSupported(Integer code) {
        return FUN_CODES.contains(code);
    }

    /** Reflectively loads the fun list, falling back to an empty set. */
    private static Set<Integer> loadFunCodes() {
        try {
            Class<?> funListClass = resolveFunListClass();
            Constructor<?> constructor = funListClass.getDeclaredConstructor(new Class[0]);
            constructor.setAccessible(true);
            Object instance = constructor.newInstance(new Object[0]);
            Field funsField = funListClass.getDeclaredField(FUNS_FIELD);
            funsField.setAccessible(true);
            FunList funList = (FunList) funsField.get(instance);
            LogUtil.i(TAG, "Get fun list name=" + funListClass.getName() + ", content=" + funList);
            return funList;
        } catch (Exception e) {
            LogUtil.e(TAG, "Get fun list error:" + e.getClass().getSimpleName() + ", cause=" + e.getCause(), e);
            if (UtilsSystemConfig.debug && !TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("Duplicated fun=")) {
                throw new RuntimeException(e);
            }
            return Collections.emptySet();
        }
    }

    /** Resolves the fun-list class from app config, or from model + region. */
    private static Class<?> resolveFunListClass() throws ClassNotFoundException {
        String appConfig = SystemPropertyUtil.get(SystemProperty.APP_CONFIG, "");
        LogUtil.i(TAG, "Read appConfig str=" + appConfig);
        if (appConfig == null || appConfig.isEmpty()) {
            LogUtil.i(TAG, "No SystemProperty.PROPERTY_APPCONFIG. Get model and region function list.");
            return resolveFromModelAndRegion();
        }
        SystemPropertyAppConfig config = (SystemPropertyAppConfig) JSONUtil.fromJSON(appConfig, SystemPropertyAppConfig.class);
        if (config == null) {
            LogUtil.e(TAG, "Maybe deserialize SystemProperty.PROPERTY_APPCONFIG error. Get model and region function list.");
            return resolveFromModelAndRegion();
        }
        String group = config.getGroup();
        if (group == null || group.isEmpty()) {
            LogUtil.i(TAG, "No group config. Get model and region function list.");
            return resolveFromModelAndRegion();
        }
        return Class.forName(buildClassName(group, "", ""));
    }

    /** Resolves the fun-list class from the watch model and region. */
    private static Class<?> resolveFromModelAndRegion() throws ClassNotFoundException {
        String className;
        String innerModel = WatchModelUtil.getInnerModel();
        if (WatchModelUtil.isModelRegionChangable(innerModel)) {
            className = buildClassName("", innerModel, WatchModelUtil.getRegion());
        } else {
            className = buildClassName("", innerModel, "");
        }
        return Class.forName(className);
    }

    /** Builds {@code com.xtc.funlist.XtcFunList_g<group>_m<model>_r<region>}. */
    private static String buildClassName(String group, String model, String region) {
        return FUN_LIST_CLASS_PREFIX + GROUP_SEGMENT + group + MODEL_SEGMENT + model + REGION_SEGMENT + region;
    }
}
package com.xtc.secretapi;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.xtc.log.LogConfig;
import com.xtc.log.LogUtil;

/**
 * 应用签名校验工具，负责按包名读取上下文并委托 native 层计算签名。
 */
public class XTCGetUtil {

    private static final String TAG = "XTCGetUtil";

    private static volatile XTCGetUtil instance;

    private String secret;

    /** 单例获取。 */
    public static XTCGetUtil getInstance() {
        if (instance == null) {
            synchronized (XTCGetUtil.class) {
                if (instance == null) {
                    instance = new XTCGetUtil();
                    initLog();
                }
            }
        }
        return instance;
    }

    /** 获取内置密钥，本地无缓存时回退到 native。 */
    public static String getSecret() {
        return TextUtils.isEmpty(getInstance().secret) ? XTCGetUtilWrapper.gete() : getInstance().secret;
    }

    /** 根据目标包名计算内容签名。 */
    public static String sign(Context context, String content, String packageName) {
        Context targetContext;
        try {
            targetContext = context.createPackageContext(packageName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "createPackageContext error，packageName = " + packageName, e);
            targetContext = null;
        }
        if (targetContext != null) {
            context = targetContext;
        }
        return XTCGetUtilWrapper.init(context, content);
    }

    private static void initLog() {
        LogConfig.builder().isPrintConsole(true).saveLog(true).module("ibwatch").appName("com.xtc.secretapi")
                .build(LogConfig.Builder.BuildMode.Android, new Object[0]);
    }

    /**
     * 自有密钥构建器。
     */
    public static class OwnKeyBuilder {

        private String key;

        public OwnKeyBuilder setKey(String key) {
            this.key = key;
            return this;
        }
    }
}
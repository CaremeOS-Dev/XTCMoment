package com.xtc.aitext.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.aitext.bean.AICreatStatusBean;
import com.xtc.aitext.bean.ModulePackageBean;
import com.xtc.aitext.constant.Constant;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.ProcessUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * AI 文案模块工具，提供模块支持判断、状态广播与文案校验等能力。
 */
public class AIModuleUtil {

    private static final String TAG = "ai_text_AIModuleUtil";

    private static boolean supportChecked;
    private static boolean supportAiText;

    /** 判断指定包名是否支持 AI 文案。 */
    public static boolean isSupportAIText(Context context, String packageName) {
        if (supportChecked) {
            return supportAiText;
        }
        supportChecked = true;
        LogUtil.d(TAG, "isSupportAIText: packageName = " + packageName);
        if (TextUtils.isEmpty(packageName)) {
            LogUtil.d(TAG, "isSupportAIText: packageName empty");
            return supportAiText;
        }
        String moduleExtra = WatchAccountBase.queryModuleSwitchExtraByInt(context.getApplicationContext(), Constant.MODULE_ID, "");
        LogUtil.d(TAG, "isSupportAIText: limit =" + moduleExtra);
        ModulePackageBean modulePackageBean = JSONUtil.fromJSON(moduleExtra, ModulePackageBean.class);
        LogUtil.d(TAG, "isSupportAIText: modulePackageBean = " + modulePackageBean);
        if (modulePackageBean == null || CollectionUtil.isEmpty(modulePackageBean.getPackageList())) {
            LogUtil.w(TAG, "isSupportAIText: no data");
            return supportAiText;
        }
        Iterator<String> iterator = modulePackageBean.getPackageList().iterator();
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next(), packageName)) {
                supportAiText = true;
                return true;
            }
        }
        return false;
    }

    /** 生成 [0, bound) 的随机数。 */
    public static int randomInt(int bound) {
        return new Random().nextInt(bound);
    }

    /** 广播 AI 创作状态。 */
    public static void notifyCreateStatus(Context context, AICreatStatusBean statusBean) {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_REFRESH_DATA);
        intent.putExtra(Constant.IntentExtras.EXTRA_REFRESH_DATA, JSONUtil.toJSON(statusBean));
        context.sendBroadcast(intent);
    }

    /** 当前进程是否为主进程。 */
    public static boolean isMainProcess(Context context) {
        String processName = ProcessUtils.getCurrentProcessName();
        if (com.xtc.log.util.TextUtils.isEmpty(processName)) {
            return false;
        }
        return processName.equals(context.getPackageName());
    }

    /** 指定类名是否为栈顶 Activity。 */
    public static boolean isTopActivity(Context context, String className) {
        try {
            List<ActivityManager.RunningTaskInfo> runningTasks =
                    ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1);
            if (runningTasks != null && runningTasks.size() > 0) {
                ComponentName topActivity = runningTasks.get(0).topActivity;
                if (topActivity != null) {
                    String topClassName = topActivity.getClassName();
                    LogUtil.d(TAG, "isTopActivity className = " + topClassName);
                    if (Objects.equals(topClassName, className)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "isTopActivity", e);
        }
        return false;
    }

    /** 格式化时间（MM月dd日）。 */
    public static String formatMonthDay(long timeMillis) {
        return new SimpleDateFormat("MM月dd日").format(new Date(timeMillis));
    }

    /** 判断文案中汉字占比是否不低于 70%。 */
    public static boolean isContentConform(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        int length = content.length();
        int hanziCount = 0;
        for (int i = 0; i < length; i++) {
            if (Character.UnicodeScript.of(content.charAt(i)) == Character.UnicodeScript.HAN) {
                hanziCount++;
            }
        }
        double percentage = (hanziCount * 100.0d) / length;
        LogUtil.d(TAG, "isContentConform: totalCount =" + length + ",hanziCount = " + hanziCount
                + ",hanziPercentage = " + percentage);
        return percentage >= 70.0d;
    }
}
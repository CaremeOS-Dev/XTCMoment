package com.xtc.dispatch.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/** Process helpers used by the dispatcher. */
public class Utils {

    private static String processName;

    /** @return true when the current process is the main process of [context]. */
    public static boolean isMainProcess(Context context) {
        String name = getProcessName(context);
        return name != null && !name.contains(":") && name.equals(context.getPackageName());
    }

    private static String getProcessName(Context context) {
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }
        try {
            int pid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info
                    : ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
                if (info.pid == pid) {
                    processName = info.processName;
                    return processName;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        processName = readProcessName();
        return processName;
    }

    private static String readProcessName() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + Process.myPid() + "/cmdline"), "iso-8859-1"));
            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = reader.read()) > 0) {
                builder.append((char) character);
            }
            return builder.toString();
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {
                    // ignored on purpose
                }
            }
        }
    }
}
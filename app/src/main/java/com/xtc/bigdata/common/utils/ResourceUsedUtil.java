package com.xtc.bigdata.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import com.xtc.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * 资源占用采集：读取 /proc 下的 CPU 信息与进程内存信息。
 */
public class ResourceUsedUtil {

    private static final int BUFFER_SIZE = 1000;
    private static final String TAG = "ResourceUsedUtil";

    /**
     * 读取系统整体 CPU 时间，返回 [总时间, 空闲时间]。
     */
    public static long[] getCpuUsedArrayOfSystem() throws Throwable {
        long[] result = {0, 0};
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("/proc/stat"));
            scanner.next();
            long user = scanner.nextLong();
            long nice = scanner.nextLong();
            long system = scanner.nextLong();
            long idle = scanner.nextLong();
            long iowait = scanner.nextLong();
            result[0] = user + nice + system + idle + iowait + scanner.nextLong() + scanner.nextLong();
            result[1] = idle;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return result;
    }

    private static long getCpuUsedOfProc(int pid) throws Throwable {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("/proc/" + pid + "/stat"));
            for (int i = 0; scanner.hasNext() && i < 13; i++) {
                scanner.next();
            }
            return scanner.nextLong() + scanner.nextLong() + scanner.nextLong() + scanner.nextLong();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * 读取指定进程下每个线程的 CPU 时间，格式：pid:time:name,...
     */
    public static String getCpuUsedOfProcThreads(int pid) throws Throwable {
        File taskDir = new File("/proc/" + pid + "/task");
        if (!taskDir.exists()) {
            return "";
        }
        File[] threadFiles = taskDir.listFiles();
        LogUtil.d(TAG, "threadFiles 个数 = " + threadFiles.length);
        String result = "";
        for (File threadFile : threadFiles) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(threadFile, "stat"))), BUFFER_SIZE);
                String line = reader.readLine();
                if (line != null) {
                    int openParen = 0;
                    int closeParen = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == '(' && openParen == 0) {
                            openParen = i;
                        }
                        if (line.charAt(i) == ')' && i > closeParen) {
                            closeParen = i;
                        }
                    }
                    String tid = line.substring(0, openParen - 1);
                    String name = line.substring(openParen + 1, closeParen);
                    String[] fields = line.substring(closeParen + 2, line.length()).split(" ");
                    if (fields.length >= 17) {
                        long cpuTime = Long.parseLong(fields[11]) + Long.parseLong(fields[12]);
                        String escapedName = name.replace(",", "@@@").replace(":", "%%%").replace("\n", "");
                        result = result + tid + ":" + cpuTime + ":" + escapedName + ",";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 读取指定进程的内存占用，返回 [PSS, USS]（单位 KB）。
     */
    public static int[] getMemoryUsedOfProc(int pid, Context context) {
        int[] result = {0, 0};
        if (context == null) {
            return result;
        }
        try {
            Debug.MemoryInfo memoryInfo = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getProcessMemoryInfo(new int[]{pid})[0];
            result[0] = memoryInfo.getTotalPss();
            Method method = memoryInfo.getClass().getDeclaredMethod("getTotalUss", new Class[0]);
            method.setAccessible(true);
            result[1] = ((Integer) method.invoke(memoryInfo, new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
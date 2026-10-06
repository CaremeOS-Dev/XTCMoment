package com.xtc.utils.system;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Step-counter / sensor sysfs helpers. */
public class SensorUtils {

    private static final String TAG = "life";
    private static final String FALLBACK_VALUE = "-1";
    private static final String STEP_REFRESH_PATH = "/sys/class/misc/m_step_c_misc/step_cforce_refresh";
    private static final String ACTIVITY_FLUSH_PATH = "/sys/class/misc/m_act_misc/actflush";
    private static final String STEP_FLUSH_PATH = "/sys/class/misc/m_step_c_misc/step_cflush";
    private static final String RUNNING_FLUSH_PATH = "/sys/class/misc/m_rm_misc/running_moduleflush";
    private static final String CALORIE_FLUSH_PATH = "/sys/class/misc/m_cc_misc/calorie_counterflush";

    private SensorUtils() {
    }

    /** Scales a step count to 75 percent. */
    public static int scaleStepCount(int count) {
        double value = count;
        return (int) (value * 0.75d);
    }

    /** Converts a step count into a distance estimate in metres. */
    public static int stepsToDistance(int steps) {
        double value = steps * 55;
        return (int) ((value * 1.036d) / 1000.0d);
    }

    /** @return true when the step counter requests a force refresh. */
    public static synchronized boolean needsStepRefresh() {
        return "1".equals(readFirstLine(STEP_REFRESH_PATH));
    }

    /** Flushes the activity module. */
    public static synchronized String flushActivity() {
        return readFirstLine(ACTIVITY_FLUSH_PATH);
    }

    /** Flushes the step counter module. */
    public static synchronized String flushStep() {
        return readFirstLine(STEP_FLUSH_PATH);
    }

    /** Flushes the running module. */
    public static synchronized String flushRunning() {
        return readFirstLine(RUNNING_FLUSH_PATH);
    }

    /** Flushes the calorie counter module. */
    public static synchronized String flushCalorie() {
        return readFirstLine(CALORIE_FLUSH_PATH);
    }

    /** Reads the first line of a sysfs node, or "-1" on failure. */
    private static String readFirstLine(String path) {
        try {
            return new BufferedReader(new FileReader(path)).readLine();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return FALLBACK_VALUE;
        }
    }
}
package com.xtc.utils_screenshot_carry_data;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the carry-data (watch id + md5) used to identify screenshots.
 *
 * <p>The value is persisted in the EXIF {@code Make} tag of the image file.
 */
public class ScreenshotUtils {

    /** Separator between the md5 and the timestamp. */
    public static final String SEPARATOR = "-";

    private static final String TAG = "ScreenshotUtils";
    private static final String HTTP_PREFIX = "http:";
    private static final String WEBP_SUFFIX = ".webp";

    private static Map<String, ScreenshotCallback> callbacks = new ConcurrentHashMap<>();
    private static Map<String, ScreenshotMd5Bean> md5Cache = new ConcurrentHashMap<>();
    private static Map<String, String> attributeCache = new ConcurrentHashMap<>();

    private ScreenshotUtils() {
    }

    /** Registers the callback for the given activity. */
    public static void register(Activity activity, ScreenshotCallback callback) {
        if (activity == null) {
            return;
        }
        String name = activity.getClass().getName();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        callbacks.put(name, callback);
    }

    /** Unregisters the callback for the given activity. */
    public static void unregister(Activity activity) {
        if (activity == null) {
            return;
        }
        String name = activity.getClass().getName();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        callbacks.remove(name);
    }

    /** Carry-data reported by the callback registered for the class name. */
    public static String getScreenshotCarryData(String className) {
        ScreenshotCallback callback = callbacks.get(className);
        return callback == null ? "" : callback.getScreenshotCarryData();
    }

    /** Reads and caches the md5 bean stored in the image EXIF data. */
    private static ScreenshotMd5Bean getLocalPathMd5Bean(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        try {
            if (md5Cache.containsKey(path)) {
                return md5Cache.get(path);
            }
            if (attributeCache.containsKey(path)) {
                return null;
            }
            String attribute = getExifMake(path);
            Log.d(TAG, "getLocalPathMd5: attribute=" + attribute);
            if (!TextUtils.isEmpty(attribute)) {
                attributeCache.put(path, attribute);
                ScreenshotMd5Bean bean = (ScreenshotMd5Bean) new Gson().fromJson(attribute, ScreenshotMd5Bean.class);
                if (bean != null) {
                    md5Cache.put(path, bean);
                    return bean;
                }
            } else {
                attributeCache.put(path, "");
            }
            return null;
        } catch (Exception e) {
            Log.d(TAG, "getLocalPathMd5 IOException=" + e.toString());
        }
        return null;
    }

    /** Md5 stored in the image EXIF data, or an empty string. */
    public static String getLocalPathMd5(String path) {
        Log.d(TAG, "getLocalPathMd5: path=" + path);
        ScreenshotMd5Bean bean = getLocalPathMd5Bean(path);
        return bean == null ? "" : bean.getMd5();
    }

    /** Builds the JSON carry-data for the given path and watch id. */
    public static String initScreenshotData(String path, String watchId) {
        Log.d(TAG, "initScreenshotData path=" + path);
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        String md5;
        if (path.startsWith(HTTP_PREFIX)) {
            md5 = parseRemoteMd5(path);
        } else {
            md5 = getLocalPathMd5(path);
        }
        String json = TextUtils.isEmpty(md5) ? "" : new Gson().toJson(new ScreenshotMd5Bean(watchId, md5));
        Log.d(TAG, "initScreenshotData screenshotValue=" + json);
        return json;
    }

    /** Extracts the md5 segment from a remote webp URL. */
    private static String parseRemoteMd5(String url) {
        int suffixIndex = url.indexOf(WEBP_SUFFIX);
        if (suffixIndex <= 0) {
            return null;
        }
        String withoutSuffix = url.substring(0, suffixIndex);
        int underscoreIndex = withoutSuffix.lastIndexOf("_");
        if (underscoreIndex <= 0) {
            return withoutSuffix;
        }
        String tail = withoutSuffix.substring(underscoreIndex + 1);
        return tail.indexOf(SEPARATOR) > 0 ? tail.substring(0, tail.indexOf(SEPARATOR)) : tail;
    }

    /** Class name of the current top activity. */
    public static String getTopActivityClassName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return "";
        }
        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
        String className = componentName.getClassName();
        Log.d(TAG, "getClassName pkg:" + componentName.getPackageName());
        Log.d(TAG, "getClassName cls:" + className);
        return className;
    }

    /** Writes the carry-data into the EXIF {@code Make} tag. */
    public static void saveCarryData(String imageFilePath, String value) {
        Log.d(TAG, "saveNoteValue mImageFilePath=" + imageFilePath + " value+" + value);
        try {
            ExifInterface exifInterface = new ExifInterface(imageFilePath);
            exifInterface.setAttribute(ExifInterface.TAG_MAKE, value);
            exifInterface.saveAttributes();
        } catch (Exception e) {
            Log.e(TAG, "saveNoteValue IOException ", e);
        }
    }

    /** JSON representation of the cached md5 bean for {@code path}. */
    public static String getScreenshotMd5Json(String path) {
        ScreenshotMd5Bean bean = getLocalPathMd5Bean(path);
        return bean == null ? "" : new Gson().toJson(bean);
    }

    /** Builds {@code md5-timestamp} for the given remote url. */
    public static String buildCarryData(String prefix, String remoteUrl) {
        String md5 = getLocalPathMd5(remoteUrl);
        if (TextUtils.isEmpty(md5)) {
            return prefix;
        }
        return truncateMd5(md5) + SEPARATOR + currentTimestamp();
    }

    /** Truncates the md5 at the first separator. */
    public static String truncateMd5(String md5) {
        if (md5 == null) {
            return "";
        }
        return md5.contains(SEPARATOR) ? md5.substring(0, md5.indexOf(SEPARATOR)) : md5;
    }

    /** Resolves the carry-data, preferring the JSON value and falling back to the file. */
    public static String resolveCarryData(String json, String fallback, String path) {
        String md5 = "";
        if (!TextUtils.isEmpty(json)) {
            try {
                ScreenshotMd5Bean bean = (ScreenshotMd5Bean) new Gson().fromJson(json, ScreenshotMd5Bean.class);
                if (bean != null) {
                    md5 = bean.getMd5();
                }
            } catch (Exception e) {
                Log.e(TAG, "getCarryMd5String: trackValue ", e);
            }
        } else {
            md5 = getLocalPathMd5(path);
        }
        if (TextUtils.isEmpty(md5)) {
            return fallback;
        }
        return truncateMd5(md5) + SEPARATOR + currentTimestamp();
    }

    /** Reads the EXIF {@code Make} tag of the file. */
    public static String getExifMake(String path) {
        try {
            return new ExifInterface(path).getAttribute(ExifInterface.TAG_MAKE);
        } catch (Exception e) {
            Log.e(TAG, "saveNoteValue IOException= ", e);
            return "";
        }
    }

    /** Current time in milliseconds as a string. */
    public static String currentTimestamp() {
        String timestamp = String.valueOf(new Date().getTime());
        Log.d(TAG, "getTimestamp=" + timestamp);
        return timestamp;
    }
}
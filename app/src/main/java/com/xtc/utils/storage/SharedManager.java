package com.xtc.utils.storage;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVHandler;
import com.tencent.mmkv.MMKVLogLevel;
import com.tencent.mmkv.MMKVRecoverStrategic;
import com.xtc.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MMKV backed key/value store with a one-off migration from the legacy
 * {@link SharedPreferences} files.
 */
public class SharedManager implements MMKVHandler {

    private static final String TAG = "SharedManager";
    /** MMKV single-process mode. */
    private static final int SINGLE_PROCESS_MODE = 2;
    /** Log level used when redirecting MMKV logs to the app logger. */
    private static final int LEVEL_DEBUG = 1;
    private static final int LEVEL_INFO = 2;
    private static final int LEVEL_WARNING = 3;
    private static final int LEVEL_ERROR = 4;
    private static final int LEVEL_NONE = 5;

    private static String packageName = "";
    private static SharedManager instance = null;
    private static volatile boolean initialized = false;

    /** No-op kept for symmetry with the original hook. */
    private void onValueChanged(String file, String key, Object value) {
    }

    @Override
    public boolean wantLogRedirecting() {
        return true;
    }

    private SharedManager(Context context) {
        if (initialized) {
            return;
        }
        String dir = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/mmkv";
        LogUtil.i(TAG, "context pack name = " + context.getApplicationContext().getPackageName());
        MMKV.initialize(dir);
        packageName = context.getApplicationContext().getPackageName();
        migrate(context, new ArrayList<String>() {{
            add(SharedManager.packageName);
        }});
        initialized = true;
        LogUtil.i(TAG, "mmkv 初始化完成, filePath = " + dir);
    }

    /** Migrates the default preferences file. */
    public synchronized void init(Context context) {
        migrateAll(context.getApplicationContext(), (List<String>) null);
    }

    /** Migrates the given preferences files. */
    public synchronized void init(Context context, List<String> names) {
        migrateAll(context, names);
    }

    /** Migrates the given preferences files into {@code targetName}. */
    public synchronized void init(Context context, List<String> names, String targetName) {
        migrateAll(context, names);
    }

    private void migrateAll(Context context, List<String> names) {
        LogUtil.i(TAG, "initAndMigrateSpFile...");
        migrateEach(context, names);
    }

    /** Explicit migration entry point. */
    public void migrate(Context context, List<String> names) {
        migrateEach(context, names);
    }

    /** Migrates a single preferences file into {@code targetName}. */
    public void migrate(Context context, String sourceName, String targetName) {
        migrateOne(context, sourceName, targetName);
    }

    /** Returns the singleton, creating it on first use. */
    public static SharedManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedManager.class) {
                if (instance == null) {
                    instance = new SharedManager(context);
                }
            }
        }
        return instance;
    }

    /** Unwraps context wrappers until an {@link Activity} is found. */
    private static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private MMKV mmkv(String name) {
        return MMKV.mmkvWithID(name, SINGLE_PROCESS_MODE);
    }

    private void migrateEach(Context context, List<String> names) {
        LogUtil.i(TAG, context.getPackageName() + ", importSharedPreferences1...");
        if (names == null) {
            return;
        }
        for (String name : names) {
            SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(name, 0);
            if (isEmpty(preferences)) {
                LogUtil.i(TAG, name + " 旧SP文件为空，不需要重复迁移...");
                LogUtil.i(TAG, name + " mmkv total Key size = " + mmkv(name).count());
                return;
            }
            LogUtil.i(TAG, "mmkv, 开始迁移文件 = " + name);
            mmkv(name).importFromSharedPreferences(preferences);
            LogUtil.i(TAG, name + " 迁移文件完成，mmkv total Key size = " + mmkv(name).count());
            preferences.edit().clear().commit();
        }
    }

    private void migrateOne(Context context, String sourceName, String targetName) {
        LogUtil.i(TAG, "importSharedPreferences2... " + targetName);
        SharedPreferences preferences = context.getApplicationContext().getSharedPreferences(sourceName, 0);
        if (isEmpty(preferences)) {
            LogUtil.i(TAG, sourceName + " 旧SP文件为空，不需要重复迁移...");
            LogUtil.i(TAG, targetName + " mmkv total Key size = " + mmkv(targetName).count());
            return;
        }
        LogUtil.i(TAG, "mmkv, 开始迁移文件 = " + sourceName);
        mmkv(targetName).importFromSharedPreferences(preferences);
        LogUtil.i(TAG, targetName + " 迁移文件完成，mmkv total Key size = " + mmkv(targetName).count());
        preferences.edit().clear().commit();
    }

    private boolean isEmpty(SharedPreferences preferences) {
        Map<String, ?> all = preferences.getAll();
        return all == null || all.size() <= 0;
    }

    public boolean containsKey(String key) {
        return mmkv(packageName).containsKey(key);
    }

    public boolean containsKey(String name, String key) {
        return mmkv(name).containsKey(key);
    }

    public boolean putInt(String key, int value) {
        onValueChanged(packageName, key, Integer.valueOf(value));
        return mmkv(packageName).encode(key, value);
    }

    public boolean putInt(String name, String key, int value) {
        onValueChanged(name, key, Integer.valueOf(value));
        return mmkv(name).encode(key, value);
    }

    public int getInt(String key, int defaultValue) {
        return mmkv(packageName).decodeInt(key, defaultValue);
    }

    public int getInt(String name, String key, int defaultValue) {
        return mmkv(name).decodeInt(key, defaultValue);
    }

    public boolean putLong(String key, long value) {
        onValueChanged(packageName, key, Long.valueOf(value));
        return mmkv(packageName).encode(key, value);
    }

    public boolean putLong(String name, String key, long value) {
        onValueChanged(name, key, Long.valueOf(value));
        return mmkv(name).encode(key, value);
    }

    public long getLong(String key, long defaultValue) {
        return mmkv(packageName).decodeLong(key, defaultValue);
    }

    public long getLong(String name, String key, long defaultValue) {
        return mmkv(name).decodeLong(key, defaultValue);
    }

    public boolean putString(String key, String value) {
        onValueChanged(packageName, key, value);
        return mmkv(packageName).encode(key, value);
    }

    public boolean putString(String name, String key, String value) {
        onValueChanged(name, key, value);
        return mmkv(name).encode(key, value);
    }

    public String getString(String key, String defaultValue) {
        return mmkv(packageName).decodeString(key, defaultValue);
    }

    public String getString(String name, String key, String defaultValue) {
        return mmkv(name).decodeString(key, defaultValue);
    }

    public boolean putBoolean(String key, boolean value) {
        onValueChanged(packageName, key, Boolean.valueOf(value));
        return mmkv(packageName).encode(key, value);
    }

    public boolean putBoolean(String name, String key, boolean value) {
        onValueChanged(name, key, Boolean.valueOf(value));
        return mmkv(name).encode(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mmkv(packageName).decodeBool(key, defaultValue);
    }

    public boolean getBoolean(String name, String key, boolean defaultValue) {
        return mmkv(name).decodeBool(key, defaultValue);
    }

    public boolean putFloat(String key, float value) {
        onValueChanged(packageName, key, Float.valueOf(value));
        return mmkv(packageName).encode(key, value);
    }

    public boolean putFloat(String name, String key, float value) {
        onValueChanged(name, key, Float.valueOf(value));
        return mmkv(name).encode(key, value);
    }

    public float getFloat(String key, float defaultValue) {
        return mmkv(packageName).decodeFloat(key, defaultValue);
    }

    public float getFloat(String name, String key, float defaultValue) {
        return mmkv(name).decodeFloat(key, defaultValue);
    }

    public boolean remove(String key) {
        mmkv(packageName).removeValueForKey(key);
        return true;
    }

    public boolean remove(String name, String key) {
        mmkv(name).removeValueForKey(key);
        return true;
    }

    /** Clears the default store. */
    public void clear() {
        LogUtil.i(TAG, packageName + " mmkve file clearAll...");
        mmkv(packageName).clearAll();
    }

    /** Clears the named store. */
    public void clear(String name) {
        LogUtil.i(TAG, name + " mmkve file clearAll...");
        mmkv(name).clearAll();
    }

    /** Synchronises the default store. */
    public void sync() {
        mmkv(packageName).sync();
    }

    /** Synchronises the named store. */
    public void sync(String name) {
        mmkv(name).sync();
    }

    /** Total size of the default store, synchronising first. */
    public long totalSize() {
        long size = mmkv(packageName).totalSize();
        LogUtil.i(TAG, packageName + " mmkv file sync, total size = " + size);
        return size;
    }

    /** Total size of the named store, synchronising first. */
    public long totalSize(String name) {
        long size = mmkv(name).totalSize();
        LogUtil.i(TAG, name + " mmkv file sync, total size = " + size);
        return size;
    }

    @Override
    public MMKVRecoverStrategic onMMKVCRCCheckFail(String mmapID) {
        return MMKVRecoverStrategic.OnErrorRecover;
    }

    @Override
    public MMKVRecoverStrategic onMMKVFileLengthError(String mmapID) {
        return MMKVRecoverStrategic.OnErrorRecover;
    }

    @Override
    public void mmkvLog(MMKVLogLevel level, String file, int line, String function, String message) {
        String text = "MMKVLogLevel : " + level.name() + " <" + file + ":" + line + "::" + function + "> " + message;
        switch (level) {
            case LevelDebug:
                LogUtil.d("redirect logging MMKV", text);
                break;
            case LevelInfo:
                LogUtil.i("redirect logging MMKV", text);
                break;
            case LevelWarning:
                LogUtil.w("redirect logging MMKV", text);
                break;
            case LevelError:
            case LevelNone:
            default:
                LogUtil.e("redirect logging MMKV", text);
                break;
        }
    }

    /** Copies every value of the named store into native SharedPreferences. */
    public void backup(Context context, String name) {
        String[] keys = mmkv(name).allKeys();
        if (keys == null || keys.length == 0) {
            LogUtil.i(TAG, name + " mmkv total size = 0, 不需要备份...");
            return;
        }
        LogUtil.i(TAG, name + " mmkv file 准备备份...");
        LogUtil.i(TAG, name + " mmkv file key size = " + keys.length);
        for (String key : keys) {
            Object value = readValue(mmkv(name), key);
            if (key != null && value != null) {
                if (value instanceof Boolean) {
                    Boolean bool = (Boolean) value;
                    NativeSharedManager.getInstance(context, name).putBoolean(key, bool.booleanValue());
                    LogUtil.i(TAG, "mmkv, key = " + key + ", value = " + bool);
                } else if (value instanceof Integer) {
                    Integer integer = (Integer) value;
                    NativeSharedManager.getInstance(context, name).putInt(key, integer.intValue());
                    LogUtil.i(TAG, "mmkv, key = " + key + ", value = " + integer);
                } else if (value instanceof Long) {
                    Long longValue = (Long) value;
                    NativeSharedManager.getInstance(context, name).putLong(key, longValue.longValue());
                    LogUtil.i(TAG, "mmkv, key = " + key + ", value = " + longValue);
                } else if (value instanceof Float) {
                    Float floatValue = (Float) value;
                    NativeSharedManager.getInstance(context, name).putFloat(key, floatValue.floatValue());
                    LogUtil.i(TAG, "mmkv, key = " + key + ", value = " + floatValue);
                } else if (value instanceof String) {
                    String stringValue = (String) value;
                    NativeSharedManager.getInstance(context, name).putString(key, stringValue);
                    LogUtil.i(TAG, "mmkv, key = " + key + ", value = " + stringValue);
                } else {
                    LogUtil.i(TAG, "unknown type: " + value.getClass());
                }
            }
        }
    }

    /** Copies the named store's files to {@code name + "_backup"}. */
    public void backupFiles(Context context, String name) {
        String[] keys = mmkv(name).allKeys();
        if (keys == null || keys.length == 0) {
            LogUtil.i(TAG, name + " mmkv total size = 0, 不需要备份...");
            return;
        }
        String backupName = name + "_backup";
        LogUtil.i(TAG, backupName + ", mmkv 文件备份结果 = " + copyFile(context, name, backupName));
        String crcName = name + ".crc";
        String crcBackupName = name + ".crc";
        LogUtil.i(TAG, crcBackupName + ", mmkv 文件备份结果 = " + copyFile(context, crcName, crcBackupName));
    }

    private boolean copyFileByStream(Context context, String name, String backupName) {
        String source = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/mmkv/" + name;
        String target = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/mmkv/" + backupName;
        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int read = inputStream.read(buffer);
            if (read != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            makeWritable(new File(target));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean copyFile(Context context, String name, String backupName) {
        String source = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/mmkv/" + name;
        String target = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/mmkv/" + backupName;
        try {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(target);
            FileChannel sourceChannel = inputStream.getChannel();
            FileChannel targetChannel = outputStream.getChannel();
            sourceChannel.transferTo(0L, sourceChannel.size(), targetChannel);
            inputStream.close();
            sourceChannel.close();
            outputStream.close();
            targetChannel.close();
            makeWritable(new File(target));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Ensures the file is world readable/writable, running {@code su} if needed. */
    private void makeWritable(File file) {
        if (file.canRead() && file.canWrite()) {
            return;
        }
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/su");
            process.getOutputStream().write(("chmod 777 " + file.getAbsolutePath() + "\nexit\n").getBytes());
            if (process.waitFor() == 0 && file.canRead() && file.canWrite()) {
                return;
            }
            throw new SecurityException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException();
        }
    }

    /** Reads a value of unknown type from the MMKV store. */
    private Object readValue(MMKV mmkv, String key) {
        String stringValue = mmkv.decodeString(key);
        if (!TextUtils.isEmpty(stringValue)) {
            return stringValue.charAt(0) == 1 ? mmkv.decodeStringSet(key) : stringValue;
        }
        Set<String> stringSet = mmkv.decodeStringSet(key);
        if (stringSet != null && stringSet.size() == 0) {
            Float floatValue = Float.valueOf(mmkv.decodeFloat(key));
            return (Float.compare(floatValue.floatValue(), 0.0f) == 0
                    || Float.compare(floatValue.floatValue(), Float.NaN) == 0)
                    ? Double.valueOf(mmkv.decodeDouble(key)) : floatValue;
        }
        int intValue = mmkv.decodeInt(key);
        long longValue = mmkv.decodeLong(key);
        if (intValue != longValue) {
            return Long.valueOf(longValue);
        }
        return Integer.valueOf(intValue);
    }
}
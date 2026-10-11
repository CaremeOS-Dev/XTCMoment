package com.xtc.moment.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.security.MessageDigest;

/**
 * 临时诊断工具：把「密钥派发链路」的关键中间值打到 logcat，tag 前缀 RAWDUMP。
 *
 * <p>用途：在密钥派发被签名校验拦截（ae 为 null）时，能直接看出是 provider 被拒、
 * 还是本地解析失败，从而定位 000016。</p>
 *
 * <p>仅在 debug（可调试）包生效：release 包在 {@link #init(Context)} 里判定为
 * {@code debuggable=false} 后，所有方法直接返回，不产生任何日志与额外开销。</p>
 */
public final class RawDump {

    /** 统一 tag 前缀，方便 adb logcat 过滤。 */
    public static final String TAG = "RAWDUMP";

    /** logcat 单条上限约 4000 字符，超长内容分片输出。 */
    private static final int CHUNK = 1500;

    /** 是否启用诊断：只有 debug（可调试）包才为 true，release 包恒为 false。 */
    private static volatile boolean enabled;

    private RawDump() {
    }

    /**
     * 依据「当前 APK 是否可调试」决定诊断开关。
     *
     * <p>debug 构建由 AGP 自动写入 {@code android:debuggable="true"}，release 构建为
     * false，因此这里天然区分两种包型，且不需要引入 BuildConfig。</p>
     */
    public static void init(Context context) {
        boolean debug = false;
        try {
            if (context != null) {
                debug = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            }
        } catch (Throwable ignored) {
            debug = false;
        }
        enabled = debug;
    }

    /** @return 当前是否为启用诊断的 debug 包。 */
    public static boolean isEnabled() {
        return enabled;
    }

    /** 输出一条带子标签的诊断信息，绝不影响主流程。 */
    public static void log(String section, String message) {
        if (!enabled) {
            return;
        }
        try {
            String line = "[" + section + "] " + message;
            int length = line.length();
            if (length <= CHUNK) {
                Log.i(TAG, line);
                return;
            }
            int index = 0;
            int part = 0;
            while (index < length) {
                int end = Math.min(index + CHUNK, length);
                Log.i(TAG, "[" + section + "][" + part + "] " + line.substring(index, end));
                index = end;
                part++;
            }
        } catch (Throwable ignored) {
            // 诊断代码不允许影响业务
        }
    }

    /** 输出当前进程 APK 的签名摘要，用于和原厂签名比对。 */
    public static void logApkSignature(Context context) {
        if (!enabled) {
            return;
        }
        try {
            if (context == null) {
                log("signature", "context is null");
                return;
            }
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Signature[] signatures = info.signatures;
            log("signature", "package=" + context.getPackageName()
                    + " signatureCount=" + (signatures == null ? -1 : signatures.length));
            if (signatures == null) {
                return;
            }
            for (int i = 0; i < signatures.length; i++) {
                byte[] raw = signatures[i].toByteArray();
                log("signature", "[" + i + "] md5=" + digest("MD5", raw)
                        + " sha1=" + digest("SHA1", raw)
                        + " sha256=" + digest("SHA256", raw)
                        + " len=" + raw.length);
            }
        } catch (Throwable t) {
            log("signature", "error: " + t);
        }
    }

    /**
     * dump 全量模块开关表（不依赖具体模块号，兼容不同桌面版本）。
     *
     * <p>签名校验用的模块号在不同 launcher 版本里可能是 3367 也可能是别的值，
     * 所以这里整表输出，同时把各模块的 display 语义翻译出来。</p>
     */
    public static void logAllModuleSwitches(Context context) {
        if (!enabled) {
            return;
        }
        Cursor cursor = null;
        try {
            if (context == null) {
                log("module", "context is null");
                return;
            }
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://com.xtc.moduledata/query");
            log("module", "query all uri=" + uri);
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor == null) {
                log("module", "cursor=null (provider 无返回)");
                return;
            }
            log("module", "rows=" + cursor.getCount()
                    + " columns=" + java.util.Arrays.toString(cursor.getColumnNames()));
            if (!cursor.moveToFirst()) {
                log("module", "rows=0");
                return;
            }
            do {
                StringBuilder builder = new StringBuilder("row: ");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    builder.append(cursor.getColumnName(i)).append('=')
                            .append(cursor.getString(i)).append(' ');
                }
                builder.append("→ display 语义: ").append(describeDisplay(cursor));
                log("module", builder.toString());
            } while (cursor.moveToNext());
        } catch (Throwable t) {
            log("module", "query all error: " + t);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * 用带 Version 查询参数的方式再查一次 init-service provider。
     *
     * <p>新版启动器（14.6.x）的 provider 会读 uri 上的 {@code Version} 参数，
     * 两种查法都可能出现"被签名校验拦掉返回 null"的情况，这里都打出来对比。</p>
     */
    public static void logInitServiceProviderWithVersion(Context context) {
        if (!enabled) {
            return;
        }
        Cursor cursor = null;
        try {
            if (context == null) {
                log("provider", "context is null");
                return;
            }
            Uri uri = Uri.parse("content://com.xtc.initservice/item")
                    .buildUpon().appendQueryParameter("Version", "2").build();
            log("provider", "query uri(Version=2)=" + uri);
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                log("provider", "Version=2 cursor=null ← 被拦截或无返回");
                return;
            }
            log("provider", "Version=2 columns=" + cursor.getCount() + "x" + cursor.getColumnCount());
            if (!cursor.moveToFirst()) {
                log("provider", "Version=2 rows=0");
                return;
            }
            do {
                StringBuilder builder = new StringBuilder("Version=2 row: ");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String name = cursor.getColumnName(i);
                    builder.append(name).append('=')
                            .append(summarize(name, cursor.getString(i))).append(' ');
                }
                log("provider", builder.toString());
            } while (cursor.moveToNext());
        } catch (Throwable t) {
            log("provider", "Version=2 error: " + t);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** 把 display 数值翻译成可读语义（0=VISIBLE 启用，1=INVISIBLE 关闭，2=VISIBLE_USELESS）。 */
    private static String describeDisplay(Cursor cursor) {
        try {
            if (cursor == null || cursor.isBeforeFirst() || cursor.isAfterLast()) {
                return "无当前行";
            }
            int index = cursor.getColumnIndex("display");
            if (index < 0) {
                return "无 display 列";
            }
            int value = cursor.getInt(index);
            if (value == 0) {
                return "0=VISIBLE(启用，签名校验生效)";
            }
            if (value == 1) {
                return "1=INVISIBLE(关闭，签名校验不生效)";
            }
            if (value == 2) {
                return "2=VISIBLE_USELESS(启用，签名校验生效)";
            }
            return String.valueOf(value);
        } catch (Throwable t) {
            return "err:" + t;
        }
    }

    /**
     * 读取启动器的模块开关，用于判断签名校验是否处于开启状态。
     *
     * @param module 模块号，例如 3367（provider 签名校验开关）
     */
    public static void logModuleSwitch(Context context, int module) {
        if (!enabled) {
            return;
        }
        Cursor cursor = null;
        try {
            if (context == null) {
                log("module", "context is null, module=" + module);
                return;
            }
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://com.xtc.moduledata/query");
            cursor = resolver.query(uri, null, "module=" + module, null, null);
            if (cursor == null) {
                log("module", "module=" + module + " cursor=null (provider 无返回)");
                return;
            }
            if (!cursor.moveToFirst()) {
                log("module", "module=" + module + " rows=0");
                return;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("module=").append(module)
                    .append(" rows=").append(cursor.getCount());
            do {
                builder.append(" | ");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    builder.append(cursor.getColumnName(i)).append('=')
                            .append(cursor.getString(i)).append(' ');
                }
                builder.append("→ ").append(describeDisplay(cursor));
            } while (cursor.moveToNext());
            log("module", builder.toString());
        } catch (Throwable t) {
            log("module", "module=" + module + " error: " + t);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** 读取 init-service provider 的原始返回，逐列打印（ae 是否为空是核心判据）。 */
    public static void logInitServiceProvider(Context context) {
        if (!enabled) {
            return;
        }
        Cursor cursor = null;
        try {
            if (context == null) {
                log("provider", "context is null");
                return;
            }
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://com.xtc.initservice/item");
            log("provider", "query uri=" + uri);
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor == null) {
                log("provider", "cursor=null ← provider 直接返回 null（极可能被签名校验拦截）");
                return;
            }
            log("provider", "columns=" + cursor.getCount() + "x" + cursor.getColumnCount()
                    + " names=" + java.util.Arrays.toString(cursor.getColumnNames()));
            if (!cursor.moveToFirst()) {
                log("provider", "rows=0 ← 查到但没有数据行");
                return;
            }
            do {
                StringBuilder builder = new StringBuilder("row: ");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String name = cursor.getColumnName(i);
                    String value = cursor.getString(i);
                    builder.append(name).append('=').append(summarize(name, value)).append(' ');
                }
                log("provider", builder.toString());
            } while (cursor.moveToNext());
        } catch (Throwable t) {
            log("provider", "error: " + t);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** 长字段做长度摘要，短字段原样输出。 */
    private static String summarize(String column, String value) {
        if (value == null) {
            return "<null>";
        }
        if (value.length() == 0) {
            return "<empty>";
        }
        boolean shortField = "id".equals(column) || "tag".equals(column)
                || "grey".equals(column) || "encSwitch".equals(column) || "ts".equals(column);
        if (shortField || value.length() <= 64) {
            return value;
        }
        return "len=" + value.length() + ",head=" + value.substring(0, 32)
                + ",tail=" + value.substring(value.length() - 12);
    }

    private static String digest(String algorithm, byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] result = digest.digest(data);
            StringBuilder builder = new StringBuilder(result.length * 2);
            for (byte b : result) {
                String hex = Integer.toHexString(b & 0xFF);
                if (hex.length() < 2) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString().toUpperCase();
        } catch (Throwable t) {
            return "err:" + t;
        }
    }

    /** 把可能为 null 的字符串安全地描述出来。 */
    public static String describe(String value) {
        if (value == null) {
            return "<null>";
        }
        if (TextUtils.isEmpty(value)) {
            return "<empty>";
        }
        return "len=" + value.length() + ",value=" + value;
    }
}

package com.xtc.httplib.confupdate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.bean.BaseSchedulerTask;
import com.xtc.log.LogUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tracks the {@code Conf-Updates} header and dispatches the matching local broadcasts
 * once the configured delay has elapsed.
 */
public class ConfUpdatesInfoManager {

    private static final String TAG = LogTag.tag("ConfUpdatesInfoManager");
    private static final AtomicReference<ConfUpdatesInfoManager> INSTANCE = new AtomicReference<>();
    private static int[] confInfos;

    private final Context context;
    private final int flagReceiverIncludeBackground = 16777216;
    private final ArrayMap<Integer, BaseSchedulerTask> schedulerTaskArray = new ArrayMap<>();

    public static ConfUpdatesInfoManager getInstance(Context context) {
        ConfUpdatesInfoManager manager;
        do {
            ConfUpdatesInfoManager cached = INSTANCE.get();
            if (cached != null) {
                return cached;
            }
            manager = new ConfUpdatesInfoManager(context);
        } while (!INSTANCE.compareAndSet(null, manager));
        return manager;
    }

    public ConfUpdatesInfoManager(Context context) {
        this.context = context;
    }

    /** Registers the config types that may be pushed by the server. */
    public void putConfInfo(int... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        for (int type : types) {
            if (type <= 0) {
                throw new IllegalArgumentException("数组值必须大于0");
            }
        }
        confInfos = types;
    }

    /**
     * Parses the {@code Conf-Updates} bit mask and version list, scheduling a broadcast for
     * every config type whose bit is set and whose version increased.
     */
    public boolean updateConf(String confBits, String confVersions) {
        if (TextUtils.isEmpty(confBits) || TextUtils.isEmpty(confVersions)) {
            return false;
        }
        try {
            int bits = Integer.parseInt(confBits, 2);
            String[] versions = confVersions.split(",");
            int[] types = confInfos;
            if (types == null || types.length == 0) {
                return false;
            }
            try {
                int delayMinutes = Integer.parseInt(versions[versions.length - 1]);
                for (int i = 0; i < types.length; i++) {
                    int confType = types[i];
                    int bitIndex = confType - 1;
                    int versionIndex = versions.length - confType - 1;
                    if (versionIndex < 0 || versionIndex >= versions.length) {
                        continue;
                    }
                    if (!this.schedulerTaskArray.containsKey(confType)) {
                        this.schedulerTaskArray.put(confType, BaseSchedulerTask.createTask(confType,
                                parseVersion(versions[versionIndex]), SystemClock.elapsedRealtime(),
                                ((long) delayMinutes * 60) * 1000));
                    }
                    if (((bits >> bitIndex) & 1) == 1) {
                        int version = parseVersion(versions[versionIndex]);
                        BaseSchedulerTask task = this.schedulerTaskArray.get(confType);
                        if (version > task.getVersion()) {
                            task.updateTask(version, SystemClock.elapsedRealtime(),
                                    ((long) delayMinutes * 60) * 1000, true);
                        }
                    }
                }
                return false;
            } catch (NumberFormatException unused) {
                return false;
            }
        } catch (NumberFormatException unused2) {
            return false;
        }
    }

    private static int parseVersion(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            LogUtil.e(TAG, ": ", e);
            return 0;
        }
    }

    /** Sends the pending config-update broadcasts. */
    public void dispathUpdatesToClient() {
        Set<Map.Entry<Integer, BaseSchedulerTask>> entries = this.schedulerTaskArray.entrySet();
        if (entries.isEmpty()) {
            return;
        }
        for (Map.Entry<Integer, BaseSchedulerTask> entry : entries) {
            BaseSchedulerTask task = entry.getValue();
            if (!task.isToDispatch()) {
                continue;
            }
            long offsetTime = SystemClock.elapsedRealtime() - task.getSetTime();
            LogUtil.d(TAG, "dispathUpdatesToClient: offsetTime = " + offsetTime + "; task" + task);
            if (offsetTime > task.getDelayTime()) {
                task.setToDispatch(false);
                sendBroadcast(this.context, entry.getKey());
            } else if (offsetTime < 0) {
                task.setToDispatch(false);
            }
        }
    }

    private boolean sendBroadcast(Context context, int confType) {
        if (context == null) {
            LogUtil.d(TAG, "sendBroadcast: context == null");
            return false;
        }
        Intent intent = new Intent(HttpConfUpdate.ACTION_HTTP_CONF_UPDATE,
                Uri.parse(HttpConfUpdate.SCHEME_HTTP_CONF_UPDATE + confType));
        intent.addFlags(this.flagReceiverIncludeBackground);
        String packageName = context.getPackageName();
        LogUtil.d(TAG, "sendBroadcast: packageName = " + packageName);
        intent.setPackage(packageName);
        context.sendBroadcast(intent);
        return true;
    }
}
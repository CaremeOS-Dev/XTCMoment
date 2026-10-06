package com.xtc.system.account;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.bean.FunExtra;
import com.xtc.system.account.bean.FunSwitch;
import com.xtc.system.account.bean.NetFunItem;
import com.xtc.system.account.constant.ActionConstants;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Observes the fundata content providers and notifies registered callbacks when a
 * feature switch changes.
 */
public class FunManager {

    private static final AtomicReference<FunManager> INSTANCE = new AtomicReference<>();

    private static final String TAG = "FunManager";
    private static final String FUN_EXTRA_URI = "content://com.xtc.fundata/FunExtra/";
    private static final String FUN_SWITCH_URI = "content://com.xtc.fundata/FunSwitch";

    private final Handler backThreadHandler;
    private final Context context;
    private final ConcurrentHashMap<String, FunDataCallback> funSwitchDataCallback = new ConcurrentHashMap<>();
    private FunSwitchInitReceiver funSwitchInitReceiver;

    /** Callback for feature-switch changes. */
    public interface FunDataCallback {

        void funDataChanged(FunSwitch funSwitch);

        void queryFunCompleted(FunSwitch funSwitch);

        void syncAllFunCompleted(FunSwitch funSwitch);
    }

    /** Feature-switch status values. */
    public interface SwitchStatus {
        int SWITCH_STATUS_CLOSE = 1;
        int SWITCH_STATUS_OPEN = 0;
    }

    private FunManager(Context context) {
        this.context = context.getApplicationContext();
        HandlerThread handlerThread = new HandlerThread("protocol");
        handlerThread.start();
        this.backThreadHandler = new Handler(handlerThread.getLooper());
        context.getContentResolver().registerContentObserver(Uri.parse(FUN_SWITCH_URI), true, new ContentObserver(new Handler(handlerThread.getLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                LogUtil.i(TAG, "FunSwitch onChange uri = " + uri);
                String appPackageName = uri.getQueryParameter("appPackageName");
                if (TextUtils.isEmpty(appPackageName)) {
                    return;
                }
                notifyChangeByAppPackage(appPackageName);
            }
        });
        context.getContentResolver().registerContentObserver(Uri.parse(FUN_EXTRA_URI), true, new ContentObserver(new Handler(handlerThread.getLooper())) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                LogUtil.i(TAG, "FunSwitchExtra onChange uri = " + uri);
                String item = uri.getQueryParameter("item");
                if (TextUtils.isEmpty(item)) {
                    return;
                }
                notifyChangeByFunItem(JSONUtil.fromJSON(item, NetFunItem.class));
            }
        });
    }

    /** Notifies the callback of the package owning {@code netFunItem}. */
    private void notifyChangeByFunItem(NetFunItem netFunItem) {
        ConcurrentHashMap<String, FunDataCallback> callbacks = funSwitchDataCallback;
        FunDataCallback callback;
        if (callbacks == null || (callback = callbacks.get(netFunItem.getAppPackage())) == null) {
            return;
        }
        FunSwitch funSwitch = WatchAccountBase.queryFunSwitchByPackageName(context, netFunItem.getAppPackage(), false);
        List<NetFunItem> items = new ArrayList<>();
        items.add(netFunItem);
        funSwitch.setItem(items);
        callback.funDataChanged(funSwitch);
    }

    private void notifyChangeAll() {
        ConcurrentHashMap<String, FunDataCallback> callbacks = funSwitchDataCallback;
        if (callbacks != null) {
            for (String appPackage : callbacks.keySet()) {
                FunDataCallback callback = funSwitchDataCallback.get(appPackage);
                if (callback != null) {
                    callback.funDataChanged(WatchAccountBase.queryFunSwitchByPackageName(context, appPackage, false));
                }
            }
        }
    }

    /** Notifies the callback registered for {@code appPackage}. */
    private void notifyChangeByAppPackage(String appPackage) {
        ConcurrentHashMap<String, FunDataCallback> callbacks = funSwitchDataCallback;
        FunDataCallback callback;
        if (callbacks == null || (callback = callbacks.get(appPackage)) == null) {
            return;
        }
        FunSwitch funSwitch = WatchAccountBase.queryFunSwitchByPackageName(context, appPackage, false);
        funSwitch.setItem(makeNetFunItem(
                WatchAccountBase.queryFunSwitchItemByPackageName(context, appPackage),
                WatchAccountBase.queryFunSwitchExtraByPackageName(context, appPackage)));
        callback.funDataChanged(funSwitch);
    }

    /** @return the shared singleton bound to {@code context}. */
    public static FunManager getInstance(Context context) {
        FunManager manager;
        do {
            FunManager existing = INSTANCE.get();
            if (existing != null) {
                return existing;
            }
            manager = new FunManager(context);
        } while (!INSTANCE.compareAndSet(null, manager));
        return manager;
    }

    /** Registers {@code funDataCallback} for {@code appPackage}. */
    public synchronized void registerFunDataCallback(String appPackage, FunDataCallback funDataCallback) {
        if (appPackage == null || funDataCallback == null) {
            return;
        }
        if (funSwitchDataCallback.isEmpty()) {
            registerFunSwitchInitReceiver();
        }
        funSwitchDataCallback.put(appPackage, funDataCallback);
    }

    /** Unregisters the callback registered for {@code appPackage}. */
    public synchronized void unregisterFunDataCallback(String appPackage) {
        if (appPackage == null) {
            return;
        }
        if (funSwitchDataCallback.containsKey(appPackage)) {
            funSwitchDataCallback.remove(appPackage);
            if (funSwitchDataCallback.isEmpty()) {
                unregisterFunSwitchInitReceiver();
            }
        }
    }

    private void registerFunSwitchInitReceiver() {
        backThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (funSwitchInitReceiver == null) {
                    funSwitchInitReceiver = new FunSwitchInitReceiver();
                }
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ActionConstants.ACTION_FUN_DATA_INIT_COMPLETED);
                context.registerReceiver(funSwitchInitReceiver, intentFilter);
            }
        });
    }

    private void unregisterFunSwitchInitReceiver() {
        backThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                context.unregisterReceiver(funSwitchInitReceiver);
            }
        });
    }

    /** @return true when the switch status is open (null or 0). */
    public static boolean isOpen(Integer switchStatus) {
        return switchStatus == null || switchStatus.intValue() == 0;
    }

    /** Pushes the current state of every registered switch to its callback. */
    public void initFunSwitch() {
        LogUtil.i(TAG, "initFunSwitch");
        if (funSwitchDataCallback.size() == 0) {
            return;
        }
        for (String appPackage : funSwitchDataCallback.keySet()) {
            FunSwitch funSwitch = WatchAccountBase.queryFunSwitchByPackageName(context, appPackage, false);
            funSwitch.setItem(makeNetFunItem(
                    WatchAccountBase.queryFunSwitchItemByPackageName(context, appPackage),
                    WatchAccountBase.queryFunSwitchExtraByPackageName(context, appPackage)));
            FunDataCallback callback = funSwitchDataCallback.get(appPackage);
            if (callback != null) {
                LogUtil.i(TAG, "initFunSwitch funSwitch = " + funSwitch.toString());
                callback.syncAllFunCompleted(funSwitch);
            }
        }
    }

    /** Merges the extra payloads into the corresponding items. */
    private List<NetFunItem> makeNetFunItem(List<NetFunItem> items, List<FunExtra> extras) {
        HashMap<Integer, NetFunItem> itemMap = new HashMap<>();
        for (NetFunItem item : items) {
            itemMap.put(item.getId(), item);
        }
        for (FunExtra extra : extras) {
            NetFunItem item = itemMap.get(extra.getItemId());
            if (item != null) {
                if (item.getExtra() == null) {
                    item.setExtra(new ArrayList<String>());
                }
                item.getExtra().add(extra.getExtra());
            }
        }
        return new ArrayList<>(itemMap.values());
    }
}
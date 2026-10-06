package com.xtc.contactapi.contact.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.contactapi.base.BaseResponse;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.constant.ContactApiConstant;
import com.xtc.contactapi.contacthead.bean.DressBean;
import com.xtc.contactapi.contacthead.impl.ContactDressCacheServeImpl;
import com.xtc.moment.module.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observer;
import rx.functions.Action1;

/**
 * 联系人 API 门面，负责初始化、联系人变更通知与联系人换装缓存管理。
 */
public class ContactApi {

    private static final String TAG = ContactApi.class.getSimpleName();
    private static final int MAX_PRELOAD_RETRY = 3;
    private static final long DEFAULT_CLEAN_INTERVAL_SECOND = 1600;

    private static Context appContext;
    private static volatile ContactApi instance;
    private static ContactApiStateListener stateListener;

    private static volatile ConcurrentHashMap<String, DressBean> dressCache;
    private static volatile ContactManager contactManager;

    private static volatile boolean contactsLoaded;
    private static volatile boolean defaultHeadLoaded;
    private static volatile boolean initDefaultHead;
    private static long lastCleanTime;
    private static long cleanIntervalSecond = DEFAULT_CLEAN_INTERVAL_SECOND;
    private static int preloadRetryCount;

    /** 初始化状态监听。 */
    public interface ContactApiStateListener {
        void onStateChanged(int state);
    }

    private ContactApi(Context context, ContactApiStateListener listener) {
        stateListener = listener;
        appContext = context.getApplicationContext();
        reloadContactManager(appContext);
    }

    /** 使用指定状态监听初始化。 */
    public static void init(Context context, ContactApiStateListener listener) {
        if (instance == null) {
            synchronized (ContactApi.class) {
                instance = new ContactApi(context, listener);
            }
        } else {
            Log.w(TAG, "ContactApi already init complete,do not init");
        }
    }

    /** 使用默认状态监听初始化。 */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (ContactApi.class) {
                instance = new ContactApi(context, new ContactApiStateListener() {
                    @Override
                    public void onStateChanged(int state) {
                        switch (state) {
                            case ContactApiConstant.ContctApiResponseCode.INIT_COMPLETE:
                                Log.i(TAG, "--------- ContactApi 初始化完成--------------");
                                break;
                            case ContactApiConstant.ContctApiResponseCode.CONTEXT_NULL:
                                Log.e(TAG, "--------- ContactApi 上下文为null--------------");
                                break;
                            case ContactApiConstant.ContctApiResponseCode.DISCONNECTED:
                                Log.e(TAG, "--------- ContactApi 已断连--------------");
                                break;
                            case ContactApiConstant.ContctApiResponseCode.PRELOAD_COMPLETE:
                                Log.i(TAG, "--------- ContactApi预加载联系人完成--------------");
                                break;
                            case ContactApiConstant.ContctApiResponseCode.PRELOAD_FAIL:
                                Log.e(TAG, "--------- ContactApi预加载联系人失败--------------");
                                if (nextPreloadRetryCount() < MAX_PRELOAD_RETRY) {
                                    Log.i(TAG, "重新尝试预加载联系人");
                                    reloadContactManager(appContext);
                                } else {
                                    Log.i(TAG, "尝试预加载联系人次数超限，停止尝试");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        } else {
            Log.w(TAG, "ContactApi already init complete,do not init");
        }
    }

    /** 重建联系人管理器并触发一次加载。 */
    public static void reloadContactManager(Context context) {
        contactManager = ContactManager.getInstance(context);
        contactManager.reloadContact();
    }

    private static int nextPreloadRetryCount() {
        return ++preloadRetryCount;
    }

    /** 全量更新换装缓存。 */
    public static void updateDressCache(Map<String, DressBean> cache) {
        if (dressCache == null) {
            dressCache = new ConcurrentHashMap<>();
        } else {
            dressCache.clear();
        }
        dressCache.putAll(cache);
        lastCleanTime = System.currentTimeMillis();
        defaultHeadLoaded = true;
    }

    /** 全量更新换装缓存并指定清理间隔。 */
    public static void updateDressCache(Map<String, DressBean> cache, long cleanIntervalSeconds) {
        updateDressCache(cache);
        cleanIntervalSecond = cleanIntervalSeconds;
    }

    /** 增量更新换装缓存，必要时清理无用缓存。 */
    public static void updateDressCache(Context context, Map<String, DressBean> cache) {
        if (context == null || cache == null) {
            return;
        }
        putDressCache(cache);
        long now = System.currentTimeMillis();
        if (now - lastCleanTime >= cleanIntervalSecond * 1000) {
            Log.i(TAG, " 更新间隔大于清理无用缓存时间执行清理操作");
            cleanUnusedDressCache(context);
        } else {
            lastCleanTime = now;
        }
    }

    private static void cleanUnusedDressCache(Context context) {
        ContactDressCacheServeImpl dressCacheServe = ContactDressCacheServeImpl.getInstance(context.getApplicationContext());
        Set<String> cachedKeys = dressCacheServe.getAllKeys();
        List<String> usedPaths = new ArrayList<>();
        if (dressCache == null) {
            return;
        }
        Set<String> keySet = dressCache.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);
        for (int i = 0; i < keySet.size(); i++) {
            DressBean dressBean = dressCache.get(keys[i]);
            if (dressBean != null && !TextUtils.isEmpty(dressBean.getDressId())
                    && !TextUtils.isEmpty(dressBean.getDressPath())) {
                usedPaths.add(dressBean.getDressPath());
            }
        }
        Iterator<String> iterator = cachedKeys.iterator();
        while (iterator.hasNext()) {
            String[] parts = iterator.next().split(ContactDressCacheServeImpl.KEY_SEPARATOR);
            if (parts != null && parts.length >= 3) {
                String key = parts[0];
                if (!usedPaths.contains(key)) {
                    Log.i(TAG, " 清理无用缓存 key" + key + ".........");
                    dressCacheServe.removeCache(key);
                }
            }
        }
    }

    private static void putDressCache(Map<String, DressBean> cache) {
        if (cache == null) {
            return;
        }
        dressCache.clear();
        dressCache.putAll(cache);
    }

    /** 通知联系人预加载结果。 */
    protected static void notifyContactsLoaded(boolean success) {
        contactsLoaded = success;
        ContactApiStateListener listener = stateListener;
        if (listener != null) {
            listener.onStateChanged(success
                    ? ContactApiConstant.ContctApiResponseCode.PRELOAD_COMPLETE
                    : ContactApiConstant.ContctApiResponseCode.PRELOAD_FAIL);
        }
    }

    /** 联系人是否已预加载完成。 */
    public static boolean isContactsLoaded() {
        return contactsLoaded;
    }

    protected static void setInitDefaultHead(boolean value) {
        initDefaultHead = value;
    }

    /** 默认头像是否已初始化。 */
    public static boolean isInitDefaultHead() {
        return initDefaultHead;
    }

    /** 换装缓存是否已加载。 */
    public static boolean isDefaultHeadLoaded() {
        return defaultHeadLoaded;
    }

    public static void setStateListener(ContactApiStateListener listener) {
        stateListener = listener;
    }

    public static ConcurrentHashMap<String, DressBean> getDressCache() {
        return dressCache;
    }

    /** 通知指定联系人发生变化。 */
    public static void notifyContactChange(String contactServerId) {
        if (appContext == null || TextUtils.isEmpty(contactServerId)) {
            Log.e(TAG, "notifyContactChange: but context = " + appContext + " ,contactServerId = " + contactServerId);
            return;
        }
        appContext.getContentResolver().notifyChange(
                Uri.parse(ContactApiConstant.CONTACT_SERVER_URI + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + contactServerId), null);
    }

    /** 发送联系人变化广播。 */
    public static void sendContactChangeBroadcast(int type, ContactBean contactBean) {
        if (contactBean == null || type < 0 || type > 2) {
            Log.e(TAG, "sendContactChangBroadCast: contactBean = " + contactBean + " ,type = " + type);
            return;
        }
        if (appContext == null) {
            Log.e(TAG, "sendContactChangBroadCast: but context null");
            return;
        }
        Intent intent = new Intent();
        if (type == ContactApiConstant.ContactReceiver.TYPE_ADD) {
            intent.setAction(ContactApiConstant.ContactReceiver.ACTION_ADD);
            intent.putExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN, contactBean);
            appContext.sendBroadcast(intent);
        } else if (type == ContactApiConstant.ContactReceiver.TYPE_UPDATE) {
            intent.setAction(ContactApiConstant.ContactReceiver.ACTION_UPDATE);
            intent.putExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN, contactBean);
            appContext.sendBroadcast(intent);
        } else if (type == ContactApiConstant.ContactReceiver.TYPE_REMOVE) {
            intent.setAction(ContactApiConstant.ContactReceiver.ACTION_REMOVE);
            intent.putExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN, contactBean);
            appContext.sendBroadcast(intent);
        }
    }

    /** 按服务端 id 查询并广播联系人变化。 */
    public static void sendContactChangeBroadcast(final int type, String contactServerId) {
        if (TextUtils.isEmpty(contactServerId) || type < 0 || type > 2) {
            Log.e(TAG, "sendContactChangBroadCast: contactServerId = " + contactServerId + " ,type = " + type);
            return;
        }
        if (appContext == null) {
            Log.e(TAG, "sendContactChangBroadCast: but context null");
            return;
        }
        contactManager.getContactByServerId(contactServerId).subscribe(new Observer<BaseResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "sendContactChangBroadCast error: " + throwable.getMessage());
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.d(TAG, "sendContactChangBroadCast baseResponse: " + baseResponse);
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    sendContactChangeBroadcast(type, (ContactBean) baseResponse.getResponse());
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.ERROR) {
                    Log.e(TAG, "sendContactChangBroadCast error: " + baseResponse);
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA) {
                    Log.d(TAG, "sendContactChangBroadCast not data: " + baseResponse);
                }
            }
        });
    }

    /** 通知联系人数据整体变化。 */
    public static void notifyContactsChange() {
        Context context = appContext;
        if (context == null) {
            Log.e(TAG, "sendContactChangBroadCast: but context null");
        } else {
            context.getContentResolver().notifyChange(ContactApiConstant.CONTACT_URI, null);
        }
    }

    /** 订阅全部联系人变化。 */
    public static void subscribeAllContacts(Action1<BaseResponse> action) {
        if (contactManager == null) {
            contactManager = ContactManager.getInstance(appContext);
        }
        contactManager.getAllContacts().subscribe(action);
    }
}
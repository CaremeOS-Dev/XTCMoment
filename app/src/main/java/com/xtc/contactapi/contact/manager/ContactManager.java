package com.xtc.contactapi.contact.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.xtc.contactapi.R;
import com.xtc.contactapi.base.BaseResponse;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.constant.ContactApiConstant;
import com.xtc.contactapi.contact.impl.ContactChangeCallback;
import com.xtc.contactapi.contact.interfaces.ContactChangeListener;
import com.xtc.contactapi.contact.interfaces.IContactServe;
import com.xtc.contactapi.contact.observable.ContactInfoObservable;
import com.xtc.contactapi.contact.observable.HeadInfoObservable;
import com.xtc.contactapi.contact.observable.interfaces.IContactSubscribe;
import com.xtc.contactapi.contact.observable.interfaces.IDefaultSubscribe;
import com.xtc.contactapi.contact.utils.GsonUtil;
import com.xtc.contactapi.contacthead.impl.ContactHeadCacheServeImpl;
import com.xtc.moment.module.Constants;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.storage.FileUtils;
import com.xtc.web.client.manager.BaseInfoManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 联系人服务实现，通过内容提供者读写联系人并维护内存缓存与变化监听。
 */
public class ContactManager implements IContactServe {

    private static final String TAG = ContactManager.class.getSimpleName();

    private static final String XTCSERVICE_PACKAGE_NAME = "com.xtc.xws";
    private static final String XTCSERVICE_VERSION_META = "com.xtc.xws.contactapi.version";
    private static final String FIELD_CALL_AUTH = "callAuth";
    private static final String FIELD_PARENT_REVIEW = "parentReview";
    private static final String METHOD_REPLACE_ALL = "replaceAll";
    private static final String METHOD_REPLACE_ALL_JSON = "replaceAll_Json";
    private static final String KEY_CONTENT_VALUES = "ContentValues";
    private static final String KEY_CONTACTS_JSON = "contactsJson";

    private static final int URI_ITEM = 1;
    private static final int URI_ITEM_SERVER_ID = 6;
    private static final int QUERY_BATCH_SIZE = 20;

    private static volatile ContactManager instance;
    private static volatile ContactChangeCallback changeCallback;
    private static volatile List<ContactBean> allContacts = new CopyOnWriteArrayList<>();
    private static volatile Map<Integer, String> defaultHeadMap = new HashMap<>();
    private static volatile boolean isLoadingDefaultHead = false;

    private final Context context;
    private final ContentResolver contentResolver;
    private final UriMatcher uriMatcher;
    private final ContactObserver contactObserver;
    private final ContactHeadCacheServeImpl headCache;

    /** 服务端是否支持 callAuth 字段。 */
    private boolean hasCallAuthField = false;
    /** 服务端是否支持 parentReview 字段。 */
    private boolean hasParentReviewField = false;
    /** 是否已注册内容观察者。 */
    private volatile boolean observerRegistered = false;
    /** 服务端是否为新版本（支持 JSON 全量替换）。 */
    private boolean isNewServiceVersion = false;

    private ContactManager(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        this.context = context;
        initChangeCallback();
        this.headCache = ContactHeadCacheServeImpl.getInstance(context);
        this.uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        this.uriMatcher.addURI(ContactApiConstant.AUTHORITY, ContactApiConstant.ITEM, URI_ITEM);
        this.uriMatcher.addURI(ContactApiConstant.AUTHORITY, ContactApiConstant.ITEM_SERVER_ID, URI_ITEM_SERVER_ID);
        this.contentResolver = context.getApplicationContext().getContentResolver();
        this.contactObserver = new ContactObserver(new Handler(Looper.getMainLooper()));
        this.isNewServiceVersion = checkServiceVersion(context);
        loadAllDefaultHead();
        loadAllContacts();
    }

    private void initChangeCallback() {
        if (changeCallback == null) {
            changeCallback = new ContactChangeCallback();
        }
    }

    public static ContactManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ContactManager.class) {
                if (instance == null) {
                    instance = new ContactManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 联系人内容观察者，负责在联系人数据变化时刷新缓存。
     */
    private class ContactObserver extends ContentObserver {

        ContactObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, "onChange: " + uri);
            if (selfChange) {
                return;
            }
            int match = uriMatcher.match(uri);
            if (match == URI_ITEM) {
                reloadAllContacts();
                reloadDefaultHead();
            } else if (match == URI_ITEM_SERVER_ID) {
                refreshOneContact(uri.getPathSegments().get(2));
            }
        }
    }
    private void loadAllDefaultHead() {
        if (isLoadingDefaultHead) {
            return;
        }
        isLoadingDefaultHead = true;
        loadDefaultHeadObservable().subscribe(new Observer<BaseResponse>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "loadAllDefaultHead completed");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "loadAllContacts error: " + throwable.getMessage());
                ContactApi.setInitDefaultHead(false);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    Map<Integer, String> headMap = (Map<Integer, String>) baseResponse.getResponse();
                    synchronized (defaultHeadMap) {
                        if (defaultHeadMap.size() > 0) {
                            defaultHeadMap.clear();
                        }
                        defaultHeadMap.putAll(headMap);
                    }
                    ContactApi.setInitDefaultHead(true);
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA) {
                    if (defaultHeadMap.size() > 0) {
                        defaultHeadMap.clear();
                    }
                    ContactApi.setInitDefaultHead(true);
                }
                isLoadingDefaultHead = false;
            }
        });
    }

    private void loadAllContacts() {
        getAllContacts().subscribe(new Observer<BaseResponse>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "loadAllContacts completed");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "loadAllContacts error: " + throwable.getMessage());
                ContactApi.notifyContactsLoaded(false);
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    List<ContactBean> contactList = (List<ContactBean>) baseResponse.getResponse();
                    synchronized (allContacts) {
                        if (allContacts.size() > 0) {
                            allContacts.clear();
                        }
                        allContacts.addAll(contactList);
                    }
                    ContactApi.notifyContactsLoaded(true);
                    changeCallback.notifyContactsRefresh(allContacts);
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA) {
                    if (allContacts.size() > 0) {
                        allContacts.clear();
                    }
                    ContactApi.notifyContactsLoaded(true);
                    changeCallback.notifyContactsRefresh(allContacts);
                }
            }
        });
    }

    /** 重新加载全部联系人（不通知状态监听）。 */
    private void reloadAllContacts() {
        getAllContacts().subscribe(new Observer<BaseResponse>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "loadAllContacts completed");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "loadAllContacts error: " + throwable.getMessage());
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    List<ContactBean> contactList = (List<ContactBean>) baseResponse.getResponse();
                    synchronized (allContacts) {
                        if (allContacts.size() > 0) {
                            allContacts.clear();
                        }
                        allContacts.addAll(contactList);
                    }
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA
                        && allContacts.size() > 0) {
                    allContacts.clear();
                }
                changeCallback.notifyContactsRefresh(allContacts);
            }
        });
    }

    /** 重新加载全部默认头像。 */
    public void reloadDefaultHead() {
        if (isLoadingDefaultHead) {
            Log.i(TAG, "reloadContact is initing");
        } else {
            loadAllDefaultHead();
        }
    }

    /** 重新加载全部联系人。 */
    public void reloadContact() {
        if (ContactApi.isContactsLoaded()) {
            Log.i(TAG, "reloadContact has init");
        } else {
            loadAllContacts();
        }
    }

    /** 刷新单个联系人并派发变化事件。 */
    private void refreshOneContact(final String contactServerId) {
        if (TextUtils.isEmpty(contactServerId)) {
            return;
        }
        getContactByServerId(contactServerId).subscribe(new Observer<BaseResponse>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "getOneContacts error: " + throwable.getMessage());
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                Log.i(TAG, "getOneContacts baseResponse: " + baseResponse);
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    final ContactBean contactBean = (ContactBean) baseResponse.getResponse();
                    ContactBean oldContactBean = replaceContactInCache(contactBean);
                    if (oldContactBean == null) {
                        changeCallback.notifyContactAdd(contactBean);
                        return;
                    }
                    headCache.removeHeadCacheOnChange(oldContactBean, contactBean)
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean result) {
                                    changeCallback.notifyContactUpdate(contactBean);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Log.e(TAG, "removeCacheHead#error: ", throwable);
                                }
                            });
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.ERROR) {
                    Log.i(TAG, "getOneContacts error: " + baseResponse);
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA) {
                    for (int i = 0; i < allContacts.size(); i++) {
                        ContactBean contactBean = allContacts.get(i);
                        if (!TextUtils.isEmpty(contactBean.getContactServerId()) && !TextUtils.isEmpty(contactServerId)
                                && contactBean.getContactServerId().equals(contactServerId)) {
                            removeContactFromCache(contactBean);
                            changeCallback.notifyContactRemove(contactBean);
                        }
                    }
                }
            }
        });
    }

    private boolean isHeadChanged(ContactBean oldContactBean, ContactBean newContactBean) {
        String oldServerId = newContactBean.getContactServerId();
        String newServerId = oldContactBean.getContactServerId();
        String oldPhotoPath = oldContactBean.getPhotoPath();
        String newPhotoPath = newContactBean.getPhotoPath();
        Log.d(TAG, "checkIsHeadChange old:" + oldContactBean + "  ,new:" + newContactBean);
        return oldServerId == null || newServerId == null || !oldServerId.equals(newServerId)
                || oldPhotoPath == null || newPhotoPath == null || !oldPhotoPath.equals(newPhotoPath);
    }

    /** 覆盖内存中的全部联系人。 */
    public void replaceContacts(List<ContactBean> contactList) {
        if (ContactApi.isContactsLoaded()) {
            synchronized (allContacts) {
                allContacts.clear();
                allContacts.addAll(contactList);
            }
        }
    }

    /** 替换或新增单个联系人，返回被替换的旧联系人（不存在则返回 null）。 */
    public ContactBean replaceContactInCache(ContactBean contactBean) {
        if (contactBean == null || !ContactApi.isContactsLoaded()) {
            return null;
        }
        synchronized (allContacts) {
            for (int i = 0; i < allContacts.size(); i++) {
                ContactBean cached = allContacts.get(i);
                if (contactBean.getContactServerId() != null
                        && contactBean.getContactServerId().equals(cached.getContactServerId())) {
                    allContacts.remove(i);
                    allContacts.add(i, contactBean);
                    return cached;
                }
                if (contactBean.getFriendWatchId() != null && contactBean.getNumberId() != null
                        && contactBean.getNumberId().equals(cached.getNumberId())) {
                    allContacts.remove(i);
                    allContacts.add(i, contactBean);
                    return cached;
                }
                if (contactBean.getFriendWatchId() != null && contactBean.getNumberId() == null
                        && contactBean.getFriendWatchId().equals(cached.getFriendWatchId())) {
                    allContacts.remove(i);
                    allContacts.add(i, contactBean);
                    return cached;
                }
            }
            allContacts.add(contactBean);
            return null;
        }
    }

    /** 从内存缓存中移除指定联系人。 */
    public void removeContactFromCache(ContactBean contactBean) {
        String contactServerId = contactBean.getContactServerId();
        if (!ContactApi.isContactsLoaded() || TextUtils.isEmpty(contactServerId)) {
            return;
        }
        synchronized (allContacts) {
            for (ContactBean cached : allContacts) {
                if (!TextUtils.isEmpty(cached.getContactServerId()) && !TextUtils.isEmpty(contactServerId)
                        && cached.getContactServerId().equals(contactServerId)) {
                    allContacts.remove(cached);
                    return;
                }
            }
        }
    }
    @Override
    public Observable<BaseResponse> getContactByServerId(final String serverId) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(
                            Uri.parse(ContactApiConstant.CONTACT_SERVER_URI + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + serverId),
                            null, null, null, null);
                    subscriber.onNext(parseOneContact(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 加载全部默认头像。 */
    public Observable<BaseResponse> loadDefaultHeadObservable() {
        return new HeadInfoObservable.HeadInfoObservableBuilder().subscribe(new IDefaultSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                File headDir = new File(ContactApiConstant.CONTACT_HEAD_DIR);
                Log.d(TAG, headDir + "");
                File[] headFiles = headDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(ContactApiConstant.HEAD_FILE_SUFFIX)
                                && name.startsWith(ContactHeadManager.DEFAULT_PORTRAIT_PREFIX);
                    }
                });
                if (FileUtils.exists(headDir)) {
                    subscriber.onNext(parseDefaultHeadMap(headFiles));
                    subscriber.onCompleted();
                }
            }
        }).build();
    }

    /** 解析默认头像目录，映射角色到文件名。 */
    private BaseResponse<Map<Integer, String>> parseDefaultHeadMap(File[] headFiles) {
        HashMap<Integer, String> headMap = new HashMap<>();
        BaseResponse<Map<Integer, String>> response = new BaseResponse<>();
        if (headFiles == null) {
            Log.w(TAG, "content resolver cursor is null !");
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.ERROR);
            response.setErrorDesc(ContactApiConstant.ContctApiErrorDev.QUERY_FAIL);
            return response;
        }
        for (File file : headFiles) {
            if (FileUtils.exists(file)) {
                String name = file.getName();
                Log.d(TAG, "exited head name  " + name);
                if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_FATHER)) {
                    headMap.put(1, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_MOTHER)) {
                    headMap.put(2, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_SISTER)) {
                    headMap.put(3, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_BROTHER)) {
                    headMap.put(4, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_GRANDFATHER)) {
                    headMap.put(5, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_GRANDMOTHER)) {
                    headMap.put(6, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_GRANDPA)) {
                    headMap.put(7, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_GRANDMA)) {
                    headMap.put(8, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_TEACHER)) {
                    headMap.put(9, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_FRIEND)) {
                    headMap.put(10, name);
                } else if (name.contains(ContactApiConstant.IRoleType.PORTRAIT_STRANGER)) {
                    headMap.put(0, name);
                }
            }
        }
        if (headMap.size() == 0) {
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.NO_DATA);
            return response;
        }
        response.setResponseCode(ContactApiConstant.ContctApiResponseCode.SUCCESS);
        response.setResponse(headMap);
        return response;
    }

    @Override
    public Observable<BaseResponse> getAllContacts() {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                if (!observerRegistered) {
                    registerContactObserver();
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null, null, null, null);
                    subscriber.onNext(parseContactList(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 内存中的全部联系人。 */
    public List<ContactBean> getAllContactsSync() {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, " contactManager is not load AllContacts");
            return null;
        }
        return allContacts;
    }
    @Override
    public Observable<BaseResponse> getContactByWatchIdOrMobileId(final String id) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                BaseResponse errorResponse = checkEmptyId(id);
                if (errorResponse != null) {
                    subscriber.onNext(errorResponse);
                    subscriber.onCompleted();
                    return;
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null,
                            "friend_watch_id=? or mobile_id=?", new String[]{id, id}, null);
                    subscriber.onNext(parseOneContact(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    @Override
    public Observable<BaseResponse> getContactWithoutShortNumberByWatchId(final String watchId) {
        return querySingleContactBySelection("friend_watch_id=? and number_id is null", new String[]{watchId}, watchId);
    }

    @Override
    public Observable<BaseResponse> getContactWithoutShortNumberByMobileId(final String mobileId) {
        return querySingleContactBySelection("mobile_id=? and number_id is null", new String[]{mobileId}, mobileId);
    }

    @Override
    public Observable<BaseResponse> getContactWithShortNumberByWatchId(final String watchId) {
        return querySingleContactBySelection("friend_watch_id=? and number_id is not null", new String[]{watchId}, watchId);
    }

    @Override
    public Observable<BaseResponse> getContactWithShortNumberByMobileId(final String mobileId) {
        return querySingleContactBySelection("mobile_id=? and number_id is not null", new String[]{mobileId}, mobileId);
    }

    private Observable<BaseResponse> querySingleContactBySelection(final String selection, final String[] selectionArgs,
                                                                   final String id) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                BaseResponse errorResponse = checkEmptyId(id);
                if (errorResponse != null) {
                    subscriber.onNext(errorResponse);
                    subscriber.onCompleted();
                    return;
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null, selection, selectionArgs, null);
                    subscriber.onNext(parseOneContact(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 按 friendWatchId 或 mobileId 同步查询联系人。 */
    public ContactBean getContactByWatchIdSync(String id) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, " contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String friendWatchId = contactBean.getFriendWatchId();
            String mobileId = contactBean.getMobileId();
            if ((!TextUtils.isEmpty(friendWatchId) && friendWatchId.equals(id))
                    || (!TextUtils.isEmpty(mobileId) && mobileId.equals(id))) {
                return contactBean;
            }
        }
        return null;
    }

    /** 按 friendWatchId 同步查询无短号联系人。 */
    public ContactBean getContactWithoutShortNumberByWatchIdSync(String watchId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, "getContactWithoutShortNumberByWatchIdSync, contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(watchId)) {
            Log.w(TAG, "getContactWithoutShortNumberByWatchIdSync, watchId is empty");
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String friendWatchId = contactBean.getFriendWatchId();
            if (!TextUtils.isEmpty(friendWatchId) && friendWatchId.equals(watchId) && contactBean.getNumberId() == null) {
                return contactBean;
            }
        }
        return null;
    }

    /** 按 mobileId 同步查询无短号联系人。 */
    public ContactBean getContactWithoutShortNumberByMobileIdSync(String mobileId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, "getContactWithoutShortNumberByMobileIdSync, contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(mobileId)) {
            Log.w(TAG, "getContactWithoutShortNumberByMobileIdSync, mobileId is empty");
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String cachedMobileId = contactBean.getMobileId();
            if (!TextUtils.isEmpty(cachedMobileId) && cachedMobileId.equals(mobileId) && contactBean.getNumberId() == null) {
                return contactBean;
            }
        }
        return null;
    }

    /** 按 friendWatchId 同步查询有短号联系人。 */
    public ContactBean getContactWithShortNumberByWatchIdSync(String watchId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, "getContactWithShortNumberByWatchIdSync, contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(watchId)) {
            Log.w(TAG, "getContactWithShortNumberByWatchIdSync, watchId is empty");
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String friendWatchId = contactBean.getFriendWatchId();
            if (!TextUtils.isEmpty(friendWatchId) && friendWatchId.equals(watchId) && contactBean.getNumberId() != null) {
                return contactBean;
            }
        }
        return null;
    }

    /** 按 mobileId 同步查询有短号联系人。 */
    public ContactBean getContactWithShortNumberByMobileIdSync(String mobileId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, "getContactWithShortNumberByMobileIdSync, contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(mobileId)) {
            Log.w(TAG, "getContactWithShortNumberByMobileIdSync, mobileId is empty");
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String cachedMobileId = contactBean.getMobileId();
            if (!TextUtils.isEmpty(cachedMobileId) && cachedMobileId.equals(mobileId) && contactBean.getNumberId() != null) {
                return contactBean;
            }
        }
        return null;
    }
    @Override
    public Observable<BaseResponse> getContactsByName(final String name) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                BaseResponse errorResponse = checkEmptyId(name);
                if (errorResponse != null) {
                    subscriber.onNext(errorResponse);
                    subscriber.onCompleted();
                    return;
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null, "name=?", new String[]{name}, null);
                    subscriber.onNext(parseContactList(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 按姓名同步查询联系人。 */
    public ContactBean getContactByNameSync(String name) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, " contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String cachedName = contactBean.getName();
            if (!TextUtils.isEmpty(cachedName) && cachedName.equals(name)) {
                return contactBean;
            }
        }
        return null;
    }

    @Override
    public Observable<BaseResponse> getContactByOpenId(final String openId) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                BaseResponse errorResponse = checkEmptyId(openId);
                if (errorResponse != null) {
                    subscriber.onNext(errorResponse);
                    subscriber.onCompleted();
                    return;
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null, "openID=?", new String[]{openId}, null);
                    subscriber.onNext(parseOneContact(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 按 openId 同步查询联系人。 */
    public ContactBean getContactByOpenIdSync(String openId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, " contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(openId)) {
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String cachedOpenId = contactBean.getOpenId();
            if (!TextUtils.isEmpty(cachedOpenId) && cachedOpenId.equals(openId)) {
                return contactBean;
            }
        }
        return null;
    }

    @Override
    public Observable<BaseResponse> getContactByContactServerId(final String contactServerId) {
        return new ContactInfoObservable.ContactInfoObservableBuilder().subscribe(new IContactSubscribe() {
            @Override
            public void subscribe(Subscriber<? super BaseResponse> subscriber) {
                BaseResponse errorResponse = checkEmptyId(contactServerId);
                if (errorResponse != null) {
                    subscriber.onNext(errorResponse);
                    subscriber.onCompleted();
                    return;
                }
                Cursor cursor = null;
                try {
                    cursor = contentResolver.query(ContactApiConstant.CONTACT_URI, null, "contact_server_id=?",
                            new String[]{contactServerId}, null);
                    subscriber.onNext(parseOneContact(cursor));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }).build();
    }

    /** 按 contactServerId 同步查询联系人。 */
    public ContactBean getContactByContactServerIdSync(String contactServerId) {
        if (!ContactApi.isContactsLoaded()) {
            Log.w(TAG, " contactManager is not load AllContacts");
            return null;
        }
        if (TextUtils.isEmpty(contactServerId)) {
            return null;
        }
        for (ContactBean contactBean : allContacts) {
            String cachedServerId = contactBean.getContactServerId();
            if (!TextUtils.isEmpty(cachedServerId) && cachedServerId.equals(contactServerId)) {
                return contactBean;
            }
        }
        return null;
    }

    @Override
    public Observable<BaseResponse> isFriendByOpenId(String openId) {
        return getContactByOpenId(openId).map(new Func1<BaseResponse, BaseResponse>() {
            @Override
            public BaseResponse call(BaseResponse baseResponse) {
                if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.SUCCESS
                        && baseResponse.getResponse() != null) {
                    baseResponse.setResponse(true);
                } else if (baseResponse.getResponseCode() == ContactApiConstant.ContctApiResponseCode.NO_DATA) {
                    baseResponse.setResponse(false);
                }
                return baseResponse;
            }
        });
    }

    private BaseResponse checkEmptyId(String id) {
        if (!TextUtils.isEmpty(id)) {
            return null;
        }
        BaseResponse response = new BaseResponse();
        response.setResponseCode(ContactApiConstant.ContctApiResponseCode.ERROR);
        response.setErrorDesc(ContactApiConstant.ContctApiErrorDev.PARAM_EMPTY);
        return response;
    }

    @Override
    public void registerContactChangeListener(ContactChangeListener listener) {
        if (listener == null) {
            return;
        }
        changeCallback.registerListener(listener, ContactApiConstant.ConvertCode.SYNC_THREAD);
    }

    @Override
    public void registerContactChangeListener(ContactChangeListener listener, int convertCode) {
        if (listener == null) {
            return;
        }
        if (!observerRegistered) {
            registerContactObserver();
        }
        changeCallback.registerListener(listener, convertCode);
    }

    @Override
    public void unregisterContactChangeListener(ContactChangeListener listener) {
        if (listener == null) {
            return;
        }
        changeCallback.unregisterListener(listener);
    }
    /** 解析联系人列表查询结果。 */
    private BaseResponse<List<ContactBean>> parseContactList(Cursor cursor) {
        List<ContactBean> contactList = new ArrayList<>();
        BaseResponse<List<ContactBean>> response = new BaseResponse<>();
        List<HashMap<String, String>> rowMaps = new ArrayList<>();
        if (cursor != null) {
            loop:
            while (true) {
                int batchCount = 0;
                do {
                    if (!cursor.moveToNext()) {
                        break loop;
                    }
                    String[] columnNames = cursor.getColumnNames();
                    HashMap<String, String> rowMap = new HashMap<>();
                    for (String columnName : columnNames) {
                        rowMap.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                    }
                    rowMaps.add(rowMap);
                    batchCount++;
                } while (batchCount < QUERY_BATCH_SIZE);
                appendParsedContacts(rowMaps, contactList);
                rowMaps.clear();
            }
            appendParsedContacts(rowMaps, contactList);
            hasCallAuthField = rowMaps.contains(FIELD_CALL_AUTH);
            hasParentReviewField = rowMaps.contains(FIELD_PARENT_REVIEW);
            Log.i(TAG, "contactBeanList size = " + contactList.size());
            if (contactList.size() == 0) {
                response.setResponseCode(ContactApiConstant.ContctApiResponseCode.NO_DATA);
                cursor.close();
                return response;
            }
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.SUCCESS);
            response.setResponse(contactList);
            Log.i(TAG, "" + contactList.size());
            cursor.close();
        } else {
            Log.w(TAG, "content resolver cursor is null !");
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.ERROR);
            response.setErrorDesc(ContactApiConstant.ContctApiErrorDev.QUERY_FAIL);
        }
        return response;
    }

    private void appendParsedContacts(List<HashMap<String, String>> rowMaps, List<ContactBean> contactList) {
        if (rowMaps == null || rowMaps.size() <= 0) {
            Log.i(TAG, "parseContactBeans, mapList is empty");
            return;
        }
        List<ContactBean> parsed = GsonUtil.fromJson(GsonUtil.toJson(rowMaps), new TypeToken<List<ContactBean>>() {
        }.getType());
        if (parsed == null || parsed.size() <= 0) {
            Log.e(TAG, "parseContactBean, parseDatas is empty");
        } else {
            contactList.addAll(parsed);
        }
    }

    /** 解析单个联系人查询结果。 */
    private BaseResponse<ContactBean> parseOneContact(Cursor cursor) {
        BaseResponse<ContactBean> response = new BaseResponse<>();
        HashMap<String, String> rowMap = new HashMap<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                for (String columnName : cursor.getColumnNames()) {
                    rowMap.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                }
            }
            String json = GsonUtil.toJson(rowMap);
            if (isAllValuesNull(rowMap)) {
                response.setResponseCode(ContactApiConstant.ContctApiResponseCode.NO_DATA);
                cursor.close();
                return response;
            }
            ContactBean contactBean = GsonUtil.fromJson(json, ContactBean.class);
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.SUCCESS);
            response.setResponse(contactBean);
            Log.i(TAG, "contactBean:" + contactBean);
            cursor.close();
        } else {
            Log.w(TAG, "content resolver cursor is null !");
            response.setResponseCode(ContactApiConstant.ContctApiResponseCode.ERROR);
            response.setErrorDesc(ContactApiConstant.ContctApiErrorDev.QUERY_FAIL);
        }
        return response;
    }

    private boolean isAllValuesNull(HashMap<String, String> rowMap) {
        if (rowMap == null) {
            return true;
        }
        for (String key : rowMap.keySet()) {
            if (rowMap.get(key) != null) {
                return false;
            }
        }
        return true;
    }

    /** 注册联系人内容观察者（需要 READ_CONTACTS 权限）。 */
    public synchronized void registerContactObserver() {
        try {
            if (observerRegistered || contactObserver == null || contentResolver == null) {
                Log.d(TAG, "registerContactObserver fail: registerObserver=" + observerRegistered
                        + " ,contactObserver=" + contactObserver + " ,mResolver=" + contentResolver);
                return;
            }
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Denied: READ_CONTACTS permission required");
                return;
            }
            try {
                contentResolver.registerContentObserver(ContactApiConstant.CONTACT_SERVER_URI, true, contactObserver);
                observerRegistered = true;
                Log.d(TAG, "registerContactObserver successful");
            } catch (Throwable throwable) {
                Log.d(TAG, "registerContactObserver failure", throwable);
            }
        } catch (Exception e) {
            Log.e(TAG, "Permission Denied: READ_CONTACTS permission required", e);
        }
    }

    private void unregisterContactObserver() {
        if (!observerRegistered || contactObserver == null || contentResolver == null) {
            Log.d(TAG, "unregisterContactObserver fail: registerObserver=" + observerRegistered
                    + " ,contactObserver=" + contactObserver + " ,mResolver=" + contentResolver);
            return;
        }
        try {
            observerRegistered = false;
            contentResolver.unregisterContentObserver(contactObserver);
            Log.d(TAG, "unregisterContentObserver successful");
        } catch (Throwable throwable) {
            Log.d(TAG, "registerContactObserver failure", throwable);
        }
    }
    @Override
    public boolean updateVideoChatMissedCallCount(ContactBean contactBean) {
        if (contactBean == null) {
            return false;
        }
        try {
            ContentValues values = new ContentValues();
            values.put("videoChatMissedCallCount", contactBean.getVideoChatMissedCallCount());
            return contentResolver.update(ContactApiConstant.CONTACT_SERVER_URI, values,
                    "contactServerId = ?", new String[]{contactBean.getContactServerId()}) > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public List<ContactBean> getContactsByType() {
        Cursor cursor = contentResolver.query(ContactApiConstant.CONTACT_SERVER_URI, null,
                "contactType <= 1", null, "contactType ASC");
        List<ContactBean> emptyList = new ArrayList<>();
        List<HashMap<String, String>> rowMaps = new ArrayList<>();
        if (cursor == null || !cursor.moveToFirst()) {
            return emptyList;
        }
        do {
            String[] columnNames = cursor.getColumnNames();
            HashMap<String, String> rowMap = new HashMap<>();
            for (String columnName : columnNames) {
                rowMap.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }
            rowMaps.add(rowMap);
        } while (cursor.moveToNext());
        List<ContactBean> contactList = GsonUtil.fromJson(GsonUtil.toJson(rowMaps),
                new TypeToken<List<ContactBean>>() {
                }.getType());
        cursor.close();
        return contactList;
    }

    @Override
    public List<ContactBean> getContactsByRole(String role) {
        Cursor cursor = contentResolver.query(ContactApiConstant.CONTACT_SERVER_URI, null, "role=?",
                new String[]{role}, null);
        List<ContactBean> emptyList = new ArrayList<>();
        List<HashMap<String, String>> rowMaps = new ArrayList<>();
        if (cursor == null || !cursor.moveToFirst()) {
            return emptyList;
        }
        do {
            String[] columnNames = cursor.getColumnNames();
            HashMap<String, String> rowMap = new HashMap<>();
            for (String columnName : columnNames) {
                rowMap.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
            }
            rowMaps.add(rowMap);
        } while (cursor.moveToNext());
        List<ContactBean> contactList = GsonUtil.fromJson(GsonUtil.toJson(rowMaps),
                new TypeToken<List<ContactBean>>() {
                }.getType());
        cursor.close();
        return contactList;
    }
    @Override
    @Deprecated
    public boolean replaceAll(List<ContactBean> contactList) {
        if (contactList == null || contactList.isEmpty()) {
            Log.w(TAG, "replaceAll()  --> allContacts == null || allContacts.isEmpty()");
            return false;
        }
        try {
            Log.w(TAG, "replaceAll()  --> allContacts size： " + contactList.size() + " , content: " + contactList);
            if (isNewServiceVersion) {
                return replaceAllForJson(contactList);
            }
            List<ContentValues> valuesList = buildContentValues(contactList, new LinkedList<ContentValues>());
            if (valuesList.size() > 0) {
                valuesList.toArray(new ContentValues[valuesList.size()]);
                String json = GsonUtil.toJson(valuesList);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_CONTENT_VALUES, json);
                Bundle result = contentResolver.call(ContactApiConstant.CONTACT_SERVER_URI, METHOD_REPLACE_ALL, null, bundle);
                boolean success = result.getBoolean(METHOD_REPLACE_ALL);
                Log.d(TAG, success ? "replaceAll success! " : "replaceAll failed! ");
                return success;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean replaceAllForJson(List<ContactBean> contactList) {
        if (contactList == null || contactList.isEmpty()) {
            Log.w(TAG, "replaceAllForJson() called with: allContacts is null ");
            return false;
        }
        try {
            if (!isNewServiceVersion) {
                Log.i(TAG, "is low version, replaceAll at old method");
                return replaceAll(contactList);
            }
            Log.w(TAG, "replaceAllForJson()  --> allContacts size： " + contactList.size() + " , content: " + contactList);
            String json = GsonUtil.toJson(contactList);
            Bundle bundle = new Bundle();
            bundle.putString(KEY_CONTACTS_JSON, json);
            Bundle result = contentResolver.call(ContactApiConstant.CONTACT_SERVER_URI, METHOD_REPLACE_ALL_JSON, null, bundle);
            boolean success = result.getBoolean(METHOD_REPLACE_ALL_JSON);
            Log.d(TAG, success ? "replaceAll success! " : "replaceAll failed! ");
            return success;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    private List<ContentValues> buildContentValues(List<ContactBean> contactList, List<ContentValues> valuesList) {
        for (ContactBean contactBean : contactList) {
            ContentValues values = new ContentValues();
            values.put("id", contactBean.getId());
            values.put("contactServerId", contactBean.getContactServerId());
            values.put("mobileId", contactBean.getMobileId());
            values.put("mobileNumber", contactBean.getMobileNumber());
            values.put("isHide", contactBean.getIsHide());
            values.put("numberId", contactBean.getNumberId());
            values.put(ContactBean.FRIEND_WATCH_ID, contactBean.getFriendWatchId());
            values.put("salutation", contactBean.getSalutation());
            values.put("contactType", contactBean.getType());
            values.put("status", contactBean.getStatus());
            values.put("autoCall", contactBean.getAutoCall());
            values.put("customIcon", contactBean.getCustomIcon());
            values.put("friendIcon", contactBean.getFriendIcon());
            values.put("friendBindNumber", contactBean.getFriendBindNumber());
            values.put("friendModel", contactBean.getFriendModel());
            values.put("friendFirmware", contactBean.getFriendFirmware());
            values.put(Constants.PublishMedia.EXTRA_PHOTO_PATH, contactBean.getPhotoPath());
            values.put("lastUpdatedTimestamp", contactBean.getLastUpdatedTimestamp());
            values.put("isFrequent", contactBean.getIsFrequent());
            values.put("role", contactBean.getRole());
            values.put("remarkFriendName", contactBean.getRemarkFriendName());
            values.put("friendOriginalName", contactBean.getFriendOriginalName());
            values.put("sortSn", contactBean.getSortSn());
            values.put("videoChatMissedCallCount", contactBean.getVideoChatMissedCallCount());
            values.put("supportVideoChat", contactBean.getSupportVideoChat());
            values.put("viewSupportContext", contactBean.getViewSupportContext());
            values.put(BaseInfoManager.Key.GENIUS_NUMBER, contactBean.getGeniusNumber());
            values.put("openID", contactBean.getOpenID());
            values.put("dialogId", contactBean.getDialogId());
            values.put("mobileNumberNew", contactBean.getMobileNumberNew());
            values.put("countryCode", contactBean.getCountryCode());
            values.put(WatchAccountBase.KEY_REAL_NAME, contactBean.getRealName());
            if (hasCallAuthField) {
                values.put(FIELD_CALL_AUTH, contactBean.getCallAuth());
            }
            if (hasParentReviewField) {
                values.put(FIELD_PARENT_REVIEW, contactBean.getParentReview());
            }
            valuesList.add(values);
        }
        return valuesList;
    }

    private boolean checkServiceVersion(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(XTCSERVICE_PACKAGE_NAME, PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                return applicationInfo.metaData.getInt(XTCSERVICE_VERSION_META) >= 1;
            }
            Log.i(TAG, "checkServiceVersion, app info is empty");
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            Log.e(TAG, "checkServiceVersion error ", e);
            return false;
        }
    }

    /** 获取默认头像路径（角色为陌生人）。 */
    public String getDefaultPortraitPath(Context context) {
        if (!ContactApi.isInitDefaultHead()) {
            Log.d(TAG, "isInitDefaultHead = false");
            return "android.resource://" + context.getPackageName()
                    + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + R.drawable.default_portrait_default_v113;
        }
        return ContactApiConstant.CONTACT_HEAD_DIR + File.separator + defaultHeadMap.get(10);
    }

    /** 获取指定角色的默认头像路径。 */
    public String getDefaultPortraitPath(Context context, int role) {
        if (!ContactApi.isInitDefaultHead()) {
            Log.d(TAG, "isInitDefaultHead = false");
            return "android.resource://" + context.getPackageName()
                    + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + R.drawable.default_portrait_default_v113;
        }
        return ContactApiConstant.CONTACT_HEAD_DIR + File.separator + defaultHeadMap.get(role);
    }
}
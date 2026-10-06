package com.xtc.contactapi.contact.manager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.contactapi.contact.bean.CallLogBean;
import com.xtc.contactapi.contact.interfaces.ICallLogServe;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Completable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * 通话记录管理器，通过内容提供者读写通话记录并监听未读数变化。
 */
public class CallLogManager implements ICallLogServe {

    private static final String TAG = "CallLogManager";

    private static final Uri ALL_CALL_LOG_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/allCallLog");
    private static final Uri CLEAR_UNREAD_NUMBER_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/clearUnreadNumber");
    private static final Uri CLEAR_CALL_LOG_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/clearCallLog");
    private static final Uri CALL_LOG_LIMIT_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/callLogLimit");
    private static final Uri CALL_LOG_READ_STATE_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/callLogReadState");
    private static final Uri CALL_LOG_DELETE_FOR_BATCH_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/callLogDeleteForBatch");
    private static final Uri VERSION_JUDGE_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/versionJudge");
    private static final Uri DELETE_ALL_CALL_LOG_URI =
            Uri.parse("content://com.xtc.contact.provider.CallLogProvider/deleteAllCallLog");

    private static final String KEY_ID = "key_id";
    private static final String KEY_NUMBER = "key_number";

    private static volatile CallLogManager instance;

    private final Context context;
    private final ContentResolver contentResolver;

    private CallLogObserver callLogObserver;
    private ICallLogUnReadNumberCallback unReadNumberCallback;

    /** 未读数量变化回调。 */
    public interface ICallLogUnReadNumberCallback {
        void onUnReadNumberChanged(int unReadNumber);
    }

    private CallLogManager(Context context) {
        this.context = context.getApplicationContext();
        this.contentResolver = this.context.getContentResolver();
    }

    public static CallLogManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CallLogManager.class) {
                if (instance == null) {
                    instance = new CallLogManager(context);
                }
            }
        }
        return instance;
    }

    /** 注册未读数变化监听。 */
    public void registerUnReadNumberCallback(ICallLogUnReadNumberCallback callback) {
        if (contentResolver == null) {
            return;
        }
        if (callLogObserver == null) {
            this.unReadNumberCallback = callback;
            this.callLogObserver = new CallLogObserver(new Handler(Looper.getMainLooper()), this.unReadNumberCallback);
        }
        contentResolver.registerContentObserver(ALL_CALL_LOG_URI, true, callLogObserver);
        Log.i(TAG, "contentResolver registerContentObserver: callLogObserver = " + callLogObserver);
    }

    /** 注销未读数变化监听。 */
    public void unregisterUnReadNumberCallback() {
        if (contentResolver == null || callLogObserver == null) {
            return;
        }
        if (unReadNumberCallback != null) {
            unReadNumberCallback = null;
        }
        contentResolver.unregisterContentObserver(callLogObserver);
    }

    @Override
    public List<CallLogBean> getAllCallLog() {
        List<CallLogBean> callLogList = new ArrayList<>();
        Cursor cursor = contentResolver.query(ALL_CALL_LOG_URI, null, null, null, null);
        try {
            if (cursor == null) {
                Log.w(TAG, "getAllCallLog: cursor is null");
                return callLogList;
            }
            if (cursor.moveToNext()) {
                String json = cursor.getString(0);
                if (TextUtils.isEmpty(json)) {
                    Log.w(TAG, "getAllCallLog: callLog is empty");
                    return callLogList;
                }
                callLogList = JSONUtil.fromJSON(json, List.class, CallLogBean.class);
            }
            return callLogList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void clearUnreadNumber() {
        Completable.fromAction(new Action0() {
            @Override
            public void call() {
                Cursor cursor = contentResolver.query(CLEAR_UNREAD_NUMBER_URI, null, null, null, null);
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearCallLog() {
        Completable.fromAction(new Action0() {
            @Override
            public void call() {
                Cursor cursor = contentResolver.query(CLEAR_CALL_LOG_URI, null, null, null, null);
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public List<CallLogBean> getCallLogLimit(long startTime, long endTime) {
        List<CallLogBean> callLogList = new ArrayList<>();
        Cursor cursor = contentResolver.query(CALL_LOG_LIMIT_URI, null, null,
                new String[]{Long.toString(startTime), Long.toString(endTime)}, null);
        try {
            if (cursor == null) {
                Log.w(TAG, "getCallLogLimit: cursor is null");
                return callLogList;
            }
            if (cursor.moveToNext()) {
                String json = cursor.getString(0);
                if (TextUtils.isEmpty(json)) {
                    Log.w(TAG, "getCallLogLimit: callLog is empty");
                    return callLogList;
                }
                callLogList = JSONUtil.fromJSON(json, List.class, CallLogBean.class);
            }
            return callLogList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void updateReadState(final int readState) {
        Completable.fromAction(new Action0() {
            @Override
            public void call() {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, readState);
                contentResolver.update(CALL_LOG_READ_STATE_URI, values, null, null);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public boolean deleteCallLogForBatch(String selection) {
        try {
            return contentResolver.delete(CALL_LOG_DELETE_FOR_BATCH_URI, selection, null) > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteCallLogForBatch: ", e);
            return false;
        }
    }

    @Override
    public boolean isSupportDelete() {
        boolean supported = false;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(VERSION_JUDGE_URI, null, null, null, null);
            if (cursor != null && cursor.getColumnName(0) != null
                    && Integer.valueOf(cursor.getColumnName(0)) >= 1) {
                supported = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "isSupportDelete: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return supported;
    }

    @Override
    public boolean deleteAllCallLog() {
        try {
            return contentResolver.delete(DELETE_ALL_CALL_LOG_URI, null, null) > 0;
        } catch (Exception e) {
            Log.e(TAG, "deleteAllCallLog: ", e);
            return false;
        }
    }

    /**
     * 通话记录内容观察者，从 Uri 中解析未读数量。
     */
    public static class CallLogObserver extends ContentObserver {

        private static final String TAG = "CallLogObserver";

        private final ICallLogUnReadNumberCallback unReadNumberCallback;

        public CallLogObserver(Handler handler, ICallLogUnReadNumberCallback callback) {
            super(handler);
            this.unReadNumberCallback = callback;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri == null) {
                Log.w(TAG, "onChange: uri is null");
                return;
            }
            Log.i(TAG, "onChange: " + uri.toString());
            unReadNumberCallback.onUnReadNumberChanged(Integer.parseInt(uri.getQueryParameter(KEY_NUMBER)));
        }
    }
}
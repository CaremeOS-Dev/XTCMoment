package com.xtc.httplib.bigdata;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.xtc.httplib.constant.Const;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogMask;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

/** Records HTTP timing data through the big-data provider. */
public class BigdataClientManager {

    public static final String AUTHORITY = "com.xtc.http.bigdata.provider.HttpBigdataProvider";

    public static final SimpleDateFormat SIMPLE_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SIMPLE_HOUR_FORMAT = new SimpleDateFormat("HH");
    public static final Gson GSON = new Gson();

    public static final Uri SWITCH_QUERY_CONTENT_URI =
            Uri.parse("content://com.xtc.http.bigdata.provider.HttpBigdataProvider/query");
    public static final Uri INSERT_RAW_DATA_CONTENT_URI =
            Uri.parse("content://com.xtc.http.bigdata.provider.HttpBigdataProvider/insert");

    private static Context context = null;

    private final String TAG;
    public String bindNumber;
    private final ConcurrentHashMap<String, HttpRequestEvent> dnsReqRecords;
    public String openId;
    private Boolean switchOpen;
    public String watchId;
    public String watchInnerModel;
    private Boolean whitePackage;

    private static class BigdataManagerHolder {
        private static final BigdataClientManager INSTANCE = new BigdataClientManager();

        private BigdataManagerHolder() {
        }
    }

    public static BigdataClientManager getInstance() {
        return BigdataManagerHolder.INSTANCE;
    }

    private BigdataClientManager() {
        this.TAG = "BigdataClientManager";
        this.dnsReqRecords = new ConcurrentHashMap<>();
        this.switchOpen = null;
        this.whitePackage = null;
        this.watchId = null;
        this.bindNumber = null;
        this.watchInnerModel = null;
        this.openId = null;
    }

    public ConcurrentHashMap<String, HttpRequestEvent> getAllDnsReqRecords() {
        return this.dnsReqRecords;
    }

    public HttpRequestEvent getDnsReqRecord(String id) {
        return this.dnsReqRecords.get(id);
    }

    public void putDnsReqRecord(String id, HttpRequestEvent event) {
        if (supportPlatform()) {
            this.dnsReqRecords.put(id, event);
        }
    }

    /** Masks identifiers embedded in the url before it is recorded. */
    public String convertUrl(String url) {
        if (url == null) {
            return "NOT_INIT";
        }
        LogUtil.d("BigdataClientManager", "before opt url: " + url);
        String bindNumber = this.bindNumber;
        if (bindNumber != null && url.contains(bindNumber)) {
            url = url.replace(this.bindNumber, "bindnumber");
        }
        String watchId = this.watchId;
        if (watchId != null && url.contains(watchId)) {
            url = url.replace(this.watchId, "watchId");
        }
        String innerModel = this.watchInnerModel;
        if (innerModel != null && url.contains(innerModel)) {
            url = url.replace(this.watchInnerModel, "model");
        }
        String openId = this.openId;
        if (openId != null && url.contains(openId)) {
            url = url.replace(this.openId, "openId");
        }
        String[] segments = url.split(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
        String result = url;
        for (int i = 0; i < segments.length; i++) {
            if (i > 2) {
                String segment = segments[i];
                boolean looksLikeDate = true;
                if (segment.length() == 10 || segment.length() == 13) {
                    try {
                        Date date = new Date();
                        date.setTime(Long.parseLong(segment));
                        SIMPLE_DATE_FORMAT.format(date);
                    } catch (Exception ignored) {
                        looksLikeDate = false;
                    }
                    if (looksLikeDate) {
                        result = result.replace(segment, "timestamp");
                    }
                } else if (segment.length() == 14) {
                    try {
                        SIMPLE_DATE_TIME_FORMAT.parse(segment);
                    } catch (Exception ignored) {
                        looksLikeDate = false;
                    }
                    if (looksLikeDate) {
                        result = result.replace(segment, "datetime");
                    }
                } else if (segment.length() > 25) {
                    result = result.replace(segment, "long-str");
                }
            }
        }
        LogUtil.d("BigdataClientManager", "after opt url: " + result);
        return result;
    }

    public void init(Context context, String watchId, String bindNumber, String watchInnerModel, String openId) {
        LogUtil.d("BigdataClientManager", "init() called with: ctx = [" + context + "], watchId = ["
                + LogMask.toMaskWatchId(this.watchId) + "], bindNumber = ["
                + LogMask.toMaskBindNumber(this.bindNumber) + "], watchInnerModel = [" + this.watchInnerModel + "]");
        if (BigdataClientManager.context == null) {
            BigdataClientManager.context = context.getApplicationContext();
        }
        updateAccount(watchId, bindNumber, watchInnerModel, openId);
    }

    public void updateAccount(String watchId, String bindNumber, String watchInnerModel, String openId) {
        LogUtil.d("BigdataClientManager", "updateAccount() called with: watchId = [" + LogMask.toMaskWatchId(watchId)
                + "], bindNumber = [" + LogMask.toMaskBindNumber(bindNumber) + "], watchInnerModel = [" + watchInnerModel + "]");
        if (watchId == null) {
            throw new RuntimeException("watchid is null");
        }
        this.watchId = watchId;
        if (bindNumber == null) {
            throw new RuntimeException("bindNumber is null");
        }
        this.bindNumber = bindNumber;
        if (watchInnerModel == null) {
            throw new RuntimeException("watchInnerModel is null");
        }
        this.watchInnerModel = watchInnerModel;
        if (openId == null) {
            throw new RuntimeException("openId is null");
        }
        this.openId = openId;
    }

    /** Records the request timing data asynchronously. */
    public void collectRawData(final HttpRequestEvent event, final Request request, final int code,
                               final String reqLength, final String respLength, final String errorReason) {
        if (context == null) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                event.setOptUrl(convertUrl(request.url().toString()));
                event.setCode(String.valueOf(code));
                event.setReqContentLength(reqLength);
                event.setRespContentLength(respLength);
                event.setErrorReason(errorReason);
                long now = System.currentTimeMillis();
                event.setReqDate(SIMPLE_DATE_FORMAT.format(Long.valueOf(now)));
                event.setReqHour(SIMPLE_HOUR_FORMAT.format(Long.valueOf(now)));
                bury(event);
                BigdataClientManager.this.dnsReqRecords.remove(event.getId());
            }
        });
    }

    public boolean supportPlatform() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /** @return true when the platform collector is enabled for this package. */
    public boolean querySwitchOpen() {
        if (context == null) {
            return false;
        }
        if (this.switchOpen != null && this.whitePackage != null) {
            return this.switchOpen.booleanValue() && this.whitePackage.booleanValue();
        }
        ContentProviderClient client = null;
        Cursor cursor = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(SWITCH_QUERY_CONTENT_URI);
            if (client == null) {
                LogUtil.e("BigdataClientManager", "not found the bigdata server!!!");
                return false;
            }
            cursor = client.query(SWITCH_QUERY_CONTENT_URI, null, null, null, null);
            if (cursor == null || !cursor.moveToNext()) {
                return false;
            }
            for (String column : cursor.getColumnNames()) {
                int index = cursor.getColumnIndex(column);
                if (Const.SWITCH_OPEN.equals(column)) {
                    this.switchOpen = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(index)));
                } else if (Const.WHITE_PACKAGE.equals(column)) {
                    this.whitePackage = Boolean.valueOf(Boolean.parseBoolean(cursor.getString(index)));
                }
            }
            LogUtil.d("BigdataClientManager", "switchOpen: " + this.switchOpen);
            LogUtil.d("BigdataClientManager", "whitePackage: " + this.whitePackage);
            return this.switchOpen.booleanValue() && this.whitePackage.booleanValue();
        } catch (RemoteException e) {
            LogUtil.e("BigdataClientManager", e.toString());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (client != null) {
                client.close();
            }
        }
    }

    /** Writes the event row into the big-data provider. */
    public void bury(HttpRequestEvent event) {
        Context ctx = context;
        if (ctx == null) {
            return;
        }
        ContentProviderClient client = null;
        try {
            client = ctx.getContentResolver().acquireUnstableContentProviderClient(SWITCH_QUERY_CONTENT_URI);
            if (client == null) {
                LogUtil.e("BigdataClientManager", "not found the bigdata server!!!");
                return;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(Const.RAW_DATA_KEY, GSON.toJson(event));
            client.insert(INSERT_RAW_DATA_CONTENT_URI, contentValues);
        } catch (Throwable t) {
            LogUtil.e("BigdataClientManager", t);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
package com.xtc.httplib.okhttp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.bean.EncryptData;
import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.im.transpond.ITranspondCallback;
import com.xtc.im.transpond.TranspondManager;
import com.xtc.log.LogUtil;
import com.xtc.system.account.constant.NotificationFlag;
import com.xtc.utils.encode.JSONUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Forwards the configured requests over the IM channel and synthesizes the http response
 * from the transponded payload.
 *
 * @deprecated superseded by {@link ImBridgeInterceptor}.
 */
@Deprecated
public class PreprocessorInterceptor extends BaseInterceptor {

    private static final String TAG = LogTag.tag("PreprocessorInterceptor");

    public static boolean allTranspond = false;
    public static final List<String> NEED_TRANSPOND = new ArrayList<>();
    public static final List<String> NO_NEED_TRANSPOND = new ArrayList<>();

    private final AlarmManager alarmManager;
    private final AtomicLong lastImTranspondTime;
    private final AtomicInteger requestCode;
    private final CopyOnWriteArrayList<TranspondListener> transpondListenerList;
    private TimeOutReceiver timeOutReceiver;

    PreprocessorInterceptor(Context context) {
        super(context);
        this.lastImTranspondTime = new AtomicLong(0L);
        this.requestCode = new AtomicInteger(0);
        this.transpondListenerList = new CopyOnWriteArrayList<>();
        this.timeOutReceiver = null;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private Response createFromCache(Interceptor.Chain chain) {
        return null;
    }

    private synchronized void tryInit() {
        if (this.timeOutReceiver != null) {
            return;
        }
        this.timeOutReceiver = new TimeOutReceiver();
        this.timeOutReceiver.register(this.context);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        long startTime = SystemClock.elapsedRealtime();
        tryInit();
        Request request = chain.request();
        Response cachedResponse = createFromCache(chain);
        if (cachedResponse != null) {
            record(request, startTime);
            return cachedResponse;
        }
        Response transpondResponse = createFromTranspond(chain);
        if (transpondResponse != null) {
            record(request, startTime);
            return transpondResponse;
        }
        record(request, startTime);
        return chain.proceed(request);
    }

    private static void record(Request request, long startTime) {
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(request.header("uuid"));
        if (dnsRecord != null) {
            dnsRecord.setInterceptorPreprocessorTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
        }
    }

    private Response createFromTranspond(Interceptor.Chain chain) throws IOException {
        long now = SystemClock.elapsedRealtime();
        if (now - this.lastImTranspondTime.get() < ConfigOptions.TranspondCondition.INTERVAL_TIME) {
            LogUtil.w(TAG, "createFromTranspond not in limit time !");
            return null;
        }
        Request request = chain.request();
        String url = request.url().toString();
        if (!needTranspond(url)) {
            return null;
        }
        this.lastImTranspondTime.set(now);
        TranspondListener listener = new TranspondListener();
        this.transpondListenerList.add(listener);
        listener.requestCode = getRequestCode();
        Map<String, String> headers = requestByTranspond(request, url, listener);
        PendingIntent pendingIntent = createPendingIntent(this.context, listener);
        setAlarm(ConfigOptions.TranspondCondition.TRANSPOND_TIME_OUT + 1000L, pendingIntent);
        try {
            listener.waitResponse(ConfigOptions.TranspondCondition.TRANSPOND_TIME_OUT);
        } catch (InterruptedException e) {
            LogUtil.e(TAG, e);
        }
        cancelAlarm(pendingIntent);
        return createTranspondResponse(request, url, headers, listener);
    }

    private boolean needTranspond(String url) {
        if (allTranspond) {
            Iterator<String> iterator = NO_NEED_TRANSPOND.iterator();
            while (iterator.hasNext()) {
                if (url.contains(iterator.next())) {
                    LogUtil.i(TAG, "NEED_TRANSPOND no need Transpond,allTranspond = " + allTranspond);
                    return false;
                }
            }
            return true;
        }
        Iterator<String> iterator = NEED_TRANSPOND.iterator();
        while (iterator.hasNext()) {
            if (url.contains(iterator.next())) {
                LogUtil.i(TAG, "NEED_TRANSPOND need Transpond,allTranspond = " + allTranspond);
                return true;
            }
        }
        return false;
    }

    private Map<String, String> requestByTranspond(Request request, String url, ITranspondCallback callback)
            throws IOException {
        int imMethod = convertToImMethod(request.method());
        RequestBody requestBody = request.body();
        Buffer buffer = new Buffer();
        if (requestBody != null) {
            requestBody.writeTo(buffer);
        }
        String body = buffer.readUtf8();
        if (TextUtils.isEmpty(body)) {
            LogUtil.w(TAG, "requestByTranspond is empty");
        }
        EncryptData encryptData = HttpHelper.getEebbkKeyAndUseSelf(
                HttpManager.getInstance(this.context).getHttpClient(), request);
        String eebbkKey = encryptData.getEebbkKey();
        int rsaEncryptType = encryptData.getRsaEncryptType();
        String aesKey = encryptData.getAesKey();
        boolean encrypt = rsaEncryptType != 0;
        String encryptBody = HttpHelper.encryptBody(body, eebbkKey, encrypt, aesKey);
        byte[] bodyBytes = encryptBody == null ? null : encryptBody.getBytes();
        Map<String, String> headerMap = HttpHelper.generateHeaderMapWithBodyBytes(
                this.context, request, url, body.getBytes(), eebbkKey, encrypt, rsaEncryptType, aesKey);
        LogUtil.i(TAG, "requestByTranspond url = " + url + ", method = " + request.method()
                + ", requestBody = " + body);
        TranspondManager.transpond(url, imMethod, JSONUtil.toJSON(headerMap).getBytes(), bodyBytes, callback);
        return headerMap;
    }

    private Response createTranspondResponse(Request request, String url, Map<String, String> headerMap,
            TranspondListener listener) {
        byte[] bytes;
        NetBaseResult result;
        if (listener.hadTimeout || !listener.isSuccess || (bytes = listener.bytes) == null || bytes.length <= 0
                || (result = JSONUtil.fromJSON(
                        HttpHelper.decodeHttpResult(this.context, headerMap, new String(bytes), request),
                        NetBaseResult.class)) == null) {
            return null;
        }
        ResponseBody responseBody = ResponseBody.create(MediaType.parse(ConfigOptions.HeaderKey.MEDIA_TYPE),
                JSONUtil.toJSON(result));
        LogUtil.i(TAG, "createTranspondResponse url = " + url + ", method = " + request.method()
                + ", responseBody = " + responseBody);
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .message(url + " Http Response Successful")
                .code(Integer.parseInt("200"))
                .body(responseBody)
                .sentRequestAtMillis(-1L)
                .receivedResponseAtMillis(System.currentTimeMillis())
                .build();
    }

    private int convertToImMethod(String method) {
        String lower = method.toLowerCase();
        if (lower.equals("get")) {
            return 1;
        }
        if (lower.equals("post")) {
            return 2;
        }
        if (lower.equals("delete")) {
            return 3;
        }
        if (lower.equals("put")) {
            return 4;
        }
        LogUtil.e(TAG, "unknown http method: " + lower);
        return -1;
    }

    /** Waits for the transpond callback of a single request. */
    class TranspondListener extends ITranspondCallback.Stub {

        byte[] bytes;
        int requestCode = -1;
        boolean hadTimeout = false;
        boolean isSuccess = false;

        TranspondListener() {
        }

        public synchronized void waitResponse(long timeoutMillis) throws InterruptedException {
            wait(timeoutMillis);
        }

        public synchronized void waitTimeout() {
            this.hadTimeout = true;
            notify();
        }

        @Override
        public synchronized void onSuccess(byte[] body, int code) throws RemoteException {
            if (body != null && body.length > 0) {
                this.bytes = body;
            }
            this.isSuccess = true;
            notify();
        }

        @Override
        public synchronized void onError(String message) {
            LogUtil.e(PreprocessorInterceptor.TAG, "http transmit failed:" + message);
            this.isSuccess = false;
            notify();
        }
    }

    private void setAlarm(long delayMillis, PendingIntent pendingIntent) {
        long triggerAtMillis = SystemClock.elapsedRealtime() + delayMillis;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            this.alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            this.alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    private void cancelAlarm(PendingIntent pendingIntent) {
        this.alarmManager.cancel(pendingIntent);
    }

    private PendingIntent createPendingIntent(Context context, TranspondListener listener) {
        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(TimeOutReceiver.ACTION_TIME_OUT);
        intent.putExtra(TimeOutReceiver.EXTRA_REQUEST_CODE, listener.requestCode);
        return PendingIntent.getBroadcast(context, listener.requestCode, intent,
                NotificationFlag.NOTIFICATION_FLAG_HEADER);
    }

    private int getRequestCode() {
        if (this.requestCode.get() > Integer.MAX_VALUE) {
            this.requestCode.set(1);
        }
        return this.requestCode.getAndIncrement();
    }

    /** Broadcast receiver that wakes a waiting transpond request on timeout. */
    class TimeOutReceiver extends BroadcastReceiver {

        public static final String ACTION_TIME_OUT = "com.xtc.httplib.IMTranspond.TIME_OUT";
        public static final String EXTRA_REQUEST_CODE = "com.xtc.httplib.IMTranspond.REQUEST_CODE";

        TimeOutReceiver() {
        }

        public void register(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_TIME_OUT);
            context.registerReceiver(this, intentFilter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                LogUtil.e(PreprocessorInterceptor.TAG, "intent is null");
                return;
            }
            if (ACTION_TIME_OUT.equals(intent.getAction())) {
                int requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, -1);
                LogUtil.i(PreprocessorInterceptor.TAG, "receive time out alarm requestCode = " + requestCode);
                if (requestCode != -1) {
                    TranspondListener listener = findTagById(requestCode);
                    if (listener == null) {
                        LogUtil.w(PreprocessorInterceptor.TAG, "receive time out alarm tag is null");
                    } else {
                        listener.waitTimeout();
                    }
                }
            }
        }
    }

    TranspondListener findTagById(int requestCode) {
        for (TranspondListener listener : this.transpondListenerList) {
            if (listener.requestCode == requestCode) {
                return listener;
            }
        }
        return null;
    }
}
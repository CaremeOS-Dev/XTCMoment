package com.xtc.httplib.cache;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.alarm.SingleAlarmScheduler;
import com.xtc.httplib.auth.HttpTokenExpireManager;
import com.xtc.httplib.rxadapter.BaseXtcOnSubscribe;
import com.xtc.httplib.rxadapter.OnSubscribeCallBack;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;

/** Parks requests while the session token is being refreshed. */
public class RequestCacheManager {

    private static final long CONCURRENT_INTERVAL = 100;
    private static final String GET_TOKEN_REQUEST = "/smartwatch/rsa/getToken";
    private static final int LIMIT = 20;
    private static final long ONE_SECOND = 1000;
    private static final long POST_TIME = 5000;
    private static final long TIME_OUT = 25000;
    private static final String TAG = LogTag.tag("RequestCacheManager");
    private static volatile RequestCacheManager instance = null;

    private final Runnable postRun = new Runnable() {
        @Override
        public void run() {
            onPostDeal();
        }
    };

    private boolean isPosting;
    private Context context = HttpManager.getInstance(ContextUtils.getContext()).getContext();
    private int hashCode = hashCode();
    private ConcurrentLinkedQueue<RequestCacheData> requestCacheDataList = new ConcurrentLinkedQueue<>();
    private SingleAlarmScheduler singleAlarmScheduler = new SingleAlarmScheduler(this.context);
    private ConcurrentHashMap<String, Boolean> requestHashMap = new ConcurrentHashMap<>();

    public static RequestCacheManager getInstance() {
        if (instance == null) {
            synchronized (RequestCacheManager.class) {
                if (instance == null) {
                    instance = new RequestCacheManager();
                }
            }
        }
        return instance;
    }

    private RequestCacheManager() {
    }

    /** @return true when the request may proceed immediately. */
    public synchronized boolean checkHttpTokenValid(Call call, BaseXtcOnSubscribe onSubscribe, Subscriber subscriber) {
        try {
            if (HttpManager.getInstance(this.context).getHttpClient().getAppInfo().isHttpTokenValid()) {
                return true;
            }
            String url = call.request().url().toString();
            if (!TextUtils.isEmpty(url) && !url.contains(GET_TOKEN_REQUEST)) {
                cache(url, onSubscribe, subscriber);
                return false;
            }
            return true;
        } catch (Throwable t) {
            LogUtil.d(TAG, "checkHttpTokenValid error:", t);
            return true;
        }
    }

    private void cache(String url, BaseXtcOnSubscribe onSubscribe, Subscriber subscriber) {
        if (this.requestCacheDataList.size() >= LIMIT) {
            callOnTokenExpireError(this.requestCacheDataList.poll());
        }
        LogUtil.d(TAG, "cache: url = [" + url + "]");
        this.requestCacheDataList.offer(new RequestCacheData(SystemClock.elapsedRealtime(), onSubscribe, subscriber));
        if (this.singleAlarmScheduler.isAlarmNow() && this.isPosting) {
            return;
        }
        checkNextCachePostOrAlarm();
    }

    private void checkNextCachePostOrAlarm() {
        long now = SystemClock.elapsedRealtime();
        Iterator<RequestCacheData> iterator = this.requestCacheDataList.iterator();
        while (iterator.hasNext()) {
            RequestCacheData data = iterator.next();
            if (data == null) {
                iterator.remove();
                continue;
            }
            long remaining = TIME_OUT - (now - data.getCacheTime());
            if (remaining <= ONE_SECOND) {
                iterator.remove();
                callOnTokenExpireError(data);
            } else if (remaining <= POST_TIME) {
                if (!this.isPosting) {
                    HandlerUtil.removeCallbacks(this.postRun);
                    this.isPosting = true;
                    HandlerUtil.runOnBackgroundDelay(this.postRun, remaining);
                    LogUtil.d(TAG, "checkNextCachePostOrAlarm: post delay time = " + remaining);
                    if (this.singleAlarmScheduler.isAlarmNow()) {
                        return;
                    }
                }
            } else {
                this.singleAlarmScheduler.addAlarm(this.hashCode, remaining);
                return;
            }
        }
    }

    private void callOnTokenExpireError(RequestCacheData data) {
        if (data == null) {
            return;
        }
        LogUtil.d(TAG, "callOnTokenExpireError");
        data.getOnSubscribe().callOnTokenExpireError(data.getSubscriber());
        HttpTokenExpireManager.getInstance().checkTokenExpire(false);
    }

    private synchronized void onPostDeal() {
        LogUtil.d(TAG, "onPostDeal");
        this.isPosting = false;
        checkNextCachePostOrAlarm();
    }

    public synchronized void onTimeAlarm(int alarmHashCode) {
        if (alarmHashCode != this.hashCode) {
            return;
        }
        LogUtil.d(TAG, "onTimeAlarm: hashCode = [" + alarmHashCode + "]");
        this.singleAlarmScheduler.setAlarmNow(false);
        checkNextCachePostOrAlarm();
    }

    public void onHttpTokenValid() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                onHttpTokenValidInterval();
            }
        });
    }

    private synchronized void onHttpTokenValidInterval() {
        LogUtil.d(TAG, "onHttpTokenValidInterval");
        HandlerUtil.removeCallbacks(this.postRun);
        this.isPosting = false;
        this.singleAlarmScheduler.cancel(this.hashCode);
        Iterator<RequestCacheData> iterator = this.requestCacheDataList.iterator();
        while (iterator.hasNext()) {
            RequestCacheData data = iterator.next();
            iterator.remove();
            if (data != null) {
                BaseXtcOnSubscribe<Response> onSubscribe = data.getOnSubscribe();
                String url = onSubscribe.getUrl();
                if (!TextUtils.isEmpty(url) && !this.requestHashMap.containsKey(url)) {
                    this.requestHashMap.put(url, true);
                }
                try {
                    Thread.sleep(CONCURRENT_INTERVAL);
                } catch (InterruptedException ignored) {
                    // ignored
                }
                LogUtil.d(TAG, "callAfterGetToken: url = " + url);
                onSubscribe.callRealRequest(data.getSubscriber(), !iterator.hasNext() ? new OnSubscribeCallBack() {
                    @Override
                    public void onComplete() {
                        LogUtil.d(RequestCacheManager.TAG, "is cache request onComplete");
                        RequestCacheManager.this.requestHashMap.clear();
                    }
                } : null);
            }
        }
    }

    public boolean isContainsUrl(String url) {
        return !TextUtils.isEmpty(url) && this.requestHashMap.containsKey(url);
    }
}
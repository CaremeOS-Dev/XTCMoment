package com.xtc.httplib.okhttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;

import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.bean.FrequentRequestInfo;
import com.xtc.httplib.bean.RequestTrafficInfo;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.cache.RequestCacheManager;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.httplib.util.HttpUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.personalinfo.constant.PersonalInfoConstant;
import com.xtc.ui.widget.privacy.PrivacyCommon;
import com.xtc.utils.system.SystemProperty;
import com.xtc.utils.system.SystemPropertyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** Guards the http stack against boot storms, request floods and heavy traffic. */
public class MonitorInterceptor extends BaseInterceptor {

    private static final String TAG = LogTag.tag("MonitorInterceptor");
    private static final String PROPERTY_CTA_PERMISSION = "persist.sys.cta.permission";

    private static final String[] INTERCEPT_REQUEST_PACKAGE_NAME = {
            "com.xtc.i3launcher", "com.xtc.migration", "com.xtc.theme", "com.xtc.bleaddfriend",
            "com.xtc.message", PersonalInfoConstant.PersonalMainIntent.SETTING_PACKAGE_NAME,
            "com.xtc.alarmclock", "com.xtc.appupdate", PersonalInfoConstant.PersonalMainIntent.SETTING_PACKAGE_NAME,
            "com.xtc.systemupdate_i11", "com.xtc.datacenter", "com.xtc.xws", "com.xtc.videochat"};
    private static final ArrayList<String> INTERCEPT_REQUEST_PACKAGE_NAMES =
            new ArrayList<>(Arrays.asList(INTERCEPT_REQUEST_PACKAGE_NAME));

    public static long BOOT_LIMIT_TIME = 0;
    public static final List<String> NECESSARY_BOOT_URLS = new ArrayList<>();
    public static boolean openMonitor = true;
    public static boolean haveConfirmPermission = true;
    public static boolean resetFrequentRequestState = false;
    public static final Set<String> NO_NEED_MONITOR = new CopyOnWriteArraySet<>();
    private static volatile RequestTrafficInfo requestTrafficInfo = new RequestTrafficInfo();

    private final Map<String, FrequentRequestInfo> frequentRequestMap;
    private final Handler mainHandler;

    MonitorInterceptor(Context context) {
        super(context);
        this.frequentRequestMap = new ConcurrentHashMap<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        long requestTime = SystemClock.elapsedRealtime();
        Request request = chain.request();
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(request.header("uuid"));
        String subUrl = subUrl(request.url().toString());
        LogUtil.i(TAG, "intercept url = " + subUrl + ",requestTime = " + requestTime);
        int errorCode;
        if (isBootLimitTime(requestTime, subUrl)) {
            errorCode = ConfigOptions.MonitorError.MONITOR_ERROR_BOOT_LIMIT_TIME;
        } else if (isRequesting(requestTime, subUrl)) {
            errorCode = ConfigOptions.MonitorError.MONITOR_ERROR_REQUESTING;
        } else if (isFrequentRequest(requestTime, subUrl)) {
            errorCode = ConfigOptions.MonitorError.MONITOR_ERROR_FREQUENT_REQUEST;
            if (!isNeedMonitor(subUrl)) {
                errorCode = -1;
            }
        } else {
            errorCode = -1;
        }
        LogUtil.i(TAG, "isFrequentRequest errorCode = " + errorCode + "---openMonitor = " + openMonitor);
        if (errorCode != -1 && openMonitor) {
            dealException(subUrl, errorCode);
            setRequesting(subUrl, false);
            return HttpUtil.createErrorResponse(request, subUrl, errorCode, dnsRecord);
        }
        boolean hadAllowedLauncherPermission = getHadAllowedLauncherPermission(this.context);
        if (!hadAllowedLauncherPermission || !haveConfirmPermission) {
            LogUtil.i(TAG, "hadAllowedLauncherPermission:" + hadAllowedLauncherPermission
                    + ",haveConfirmPermission:" + haveConfirmPermission);
            dealException(subUrl, ConfigOptions.MonitorError.MONITOR_ERROR_NO_NETWORK_PERMISSION);
            setRequesting(subUrl, false);
            return HttpUtil.createErrorResponse(request, subUrl,
                    ConfigOptions.MonitorError.MONITOR_ERROR_NO_NETWORK_PERMISSION, dnsRecord);
        }
        if (!isConnected(this.context)) {
            dealException(subUrl, ConfigOptions.MonitorError.MONITOR_ERROR_NO_NETWORK_CONNECTIVITY);
            setRequesting(subUrl, false);
            return HttpUtil.createErrorResponse(request, subUrl,
                    ConfigOptions.MonitorError.MONITOR_ERROR_NO_NETWORK_CONNECTIVITY, dnsRecord);
        }
        boolean needLimit = true;
        setRequesting(subUrl, true);
        RequestBody requestBody = request.body();
        long requestLength = requestBody != null ? requestBody.contentLength() : 0L;
        long beforeRequestTime = SystemClock.elapsedRealtime() - requestTime;
        long responseTime = -1;
        try {
            Response response = chain.proceed(request);
            responseTime = SystemClock.elapsedRealtime();
            if (response != null && response.isSuccessful()) {
                long totalLength = requestLength
                        + response.peekBody(PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED).contentLength();
                if (!isNeedMonitor(subUrl) || (request.body() != null && request.body().contentType() != null
                        && request.body().contentType().toString().contains("multipart/form-data"))) {
                    LogUtil.i(TAG, "no need limit: " + subUrl);
                    needLimit = false;
                }
                if (needLimit && totalLength > ConfigOptions.MonitorCondition.REQUEST_MAX_TRAFFIC) {
                    dealException(subUrl, ConfigOptions.MonitorError.MONITOR_ERROR_HEAVY_TRAFFIC);
                }
                dealDayTotalTraffic(totalLength);
                HttpManager.getInstance(this.context).onHttpSuccessMonitor();
            }
            setRequesting(subUrl, false);
            recordMonitorTime(dnsRecord, beforeRequestTime, responseTime);
            return response;
        } catch (Exception e) {
            LogUtil.e(TAG, "intercept e = " + e.getMessage());
            setRequesting(subUrl, false);
            recordMonitorTime(dnsRecord, beforeRequestTime, responseTime);
            throw e;
        } catch (Throwable t) {
            setRequesting(subUrl, false);
            recordMonitorTime(dnsRecord, beforeRequestTime, responseTime);
            throw t;
        }
    }

    private static void recordMonitorTime(HttpRequestEvent dnsRecord, long beforeRequestTime, long responseTime) {
        if (responseTime == -1 || dnsRecord == null) {
            return;
        }
        long costTime = beforeRequestTime + (SystemClock.elapsedRealtime() - responseTime);
        dnsRecord.setInterceptorMonitorTime(String.valueOf(costTime));
    }

    private boolean isNeedMonitor(String url) {
        Iterator<String> iterator = NO_NEED_MONITOR.iterator();
        while (iterator.hasNext()) {
            if (url.contains(iterator.next())) {
                LogUtil.i(TAG, "no need monitor");
                return false;
            }
        }
        return true;
    }

    private void dealException(String url, int errorCode) {
        LogUtil.i(TAG, "dealException url = " + url + ",errorCode = " + errorCode);
    }

    private boolean isBootLimitTime(long time, String url) {
        Iterator<String> iterator = NECESSARY_BOOT_URLS.iterator();
        while (iterator.hasNext()) {
            if (url.contains(iterator.next())) {
                return true;
            }
        }
        return time < BOOT_LIMIT_TIME;
    }

    private boolean isRequesting(long time, String url) {
        List<Long> requestTimes;
        if (RequestCacheManager.getInstance().isContainsUrl(url)) {
            LogUtil.d(TAG, "isRequesting: RequestCache url");
            return false;
        }
        FrequentRequestInfo info = this.frequentRequestMap.get(url);
        if (info == null || (requestTimes = info.getRequestTimes()) == null || requestTimes.size() == 0
                || time - requestTimes.get(requestTimes.size() - 1) > ConfigOptions.MonitorCondition.REQUESTING_TIME_OUT) {
            return false;
        }
        return info.isRequesting();
    }

    private void setRequesting(String url, boolean requesting) {
        FrequentRequestInfo info = this.frequentRequestMap.get(url);
        if (info != null) {
            info.setRequesting(requesting);
        }
    }

    private boolean isFrequentRequest(long time, String url) {
        if (RequestCacheManager.getInstance().isContainsUrl(url)) {
            LogUtil.d(TAG, "isFrequentRequest: RequestCache url");
            return false;
        }
        if (resetFrequentRequestState) {
            this.frequentRequestMap.clear();
            resetFrequentRequestState = false;
        }
        FrequentRequestInfo info = this.frequentRequestMap.get(url);
        if (info == null) {
            FrequentRequestInfo newInfo = new FrequentRequestInfo();
            newInfo.setRequestUrl(url);
            newInfo.getRequestTimes().add(time);
            this.frequentRequestMap.put(url, newInfo);
            return false;
        }
        info.getRequestTimes().add(time);
        if (time - info.getRequestTimes().get(0) < ConfigOptions.MonitorCondition.FREQUENT_REQUEST_PERIOD) {
            if (info.getRequestTimes().size() > ConfigOptions.MonitorCondition.FREQUENT_REQUEST_COUNT) {
                info.getRequestTimes().remove(0);
                return true;
            }
        } else {
            int index = 0;
            while (index < info.getRequestTimes().size()
                    && time - info.getRequestTimes().get(index) >= ConfigOptions.MonitorCondition.FREQUENT_REQUEST_PERIOD) {
                index++;
            }
            if (index < info.getRequestTimes().size()) {
                info.setRequestTimes(copyData(info.getRequestTimes(), index));
            } else {
                this.frequentRequestMap.remove(url);
            }
        }
        return false;
    }

    private List<Long> copyData(List<Long> list, int fromIndex) {
        CopyOnWriteArrayList<Long> copy = new CopyOnWriteArrayList<>();
        for (int i = fromIndex; i < list.size(); i++) {
            copy.add(list.get(i));
        }
        return copy;
    }

    private void dealDayTotalTraffic(long traffic) {
        long currentTimeMillis = System.currentTimeMillis();
        long startTime = (((requestTrafficInfo.getStartTime() / 24) * 3600 * 1000) + 1) * 24 * 3600 * 1000;
        if (requestTrafficInfo.getHadRecord()) {
            LogUtil.i(TAG, "dealDayTotalTraffic HadRecord");
            return;
        }
        if (currentTimeMillis < startTime) {
            requestTrafficInfo.addTotalTraffic(traffic);
            if (requestTrafficInfo.getTotalTraffic() > ConfigOptions.MonitorCondition.DAY_REQUEST_MAX_TRAFFIC) {
                requestTrafficInfo.setHadRecord(true);
                LogUtil.w(TAG, "dealDayTotalTraffic Total Traffic Over 524288000");
                recordTotalTraffic(currentTimeMillis);
            }
            return;
        }
        requestTrafficInfo.setStartTime(currentTimeMillis);
        requestTrafficInfo.setTotalTraffic(0L);
        requestTrafficInfo.setHadRecord(false);
        requestTrafficInfo.addTotalTraffic(traffic);
    }

    private void recordTotalTraffic(long currentTime) {
        String packageName = this.context.getPackageName();
        HashMap<String, String> extras = new HashMap<>();
        extras.put(PrivacyCommon.PrivacyExtras.EXTRA_PACKAGE_NAME, packageName);
        extras.put("currentTime", String.valueOf(currentTime));
        extras.put("maxTraffic", String.valueOf(ConfigOptions.MonitorCondition.DAY_REQUEST_MAX_TRAFFIC));
        BehaviorUtil.customEvent(this.context, "Http_Monitor_Total_Traffic_Over", packageName, null, extras);
    }

    private NetworkInfo getActiveNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    private boolean isConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

    /** @return true when the caller is allowed to send http requests before user confirmation. */
    public static boolean getHadAllowedLauncherPermission(Context context) {
        return !INTERCEPT_REQUEST_PACKAGE_NAMES.contains(context.getPackageName())
                || SystemPropertyUtil.getBoolean(SystemProperty.BIND_STATUS, false)
                || SystemPropertyUtil.getBoolean(PROPERTY_CTA_PERMISSION, false);
    }
}
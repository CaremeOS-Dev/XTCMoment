package com.xtc.httplib.okhttp;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.HttpClient;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.cache.AesKeyCache;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.log.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** Decrypts the encrypted response body before it reaches the converter. */
public class HttpResponseInterceptor extends BaseInterceptor {

    private static final String TAG = LogTag.tag("HttpResponseInterceptor");

    private final HttpClient httpClient;

    HttpResponseInterceptor(Context context, HttpClient httpClient) {
        super(context);
        this.httpClient = httpClient;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = SystemClock.elapsedRealtime();
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(request.header("uuid"));
        Response response = chain.proceed(request);
        if (dnsRecord != null) {
            dnsRecord.setCallServerCostTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
        }
        long decryptStartTime = SystemClock.elapsedRealtime();
        Response decryptedResponse = decryptResponse(response, HttpHelper.getEebbkKey(this.httpClient, request));
        if (dnsRecord != null) {
            dnsRecord.setInterceptorRespTime(String.valueOf(SystemClock.elapsedRealtime() - decryptStartTime));
        }
        return decryptedResponse;
    }

    private Response decryptResponse(Response response, String eebbkKey) throws IOException {
        if (response.body() == null) {
            return response;
        }
        String body = response.peekBody(PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED).string();
        if (!TextUtils.isEmpty(body) && !TextUtils.isEmpty(eebbkKey)) {
            String encrypted = response.header(ConfigOptions.HeaderKey.ENCRYPTED);
            String sign = response.header(ConfigOptions.HeaderKey.EEBBK_SIGN);
            if (encrypted != null && encrypted.contains(ConfigOptions.HeaderKey.ENCRYPTED)) {
                body = HttpHelper.decodeHttpResponseBody(eebbkKey, body, AesKeyCache.getInstance().getKey(sign));
            }
        } else {
            LogUtil.w(TAG, "result or eebbkKey is null");
        }
        Response.Builder builder = response.newBuilder();
        builder.body(ResponseBody.create(MediaType.parse(ConfigOptions.HeaderKey.MEDIA_TYPE), body));
        return builder.build();
    }
}
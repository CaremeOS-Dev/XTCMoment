package com.xtc.httplib.okhttp;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.domain.DomainManager;
import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.HttpClient;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.bean.AppInfo;
import com.xtc.httplib.bean.EncryptData;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.cache.AesKeyCache;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.log.LogUtil;
import com.xtc.utils.system.WatchModelUtil;

import java.io.IOException;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/** Encrypts the outgoing request and appends the uuid query parameter. */
public class HttpRequestInterceptor extends BaseInterceptor {

    private static final String TAG = LogTag.tag("HttpRequestInterceptor");
    private static final String MEDIA_SUBTYPE_JSON = "json";
    private static final String MEDIA_TYPE_APPLICATION = "application";

    private final HttpClient httpClient;

    HttpRequestInterceptor(Context context, HttpClient httpClient) {
        super(context);
        this.httpClient = httpClient;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        return doHttpDns(chain, encryptRequest(chain), SystemClock.elapsedRealtime());
    }

    private Response doHttpDns(Interceptor.Chain chain, Request.Builder builder, long startTime) throws IOException {
        builder.url(getRedirectURL(builder.build().url().toString(), false));
        HttpRequestEvent dnsRecord = BigdataClientManager.getInstance().getDnsReqRecord(chain.request().header("uuid"));
        if (dnsRecord != null) {
            dnsRecord.setInterceptorReqTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
        }
        return chain.proceed(builder.build());
    }

    private Request.Builder encryptRequest(Interceptor.Chain chain) {
        Request request = chain.request();
        String url = request.url().toString();
        String method = request.method();
        String requestBodyString = HttpHelper.getRequestBodyString(request.body());
        EncryptData encryptData = HttpHelper.getEebbkKeyAndUseSelf(this.httpClient, request);
        String eebbkKey = encryptData.getEebbkKey();
        int rsaEncryptType = encryptData.getRsaEncryptType();
        String aesKey = encryptData.getAesKey();
        boolean encrypt = rsaEncryptType != 0 && isEncrypt(request);
        LogUtil.i(TAG, "encryptRequest url:" + url + ", method:" + method + ", isEncrypt:" + encrypt);
        String baseRequestParamJson = HttpHelper.buildBaseRequestParamJson(this.httpClient);
        String encryptRequestParam = (TextUtils.isEmpty(eebbkKey) || !encrypt)
                ? baseRequestParamJson
                : HttpHelper.generateEncryptRequestParam(baseRequestParamJson, aesKey);
        String sign = HttpHelper.generateSign(aesKey, url, baseRequestParamJson, requestBodyString);
        AesKeyCache.getInstance().cache(sign, aesKey);
        Request.Builder builder = request.newBuilder();
        addHeader(request, builder, sign, encryptRequestParam, eebbkKey, encrypt, rsaEncryptType);
        LogUtil.v(TAG, "encryptRequest header = " + headersToString(builder.build().headers()));
        dealBody(request, builder, requestBodyString, eebbkKey, encrypt, aesKey);
        return builder;
    }

    private String headersToString(Headers headers) {
        if (headers == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        int size = headers.size();
        for (int i = 0; i < size; i++) {
            if (!ConfigOptions.HeaderKey.BASE_REQUEST_PARAM.equals(headers.name(i))
                    && !ConfigOptions.HeaderKey.CONTENT_TYPE.equals(headers.name(i))
                    && !ConfigOptions.HeaderKey.IM_SDK_VERSION_CODE.equals(headers.name(i))) {
                builder.append(headers.name(i));
                builder.append(": ");
                builder.append(headers.value(i));
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private boolean isEncrypt(Request request) {
        RequestBody body = request.body();
        if (body == null) {
            return true;
        }
        MediaType contentType = body.contentType();
        if (contentType == null) {
            try {
                if (body.contentLength() <= 0) {
                    return true;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "isEncrypt: get contentLength error: ", e);
            }
        }
        return contentType != null
                && MEDIA_TYPE_APPLICATION.equals(contentType.type())
                && MEDIA_SUBTYPE_JSON.equals(contentType.subtype());
    }

    private void dealBody(Request request, Request.Builder builder, String body, String eebbkKey, boolean encrypt,
            String aesKey) {
        RequestBody requestBody = request.body();
        if (!TextUtils.isEmpty(body) && !TextUtils.isEmpty(eebbkKey) && encrypt) {
            String encryptBody = HttpHelper.generateEncryptBody(body, eebbkKey, aesKey);
            if (!TextUtils.isEmpty(encryptBody)) {
                requestBody = RequestBody.create(requestBody.contentType(), encryptBody);
            }
        }
        builder.method(request.method(), requestBody);
    }

    private void addHeader(Request request, Request.Builder builder, String sign, String encryptRequestParam,
            String eebbkKey, boolean encrypt, int rsaEncryptType) {
        try {
            addHeader(request, builder, ConfigOptions.HeaderKey.CONTENT_TYPE, ConfigOptions.HeaderKey.MEDIA_TYPE);
            addHeader(request, builder, ConfigOptions.HeaderKey.MODEL, WatchModelUtil.getWatchInnerModel());
            addHeader(request, builder, ConfigOptions.HeaderKey.IM_SDK_VERSION_CODE, String.valueOf(102));
            addHeader(request, builder, ConfigOptions.HeaderKey.PACKAGE_VERSION,
                    String.valueOf(this.httpClient.getVersionCode()));
            addHeader(request, builder, ConfigOptions.HeaderKey.PACKAGE_NAME, this.httpClient.getPackageName());
            builder.header(ConfigOptions.HeaderKey.EEBBK_SIGN, sign);
            builder.header(ConfigOptions.HeaderKey.BASE_REQUEST_PARAM, encryptRequestParam);
            String dataCenterCode = DomainManager.getInstance(this.context).getDataCenterCode();
            if (!TextUtils.isEmpty(dataCenterCode)) {
                builder.header(ConfigOptions.HeaderKey.DATA_CENTER, dataCenterCode);
            }
            AppInfo appInfo = this.httpClient.getAppInfo();
            if (appInfo != null) {
                String version = appInfo.getVersion();
                if (!TextUtils.isEmpty(version)) {
                    builder.header(ConfigOptions.HeaderKey.VERSION, version);
                }
                String grey = appInfo.getGrey();
                if (!TextUtils.isEmpty(grey)) {
                    builder.header(ConfigOptions.HeaderKey.GREY, grey);
                }
                if (rsaEncryptType == 3 || rsaEncryptType == 2) {
                    builder.header(ConfigOptions.HeaderKey.EEBBK_KEY_ID, appInfo.getKeyId());
                }
                Map<String, String> httpHeadParamMap = appInfo.getHttpHeadParamMap();
                if (httpHeadParamMap != null) {
                    for (Map.Entry<String, String> entry : httpHeadParamMap.entrySet()) {
                        String key = entry.getKey();
                        if (!TextUtils.isEmpty(key)) {
                            builder.header(key, entry.getValue());
                        }
                    }
                }
            }
            if (!TextUtils.isEmpty(eebbkKey) && encrypt) {
                builder.addHeader(ConfigOptions.HeaderKey.EEBBK_KEY, eebbkKey);
                builder.addHeader(ConfigOptions.HeaderKey.ENCRYPTED, ConfigOptions.HeaderKey.ENCRYPTED);
                builder.addHeader(ConfigOptions.HeaderKey.CONTENT_ENCODING, "gzip");
            }
            if (!TextUtils.isEmpty(HttpManager.getInstance(this.context).getAcceptLanguage())) {
                builder.addHeader(ConfigOptions.HeaderKey.ACCEPT_LANGUAGE,
                        HttpManager.getInstance(this.context).getAcceptLanguage());
            }
            if (TextUtils.isEmpty(HttpManager.getInstance(this.context).getWatchTimeZone())) {
                return;
            }
            builder.addHeader(ConfigOptions.HeaderKey.WATCH_TIME_ZONE,
                    HttpManager.getInstance(this.context).getWatchTimeZone());
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    private void addHeader(Request request, Request.Builder builder, String name, String value) {
        if (request.headers().get(name) == null) {
            builder.header(name, value);
        }
    }
}
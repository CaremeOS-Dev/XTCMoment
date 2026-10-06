package com.xtc.httplib.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.common.Constants;
import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.HttpClient;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.annotation.KeepHead;
import com.xtc.httplib.auth.HttpAuthManager;
import com.xtc.httplib.bean.AppInfo;
import com.xtc.httplib.bean.DeviceInfo;
import com.xtc.httplib.bean.EncryptData;
import com.xtc.httplib.bean.NetBaseRequestParam;
import com.xtc.httplib.cache.AesKeyCache;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.AESUtil;
import com.xtc.utils.encode.GzipUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.encode.MD5Util;
import com.xtc.utils.encode.RSAUtil;
import com.xtc.utils.encode.UUIDUtil;
import com.xtc.utils.security.XtcSecurity;
import com.xtc.utils.system.WatchModelUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Invocation;

/**
 * Builds the encrypted request headers/body and decrypts the response body.
 *
 * <p>All helpers are static because the interceptors only need pure transformations.
 */
public class HttpHelper {

    private static final String TAG = LogTag.tag("DefaultOkHttpClient");

    /** Encrypts the request body when encryption is enabled. */
    public static String encryptBody(String body, String eebbkKey, boolean encrypt, String aesKey) {
        if (!isEmptyString(body) && !isEmptyString(eebbkKey) && encrypt) {
            body = generateEncryptBody(body, eebbkKey, aesKey);
        }
        LogUtil.i(TAG, "encrypt body:" + body);
        return body;
    }

    /** Builds the header map for the IM-transpond channel. */
    public static Map<String, String> generateHeaderMapWithBodyBytes(Context context, Request request, String url,
            byte[] bodyBytes, String eebbkKey, boolean encrypt, int rsaEncryptType, String aesKey) {
        HttpClient httpClient = HttpManager.getInstance(context).getHttpClient();
        HashMap<String, String> map = new HashMap<>();
        addHeader(request, map, ConfigOptions.HeaderKey.CONTENT_TYPE, ConfigOptions.HeaderKey.MEDIA_TYPE);
        String baseRequestParamJson = buildBaseRequestParamJson(httpClient);
        String encryptRequestParam = (isEmptyString(eebbkKey) || !encrypt)
                ? baseRequestParamJson
                : generateEncryptRequestParam(baseRequestParamJson, aesKey);
        String sign = generateSignWithBodyBytes(aesKey, url, baseRequestParamJson, bodyBytes);
        map.put(ConfigOptions.HeaderKey.EEBBK_SIGN, sign);
        AesKeyCache.getInstance().cache(sign, aesKey);
        map.put(ConfigOptions.HeaderKey.BASE_REQUEST_PARAM, encryptRequestParam);
        addHeader(request, map, ConfigOptions.HeaderKey.MODEL, WatchModelUtil.getWatchInnerModel());
        addHeader(request, map, ConfigOptions.HeaderKey.IM_SDK_VERSION_CODE, String.valueOf(102));
        addHeader(request, map, ConfigOptions.HeaderKey.PACKAGE_VERSION, String.valueOf(httpClient.getVersionCode()));
        addHeader(request, map, ConfigOptions.HeaderKey.PACKAGE_NAME, httpClient.getPackageName());
        AppInfo appInfo = httpClient.getAppInfo();
        if (appInfo != null) {
            String version = appInfo.getVersion();
            if (!isEmptyString(version)) {
                map.put(ConfigOptions.HeaderKey.VERSION, version);
            }
            String grey = appInfo.getGrey();
            if (!isEmptyString(grey)) {
                map.put(ConfigOptions.HeaderKey.GREY, grey);
            }
            if (rsaEncryptType == 3 || rsaEncryptType == 2) {
                map.put(ConfigOptions.HeaderKey.EEBBK_KEY_ID, appInfo.getKeyId());
            }
            Map<String, String> httpHeadParamMap = appInfo.getHttpHeadParamMap();
            if (httpHeadParamMap != null) {
                map.putAll(httpHeadParamMap);
            }
        }
        if (!isEmptyString(eebbkKey) && encrypt) {
            map.put(ConfigOptions.HeaderKey.EEBBK_KEY, eebbkKey);
            map.put(ConfigOptions.HeaderKey.ENCRYPTED, ConfigOptions.HeaderKey.ENCRYPTED);
            map.put(ConfigOptions.HeaderKey.CONTENT_ENCODING, "gzip");
        }
        if (!TextUtils.isEmpty(HttpManager.getInstance(context).getAcceptLanguage())) {
            map.put(ConfigOptions.HeaderKey.ACCEPT_LANGUAGE, HttpManager.getInstance(context).getAcceptLanguage());
        }
        if (!TextUtils.isEmpty(HttpManager.getInstance(context).getWatchTimeZone())) {
            map.put(ConfigOptions.HeaderKey.WATCH_TIME_ZONE, HttpManager.getInstance(context).getWatchTimeZone());
        }
        LogUtil.i(TAG, "generateHeaderMap:" + map);
        return map;
    }

    private static void addHeader(Request request, Map<String, String> map, String name, String value) {
        String tempValue = request.headers().get(name);
        if (tempValue == null) {
            map.put(name, value);
            return;
        }
        LogUtil.i(TAG, "addHeader tempValue:" + tempValue);
    }

    /** Decrypts the IM-transpond response body. */
    public static String decodeHttpResult(Context context, Map<String, String> map, String body, Request request) {
        String eebbkKey = getEebbkKey(HttpManager.getInstance(context).getHttpClient(), request);
        if (isEmptyString(body) || isEmptyString(eebbkKey)) {
            LogUtil.e(TAG, "decodeHttpResult:eebbkKey is null or http response body is nll.");
        } else if (map != null) {
            Headers headers = Headers.of(map);
            String encrypted = headers.get(ConfigOptions.HeaderKey.ENCRYPTED);
            String sign = headers.get(ConfigOptions.HeaderKey.EEBBK_SIGN);
            if (encrypted != null && encrypted.contains(ConfigOptions.HeaderKey.ENCRYPTED)) {
                body = decodeHttpResponseBody(eebbkKey, body, AesKeyCache.getInstance().getKey(sign));
            }
        }
        LogUtil.i(TAG, "decodeHttpResult:" + body);
        return body;
    }

    protected static String generateEncryptBody(String body, String eebbkKey, String aesKey) {
        if (!isEmptyString(eebbkKey)) {
            return AESUtil.encryptToBase64(GzipUtil.compress(body, Constants.UTF_8), aesKey);
        }
        LogUtil.w(TAG, "eebbkKey is null,eebbkKey:" + eebbkKey);
        return body;
    }

    protected static String getRequestBodyString(RequestBody requestBody) {
        if (requestBody == null) {
            return null;
        }
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            LogUtil.e(e);
            return null;
        }
    }

    protected static String buildBaseRequestParamJson(HttpClient httpClient) {
        String timestamp = new SimpleDateFormat(DateFormatUtil.FORMAT_1, Locale.getDefault()).format(new Date());
        NetBaseRequestParam param = new NetBaseRequestParam();
        param.setImFlag("1");
        param.setAppId("2");
        param.setProgram("watch");
        DeviceInfo deviceInfo = httpClient.getDeviceInfo();
        param.setTimestamp(timestamp);
        param.setDeviceId(deviceInfo.getBindNumber());
        param.setToken(deviceInfo.getChipId());
        param.setMac(deviceInfo.getMacAddr());
        param.setAccountId(httpClient.getWatchId());
        param.setRegistId(Long.valueOf(httpClient.getRegistId()));
        param.setRequestId(UUIDUtil.randomUUID());
        return JSONUtil.toJSON(param);
    }

    protected static String getEebbkKey(HttpClient httpClient, Request request) {
        return getEebbkKeyAndUseSelf(httpClient, request).getEebbkKey();
    }

    protected static EncryptData getEebbkKeyAndUseSelf(HttpClient httpClient, Request request) {
        AppInfo appInfo = httpClient.getAppInfo();
        String eebbkKey = null;
        if (appInfo == null) {
            LogUtil.w(TAG, "getEebbkKey AppInfo is null");
            return new EncryptData(null, null, 0);
        }
        int rsaEncryptType = appInfo.getRsaEncryptType();
        if (rsaEncryptType > 1 && isRequestNotUseSelfKey(request)) {
            rsaEncryptType = 1;
        }
        String aesKey = appInfo.getAesKey();
        if (rsaEncryptType == 3) {
            eebbkKey = appInfo.getEncryptEebbkKey();
        } else if (rsaEncryptType != 0) {
            String publicKey = appInfo.getPublicKey(rsaEncryptType);
            if (TextUtils.isEmpty(publicKey)) {
                publicKey = XtcSecurity.getDefaultRsaKey();
            }
            if (!isEmptyString(publicKey) && (eebbkKey = RSAUtil.encryptWithPublicKey(aesKey, publicKey)) == null) {
                LogUtil.e(TAG, "pubEncrypt error: publicKey = [" + publicKey + "]");
                HttpAuthManager.getInstance().checkAuth(HttpAuthManager.AUTH_ERROR_CODE, 2);
            }
        }
        return new EncryptData(eebbkKey, aesKey, rsaEncryptType);
    }

    /** @return true when the request must keep its own RSA key. */
    public static boolean isRequestNotUseSelfKey(Request request) {
        Invocation invocation;
        if (request == null || (invocation = request.tag(Invocation.class)) == null) {
            return false;
        }
        boolean notUseSelfKey = invocation.method().getAnnotation(KeepHead.class) != null;
        if (notUseSelfKey) {
            LogUtil.d(TAG, "NotUseSelfKey url = [" + request.url() + "]");
        }
        return notUseSelfKey;
    }

    private static boolean isEmptyString(CharSequence value) {
        return value == null || value.length() == 0;
    }

    protected static String generateEncryptRequestParam(String param, String aesKey) {
        return AESUtil.encryptToBase64(param, aesKey);
    }

    protected static String generateSign(String aesKey, String url, String baseRequestParamJson, String body) {
        return generateSignWithBodyBytes(aesKey, url, baseRequestParamJson,
                (body == null || "".equals(body)) ? null : body.getBytes(Charset.forName(Constants.UTF_8)));
    }

    protected static String generateSignWithBodyBytes(String aesKey, String url, String baseRequestParamJson,
            byte[] bodyBytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Charset charset = Charset.forName(Constants.UTF_8);
        byte[] urlBytes = url.getBytes(charset);
        outputStream.write(urlBytes, 0, urlBytes.length);
        byte[] paramBytes = baseRequestParamJson.getBytes(charset);
        outputStream.write(paramBytes, 0, paramBytes.length);
        if (bodyBytes != null && bodyBytes.length > 0) {
            outputStream.write(bodyBytes, 0, bodyBytes.length);
        }
        byte[] aesBytes = aesKey.getBytes(charset);
        outputStream.write(aesBytes, 0, aesBytes.length);
        try {
            outputStream.close();
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return MD5Util.md5(outputStream.toByteArray());
    }

    protected static String decodeHttpResponseBody(String eebbkKey, String body, String aesKey) {
        return isEmptyString(eebbkKey) ? body : decodeAndUncompressBodyBase64(body, aesKey);
    }

    private static String decodeAndUncompressBodyBase64(String body, String aesKey) {
        if (!isEmptyString(body)) {
            body = body.replace("\"", "");
        }
        byte[] decrypted = null;
        try {
            decrypted = AESUtil.decryptFromBase64ToBytes(body, aesKey);
        } catch (Throwable t) {
            LogUtil.d(TAG, "decryptAESToByte error: ", t);
        }
        if (!TextUtils.isEmpty(body) && decrypted == null) {
            LogUtil.e(TAG, "decryptAESToByte result is null");
        }
        byte[] unzipped = GzipUtil.decompress(decrypted);
        return unzipped != null ? new String(unzipped) : body;
    }
}
package com.xtc.qiniu;

import android.text.TextUtils;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.config.NetBuildConfig;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 把文件上传到自建好友圈服务端。
 *
 * <p>原厂流程是「服务端发七牛 token → 七牛 SDK 直传七牛」。自建服务端没有七牛，
 * 因此当 {@link NetBuildConfig#OVERRIDE_BASE_URL} 打开时，改用这里的实现：把文件
 * POST 到 {@code /moment/file/upload}，由服务端落到自己的 media 目录。</p>
 *
 * <p>上传接口不经过应用那套签名拦截器（会带上 Base-Request-Param），而是直接用
 * 干净的 OkHttp，用服务端下发的 uploadToken 作为凭据。</p>
 */
public final class SelfHostedUploader {

    private static final String TAG = "SelfHostedUploader";
    private static final String UPLOAD_PATH = "/moment/file/upload";
    private static final String HEADER_KEY = "Upload-Key";
    private static final String HEADER_TOKEN = "Upload-Token";

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private SelfHostedUploader() {
    }

    /** @return true when uploads should go to our own server. */
    public static boolean isEnabled() {
        return NetBuildConfig.OVERRIDE_BASE_URL;
    }

    /**
     * Token for an upload whose key we chose ourselves.
     *
     * <p>Normally the server hands out the token, but a few callers generate the
     * key locally and never ask for one; the server accepts this shape as long
     * as it matches the key.</p>
     */
    public static String tokenFor(String key) {
        return "local:" + key;
    }

    /**
     * Uploads {@code filePath} under {@code key}.
     *
     * <p>Runs asynchronously and reports through {@code listener}, matching the
     * contract of {@link ICloudManager.OnUpLoadListener}.</p>
     */
    public static void upload(final String filePath, final String key, final String token,
            final ICloudManager.OnUpLoadListener listener) {
        if (TextUtils.isEmpty(filePath)) {
            fail(listener, key, Constants.ErrorCode.PATH_EMPTY, "filePath is empty");
            return;
        }
        if (TextUtils.isEmpty(key)) {
            fail(listener, key, Constants.ErrorCode.KEY_EMPTY, "key is empty");
            return;
        }
        if (TextUtils.isEmpty(token)) {
            fail(listener, key, Constants.ErrorCode.TOKEN_EMPTY, "token is empty");
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            fail(listener, key, Constants.ErrorCode.FILE_NOT_EXIT, "file not exists: " + filePath);
            return;
        }
        String baseUrl = BaseUrlManager.getSelfHostedBaseUrl();
        if (TextUtils.isEmpty(baseUrl)) {
            fail(listener, key, Constants.ErrorCode.QINIU_NOT_RESPONSE, "self-hosted base url is not set");
            return;
        }
        String url = baseUrl + UPLOAD_PATH.substring(1);
        Request request = new Request.Builder()
                .url(url)
                .header(HEADER_KEY, key)
                .header(HEADER_TOKEN, token)
                .post(RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();
        LogUtil.i(TAG, "upload url = " + url + ", key = " + key + ", size = " + file.length());
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "upload failed: " + e);
                fail(listener, key, Constants.ErrorCode.QINIU_NOT_RESPONSE, String.valueOf(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.code() != 200) {
                        LogUtil.e(TAG, "upload http " + response.code());
                        fail(listener, key, Constants.ErrorCode.QINIU_NOT_RESPONSE,
                                "http " + response.code());
                        return;
                    }
                    LogUtil.i(TAG, "upload success: " + key);
                    if (listener != null) {
                        listener.onProgress(key, 100.0d);
                        listener.onSuccess(key);
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    private static void fail(ICloudManager.OnUpLoadListener listener, String key, int code, String message) {
        LogUtil.e(TAG, "upload fail key = " + key + ", code = " + code + ", " + message);
        if (listener != null) {
            listener.onFailure(key, code, message);
        }
    }
}

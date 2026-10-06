package com.xtc.httplib.okhttp;

import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.annotation.ImBridge;
import com.xtc.im.transpond.ITranspondCallback;
import com.xtc.im.transpond.TranspondManager;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Invocation;

/** Bridges requests annotated with {@link ImBridge} over the IM channel. */
public class ImBridgeInterceptor implements Interceptor {

    private static final String TAG = "ImBridgeInterceptor";
    private static final long MAX_BODY_SIZE = 10240;
    private static final long TIMEOUT_SECOND = 10;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        if (!isNeedImBridge(chain)) {
            return chain.proceed(chain.request());
        }
        LogUtil.i(TAG, "bridge http request: " + chain.request().url());
        return bridgeHttp(chain);
    }

    private boolean isNeedImBridge(Interceptor.Chain chain) {
        Invocation invocation = chain.request().tag(Invocation.class);
        return invocation != null && invocation.method().getAnnotation(ImBridge.class) != null;
    }

    private Response bridgeHttp(Interceptor.Chain chain) throws IOException {
        final Request request = chain.request();
        String url = request.url().toString();
        Integer code = MethodCode.getCode(request.method());
        if (code == null) {
            throw new IOException("Nonsupport bridge http method" + request.method());
        }
        byte[] headers = generateHeaders(request.headers());
        byte[] body = generateBody(request.body());
        if (body.length > MAX_BODY_SIZE) {
            throw new IOException("body is larger than 10Kb");
        }
        final AtomicReference<Response> responseRef = new AtomicReference<>();
        final AtomicReference<IOException> errorRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        if (!TranspondManager.transpond(url, code, headers, body, new ITranspondCallback.Stub() {
            @Override
            public void onSuccess(byte[] responseBody, int responseCode) {
                responseRef.set(ImBridgeInterceptor.this.generateResponse(request, responseBody));
                latch.countDown();
            }

            @Override
            public void onError(String message) {
                errorRef.set(new IOException(message));
                latch.countDown();
            }
        })) {
            throw new IOException("Bridge http failure");
        }
        try {
            if (!latch.await(TIMEOUT_SECOND, TimeUnit.SECONDS)) {
                LogUtil.i(TAG, "await timeout");
            }
        } catch (InterruptedException ignored) {
            // interrupted while waiting for the bridge response
        }
        if (responseRef.get() != null) {
            return responseRef.get();
        }
        if (errorRef.get() != null) {
            throw errorRef.get();
        }
        throw new IOException("timeout");
    }

    private byte[] generateHeaders(Headers headers) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            map.put(headers.name(i), headers.value(i));
        }
        String json = JSONUtil.toJSON(map);
        return json == null ? new byte[0] : json.getBytes(StandardCharsets.US_ASCII);
    }

    private byte[] generateBody(RequestBody requestBody) throws IOException {
        if (requestBody == null) {
            return new byte[0];
        }
        Buffer buffer = new Buffer();
        try {
            requestBody.writeTo(buffer);
            return buffer.readByteArray();
        } finally {
            buffer.close();
        }
    }

    private Response generateResponse(Request request, byte[] body) {
        Response.Builder builder = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(Integer.parseInt("200"))
                .message("")
                .body(ResponseBody.create(MediaType.parse(ConfigOptions.HeaderKey.MEDIA_TYPE), body))
                .sentRequestAtMillis(-1L)
                .receivedResponseAtMillis(System.currentTimeMillis());
        if (isEncrypted(request)) {
            builder.header(ConfigOptions.HeaderKey.ENCRYPTED, ConfigOptions.HeaderKey.ENCRYPTED);
        }
        return builder.build();
    }

    private boolean isEncrypted(Request request) {
        return ConfigOptions.HeaderKey.ENCRYPTED.equals(request.header(ConfigOptions.HeaderKey.ENCRYPTED));
    }

    /** IM transpond method codes. */
    private enum MethodCode {
        GET("GET", 1),
        POST("POST", 2),
        DELETE("DELETE", 3),
        PUT("PUT", 4);

        private final String method;
        private final int code;

        MethodCode(String method, int code) {
            this.method = method;
            this.code = code;
        }

        public static Integer getCode(String method) {
            for (MethodCode methodCode : values()) {
                if (methodCode.method.equals(method)) {
                    return methodCode.code;
                }
            }
            return null;
        }
    }
}
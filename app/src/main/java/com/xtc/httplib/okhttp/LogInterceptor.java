package com.xtc.httplib.okhttp;

import android.os.SystemClock;

import com.xtc.httplib.HttpClient;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.annotation.HideResponseLog;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.constant.HttpRequestEvent;
import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.log.LogUtil;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import retrofit2.Invocation;

/** Logs the request/response pair and records the timing used by the big-data monitor. */
public class LogInterceptor implements Interceptor {

    public static final String TAG = LogTag.tag("LogInterceptor");

    private static final String BODY_ELLIPSIS = "…";
    private static final int MAX_PRINT_BODY_LENGTH = 1000;
    private static final String MEDIA_SUBTYPE_JSON = "json";
    private static final String MEDIA_TYPE_APPLICATION = "application";

    private static final ThreadLocal<StringBuilder> STRING_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    private HttpClient httpClient;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        long startTime = SystemClock.elapsedRealtime();
        StringBuilder builder = STRING_BUILDER.get();
        builder.setLength(0);
        Request request = chain.request();
        boolean hideResponseLog = isRequestHideResponseLog(request);
        try {
            HttpRequestEvent event = new HttpRequestEvent();
            String requestId = UUID.randomUUID().toString();
            event.setId(requestId);
            String url = request.url().toString();
            event.setUrl(url);
            event.setPkgName(this.httpClient.getPackageName());
            event.setAppVer(this.httpClient.getVersionName());
            String host = NetStateDataManager.UNKNOWN;
            try {
                host = URI.create(url).getHost();
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
            event.setHost(host);
            event.setDnsProvider(String.valueOf(0));
            event.setDnsResult(String.valueOf(true));
            event.setDnsCostTime(String.valueOf(0));
            BigdataClientManager.getInstance().putDnsReqRecord(requestId, event);
            builder.append("--> ");
            Request loggedRequest = buildRequestMessage(request, builder, requestId);
            builder.append('\n');
            long requestNanoTime = System.nanoTime();
            event.setInterceptorLogTime(String.valueOf(SystemClock.elapsedRealtime() - startTime));
            Response response = buildResponseMessage(chain.proceed(loggedRequest),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - requestNanoTime), builder, hideResponseLog);
            try {
                long totalCostTime = SystemClock.elapsedRealtime() - startTime;
                if (BigdataClientManager.getInstance().supportPlatform()
                        && BigdataClientManager.getInstance().querySwitchOpen() && response != null) {
                    event.setTotalCostTime(String.valueOf(totalCostTime));
                    BigdataClientManager.getInstance().collectRawData(event, request, response.code(),
                            getReqLength(request), getRespLength(response), response.message());
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
            LogUtil.i(TAG, builder.toString());
            return response;
        } catch (Throwable t) {
            buildExceptionMessage(t, builder);
            if (t instanceof IOException) {
                LogUtil.i(TAG, builder.toString());
                throw (IOException) t;
            }
            LogUtil.i(TAG, builder.toString());
            throw new IOException(t);
        }
    }

    private String getReqLength(Request request) {
        long contentLength;
        RequestBody body = request.body();
        if (body == null) {
            return String.valueOf(0);
        }
        try {
            contentLength = body.contentLength();
        } catch (Exception ignored) {
            contentLength = 0;
        }
        return String.valueOf(contentLength >= 0 ? contentLength : 0L);
    }

    private String getRespLength(Response response) {
        ResponseBody body = response.body();
        if (body == null) {
            return String.valueOf(0);
        }
        return String.valueOf(body.contentLength());
    }

    /** @return true when the response body must not be printed. */
    public static boolean isRequestHideResponseLog(Request request) {
        Invocation invocation;
        return request != null
                && (invocation = request.tag(Invocation.class)) != null
                && invocation.method().getAnnotation(HideResponseLog.class) != null;
    }

    /** Appends the request line/headers/body to {@code builder} and returns the logged request. */
    public Request buildRequestMessage(Request request, StringBuilder builder, String requestId) throws IOException {
        Request.Builder requestBuilder = request.newBuilder()
                .url(request.url())
                .method(request.method(), request.body())
                .headers(request.headers())
                .cacheControl(request.cacheControl());
        if (requestId != null) {
            requestBuilder.addHeader("uuid", requestId);
        }
        Request loggedRequest = requestBuilder.build();
        builder.append(loggedRequest.method());
        builder.append('(');
        builder.append(loggedRequest.url());
        builder.append(')');
        builder.append('\n');
        Headers headers = loggedRequest.headers();
        for (int i = 0; i < headers.size(); i++) {
            builder.append(headers.name(i));
            builder.append(':');
            builder.append(headers.value(i));
            builder.append('\n');
        }
        RequestBody body = loggedRequest.body();
        if (body == null) {
            return loggedRequest;
        }
        if (!isPrintable(body.contentType())) {
            builder.append(String.format(Locale.ROOT, "[%s, %.2fKb]", body.contentType(),
                    Float.valueOf(body.contentLength() / 1024.0f)));
            return loggedRequest;
        }
        Utf8RequestBody utf8RequestBody = new Utf8RequestBody(body);
        Request result = loggedRequest.newBuilder().method(request.method(), utf8RequestBody).build();
        builder.append(utf8RequestBody.getContent());
        return result;
    }

    /** Appends the response line/body to {@code builder} and returns the logged response. */
    public Response buildResponseMessage(Response response, long costMillis, StringBuilder builder, boolean hideResponseLog)
            throws IOException {
        builder.append("<-- ");
        builder.append(response.code());
        builder.append('-');
        builder.append(response.message());
        builder.append('(');
        builder.append(costMillis);
        builder.append("ms)\n");
        ResponseBody body = response.body();
        if (body == null) {
            builder.append("[null]");
            return response;
        }
        if (!isPrintable(body.contentType())) {
            builder.append(String.format(Locale.ROOT, "[%s, %.2fKb]", body.contentType(),
                    Float.valueOf(body.contentLength() / 1024.0f)));
            return response;
        }
        Utf8ResponseBody utf8ResponseBody = new Utf8ResponseBody(body);
        Response result = response.newBuilder().body(utf8ResponseBody).build();
        if (!hideResponseLog) {
            builder.append(utf8ResponseBody.getContent());
        }
        return result;
    }

    private void buildExceptionMessage(Throwable throwable, StringBuilder builder) {
        builder.append("<-- ");
        builder.append(throwable);
    }

    private boolean isPrintable(MediaType mediaType) {
        return mediaType != null
                && MEDIA_TYPE_APPLICATION.equals(mediaType.type())
                && MEDIA_SUBTYPE_JSON.equals(mediaType.subtype());
    }

    /** Records the DNS lookup result on every pending request of the same host. */
    public void onDnsResult(final String host, final boolean success, final int provider, final long costMillis) {
        LogUtil.d(TAG, "onDnsResult() called with: host = [" + host + "], success = [" + success
                + "], dnsProvider = [" + provider + "], costMills = [" + costMillis + "]");
        BigdataClientManager.getInstance().getAllDnsReqRecords().entrySet().iterator()
                .forEachRemaining(new Consumer<Map.Entry<String, HttpRequestEvent>>() {
                    @Override
                    public void accept(Map.Entry<String, HttpRequestEvent> entry) {
                        HttpRequestEvent value = entry.getValue();
                        if (host.equals(value.getHost())) {
                            LogUtil.d(LogInterceptor.TAG, "onDnsResult() called with: reqHost = [" + host + "]");
                            value.setDnsResult(String.valueOf(success));
                            value.setDnsCostTime(String.valueOf(costMillis));
                            value.setDnsProvider(String.valueOf(provider));
                        }
                    }
                });
    }

    /** Binds the client used to read package name/version for the log entries. */
    public void assign(DefaultOkHttpClient client) {
        this.httpClient = client;
    }

    /** Request body wrapper that keeps the payload in memory so it can be printed. */
    private static class Utf8RequestBody extends RequestBody {

        private final ByteString content;
        private final RequestBody origin;

        Utf8RequestBody(RequestBody origin) throws IOException {
            this.origin = origin;
            Buffer buffer = new Buffer();
            origin.writeTo(buffer);
            this.content = buffer.readByteString();
        }

        @Override
        public MediaType contentType() {
            return this.origin.contentType();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            sink.write(this.content);
        }

        @Override
        public long contentLength() {
            return this.content.size();
        }

        public String getContent() {
            if (this.content.size() > MAX_PRINT_BODY_LENGTH) {
                return this.content.substring(0, MAX_PRINT_BODY_LENGTH).string(StandardCharsets.UTF_8)
                        .concat(BODY_ELLIPSIS);
            }
            return this.content.string(StandardCharsets.UTF_8);
        }
    }

    /** Response body wrapper that keeps the payload in memory so it can be printed. */
    private static class Utf8ResponseBody extends ResponseBody {

        private final ByteString content;
        private final ResponseBody origin;

        Utf8ResponseBody(ResponseBody origin) throws IOException {
            this.origin = origin;
            Buffer buffer = new Buffer();
            origin.source().readAll(buffer);
            this.content = buffer.readByteString();
        }

        @Override
        public MediaType contentType() {
            return this.origin.contentType();
        }

        @Override
        public long contentLength() {
            return this.content.size();
        }

        @Override
        public BufferedSource source() {
            return new Buffer().write(this.content);
        }

        @Override
        public void close() {
            this.origin.close();
        }

        public String getContent() {
            if (this.content.size() > MAX_PRINT_BODY_LENGTH) {
                return this.content.substring(0, MAX_PRINT_BODY_LENGTH).string(StandardCharsets.UTF_8)
                        .concat(BODY_ELLIPSIS);
            }
            return this.content.string(StandardCharsets.UTF_8);
        }
    }
}
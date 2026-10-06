package com.xtc.httplib.okhttp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Process;

import com.google.protobuf.ExtensionRegistryLite;
import com.xtc.dns.api.DnsStrategy;
import com.xtc.httplib.HttpClient;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.bigdata.BigdataClientManager;
import com.xtc.httplib.convert.XtcGsonConverterFactory;
import com.xtc.httplib.rxadapter.XtcRxJavaCallAdapterFactory;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.protobuf.ProtoConverterFactory;
import rx.schedulers.Schedulers;

/** Default {@link HttpClient} implementation wiring the okhttp interceptor chain. */
public class DefaultOkHttpClient extends HttpClient {

    private static final String TAG = LogTag.tag("DefaultOkHttpClient");

    /** Shared log interceptor, also used to log synthetic error responses. */
    public static final LogInterceptor LOG_INTERCEPTOR = new LogInterceptor();
    public static int CONNECT_TIMEOUT = 15000;
    public static int WRITE_TIMEOUT = 15000;
    public static int READ_TIMEOUT = 15000;

    private DnsStrategy dnsStrategy;
    private Executor httpExecutor;
    private OkHttpClient okHttpClient;
    private OkHttpClient okHttpClientThird;
    private OkHttpClient webOkHttpClient;
    private OkHttpClient webOkHttpClientThird;
    private final Map<String, Retrofit> urlToRetrofitCache;
    String packageName;
    int versionCode;
    String versionName;

    public DefaultOkHttpClient(Context context) {
        super(context);
        this.packageName = "unknown";
        this.versionCode = -1;
        this.versionName = "unknown";
        this.urlToRetrofitCache = new HashMap<>();
        initDns();
        initOkHttpClient();
        initOkHttpClientThird();
        initExecutor();
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public int getVersionCode() {
        return this.versionCode;
    }

    @Override
    public String getVersionName() {
        return this.versionName;
    }

    private void initDns() {
        this.dnsStrategy = new DnsStrategy(this.context);
        if (BigdataClientManager.getInstance().supportPlatform()) {
            this.dnsStrategy.setDnsCallback(new DnsStrategy.DnsCallback() {
                @Override
                public void onDnsResult(String host, boolean success, int provider, long costMillis) {
                    DefaultOkHttpClient.LOG_INTERCEPTOR.onDnsResult(host, success, provider, costMillis);
                }
            });
        }
    }

    private void initOkHttpClient() {
        this.okHttpClient = createOkHttpClientBuilder().build();
    }

    private void initWebOkHttpClient() {
        this.webOkHttpClient = createOkHttpClientBuilder().build();
    }

    private OkHttpClient.Builder createOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        initAppVersion(this.context);
        builder.dns(this.dnsStrategy);
        builder.eventListenerFactory(this.dnsStrategy);
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.addInterceptor(LOG_INTERCEPTOR);
        LOG_INTERCEPTOR.assign(this);
        builder.addInterceptor(new ShutdownLocationInterceptor());
        builder.addInterceptor(new TimeoutInterceptor());
        builder.addInterceptor(new MonitorInterceptor(this.context));
        builder.addInterceptor(new PreprocessorInterceptor(this.context));
        builder.addInterceptor(new HttpRequestInterceptor(this.context, this));
        builder.addInterceptor(new ConfUpdateInterceptor(this.context));
        if ("com.xtc.i3launcher".equals(this.context.getPackageName())) {
            builder.addInterceptor(new IMRefreshDomainInterceptor(this.context));
        }
        builder.addInterceptor(new HttpResponseInterceptor(this.context, this));
        builder.addInterceptor(new ImBridgeInterceptor());
        builder.addInterceptor(new RetryInterceptor(this.context));
        return builder;
    }

    private void initAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.versionCode = packageInfo.versionCode;
            this.packageName = packageInfo.packageName;
            this.versionName = packageInfo.versionName;
        } catch (Exception e) {
            LogUtil.e(TAG, "get app version error = " + e);
        }
    }

    private void initOkHttpClientThird() {
        this.okHttpClientThird = createOkHttpClientThirdBuilder().build();
    }

    private void initWebOkHttpClientThird() {
        this.webOkHttpClientThird = createOkHttpClientThirdBuilder().build();
    }

    private OkHttpClient.Builder createOkHttpClientThirdBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dns(this.dnsStrategy);
        builder.eventListenerFactory(this.dnsStrategy);
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.addInterceptor(LOG_INTERCEPTOR);
        builder.addInterceptor(new MonitorInterceptor(this.context));
        builder.addInterceptor(new RetryInterceptor(this.context));
        return builder;
    }

    private void initExecutor() {
        this.httpExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(10);
                        runnable.run();
                    }
                }, "http-thread");
            }
        });
    }

    @Override
    public Call newCall(Request request) {
        return this.okHttpClient.newCall(request);
    }

    private Retrofit getOrCreateRetrofit(String baseUrl, OkHttpClient okHttpClient, boolean async) {
        Retrofit retrofit;
        synchronized (this.urlToRetrofitCache) {
            retrofit = this.urlToRetrofitCache.get(baseUrl + async);
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .addCallAdapterFactory(getCallAdapterFactory(async))
                        .addConverterFactory(ProtoConverterFactory.createWithRegistry(ExtensionRegistryLite.getEmptyRegistry()))
                        .addConverterFactory(XtcGsonConverterFactory.create(JSONUtil.newGsonBuilder().create()))
                        .baseUrl(baseUrl)
                        .client(okHttpClient)
                        .build();
                this.urlToRetrofitCache.put(baseUrl + async, retrofit);
            }
        }
        return retrofit;
    }

    private Retrofit createRetrofit(String baseUrl, OkHttpClient okHttpClient, boolean async) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(getCallAdapterFactory(async))
                .addConverterFactory(GsonConverterFactory.create(JSONUtil.newGsonBuilder().create()))
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
    }

    private CallAdapter.Factory getCallAdapterFactory(boolean async) {
        if (async) {
            return XtcRxJavaCallAdapterFactory.create();
        }
        return XtcRxJavaCallAdapterFactory.createWithScheduler(Schedulers.from(this.httpExecutor));
    }

    @Override
    public <T> T request(String baseUrl, Class<T> service) {
        LogUtil.i(TAG, "request baseUrl = " + baseUrl);
        return getOrCreateRetrofit(baseUrl, this.okHttpClient, false).create(service);
    }

    @Override
    public <T> T requestSync(String baseUrl, Class<T> service) {
        LogUtil.i(TAG, "requestSync baseUrl = " + baseUrl);
        return getOrCreateRetrofit(baseUrl, this.okHttpClient, true).create(service);
    }

    @Override
    public <T> T requestThird(String baseUrl, Class<T> service) {
        LogUtil.i(TAG, "requestThird baseUrl = " + baseUrl);
        return getOrCreateRetrofit(baseUrl, this.okHttpClientThird, false).create(service);
    }

    @Override
    public <T> T requestThirdSync(String baseUrl, Class<T> service) {
        LogUtil.i(TAG, "requestThirdSync baseUrl = " + baseUrl);
        return getOrCreateRetrofit(baseUrl, this.okHttpClientThird, true).create(service);
    }

    @Override
    public void addHeadToOkHttpClient(final Map<String, String> headers) {
        OkHttpClient.Builder builder = this.okHttpClient.newBuilder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request.Builder requestBuilder = chain.request().newBuilder();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder = requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
                return chain.proceed(requestBuilder.build());
            }
        });
        this.okHttpClient = builder.build();
    }

    public synchronized OkHttpClient getWebOkHttpClient() {
        if (this.webOkHttpClient == null) {
            initWebOkHttpClient();
        }
        return this.webOkHttpClient;
    }

    public synchronized OkHttpClient getWebOkHttpClientThird() {
        if (this.webOkHttpClientThird == null) {
            initWebOkHttpClientThird();
        }
        return this.webOkHttpClientThird;
    }
}
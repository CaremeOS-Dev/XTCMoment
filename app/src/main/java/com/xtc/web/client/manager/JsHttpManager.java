package com.xtc.web.client.manager;

import android.content.Context;

import com.xtc.httplib.HttpManager;
import com.xtc.httplib.okhttp.DefaultOkHttpClient;
import com.xtc.httplib.okhttp.OnGetAppInfoListener;
import com.xtc.log.LogUtil;
import com.xtc.system.account.AppInfoImpl;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.data.request.ReqJsHttp;
import com.xtc.web.client.data.response.RespJsHttp;
import com.xtc.web.core.callback.CompletionHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** 代理 H5 发起的原生 http 请求（内网/外网两套 OkHttpClient）。 */
public class JsHttpManager {

    private static final String TAG = Constants.TAG + JsHttpManager.class.getSimpleName();

    interface Type {
        String INNER = "1";
        String OUTER = "2";
    }

    /** 发起请求并把响应体回调给 H5。 */
    public void request(Context context, ReqJsHttp reqJsHttp, final CompletionHandler<RespJsHttp> completionHandler) {
        OkHttpClient okHttpClient = createOkHttpClient(context, reqJsHttp.getType());
        Request request = createRequest(reqJsHttp);
        if (okHttpClient == null || request == null) {
            RespJsHttp response = new RespJsHttp();
            response.setCode(RespJsHttp.Code.NOT_SUPPORT);
            response.setDesc("getLocalImage not support!");
            completionHandler.complete(response);
            return;
        }
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                RespJsHttp response = new RespJsHttp();
                response.setCode(RespJsHttp.Code.OTHER_ERROR);
                response.setDesc(e.getMessage());
                completionHandler.complete(response);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                RespJsHttp result = new RespJsHttp();
                ResponseBody responseBody = response.body();
                if (response.isSuccessful()) {
                    LogUtil.i(TAG, "response is success");
                    result.setData(responseBody != null ? responseBody.string() : "");
                    result.setCode(RespJsHttp.Code.SUCCESS);
                    result.setDesc("success");
                } else {
                    result.setCode(String.valueOf(response.code()));
                    result.setDesc("server or native httpLib error!");
                    LogUtil.i(TAG, "response is fail" + response.code());
                }
                LogUtil.d(TAG, "complete onResponse: " + result + "; ");
                completionHandler.complete(result);
            }
        });
    }

    /** 按 type 选择内网或外网 OkHttpClient。 */
    private OkHttpClient createOkHttpClient(final Context context, String type) {
        HttpManager.getInstance(context).setOnGetAppInfoListener(new OnGetAppInfoListener() {
            @Override
            public com.xtc.httplib.bean.AppInfo getAppInfo() {
                AppInfoImpl appInfoImpl = AppInfoImpl.getDefaultInstance(context);
                com.xtc.httplib.bean.AppInfo appInfo = new com.xtc.httplib.bean.AppInfo();
                appInfo.setEncSwitch(appInfoImpl.getEncSwitch());
                appInfo.setGrey(appInfoImpl.getGrey());
                appInfo.setRsaPublicKey(appInfoImpl.getRsaPublicKey());
                appInfo.setVersion(appInfoImpl.getVersion());
                return appInfo;
            }
        });
        DefaultOkHttpClient defaultOkHttpClient = (DefaultOkHttpClient) HttpManager.getInstance(context).getHttpClient();
        if (Type.INNER.equals(type)) {
            return defaultOkHttpClient.getWebOkHttpClient();
        }
        if (Type.OUTER.equals(type)) {
            return defaultOkHttpClient.getWebOkHttpClientThird();
        }
        LogUtil.e(TAG, "not support getLocalImage type!");
        return null;
    }

    /** 根据请求方法构造 OkHttp 的 Request。 */
    private Request createRequest(ReqJsHttp reqJsHttp) {
        Headers.Builder headerBuilder;
        HashMap<String, String> header = reqJsHttp.getHeader();
        if (header != null) {
            headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : header.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        } else {
            headerBuilder = null;
        }
        if ("GET".equalsIgnoreCase(reqJsHttp.getMethod())) {
            Request.Builder builder = new Request.Builder().url(reqJsHttp.getUrl());
            if (headerBuilder != null) {
                builder.headers(headerBuilder.build());
            }
            return builder.build();
        }
        if ("POST".equalsIgnoreCase(reqJsHttp.getMethod())) {
            String body = reqJsHttp.getBody() != null ? JSONUtil.toJSON(reqJsHttp.getBody()) : "";
            Request.Builder builder = new Request.Builder().url(reqJsHttp.getUrl())
                    .post(RequestBody.create(MediaType.parse("application/json"), body));
            if (headerBuilder != null) {
                builder.headers(headerBuilder.build());
            }
            return builder.build();
        }
        LogUtil.e(TAG, "not support getLocalImage method!");
        return null;
    }
}
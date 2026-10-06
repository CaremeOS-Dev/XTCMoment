package com.xtc.httplib.okhttp;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.auth.HttpTokenExpireManager;
import com.xtc.httplib.confupdate.ConfUpdatesInfoManager;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/** Applies the {@code Conf-Updates} / {@code Token-Refresh} response headers. */
public class ConfUpdateInterceptor extends BaseInterceptor {

    public static final String HEADER_CONF_UPDATES = "Conf-Updates";
    public static final String HEADER_CONF_UPDATES_VERSION = "Conf-Updates-V";
    public static final String HEADER_TOKEN_REFRESH = "Token-Refresh";
    public static final String REFRESH = "1";

    private static final String TAG = LogTag.tag("ConfUpdateInterceptor");

    ConfUpdateInterceptor(Context context) {
        super(context);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response != null) {
            dealConfigurationUpdate(response);
        }
        return response;
    }

    private void dealConfigurationUpdate(final Response response) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                checkTokenExpire(response);
                String confUpdates = response.header(HEADER_CONF_UPDATES);
                String confUpdatesVersion = response.header(HEADER_CONF_UPDATES_VERSION);
                if (TextUtils.isEmpty(confUpdates) || TextUtils.isEmpty(confUpdatesVersion)) {
                    return;
                }
                ConfUpdatesInfoManager.getInstance(ConfUpdateInterceptor.this.context)
                        .updateConf(confUpdates, confUpdatesVersion);
                ConfUpdatesInfoManager.getInstance(ConfUpdateInterceptor.this.context).dispathUpdatesToClient();
            }
        });
    }

    private void checkTokenExpire(Response response) {
        if (REFRESH.equals(response.header(HEADER_TOKEN_REFRESH))) {
            LogUtil.d(TAG, "checkTokenExpire: Token-Refresh");
            HttpTokenExpireManager.getInstance().checkTokenExpire(false);
        }
    }
}
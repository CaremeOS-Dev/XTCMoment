package com.xtc.httplib.net;

import android.content.Context;

import com.xtc.domain.Domain;
import com.xtc.domain.DomainManager;
import com.xtc.httplib.ConfigOptions;
import com.xtc.httplib.LogTag;
import com.xtc.moment.config.NetBuildConfig;
import com.xtc.moment.module.Constants;

/** Builds the base url of each service. */
public class BaseUrlManager {

    private static final String TAG = LogTag.tag("BaseUrlManager");
    public static String PROTOCOL_TYPE = ConfigOptions.ProtocolType.HTTPS;

    private BaseUrlManager() {
    }

    public static String getH5Url(Context context) {
        return getProtocolUrl(context, Domain.H5);
    }

    public static String getDefaultUrl(Context context) {
        return getProtocolUrl(context, Domain.WATCH);
    }

    public static String getSportUrl(Context context) {
        return getProtocolUrl(context, Domain.SPORT);
    }

    public static String getPointsUrl(Context context) {
        return getProtocolUrl(context, Domain.TCX_POINTS);
    }

    public static String getLocationUrl(Context context) {
        return getProtocolUrl(context, Domain.LOCATION);
    }

    public static String getActivityUrl(Context context) {
        return getProtocolUrl(context, Domain.TCX_ACTIVITY);
    }

    public static String getThirdPartyUrl(Context context) {
        return getProtocolUrl(context, Domain.THIRD);
    }

    public static String getChatUrl(Context context) {
        return getProtocolUrl(context, Domain.CHAT);
    }

    public static String getWeatherUrl(Context context) {
        return getProtocolUrl(context, Domain.WEATHER);
    }

    public static String getStoreUrl(Context context) {
        return getProtocolUrl(context, Domain.TCX_STORE);
    }

    public static String getGameUrl(Context context) {
        return getProtocolUrl(context, Domain.TCX_GAME);
    }

    public static String getThemeUrl(Context context) {
        return getProtocolUrl(context, Domain.THEME);
    }

    public static String getGatewayUrl(Context context) {
        return getProtocolUrl(context, Domain.GATEWAY);
    }

    public static String getTcxGatewayUrl(Context context) {
        return getProtocolUrl(context, Domain.TCX_GATEWAY);
    }

    public static String getMomentUrl(Context context) {
        return getProtocolUrl(context, Domain.MOMENT);
    }

    public static String getRouteUrl(Context context) {
        return getProtocolUrl(context, Domain.ROUTE);
    }

    public static String getSensorServerUrl(Context context) {
        return getProtocolUrl(context, Domain.SENSOR_SERVICE);
    }

    /** {@code protocol + domain + "/"} for the given service. */
    public static String getProtocolUrl(Context context, Domain domain) {
        if (NetBuildConfig.OVERRIDE_BASE_URL) {
            // 编译期指定了 baseUrl：协议与主机都取自它，忽略线上域名配置。
            return NetBuildConfig.SCHEME + NetBuildConfig.HOST
                    + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER;
        }
        return PROTOCOL_TYPE + DomainManager.getInstance(context).getDomain(domain.getName())
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER;
    }

    public static boolean isHttps() {
        if (NetBuildConfig.OVERRIDE_BASE_URL) {
            return ConfigOptions.ProtocolType.HTTPS.equals(NetBuildConfig.SCHEME);
        }
        return PROTOCOL_TYPE.equals(ConfigOptions.ProtocolType.HTTPS);
    }

    /**
     * 编译期指定的自建服务端根地址，形如 {@code http://127.0.0.1:8000/}。
     *
     * @return 未指定 baseUrl 时为 null。
     */
    public static String getSelfHostedBaseUrl() {
        if (!NetBuildConfig.OVERRIDE_BASE_URL) {
            return null;
        }
        return NetBuildConfig.SCHEME + NetBuildConfig.HOST
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER;
    }
}

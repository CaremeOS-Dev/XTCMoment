package com.xtc.domain;

/** Known service domains with their CN and South-Asia hosts. */
public enum Domain {
    WATCH("watchDomain", "watch.okii.com", "watch-sa.imoo.com"),
    CHAT("chatDomain", "watch.okii.com", "watch-sa.imoo.com"),
    LOCATION("locationDomain", "location.watch.okii.com", "location-sa.imoo.com"),
    SENSOR_SERVICE("sensorServiceDomain", "report-data-watch.okii.com", "api-sa.imoo.com"),
    POINTS("pointsDomain", "points.okii.com", "points-sa.imoo.com"),
    TCX_POINTS("pointsDomainTcx", "points.tiancaixing.com", "points-sa.imoo.com"),
    ACTIVITY("activityDomain", "act.okii.com", "acttest.okii.com"),
    TCX_ACTIVITY("activityDomainTcx", "act.tiancaixing.com", "acttest.okii.com"),
    SSO("ssoDomain", "account.okii.com", "account-sa.imoo.com"),
    IM_JOIN("imJoinDomain", "gw.im.okii.com:8000", "gw-sa.im.imoo.com:8000"),
    SPORT("sportDomain", "sport.watch.okii.com", "sport-sa.imoo.com"),
    WEATHER("weatherDomain", "sport.watch.okii.com", "sport-sa.imoo.com"),
    H5("h5Domain", "static.watch.okii.com", "static.watch.okii.com"),
    THIRD("thirdDomain", "thirdparty.watch.okii.com", "thirdparty.watch.okii.com"),
    STORE("storeDomain", "store.watch.okii.com", "store-sa.imoo.com"),
    TCX_STORE("storeDomainTcx", "store-watch.tiancaixing.com", "store-sa.imoo.com"),
    IM_BACKUP("imBackupDomainList", "106.75.31.151:1883,106.75.31.151:8000", ""),
    IM_JOIN_GREY("imJoinGreyDomain", "gw.beta.im.okii.com:8000", "gw-sa-beta.im.imoo.com:8000"),
    GAME("gameDomain", "game.watch.okii.com", "game-sa.imoo.com"),
    TCX_GAME("gameDomainTcx", "game-watch.tiancaixing.com", "game-sa.imoo.com"),
    THEME("themeDomain", "theme.watch.okii.com", "theme-sa.imoo.com"),
    EMOJI("emojiDomain", "smartwatch.qiniucdn.com", "smartwatch.qiniucdn.com"),
    MOMENT("momentDomain", "moment.watch.okii.com", "moment-sa.imoo.com"),
    GATEWAY("gatewayDomain", "api.watch.okii.com", "api-sa.imoo.com"),
    TCX_GATEWAY("gatewayDomainTcx", "api-watch.tiancaixing.com", "api-sa.imoo.com"),
    SHOP("shopDomain", "oper.tiancaixing.com", "oper.tiancaixing.com"),
    ROUTE("routeDomain", "route.okii.com", "route-sa.imoo.com"),
    BIGDATA("bigdataDomain", "watchda.eebbk.net", "watchda.eebbk.net"),
    QINIU_PRE_QUERY("qiniuPreQueryDomain", "uc.qbox.me", "kodo-config.qiniuapi.com");

    private final String name;
    private final String cnDomain;
    private final String saDomain;

    Domain(String name, String cnDomain, String saDomain) {
        this.name = name;
        this.cnDomain = cnDomain;
        this.saDomain = saDomain;
    }

    public final String getName() {
        return this.name;
    }

    public final String getCnDomain() {
        return this.cnDomain;
    }

    public final String getSaDomain() {
        return this.saDomain;
    }
}
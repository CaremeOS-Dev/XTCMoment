package com.xtc.system.location.core;

/**
 * 定位请求的数据库、Action 与参数键常量。
 */
public interface LocationRequest {

    /** 定位数据库名。 */
    String DB_NAME = "i3launcher.db";
    /** 请求定位的广播 Action。 */
    String ACTION_REQUEST_LOCATION = "xtc.watch.location.REQUEST_LOCATION";
    /** 定位服务 Action。 */
    String ACTION_LOCATION_SERVICE = "com.xtc.location.action.LocationService";
    /** 定位成功广播 Action。 */
    String ACTION_LOCATION_SUCCESS = "xtc.watch.location.action.LOCATION_SUCCESS";

    /** 请求类型。 */
    interface RequestType {
        String REQUEST_TYPE_LOCATION = "request_type_location";
        String REQUEST_TYPE_LOCATION_ALT = "request_type_location";
        String REQUEST_TYPE_SERVICE = "request_type_service";
    }

    /** 定位结果参数键。 */
    interface ResultParams {
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String TYPE = "type";
        String CITY = "city";
        String RADIUS = "radius";
        String CREATE_TIME = "createTime";
        String PROVINCE = "province";
        String REGION = "region";
        String IN_CN = "inCn";
        String ROAD = "road";
        String STREET = "street";
        String POI = "poi";
        String DESC = "desc";
        String SCHOOLS = "schools";
    }

    /** 服务请求参数键。 */
    interface ServiceParams {
        String REQUEST_TYPE = "request_type";
        String LOCATION_TYPE = "location_type";
        String WATCH_ID = "watch_id";
        String UID = "uid";
        String EXTRA = "extra";
    }
}
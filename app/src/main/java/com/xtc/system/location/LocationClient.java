package com.xtc.system.location;

import android.content.Context;
import android.content.Intent;

import com.xtc.system.location.core.LocationRequest;

import java.util.Objects;

/**
 * 定位请求客户端，通过启动 launcher 的定位服务发起定位。
 */
public class LocationClient {

    /** 发起定位请求。 */
    public static boolean requestLocation(Context context, String watchId, int locationType) {
        Objects.requireNonNull(context, "context 不能为空");
        Objects.requireNonNull(watchId, "watchId 不能为空");
        Intent intent = new Intent();
        intent.putExtra(LocationRequest.ServiceParams.REQUEST_TYPE, LocationRequest.RequestType.REQUEST_TYPE_LOCATION);
        intent.putExtra(LocationRequest.ServiceParams.LOCATION_TYPE, locationType);
        intent.putExtra(LocationRequest.ServiceParams.WATCH_ID, watchId);
        intent.setAction(LocationRequest.ACTION_LOCATION_SERVICE);
        intent.setPackage("com.xtc.i3launcher");
        return context.startService(intent) != null;
    }
}
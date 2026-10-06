package com.xtc.system.location;

import android.content.Context;
import android.content.Intent;

import com.xtc.system.location.core.LocationRequest;

import java.util.Objects;

/** 定位服务客户端：向 i3launcher 的定位服务发起一次定位请求。 */
public class LocationClient {

    private static final String LAUNCHER_PACKAGE = "com.xtc.i3launcher";

    /** 请求定位，返回是否成功下发请求。 */
    public static boolean requestLocation(Context context, String watchId, int locationType) {
        Objects.requireNonNull(context, "context 不能为空");
        Objects.requireNonNull(watchId, "watchId 不能为空");
        Intent intent = new Intent();
        intent.putExtra(LocationRequest.ServiceParams.REQUEST_TYPE, LocationRequest.RequestType.LOCATION);
        intent.putExtra(LocationRequest.ServiceParams.LOCATION_TYPE, locationType);
        intent.putExtra(LocationRequest.ServiceParams.WATCH_ID, watchId);
        intent.setAction(LocationRequest.ACTION_LOCATION_SERVICE);
        intent.setPackage(LAUNCHER_PACKAGE);
        return context.startService(intent) != null;
    }
}
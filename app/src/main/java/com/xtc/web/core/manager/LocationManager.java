package com.xtc.web.core.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.system.location.LocationClient;
import com.xtc.system.location.core.LocationRequest;
import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.LocationInfo;
import com.xtc.web.core.data.resp.RespLocationInfo;

/** 定位管理器：触发系统定位并监听定位结果广播。 */
public class LocationManager {

    private static final String TAG = CoreConstants.TAG + LocationManager.class.getSimpleName();
    private static final long LOCATION_TIMEOUT = 20000L;
    private static final int LOCATION_TYPE = 41;
    private static LocationManager instance;

    private Context context;
    private boolean locateSuccessful;
    private LocationInfo location;
    private LocationReceiver locationReceiver;
    private CompletionHandler<RespLocationInfo> mCompletionHandler;

    public LocationManager(Context context) {
        this.context = context;
    }

    public static synchronized LocationManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocationManager(context);
        }
        return instance;
    }

    /** 请求定位，20 秒未返回结果则回调失败。 */
    public synchronized void getLocation(CompletionHandler<RespLocationInfo> completionHandler) {
        String watchId = WatchAccountBase.getAccountWatchId(this.context);
        this.mCompletionHandler = completionHandler;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showLocationError();
            }
        }, LOCATION_TIMEOUT);
        triggerLocation(watchId);
    }

    private void showLocationError() {
        if (this.locateSuccessful) {
            return;
        }
        RespLocationInfo response = new RespLocationInfo();
        response.setCode(RespLocationInfo.Code.FAIL);
        this.mCompletionHandler.complete(response);
        unRegisterLocationReceiver();
    }

    private void showLocationSuccess() {
        RespLocationInfo response = new RespLocationInfo();
        response.setCode(RespLocationInfo.Code.SUCCESS);
        response.setData(this.location);
        this.mCompletionHandler.complete(response);
        unRegisterLocationReceiver();
    }

    /** 触发一次系统定位。 */
    public void triggerLocation(String watchId) {
        LogUtil.d(TAG, "触发定位");
        if (LocationClient.requestLocation(this.context, watchId, LOCATION_TYPE)) {
            LogUtil.i(TAG, "triggerLocation successfully.");
        } else {
            LogUtil.w(TAG, "triggerLocation failed");
        }
        registerLocationReceiver();
    }

    /** 定位结果广播接收器。 */
    public class LocationReceiver extends BroadcastReceiver {

        boolean registered;

        void register(Context context) {
            if (this.registered) {
                return;
            }
            this.registered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LocationRequest.ACTION_LOCATION_SUCCESS);
            context.registerReceiver(this, intentFilter);
        }

        void unRegister(Context context) {
            if (this.registered) {
                this.registered = false;
                context.unregisterReceiver(this);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            LogUtil.i(TAG, "receive action on webCore module:" + action);
            if (action.equals(LocationRequest.ACTION_LOCATION_SUCCESS)) {
                location = getLocationFromIntent(intent);
                LogUtil.i(TAG, "receive webcore location info:" + location);
                if (TextUtils.isEmpty(location.getRegion()) && TextUtils.isEmpty(location.getDesc())) {
                    return;
                }
                locateSuccessful = true;
                showLocationSuccess();
            }
        }
    }

    private void registerLocationReceiver() {
        if (this.locationReceiver == null) {
            this.locationReceiver = new LocationReceiver();
        }
        this.locationReceiver.register(this.context);
    }

    private void unRegisterLocationReceiver() {
        LocationReceiver receiver = this.locationReceiver;
        if (receiver != null) {
            receiver.unRegister(this.context);
        }
    }

    private LocationInfo getLocationFromIntent(Intent intent) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLatitude(String.valueOf(intent.getDoubleExtra(
                LocationRequest.ResultParams.LATITUDE, 0.0d)));
        locationInfo.setLongitude(String.valueOf(intent.getDoubleExtra(
                LocationRequest.ResultParams.LONGITUDE, 0.0d)));
        locationInfo.setProvince(intent.getStringExtra(LocationRequest.ResultParams.PROVINCE));
        locationInfo.setCity(intent.getStringExtra(LocationRequest.ResultParams.CITY));
        locationInfo.setRegion(intent.getStringExtra(LocationRequest.ResultParams.REGION));
        locationInfo.setRoad(intent.getStringExtra(LocationRequest.ResultParams.ROAD));
        locationInfo.setStreet(intent.getStringExtra(LocationRequest.ResultParams.STREET));
        locationInfo.setPoi(intent.getStringExtra(LocationRequest.ResultParams.POI));
        locationInfo.setDesc(intent.getStringExtra(LocationRequest.ResultParams.DESC));
        locationInfo.setRadius(intent.getIntExtra(LocationRequest.ResultParams.RADIUS, 0));
        locationInfo.setCreateTime(intent.getLongExtra(LocationRequest.ResultParams.CREATE_TIME, 0L));
        return locationInfo;
    }
}
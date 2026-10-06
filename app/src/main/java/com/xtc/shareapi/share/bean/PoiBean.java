package com.xtc.shareapi.share.bean;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;

/**
 * 位置兴趣点信息，包含名称、行政区、地址与经纬度坐标。
 */
public class PoiBean {

    private static final String TAG = "Share_PoiBean";

    private String name;
    private String province;
    private String city;
    private String address;
    private Location location;
    private int locationType;
    private String addressDesc;
    private String goalPoi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /** 地址描述，为空时回退到名称。 */
    public String getAddressDesc() {
        if (!TextUtils.isEmpty(addressDesc)) {
            return addressDesc;
        }
        return name;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getGoalPoi() {
        return goalPoi;
    }

    public void setGoalPoi(String goalPoi) {
        this.goalPoi = goalPoi;
    }

    /** 校验兴趣点参数。 */
    public BaseResponse checkArgs() {
        ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
        if (TextUtils.isEmpty(name)) {
            Log.d(TAG, "checkArgs fail , poi name is null or empty");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail , poi name is null or empty");
            return response;
        }
        Location location = this.location;
        if (location == null) {
            Log.d(TAG, "checkArgs fail , poi location is null ");
            response.setCode(6);
            response.setErrorDesc("checkArgs fail , poi location is null ");
            return response;
        }
        return location.checkArgs();
    }

    @Override
    public String toString() {
        return "LocationInfo{name='" + name + "', province='" + province + "', city='" + city + "', address='"
                + address + "', location=" + location + ", locationType=" + locationType + ", addressDesc="
                + addressDesc + ", goalPoi='" + goalPoi + "'}";
    }

    /**
     * 经纬度坐标。
     */
    public static class Location {

        private static final String TAG = "Share_Location";

        private String lat;
        private String lng;

        public Location(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        protected Location(Parcel parcel) {
            this.lat = parcel.readString();
            this.lng = parcel.readString();
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        /** 校验经纬度参数。 */
        public BaseResponse checkArgs() {
            ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
            if (TextUtils.isEmpty(lat)) {
                Log.e(TAG, "checkArgs fail , lat is null or empty");
                response.setCode(6);
                response.setErrorDesc("checkArgs fail , lat is null or empty");
            } else if (TextUtils.isEmpty(lng)) {
                Log.e(TAG, "checkArgs fail , lng is null or empty");
                response.setCode(6);
                response.setErrorDesc("checkArgs fail , lng is null or empty");
            } else {
                response.setCode(1);
            }
            return response;
        }

        @Override
        public String toString() {
            return "Location{lat='" + lat + "', lng='" + lng + "'}";
        }
    }
}
package com.xtc.web.core.data.req;

/** H5 分享地理位置并跳转到动态发布页的请求。 */
public class ReqShareJumpMoment {

    private String addressId;
    private String city;
    private String lat;
    private String lng;
    private String poiName;

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressId() {
        return this.addressId;
    }

    public String getPoiName() {
        return this.poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "ReqShareJumpMoment{addressId='" + this.addressId + "', poiName='" + this.poiName + "', city='"
                + this.city + "', lat='" + this.lat + "', lng='" + this.lng + "'}";
    }
}
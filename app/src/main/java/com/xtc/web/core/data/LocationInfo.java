package com.xtc.web.core.data;

/** 定位结果：经纬度、行政区划、POI 与创建时间。 */
public class LocationInfo {

    private String address;
    private String city;
    private long createTime;
    private String desc;
    private String latitude;
    private String longitude;
    private String poi;
    private String province;
    private int radius;
    private String region;
    private String road;
    private String street;

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRoad() {
        return this.road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPoi() {
        return this.poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "LocationInfo{latitude='" + this.latitude + "', longitude='" + this.longitude + "', province='"
                + this.province + "', city='" + this.city + "', region='" + this.region + "', road='" + this.road
                + "', street='" + this.street + "', poi='" + this.poi + "', desc='" + this.desc + "', address='"
                + this.address + "', radius=" + this.radius + ", createTime=" + this.createTime + '}';
    }
}
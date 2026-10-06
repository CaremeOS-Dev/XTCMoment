package com.xtc.moment.net.bean;

import java.util.List;

/** Request body for publishing a moment. */
public class PublishMomentBody {
    private String content;
    private int emotionId;
    private double latitude;
    private String location;
    private String locationTag;
    private int locationType;
    private double longitude;
    private List<String> lookupIds;
    private String packageName;
    private int permissionType;
    private String resource;
    private int resourceId;
    private int type;
    private String typeList;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTypeList() {
        return this.typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getLocationType() {
        return this.locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocationTag() {
        return this.locationTag;
    }

    public void setLocationTag(String locationTag) {
        this.locationTag = locationTag;
    }

    public int getPermissionType() {
        return this.permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
    }

    public List<String> getLookupIds() {
        return this.lookupIds;
    }

    public void setLookupIds(List<String> lookupIds) {
        this.lookupIds = lookupIds;
    }

    @Override
    public String toString() {
        return "PublishMomentBody{watchId='" + this.watchId + "', resourceId=" + this.resourceId + ", resource='" + this.resource + "', content='" + this.content + "', type=" + this.type + ", packageName='" + this.packageName + "', typeList='" + this.typeList + "', location='" + this.location + "', locationType=" + this.locationType + ", longitude=" + this.longitude + ", latitude=" + this.latitude + ", emotionId=" + this.emotionId + ", locationTag='" + this.locationTag + "', permissionType=" + this.permissionType + ", lookupIds=" + this.lookupIds + '}';
    }
}

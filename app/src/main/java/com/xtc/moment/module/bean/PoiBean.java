package com.xtc.moment.module.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.xtc.moment.module.Constants;

/** A point of interest attached to a moment's location. */
public class PoiBean implements Parcelable {

    public static final Parcelable.Creator<PoiBean> CREATOR = new Parcelable.Creator<PoiBean>() {
        @Override
        public PoiBean createFromParcel(Parcel parcel) {
            return new PoiBean(parcel);
        }

        @Override
        public PoiBean[] newArray(int size) {
            return new PoiBean[size];
        }
    };

    @SerializedName("address")
    private String address;

    @SerializedName("addressDesc")
    private String addressDesc;

    @SerializedName("city")
    private String city;

    @SerializedName("detail_info")
    private DetailInfo detailInfo;

    private String goalPoi;

    @SerializedName(Constants.ProviderConstants.MOMENT_ITEM_LOCATION_PATH)
    private Location location;

    @SerializedName("locationType")
    private int locationType;

    @SerializedName("name")
    private String poiName;

    @SerializedName("province")
    private String province;

    public PoiBean() {
    }

    protected PoiBean(Parcel parcel) {
        this.poiName = parcel.readString();
        this.province = parcel.readString();
        this.city = parcel.readString();
        this.address = parcel.readString();
        this.location = (Location) parcel.readParcelable(Location.class.getClassLoader());
        this.addressDesc = parcel.readString();
        this.locationType = parcel.readInt();
        this.goalPoi = parcel.readString();
        this.detailInfo = (DetailInfo) parcel.readParcelable(DetailInfo.class.getClassLoader());
    }

    public String getPoiName() {
        return this.poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
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

    public String getAddressDesc() {
        if (!TextUtils.isEmpty(this.addressDesc)) {
            return this.addressDesc;
        }
        return this.poiName;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public int getLocationType() {
        return this.locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getGoalPoi() {
        return this.goalPoi;
    }

    public void setGoalPoi(String goalPoi) {
        this.goalPoi = goalPoi;
    }

    public DetailInfo getDetail_info() {
        return this.detailInfo;
    }

    public void setDetail_info(DetailInfo detailInfo) {
        this.detailInfo = detailInfo;
    }

    @Override
    public String toString() {
        return "LocationInfo{poiName='" + this.poiName + "', province='" + this.province + "', city='" + this.city + "', address='" + this.address + "', location=" + this.location + ", locationType=" + this.locationType + ", addressDesc=" + this.addressDesc + ", goalPoi='" + this.goalPoi + "'}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.poiName);
        parcel.writeString(this.province);
        parcel.writeString(this.city);
        parcel.writeString(this.address);
        parcel.writeParcelable(this.location, flags);
        parcel.writeString(this.addressDesc);
        parcel.writeInt(this.locationType);
        parcel.writeString(this.goalPoi);
        parcel.writeParcelable(this.detailInfo, flags);
    }

    /** Latitude/longitude pair. */
    public static class Location implements Parcelable {

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel parcel) {
                return new Location(parcel);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };

        @SerializedName("lat")
        private String latitude;

        @SerializedName("lng")
        private String longitude;

        public Location(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        protected Location(Parcel parcel) {
            this.latitude = parcel.readString();
            this.longitude = parcel.readString();
        }

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

        @Override
        public String toString() {
            return "Location{goalLatitude='" + this.latitude + "', goalLongitude='" + this.longitude + "'}";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeString(this.latitude);
            parcel.writeString(this.longitude);
        }
    }

    /** Distance and tag for a POI. */
    public static class DetailInfo implements Parcelable {

        public static final Parcelable.Creator<DetailInfo> CREATOR = new Parcelable.Creator<DetailInfo>() {
            @Override
            public DetailInfo createFromParcel(Parcel parcel) {
                return new DetailInfo(parcel);
            }

            @Override
            public DetailInfo[] newArray(int size) {
                return new DetailInfo[size];
            }
        };

        @SerializedName("distance")
        private int distance;

        @SerializedName("tag")
        private String tag;

        public DetailInfo(int distance, String tag) {
            this.distance = distance;
            this.tag = tag;
        }

        protected DetailInfo(Parcel parcel) {
            this.distance = parcel.readInt();
            this.tag = parcel.readString();
        }

        public int getDistance() {
            return this.distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getTag() {
            return this.tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        @Override
        public String toString() {
            return "DetailInfo{distance=" + this.distance + ", tag='" + this.tag + "'}";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(this.distance);
            parcel.writeString(this.tag);
        }
    }
}

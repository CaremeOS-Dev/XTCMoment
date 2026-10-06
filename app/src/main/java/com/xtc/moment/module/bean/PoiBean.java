package com.xtc.moment.module.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.xtc.moment.module.Constants;
import com.xtc.system.account.bean.AppInfoBase;

/** Point of interest attached to a published moment. */
public class PoiBean implements Parcelable {

    public static final Parcelable.Creator<PoiBean> CREATOR = new Parcelable.Creator<PoiBean>() {
        @Override
        public PoiBean createFromParcel(Parcel source) {
            return new PoiBean(source);
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

    protected PoiBean(Parcel source) {
        this.poiName = source.readString();
        this.province = source.readString();
        this.city = source.readString();
        this.address = source.readString();
        this.location = source.readParcelable(Location.class.getClassLoader());
        this.addressDesc = source.readString();
        this.locationType = source.readInt();
        this.goalPoi = source.readString();
        this.detailInfo = source.readParcelable(DetailInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.poiName);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.address);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.addressDesc);
        dest.writeInt(this.locationType);
        dest.writeString(this.goalPoi);
        dest.writeParcelable(this.detailInfo, flags);
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

    /** @return the address description, falling back to the poi name. */
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
        return "LocationInfo{poiName='" + this.poiName + "', province='" + this.province + "', city='" + this.city
                + "', address='" + this.address + "', location=" + this.location + ", locationType=" + this.locationType
                + ", addressDesc=" + this.addressDesc + ", goalPoi='" + this.goalPoi + "'}";
    }

    /** Latitude/longitude of the poi. */
    public static class Location implements Parcelable {

        public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel source) {
                return new Location(source);
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

        protected Location(Parcel source) {
            this.latitude = source.readString();
            this.longitude = source.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.latitude);
            dest.writeString(this.longitude);
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
    }

    /** Additional poi attributes reported by the map service. */
    public static class DetailInfo implements Parcelable {

        public static final Parcelable.Creator<DetailInfo> CREATOR = new Parcelable.Creator<DetailInfo>() {
            @Override
            public DetailInfo createFromParcel(Parcel source) {
                return new DetailInfo(source);
            }

            @Override
            public DetailInfo[] newArray(int size) {
                return new DetailInfo[size];
            }
        };

        @SerializedName("distance")
        private int distance;

        @SerializedName(AppInfoBase.KEY_TAG)
        private String tag;

        public DetailInfo(int distance, String tag) {
            this.distance = distance;
            this.tag = tag;
        }

        protected DetailInfo(Parcel source) {
            this.distance = source.readInt();
            this.tag = source.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.distance);
            dest.writeString(this.tag);
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
    }
}
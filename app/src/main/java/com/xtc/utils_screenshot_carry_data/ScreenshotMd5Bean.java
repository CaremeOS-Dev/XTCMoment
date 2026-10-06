package com.xtc.utils_screenshot_carry_data;

import android.os.Parcel;
import android.os.Parcelable;

/** Watch id + md5 pair stored in the EXIF {@code Make} tag. */
public class ScreenshotMd5Bean implements Parcelable {

    public static final Parcelable.Creator<ScreenshotMd5Bean> CREATOR = new Parcelable.Creator<ScreenshotMd5Bean>() {
        @Override
        public ScreenshotMd5Bean createFromParcel(Parcel parcel) {
            return new ScreenshotMd5Bean(parcel);
        }

        @Override
        public ScreenshotMd5Bean[] newArray(int size) {
            return new ScreenshotMd5Bean[size];
        }
    };

    private String watchId;
    private String md5;

    @Override
    public int describeContents() {
        return 0;
    }

    public ScreenshotMd5Bean() {
    }

    public ScreenshotMd5Bean(String watchId, String md5) {
        this.watchId = watchId;
        this.md5 = md5;
    }

    protected ScreenshotMd5Bean(Parcel parcel) {
        this.watchId = parcel.readString();
        this.md5 = parcel.readString();
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getMd5() {
        return this.md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.watchId);
        parcel.writeString(this.md5);
    }
}
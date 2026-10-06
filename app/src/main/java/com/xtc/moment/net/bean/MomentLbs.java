package com.xtc.moment.net.bean;

import android.os.Parcel;
import android.os.Parcelable;

/** LBS metadata attached to a moment (star rating, recommendation, visited). */
public class MomentLbs implements Parcelable {

    public static final Parcelable.Creator<MomentLbs> CREATOR = new Parcelable.Creator<MomentLbs>() {
        @Override
        public MomentLbs createFromParcel(Parcel parcel) {
            return new MomentLbs(parcel);
        }

        @Override
        public MomentLbs[] newArray(int size) {
            return new MomentLbs[size];
        }
    };

    private int alreadyBeen;
    private int recommend;
    private int star;
    private int state;

    public MomentLbs(int state, int star, int recommend, int alreadyBeen) {
        this.state = state;
        this.star = star;
        this.recommend = recommend;
        this.alreadyBeen = alreadyBeen;
    }

    protected MomentLbs(Parcel parcel) {
        this.state = parcel.readInt();
        this.star = parcel.readInt();
        this.recommend = parcel.readInt();
        this.alreadyBeen = parcel.readInt();
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getRecommend() {
        return this.recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getAlreadyBeen() {
        return this.alreadyBeen;
    }

    public void setAlreadyBeen(int alreadyBeen) {
        this.alreadyBeen = alreadyBeen;
    }

    public boolean hasEvaluation() {
        return this.state == 1;
    }

    public boolean hasAlready() {
        return this.alreadyBeen == 1;
    }

    @Override
    public String toString() {
        return "MomentLbs{state=" + this.state + ", star=" + this.star + ", recommend=" + this.recommend + ", alreadyBeen=" + this.alreadyBeen + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.state);
        parcel.writeInt(this.star);
        parcel.writeInt(this.recommend);
        parcel.writeInt(this.alreadyBeen);
    }
}

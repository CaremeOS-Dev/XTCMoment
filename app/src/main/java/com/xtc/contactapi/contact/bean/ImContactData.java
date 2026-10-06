package com.xtc.contactapi.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * IM 联系人数据，描述联系人的号码、角色与视频通话能力等。
 */
public class ImContactData implements Parcelable {

    private Integer action;
    private String id;
    private String mobileNumber;
    private Integer type;
    private String salutation;
    private Integer status;
    private String mobileId;
    private String numberId;
    private String shortNumber;
    private Integer bHide;
    private String isFrequent;
    private String customIcon;
    private Integer autoCall;
    private Integer role;
    private Integer sn;
    private Integer supportVideoChat;
    private String geniusNumber;
    private String openID;

    public ImContactData() {
    }

    protected ImContactData(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.action = null;
        } else {
            this.action = parcel.readInt();
        }
        this.id = parcel.readString();
        this.mobileNumber = parcel.readString();
        if (parcel.readByte() == 0) {
            this.type = null;
        } else {
            this.type = parcel.readInt();
        }
        this.salutation = parcel.readString();
        if (parcel.readByte() == 0) {
            this.status = null;
        } else {
            this.status = parcel.readInt();
        }
        this.mobileId = parcel.readString();
        this.numberId = parcel.readString();
        this.shortNumber = parcel.readString();
        if (parcel.readByte() == 0) {
            this.bHide = null;
        } else {
            this.bHide = parcel.readInt();
        }
        this.isFrequent = parcel.readString();
        this.customIcon = parcel.readString();
        if (parcel.readByte() == 0) {
            this.autoCall = null;
        } else {
            this.autoCall = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.role = null;
        } else {
            this.role = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.sn = null;
        } else {
            this.sn = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.supportVideoChat = null;
        } else {
            this.supportVideoChat = parcel.readInt();
        }
        this.geniusNumber = parcel.readString();
        this.openID = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        if (this.action == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.action.intValue());
        }
        parcel.writeString(this.id);
        parcel.writeString(this.mobileNumber);
        if (this.type == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.type.intValue());
        }
        parcel.writeString(this.salutation);
        if (this.status == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.status.intValue());
        }
        parcel.writeString(this.mobileId);
        parcel.writeString(this.numberId);
        parcel.writeString(this.shortNumber);
        if (this.bHide == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.bHide.intValue());
        }
        parcel.writeString(this.isFrequent);
        parcel.writeString(this.customIcon);
        if (this.autoCall == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.autoCall.intValue());
        }
        if (this.role == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.role.intValue());
        }
        if (this.sn == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.sn.intValue());
        }
        if (this.supportVideoChat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.supportVideoChat.intValue());
        }
        parcel.writeString(this.geniusNumber);
        parcel.writeString(this.openID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getNumberId() {
        return numberId;
    }

    public void setNumberId(String numberId) {
        this.numberId = numberId;
    }

    public String getShortNumber() {
        return shortNumber;
    }

    public void setShortNumber(String shortNumber) {
        this.shortNumber = shortNumber;
    }

    public Integer getbHide() {
        return bHide;
    }

    public void setbHide(Integer bHide) {
        this.bHide = bHide;
    }

    public String getIsFrequent() {
        return isFrequent;
    }

    public void setIsFrequent(String isFrequent) {
        this.isFrequent = isFrequent;
    }

    public String getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(String customIcon) {
        this.customIcon = customIcon;
    }

    public Integer getAutoCall() {
        return autoCall;
    }

    public void setAutoCall(Integer autoCall) {
        this.autoCall = autoCall;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public Integer getSupportVideoChat() {
        return supportVideoChat;
    }

    public void setSupportVideoChat(Integer supportVideoChat) {
        this.supportVideoChat = supportVideoChat;
    }

    public String getGeniusNumber() {
        return geniusNumber;
    }

    public void setGeniusNumber(String geniusNumber) {
        this.geniusNumber = geniusNumber;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    @Override
    public String toString() {
        return "ImContactData{action=" + action + ", id='" + id + "', mobileNumber='" + mobileNumber + "', type="
                + type + ", salutation='" + salutation + "', status=" + status + ", mobileId='" + mobileId
                + "', numberId='" + numberId + "', shortNumber='" + shortNumber + "', bHide=" + bHide
                + ", isFrequent='" + isFrequent + "', customIcon='" + customIcon + "', autoCall=" + autoCall
                + ", role=" + role + ", sn=" + sn + ", supportVideoChat=" + supportVideoChat + ", geniusNumber="
                + geniusNumber + ", openID=" + openID + '}';
    }

    public static final Parcelable.Creator<ImContactData> CREATOR = new Parcelable.Creator<ImContactData>() {
        @Override
        public ImContactData createFromParcel(Parcel parcel) {
            return new ImContactData(parcel);
        }

        @Override
        public ImContactData[] newArray(int size) {
            return new ImContactData[size];
        }
    };
}
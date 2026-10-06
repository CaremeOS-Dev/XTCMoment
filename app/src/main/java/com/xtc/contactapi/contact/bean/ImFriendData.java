package com.xtc.contactapi.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * IM 好友数据，描述好友的设备、固件与视频/表情能力等。
 */
public class ImFriendData implements Parcelable {

    private Integer action;
    private String id;
    private String dialogId;
    private String watchId;
    private String friendId;
    private String friendName;
    private String friendModel;
    private String friendFirmware;
    private String phoneNum;
    private Integer bHide;
    private String bindNumber;
    private String icon;
    private String customIcon;
    private String isFrequent;
    private Integer sn;
    private Integer supportVideoChat;
    private String imDialogId;
    private long imFriendId;
    private int expressionVersion;
    private int supportExpression;
    private String supportGroupChat;
    private String geniusNumber;
    private String openID;

    public ImFriendData() {
    }

    protected ImFriendData(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.action = null;
        } else {
            this.action = parcel.readInt();
        }
        this.id = parcel.readString();
        this.dialogId = parcel.readString();
        this.watchId = parcel.readString();
        this.friendId = parcel.readString();
        this.friendName = parcel.readString();
        this.friendModel = parcel.readString();
        this.friendFirmware = parcel.readString();
        this.phoneNum = parcel.readString();
        if (parcel.readByte() == 0) {
            this.bHide = null;
        } else {
            this.bHide = parcel.readInt();
        }
        this.bindNumber = parcel.readString();
        this.icon = parcel.readString();
        this.customIcon = parcel.readString();
        this.isFrequent = parcel.readString();
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
        this.imDialogId = parcel.readString();
        this.imFriendId = parcel.readLong();
        this.expressionVersion = parcel.readInt();
        this.supportExpression = parcel.readInt();
        this.supportGroupChat = parcel.readString();
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
        parcel.writeString(this.dialogId);
        parcel.writeString(this.watchId);
        parcel.writeString(this.friendId);
        parcel.writeString(this.friendName);
        parcel.writeString(this.friendModel);
        parcel.writeString(this.friendFirmware);
        parcel.writeString(this.phoneNum);
        if (this.bHide == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.bHide.intValue());
        }
        parcel.writeString(this.bindNumber);
        parcel.writeString(this.icon);
        parcel.writeString(this.customIcon);
        parcel.writeString(this.isFrequent);
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
        parcel.writeString(this.imDialogId);
        parcel.writeLong(this.imFriendId);
        parcel.writeInt(this.expressionVersion);
        parcel.writeInt(this.supportExpression);
        parcel.writeString(this.supportGroupChat);
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

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getFriendModel() {
        return friendModel;
    }

    public void setFriendModel(String friendModel) {
        this.friendModel = friendModel;
    }

    public String getFriendFirmware() {
        return friendFirmware;
    }

    public void setFriendFirmware(String friendFirmware) {
        this.friendFirmware = friendFirmware;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getbHide() {
        return bHide;
    }

    public void setbHide(Integer bHide) {
        this.bHide = bHide;
    }

    public String getBindNumber() {
        return bindNumber;
    }

    public void setBindNumber(String bindNumber) {
        this.bindNumber = bindNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(String customIcon) {
        this.customIcon = customIcon;
    }

    public String getIsFrequent() {
        return isFrequent;
    }

    public void setIsFrequent(String isFrequent) {
        this.isFrequent = isFrequent;
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

    public String getImDialogId() {
        return imDialogId;
    }

    public void setImDialogId(String imDialogId) {
        this.imDialogId = imDialogId;
    }

    public long getImFriendId() {
        return imFriendId;
    }

    public void setImFriendId(long imFriendId) {
        this.imFriendId = imFriendId;
    }

    public int getExpressionVersion() {
        return expressionVersion;
    }

    public void setExpressionVersion(int expressionVersion) {
        this.expressionVersion = expressionVersion;
    }

    public int getSupportExpression() {
        return supportExpression;
    }

    public void setSupportExpression(int supportExpression) {
        this.supportExpression = supportExpression;
    }

    public String getSupportGroupChat() {
        return supportGroupChat;
    }

    public void setSupportGroupChat(String supportGroupChat) {
        this.supportGroupChat = supportGroupChat;
    }

    @Override
    public String toString() {
        return "ImFriendData{action=" + action + ", id='" + id + "', watchId='" + watchId + "', friendId='"
                + friendId + "', friendName='" + friendName + "', friendModel='" + friendModel + "', friendFirmware='"
                + friendFirmware + "', phoneNum='" + phoneNum + "', bHide=" + bHide + ", bindNumber='" + bindNumber
                + "', icon='" + icon + "', customIcon='" + customIcon + "', isFrequent='" + isFrequent + "', sn=" + sn
                + ", supportVideoChat=" + supportVideoChat + ", geniusNumber='" + geniusNumber + "', openID='" + openID + "'}";
    }

    public static final Parcelable.Creator<ImFriendData> CREATOR = new Parcelable.Creator<ImFriendData>() {
        @Override
        public ImFriendData createFromParcel(Parcel parcel) {
            return new ImFriendData(parcel);
        }

        @Override
        public ImFriendData[] newArray(int size) {
            return new ImFriendData[size];
        }
    };
}
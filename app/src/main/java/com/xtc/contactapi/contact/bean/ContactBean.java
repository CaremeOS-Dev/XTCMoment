package com.xtc.contactapi.contact.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.FieldType;
import com.xtc.moment.module.Constants;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.web.client.manager.BaseInfoManager;

/**
 * 联系人数据，聚合本地联系人、IM 联系人与好友信息。
 */
public class ContactBean implements Parcelable {

    /** 好友手表 id 字段名（与原实现常量一致）。 */
    public static final String FRIEND_WATCH_ID = "friendWatchId";

    @SerializedName(alternate = {"contact_server_id"}, value = "contactServerId")
    private String contactServerId;
    @SerializedName(alternate = {FieldType.FOREIGN_ID_FIELD_SUFFIX}, value = "id")
    private Integer id;
    @SerializedName(alternate = {"mobile_id"}, value = "mobileId")
    private String mobileId;
    private String number;
    @SerializedName(alternate = {"country_code"}, value = "countryCode")
    private String countryCode;
    @SerializedName(alternate = {"is_hide"}, value = "isHide")
    private Integer isHide;
    @SerializedName(alternate = {"number_id"}, value = "numberId")
    private String numberId;
    @SerializedName(alternate = {"friend_watch_id"}, value = FRIEND_WATCH_ID)
    private String friendWatchId;
    private String name;
    @SerializedName(alternate = {"contactType"}, value = "type")
    private Integer type;
    @SerializedName(alternate = {"contact_status"}, value = "status")
    private Integer status;
    @SerializedName(alternate = {"auto_call"}, value = "autoCall")
    private Integer autoCall;
    @SerializedName("customIcon")
    private String customIcon;
    @SerializedName("friendIcon")
    private String friendIcon;
    @SerializedName(alternate = {"friend_bind_number"}, value = "friendBindNumber")
    private String friendBindNumber;
    @SerializedName(alternate = {"friend_model"}, value = "friendModel")
    private String friendModel;
    @SerializedName(alternate = {"friend_firmware"}, value = "friendFirmware")
    private String friendFirmware;
    @SerializedName(alternate = {"photo_path"}, value = Constants.PublishMedia.EXTRA_PHOTO_PATH)
    private String photoPath;
    @SerializedName("lastUpdatedTimestamp")
    private long lastUpdatedTimestamp;
    @SerializedName("isFrequent")
    private String isFrequent;
    @SerializedName(alternate = {"contact_role"}, value = "role")
    private Integer role;
    @SerializedName(alternate = {"remark_friend_name"}, value = "remarkFriendName")
    private Integer remarkFriendName;
    @SerializedName(alternate = {"friend_origin_name"}, value = "friendOriginalName")
    private String friendOriginalName;
    @SerializedName(alternate = {"sort_sn"}, value = "sortSn")
    private Integer sortSn;
    @SerializedName(alternate = {"videochat_misscall_count"}, value = "videoChatMissedCallCount")
    private Integer videoChatMissedCallCount;
    @SerializedName(alternate = {"support_video_chat"}, value = "supportVideoChat")
    private Integer supportVideoChat;
    @SerializedName(BaseInfoManager.Key.GENIUS_NUMBER)
    private String geniusNumber;
    private String openID;
    private Integer missedCallCount;
    private int totalMissCallCount;
    private ImFriendData friendData;
    private ImContactData contactData;
    @SerializedName(alternate = {"dialog_id"}, value = "dialogId")
    private String dialogId;
    @SerializedName(WatchAccountBase.KEY_REAL_NAME)
    private String realName;
    @SerializedName("callAuth")
    private String callAuth;
    @SerializedName("mobileNumber")
    private String mobileNumber;
    @SerializedName("mobileNumberNew")
    private String mobileNumberNew;
    @SerializedName("salutation")
    private String salutation;
    @SerializedName("viewSupportContext")
    private Integer viewSupportContext;
    @SerializedName("parentReview")
    private Integer parentReview;

    public ContactBean() {
    }

    protected ContactBean(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.id = null;
        } else {
            this.id = parcel.readInt();
        }
        this.contactServerId = parcel.readString();
        this.mobileId = parcel.readString();
        this.number = parcel.readString();
        this.countryCode = parcel.readString();
        if (parcel.readByte() == 0) {
            this.isHide = null;
        } else {
            this.isHide = parcel.readInt();
        }
        this.numberId = parcel.readString();
        this.friendWatchId = parcel.readString();
        this.name = parcel.readString();
        if (parcel.readByte() == 0) {
            this.type = null;
        } else {
            this.type = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.status = null;
        } else {
            this.status = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.autoCall = null;
        } else {
            this.autoCall = parcel.readInt();
        }
        this.customIcon = parcel.readString();
        this.friendIcon = parcel.readString();
        this.friendBindNumber = parcel.readString();
        this.friendModel = parcel.readString();
        this.friendFirmware = parcel.readString();
        this.photoPath = parcel.readString();
        this.lastUpdatedTimestamp = parcel.readLong();
        this.isFrequent = parcel.readString();
        if (parcel.readByte() == 0) {
            this.role = null;
        } else {
            this.role = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.remarkFriendName = null;
        } else {
            this.remarkFriendName = parcel.readInt();
        }
        this.friendOriginalName = parcel.readString();
        if (parcel.readByte() == 0) {
            this.sortSn = null;
        } else {
            this.sortSn = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.videoChatMissedCallCount = null;
        } else {
            this.videoChatMissedCallCount = parcel.readInt();
        }
        if (parcel.readByte() == 0) {
            this.supportVideoChat = null;
        } else {
            this.supportVideoChat = parcel.readInt();
        }
        this.geniusNumber = parcel.readString();
        this.openID = parcel.readString();
        if (parcel.readByte() == 0) {
            this.missedCallCount = null;
        } else {
            this.missedCallCount = parcel.readInt();
        }
        this.totalMissCallCount = parcel.readInt();
        this.friendData = parcel.readParcelable(ImFriendData.class.getClassLoader());
        this.contactData = parcel.readParcelable(ImContactData.class.getClassLoader());
        this.dialogId = parcel.readString();
        this.realName = parcel.readString();
        this.callAuth = parcel.readString();
        this.mobileNumber = parcel.readString();
        this.mobileNumberNew = parcel.readString();
        this.salutation = parcel.readString();
        if (parcel.readByte() == 0) {
            this.viewSupportContext = null;
        } else {
            this.viewSupportContext = parcel.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        if (this.id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.id.intValue());
        }
        parcel.writeString(this.contactServerId);
        parcel.writeString(this.mobileId);
        parcel.writeString(this.number);
        parcel.writeString(this.countryCode);
        if (this.isHide == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.isHide.intValue());
        }
        parcel.writeString(this.numberId);
        parcel.writeString(this.friendWatchId);
        parcel.writeString(this.name);
        if (this.type == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.type.intValue());
        }
        if (this.status == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.status.intValue());
        }
        if (this.autoCall == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.autoCall.intValue());
        }
        parcel.writeString(this.customIcon);
        parcel.writeString(this.friendIcon);
        parcel.writeString(this.friendBindNumber);
        parcel.writeString(this.friendModel);
        parcel.writeString(this.friendFirmware);
        parcel.writeString(this.photoPath);
        parcel.writeLong(this.lastUpdatedTimestamp);
        parcel.writeString(this.isFrequent);
        if (this.role == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.role.intValue());
        }
        if (this.remarkFriendName == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.remarkFriendName.intValue());
        }
        parcel.writeString(this.friendOriginalName);
        if (this.sortSn == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.sortSn.intValue());
        }
        if (this.videoChatMissedCallCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.videoChatMissedCallCount.intValue());
        }
        if (this.supportVideoChat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.supportVideoChat.intValue());
        }
        parcel.writeString(this.geniusNumber);
        parcel.writeString(this.openID);
        if (this.missedCallCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.missedCallCount.intValue());
        }
        parcel.writeInt(this.totalMissCallCount);
        parcel.writeParcelable(this.friendData, flags);
        parcel.writeParcelable(this.contactData, flags);
        parcel.writeString(this.dialogId);
        parcel.writeString(this.realName);
        parcel.writeString(this.callAuth);
        parcel.writeString(this.mobileNumber);
        parcel.writeString(this.mobileNumberNew);
        parcel.writeString(this.salutation);
        if (this.viewSupportContext == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(this.viewSupportContext.intValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContactServerId() {
        return contactServerId;
    }

    public void setContactServerId(String contactServerId) {
        this.contactServerId = contactServerId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAutoCall() {
        return autoCall;
    }

    public void setAutoCall(Integer autoCall) {
        this.autoCall = autoCall;
    }

    public Integer getParentReview() {
        return parentReview;
    }

    public void setParentReview(Integer parentReview) {
        this.parentReview = parentReview;
    }

    public String getFriendWatchId() {
        return friendWatchId;
    }

    public void setFriendWatchId(String friendWatchId) {
        this.friendWatchId = friendWatchId;
    }

    public String getCustomIcon() {
        return customIcon;
    }

    public void setCustomIcon(String customIcon) {
        this.customIcon = customIcon;
    }

    public String getFriendIcon() {
        return friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon;
    }

    public String getFriendBindNumber() {
        return friendBindNumber;
    }

    public void setFriendBindNumber(String friendBindNumber) {
        this.friendBindNumber = friendBindNumber;
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Long getLastUpdatedTimestamp() {
        return lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp() {
        this.lastUpdatedTimestamp = System.currentTimeMillis();
    }

    public void setLastUpdatedTimestamp(Long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
        this.lastUpdatedTimestamp = lastUpdatedTimestamp;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public void setIsHide(Integer isHide) {
        this.isHide = isHide;
    }

    public String getIsFrequent() {
        return isFrequent;
    }

    public void setIsFrequent(String isFrequent) {
        this.isFrequent = isFrequent;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getFriendOriginalName() {
        return friendOriginalName;
    }

    public void setFriendOriginalName(String friendOriginalName) {
        this.friendOriginalName = friendOriginalName;
    }

    public Integer getRemarkFriendName() {
        return remarkFriendName;
    }

    public void setRemarkFriendName(Integer remarkFriendName) {
        this.remarkFriendName = remarkFriendName;
    }

    public Integer getSortSn() {
        return sortSn;
    }

    public void setSortSn(Integer sortSn) {
        this.sortSn = sortSn;
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

    public String getOpenId() {
        return openID;
    }

    public void setOpenId(String openId) {
        this.openID = openId;
    }

    public Integer getMissedCallCount() {
        return missedCallCount;
    }

    public void setMissedCallCount(Integer missedCallCount) {
        this.missedCallCount = missedCallCount;
    }

    public Integer getVideoChatMissedCallCount() {
        return videoChatMissedCallCount;
    }

    public void setVideoChatMissedCallCount(int videoChatMissedCallCount) {
        this.videoChatMissedCallCount = videoChatMissedCallCount;
    }

    public void setVideoChatMissedCallCount(Integer videoChatMissedCallCount) {
        this.videoChatMissedCallCount = videoChatMissedCallCount;
    }

    /** 未接来电总数 = 语音未接 + 视频未接。 */
    public int getTotalMissCallCount() {
        Integer voiceMissed = this.missedCallCount;
        Integer videoMissed = this.videoChatMissedCallCount;
        if (voiceMissed != null && videoMissed != null) {
            this.totalMissCallCount = voiceMissed + videoMissed;
        } else if (voiceMissed == null && videoMissed != null) {
            this.totalMissCallCount = videoMissed;
        } else if (voiceMissed != null && videoMissed == null) {
            this.totalMissCallCount = voiceMissed;
        } else {
            this.totalMissCallCount = 0;
        }
        return this.totalMissCallCount;
    }

    public void setTotalMissCallCount(int totalMissCallCount) {
        this.totalMissCallCount = totalMissCallCount;
    }

    public ImFriendData getFriendData() {
        return friendData;
    }

    public void setFriendData(ImFriendData friendData) {
        this.friendData = friendData;
    }

    public ImContactData getContactData() {
        return contactData;
    }

    public void setContactData(ImContactData contactData) {
        this.contactData = contactData;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCallAuth() {
        return callAuth;
    }

    public void setCallAuth(String callAuth) {
        this.callAuth = callAuth;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumberNew() {
        return mobileNumberNew;
    }

    public void setMobileNumberNew(String mobileNumberNew) {
        this.mobileNumberNew = mobileNumberNew;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public Integer getViewSupportContext() {
        return viewSupportContext;
    }

    public void setViewSupportContext(Integer viewSupportContext) {
        this.viewSupportContext = viewSupportContext;
    }

    public String getOpenID() {
        return openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    @Override
    public String toString() {
        return "ContactBean{id=" + id + ", contactServerId='" + contactServerId + "', mobileId='" + mobileId
                + "', number='" + number + "', mobileNumber='" + mobileNumber + "', mobileNumberNew='" + mobileNumberNew
                + "', countryCode='" + countryCode + "', isHide=" + isHide + ", numberId='" + numberId
                + "', friendWatchId='" + friendWatchId + "', name='" + name + "', salutation='" + salutation
                + "', viewSupportContext=" + viewSupportContext + ", type=" + type + ", status=" + status
                + ", autoCall=" + autoCall + ", customIcon='" + customIcon + "', friendIcon='" + friendIcon
                + "', friendBindNumber='" + friendBindNumber + "', friendModel='" + friendModel + "', friendFirmware='"
                + friendFirmware + "', photoPath='" + photoPath + "', lastUpdatedTimestamp=" + lastUpdatedTimestamp
                + ", isFrequent='" + isFrequent + "', role=" + role + ", remarkFriendName=" + remarkFriendName
                + ", friendOriginalName='" + friendOriginalName + "', sortSn=" + sortSn + ", videoChatMissedCallCount="
                + videoChatMissedCallCount + ", supportVideoChat=" + supportVideoChat + ", geniusNumber='" + geniusNumber
                + "', openID='" + openID + "', missedCallCount=" + missedCallCount + ", totalMissCallCount="
                + totalMissCallCount + ", friendData=" + friendData + ", contactData=" + contactData + ", dialogId='"
                + dialogId + "', realName='" + realName + "', callAuth='" + callAuth + "', parentReview=" + parentReview + '}';
    }

    public static final Parcelable.Creator<ContactBean> CREATOR = new Parcelable.Creator<ContactBean>() {
        @Override
        public ContactBean createFromParcel(Parcel parcel) {
            return new ContactBean(parcel);
        }

        @Override
        public ContactBean[] newArray(int size) {
            return new ContactBean[size];
        }
    };
}
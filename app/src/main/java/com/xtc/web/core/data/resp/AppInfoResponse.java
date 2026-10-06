package com.xtc.web.core.data.resp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/** 应用市场返回的应用详情，用于 H5 查询应用状态与下载信息。 */
public class AppInfoResponse implements Parcelable {

    public static final Parcelable.Creator<AppInfoResponse> CREATOR = new Parcelable.Creator<AppInfoResponse>() {
        @Override
        public AppInfoResponse createFromParcel(Parcel source) {
            return new AppInfoResponse(source);
        }

        @Override
        public AppInfoResponse[] newArray(int size) {
            return new AppInfoResponse[size];
        }
    };

    private int appId;
    private String cheatDownloadNumber;
    private int classifyId;
    private int controlType;
    private String developerContactWay;
    private String diffMD5;
    private int diffSize;
    private List<String> diffUrl;
    private int downType;
    private String downloadNumberShow;
    private String firmware;
    private String fullMD5;
    private int fullSize;
    private List<String> fullUrl;
    private String icon;
    private String instruction;
    private int isAllowUpInstall;
    private int isCache;
    private int isInLocalTask;
    private int localAction;
    private String name;
    private int netWorkMode;
    private String packageName;
    private int progress;
    private int pushVersionCode;
    private float score;
    private int scoreNumber;
    private int serviceAction;
    private int size;
    private String sizeShow;
    private int suitableAge;
    private String summary;
    private int systemApp;
    private String thumbnailIcon;
    private long upDate;
    private String upDateShow;
    private String upgradeInfo;
    private String url;
    private int userAppStatus;
    private String userAppStatusShow;
    private int versionCode;
    private int versionId;
    private String versionName;
    private int watchVersionCode;

    @Override
    public int describeContents() {
        return 0;
    }

    public AppInfoResponse() {
    }

    protected AppInfoResponse(Parcel source) {
        this.appId = source.readInt();
        this.packageName = source.readString();
        this.name = source.readString();
        this.versionName = source.readString();
        this.versionCode = source.readInt();
        this.size = source.readInt();
        this.icon = source.readString();
        this.upDate = source.readLong();
        this.upgradeInfo = source.readString();
        this.instruction = source.readString();
        this.summary = source.readString();
        this.thumbnailIcon = source.readString();
        this.userAppStatus = source.readInt();
        this.userAppStatusShow = source.readString();
        this.downloadNumberShow = source.readString();
        this.sizeShow = source.readString();
        this.upDateShow = source.readString();
        this.cheatDownloadNumber = source.readString();
        this.firmware = source.readString();
        this.isAllowUpInstall = source.readInt();
        this.downType = source.readInt();
        this.systemApp = source.readInt();
        this.score = source.readFloat();
        this.scoreNumber = source.readInt();
        this.suitableAge = source.readInt();
        this.developerContactWay = source.readString();
        this.versionId = source.readInt();
        this.classifyId = source.readInt();
        this.controlType = source.readInt();
        this.isCache = source.readInt();
        this.watchVersionCode = source.readInt();
        this.fullUrl = source.readArrayList(List.class.getClassLoader());
        this.diffUrl = source.readArrayList(List.class.getClassLoader());
        this.fullSize = source.readInt();
        this.diffSize = source.readInt();
        this.fullMD5 = source.readString();
        this.diffMD5 = source.readString();
        this.url = source.readString();
        this.progress = source.readInt();
        this.pushVersionCode = source.readInt();
        this.netWorkMode = source.readInt();
        this.localAction = source.readInt();
        this.isInLocalTask = source.readInt();
        this.serviceAction = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appId);
        dest.writeString(this.packageName);
        dest.writeString(this.name);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeInt(this.size);
        dest.writeString(this.icon);
        dest.writeLong(this.upDate);
        dest.writeString(this.upgradeInfo);
        dest.writeString(this.instruction);
        dest.writeString(this.summary);
        dest.writeString(this.thumbnailIcon);
        dest.writeInt(this.userAppStatus);
        dest.writeString(this.userAppStatusShow);
        dest.writeString(this.downloadNumberShow);
        dest.writeString(this.sizeShow);
        dest.writeString(this.upDateShow);
        dest.writeString(this.cheatDownloadNumber);
        dest.writeString(this.firmware);
        dest.writeInt(this.isAllowUpInstall);
        dest.writeInt(this.downType);
        dest.writeInt(this.systemApp);
        dest.writeFloat(this.score);
        dest.writeInt(this.scoreNumber);
        dest.writeInt(this.suitableAge);
        dest.writeString(this.developerContactWay);
        dest.writeInt(this.versionId);
        dest.writeInt(this.classifyId);
        dest.writeInt(this.controlType);
        dest.writeInt(this.isCache);
        dest.writeInt(this.watchVersionCode);
        dest.writeList(this.fullUrl);
        dest.writeList(this.diffUrl);
        dest.writeInt(this.fullSize);
        dest.writeInt(this.diffSize);
        dest.writeString(this.fullMD5);
        dest.writeString(this.diffMD5);
        dest.writeString(this.url);
        dest.writeInt(this.progress);
        dest.writeInt(this.pushVersionCode);
        dest.writeInt(this.netWorkMode);
        dest.writeInt(this.localAction);
        dest.writeInt(this.isInLocalTask);
        dest.writeInt(this.serviceAction);
    }
    public int getAppId() {
        return this.appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getUpDate() {
        return this.upDate;
    }

    public void setUpDate(long upDate) {
        this.upDate = upDate;
    }

    public String getUpgradeInfo() {
        return this.upgradeInfo;
    }

    public void setUpgradeInfo(String upgradeInfo) {
        this.upgradeInfo = upgradeInfo;
    }

    public String getInstruction() {
        return this.instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getThumbnailIcon() {
        return this.thumbnailIcon;
    }

    public void setThumbnailIcon(String thumbnailIcon) {
        this.thumbnailIcon = thumbnailIcon;
    }

    public int getUserAppStatus() {
        return this.userAppStatus;
    }

    public void setUserAppStatus(int userAppStatus) {
        this.userAppStatus = userAppStatus;
    }

    public String getUserAppStatusShow() {
        return this.userAppStatusShow;
    }

    public void setUserAppStatusShow(String userAppStatusShow) {
        this.userAppStatusShow = userAppStatusShow;
    }

    public String getDownloadNumberShow() {
        return this.downloadNumberShow;
    }

    public void setDownloadNumberShow(String downloadNumberShow) {
        this.downloadNumberShow = downloadNumberShow;
    }

    public String getSizeShow() {
        return this.sizeShow;
    }

    public void setSizeShow(String sizeShow) {
        this.sizeShow = sizeShow;
    }

    public String getUpDateShow() {
        return this.upDateShow;
    }

    public void setUpDateShow(String upDateShow) {
        this.upDateShow = upDateShow;
    }

    public String getCheatDownloadNumber() {
        return this.cheatDownloadNumber;
    }

    public void setCheatDownloadNumber(String cheatDownloadNumber) {
        this.cheatDownloadNumber = cheatDownloadNumber;
    }

    public String getFirmware() {
        return this.firmware;
    }

    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }

    public int getIsAllowUpInstall() {
        return this.isAllowUpInstall;
    }

    public void setIsAllowUpInstall(int isAllowUpInstall) {
        this.isAllowUpInstall = isAllowUpInstall;
    }

    public int getDownType() {
        return this.downType;
    }

    public void setDownType(int downType) {
        this.downType = downType;
    }

    public int getSystemApp() {
        return this.systemApp;
    }

    public void setSystemApp(int systemApp) {
        this.systemApp = systemApp;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getScoreNumber() {
        return this.scoreNumber;
    }

    public void setScoreNumber(int scoreNumber) {
        this.scoreNumber = scoreNumber;
    }

    public int getSuitableAge() {
        return this.suitableAge;
    }

    public void setSuitableAge(int suitableAge) {
        this.suitableAge = suitableAge;
    }

    public String getDeveloperContactWay() {
        return this.developerContactWay;
    }

    public void setDeveloperContactWay(String developerContactWay) {
        this.developerContactWay = developerContactWay;
    }

    public int getVersionId() {
        return this.versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getClassifyId() {
        return this.classifyId;
    }

    public void setClassifyId(int classifyId) {
        this.classifyId = classifyId;
    }

    public int getControlType() {
        return this.controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public int getIsCache() {
        return this.isCache;
    }

    public void setIsCache(int isCache) {
        this.isCache = isCache;
    }

    public int getWatchVersionCode() {
        return this.watchVersionCode;
    }

    public void setWatchVersionCode(int watchVersionCode) {
        this.watchVersionCode = watchVersionCode;
    }

    public int getFullSize() {
        return this.fullSize;
    }

    public void setFullSize(int fullSize) {
        this.fullSize = fullSize;
    }

    public int getDiffSize() {
        return this.diffSize;
    }

    public void setDiffSize(int diffSize) {
        this.diffSize = diffSize;
    }

    public String getFullMD5() {
        return this.fullMD5;
    }

    public void setFullMD5(String fullMD5) {
        this.fullMD5 = fullMD5;
    }

    public String getDiffMD5() {
        return this.diffMD5;
    }

    public void setDiffMD5(String diffMD5) {
        this.diffMD5 = diffMD5;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPushVersionCode() {
        return this.pushVersionCode;
    }

    public void setPushVersionCode(int pushVersionCode) {
        this.pushVersionCode = pushVersionCode;
    }

    public int getNetWorkMode() {
        return this.netWorkMode;
    }

    public void setNetWorkMode(int netWorkMode) {
        this.netWorkMode = netWorkMode;
    }

    public int getLocalAction() {
        return this.localAction;
    }

    public void setLocalAction(int localAction) {
        this.localAction = localAction;
    }

    public int getIsInLocalTask() {
        return this.isInLocalTask;
    }

    public void setIsInLocalTask(int isInLocalTask) {
        this.isInLocalTask = isInLocalTask;
    }

    public int getServiceAction() {
        return this.serviceAction;
    }

    public void setServiceAction(int serviceAction) {
        this.serviceAction = serviceAction;
    }

    public List<String> getFullUrl() {
        return this.fullUrl;
    }

    public void setFullUrl(List<String> fullUrl) {
        this.fullUrl = fullUrl;
    }

    public List<String> getDiffUrl() {
        return this.diffUrl;
    }

    public void setDiffUrl(List<String> diffUrl) {
        this.diffUrl = diffUrl;
    }
    @Override
    public String toString() {
        return "AppInfoResponse{appId=" + this.appId + ", packageName='" + this.packageName + "', name='" + this.name
                + "', versionName='" + this.versionName + "', versionCode=" + this.versionCode + ", size=" + this.size
                + ", icon='" + this.icon + "', upDate=" + this.upDate + ", upgradeInfo='" + this.upgradeInfo
                + "', instruction='" + this.instruction + "', summary='" + this.summary + "', thumbnailIcon='"
                + this.thumbnailIcon + "', userAppStatus=" + this.userAppStatus + ", userAppStatusShow='"
                + this.userAppStatusShow + "', downloadNumberShow='" + this.downloadNumberShow + "', sizeShow='"
                + this.sizeShow + "', upDateShow='" + this.upDateShow + "', cheatDownloadNumber='"
                + this.cheatDownloadNumber + "', firmware='" + this.firmware + "', isAllowUpInstall="
                + this.isAllowUpInstall + ", downType=" + this.downType + ", systemApp=" + this.systemApp
                + ", score=" + this.score + ", scoreNumber=" + this.scoreNumber + ", suitableAge=" + this.suitableAge
                + ", developerContactWay='" + this.developerContactWay + "', versionId=" + this.versionId
                + ", classifyId=" + this.classifyId + ", isCache=" + this.isCache + ", controlType=" + this.controlType
                + ", watchVersionCode=" + this.watchVersionCode + ", fullUrl=" + this.fullUrl + ", diffUrl="
                + this.diffUrl + ", fullSize=" + this.fullSize + ", diffSize=" + this.diffSize + ", fullMD5='"
                + this.fullMD5 + "', diffMD5='" + this.diffMD5 + "', url='" + this.url + "', progress="
                + this.progress + ", pushVersionCode=" + this.pushVersionCode + ", netWorkMode=" + this.netWorkMode
                + ", localAction=" + this.localAction + ", isInLocalTask=" + this.isInLocalTask + ", serviceAction="
                + this.serviceAction + '}';
    }
}
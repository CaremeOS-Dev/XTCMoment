package com.xtc.moment.behavior;

import java.util.HashMap;

/**
 * 动态发布全链路耗时埋点实体。
 */
public class DigitalEntity {

    public static final String FACTION_NAME = "moment_publish_item";

    public String clientVersionCode;
    public String momentType;
    public long startPushTime;
    public String shareTransTime;
    public String saveThumnailTime;
    public String compressTime;
    public String nsfwTime;
    public String getTokenTime;
    public String uploadTime;
    public String publicTime;
    public String transformTime;
    public String totalTime;
    public String code;
    public String videoLength;
    public String fileSize;
    public String sendFailReason;
    public String publicStatus;
    public String momentContent;
    public HashMap<String, String> mHashMap = new HashMap<>();

    public HashMap<String, String> getHashMap() {
        this.mHashMap.put("clientVersionCode", this.clientVersionCode);
        this.mHashMap.put("moment_type", this.momentType);
        this.mHashMap.put("CompressTime", this.compressTime);
        this.mHashMap.put("saveThumnailTime", this.saveThumnailTime);
        this.mHashMap.put("getTokenTime", this.getTokenTime);
        this.mHashMap.put("nsfwTime", this.nsfwTime);
        this.mHashMap.put("uploadTime", this.uploadTime);
        this.mHashMap.put("publicTime", this.publicTime);
        this.mHashMap.put("transformTime", this.transformTime);
        this.mHashMap.put("totalTime", this.totalTime);
        this.mHashMap.put("code", this.code);
        this.mHashMap.put("videoLength", this.videoLength);
        this.mHashMap.put("fileSize", this.fileSize);
        this.mHashMap.put("sendFailReason", this.sendFailReason);
        this.mHashMap.put("publicStatus", this.publicStatus);
        this.mHashMap.put("shareTransTime", this.shareTransTime);
        this.mHashMap.put("moment_content", this.momentContent);
        return this.mHashMap;
    }

    public HashMap<String, String> getHashMapNoSet() {
        return this.mHashMap;
    }

    @Override
    public String toString() {
        return "DigitalEntity{clientVersionCode='" + this.clientVersionCode + "', momentType='" + this.momentType
                + "', startPushTime=" + this.startPushTime + ", shareTransTime=" + this.shareTransTime
                + ", saveThumnailTime='" + this.saveThumnailTime + "', CompressTime='" + this.compressTime
                + "', nsfwTime='" + this.nsfwTime + "', getTokenTime='" + this.getTokenTime + "', uploadTime='"
                + this.uploadTime + "', publicTime='" + this.publicTime + "', transformTime='" + this.transformTime
                + "', totalTime='" + this.totalTime + "', code='" + this.code + "', videoLength='" + this.videoLength
                + "', fileSize='" + this.fileSize + "', sendFailReason='" + this.sendFailReason + "', publicStatus='"
                + this.publicStatus + "', mHashMap=" + this.mHashMap + '}';
    }
}
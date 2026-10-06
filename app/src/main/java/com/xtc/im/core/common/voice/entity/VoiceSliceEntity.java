package com.xtc.im.core.common.voice.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.request.Entity;

/** 语音分片：一段语音数据及其序号、是否结束标记。 */
@CommandValue(0)
public class VoiceSliceEntity extends Entity {

    /** 是否最后一片。 */
    public interface IsFin {
        int NO = 1;
        int YES = 2;
    }

    @TagValue(10)
    private String groupId;

    @TagValue(11)
    private int index;

    @TagValue(12)
    private byte[] voc;

    @TagValue(13)
    private int vocTime;

    @TagValue(14)
    private int isFin;

    @TagValue(15)
    private String gdLatitude;

    @TagValue(16)
    private String gdLongitude;

    @TagValue(17)
    private int gdRadius;

    @TagValue(18)
    private String bdLatitude;

    @TagValue(19)
    private String bdLongitude;

    @TagValue(20)
    private int bdRadius;

    @TagValue(21)
    private String ggLatitude;

    @TagValue(22)
    private String ggLongitude;

    @TagValue(23)
    private int ggRadius;

    @TagValue(24)
    private String extra;

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getVoc() {
        return this.voc;
    }

    public void setVoc(byte[] voc) {
        this.voc = voc;
    }

    public int getVocTime() {
        return this.vocTime;
    }

    public void setVocTime(int vocTime) {
        this.vocTime = vocTime;
    }

    public int getIsFin() {
        return this.isFin;
    }

    public void setIsFin(int isFin) {
        this.isFin = isFin;
    }

    public String getGdLatitude() {
        return this.gdLatitude;
    }

    public void setGdLatitude(String gdLatitude) {
        this.gdLatitude = gdLatitude;
    }

    public String getGdLongitude() {
        return this.gdLongitude;
    }

    public void setGdLongitude(String gdLongitude) {
        this.gdLongitude = gdLongitude;
    }

    public int getGdRadius() {
        return this.gdRadius;
    }

    public void setGdRadius(int gdRadius) {
        this.gdRadius = gdRadius;
    }

    public String getBdLatitude() {
        return this.bdLatitude;
    }

    public void setBdLatitude(String bdLatitude) {
        this.bdLatitude = bdLatitude;
    }

    public String getBdLongitude() {
        return this.bdLongitude;
    }

    public void setBdLongitude(String bdLongitude) {
        this.bdLongitude = bdLongitude;
    }

    public int getBdRadius() {
        return this.bdRadius;
    }

    public void setBdRadius(int bdRadius) {
        this.bdRadius = bdRadius;
    }

    public String getGgLatitude() {
        return this.ggLatitude;
    }

    public void setGgLatitude(String ggLatitude) {
        this.ggLatitude = ggLatitude;
    }

    public String getGgLongitude() {
        return this.ggLongitude;
    }

    public void setGgLongitude(String ggLongitude) {
        this.ggLongitude = ggLongitude;
    }

    public int getGgRadius() {
        return this.ggRadius;
    }

    public void setGgRadius(int ggRadius) {
        this.ggRadius = ggRadius;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "VoiceSliceEntity{groupId='" + this.groupId + "'" + ", index=" + this.index + ", vocTime="
                + this.vocTime + ", isFin=" + this.isFin + ", gdLatitude='" + this.gdLatitude + "'" + ", gdLongitude='"
                + this.gdLongitude + "'" + ", gdRadius=" + this.gdRadius + ", bdLatitude='" + this.bdLatitude + "'"
                + ", bdLongitude='" + this.bdLongitude + "'" + ", bdRadius=" + this.bdRadius + ", ggLatitude='"
                + this.ggLatitude + "'" + ", ggLongitude='" + this.ggLongitude + "'" + ", ggRadius=" + this.ggRadius
                + ", extra='" + this.extra + "'" + "}";
    }
}
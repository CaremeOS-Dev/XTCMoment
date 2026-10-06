package com.xtc.virtualselfapi.bean.net.resp;

import java.util.List;

/**
 * 宝箱信息响应。
 */
public class RespBox {

    private int boxStatus;
    private int boxType;
    private List<RespDanger> danger;
    private String id;
    private long openTime;
    private int ornamentId;
    private int protectTime;
    private int robEnable;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBoxType() {
        return this.boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }

    public long getOpenTime() {
        return this.openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public int getBoxStatus() {
        return this.boxStatus;
    }

    public void setBoxStatus(int boxStatus) {
        this.boxStatus = boxStatus;
    }

    public int getOrnamentId() {
        return this.ornamentId;
    }

    public void setOrnamentId(int ornamentId) {
        this.ornamentId = ornamentId;
    }

    public List<RespDanger> getDanger() {
        return this.danger;
    }

    public void setDanger(List<RespDanger> danger) {
        this.danger = danger;
    }

    public int getProtectTime() {
        return this.protectTime;
    }

    public void setProtectTime(int protectTime) {
        this.protectTime = protectTime;
    }

    public int getRobEnable() {
        return this.robEnable;
    }

    public void setRobEnable(int robEnable) {
        this.robEnable = robEnable;
    }

    @Override
    public String toString() {
        return "RespBox{id='" + this.id + "', boxType=" + this.boxType + ", openTime=" + this.openTime
                + ", boxStatus=" + this.boxStatus + ", ornamentId=" + this.ornamentId + ", protectTime="
                + this.protectTime + ", robEnable=" + this.robEnable + ", danger=" + this.danger + '}';
    }
}
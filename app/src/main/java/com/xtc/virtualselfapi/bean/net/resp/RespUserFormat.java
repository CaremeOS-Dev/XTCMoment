package com.xtc.virtualselfapi.bean.net.resp;

import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;

import java.util.HashMap;
import java.util.List;

/**
 * 用户虚拟形象数据响应。
 */
public class RespUserFormat {

    private RespCurrentCostumeInfo currentCollectCardInfo;
    private HashMap<Integer, Integer> debris;
    private List<Integer> decoration;
    private int gender;
    private DynamicsVirtualSelfBean talentCustomDress;
    private int useDynamic;
    private RespBox userBox;

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Integer> getDecoration() {
        return this.decoration;
    }

    public void setDecoration(List<Integer> decoration) {
        this.decoration = decoration;
    }

    public HashMap<Integer, Integer> getDebris() {
        return this.debris;
    }

    public void setDebris(HashMap<Integer, Integer> debris) {
        this.debris = debris;
    }

    public RespBox getUserBox() {
        return this.userBox;
    }

    public void setUserBox(RespBox userBox) {
        this.userBox = userBox;
    }

    public DynamicsVirtualSelfBean getTalentCustomDress() {
        return this.talentCustomDress;
    }

    public void setTalentCustomDress(DynamicsVirtualSelfBean talentCustomDress) {
        this.talentCustomDress = talentCustomDress;
    }

    public int getUseDynamic() {
        return this.useDynamic;
    }

    public void setUseDynamic(int useDynamic) {
        this.useDynamic = useDynamic;
    }

    public RespCurrentCostumeInfo getCurrentCollectCardInfo() {
        return this.currentCollectCardInfo;
    }

    public void setCurrentCollectCardInfo(RespCurrentCostumeInfo currentCollectCardInfo) {
        this.currentCollectCardInfo = currentCollectCardInfo;
    }

    @Override
    public String toString() {
        return "RespUserFormat{gender=" + this.gender + ", decoration=" + this.decoration + ", debris=" + this.debris
                + ", userBox=" + this.userBox + ", useDynamic=" + this.useDynamic + ", currentCollectCardInfo="
                + this.currentCollectCardInfo + ", talentCustomDress=" + this.talentCustomDress + '}';
    }
}
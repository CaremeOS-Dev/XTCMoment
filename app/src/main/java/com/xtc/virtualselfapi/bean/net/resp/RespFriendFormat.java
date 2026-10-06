package com.xtc.virtualselfapi.bean.net.resp;

import android.text.TextUtils;

import com.xtc.utils.encode.JSONUtil;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友虚拟形象数据响应。
 */
public class RespFriendFormat {

    private int cardCostume;
    private String decoration;
    private List<Integer> decorationList;
    private int gender;
    private String openId;
    private RespCurrentCostumeInfo respCurrentCostumeInfo;
    private DynamicsVirtualSelfBean talentCustomDress;
    private RespBox userBox;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Integer> getDecorationList() {
        if (this.decorationList == null) {
            if (TextUtils.isEmpty(this.decoration)) {
                this.decorationList = new ArrayList<>();
            } else {
                this.decorationList = (List) JSONUtil.fromJSON(this.decoration, List.class, Integer.class);
            }
        }
        return this.decorationList;
    }

    public void setDecorationList(List<Integer> decorationList) {
        this.decorationList = decorationList;
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

    public int getCardCostume() {
        return this.cardCostume;
    }

    public void setCardCostume(int cardCostume) {
        this.cardCostume = cardCostume;
    }

    public RespCurrentCostumeInfo getRespCurrentCostumeInfo() {
        return this.respCurrentCostumeInfo;
    }

    public void setRespCurrentCostumeInfo(RespCurrentCostumeInfo respCurrentCostumeInfo) {
        this.respCurrentCostumeInfo = respCurrentCostumeInfo;
    }

    @Override
    public String toString() {
        return "RespFriendFormat{openId='" + this.openId + "', gender=" + this.gender + ", decoration="
                + this.decoration + ", userBox=" + this.userBox + '}';
    }
}
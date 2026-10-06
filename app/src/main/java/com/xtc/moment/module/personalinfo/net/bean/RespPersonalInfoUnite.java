package com.xtc.moment.module.personalinfo.net.bean;

import com.xtc.moment.module.prerogative.bean.PersonalState;

import java.io.Serializable;

/**
 * 个人中心聚合信息。
 */
public class RespPersonalInfoUnite implements Serializable {

    private GetBadgeResponse simpleMedal;
    private PersonalInfoAndLikeRuleResponse personalInfo;
    private PersonalInfoResponse geniusAccount;
    private PersonalState socializeUser;

    public GetBadgeResponse getSimpleMedal() {
        return this.simpleMedal;
    }

    public void setSimpleMedal(GetBadgeResponse simpleMedal) {
        this.simpleMedal = simpleMedal;
    }

    public PersonalInfoAndLikeRuleResponse getPersonalInfo() {
        return this.personalInfo;
    }

    public void setPersonalInfo(PersonalInfoAndLikeRuleResponse personalInfo) {
        this.personalInfo = personalInfo;
    }

    public PersonalInfoResponse getGeniusAccount() {
        return this.geniusAccount;
    }

    public void setGeniusAccount(PersonalInfoResponse geniusAccount) {
        this.geniusAccount = geniusAccount;
    }

    public PersonalState getSocializeUser() {
        return this.socializeUser;
    }

    public void setSocializeUser(PersonalState socializeUser) {
        this.socializeUser = socializeUser;
    }

    @Override
    public String toString() {
        return "RespPersonalInfoUnite{getBadgeResponse=" + this.simpleMedal + ", personalInfoAndLikeRuleResponse="
                + this.personalInfo + ", personalResponse=" + this.geniusAccount + '}';
    }
}
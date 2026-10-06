package com.xtc.virtualselfapi.view;

/**
 * 好友虚拟形象视图。
 */
public class FriendVirtualView extends BaseVirtualView {

    private boolean isSkill;

    public boolean isSkill() {
        return this.isSkill;
    }

    public void setSkill(boolean skill) {
        this.isSkill = skill;
    }

    @Override
    public String toString() {
        return "FriendView{status=" + this.status + ", viewInfoList=" + this.viewInfoList
                + ", talentCustomDress=" + this.talentCustomDress + ", isSkill=" + this.isSkill + '}';
    }
}
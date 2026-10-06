package com.xtc.moment.module.personalinfo;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.module.personalinfo.net.bean.BadgeBean;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleResponse;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoResponse;
import com.xtc.moment.module.prerogative.bean.PersonalState;

import java.util.List;

public interface IAccountInfoView extends MvpView {

    void deleteFriendSuccess();

    void loadFailed(String str);

    void loadSuccess();

    void onContactRemove(String str);

    void refreshMainBg(String str);

    void setHelperStr(String str, String str2, String str3);

    void showLoading();

    void startToChatActivity(String str);

    void updateBadge(List<BadgeBean> list);

    void updatePersonalInfo(PersonalInfoResponse personalInfoResponse);

    void updateSignatureAndLike(PersonalInfoAndLikeRuleResponse personalInfoAndLikeRuleResponse);

    void updateState(PersonalState personalState);
}
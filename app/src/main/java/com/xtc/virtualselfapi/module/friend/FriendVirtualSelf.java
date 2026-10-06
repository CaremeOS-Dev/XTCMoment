package com.xtc.virtualselfapi.module.friend;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.virtualselfapi.bean.Costume;
import com.xtc.virtualselfapi.bean.DynamicsVirtualSelfBean;
import com.xtc.virtualselfapi.bean.State;
import com.xtc.virtualselfapi.bean.ViewInfo;
import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.net.resp.RespBox;
import com.xtc.virtualselfapi.bean.net.resp.RespCurrentCostumeInfo;
import com.xtc.virtualselfapi.bean.net.resp.RespDanger;
import com.xtc.virtualselfapi.bean.net.resp.RespFriendFormat;
import com.xtc.virtualselfapi.bean.net.resp.RespUserFormat;
import com.xtc.virtualselfapi.constants.Constants;
import com.xtc.virtualselfapi.helper.DangerHelper;
import com.xtc.virtualselfapi.manager.VirtualSelfDBServeImpl;
import com.xtc.virtualselfapi.manager.VirtualSelfInitManager;
import com.xtc.virtualselfapi.module.BaseVirtualSelf;
import com.xtc.virtualselfapi.utils.TimeUtils;
import com.xtc.virtualselfapi.view.FriendVirtualView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 好友虚拟形象数据模块。
 */
public class FriendVirtualSelf extends BaseVirtualSelf<FriendVirtualView> {

    private static final String TAG = "Virtual_Self_Api_FriendVirtualSelf";

    public FriendVirtualSelf(Context context) {
        super(context);
    }

    @Override
    public Observable<FriendVirtualView> getVirtualSelfView(String openId) {
        return Observable.combineLatest(fetchFriendInfo(openId), fetchCurrentCostume(openId),
                new Func2<RespFriendFormat, RespCurrentCostumeInfo, RespFriendFormat>() {
                    @Override
                    public RespFriendFormat call(RespFriendFormat friendFormat, RespCurrentCostumeInfo costumeInfo) {
                        LogUtil.d(TAG, "friendInfo = " + friendFormat);
                        LogUtil.d(TAG, "respCurrentCostumeInfo = " + costumeInfo);
                        return mergeFriendAndCostumeInfo(friendFormat, costumeInfo);
                    }
                })
                .flatMap(new Func1<RespFriendFormat, Observable<FriendVirtualView>>() {
                    @Override
                    public Observable<FriendVirtualView> call(RespFriendFormat friendFormat) {
                        if (friendFormat != null) {
                            return dealFriendVirtual(friendFormat);
                        }
                        return Observable.just(new FriendVirtualView());
                    }
                });
    }

    private RespFriendFormat mergeFriendAndCostumeInfo(RespFriendFormat friendFormat, RespCurrentCostumeInfo costumeInfo) {
        if (friendFormat == null) {
            return null;
        }
        boolean supportCardCostume = WatchAccountBase.queryModuleSwitchByBoolean(
                VirtualSelfInitManager.getInstance().getAppContext(),
                Constants.ModuleSwitch.MODULE_SWITCH_COLLECT_CARD, false);
        if (costumeInfo != null && supportCardCostume) {
            Costume costume = costumeInfo.getCostume();
            if (costume != null) {
                friendFormat.setCardCostume(costume.getCurrent());
            }
            friendFormat.setRespCurrentCostumeInfo(costumeInfo);
        }
        return friendFormat;
    }

    private Observable<RespFriendFormat> fetchFriendInfo(String openId) {
        List<String> openIdList = new ArrayList<>();
        openIdList.add(openId);
        return this.httpManager.getFriendList(openIdList).map(new Func1<List<RespFriendFormat>, RespFriendFormat>() {
            @Override
            public RespFriendFormat call(List<RespFriendFormat> friendList) {
                if (friendList == null || friendList.size() <= 0) {
                    return null;
                }
                return friendList.get(0);
            }
        });
    }

    private Observable<RespCurrentCostumeInfo> fetchCurrentCostume(String openId) {
        return this.httpManager.getCurrentCostume(openId);
    }

    private Observable<FriendVirtualView> dealFriendVirtual(RespFriendFormat friendFormat) {
        RespBox userBox = friendFormat.getUserBox();
        boolean hasCardCostume = friendFormat.getCardCostume() > 0;
        boolean isSkill = loadFriendSkillState(friendFormat);
        LogUtil.d(TAG, "getFriendViewInfo：isHaveCardCostume = " + hasCardCostume + ", isSkill = " + isSkill);
        if (userBox == null || userBox.getBoxStatus() != 0) {
            return createFriendViewInfo(createUserFormat(friendFormat, hasCardCostume), 6, isSkill);
        }
        long now = System.currentTimeMillis();
        RespDanger danger = null;
        if (userBox.getDanger() != null && userBox.getDanger().size() > 0) {
            danger = userBox.getDanger().get(0);
        }
        if (TimeUtils.checkHasDanger(danger)) {
            LogUtil.d(TAG, "current box status is danger!");
            friendFormat.setDecorationList(new DangerHelper().convertDangerToUsingDecoration(
                    danger.getDangerId(), friendFormat.getDecorationList()));
            return createFriendViewInfo(createUserFormat(friendFormat, hasCardCostume), 1, isSkill);
        }
        if (now < userBox.getOpenTime()) {
            return createFriendViewInfo(createUserFormat(friendFormat, hasCardCostume), 5, isSkill);
        }
        if (now < userBox.getOpenTime() + ((long) (userBox.getProtectTime() * 1000))) {
            return createFriendViewInfo(createUserFormat(friendFormat, hasCardCostume), 2, isSkill);
        }
        return createFriendViewInfo(createUserFormat(friendFormat, hasCardCostume), 3, isSkill);
    }

    private Observable<FriendVirtualView> createFriendViewInfo(final RespUserFormat userFormat, final int status, final boolean isSkill) {
        return getViewInfoList(userFormat).map(new Func1<List<ViewInfo>, FriendVirtualView>() {
            @Override
            public FriendVirtualView call(List<ViewInfo> viewInfoList) {
                FriendVirtualView friendVirtualView = new FriendVirtualView();
                friendVirtualView.setViewInfoList(viewInfoList);
                friendVirtualView.setStatus(status);
                friendVirtualView.setSkill(isSkill);
                DynamicsVirtualSelfBean talentCustomDress = userFormat.getTalentCustomDress();
                if (talentCustomDress != null) {
                    friendVirtualView.setTalentCustomDress(talentCustomDress);
                }
                return friendVirtualView;
            }
        });
    }

    private RespUserFormat createUserFormat(RespFriendFormat friendFormat, boolean hasCardCostume) {
        RespUserFormat userFormat = new RespUserFormat();
        userFormat.setGender(friendFormat.getGender());
        userFormat.setDecoration(friendFormat.getDecorationList());
        userFormat.setUserBox(friendFormat.getUserBox());
        userFormat.setTalentCustomDress(hasCardCostume ? null : friendFormat.getTalentCustomDress());
        userFormat.setCurrentCollectCardInfo(friendFormat.getRespCurrentCostumeInfo());
        return userFormat;
    }

    private boolean loadFriendSkillState(RespFriendFormat friendFormat) {
        if (friendFormat == null || friendFormat.getRespCurrentCostumeInfo() == null) {
            return false;
        }
        State state = friendFormat.getRespCurrentCostumeInfo().getState();
        if (state == null) {
            LogUtil.d(TAG, "未中技能卡");
            return false;
        }
        DbCostume skillCostume = VirtualSelfDBServeImpl.getInstance().getCostume((int) state.getCostunmeId());
        if (TimeUtils.isSkillCardExpired(state)) {
            LogUtil.d(TAG, "技能卡超出有效期");
            return false;
        }
        if (skillCostume == null) {
            LogUtil.w(TAG, "技能卡命中用户，但未查询到对应技能显示装扮");
            return false;
        }
        List<Integer> decorationList = friendFormat.getDecorationList();
        Integer replacedCostumeId = null;
        for (Integer decorationId : decorationList) {
            DbCostume costume = VirtualSelfDBServeImpl.getInstance().getCostume(decorationId.intValue());
            if (costume != null && costume.getType() == skillCostume.getType()) {
                LogUtil.i(TAG, "替换用户装扮用于呈现技能卡 替换装扮 ：" + costume);
                replacedCostumeId = Integer.valueOf(costume.getCostumeId());
            }
        }
        if (replacedCostumeId == null) {
            LogUtil.w(TAG, "技能卡命中用户，但未能替换用户数据");
        } else {
            decorationList.remove(replacedCostumeId);
        }
        decorationList.add(Integer.valueOf(skillCostume.getCostumeId()));
        LogUtil.i(TAG, "需显示的装扮列表为：" + decorationList);
        friendFormat.setDecorationList(decorationList);
        if (skillCostume.getType() != 0) {
            return true;
        }
        LogUtil.i(TAG, "技能改变的是人物套装时，去掉动态装扮");
        friendFormat.setTalentCustomDress(null);
        return true;
    }
}
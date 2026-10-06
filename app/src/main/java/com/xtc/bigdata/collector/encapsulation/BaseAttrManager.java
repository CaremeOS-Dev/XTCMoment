package com.xtc.bigdata.collector.encapsulation;

import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.xtc.bigdata.collector.SessionAgent;
import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.bigdata.collector.encapsulation.entity.BaseAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.ApplicationAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.MachineAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.OtherAttr;
import com.xtc.bigdata.collector.encapsulation.entity.attr.UserAttr;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

/** Provides the shared attributes attached to every event. */
public class BaseAttrManager {

    private static BaseAttr baseAttr;
    private ApplicationAttr applicationAttr;
    private MachineAttr machineAttr;
    private UserAttr userAttr;

    private static class InstanceHolder {
        private static final BaseAttrManager INSTANCE = new BaseAttrManager();

        private InstanceHolder() {
        }
    }

    public static BaseAttrManager getInstance() {
        if (baseAttr == null) {
            baseAttr = BaseAttr.getBaseAttr();
            LogUtil.i("bigData", "baseAttr = " + baseAttr);
        }
        return InstanceHolder.INSTANCE;
    }

    public static void setBaseAttr(BaseAttr baseAttr) {
        BaseAttrManager.baseAttr = baseAttr;
    }

    public void setUserAttr(UserAttr userAttr) {
        this.userAttr = userAttr;
        SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.USERINFO, JSONUtil.toJSON(this.userAttr));
    }

    public UserAttr getUserAttr() {
        if (this.userAttr == null) {
            String json = SharedPrefUtils.getInstance().getKeyStringValue(DeviceInfo.USERINFO, "");
            if (!TextUtils.isEmpty(json)) {
                this.userAttr = (UserAttr) JSONUtil.fromJSON(json, UserAttr.class);
            }
        }
        return this.userAttr;
    }

    public OtherAttr getOtherAttr(String extend, String extendJudgment) {
        OtherAttr otherAttr = new OtherAttr();
        otherAttr.setSessionid(SessionAgent.getSessionId());
        otherAttr.setDaVer("");
        otherAttr.setExtend(extend);
        otherAttr.setExtendJudgment(extendJudgment);
        return otherAttr;
    }

    public ApplicationAttr getApplicationAttr() {
        ApplicationAttr current = this.applicationAttr;
        if (current == null || TextUtils.isEmpty(current.getAppId())) {
            String channelId = Constants.deviceType.equals(Constants.PHONE)
                    ? SharedPrefUtils.getInstance().getKeyStringValue(DeviceInfo.PHONE_CHANNEL_ID, "") : "";
            if (baseAttr != null) {
                this.applicationAttr = new ApplicationAttr()
                        .setAppId(baseAttr.getAppId())
                        .setAppVersion(baseAttr.getAppVersion())
                        .setModuleName(baseAttr.getModuleName())
                        .setchannleId(channelId)
                        .setPackageName(baseAttr.getPackageName());
            }
        }
        return this.applicationAttr;
    }

    public MachineAttr getMachineAttr() {
        if (baseAttr != null) {
            this.machineAttr = new MachineAttr()
                    .setmId(baseAttr.getmId())
                    .setOsVersion(baseAttr.getOsVer())
                    .setDevName(baseAttr.getDevName())
                    .setInnerModel(baseAttr.getInnerModel())
                    .setBrand(baseAttr.getBrand())
                    .setRooted(baseAttr.getRooted());
        }
        return this.machineAttr;
    }

    public String getScreenResolution() {
        if (ContextUtils.isEmpty()) {
            return "";
        }
        DisplayMetrics displayMetrics = ContextUtils.getContext().getResources().getDisplayMetrics();
        return "{" + displayMetrics.widthPixels + "," + displayMetrics.heightPixels + "}";
    }

    private BaseAttrManager() {
    }
}
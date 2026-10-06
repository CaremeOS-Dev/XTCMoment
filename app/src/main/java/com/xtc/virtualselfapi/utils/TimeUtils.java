package com.xtc.virtualselfapi.utils;

import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.State;
import com.xtc.virtualselfapi.bean.net.resp.RespDanger;

/**
 * 时间相关判断工具。
 */
public class TimeUtils {

    private static final String TAG = "Virtual_Self_Api_TimeUtils";

    public static boolean checkHasDanger(RespDanger danger) {
        long now = System.currentTimeMillis();
        return danger != null && now > danger.getStartTime()
                && now < danger.getStartTime() + danger.getDuration();
    }

    public static boolean isSkillCardExpired(State state) {
        if (state == null || state.getCostunmeId() == 0) {
            return true;
        }
        if (System.currentTimeMillis() < state.getHour()) {
            return false;
        }
        LogUtil.i(TAG, "技能卡已超出有效期");
        return true;
    }
}
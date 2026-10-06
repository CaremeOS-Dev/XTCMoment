package com.xtc.moment.util;

import com.xtc.moment.db.bean.DbMoment;

/**
 * 官方动态类型判定工具。
 */
public class OfficialMomentTypeUtil {

    public static boolean isOfficialType(int type) {
        switch (type) {
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTopOfficialMoment(DbMoment moment) {
        return isOfficialType(moment.getType().intValue())
                && 1 == moment.getTop()
                && System.currentTimeMillis() < moment.getTopExpireTime();
    }
}
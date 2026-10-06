package com.xtc.moment.event;

import com.xtc.moment.module.StringConstant;

import java.util.HashMap;

/**
 * 分享行为埋点实体。
 */
public class ShareEventEntity {

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public static String funtionName = "enjoytakepicture_sharefriend";

    public String time;
    public String share;
    public String appname;
    public String isSuccess;

    private HashMap<String, String> mHashMap = new HashMap<>();

    public HashMap<String, String> getHashMap() {
        this.mHashMap.put("time", this.time);
        this.mHashMap.put("share", StringConstant.BehaviorKey.FRIEND_CIRCLE);
        this.mHashMap.put(StringConstant.BehaviorKey.SHARE_APP_NAME, this.appname);
        this.mHashMap.put("success", this.isSuccess);
        return this.mHashMap;
    }
}
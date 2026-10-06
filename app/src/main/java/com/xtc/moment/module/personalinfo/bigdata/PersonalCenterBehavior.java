package com.xtc.moment.module.personalinfo.bigdata;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.util.HandlerUtil;

import java.util.HashMap;

public class PersonalCenterBehavior implements IPersonalCenterBehaviorConstant {

    private static final String TAG = "PersonalCenterBehavior";

    public static void viewFriendPage(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "viewFriendPage : way = " + way);
                HashMap<String, String> map = new HashMap<>();
                map.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), VIEW_FRIEND_PAGE, map);
            }
        });
    }

    public static void pullDownVirtualself() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "pullDownVirtualself");
                BehaviorUtil.customEvent(ContextUtils.getContext(), PULL_DOWN_VIRTUALSELF, new HashMap<String, String>());
            }
        });
    }

    public static void likeFriendWay(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendWay : way = " + way);
                HashMap<String, String> map = new HashMap<>();
                map.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_WAY, map);
            }
        });
    }

    public static void likeFriendCount(final int count) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendCount : count = " + count);
                HashMap<String, String> map = new HashMap<>();
                map.put(Key.COUNT, String.valueOf(count));
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_COUNT, map);
            }
        });
    }

    public static void likeFriendLimit() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendLimit");
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_LIMIT, new HashMap<String, String>());
            }
        });
    }

    public static void viewFriendBadge() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "viewFriendBadge");
                BehaviorUtil.customEvent(ContextUtils.getContext(), VIEW_FRIEND_BADGE, new HashMap<String, String>());
            }
        });
    }

    public static void deleteFriend(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "deleteFriend : way = " + way);
                HashMap<String, String> map = new HashMap<>();
                map.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), DELETE_FRIEND, map);
            }
        });
    }
}
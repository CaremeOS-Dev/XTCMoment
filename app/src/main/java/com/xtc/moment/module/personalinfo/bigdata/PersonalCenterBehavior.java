package com.xtc.moment.module.personalinfo.bigdata;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.util.HandlerUtil;

import java.util.HashMap;

/**
 * 个人中心页面的埋点上报入口。
 *
 * <p>每个事件都在后台线程里组装参数并调用 {@link BehaviorUtil#customEvent}，
 * 避免埋点逻辑阻塞 UI。事件名与参数键来自 {@link IPersonalCenterBehaviorConstant}。
 */
public class PersonalCenterBehavior implements IPersonalCenterBehaviorConstant {

    private static final String TAG = "PersonalCenterBehavior";

    private PersonalCenterBehavior() {
    }

    /** 进入好友主页，{@code way} 表示入口来源。 */
    public static void viewFriendPage(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "viewFriendPage : way = " + way);
                HashMap<String, String> params = new HashMap<>();
                params.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), VIEW_FRIEND_PAGE, params);
            }
        });
    }

    /** 下拉展示好友天才秀。 */
    public static void pullDownVirtualself() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "pullDownVirtualself");
                BehaviorUtil.customEvent(ContextUtils.getContext(), PULL_DOWN_VIRTUALSELF, new HashMap<String, String>());
            }
        });
    }

    /** 点赞好友，{@code way} 表示点赞入口。 */
    public static void likeFriendWay(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendWay : way = " + way);
                HashMap<String, String> params = new HashMap<>();
                params.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_WAY, params);
            }
        });
    }

    /** 上报点赞数量。 */
    public static void likeFriendCount(final int count) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendCount : count = " + count);
                HashMap<String, String> params = new HashMap<>();
                params.put(Key.COUNT, String.valueOf(count));
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_COUNT, params);
            }
        });
    }

    /** 点赞次数达到上限。 */
    public static void likeFriendLimit() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "likeFriendLimit");
                BehaviorUtil.customEvent(ContextUtils.getContext(), LIKE_FRIEND_LIMIT, new HashMap<String, String>());
            }
        });
    }

    /** 查看好友勋章墙。 */
    public static void viewFriendBadge() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "viewFriendBadge");
                BehaviorUtil.customEvent(ContextUtils.getContext(), VIEW_FRIEND_BADGE, new HashMap<String, String>());
            }
        });
    }

    /** 删除好友，{@code way} 表示删除入口。 */
    public static void deleteFriend(final int way) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "deleteFriend : way = " + way);
                HashMap<String, String> params = new HashMap<>();
                params.put(Key.WAY, String.valueOf(way));
                BehaviorUtil.customEvent(ContextUtils.getContext(), DELETE_FRIEND, params);
            }
        });
    }
}
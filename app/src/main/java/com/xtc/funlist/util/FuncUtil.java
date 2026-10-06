package com.xtc.funlist.util;

import com.xtc.funlist.IXtcFunListNew;
import com.xtc.watch.WatchConfigManager;

/** Convenience checks against the watch fun-list configuration. */
public class FuncUtil {

    /** @return true when personality dress is enabled. */
    public static boolean supportPersonalityDress() {
        return supportByFunction(IXtcFunListNew.PERSONALITY_DRESS_1001, 1001);
    }

    /** @return true when the fun short-video entry is enabled. */
    public static boolean supportFunShortVideo() {
        return supportByFunction(IXtcFunListNew.FUN_SHORT_VIDEO_1002, 1002);
    }

    /** @return true when "take the same" short video is enabled. */
    public static boolean supportFunShortVideoTakeSame() {
        return supportByFunction(IXtcFunListNew.FUN_SHORT_VIDEO_TAKE_SAME_1003, 1003);
    }

    /** @return true when locally deleting a friend on the watch is enabled. */
    public static boolean supportLocalDeleteFriendWatch() {
        return supportByFunction(IXtcFunListNew.LOCALE_DELETE_FRIEND_1004, 1004);
    }

    /** @return true when the friend virtual-self entry is enabled. */
    public static boolean supportFriendVirtualSelf() {
        return supportByFunction(IXtcFunListNew.FRIEND_VIRTUAL_SELF_1005, 1005);
    }

    /** @return true when camera integration is enabled. */
    public static boolean supportCameraIntegration() {
        return supportByFunction(IXtcFunListNew.CAMERA_INTEGRATION_1006, 1006);
    }

    private static boolean supportByFunction(String name, int code) {
        return WatchConfigManager.getBoolean(name, code, false);
    }
}
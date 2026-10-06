package com.xtc.moment.third.behavior;

import android.content.Context;
import android.os.SystemClock;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.StringConstant;
import com.xtc.moment.third.bean.PushBehaviorBean;
import com.xtc.moment.third.bean.PushCommentBean;
import com.xtc.moment.third.bean.PushContentBean;
import com.xtc.moment.third.bean.PushReportBean;
import com.xtc.moment.third.bean.PushVideoBean;
import com.xtc.moment.util.SystemUtil;

import java.util.HashMap;

/**
 * 好友圈埋点统一入口：把各交互事件转成自定义事件上报。
 */
public class MomentBehavior {

    private static final long LIMIT_TIME = 400;
    private static final String TAG = MomentBehavior.class.getSimpleName();
    private static HashMap<Integer, Long> sLastTimeMap = null;

    public static void usageTime(Context context, long scrollTime, long leaveTime) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.SCROLL_TIME, String.valueOf(scrollTime));
        map.put(StringConstant.BehaviorKey.LEAVE_TIME, String.valueOf(leaveTime));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_HOME_USED, map);
    }

    public static void publishClick(Context context, long time, String type) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put("time", String.valueOf(time));
        map.put("type", type);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_HOME_PUBLISH_CLICK, map);
    }

    public static void moodEnter(Context context, long inTime, long outTime) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.IN_TIME, String.valueOf(inTime));
        map.put(StringConstant.BehaviorKey.OUT_TIME, String.valueOf(outTime));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUBLISH_MOOD, map);
    }

    public static void stateEnter(Context context, long inTime, long outTime) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.IN_TIME, String.valueOf(inTime));
        map.put(StringConstant.BehaviorKey.OUT_TIME, String.valueOf(outTime));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUBLISH_STATE, map);
    }

    public static void newLikeEnter(Context context, long inTime, long outTime) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.IN_TIME, String.valueOf(inTime));
        map.put(StringConstant.BehaviorKey.OUT_TIME, String.valueOf(outTime));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_HOME_NEWLIKE, map);
    }

    public static void shareEnter(Context context, long inTime, long outTime) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.IN_TIME, String.valueOf(inTime));
        map.put(StringConstant.BehaviorKey.OUT_TIME, String.valueOf(outTime));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_HOME_SHARE, map);
    }

    public static void likeClick(Context context, String watchId, String content) {
        LogUtil.i(TAG, "点击点赞按钮埋点");
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(watchId));
        map.put("moment_content", String.valueOf(content));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_HOME_LIKE_CLICK, map);
    }

    public static void clickFunctionRecord(Context context, int type) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_PUBLISH_FUNCTION_ITEM_TYPE, map);
    }

    public static void deleteContent(Context context, PushContentBean bean) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("moment_content", String.valueOf(bean.getContent()));
        map.put("moment_type", String.valueOf(bean.getType()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_DELETE_ITEM, map);
    }

    public static void commentMoment(Context context, PushCommentBean bean) {
        HashMap<String, String> map = new HashMap<>(4);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.COMMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.COMMENT_REPLYID, String.valueOf(bean.getReplyId()));
        map.put(StringConstant.BehaviorKey.COMMENT_STATUS, String.valueOf(bean.getStatus()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUBLISH_COMMENT, map);
    }

    public static void commentOfficialMoment(Context context, PushCommentBean bean) {
        LogUtil.i(TAG, "评论官方消息埋点");
        HashMap<String, String> map = new HashMap<>(5);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.COMMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.COMMENT_REPLYID, String.valueOf(bean.getReplyId()));
        map.put(StringConstant.BehaviorKey.COMMENT_STATUS, String.valueOf(bean.getStatus()));
        map.put(StringConstant.BehaviorKey.COMMENT_CONTENT, String.valueOf(bean.getContent()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUBLISH_COMMENT_OFFICIAL, map);
    }

    public static void behaviorOfficialMoment(Context context, PushBehaviorBean bean) {
        LogUtil.i(TAG, "官方消息浏览埋点");
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.MOMENT_BEHAVIOR, String.valueOf(bean.getBehavior()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_BEHAVIOR_OFFICIAL, map);
    }

    public static void commentDelete(Context context, PushCommentBean bean) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.COMMENT_ID, String.valueOf(bean.getCommentId()));
        map.put(StringConstant.BehaviorKey.COMMENT_WATCHID, String.valueOf(bean.getWatchId()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_DELETE_COMMENT, map);
    }

    public static void clickReportBtn(Context context, PushReportBean bean) {
        LogUtil.i(TAG, "点击举报按钮");
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_REPORT_BTN, map);
    }

    public static void selectReportReasonsContent(Context context, PushReportBean bean) {
        HashMap<String, String> map = new HashMap<>(3);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.MOMENT_REASONS_CONTENT, String.valueOf(bean.getContent()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_SELECT_REPORT_REASONS, map);
    }

    public static void submitReportContent(Context context, PushReportBean bean) {
        HashMap<String, String> map = new HashMap<>(3);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.MOMENT_REPORT_CONTENT, String.valueOf(bean.getContent()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_SUBMIT_REPORT_CONTENT, map);
    }

    public static void clickReportBtnHint(Context context, PushReportBean bean) {
        HashMap<String, String> map = new HashMap<>(3);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.MOMENT_REPORT_HINT_CONTENT, String.valueOf(bean.getContent()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_REPORT_BTN_HINT, map);
    }

    public static void videoPlayCount(Context context, PushVideoBean bean) {
        HashMap<String, String> map = new HashMap<>(3);
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put("time", String.valueOf(bean.getTime()));
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_LOOK_VIDEO_PLAY_COUNT, map);
    }

    public static void sendVideoAndSuccessSend(Context context, PushVideoBean bean) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put("moment_type", String.valueOf(bean.getType()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_SEND_AND_SEND_SUCCESS, map);
    }

    public static void shareFail(Context context, String appName, String success, String time) {
        HashMap<String, String> map = new HashMap<>(4);
        map.put("time", time);
        map.put("share", StringConstant.BehaviorKey.FRIEND_CIRCLE);
        map.put(StringConstant.BehaviorKey.SHARE_APP_NAME, appName);
        map.put("success", success);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_SHARE, map);
    }

    public static void clickTakeSameButton(Context context, int type, String watchId) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("type", String.valueOf(type));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, watchId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_TAKE_SAME, map);
    }

    public static void sendGiftRecord(Context context, int type, String watchId) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("type", String.valueOf(type));
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, watchId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_SEND_GIFT, map);
    }

    public static void clickInteractButton(Context context) {
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_INTERACT_BUTTON, new HashMap<String, String>(0));
    }

    public static void clickBarrageButton(Context context, int type) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_CLICK_BARRAGE_BUTTON, map);
    }

    public static void advLikedClick(Context context, String momentId) {
        LogUtil.i(TAG, "广告点击点赞按钮埋点");
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, momentId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_LIKED_CLICK, map);
    }

    public static void advCancelLikedClick(Context context, String momentId) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, momentId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_CANCEL_LIKED_CLICK, map);
    }

    public static void advCommentDeleteClick(Context context, PushCommentBean bean) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.COMMENT_ID, String.valueOf(bean.getCommentId()));
        map.put(StringConstant.BehaviorKey.COMMENT_WATCHID, String.valueOf(bean.getWatchId()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_DELETE_COMMENT_CLICK, map);
    }

    public static void advLongClick(Context context, String momentId) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, momentId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_LONG_CLICK, map);
    }

    public static void advCloseClick(Context context, String momentId) {
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.MOMENT_ID, momentId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_CLOSE_CLICK, map);
    }

    public static void advVideoPlayCompletely(Context context, PushVideoBean bean) {
        HashMap<String, String> map = new HashMap<>(2);
        map.put(StringConstant.BehaviorKey.MOMENT_WATCHID, String.valueOf(bean.getWatchId()));
        map.put(StringConstant.BehaviorKey.MOMENT_ID, String.valueOf(bean.getMomentId()));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ADVERTISING_PLAY_THE_FULL_VIDEO, map);
    }

    public static void pushDynamic(Context context, int type) {
        LogUtil.i(TAG, "pushDynamic type = " + type);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUSH_NUMBER_MAGES, map);
    }

    public static void pushDynamic(Context context, int type, int num) {
        LogUtil.i(TAG, "pushDynamic type = " + type);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        map.put("num", String.valueOf(num));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_PUSH_NUMBER_MAGES, map);
    }

    public static void pushDynamicEntrace(Context context, int type) {
        LogUtil.i(TAG, "pushDynamicEntrace type = " + type);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_COMPILE_ENTRANCE, map);
    }

    public static void addDynamicType(Context context, int type) {
        LogUtil.i(TAG, "addDynamicType type = " + type);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_ENTRANCE_WAY, map);
    }

    public static void clickHeadState(Context context, int type) {
        LogUtil.i(TAG, "clickHeadState type = " + type);
        HashMap<String, String> map = new HashMap<>(1);
        map.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_FRIEND_AND_ONESELF, map);
    }

    public static void shareSupportResult(Context context, String packageName, int shareType, int result, String url) {
        LogUtil.i(TAG, "shareSupportResult packageName = " + packageName + ", shareType = " + shareType + ", result = " + result);
        HashMap<String, String> map = new HashMap<>(4);
        map.put("packageName", packageName);
        map.put(StringConstant.BehaviorKey.SHARE_TYPE, String.valueOf(shareType));
        map.put(StringConstant.BehaviorKey.SHARE_FROM, "com.xtc.moment");
        map.put("result", String.valueOf(result));
        map.put(StringConstant.BehaviorKey.WEB_URL, url);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.SHARE_SUPPORT_RESULT, map);
    }

    public static void ShareToPicture(Context context, String aoiId) {
        LogUtil.d(TAG, "from launcher address =" + aoiId);
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.AOI_ID, aoiId);
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.FROM_LAUNCHER_ADDRESS_TOMOMENT, map);
    }

    /**
     * 上报列表项卡顿：同一类型半小时内只上报一次。
     */
    public static void uploadMomentItemCatonPoint(int type, long startTime) {
        long now = SystemClock.elapsedRealtime();
        long interval = now - startTime;
        if (interval < LIMIT_TIME) {
            return;
        }
        if (sLastTimeMap == null) {
            sLastTimeMap = new HashMap<>();
        }
        LogUtil.d(TAG, "uploadMomentItemCatonPoint: type = [" + type + "], interval = [" + interval + "]");
        Long lastTime = sLastTimeMap.get(Integer.valueOf(type));
        if (lastTime == null || now - lastTime.longValue() >= Constants.HALF_HOUR) {
            sLastTimeMap.put(Integer.valueOf(type), Long.valueOf(now));
            HashMap<String, String> map = new HashMap<>();
            map.put("type", String.valueOf(type));
            map.put("time", String.valueOf(interval));
            map.put(StringConstant.BehaviorKey.CLIENT_VERSION, SystemUtil.getVersionCode(ContextUtils.getContext()));
            BehaviorUtil.customEvent(ContextUtils.getContext(), StringConstant.BehaviorFunName.MOMENT_ITEM_CATON, map);
        }
    }

    public static void upLoadVisibleRange(Context context, int timeType, int visibleType) {
        LogUtil.i(TAG, "upLoadVisibleRange: , timeType = " + timeType + ", visibleType = " + visibleType);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("time", String.valueOf(timeType));
        map.put("type", String.valueOf(visibleType));
        BehaviorUtil.customEvent(context, StringConstant.BehaviorFunName.MOMENT_VISIBLE_RANGE, map);
    }

    public static void uploadMomentReminderInfo(String label, int count) {
        LogUtil.d(ReminderHelper.M_TAG, "uploadIMInfo label =" + label + ", count =" + count);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("type", label);
        map.put("time", String.valueOf(count));
        BehaviorUtil.customEvent(ContextUtils.getContext(), StringConstant.BehaviorFunName.MOMENT_REMINDER_INFO, map);
    }

    public static void uploadNotMatchLabel(String labelList) {
        LogUtil.d(ReminderHelper.M_TAG, "uploadNotMatch labelList =" + labelList);
        HashMap<String, String> map = new HashMap<>(1);
        map.put(StringConstant.BehaviorKey.MOMENT_REMINDER_ERROR_LABEL_LIST, labelList);
        BehaviorUtil.customEvent(ContextUtils.getContext(), StringConstant.BehaviorFunName.MOMENT_REMINDER_ERROR_LABEL, map);
    }
}
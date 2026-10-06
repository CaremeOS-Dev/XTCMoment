package com.xtc.moment.module;

/** Shared string constants: broadcast actions, behaviour keys, start types. */
public class StringConstant {

    public static final String FILE_NAME_DEFAULT_HEAD = "default_head.png";
    public static final String FRIEND_INFO = "friend_info";
    public static final String FRIEND_INFO_UPDATE = "friend_info_update";
    public static final String INTT_WATCHID_SUCCESS = "init_watchID_success";
    public static final String MOMENT_IM_ACTION = "com.xtc.moment.im.response";
    public static final String MOMENT_PACKAGE = "com.xtc.moment";
    public static final String MOMENT_PUSH_MSG = "moment_push_msg";
    public static final String MOMENT_PUSH_TYPE = "moment_push_type";
    public static final String MY_ICON_NAME = "my_icon.jpg";
    public static final String MY_ICON_PATH = "my_icon_path";
    public static final String MY_INFO_UPDATE = "my_info_update";
    public static final String MY_NAME_KEY = "my_name_string";
    public static final String MY_WATCH_ID = "my_watch_id";
    public static final String RABBIT_WATCHID = "-1";
    public static final String SYNC_LAUNCHER_DB_DATA = "sync_launcher_db_data";
    public static final String SYNC_LAUNCHER_SP_DATA = "sync_launcher_sp_data";
    public static final String TAG = "moment";

    /** Behaviour event names. */
    public interface BehaviorFunName {
        String FROM_LAUNCHER_ADDRESS_TOMOMENT = "launcher_address_tomoment";
        String MOMENT_ADVERTISING_CANCEL_LIKED_CLICK = "moment_advertising_cancel_liked_click";
        String MOMENT_ADVERTISING_CLOSE_CLICK = "moment_advertising_close_click";
        String MOMENT_ADVERTISING_DELETE_COMMENT_CLICK = "moment_advertising_delete_comment_click";
        String MOMENT_ADVERTISING_LIKED_CLICK = "moment_advertising_liked_click";
        String MOMENT_ADVERTISING_LONG_CLICK = "moment_advertising_long_click";
        String MOMENT_ADVERTISING_PLAY_THE_FULL_VIDEO = "moment_advertising_play_the_full_video";
        String MOMENT_BEHAVIOR_OFFICIAL = "moment_behavior_official";
        String MOMENT_CLICK_BARRAGE_BUTTON = "moment_click_barrage_button";
        String MOMENT_CLICK_INTERACT_BUTTON = "moment_click_interact_button";
        String MOMENT_CLICK_PUBLISH_FUNCTION_ITEM_TYPE = "moment_click_publish_function_item_type";
        String MOMENT_CLICK_REPORT_BTN = "moment_click_report_btn";
        String MOMENT_CLICK_REPORT_BTN_HINT = "moment_click_report_btn_hint";
        String MOMENT_CLICK_SEND_AND_SEND_SUCCESS = "moment_click_send_and_send_success";
        String MOMENT_CLICK_TAKE_SAME = "moment_click_take_same";
        String MOMENT_COMPILE_ENTRANCE = "moment_compile_entrance";
        String MOMENT_DELETE_COMMENT = "moment_delete_comment";
        String MOMENT_DELETE_ITEM = "moment_delete_item";
        String MOMENT_ENTRANCE_WAY = "moment_entrance_way";
        String MOMENT_FRIEND_AND_ONESELF = "moment_friend_and_oneself";
        String MOMENT_HOME_LIKE_CLICK = "moment_home_like_click";
        String MOMENT_HOME_NEWLIKE = "moment_home_newlike";
        String MOMENT_HOME_PUBLISH_CLICK = "moment_home_publish_click";
        String MOMENT_HOME_SHARE = "moment_home_share";
        String MOMENT_HOME_USED = "moment_home_used";
        String MOMENT_ITEM_CATON = "moment_item_caton";
        String MOMENT_LOOK_VIDEO_PLAY_COUNT = "moment_video_play_count";
        String MOMENT_PUBLISH_COMMENT = "moment_publish_comment";
        String MOMENT_PUBLISH_COMMENT_OFFICIAL = "moment_publish_comment_official";
        String MOMENT_PUBLISH_MOOD = "moment_publish_mood";
        String MOMENT_PUBLISH_STATE = "moment_publish_state";
        String MOMENT_PUSH_NUMBER_MAGES = "moment_push_number_mages";
        String MOMENT_REMINDER_ERROR_LABEL = "moment_reminder_error_label";
        String MOMENT_REMINDER_INFO = "moment_reminder_info";
        String MOMENT_SELECT_REPORT_REASONS = "moment_select_report_reasons";
        String MOMENT_SEND_GIFT = "moment_send_gift";
        String MOMENT_SHARE = "enjoytakepicture_sharefriend";
        String MOMENT_SUBMIT_REPORT_CONTENT = "moment_submit_report_content";
        String MOMENT_VISIBLE_RANGE = "moment_visible_range";
        String SHARE_SUPPORT_RESULT = "share_support_result";
    }

    /** Behaviour event parameter keys. */
    public interface BehaviorKey {
        String AOI_ID = "aoi_id";
        String CLIENT_VERSION = "clientVersion";
        String COMMENT_CONTENT = "comment_content";
        String COMMENT_ID = "comment_id";
        String COMMENT_REPLYID = "comment_replyid";
        String COMMENT_STATUS = "comment_status";
        String COMMENT_WATCHID = "comment_watchid";
        String FRIEND_CIRCLE = "friendCircle";
        String IN_TIME = "in_time";
        String LEAVE_TIME = "leave_time";
        String LOOK_VIDEO_NAME = "moment_look_video_name";
        String MOMENT_BEHAVIOR = "moment_behavior";
        String MOMENT_CONTENT = "moment_content";
        String MOMENT_ID = "moment_Id";
        String MOMENT_REASONS_CONTENT = "moment_reasons_content";
        String MOMENT_REMINDER_COUNT = "time";
        String MOMENT_REMINDER_ERROR_LABEL_LIST = "label_list";
        String MOMENT_REMINDER_LABEL = "type";
        String MOMENT_REPORT_CONTENT = "moment_report_content";
        String MOMENT_REPORT_HINT_CONTENT = "moment_report_hint_content";
        String MOMENT_TYPE = "moment_type";
        String MOMENT_VIDEO_NAME = "moment_video_name";
        String MOMENT_WATCHID = "moment_watchId";
        String OUT_TIME = "out_time";
        String PACKAGE_NAME = "packageName";
        String RESULT = "result";
        String SCROLL_TIME = "scroll_time";
        String SHARE_APP_NAME = "appname";
        String SHARE_FROM = "shareFrom";
        String SHARE_SCENE = "share";
        String SHARE_SUCCESS = "success";
        String SHARE_TIME = "time";
        String SHARE_TYPE = "shareType";
        String TIME = "time";
        String TYPE = "type";
        String VISIBLE_TIMING = "time";
        String VISIBLE_TYPE = "type";
        String WEB_URL = "webUrl";
    }

    /** How the moment module was started. */
    public interface StartType {
        String START_TYPE_CLICK = "click";
        String START_TYPE_DUER = "duer";
        String START_TYPE_FOR_WEICHAT = "weichat_start";
    }
}

package com.xtc.moment.module;

import com.xtc.moment.R;

import java.util.concurrent.TimeUnit;

/**
 * Central catalogue of the app's magic numbers, intent keys and provider
 * constants.
 *
 * <p>Grouped into nested interfaces by area, mirroring the stock layout, so a
 * call site reads as {@code Constants.PublishType.PHOTO} instead of a bare int.
 */
public interface Constants {

    int AI_TEXT_CLIENT_TYPE = 2;
    int DEFAULT_FLAG = 0;
    long DIFFER_TIME = 5000;
    String ENGLISH_PLURAL = "s";
    int ENGLISH_PLURAL_SIZE = 1;
    String FINISH_PLAY_VIDEO_ACTIVITY = "finish_play_video_activity";
    int FLAG_FROM_OS = 1;
    long HALF_HOUR = TimeUnit.MINUTES.toMillis(30);
    String HDPI = "hdpi";
    String INTENT_EXTRA_DB_TEMPLATE = "INTENT_EXTRA_DB_TEMPLATE";
    String INTENT_EXTRA_FRIEND_NAME = "INTENT_EXTRA_FRIEND_NAME";
    String INTENT_EXTRA_ICON_PATH = "INTENT_EXTRA_ICON_PATH";
    String INTENT_EXTRA_IS_SELF = "INTENT_EXTRA_IS_SELF";
    String INTENT_EXTRA_LIKE_TOTAL = "INTENT_EXTRA_LIKE_TOTAL";
    String INTENT_EXTRA_MOMENT_ID = "INTENT_EXTRA_MOMENT_ID";
    String INTENT_EXTRA_NAME = "INTENT_EXTRA_NAME";
    String INTENT_EXTRA_PUBLISH_TYPE = "INTENT_EXTRA_PUBLISH_TYPE";
    String INTENT_EXTRA_START_FROM_MOMENT = "INTENT_EXTRA_START_FROM_MOMENT";
    String INTENT_EXTRA_TEXT_CONTENT = "INTENT_EXTRA_TEXT_CONTENT";
    String INTENT_EXTRA_WATCH_ID = "INTENT_EXTRA_WATCH_ID";
    int MOMENT_LENGTH = 40;
    String MOMENT_OFFICIAL_ADVERT_ID = "advertId";
    String MOMENT_TAG = "XTC_MOMENT_";
    int MOMENT_WATCHID_OFFICiAL_LENGTH = 40;
    String PATH_ASSETS_RESOURCE = "video";
    String POSITION_LOCATE = "position_locate";
    int PUBLISH_REQUEST_CODE = 66;
    int PX_HEIGHT_DEFAULT_PUBLISH_BTN = 71;
    int PX_WIDTH_DEFAULT_PUBLISH_BTN = 225;
    int RETRY_COUNT = 2;
    int SCREEN_HEIGHT_GT = 360;
    int SCREEN_HEIGHT_ZX = 240;
    int SCREEN_WIDTH_GT = 320;
    int SCREEN_WIDTH_ZX = 240;
    String XHDPI = "xhdpi";

    /** Item view types used by the moment list adapters. */
    interface AdapterItemType {
        int EMPTY = 2;
        int FOOTER = 4;
        int HEADER = 3;
        int NORMAL = 1;
    }

    /** Barrage (danmaku) on/off state. */
    interface BarrageType {
        int CLOSE = 2;
        int OPEN = 1;
    }

    /** Binding information for the personality-dress service. */
    interface BindDressInfo {
        String BIND_ACTION = "com.xtc.personalitydress.service.DressService";
        String BIND_PACKAGE = "com.xtc.theme";
    }

    /** System broadcasts the app listens for. */
    interface BroadcastAction {
        String ACTION_ALARM_CLOCK = "com.xtc.alarmclock.action.ALARM_VIEW_SHOWING";
        String ACTION_CLASS_MODE = "com.xtc.setting.action.CLASS.ACTION";
        String ACTION_POWER_KEY = "com.xtc.i3launcher.module.powerkey.event.broadcast";
        String ACTION_VIDEO_CALL = "com.xtc.videochat.callin";
        String ACTION_WATCH_LOSS = "com.xtc.setting.WATCH.LOSS";
    }

    /** Camera capture geometry and media type. */
    interface Camera {

        interface PhotoSize {
            int HEIGHT = 990;
            int WIDTH = 880;
        }

        interface PhotoType {
            int LIVE_PHOTO = 2;
            int PHOTO = 0;
            int VIDEO = 1;
        }
    }

    /** Gradient colour pairs keyed by name. */
    interface ColorResource {
        int[] GRADIENT_BLUE = {R.color.color_2bdbff, R.color.color_1794fa};
        int[] GRADIENT_GRAY = {R.color.color_333333, R.color.color_333333};
        int[] GRADIENT_ORIG = {R.color.color_f7bd81c, R.color.color_f37e18};
    }

    /** Comment length limits. */
    interface CommentMaxLength {
        int MAX_LENGTH_10 = 10;
        int MAX_LENGTH_40 = 40;
    }

    /** Dress (theme) movement kinds. */
    interface DressMovementType {
        int DYNAMIC_DRESS = 2;
        int STATIC_DRESS = 1;
    }

    /** Enter-animation file kinds. */
    interface EnterAnimationFileType {
        int TYPE_ALPHA = 2;
        int TYPE_LOTTIE = 1;
    }

    /** Whether a purchased enter animation is in use. */
    interface EnterAnimationUseStatus {
        int NO_HAVE = 0;
        int NO_USING = 2;
        int USING = 1;
    }

    /** Bundled gift animation file names (under assets/video). */
    interface FileName {
        String SEND_EGG = "send_egg";
        String SEND_FLOWER = "send_flower";
    }

    /** Visibility scope of a moment. */
    interface FriendSwitchType {
        int ALL_FRIENDS_VISIBLE = 0;
        int NO_SET_VISIBLE = -1;
        int ONLY_MYSELF = 3;
        int PART_FRIENDS_ABLE_SEE = 1;
        int PART_FRIENDS_UNABLE_SEE = 2;
    }

    /** Fun-photo (short video) integration. */
    interface FunPhoto {
        String ACTION_NAME = "com.xtc.funphoto.FUN_VIDEO";
        String ACTION_VIDEO_NAME = "com.xtc.funphoto.FUN_VIDEO_RECORD";

        @Deprecated
        String FUN_PHOTO_CONTENT_URI = "content://com.xtc.funphoto.provider.funvideo/fun_video_available";
    }

    /** Gift kinds. */
    interface GiftType {
        int GIFT_TYPE_EGG = 2;
        int GIFT_TYPE_FLOWER = 1;
    }

    /** Input modes. */
    interface InputType {
        int INPUT_VOICE = 111;
    }

    /** Intent extra keys shared between activities. */
    interface IntentExtra {
        String ARGUED_ID = "argued_id";
        String ARGUED_NAME = "argued_name";
        String COMMENT_TYPE = "comment_type";
        String EXT_INFO_TYPE = "1";
        String MOMENT_ID = "moment_id";
        String MOMENT_TYPE = "moment_type";
        String MOMENT_WATCH_ID = "moment_watch_id";
        String REPORT_INFO_MOMENT = "report_info_moment";
        String REPORT_MOMENT = "report_moment";
        String VIDEO_SOURCE = "VIDEO_SOURCE";
        String VIDEO_THUMB_SOURCE = "VIDEO_THUMB_SOURCE";
    }

    /** Intent path keys used when returning media from the picker. */
    interface IntentPath {
        String IS_FROM_ALBUM = "isFromAlbum";
        String IS_FROM_OUTSIDE_JUMP = "is_from_outside_jump";
        String LBS_ADDRESS_POI_BEAN = "lbs_address_city_poi_bean";
        String LBS_FROM_LAUNCHER_ADDRESS_ID = "lbs_from_launcher_address_id";
        String PHOTO_PATH = "photo_path";
        String PHOTO_TYPE = "com.xtc.camera.EXTRA_PHOTO_TYPE";
        String PiCURE_PHOTO_PATH = "picture_photo_path";
        String VIDEO_PATH = "video_path";
    }

    /** Location request kinds. */
    interface LocationType {
        int LOCATION_TYPE_MOMENT = 41;
        int LOCATION_TYPE_POI = 116;
    }

    /** Whether an official moment is pinned to the top. */
    interface MomentOfficialTopType {
        int NOT_TOP = 0;
        int TOP = 1;
    }

    /** Picture-count sentinels for a moment. */
    interface MomentPhotoCount {
        String MAXIMUM_PICTURES = "9";
        String NO_PICTURES = "0";
    }

    /** Extra names for starting the moment module from outside. */
    interface MomentStartExtraName {
        String FRIEND_ID = "friend_id";
        String START_FROM_OS_WEI_CHAT = "start_moment_type";
    }

    /** Entry points into the moment module. */
    interface MomentStartType {
        int DEFAULT_TYPE = 200;
        int START_FRIEND_HOME_PAGE_ACTIVITY = 320;
        int START_NEW_MESSAGE_ACTIVITY = 310;
        int START_PUBLISH_ACTIVITY = 300;
    }

    /** Mood sub-types for the mood/state composer. */
    interface MoodSubType {
        int BORING = 0;
        int DAYLIY = 1;
        int FIGHTING = 7;
        int FIXED = 2;
        int HAPPY = 3;
        int OTHER = 4;
        int SAD = 5;
    }

    /** Multi-media composer behaviour codes. */
    interface MultiBehavior {
        int ADD_FUNTION_TYPE_ALBUM = 2;
        int ADD_FUNTION_TYPE_CAMERA = 1;
        int ADD_FUNTION_TYPE_VIDEO = 3;
        int CLICK_FRIEND_HEADER = 2;
        int CLICK_SELF_HEADER = 1;
        int PUSH_MOMENT_TYPE_MULTI_PICS = 27;
        int PUSH_MOMENT_TYPE_ONE_PIC = 26;
        int PUSH_MOMENT_TYPE_PICS = 28;
        int PUSH_MOMENT_TYPE_VIDEO = 29;
    }

    /** Whether a moment is from the official account. */
    interface OfficialType {
        int OFFICIAL_OTHER = 0;
        int OFFICIAL_XTC = 1;
    }

    /** Power instrumentation tags. */
    interface POWER {
        String POWER_ASR_PREFIX = "XtcPwrAsr time = ";
        String POWER_TAG = "PwrHistorian Moment";
    }

    /** Prerogative (paid theme) resource kinds. */
    interface PrerogativeEmotion {
        int ENTER_ANIMATION = 1;
        int MOMENT_BACKGROUND = 3;
        int MOMENT_LIKE = 2;
        int PERSONAL_INFO = 4;
    }

    /** Content-provider paths, codes and authorities. */
    interface ProviderConstants {
        String ALL_LIKE_MESSAGE_UNREAD = "likeMessageunread";
        int ALL_LIKE_MESSAGE_UNREAD_CODE = 302;
        int ALL_MOMENT_ITEM_CODE = 101;
        String ALL_MOMENT_ITEM_PATH = "item";
        int COMMENT_CODE = 201;
        String COMMENT_PATH = "comment";
        String COMMENT_PROVIDER_AUTHORITY = "com.xtc.moment.commentProvider";
        int COMMENT_UNREAD_CODE = 204;
        String COMMENT_UNREAD_PATH = "commentunread";
        String LIKE_MESSAGE_AUTHORITY = "com.xtc.moment.likeMessageProvider";
        int LIKE_MESSAGE_CODE = 301;
        String LIKE_MESSAGE_PATH = "likeMessage";
        int MOMENT_ITEM_LOCATION_CODE = 2;
        String MOMENT_ITEM_LOCATION_PATH = "location";
        int MOMENT_ITEM_MOOD_CODE = 0;
        String MOMENT_ITEM_MOOD_PATH = "mood";
        int MOMENT_ITEM_PHOTO_CODE = 5;
        String MOMENT_ITEM_PHOTO_PATH = "photo";
        int MOMENT_ITEM_STATE_CODE = 1;
        String MOMENT_ITEM_STATE_PATH = "state";
        int MOMENT_ITEM_VIDEO_CODE = 6;
        String MOMENT_ITEM_VIDEO_PATH = "video";
        int MOMENT_ITEM_WORD_CODE = 3;
        String MOMENT_ITEM_WORD_PATH = "word";
        String MOMENT_PROVIDER_AUTHORITY = "com.xtc.moment.momentProvider";
        int MOMENT_UNREAD_CODE = 106;
        String MOMENT_UNREAD_PATH = "momentunread";
        String PREFIX_CONTENT_PROVIDER = "content://";
        String SEPARATOR_CONTENT_PROVIDER = "/";
        int TEST_CODE = 100;
        String TEST_PATH = "testPath";
    }

    /** Dialog strings/ids for saving media from another app. */
    interface PublishMedia {
        String ACTION_PHOTO_SAVE_SUCCESS = "otherapp_photoSaveSuccess";
        String ACTION_PHOTO_TAKE_SUCCESS = "otherapp_photoTakeSuccess";
        String APP_NAME = "app_name";
        long DIALOGID = 36449471;
        String EXTRA_PHOTO_PATH = "photoPath";
        String LEFT_TEXT = "left_text";
        String MEDIA_TYPE = "media_type";
        String RIGHT_TEXT = "right_text";
        int SHOW_TYPE_PHOTO = 5;
        String VERSION = "version";
    }

    /** Publish kinds; drives which composer is opened. */
    interface PublishType {
        int ADVERTISE_PHOTO = 11;
        int ADVERTISE_PHOTO_H5 = 14;
        int ADVERTISE_PHOTO_TEXT = 12;
        int ADVERTISE_TEXT = 10;
        int ADVERTISE_VIDEO = 13;
        int AI_TEXT = 29;
        int COMMENT_TEXT = 21;
        int DUER_VOICE = 103;
        int LIST_PHOTO = 26;
        int LIVE_PHOTO = 22;
        int LOCATION = 2;
        int MAX_TYPE = 30;
        int MOOD = 0;
        int MULTI_SHARE_IMAGE = 28;
        int PHOTO = 5;
        int PICTURE = 4;
        int PRAISE = 20;
        int SHARE_APP = 9;
        int SHARE_H5 = 25;
        int SHARE_IMAGE = 8;
        int SHARE_LIVE_PHOTO = 23;
        int SHARE_TEXT = 7;
        int SHARE_VIDEO = 24;
        int STATE = 1;
        int VIDEO = 6;
        int VIDEO_CONTENT = 27;
        int VOICE = 3;
    }

    /** Content-provider query parameters. */
    interface QueryParameter {
        String DELETE_COMMENT_OR_CANCEL_LIKE = "202";
        String DELETE_MY_MOMENT = "102";
        String FRIEND_DELETE_MOMENT = "104";
        String FRIEND_PUBLISH_MOMENT = "103";
        String NEW_COMMENT_BY_CONTENT_PROVIDER = "200";
        String NEW_COMMENT_OR_LIKE_MESSAGE = "201";
        String NEW_LIKE_MESSAGE_BY_CONTENT_PROVIDER = "203";
        String NEW_MOMENT_BY_CONTENT_PROVIDER = "100";
        String PUBLISH_MOMENT = "101";
        String QUERY_PARAMETER_DATA = "data";
        String QUERY_PARAMETER_TYPE = "type";
    }

    /** Remote-config refresh kind. */
    enum RCType {
        INIT,
        REFRESH,
        REMOTE
    }

    /** Report reason kinds. */
    interface ReportInformType {
        int REPORT_OTHER = 2;
        int REPORT_RUMOR = 3;
        int REPORT_SEXY = 0;
        int REPORT_UNCIVILIZED = 1;
    }

    /** Report dialog layout kinds. */
    interface ReportType {
        int COMMON_TYPE = 3;
        int MULTIPLE_TYPE = 6;
    }

    /** Which confirm dialog is shown. */
    interface SHOW_DIALOG_TYPE {
        int CHANGE_RANGE_ONE = 3;
        int DELETE_AND_RANG = 13;
        int DELETE_ONE = 1;
    }

    /** Image scale modes for the moment photo view. */
    interface ScaleType {
        int CENTER_CROP = 2;
        int FIT_XY = 3;
        int MATRIX_BOTTOM = 1;
        int MATRIX_TOP = 0;
    }

    /** Where a shared video originated. */
    interface SendVideoFromType {
        int NORMAL = 0;
        int SHARE = 1;
    }

    /** Share timing/format helpers. */
    interface Share {
        String ACTION_EXTRA = "share";
        int TO_MINUTE = 60000;
        int TO_SECOND = 1000;
        String VIDEO_DURATION_PATTERN = "00";
    }

    /** Shared-video moment kinds. */
    interface ShareVideoCode {
        int MOMENT_TYPE_FUN_VIDEO = 2;
        int MOMENT_TYPE_NORMAL_SHARE_VIDEO = 0;
        int MOMENT_TYPE_POINT_VIDEO = 1;
    }

    /** Keys carried in a shared-video payload. */
    interface ShareVideoKey {
        String FUN_VIDEO_APP_ICON = "fun_video_app_icon";
        String FUN_VIDEO_APP_NAME = "fun_video_app_name";
        String FUN_VIDEO_APP_PACK_NAME = "fun_video_app_packName";
        String FUN_VIDEO_LENGTH = "fun_video_length";
        String FUN_VIDEO_PARAM = "fun_video_param";
        String FUN_VIDEO_PARAM_MATE_DATA = "fun.video.param";
        String FUN_VIDEO_PATH = "fun_video_path";
    }

    /** Shared-preferences keys. */
    interface SpConstant {
        String LIKE_RULES = "like_rules";
        String REPORT_INFORMATION_DATA = "report_information_data";
        String REPORT_IS_CAN_REQUEST = "report_is_can_request";
        String SP_KEY_FROM_WEI_CHAT = "fromweichat";
        String SP_KEY_LBS_ANIM_LIST = "lbs_anim_list";
        String SP_LAST_REPORT_MOMENT_ID = "sp_last_report_moment_id";
        String SP_MOMENT_HINT_PERMISSION = "moment_hint_permission";
        String SP_MOMENT_IS_RUNNING = "moment_isRunning";
        String SP_MOMENT_LOCATION_PERMISSION = "sp_moment_location_permission";
        String SP_MOMENT_PREROGATIVE_RESOURCE_BACKGROUND = "moment_prerogative_resource_background";
        String SP_MOMENT_PREROGATIVE_RESOURCE_LIKE = "moment_prerogative_resource_like";
        String SP_MOMENT_REFRESH_PREROGATIVE_RESOURCE_TIME = "moment_refresh_prerogative_resource_time";
        String SP_MOMENT_USING_PREROGATIVE_BACKGROUND = "moment_using_prerogative_background";
        String SP_MOMENT_USING_PREROGATIVE_LIKE = "moment_using_prerogative_like";
        String SP_PULL_REMINDER_CONFIG_TIME = "sp_pull_reminder_config_time";
    }

    /** Request codes for activities started for a result. */
    interface StartActivityOnResultCode {
        int MOMENT_FRIENDS_SEE_REQUEST_CODE = 6;
        int MOMENT_FRIENDS_VISIBLE_LIST = 7;
        int MOMENT_OPEN_ALBUM_REQUEST_CODE = 2;
        int MOMENT_OPEN_CAMERA_REQUEST_CODE = 1;
        int MOMENT_OPEN_CAMERA_VIDEO_REQUEST_CODE = 3;
        int MOMENT_OPEN_FUN_VIDEO_REQUEST_CODE = 4;
        int MOMENT_POI_REQUEST_CODE = 5;
    }

    /** Personal-state integration. */
    interface State {
        String APP_UPDATE_DATA = "market://details.uri.activity?id=";
        int CANCEL_STATE = 0;
        String CLASS_NAME = "com.xtc.state.home.StateLookActivity";
        String MY_STATE_DETAILS = "com.xtc.state.home.StateLookActivity";
        String PACKAGE_PERSONALCENTER = "com.xtc.personalcenter";
        String REFRESH_LIKES = "refresh_state_to_get_likes";
        int SET_STATE = 1;
        String STATE_NAME_STR = "personal_state_str";
    }

    /** Personal-state sub-types. */
    interface StateSubType {
        int STATE = 6;
    }

    /** Misc string keys. */
    interface Strings {
        String STRING_SOURCE = "source";
    }

    /** File suffix constants. */
    interface Suffix {
        String SUFFIX_AUDIO = ".amr";
        String SUFFIX_EDIT = "_edit";
        String SUFFIX_POINT = "_point";
        String SUFFIX_VIDEO_ANIM = ".mp4";
        String SUFFIX_ZIP = ".zip";
    }

    /** Visibility-range change kinds. */
    interface TYPE_VISIBLE_RANGE {
        int CHANGE_VISIBLE_RANGE = 2;
        int PUBLIC_VISIBLE_RANGE = 1;
    }

    /** Text sizes in sp. */
    interface TextSize {
        long SIZE_10 = 10;
        long SIZE_11 = 11;
        long SIZE_12 = 12;
        long SIZE_13 = 13;
        long SIZE_14 = 14;
        long SIZE_15 = 15;
        long SIZE_16 = 16;
    }

    /** Transfer states. */
    interface TransState {
        int SUCCESS = 0;
    }

    /** View visibility constants (used with a byte-typed API). */
    interface VisibleType {
        int GONE = 8;
        int INVISIBLE = 4;
        int VISIBLE = 0;
    }

    /** Query parameters understood by the H5 pages. */
    interface WebQueryParameter {
        String MOMENT_ID = "momentId";
        String MOMENT_WATCH_ID = "momentWatchId";
    }
}

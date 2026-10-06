package com.xtc.shareapi.share.constant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 分享开放 API 的常量集合，包含 Intent/Bundle 键名、分享类型、错误码与尺寸等约定。
 */
@Retention(RetentionPolicy.SOURCE)
public @interface OpenApiConstant {

    /** 是否支持多张实况照片。 */
    boolean MULTI_LIVE_PHOTO_SUPPORT = false;
    /** 是否支持多个视频。 */
    boolean MULTI_VIDEO_SUPPORT = false;
    /** 路径长度上限。 */
    int PATH_LENGTH_LIMIT = 512;
    /** 单次分享数量上限。 */
    int SHARE_COUNT_LIMIT = 9;
    /** 日志统一前缀。 */
    String TAG = "Share_";

    /** 应用相关常量。 */
    interface App {
        String CHAT_PACKAGE_NAME = "com.xtc.weichat";
        String LAUNCHER = "com.xtc.i3launcher";
        String LAUNCHER_CHAT_ACTIVITY = "com.xtc.open.share.chat.activity";
        String LAUNCHER_MOMENT_ACTIVITY = "com.xtc.open.share.moment.activity";
        String META_DATA_VERSION = "com.xtc.open.share.version";
        String META_DATA_XTCSERVICE_VERSION = "com.xtc.xws.apkCertificate.version";
        String MOMENT_MATA_LBS_ACTIVITY = "com.xtc.moment.jump.pushPictureActivity";
        String MOMENT_PACKAGE_NAME = "com.xtc.moment";
        String PACKAGE_TIME_MEMORY = "com.xtc.timememory";
    }

    /** 基础请求的 Bundle 键。 */
    interface BaseRequestConstant {
        String BASE_REQUEST_TRANSACTION = "xtc_share_base_request_transaction";
        String BASE_REQUEST_TYPE = "xtc_share_base_request_type";
    }

    /** 构建器相关键。 */
    interface BuilderConstant {
        String BUILDER_WEB_BUNDLE_MAP = "builder_web_bundle_map";
        String KEY_IDENTIFIER = "_wxobject_identifier_";
    }

    /** 相机/相册返回的额外参数。 */
    interface BundleExtra {
        int PHOTO = 0;
        int VIDEO = 1;
        int LIVE_PHOTO = 2;
        int MAX_LENGTH_40 = 40;
        String PHOTO_TYPE = "com.xtc.camera.EXTRA_PHOTO_TYPE";
        String SELECT_PHOTO_LIST = "com.xtc.camera.SELECT_PHOTO_LIST";
    }

    /** BundleMap 序列化键。 */
    interface BundleMap {
        String BUNDLE_MAP = "_xtc_api_serializable_map";
    }

    /** 图片裁剪弹窗参数常量。 */
    interface DialogBitmapArgsConstant {
        String DEBRIS_CROP_HEIGHT = "_xtc_api_dialog_debris_crop_height";
        String DEBRIS_CROP_WIDTH = "_xtc_api_dialog_debris_crop_width";
        String DEBRIS_CUT_START = "_xtc_api_dialog_debris_cut_start";
        String DEBRIS_CUT_TOP = "_xtc_api_dialog_debris_cut_top";
        String DEBRIS_HEIGHT = "_xtc_api_dialog_debris_height";
        String DEBRIS_TYPE = "_xtc_api_dialog_debris_type";
        String DEBRIS_WIDTH = "_xtc_api_dialog_debris_width";
        int DIALOG_HEIGHT_1 = 214;
        int DIALOG_HEIGHT_2 = 190;
        int DIALOG_HEIGHT_3 = 220;
        int DIALOG_HEIGHT_4 = 220;
        int DIALOG_WIDTH_1 = 190;
        int DIALOG_WIDTH_2 = 304;
        int DIALOG_WIDTH_3 = 240;
        int DIALOG_WIDTH_4 = 196;
        int TYPE_NORMAL = 1;
        int TYPE_INTELLIGENT = 2;
        int TYPE_FACE_SCORE = 3;
        int TYPE_DOUBLE_PK = 4;
        int TYPE_CUSTOM = 5;
    }

    /** 功能开关状态。 */
    interface FunSwitchStatue {
        int FUN_SWITCH_OPEN = 0;
        int FUN_SWITCH_CLOSE = 1;
    }

    /** Intent 传参键。 */
    interface IntentConstant {
        String INTENT_APP_ICON = "_xtc_api_app_icon";
        String INTENT_APP_JUMP_FLAG = "_xtc_api_app_jump_flag";
        String INTENT_APP_KEY = "_xtc_api_app_key";
        String INTENT_APP_NAME = "_xtc_api_app_name";
        String INTENT_APP_TOKEN = "_xtc_api_app_token";
        String INTENT_CHAT_SERVICE = "com.xtc.share.chat.service";
        String INTENT_CLASSNAME = "_xtc_api_class_name";
        String INTENT_PACKAGE = "_xtc_api_package";
        String INTENT_VERSION = "_xtc_api_version";
        String SERVICE_TIME_MEMORY = "com.xtc.share.timememory.service";
    }

    /** 消息图片参数常量。 */
    interface MessageBitmapArgsConstant {
        String DEBRIS_HEIGHT = "_xtc_api_message_debris_height";
        String DEBRIS_TYPE = "_xtc_api_message_debris_type";
        String DEBRIS_WIDTH = "_xtc_api_message_debris_width";
        int MESSAGE_HEIGHT_1 = 180;
        int MESSAGE_HEIGHT_2 = 156;
        int MESSAGE_HEIGHT_3 = 154;
        int MESSAGE_HEIGHT_4 = 160;
        int MESSAGE_WIDTH_1 = 160;
        int MESSAGE_WIDTH_2 = 250;
        int MESSAGE_WIDTH_3 = 168;
        int MESSAGE_WIDTH_4 = 142;
        int TYPE_NORMAL = 1;
        int TYPE_INTELLIGENT = 2;
        int TYPE_FACE_SCORE = 3;
        int TYPE_DOUBLE_PK = 4;
    }

    /** 模块开关常量。 */
    interface ModuleSwitch {
        int MODULE_SWITCH_MOMENT = 47;
        int MODULE_TIME_MEMORY = 2459;
        String SHARE_APP_MODULE = "content://com.xtc.share/switch";
        String XTC_MODULE_SWITCH_OPEN = "moduleSwitch";
        String XTC_MODULE_SWITCH_TIP = "moduleSwitchTip";
    }

    /** 跳转好友圈的 Intent 键。 */
    interface MomentIntentConstant {
        String IS_FROM_ALBUM = "isFromAlbum";
        String IS_FROM_OUTSIDE_JUMP = "is_from_outside_jump";
        String LBS_ADDRESS_POI_BEAN = "lbs_address_city_poi_bean";
        String LBS_FROM_LAUNCHER_ADDRESS_ID = "lbs_from_launcher_address_id";
        String LBS_TEXT = "lbs_text";
        String PHOTO_PATH = "photo_path";
        String PiCURE_PHOTO_PATH = "picture_photo_path";
        String SHARE_FROM_EXTRA = "share_from_extra";
        String VIDEO_PATH = "video_path";
    }
    /** 响应传参键。 */
    interface ResponseConstant {
        String BUNDLE_CONVERSATION_ID = "_xtc_api_conversation_id";
        String BUNDLE_CONVERSATION_TITLE = "_xtc_api_conversation_title";
        String BUNDLE_ERROR_CODE = "_xtc_api_response_code";
        String BUNDLE_ERROR_DESC = "_xtc_api_response_desc";
        String BUNDLE_ERROR_TRANSACTION = "_xtc_api_response_transation";
        String BUNDLE_SCENE_FROM_MOMENT = "_xtc_api_from_moment";
        String BUNDLE_SCENE_FROM_TYPE = "_xtc_api_from_type";
        String BUNDLE_SCENE_FROM_WEICHAT = "_xtc_api_from_weichat";
    }

    /** 场景参数键。 */
    interface SceneConstant {
        String BUNDLE_CHAT_CONVERSATION_LIST = "_xtc_api_conversationId_list";
        String BUNDLE_CHAT_FILTER_TIP = "_xtc_api_chat_filter_tip";
        String BUNDLE_CHAT_FRIEND_TYPE = "_xtc_api_chat_firend_type";
        String BUNDLE_CHAT_MODE_LIST = "_xtc_api_conversationMode_list";
        String BUNDLE_CHAT_OPENID_LIST = "_xtc_api_chat_openId_list";
        String BUNDLE_CHAT_SELECTION_MODE = "_xtc_api_chat_select_actionMode";
        String BUNDLE_SCENE_SHARE_TYPE = "_xtc_api_chat_share_type";
    }

    /** 场景安装状态。 */
    interface SceneInstall {
        int SCENE_INSTALL = 0;
        int WEI_CHAT_NOT_INSTALL = 1;
        int MOMENT_NOT_INSTALL = 2;
    }

    /** SDK 版本号常量。 */
    interface SdkVersionCode {
        int CHAT_VERSION_CODE = 1;
        int MOMENT_VERAION_CODE_SUPPORT_JUMP = 4;
        int MOMENT_VERSION_CODE = 1;
        String SDK_VERSION_CODE = "1";
    }

    /** 自启动相关常量。 */
    @interface SelfStart {
        String EXTRA_SELF_START = "EXTRA_SELF_START";
        String LAUNCHER_SELF_START_URI = "content://com.xtc.launcher.self.start";
        String METHOD_GET_PACKAGE_SELF_START = "METHOD_GET_PACKAGE_SELF_START";
    }

    /** 发送消息常量。 */
    interface SendMessageFromXTCConstant {
        String BUNDLE_MESSAGE_SHARE_ICON = "_xtc_api_share_icon";
        String BUNDLE_MESSAGE_SHARE_JUMP_FLAG = "_xtc_api_share_jump_flag";
        String BUNDLE_MESSAGE_SHARE_NAME = "_xtc_api_share_name";
        String BUNDLE_MESSAGE_SHARE_SENCE = "_xtc_api_share_scene";
        String BUNDLE_MESSAGE_SHARE_TYPE = "_xtc_api_share_type";
    }

    /** 云文件分享常量。 */
    interface ShareCloudFileConstant {
        String BUNDLE_FILE_DOWNLOAD_URL = "_xtc_api_share_cloud_file_download_url";
        String BUNDLE_FILE_HEIGHT = "_xtc_api_share_cloud_file_height";
        String BUNDLE_FILE_KEY = "_xtc_api_share_cloud_file_key";
        String BUNDLE_FILE_URL_DEADLINE = "_xtc_api_share_cloud_file_url_deadline";
        String BUNDLE_FILE_WIDTH = "_xtc_api_share_cloud_file_width";
    }

    /** 分享支持（应用鉴权）相关常量。 */
    @interface ShareSupport {
        int RESULT_UN_INIT = 0;
        int RESULT_SUCCESS = 1;
        int RESULT_NON_EXIST = 2;
        int RESULT_NOT_VERIFIED = 3;
        String URI_QUERY_ALL_APK_INFO = "content://com.xtc.share.support.provider.ShareSupportContentProvider/queryAllApkInfo";
        String URI_UPDATE_APK_INFO = "content://com.xtc.share.support.provider.ShareSupportContentProvider/updateApkInfo";
    }

    /** 展示消息常量。 */
    interface ShowMessageFromXTCConstant {
        String BUNDLE_MESSAGE_EXTEND = "_xtc_api_share_type";
        String BUNDLE_MESSAGE_SHARE_ICON = "_xtc_api_share_icon";
        String BUNDLE_MESSAGE_SHARE_NAME = "_xtc_api_share_name";
        String BUNDLE_MESSAGE_SHARE_SENCE = "_xtc_api_share_scene";
    }

    /** Token 相关常量。 */
    interface TokenConstant {
        String QUERY_SELECTION = "xtc_share_package = ?";
        String SHARE_APP_ALLOW = "xtc_share_allow";
        String SHARE_APP_PACKAGE = "xtc_share_package";
        String SHARE_APP_TOKEN = "xtc_share_token";
        String SHARE_APP_URI = "content://com.xtc.share/app";
        String SHARE_CHAT_URI = "content://com.xtc.share.chat/app";
        String SHARE_MOMENT_URI = "content://com.xtc.share.moment/app";
    }

    /** 应用扩展分享常量。 */
    interface XTCAppExtendConstant {
        String BUNDLE_EXTEND_INFO = "_xtc_api_share_app_content";
        String BUNDLE_START_ACTIVITY = "_xtc_api_share_app_start";
    }

    /** 图片分享常量。 */
    interface XTCImageConstant {
        String BUNDLE_IMAGE_DATA = "_xtc_api_share_image_data";
        String BUNDLE_IMAGE_DIALOG_BITMAP_ARGS = "_xtc_api_share_image_dialog_bitmap_args";
        String BUNDLE_IMAGE_MESSAGE_BITMAP_ARGS = "_xtc_api_share_image_message_bitmap_args";
        String BUNDLE_IMAGE_PATH = "_xtc_api_share_image_path";
        String BUNDLE_IMAGE_TEXT_MESSAGE = "_xtc_api_share_image_text_message";
    }

    /** 实况照片分享常量。 */
    interface XTCLivePhotoConstant {
        String BUNDLE_PHOTO_PATH = "_xtc_api_photo_path";
        String BUNDLE_VIDEO_PATH = "_xtc_api_video_path";
    }

    /** 多图分享常量。 */
    interface XTCMultiImageConstant {
        String BUNDLE_IMAGE_PATH_LIST = "_xtc_api_image_path_list";
    }

    /** 多张实况照片分享常量。 */
    interface XTCMultiLivePhotoConstant {
        String BUNDLE_LIVE_PHOTO_LIST = "_xtc_api_live_photo_list";
    }

    /** 多视频分享常量。 */
    interface XTCMultiVideoConstant {
        String BUNDLE_VIDEO_LIST = "_xtc_api_video_list";
    }

    /** 音乐分享常量。 */
    interface XTCMusicConstant {
        String BUNDLE_MUSIC_AUTHOR = "_xtc_api_share_music_author";
        String BUNDLE_MUSIC_DEFAULT_URL = "_xtc_api_share_music_url";
        String BUNDLE_MUSIC_DURATION = "_xtc_api_share_music_duration";
        String BUNDLE_MUSIC_EXTEND = "_xtc_api_share_music_ext_info";
        String BUNDLE_MUSIC_HIGH_URL = "_xtc_api_share_music_highUrl";
        String BUNDLE_MUSIC_LOW_URL = "_xtc_api_share_music_lowUrl";
        String BUNDLE_MUSIC_NAME = "_xtc_api_share_music_name";
        String BUNDLE_MUSIC_START_ACTIVITY = "_xtc_api_share_start_activity";
    }

    /** 分享应用名称。 */
    interface XTCShareAppName {
        String XTC_CHAT_APP_NAME = "微聊";
        String XTC_MOMENT_APP_NAME = "好友圈";
    }

    /** 分享消息常量。 */
    interface XTCShareMessageConstant {
        String BUNDLE_MESSAGE_ACTION = "_xtc_api_share_action";
        String BUNDLE_MESSAGE_ACTION_TYPE = "_xtc_api_share_actionType";
        String BUNDLE_MESSAGE_DESC = "_xtc_api_share_desc";
        String BUNDLE_MESSAGE_EXT = "_xtc_api_share_ext";
        String BUNDLE_MESSAGE_THUMB = "_xtc_api_share_thumb";
        String BUNDLE_MESSAGE_TITLE = "_xtc_api_share_title";
    }

    /** 社交视频平台类型。 */
    interface XTCShareSocialVideoType {
        String FACEBOOK = "1";
        String INSTAGRAM = "2";
        String STR_FACEBOOK = "Facebook";
        String STR_INS = "Instagram";
        String STR_YOUTUBE = "YouTube";
        String YOUTUBE = "0";
    }

    /** 分享类型。 */
    interface XTCShareType {
        int TEXT = 1;
        int IMAGE = 2;
        int APP = 3;
        int VIDEO = 4;
        int MUSIC = 5;
        int WEB = 6;
        int LIVE_PHOTO = 7;
        int SOCIAL_VIDEO = 8;
        int MULTI_IMAGE = 9;
        int MULTI_LIVE_PHOTO = 10;
        int MULTI_VIDEO = 11;
    }

    /** 文本分享常量。 */
    interface XTCTextConstant {
        String BUNDLE_TEXT = "_xtc_api_share_text_content";
    }

    /** 视频分享常量。 */
    interface XTCVideoConstant {
        String BUNDLE_VIDEO_CUSTOM_PARAM_MAP = "_xtc_api_share_video_customParamMap";
        String BUNDLE_VIDEO_DURATION = "_xtc_api_share_video_duration";
        String BUNDLE_VIDEO_EXTINFO = "_xtc_api_share_video_ext_info";
        String BUNDLE_VIDEO_ORIGIN_VIDEO_PATH = "_xtc_api_share_video_originVideoPath";
        String BUNDLE_VIDEO_SOURCE_DEADLINE = "_xtc_api_share_video_sourceDeadline";
        String BUNDLE_VIDEO_SOURCE_DOWNLOAD_URL = "_xtc_api_share_video_sourceDownloadUrl";
        String BUNDLE_VIDEO_SOURCE_KEY = "_xtc_api_share_video_sourceKey";
        String BUNDLE_VIDEO_START_ACTIVITY = "_xtc_api_video_start_activity";
        String BUNDLE_VIDEO_THUMBNAIL_DEADLINE = "_xtc_api_share_video_thumbnailDeadline";
        String BUNDLE_VIDEO_THUMBNAIL_DOWNLOAD_URL = "_xtc_api_share_video_thumbnailDownloadUrl";
        String BUNDLE_VIDEO_THUMBNAIL_KEY = "_xtc_api_share_video_thumbnailKey";
        String BUNDLE_VIDEO_THUMBNAIL_PATH = "_xtc_api_share_video_thumbnailPath";
        String BUNDLE_VIDEO_TYPE = "_xtc_api_share_video_type";
        String BUNDLE_VIDEO_VIDEO_DEADLINE = "_xtc_api_share_video_videoDeadline";
        String BUNDLE_VIDEO_VIDEO_DOWNLOAD_URL = "_xtc_api_share_video_videoDownloadUrl";
        String BUNDLE_VIDEO_VIDEO_KEY = "_xtc_api_share_video_videoKey";
        String BUNDLE_VIDEO_VIDEO_PATH = "_xtc_api_share_video_videoPath";
        String BUNDLE_VIDEO_WANGSU_URL = "_xtc_api_share_video_wangSuUrl";
        String BUNDLE_VIDEO_ZONE = "_xtc_api_share_video_zone";
    }

    /** 网页分享常量。 */
    interface XTCWebConstant {
        String BUILDER_WEB_MAP = "_xtc_api_share_web_map";
        String BUILDER_WEB_RTOS_SUPPORT = "_xtc_api_share_web_rtos_support";
        String BUNDLE_WEB_ARGS = "_xtc_api_share_web_args";
        String BUNDLE_WEB_EXTEND_INFO = "_xtc_api_share_web_content";
        String BUNDLE_WEB_URL = "_xtc_api_share_web_url";
    }
}
package com.xtc.web.core;

/** Constants shared by the H5 web-view bridge. */
public interface CoreConstants {

    String TAG = "WebCore_";

    /** Camera / gallery result constants. */
    interface BitmapConstant {
        String CAMERA_PATH = "/DCIM/Camera/";
    }

    /** Chrome-version parsing constants. */
    interface ChromeConstant {
        int CHROME_VERSION_86 = 86;
        int CHROME_VERSION_LENGTH = 2;
        int ERROR_INDEX = -1;
        int ONE = 1;
        int START_INDEX = 0;
        String TARGET_STR = "Chrome/";
    }

    /** Extras of the location-share H5 messages. */
    interface ExtraName {
        String EXTRA_CITY = "city";
        String EXTRA_CREATE_TIME = "createTime";
        String EXTRA_DESC = "desc";
        String EXTRA_LATITUDE = "latitude";
        String EXTRA_LOCAL_PIC_PATH = "localPicPath";
        String EXTRA_LONGITUDE = "longitude";
        String EXTRA_MSG_ID = "msgId";
        String EXTRA_PIC_URL = "picUrl";
        String EXTRA_POI = "poi";
        String EXTRA_PROVINCE = "province";
        String EXTRA_RADIUS = "radius";
        String EXTRA_REGION = "region";
        String EXTRA_ROAD = "road";
        String EXTRA_STREET = "street";
    }

    /** Local file constants. */
    interface FileConstant {
        String FILE_PREFIX = "https://androidstorage";
    }

    /** Local file types. */
    interface FileType {
        String TYPE_AAC = "aac";
        String TYPE_AMR = "amr";
    }

    /** Jump targets of the dial-store / theme apps. */
    interface JumpConstant {
        String COM_XTC_DIALSTORE = "com.xtc.dialstore";
        String COM_XTC_DIALSTORE_TARGET_ACTIVITY = "com.xtc.dialstore.module.MainActivity";
        String COM_XTC_THEME = "com.xtc.theme";
        String DIALSTORE_URI = "#Intent;component=com.xtc.dialstore/com.xtc.dialstore.module.MainActivity;end";
        String THEME_DIAL_URI = "#Intent;component=com.xtc.theme/com.xtc.dialstore.module.mine.view.MineDialActivity;end";
    }

    /** Mime types used when sharing local files. */
    interface MimeType {
        String TYPE_AMR = "application/octet-stream";
    }

    /** Module switch codes of the web-view cache. */
    interface ModuleSwitch {
        int MODULE_SWITCH_CACHE = 2463;
    }

    /** System properties read by the web-view bridge. */
    interface SystemProp {
        String H5_PROP = "persist.sys.support.H5";
        String IS_TRUE = "true";
    }

    /** Types of the system-property values. */
    interface SystemPropertyConstant {
        int TYPE_BOOLEAN = 0;
        int TYPE_INT = 2;
        int TYPE_LONG = 3;
        int TYPE_STRING = 1;
    }

    /** Extras of the take-photo request. */
    interface TakePhotoConstant {
        String LEFT_BUTTON_TEXT = "com.xtc.camera.LEFT_BUTTON_TEXT";
        String REQUEST = "REQUEST";
        int REQUEST_CAMERA = 1;
        String REQUEST_KEY = "requestKey";
        int REQUEST_PHOTO = 2;
        String RIGHT_BUTTON_TEXT = "com.xtc.camera.RIGHT_BUTTON_TEXT";
        String TARGET_APP = "targetApp";
        String TARGET_INTENT = "targetIntent";
        String TYPE_IMAGE = "image/*";
    }
}
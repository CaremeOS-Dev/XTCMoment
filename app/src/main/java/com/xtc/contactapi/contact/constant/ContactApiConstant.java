package com.xtc.contactapi.contact.constant;

import android.net.Uri;
import android.os.Environment;


import com.xtc.dataservice.api.SessionConstant;

import java.io.File;

/**
 * 联系人 API 常量，包含内容提供者地址、广播、错误码与默认头像资源名等。
 */
public class ContactApiConstant {

    /** 联系人内容提供者 authority。 */
    public static final String AUTHORITY = "com.xtc.contact";
    /** 排序字段。 */
    public static final String SORT_SN = "sortSN";
    /** item 路径。 */
    public static final String ITEM = "item";
    /** item/# 路径。 */
    public static final String ITEM_ID = "item/#";
    /** pos/# 路径。 */
    public static final String POS_ID = "pos/#";
    /** item/serverId/* 路径。 */
    public static final String ITEM_SERVER_ID = "item/serverId/*";

    public static final int TYPE_ADD = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_REMOVE = 3;
    public static final int TYPE_REFRESH = 4;
    public static final int TYPE_QUERY = 5;
    public static final int TYPE_QUERY_ONE = 6;

    /** 默认头像资源名前缀。 */
    public static final String DEFAULT_PORTRAIT = "default_portrait_";

    public static final Uri CONTACT_URI = Uri.parse("content://com.xtc.contact/item");
    public static final Uri CONTACT_SERVER_URI = Uri.parse("content://com.xtc.contact/item/serverId");

    /** 模块名。 */
    public static String MODULE_LAUNCHER = "launcher";
    /** 联系人数据源名。 */
    public static String SOURCE_CONTACT = "contact";
    /** 联系人头像数据源名。 */
    public static String SOURCE_CONTACT_HEAD = "contact_head";

    /** 联系人头像缓存目录。 */
    public static String CONTACT_HEAD_DIR = Environment.getExternalStorageDirectory().getPath()
            + File.separator + SessionConstant.Source.XTC + File.separator + "ibwatch"
            + File.separator + "launcher" + File.separator + SOURCE_CONTACT
            + File.separator + "contact_head";
    /** 头像文件后缀。 */
    public static String HEAD_FILE_SUFFIX = ".png";
    /** 头像文件后缀（分享图片）。 */
    public static String SHARE_PIC_SUFFIX = "/my_icon.jpg";

    /** 分享图片全路径。 */
    public static String SHARE_PIC_PATH;
    /** 头像版本 v112。 */
    public static String HEAD_VERSION_V112;
    /** 头像版本 v113。 */
    public static String HEAD_VERSION_V113;
    /** 头像版本字段名。 */
    public static String HEAD_VERSION_KEY;

    static {
        SHARE_PIC_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator
                + SessionConstant.Source.XTC + File.separator + "ibwatch" + File.separator + "sharepic"
                + SHARE_PIC_SUFFIX;
        HEAD_VERSION_V112 = "v112";
        HEAD_VERSION_V113 = "v113";
        HEAD_VERSION_KEY = "HeadVersion";
    }

    /** 联系人变化广播。 */
    public interface ContactReceiver {
        String ACTION_ADD = "com.xtc.contactapi.module.contact.add.broadcast";
        String ACTION_UPDATE = "com.xtc.contactapi.module.contact.update.broadcast";
        String ACTION_REMOVE = "com.xtc.contactapi.module.contact.remove.broadcast";
        String EXTRA_CONTACT_BEAN = "conact_bean";
        int TYPE_ADD = 0;
        int TYPE_UPDATE = 1;
        int TYPE_REMOVE = 2;
    }

    /** 错误描述。 */
    public interface ContctApiErrorDev {
        String QUERY_FAIL = "数据查询失败,请确认是否拥有查询权限";
        String PARAM_EMPTY = "参数为空，请检查";
        String QUERY_FRIEND_FAIL = "查询是否为好友失败，查询游标为空或游标数据为空";
    }

    /** 响应码。 */
    public interface ContctApiResponseCode {
        int SUCCESS = 200;
        int NO_DATA = 201;
        int ERROR = 500;
        int INIT_COMPLETE = 10;
        int CONTEXT_NULL = 11;
        int DISCONNECTED = 12;
        int PRELOAD_COMPLETE = 15;
        int PRELOAD_FAIL = 16;
    }

    /** 转换码。 */
    public interface ConvertCode {
        /** 与调用线程同步回调。 */
        int SYNC_THREAD = 1;
        /** 在主线程回调。 */
        int MAIN_THREAD = 2;
    }

    /** 角色类型。 */
    public interface IRoleType {
        int NONE = 0;
        int BROTHER = 1;
        int FRIEND = 2;
        int FATHER = 3;
        int GRANDFATHER = 4;
        int GRANDPA = 5;
        int GRANDMOTHER = 6;
        int GRANDMA = 7;
        int MOTHER = 8;
        int SISTER = 9;
        int STRANGER = 10;
        String PORTRAIT_BROTHER = "default_portrait_brother";
        String PORTRAIT_DEFAULT = "default_portrait_default";
        String PORTRAIT_FRIEND = "default_portrait_friend";
        String PORTRAIT_FATHER = "default_portrait_father";
        String PORTRAIT_GRANDFATHER = "default_portrait_grandfather";
        String PORTRAIT_GRANDPA = "default_portrait_grandpa";
        String PORTRAIT_GRANDMOTHER = "default_portrait_grandmother";
        String PORTRAIT_GRANDMA = "default_portrait_grandma";
        String PORTRAIT_MOTHER = "default_portrait_mother";
        String PORTRAIT_SISTER = "default_portrait_sister";
        String PORTRAIT_STRANGER = "default_portrait_stranger";
        String PORTRAIT_TEACHER = "default_portrait_teacher";
    }
}
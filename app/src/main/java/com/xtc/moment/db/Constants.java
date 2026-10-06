package com.xtc.moment.db;

/** Database constants of the moment app. */
public interface Constants {

    String DATABASE_NAME = "moment.db";

    /** Error codes shared with the http layer. */
    interface ErrorCode {
        String FREQUENT_REQUEST = "1003";
        String NET_WORK = "1005";
    }

    /** Keep-alive notification constants. */
    interface KeepAlive {
        int KEEP_DURATION = 10000;
        int NOTIFICATION_ID = 22;
        String TAG_PRE = "keep_pre_";
    }

    /** Physical table names. */
    interface TableName {
        String ADVERT_CLOSE_RECORD = "advert_close_record";
        String ILLEGAL_RECORD = "db_illegal_record";
        String LIKE_MESSAGE_INFO = "like_message_info";
        String MOMENT = "moment";
        String MOMENT_COMMENT = "moment_comment";
        String MOMENT_GIFT_RECORD = "gift_record";
        String MOMENT_IM_REMINDER = "moment_im_reminder";
        String MOMENT_PREROGATIVE_BACKGROUND = "prerogative_background";
        String MOMENT_PREROGATIVE_LIKE = "prerogative_like";
        String MOMENT_REMINDER_CONFIG = "moment_reminder_config";
        String MOMENT_SEXY_PHOTO_DISTINGUISH = "moment_sexy_photo_distinguish";
        String MOMENT_TEMPLATE = "moment_template";
        String MOMENT_VISIBLE = "moment_visible";
    }
}
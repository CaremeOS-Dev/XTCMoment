package com.xtc.moment.module.personalinfo.constant;

/** Personal-centre integration constants shared with the moment module. */
public interface PersonalInfoConstant {
    int BADGE_WEB_TYPE = 160;
    int DEFAULT_GENDER = 3;
    int GENDER_BOY = 0;
    int GENDER_GIRL = 1;
    String URI_QUERY_FRIEND_GRADE_INFO = "content://com.xtc.grade.provider/queryFriendInfoByWatchId";
    String URI_QUERY_PERSONAL_BG_PATH = "content://com.xtc.personalcenter.prerogativeContentProvider/queryPrerogativeBgById";
    String URI_QUERY_SINGLE_CONVERSATION = "content://com.xtc.weichat.conversationContentProvider/querySingleConversation";

    /** Entry points into the personal-centre app. */
    interface PersonalMainIntent {
        String PERSONAL_ACTIVITY = "com.xtc.setting.module.personalcentre.module.home.PersonalMainActivity";
        String PERSONAL_ACTIVITY_NEW = "com.xtc.business.personalcenter.module.splash.SplashActivity";
        String PERSONAL_PACKAGE_NAME = "com.xtc.personalcenter";
        String SETTING_PACKAGE_NAME = "com.xtc.setting";
    }

    /** Entry points into the WeChat app. */
    interface WeiChatIntent {
        String INTENT_ACTION = "com.xtc.weichat.module.view.activity.ChatActivity";
        String KEY_CONVERSATION = "dbConversation";
        String KEY_CONVERSATION_SAVED = "dbConversation_saved";
        String WEICHAT_ACTIVITY = "com.xtc.weichat.module.WeiChatActivity";
        String WEICHAT_PACKAGE_NAME = "com.xtc.weichat";
    }
}

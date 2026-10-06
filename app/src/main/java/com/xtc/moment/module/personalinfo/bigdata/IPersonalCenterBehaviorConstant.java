package com.xtc.moment.module.personalinfo.bigdata;

public interface IPersonalCenterBehaviorConstant {

    String CLICK_FRIEND_MOMENT = "account_center_click_friend_moment";
    String CLICK_MORE = "account_center_click_more";
    String CLICK_SELF_BADGE = "account_center_click_self_badge";
    String CLICK_SELF_VIRTUALSELF = "account_center_click_self_virtualself";
    String DELETE_FRIEND = "account_center_delete_friend";
    String EDIT_SELF_INFO = "account_center_edit_self_info";
    String ENTER_LIKE_RECORD = "account_center_enter_like_record";
    String ENTER_PERSONAL_CENTER = "account_center_enter_personal_center";
    String LIKE_FRIEND_COUNT = "account_center_like_friend_count";
    String LIKE_FRIEND_LIMIT = "account_center_like_friend_limit";
    String LIKE_FRIEND_WAY = "account_center_like_friend_way";
    String PULL_DOWN_VIRTUALSELF = "account_center_pull_down_virtualself";
    String SET_FRIEND_NOTES = "account_center_set_friend_notes";
    String SET_SIGNATURE = "account_center_set_signature";
    String VIEW_FRIEND_BADGE = "account_center_view_friend_badge";
    String VIEW_FRIEND_PAGE = "account_center_view_friend_page";

    interface ClickMoreType {
        int AUTHORIZED_APPLICATION = 2;
        int LOCAL_NUM = 1;
        int NEW_LOGIN = 3;
    }

    interface DeleteFriendWay {
        int MOMENT = 2;
        int PERSONAL_CENTER = 3;
        int WEICHAT = 1;
    }

    interface Key {
        String COUNT = "count";
        String TYPE = "type";
        String WAY = "way";
    }

    interface LikeFriendWay {
        int FRIEND_PAGE = 2;
        int LIKE_RECORD = 1;
    }

    interface ViewFriendPageWay {
        int CLASS_CHAT = 4;
        int GROUP_CHAT = 3;
        int LIKE_RECORD = 5;
        int MOMENT_HEAD = 2;
        int SINGLE_CHAT = 1;
    }
}
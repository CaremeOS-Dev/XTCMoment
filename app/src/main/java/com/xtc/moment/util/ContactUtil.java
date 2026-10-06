package com.xtc.moment.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.bean.Friend;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友数据工具：从联系人 Provider 读取并转换成动态模块的 Friend。
 */
public class ContactUtil {

    private static final String TAG = "XTC_MOMENT_ContactUtil";
    private static final String AUTHORITY = "content://com.xtc.contact/item_all_column";
    private static final int FRIEND_SHORT_NUMBER = 5;

    private ContactUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static List<Friend> getFriendList(Context context) {
        if (context == null) {
            LogUtil.e(TAG, "getFriendList: context is null!");
            return null;
        }
        List<Friend> friendList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse(AUTHORITY), null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                Friend friend = new Friend();
                friend.setWatchId(cursor.getString(cursor.getColumnIndex("friend_watch_id")));
                friend.setName(cursor.getString(cursor.getColumnIndex("name")));
                friend.setFriendIcon(cursor.getString(cursor.getColumnIndex("photo_path")));
                friendList.add(friend);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "getFriendList: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return friendList;
    }

    public static List<Friend> convertToFriend(List<ContactBean> contactBeanList) {
        if (contactBeanList == null) {
            LogUtil.e(TAG, "getFriendList: contactBean is null!");
            return null;
        }
        List<Friend> friendList = new ArrayList<>();
        for (ContactBean contactBean : contactBeanList) {
            Friend friend = convertToFriend(contactBean);
            if (friend != null) {
                friendList.add(friend);
            }
        }
        return friendList;
    }

    public static Friend convertToFriend(ContactBean contactBean) {
        if (contactBean == null) {
            LogUtil.e(TAG, "getFriendList: contactBean is null!");
            return null;
        }
        Friend friend = new Friend();
        friend.setWatchId(contactBean.getFriendWatchId());
        friend.setName(contactBean.getName());
        friend.setFriendIcon(contactBean.getPhotoPath());
        LogUtil.i(TAG, "convertToFriend: friendInfo = [" + friend + "]");
        return friend;
    }

    public static List<Friend> convertToFriendWithPhoneAccount(List<ContactBean> contactBeanList) {
        if (contactBeanList == null) {
            LogUtil.e(TAG, "getFriendList: contactBean is null!");
            return null;
        }
        List<Friend> friendList = new ArrayList<>();
        for (ContactBean contactBean : contactBeanList) {
            Friend friend = convertToFriend(contactBean);
            if (contactBean.getType().intValue() != FRIEND_SHORT_NUMBER
                    && friend != null
                    && !TextUtils.isEmpty(friend.getWatchId())) {
                friendList.add(friend);
            }
        }
        return friendList;
    }
}
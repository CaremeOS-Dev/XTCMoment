package com.xtc.moment.serve;

import android.content.Context;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.interfaces.ContactChangeListener;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.util.ContactUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友信息服务：缓存通讯录转换后的好友列表，并在联系人变化时刷新。
 */
public class FriendInfoServeImpl extends BaseServe implements ContactChangeListener, IFriendInfoServe {

    private static final String TAG = "FriendInfoServeImpl";

    private ContactManager contactManager;
    private List<Friend> friends;

    public FriendInfoServeImpl(Context context) {
        super(context);
        ServerCache.putBusinessServer(this);
        this.contactManager = ContactManager.getInstance(context);
        this.contactManager.registerContactChangeListener(this, 1);
    }

    public static IFriendInfoServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, FriendInfoServeImpl.class);
    }

    @Override
    public List<Friend> getAllFriendInfo() {
        return getAllFriendInfo(false);
    }

    private List<Friend> getAllFriendInfo(boolean isUpdate) {
        LogUtil.d(TAG, "getAllFriendInfo: isUpdate = [" + isUpdate + "]");
        if (this.contactManager == null) {
            return new ArrayList<>();
        }
        if (this.friends == null || isUpdate) {
            this.friends = ContactUtil.convertToFriend(this.contactManager.getAllContactsSync());
        }
        return this.friends;
    }

    @Override
    public void onContactUpdate(ContactBean contactBean) {
        LogUtil.d(TAG, "onContactUpdate : name = [" + getContactName(contactBean) + "]");
        onContactChange();
    }

    @Override
    public void onContactRemove(ContactBean contactBean) {
        LogUtil.d(TAG, "onContactRemove : name = [" + getContactName(contactBean) + "]");
        onContactChange();
    }

    @Override
    public void onContactAdd(ContactBean contactBean) {
        LogUtil.d(TAG, "onContactAdd : name = [" + getContactName(contactBean) + "]");
        onContactChange();
    }

    @Override
    public void onContactsRefresh(List<ContactBean> contacts) {
        LogUtil.d(TAG, "onContactsRefresh");
        onContactChange();
    }

    @Override
    public void onContactChange() {
        getAllFriendInfo(true);
        EventBus.getDefault().post(new EventType(2));
    }

    public String getContactName(ContactBean contactBean) {
        return contactBean == null ? "" : contactBean.getName();
    }

    @Override
    public String getDefaultHead() {
        return this.contactManager.getDefaultPortraitPath(this.mContext);
    }
}
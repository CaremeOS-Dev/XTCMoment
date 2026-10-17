package com.xtc.moment.serve;

import android.content.Context;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.interfaces.ContactChangeListener;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.GetFriendListBean;
import com.xtc.moment.util.ContactUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 好友信息服务：缓存通讯录转换后的好友列表，并在联系人变化时刷新。
 */
public class FriendInfoServeImpl extends BaseServe implements ContactChangeListener, IFriendInfoServe {

    private static final String TAG = "FriendInfoServeImpl";

    private ContactManager contactManager;
    private List<Friend> friends;
    private MomentHttpServiceProxy momentHttpServiceProxy;

    public FriendInfoServeImpl(Context context) {
        super(context);
        ServerCache.putBusinessServer(this);
        this.contactManager = ContactManager.getInstance(context);
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
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
            // 本机通讯录 provider 可能没有好友数据（新装机、未同步联系人），
            // 这时回退到自建服务端的好友列表；本地有数据时保持原逻辑不变。
            if (this.friends == null || this.friends.isEmpty()) {
                loadFriendsFromServer();
            }
        }
        return this.friends;
    }

    /** 异步从服务端补好友列表，成功后替换本地缓存并通知界面刷新。 */
    private void loadFriendsFromServer() {
        if (this.momentHttpServiceProxy == null) {
            return;
        }
        GetFriendListBean body = new GetFriendListBean();
        body.setWatchId(MomentApp.getWatchId());
        this.momentHttpServiceProxy.getFriendList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "loadFriendsFromServer#error", throwable);
                    }

                    @Override
                    public void onNext(List<Friend> serverFriends) {
                        if (serverFriends == null || serverFriends.isEmpty()) {
                            return;
                        }
                        FriendInfoServeImpl.this.friends = serverFriends;
                        EventBus.getDefault().post(new EventType(2));
                    }
                });
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

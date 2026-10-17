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
import com.xtc.moment.util.HandlerUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.schedulers.Schedulers;

/**
 * 好友信息服务。
 *
 * <p>好友来源按优先级取：先问自建服务端（{@code /extra/getFriendList}），
 * 服务端拿不到或返回空时才回退到本机通讯录 provider。调用方
 * （动态过滤、可见范围、点赞列表、分享列表）都依赖本方法同步返回结果，
 * 因此服务端请求在这里阻塞等待，并带一个较短的超时兜底。</p>
 */
public class FriendInfoServeImpl extends BaseServe implements ContactChangeListener, IFriendInfoServe {

    private static final String TAG = "FriendInfoServeImpl";

    /** 服务端好友请求的最长等待时间，超时即回退本机通讯录。 */
    private static final long SERVER_TIMEOUT_MILLIS = 3000L;

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
            List<Friend> serverFriends = loadFriendsFromServer();
            if (serverFriends != null && !serverFriends.isEmpty()) {
                this.friends = serverFriends;
            } else {
                // 服务端没有好友数据时才走本机通讯录 provider。
                this.friends = ContactUtil.convertToFriend(this.contactManager.getAllContactsSync());
            }
        }
        return this.friends;
    }

    /**
     * 同步向服务端请求好友列表。
     *
     * <p>返回 {@code null} 表示请求失败或超时，调用方据此回退本机通讯录。</p>
     */
    private List<Friend> loadFriendsFromServer() {
        if (this.momentHttpServiceProxy == null) {
            return null;
        }
        try {
            GetFriendListBean body = new GetFriendListBean();
            body.setWatchId(MomentApp.getWatchId());
            List<Friend> serverFriends = this.momentHttpServiceProxy.getFriendList(body)
                    .subscribeOn(Schedulers.io())
                    .timeout(SERVER_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                    .toBlocking()
                    .firstOrDefault(null);
            LogUtil.i(TAG, "loadFriendsFromServer size = "
                    + (serverFriends == null ? "null" : serverFriends.size()));
            return serverFriends;
        } catch (Exception e) {
            LogUtil.e(TAG, "loadFriendsFromServer#error", e);
            return null;
        }
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
        // 联系人变化回调在主线程，不能在这里阻塞等网络；放到后台刷新，
        // 完成后重新通知界面。刷新期间继续使用上一份缓存。
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                getAllFriendInfo(true);
                EventBus.getDefault().post(new EventType(2));
            }
        });
    }

    public String getContactName(ContactBean contactBean) {
        return contactBean == null ? "" : contactBean.getName();
    }

    @Override
    public String getDefaultHead() {
        return this.contactManager.getDefaultPortraitPath(this.mContext);
    }
}

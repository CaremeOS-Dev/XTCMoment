package com.xtc.moment.module.publish.visible.friends;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.util.ContactUtil;

import java.util.List;

/**
 * Presenter of the "visible to friends" page: exposes the full contact list converted to friends.
 */
public class VisibleFriendListPresenter extends MvpPresenter<IVisibleFrienListView> {

    private static final String TAG = "VisibleTypePresenter";

    private final Context context;
    private final ContactManager contactManager;

    public VisibleFriendListPresenter(Context context) {
        this.context = context;
        this.contactManager = ContactManager.getInstance(context);
    }

    public List<Friend> getAllFriend() {
        return ContactUtil.convertToFriendWithPhoneAccount(this.contactManager.getAllContactsSync());
    }
}
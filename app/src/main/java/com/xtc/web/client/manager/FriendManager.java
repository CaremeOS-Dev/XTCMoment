package com.xtc.web.client.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.data.FriendInfo;
import com.xtc.web.client.data.response.RespFriendInfo;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.callback.LifecycleCallbacks;
import com.xtc.web.core.manager.LifecycleDispatcher;

import java.util.ArrayList;
import java.util.List;

/** 好友列表查询：先校验联系人权限，无权限时跳转授权页。 */
public class FriendManager extends LifecycleCallbacks {

    private static final int BASE_LEVEL = 1;
    private static final int CODE_CONTACT = 3259;
    private static final String CONTACT_PERMISSION = "content://com.xtc.contact.permission/item";
    private static final String FRIEND_URL = "content://com.xtc.contact/item_base_column";
    private static final String TAG = Constants.TAG + FriendManager.class.getSimpleName();

    private Context context;
    private CompletionHandler<RespFriendInfo> handler;

    /** 请求好友列表。 */
    public void request(Context context, CompletionHandler<RespFriendInfo> completionHandler) {
        this.context = context;
        this.handler = completionHandler;
        if (!queryContactPermission()) {
            requestPermission();
        } else {
            sendSuccess();
        }
    }

    private void sendSuccess() {
        List<FriendInfo> friendList = queryFriendList();
        RespFriendInfo response = new RespFriendInfo();
        response.setCode(RespFriendInfo.Code.SUCCESS);
        response.setData(friendList);
        this.handler.complete(response);
    }

    /** 拉起联系人授权页。 */
    private void requestPermission() {
        LogUtil.d(TAG, "getLocalImage permission!");
        Intent intent = new Intent();
        intent.setAction("com.xtc.contact.module.share.view.ShareContactPermissionActivity");
        intent.putExtra("pkg_name_extra", this.context.getPackageName());
        intent.putExtra("app_name_extra", this.context.getApplicationInfo().name);
        if (this.context.getPackageManager().resolveActivity(intent, 65536) != null) {
            try {
                LifecycleDispatcher.getInstance().registerCallback(this);
                ((Activity) this.context).startActivityForResult(intent, CODE_CONTACT);
                return;
            } catch (Exception e) {
                LogUtil.d(TAG, "getLocalImage permission error = " + e);
                notSupport();
                return;
            }
        }
        LogUtil.d(TAG, "getLocalImage permission false!");
        notSupport();
    }

    private void notSupport() {
        LifecycleDispatcher.getInstance().unRegisterCallback(this);
        RespFriendInfo response = new RespFriendInfo();
        response.setCode(RespFriendInfo.Code.NOT_SUPPORT);
        this.handler.complete(response);
    }

    /** 从联系人 Provider 读取好友列表。 */
    private List<FriendInfo> queryFriendList() {
        ArrayList<FriendInfo> friendList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = this.context.getApplicationContext().getContentResolver()
                    .query(Uri.parse(FRIEND_URL), null, "openID != ?", new String[]{"null"}, null);
            while (cursor != null && cursor.moveToNext()) {
                FriendInfo friendInfo = new FriendInfo();
                friendInfo.setOpenId(cursor.getString(cursor.getColumnIndex("openID")));
                friendInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                friendInfo.setIcon(cursor.getString(cursor.getColumnIndex("photo_path")));
                friendList.add(friendInfo);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "get friend info exception：" + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        LogUtil.d(TAG, "get friend info success = " + friendList);
        return friendList;
    }

    /** 查询当前应用是否已获得联系人权限。 */
    private boolean queryContactPermission() {
        Cursor cursor = null;
        try {
            cursor = this.context.getApplicationContext().getContentResolver()
                    .query(Uri.parse(CONTACT_PERMISSION), null, "pkgName=?",
                            new String[]{this.context.getPackageName()}, null);
            if (cursor == null || !cursor.moveToNext()) {
                return false;
            }
            return cursor.getInt(cursor.getColumnIndex("permissionLevel")) >= BASE_LEVEL;
        } catch (Exception e) {
            LogUtil.e(TAG, "get friend info exception：" + e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            sendSuccess();
        } else {
            RespFriendInfo response = new RespFriendInfo();
            response.setCode(RespFriendInfo.Code.NOT_AUTHOR);
            this.handler.complete(response);
        }
        LifecycleDispatcher.getInstance().unRegisterCallback(this);
    }
}
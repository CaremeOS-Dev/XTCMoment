package com.xtc.moment.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.serve.AccountInfoServerImpl;

/**
 * 联系人/个人信息变更广播接收器。
 */
public class InitServiceSyncMomentReceiver extends BroadcastReceiver {

    private static final String CONTACT_BROADCAST_UPDATE_ACTION = "com.xtc.contact.update";
    private static final String PERSONAL_INFO_CHANGE_ACTION = "com.xtc.personalcenter.infochange";
    private static final String INFO_TYPE = "info_type";
    private static final int INFO_TYPE_NAME = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (CONTACT_BROADCAST_UPDATE_ACTION.equals(action)) {
            LogUtil.i("moment", ">>>>>>> 好友圈接收到联系人变更通知 >>>>>>>>>>");
            MomentApp.initFriendInfo(context);
        }
        if (PERSONAL_INFO_CHANGE_ACTION.equals(action) && intent.getIntExtra(INFO_TYPE, 0) == INFO_TYPE_NAME) {
            LogUtil.i("moment", ">>>>>>> 从个人中心修改昵称 >>>>>>>>>>");
            AccountInfoServerImpl.getInstance(context).resetToInit();
        }
    }
}
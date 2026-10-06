package com.xtc.contactapi.contact.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.constant.ContactApiConstant;

/**
 * 联系人变化广播接收基类，子类按需覆写新增/更新/删除回调。
 */
public class BaseContactChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "ContactChangeReceiver";

    protected void onContactAdd(Context context, ContactBean contactBean) {
    }

    protected void onContactRemove(Context context, ContactBean contactBean) {
    }

    protected void onContactUpdate(Context context, ContactBean contactBean) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(ContactApiConstant.ContactReceiver.ACTION_ADD)) {
            ContactBean contactBean = intent.getParcelableExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN);
            Log.d(TAG, "onReceive:  ACTION_CONTACT_ADD  contactBean =" + contactBean);
            onContactAdd(context, contactBean);
            return;
        }
        if (action.equals(ContactApiConstant.ContactReceiver.ACTION_UPDATE)) {
            ContactBean contactBean = intent.getParcelableExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN);
            Log.d(TAG, "onReceive:  ACTION_CONTACT_UPDATE  contactBean =" + contactBean);
            onContactUpdate(context, contactBean);
            return;
        }
        if (action.equals(ContactApiConstant.ContactReceiver.ACTION_REMOVE)) {
            ContactBean contactBean = intent.getParcelableExtra(ContactApiConstant.ContactReceiver.EXTRA_CONTACT_BEAN);
            Log.d(TAG, "onReceive:  ACTION_CONTACT_REMOVE  contactBean =" + contactBean);
            onContactRemove(context, contactBean);
        }
    }
}
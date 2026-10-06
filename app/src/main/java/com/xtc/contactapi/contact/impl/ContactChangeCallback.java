package com.xtc.contactapi.contact.impl;

import android.util.Log;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.constant.ContactApiConstant;
import com.xtc.contactapi.contact.convert.MainThreadConvert;
import com.xtc.contactapi.contact.interfaces.ContactChangeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人变化回调分发器，支持同线程回调与主线程回调两种监听器。
 */
public class ContactChangeCallback {

    private final List<ContactChangeListener> syncListeners = new ArrayList<>();
    private final List<ContactChangeListener> mainThreadListeners = new ArrayList<>();
    private final MainThreadConvert mainThreadConvert = new MainThreadConvert();

    /** 通知联系人更新。 */
    public void notifyContactUpdate(final ContactBean contactBean) {
        Log.i("ContactChangeCallback", "----------onUpdateContact------------:" + contactBean);
        for (int i = 0; i < syncListeners.size(); i++) {
            ContactChangeListener listener = syncListeners.get(i);
            if (listener != null) {
                listener.onContactUpdate(contactBean);
            }
        }
        for (int i = 0; i < mainThreadListeners.size(); i++) {
            final ContactChangeListener listener = mainThreadListeners.get(i);
            if (listener != null) {
                mainThreadConvert.convert(new Runnable() {
                    @Override
                    public void run() {
                        listener.onContactUpdate(contactBean);
                    }
                });
            }
        }
    }

    /** 通知联系人新增。 */
    public void notifyContactAdd(final ContactBean contactBean) {
        Log.i("ContactChangeCallback", "----------onContactAdd------------:" + contactBean);
        for (int i = 0; i < syncListeners.size(); i++) {
            ContactChangeListener listener = syncListeners.get(i);
            if (listener != null) {
                listener.onContactAdd(contactBean);
            }
        }
        for (int i = 0; i < mainThreadListeners.size(); i++) {
            final ContactChangeListener listener = mainThreadListeners.get(i);
            if (listener != null) {
                mainThreadConvert.convert(new Runnable() {
                    @Override
                    public void run() {
                        listener.onContactAdd(contactBean);
                    }
                });
            }
        }
    }

    /** 通知联系人删除。 */
    public void notifyContactRemove(final ContactBean contactBean) {
        Log.i("ContactChangeCallback", "----------onRemoveContact------------:" + contactBean);
        for (int i = 0; i < syncListeners.size(); i++) {
            ContactChangeListener listener = syncListeners.get(i);
            if (listener != null) {
                listener.onContactRemove(contactBean);
            }
        }
        for (int i = 0; i < mainThreadListeners.size(); i++) {
            final ContactChangeListener listener = mainThreadListeners.get(i);
            if (listener != null) {
                mainThreadConvert.convert(new Runnable() {
                    @Override
                    public void run() {
                        listener.onContactRemove(contactBean);
                    }
                });
            }
        }
    }

    /** 通知联系人列表刷新。 */
    public void notifyContactsRefresh(final List<ContactBean> contactList) {
        Log.i("ContactsRefreshCallback", "----------onsRefreshContact------------");
        for (int i = 0; i < syncListeners.size(); i++) {
            ContactChangeListener listener = syncListeners.get(i);
            if (listener != null) {
                listener.onContactsRefresh(contactList);
            }
        }
        for (int i = 0; i < mainThreadListeners.size(); i++) {
            final ContactChangeListener listener = mainThreadListeners.get(i);
            if (listener != null) {
                mainThreadConvert.convert(new Runnable() {
                    @Override
                    public void run() {
                        listener.onContactsRefresh(contactList);
                    }
                });
            }
        }
    }

    /** 注册监听器，convertCode 为 2 时在主线程回调。 */
    public void registerListener(ContactChangeListener listener, int convertCode) {
        if (syncListeners == null) {
            return;
        }
        if (convertCode == ContactApiConstant.ConvertCode.MAIN_THREAD) {
            mainThreadListeners.add(listener);
        } else {
            syncListeners.add(listener);
        }
    }

    /** 注销监听器。 */
    public void unregisterListener(ContactChangeListener listener) {
        if (listener == null || mainThreadListeners.remove(listener)) {
            return;
        }
        syncListeners.remove(listener);
    }

    public List<ContactChangeListener> getSyncListeners() {
        return syncListeners;
    }

    public List<ContactChangeListener> getMainThreadListeners() {
        return mainThreadListeners;
    }

    /** 是否没有任何监听器。 */
    public boolean isEmpty() {
        boolean syncEmpty = syncListeners == null || syncListeners.isEmpty();
        boolean mainEmpty = mainThreadListeners == null || mainThreadListeners.isEmpty();
        return syncEmpty && mainEmpty;
    }
}
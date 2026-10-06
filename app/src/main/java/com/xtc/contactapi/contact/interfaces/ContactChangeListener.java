package com.xtc.contactapi.contact.interfaces;

import com.xtc.contactapi.contact.bean.ContactBean;

import java.util.List;

/**
 * 联系人变化监听器。
 */
public interface ContactChangeListener {

    /** 新增联系人。 */
    void onContactAdd(ContactBean contactBean);

    /** 删除联系人。 */
    void onContactRemove(ContactBean contactBean);

    /** 更新联系人。 */
    void onContactUpdate(ContactBean contactBean);

    /** 联系人列表刷新。 */
    void onContactsRefresh(List<ContactBean> contactList);
}
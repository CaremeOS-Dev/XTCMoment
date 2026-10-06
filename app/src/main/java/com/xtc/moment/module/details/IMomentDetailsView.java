package com.xtc.moment.module.details;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.moment.base.IBaseInteractView;
import com.xtc.moment.db.bean.DbMoment;

/**
 * 动态详情页视图接口。
 */
public interface IMomentDetailsView extends IBaseInteractView {
    void loadCommentError(DbMoment moment);

    void contactUpdate(ContactBean contactBean);

    void contactRemove(ContactBean contactBean);
}
package com.xtc.moment.module.publish.text;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;

/** 纯文本动态发布视图接口。 */
public interface IPublishTextView extends MvpView {

    void publishFail(String message);

    void publishInvalidate();

    void publishLimited();

    void publishSuccess(DbMoment moment);
}
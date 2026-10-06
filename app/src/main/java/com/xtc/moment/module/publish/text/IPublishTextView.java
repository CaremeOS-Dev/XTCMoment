package com.xtc.moment.module.publish.text;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;

/**
 * 文字发布视图接口。
 */
public interface IPublishTextView extends MvpView {
    void publishSuccess(DbMoment moment);

    void publishFail(String message);

    void publishLimited();

    void publishInvalidate();
}
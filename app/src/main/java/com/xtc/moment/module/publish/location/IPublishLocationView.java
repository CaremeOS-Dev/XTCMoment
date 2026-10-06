package com.xtc.moment.module.publish.location;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;

/**
 * 位置发布视图接口。
 */
public interface IPublishLocationView extends MvpView {
    void publishSuccess(DbMoment moment);

    void publishFail(String message);

    void publishLimited();

    void publishInvalidate();
}
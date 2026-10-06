package com.xtc.moment.module.publish.location;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;

/** 带位置信息的动态发布视图接口。 */
public interface IPublishLocationView extends MvpView {

    void publishFail(String message);

    void publishInvalidate();

    void publishLimited();

    void publishSuccess(DbMoment moment);
}
package com.xtc.moment.module.publish.moodorstate;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbTemplate;

import java.util.List;

/** 心情/状态模板列表视图接口。 */
public interface IMoodOrStateView extends MvpView {

    void loadFail();

    void loadSuccess(List<DbTemplate> templates);
}
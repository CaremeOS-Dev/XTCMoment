package com.xtc.moment.module.main;

import android.view.View;

import com.xtc.moment.base.IBaseInteractView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;

import java.util.List;
import java.util.Map;

/**
 * 好友圈主页视图接口。
 */
public interface IMomentActivityView extends IBaseInteractView {

    void loadLocalData(long startTime, long endTime);

    void loadSuccess(List<DbMoment> moments);

    void loadError();

    void showNoMore();

    void refreshData(DbMoment moment);

    void refreshMomentLikes(Map<String, List<DbLikeMessage>> likes);

    void dealIllegal();

    View getBackgroundView();
}
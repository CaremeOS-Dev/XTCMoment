package com.xtc.moment.module.share;

import android.view.View;

import com.xtc.moment.base.IBaseInteractView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;

import java.util.List;
import java.util.Map;

/**
 * 分享到好友圈页面视图接口。
 */
public interface IShareView extends IBaseInteractView {

    void startLoad();

    void loadLocalData(long startTime, long endTime, String watchId);

    void loadSuccess(List<DbMoment> moments);

    void loadComplete();

    void loadError();

    void showNoMore();

    void loadPraiseRecordSuccess(Map<String, List<DbLikeMessage>> likes);

    void loadPraiseRecordFailed();

    void notifyAdapterContactChange();

    View getBackgroundView();
}
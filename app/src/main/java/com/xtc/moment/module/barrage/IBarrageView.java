package com.xtc.moment.module.barrage;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.SearchGiftResponse;

import java.util.List;

/**
 * 弹幕页面视图接口。
 */
public interface IBarrageView extends MvpView {
    void getBarrageDataSuccess(List<DbMomentComment> comments, SearchGiftResponse giftResponse);

    void getBarrageDataFail(List<DbMomentComment> comments);

    void sendGiftSuccess(int giftId, boolean success);

    void sendGiftFail();

    void showBtn();
}
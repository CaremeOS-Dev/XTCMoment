package com.xtc.moment.module.publish;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;

/**
 * 发布页面视图接口。
 */
public interface IPublishView extends MvpView {

    void chooseAlbumItemSuccess(PhotoMsg photoMsg);

    void chooseAlbumItemSuccess(LivePhotoMsg livePhotoMsg);

    void savePhotoSuccess(PhotoMsg photoMsg);

    void publishSuccess(DbMoment moment);

    void publishFail(String message);

    void publishLimited();

    void publishInvalidate();

    void showLoading();

    void dismissLoading();

    void toMomentActivity();
}
package com.xtc.moment.module.publish;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;

/** 动态发布页视图接口。 */
public interface IPublishView extends MvpView {

    void chooseAlbumItemSuccess(LivePhotoMsg livePhotoMsg);

    void chooseAlbumItemSuccess(PhotoMsg photoMsg);

    void dismissLoading();

    void publishFail(String message);

    void publishInvalidate();

    void publishLimited();

    void publishSuccess(DbMoment moment);

    void savePhotoSuccess(PhotoMsg photoMsg);

    void showLoading();

    void toMomentActivity();
}
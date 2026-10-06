package com.xtc.moment.module.like;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbLikeMessage;

import java.util.List;
import java.util.Map;

/**
 * 点赞列表视图接口。
 */
public interface IMomentLikeView extends MvpView {
    void showMomentLikes(Map<String, List<DbLikeMessage>> likes);

    void showNoMomentsLike();

    void showGetMomentLikesError();
}
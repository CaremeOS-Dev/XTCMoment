package com.xtc.moment.base;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;

/**
 * 动态互动视图基类接口。
 */
public interface IBaseInteractView extends MvpView {

    void loadCommentSuccess(DbMoment moment);

    void likeSuccess(DbLikeMessage likeMessage);

    void cancelLikeSuccess(DbMoment moment);

    void likeError();

    void likeError(String message);

    void publishSuccess(DbMomentComment comment, String extra);

    void publishFail(String message);

    void publishLimited();

    void publishInvalidate();

    void removeComment(DbMomentComment comment);

    void removeCommentFail();

    void removeMoment(DbMoment moment);

    void removeFail();

    void momentAlreadyDeleted();
}
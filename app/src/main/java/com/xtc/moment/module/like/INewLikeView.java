package com.xtc.moment.module.like;

import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.MomentNewMsgBean;

import java.util.List;

/**
 * 新消息（点赞/评论）视图接口。
 */
public interface INewLikeView extends MvpView {
    void showNewLike(List<Friend> friends, List<DbLikeMessage> likeMessages);

    void showNewMsg(List<MomentNewMsgBean<DbMoment>> messages);

    void showNoNewLike();
}
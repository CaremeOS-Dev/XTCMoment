package com.xtc.moment.module.like;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.log.LogUtil;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.MomentNewMsgBean;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.Utils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * 新消息页业务处理：汇总未读点赞与评论，生成消息列表。
 */
public class NewLikePresenter extends MvpBasePresenter<INewLikeView> {

    private static final String TAG = "NewLikePresenter";

    private Context mContext;

    List<DbLikeMessage> likeDatas;
    List<DbMomentComment> commentDatas;
    List<DbMoment> momentDatas;
    List<MomentNewMsgBean<DbMoment>> praiseAndCommentDatas;

    public NewLikePresenter(Context context) {
        this.mContext = context;
    }

    public void initData() {
        this.likeDatas = loadNewLikeMessageAboutMine();
        this.commentDatas = loadCommentMessageAboutMine();
        this.momentDatas = loadMomentByUncheckLikeAndComment();
        this.praiseAndCommentDatas = createPraiseAndCommentDatas();
        List<MomentNewMsgBean<DbMoment>> messageList = this.praiseAndCommentDatas;
        if (messageList != null && messageList.size() > 0) {
            Utils.logSize(TAG, "praiseAndCommentDatas size :", this.praiseAndCommentDatas);
            Collections.sort(this.praiseAndCommentDatas, new Comparator<MomentNewMsgBean<DbMoment>>() {
                @Override
                public int compare(MomentNewMsgBean<DbMoment> first, MomentNewMsgBean<DbMoment> second) {
                    return first.getCreateTime().compareTo(second.getCreateTime());
                }
            });
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (!NewLikePresenter.this.isViewAttached() || NewLikePresenter.this.getView() == null) {
                        return;
                    }
                    NewLikePresenter.this.getView().showNewMsg(NewLikePresenter.this.praiseAndCommentDatas);
                }
            });
        } else {
            LogUtil.d(TAG, "praiseAndCommentDatas is null");
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (!NewLikePresenter.this.isViewAttached() || NewLikePresenter.this.getView() == null) {
                        return;
                    }
                    NewLikePresenter.this.getView().showNoNewLike();
                }
            });
        }
    }

    private List<DbMoment> loadMomentByUncheckLikeAndComment() {
        HashSet<String> momentIds = new HashSet<>();
        List<DbLikeMessage> likeMessages = this.likeDatas;
        if (likeMessages != null && likeMessages.size() > 0) {
            LogUtil.d(TAG, "likeDatas size =" + this.likeDatas.size());
            for (int i = 0; i < this.likeDatas.size(); i++) {
                DbLikeMessage likeMessage = this.likeDatas.get(i);
                if (likeMessage != null) {
                    LogUtil.d(TAG, "未读的点赞信息：" + likeMessage.getMomentId());
                    momentIds.add(this.likeDatas.get(i).getMomentId());
                }
            }
        }
        List<DbMomentComment> comments = this.commentDatas;
        if (comments != null && comments.size() > 0) {
            LogUtil.d(TAG, "commentDatas size =" + this.commentDatas.size());
            for (int i = 0; i < this.commentDatas.size(); i++) {
                DbMomentComment comment = this.commentDatas.get(i);
                if (comment != null) {
                    LogUtil.d(TAG, "未读的评论信息：" + comment.getCommentId());
                    momentIds.add(this.commentDatas.get(i).getMomentId());
                }
            }
        }
        if (momentIds.size() <= 0) {
            LogUtil.d(TAG, "momentIds.size() <= 0");
            return null;
        }
        List<DbMoment> moments = MomentServeImpl.getInstance(this.mContext).queryByMomentIds(momentIds);
        Utils.logSize(TAG, "dbMomentList:", moments);
        if (CollectionUtil.isEmpty(moments)) {
            BehaviorEvent.recordNewLikeEmpty(this.mContext);
        }
        return moments;
    }

    private List<MomentNewMsgBean<DbMoment>> createPraiseAndCommentDatas() {
        List<DbMoment> moments = this.momentDatas;
        if (moments == null || moments.size() == 0) {
            return null;
        }
        ArrayList<MomentNewMsgBean<DbMoment>> messages = new ArrayList<>();
        boolean lbsPublishSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext,
                ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
        for (DbMoment moment : this.momentDatas) {
            List<DbLikeMessage> likeMessages = this.likeDatas;
            if (likeMessages != null && likeMessages.size() > 0) {
                for (DbLikeMessage likeMessage : this.likeDatas) {
                    if (moment.getMomentId().equals(likeMessage.getMomentId()) && !likeMessage.isChecked()) {
                        messages.add(BeanConverterUtil.toMomentNewMsgBean(likeMessage, moment));
                    }
                }
            }
            List<DbMomentComment> comments = this.commentDatas;
            if (comments != null && comments.size() > 0) {
                for (DbMomentComment comment : this.commentDatas) {
                    if (moment.getMomentId().equals(comment.getMomentId()) && !comment.isChecked()) {
                        messages.add(BeanConverterUtil.toMomentNewMsgBean(comment, moment));
                    }
                }
            }
            moment.setLbsSwitch(lbsPublishSwitch);
        }
        return messages;
    }

    private List<DbMomentComment> loadCommentMessageAboutMine() {
        return MomentServeImpl.getInstance(this.mContext).loadAllUncheckedCommentByWatchId(
                AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                        .getWatchId(this.mContext));
    }

    public void checkLikeMessage() {
        List<DbLikeMessage> likeMessages = this.likeDatas;
        if (likeMessages == null || likeMessages.isEmpty()) {
            return;
        }
        ArrayList<DbLikeMessage> checkedMessages = new ArrayList<>();
        for (DbLikeMessage likeMessage : this.likeDatas) {
            if (likeMessage != null) {
                likeMessage.setChecked(true);
                checkedMessages.add(likeMessage);
            }
        }
        MomentServeImpl.getInstance(this.mContext).updateLikeMessage(checkedMessages);
    }

    public void checkMomentComment() {
        List<DbMomentComment> comments = this.commentDatas;
        if (comments == null || comments.isEmpty()) {
            return;
        }
        ArrayList<DbMomentComment> checkedComments = new ArrayList<>();
        ArrayList<DbMomentComment> deletedComments = new ArrayList<>();
        for (DbMomentComment comment : this.commentDatas) {
            if (comment != null) {
                if (comment.isDeleted()) {
                    deletedComments.add(comment);
                } else {
                    comment.setChecked(true);
                    checkedComments.add(comment);
                }
            }
        }
        MomentServeImpl.getInstance(this.mContext).updateMomentComment(checkedComments);
        MomentServeImpl.getInstance(this.mContext).deleteDbCommentForBatch(deletedComments);
    }

    private List<DbLikeMessage> loadNewLikeMessageAboutMine() {
        return MomentServeImpl.getInstance(this.mContext).loadAllUncheckedLikeMessageByWatchId(
                AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
    }

    public List<Friend> getAllFriend() {
        return FriendInfoServeImpl.getInstance(this.mContext).getAllFriendInfo();
    }
}
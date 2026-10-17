package com.xtc.moment.favorite;

import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

/**
 * 收藏动态的快照载荷。
 *
 * <p>动态被删除后本地只剩这一份 JSON：动态本体（含点赞总数 {@code likeTotal}、评论总数
 * {@code commentsTotalCount}）、当时的评论列表与点赞记录。收藏列表在动态仍存在时用实时数据渲染，
 * 动态被删之后回退到这份快照，因此「最后一次的点赞数和评论列表」始终可读。
 */
public class FavoriteSnapshot {

    /** 动态本体快照。 */
    private DbMoment moment;

    /** 评论列表快照，最多保留卡片可展示的条数。 */
    private List<DbMomentComment> comments;

    /** 点赞记录快照，用于渲染「谁赞过我」的点赞文案。 */
    private List<DbLikeMessage> likes;

    public DbMoment getMoment() {
        return this.moment;
    }

    public void setMoment(DbMoment moment) {
        this.moment = moment;
    }

    public List<DbMomentComment> getComments() {
        return this.comments;
    }

    public void setComments(List<DbMomentComment> comments) {
        this.comments = comments;
    }

    public List<DbLikeMessage> getLikes() {
        return this.likes;
    }

    public void setLikes(List<DbLikeMessage> likes) {
        this.likes = likes;
    }
}

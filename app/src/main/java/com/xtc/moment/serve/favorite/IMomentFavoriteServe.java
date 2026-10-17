package com.xtc.moment.serve.favorite;

import com.xtc.moment.db.bean.DbFavoriteMoment;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.favorite.FavoriteSnapshot;

import java.util.List;

/**
 * 本地动态收藏服务：收藏/取消收藏、收藏态判断与快照读写。
 */
public interface IMomentFavoriteServe {

    /** 该动态是否已被收藏（内存判断，供列表绑定使用）。 */
    boolean isCollected(String momentId);

    /** 收藏一条动态，并把当前的评论与点赞记录一并写入快照。 */
    boolean collect(DbMoment moment, List<DbMomentComment> comments, List<DbLikeMessage> likes);

    /** 取消收藏。 */
    boolean uncollect(String momentId);

    /** 动态仍存在时刷新快照，保证「最后一次的点赞数和评论列表」尽量新。 */
    boolean refreshSnapshot(DbMoment moment, List<DbMomentComment> comments, List<DbLikeMessage> likes);

    /** 按收藏时间倒序返回全部收藏记录。 */
    List<DbFavoriteMoment> loadAll();

    /** 解析收藏记录里的快照，失败返回 null。 */
    FavoriteSnapshot parseSnapshot(DbFavoriteMoment favorite);

    /** 解析收藏记录里的快照，失败返回 null。 */
    FavoriteSnapshot parseSnapshot(String snapshotJson);

    /** 强制下一次收藏态判断重新读库。 */
    void invalidateCache();
}

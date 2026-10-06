package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

import rx.Observable;

/**
 * 评论数据服务接口（本地库 + 网络）。
 */
public interface ICommentServe {

    DbMoment getMomentsFromDbSync(String momentId);

    DbMoment getDbCommentFromDbSync(String momentId);

    Observable<List<DbMomentComment>> searchCommentFromDb(int page, int pageSize, String momentId, String commentId);

    Observable<List<DbMomentComment>> searchCommentFromNet(int page, int pageSize, String momentId, String commentId,
            boolean isOfficial);

    Observable<List<DbMomentComment>> searchOfficialCommentFromNet(int page, int pageSize, String advertId,
            String commentId, boolean isOfficial);

    Observable<List<DbMomentComment>> searchAllCommentFromNet(String momentId, String commentId);
}
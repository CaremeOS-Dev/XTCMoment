package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

import rx.Observable;

/** Comment persistence and network access. */
public interface ICommentServe {
    DbMoment getDbCommentFromDbSync(String momentId);

    DbMoment getMomentsFromDbSync(String momentId);

    Observable<List<DbMomentComment>> searchAllCommentFromNet(String momentId, String momentWatchId);

    Observable<List<DbMomentComment>> searchCommentFromDb(int pageNum, int pageSize, String momentId, String momentWatchId);

    Observable<List<DbMomentComment>> searchCommentFromNet(int pageNum, int pageSize, String momentId, String momentWatchId, boolean fromMomentDetail);

    Observable<List<DbMomentComment>> searchOfficialCommentFromNet(int pageNum, int pageSize, String momentId, String momentWatchId, boolean fromMomentDetail);
}

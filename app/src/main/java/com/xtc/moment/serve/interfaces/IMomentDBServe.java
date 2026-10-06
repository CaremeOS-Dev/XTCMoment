package com.xtc.moment.serve.interfaces;

import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;

import java.util.List;

import rx.Observable;

/**
 * 动态本地数据库服务接口。
 */
public interface IMomentDBServe {

    Observable<List<DbMoment>> getMomentsFromDb(long startTime, long endTime, int pageSize, String watchId);

    List<DbMoment> getMomentsFromDbSync(long startTime, long endTime, int pageSize, String watchId);

    List<DbMoment> getMomentById(String momentId);

    List<DbMoment> getMomentByWatchId(String watchId);

    List<DbMoment> getOthersUncheckedMoment(String watchId);

    void addMoments(List<DbMoment> moments);

    void insertMomentByMomentId(DbMoment moment);

    void updateMomentByMomentId(String momentId);

    void updateMomentsByMomentId(List<DbMoment> moments);

    void deleteMomentByWatchId(String watchId);

    long getMomentCountInDb();

    boolean increaseLikeTotal(String momentId);

    boolean isLiked(DbMoment moment);

    void addLikeMessage(DbLikeMessage message);

    void addLikeMessage(List<DbLikeMessage> messages);

    void updateLikeMessage(List<DbLikeMessage> messages);

    void updateLikeMessageName(String watchId, String watchName);

    List<DbLikeMessage> getLikeMessageByMomentId(String momentId);

    List<DbLikeMessage> getLikeMessageByMomentIdAndWatchId(String momentId, String watchId);

    List<DbLikeMessage> loadAllLikeMessage();
}
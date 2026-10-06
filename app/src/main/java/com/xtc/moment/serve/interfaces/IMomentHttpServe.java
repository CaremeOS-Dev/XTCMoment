package com.xtc.moment.serve.interfaces;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.PraiseResponse;

import java.util.List;

import rx.Observable;

/**
 * 动态网络服务接口。
 */
public interface IMomentHttpServe {

    Observable<List<DbMoment>> getMomentsFromNet(long startTime, long endTime, int pageSize, String watchId);

    Observable<Moment> publishMoment(int type, String content, int mediaType, String resource);

    Observable<DefaultResponse> praiseMoment(String momentId, String watchId);

    Observable<PraiseResponse> getPraiseRecord(List<String> momentIds, String watchId);
}
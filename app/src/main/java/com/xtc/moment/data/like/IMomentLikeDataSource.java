package com.xtc.moment.data.like;

import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.tasks.domain.usecase.CancelPraiseMomentTask;
import com.xtc.moment.tasks.domain.usecase.PraiseMomentTask;

import rx.Observable;

/**
 * 点赞数据源接口。
 */
public interface IMomentLikeDataSource {

    Observable<DefaultResponse> praiseMoment(PraiseMomentTask.RequestValues requestValues);

    Observable<DefaultResponse> praiseAdvertise(PraiseMomentTask.RequestValues requestValues);

    Observable<String> cancelPraiseMoment(CancelPraiseMomentTask.RequestValues requestValues);

    boolean addLikeMessage(DbLikeMessage likeMessage);

    void updateMomentByMomentId(String momentId);

    DbMoment deleteLikeDaoByMomentId(String momentId, String watchId);
}
package com.xtc.moment.data;

import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.LbsStarBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.ReminderConfig;
import com.xtc.moment.net.bean.ReportMomentReq;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.tasks.domain.usecase.PublishCommentTask;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.tasks.domain.usecase.PublishOfficialCommentTask;

import rx.Observable;

/**
 * 动态数据源接口。
 */
public interface IMomentsDataSource {

    Observable<Moment> publishMoment(PublishMomentTask.RequestValues requestValues);

    Observable<CommentResultBean> publishComment(PublishCommentTask.RequestValues requestValues);

    Observable<CommentOfficialResultBean> publishOfficialComment(PublishOfficialCommentTask.RequestValues requestValues);

    Observable<String> doLbsStar(LbsStarBean body);

    Observable<ReminderConfig> getReminderConfig();

    Observable<SearchGiftResponse> searchGift(SearchGiftRequest request);

    Observable<NormalResultBean> sendGift(SendGiftRequest request);

    Observable<String> reportMoment(ReportMomentReq request);

    void addCommentMoment(DbMomentComment comment);

    void insertMomentByMomentId(DbMoment moment);

    boolean isLiked(DbMoment moment);

    boolean addGiftRecordMessage(DbGiftRecord record);

    boolean updateGift(DbGiftRecord record);

    DbGiftRecord queryGiftByMomentId(String momentId);
}
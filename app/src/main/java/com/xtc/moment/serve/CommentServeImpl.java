package com.xtc.moment.serve;

import android.content.Context;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.dao.MomentCommentDao;
import com.xtc.moment.db.dao.MomentDao;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.CommentBean;
import com.xtc.moment.net.bean.SearchAllCommentResponse;
import com.xtc.moment.net.bean.SearchCommentResponse;
import com.xtc.moment.net.bean.SearchOfficialCommentResponse;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * 评论服务实现：本地评论读取与网络评论拉取。
 */
public class CommentServeImpl extends BaseServe implements ICommentServe {

    private static final String TAG = "CommentServeImpl";

    private final MomentCommentDao momentCommentDao;
    private final MomentDao momentDao;
    private MomentHttpServiceProxy momentHttpServiceProxy;

    public CommentServeImpl(Context context) {
        super(context);
        this.momentDao = ServerCache.getDao(context, MomentDao.class);
        this.momentCommentDao = ServerCache.getDao(context, MomentCommentDao.class);
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
        ServerCache.putBusinessServer(this);
    }

    public static ICommentServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, CommentServeImpl.class);
    }

    @Override
    public DbMoment getMomentsFromDbSync(String momentId) {
        List<DbMoment> moments = this.momentDao.queryByMomentId(momentId);
        if (moments == null || moments.size() <= 0) {
            return null;
        }
        return moments.get(0);
    }

    @Override
    public DbMoment getDbCommentFromDbSync(String momentId) {
        DbMoment moment = getMomentsFromDbSync(momentId);
        if (moment == null) {
            return null;
        }
        if (CollectionUtil.isEmpty(moment.getComments())) {
            moment.setComments(new ArrayList<DbMomentComment>());
        }
        List<DbMomentComment> comments = this.momentCommentDao.queryCommentByMomentId(momentId);
        if (!CollectionUtil.isEmpty(comments)) {
            moment.setComments(comments);
        }
        return moment;
    }

    @Override
    public Observable<List<DbMomentComment>> searchCommentFromNet(int pageNum, int pageSize, String momentId, String momentWatchId, boolean flag) {
        return this.momentHttpServiceProxy.searchComment(pageNum, pageSize, momentId, momentWatchId, flag).map(new Func1<SearchCommentResponse, List<DbMomentComment>>() {
            @Override
            public List<DbMomentComment> call(SearchCommentResponse response) {
                return response != null ? response.getComments() : new ArrayList<DbMomentComment>();
            }
        });
    }

    @Override
    public Observable<List<DbMomentComment>> searchAllCommentFromNet(String momentId, String momentWatchId) {
        return this.momentHttpServiceProxy.searchAllComment(momentId, momentWatchId).map(new Func1<NetBaseResult<SearchAllCommentResponse>, List<DbMomentComment>>() {
            @Override
            public List<DbMomentComment> call(NetBaseResult<SearchAllCommentResponse> result) {
                if ("000001".equals(result.getCode()) && result.getData() != null) {
                    return result.getData().getComments();
                }
                return new ArrayList<DbMomentComment>();
            }
        });
    }

    @Override
    public Observable<List<DbMomentComment>> searchOfficialCommentFromNet(int pageNum, int pageSize, String advertId, final String lookUpId, boolean flag) {
        return this.momentHttpServiceProxy.searchOfficialComment(pageNum, pageSize, advertId, lookUpId, flag).map(new Func1<NetBaseResult<SearchOfficialCommentResponse>, List<DbMomentComment>>() {
            @Override
            public List<DbMomentComment> call(NetBaseResult<SearchOfficialCommentResponse> result) {
                if ("000001".equals(result.getCode())) {
                    List<CommentBean> comments = result.getData().getComments();
                    return BeanConverterUtil.convertToDbMomentCommentList(comments, lookUpId);
                }
                return new ArrayList<DbMomentComment>();
            }
        });
    }

    @Override
    public Observable<List<DbMomentComment>> searchCommentFromDb(int pageNum, int pageSize, String momentId, String commentId) {
        return Observable.just(this.momentCommentDao.queryCommentPageByMomentId(0L, pageSize, momentId));
    }
}
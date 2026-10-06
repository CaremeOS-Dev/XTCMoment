package com.xtc.moment.module.details;

import android.content.Context;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.interfaces.ContactChangeListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.base.BaseInteractPresenter;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.OfficialCommentResultBean;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.CommentServeImpl;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.ICommentServe;
import com.xtc.moment.serve.IFriendInfoServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.third.bean.PushBehaviorBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 动态详情业务处理：加载评论（优先网络，失败回退本地）与官方消息已读埋点。
 */
public class MomentDetailsPresenter extends BaseInteractPresenter<IMomentDetailsView>
        implements ContactChangeListener {

    private static final String TAG = "MomentDetailsPresenter";

    private final IAccountInfoServe accountInfoServe;
    private final ICommentServe iCommentServe;
    private final IFriendInfoServe friendInfoServe;

    private DbMoment dbMoment;
    private List<String> previewMomentIdData = new ArrayList<>();

    @Override
    public String getLogTag() {
        return TAG;
    }

    public MomentDetailsPresenter(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(this.mContext);
        this.iCommentServe = CommentServeImpl.getInstance(this.mContext);
        this.friendInfoServe = FriendInfoServeImpl.getInstance(context);
        this.accountInfoServe = AccountInfoServerImpl.getInstance(this.mContext);
    }

    @Override
    public void onContactAdd(ContactBean contactBean) {
    }

    @Override
    public void onContactsRefresh(List<ContactBean> contactBeanList) {
    }

    /** 首次进入详情时加载评论：官方消息走专用接口，其余走全量评论接口。 */
    public void getCommentByMomentId(int page, int pageSize, String momentId, final String watchId,
            String momentJson) {
        if (this.dbMoment == null) {
            this.dbMoment = this.iCommentServe.getDbCommentFromDbSync(momentId);
        }
        LogUtil.d(TAG, "getCommentByMomentId call db: " + this.dbMoment);
        if (this.dbMoment == null) {
            this.dbMoment = JSONUtil.fromJSON(momentJson, DbMoment.class);
        }
        if (!MomentTypeUtil.isOfficialType(this.dbMoment.getType().intValue())) {
            searchAllCommentFromNet(page, pageSize, momentId, watchId);
            return;
        }
        LogUtil.d(TAG, "getCommentByMomentId call: " + this.dbMoment);
        ArrayList<String> momentIds = new ArrayList<>();
        momentIds.add(momentId);
        this.iMomentServe.getOfficialCommentFromNet(
                        this.accountInfoServe.getWatchAccountInfo().getWatchId(this.mContext), momentIds)
                .map(new Func1<List<OfficialCommentResultBean>, DbMoment>() {
                    @Override
                    public DbMoment call(List<OfficialCommentResultBean> resultList) {
                        List<DbMomentComment> comments = null;
                        for (int i = 0; i < resultList.size(); i++) {
                            comments = BeanConverterUtil.convertToDbMomentCommentList(
                                    resultList.get(i).getList(), watchId);
                            MomentDetailsPresenter.this.iMomentServe.addCommentMoment(comments);
                        }
                        if (CollectionUtil.isEmpty(MomentDetailsPresenter.this.dbMoment.getComments())) {
                            MomentDetailsPresenter.this.dbMoment.setComments(new ArrayList<>());
                        }
                        if (!CollectionUtil.isEmpty(comments)) {
                            MomentDetailsPresenter.this.dbMoment.setComments(
                                    BeanConverterUtil.mapMomentToCommentList(
                                            MomentDetailsPresenter.this.dbMoment, comments));
                        }
                        return MomentDetailsPresenter.this.dbMoment;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        if (!MomentDetailsPresenter.this.isViewAttached()
                                || MomentDetailsPresenter.this.getView() == null) {
                            return;
                        }
                        MomentDetailsPresenter.this.getView().loadCommentSuccess(moment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, throwable);
                        final String failedMomentId = momentId;
                        HandlerUtil.runOnBackground(new Runnable() {
                            @Override
                            public void run() {
                                final DbMoment localMoment = MomentDetailsPresenter.this.iCommentServe
                                        .getDbCommentFromDbSync(failedMomentId);
                                HandlerUtil.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (localMoment == null) {
                                            MomentDetailsPresenter.this.loadFail(
                                                    MomentDetailsPresenter.this.dbMoment);
                                        } else if (MomentDetailsPresenter.this.getView() != null) {
                                            MomentDetailsPresenter.this.dbMoment = localMoment;
                                            MomentDetailsPresenter.this.getView()
                                                    .loadCommentSuccess(localMoment);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
    }

    private void loadFail(DbMoment moment) {
        if (getView() != null) {
            getView().loadCommentError(moment);
        }
    }

    private void searchAllCommentFromNet(final int page, final int pageSize, final String momentId,
            final String watchId) {
        this.iCommentServe.searchAllCommentFromNet(momentId, watchId)
                .map(new Func1<List<DbMomentComment>, DbMoment>() {
                    @Override
                    public DbMoment call(List<DbMomentComment> comments) {
                        MomentDetailsPresenter.this.iMomentServe.addCommentMoment(comments);
                        if (comments != null) {
                            if (CollectionUtil.isEmpty(MomentDetailsPresenter.this.dbMoment.getComments())) {
                                MomentDetailsPresenter.this.dbMoment.setComments(new ArrayList<>());
                            }
                            if (!CollectionUtil.isEmpty(comments)) {
                                MomentDetailsPresenter.this.dbMoment.setComments(
                                        BeanConverterUtil.mapMomentToCommentList(
                                                MomentDetailsPresenter.this.dbMoment, comments));
                            }
                        }
                        return MomentDetailsPresenter.this.dbMoment;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        LogUtil.d(TAG, "getMomentByMomentId success call: " + moment);
                        if (moment == null || MomentDetailsPresenter.this.getView() == null) {
                            return;
                        }
                        MomentDetailsPresenter.this.getView().loadCommentSuccess(moment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getMomentByMomentId throwable call: " + throwable);
                        MomentDetailsPresenter.this.searchCommentFromDb(page, pageSize, momentId, watchId);
                    }
                });
    }

    public void getMomentCommentByMomentId(final int page, final int pageSize, final String momentId,
            final String watchId) {
        if (this.dbMoment == null) {
            this.dbMoment = this.iCommentServe.getDbCommentFromDbSync(momentId);
        }
        LogUtil.d(TAG, "getMomentCommentByMomentId call: " + this.dbMoment);
        boolean isOfficialType = MomentTypeUtil.isOfficialType(this.dbMoment.getType().intValue());
        if (isOfficialType) {
            searchOfficialComment(page, pageSize, momentId, watchId, true);
            return;
        }
        this.iCommentServe.searchCommentFromNet(page, pageSize, momentId, watchId, false)
                .map(new Func1<List<DbMomentComment>, DbMoment>() {
                    @Override
                    public DbMoment call(List<DbMomentComment> comments) {
                        MomentDetailsPresenter.this.iMomentServe.addCommentMoment(comments);
                        if (comments != null) {
                            if (CollectionUtil.isEmpty(MomentDetailsPresenter.this.dbMoment.getComments())) {
                                MomentDetailsPresenter.this.dbMoment.setComments(new ArrayList<>());
                            }
                            if (!CollectionUtil.isEmpty(comments)) {
                                MomentDetailsPresenter.this.dbMoment.setComments(
                                        BeanConverterUtil.mapMomentToCommentList(
                                                MomentDetailsPresenter.this.dbMoment, comments));
                            }
                        }
                        return MomentDetailsPresenter.this.dbMoment;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        LogUtil.d(TAG, "getMomentByMomentId success call: " + moment);
                        if (moment == null || MomentDetailsPresenter.this.getView() == null) {
                            return;
                        }
                        MomentDetailsPresenter.this.getView().loadCommentSuccess(moment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getMomentByMomentId throwable call: " + throwable);
                        MomentDetailsPresenter.this.searchCommentFromDb(page, pageSize, momentId, watchId);
                    }
                });
    }

    private void searchOfficialComment(final int page, final int pageSize, final String momentId,
            final String watchId, boolean isOfficial) {
        this.iCommentServe.searchOfficialCommentFromNet(page, pageSize, momentId, watchId, isOfficial)
                .map(new Func1<List<DbMomentComment>, DbMoment>() {
                    @Override
                    public DbMoment call(List<DbMomentComment> comments) {
                        MomentDetailsPresenter.this.iMomentServe.addCommentMoment(comments);
                        if (comments != null) {
                            if (CollectionUtil.isEmpty(MomentDetailsPresenter.this.dbMoment.getComments())) {
                                MomentDetailsPresenter.this.dbMoment.setComments(new ArrayList<>());
                            }
                            if (!CollectionUtil.isEmpty(comments)) {
                                MomentDetailsPresenter.this.dbMoment.setComments(
                                        BeanConverterUtil.mapMomentToCommentList(
                                                MomentDetailsPresenter.this.dbMoment, comments));
                            }
                        }
                        return MomentDetailsPresenter.this.dbMoment;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        LogUtil.d(TAG, "getMomentByMomentId success call: " + moment);
                        if (moment == null || MomentDetailsPresenter.this.getView() == null) {
                            return;
                        }
                        MomentDetailsPresenter.this.getView().loadCommentSuccess(moment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getMomentByMomentId throwable call: " + throwable);
                        MomentDetailsPresenter.this.searchCommentFromDb(page, pageSize, momentId, watchId);
                    }
                });
    }

    public void searchCommentFromDb(int page, int pageSize, String momentId, String watchId) {
        this.iCommentServe.searchCommentFromDb(page, pageSize, momentId, watchId)
                .map(new Func1<List<DbMomentComment>, DbMoment>() {
                    @Override
                    public DbMoment call(List<DbMomentComment> comments) {
                        if (comments != null) {
                            if (CollectionUtil.isEmpty(MomentDetailsPresenter.this.dbMoment.getComments())) {
                                MomentDetailsPresenter.this.dbMoment.setComments(new ArrayList<>());
                            }
                            if (!CollectionUtil.isEmpty(comments)) {
                                MomentDetailsPresenter.this.dbMoment.setComments(
                                        BeanConverterUtil.mapMomentToCommentList(
                                                MomentDetailsPresenter.this.dbMoment, comments));
                            }
                        }
                        return MomentDetailsPresenter.this.dbMoment;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbMoment>() {
                    @Override
                    public void call(DbMoment moment) {
                        LogUtil.d(TAG, "getMomentByMomentId success too call: " + moment);
                        if (moment == null || MomentDetailsPresenter.this.getView() == null) {
                            return;
                        }
                        MomentDetailsPresenter.this.getView().loadCommentSuccess(moment);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        MomentDetailsPresenter.this.loadFail(MomentDetailsPresenter.this.dbMoment);
                        LogUtil.e(TAG, "getMomentByMomentId throwable too call: " + throwable);
                    }
                });
    }

    /** 官方消息首次浏览时上报浏览行为。 */
    public void checkPreviewBehavior(final Context context, DbMoment moment) {
        if (moment == null || !MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            return;
        }
        if (this.previewMomentIdData == null) {
            this.previewMomentIdData = new ArrayList<>();
        }
        if (this.previewMomentIdData.size() > 0 && this.previewMomentIdData.contains(moment.getMomentId())) {
            LogUtil.d(TAG, "moment has preview");
            return;
        }
        this.previewMomentIdData.add(moment.getMomentId());
        Observable.just(moment.getMomentId())
                .map(new Func1<String, List<DbMoment>>() {
                    @Override
                    public List<DbMoment> call(String momentId) {
                        return MomentDetailsPresenter.this.iMomentServe.getMomentById(momentId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<DbMoment>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.d(TAG, "checkBehavior#e:", throwable);
                    }

                    @Override
                    public void onNext(List<DbMoment> moments) {
                        if (moments == null || moments.size() <= 0) {
                            return;
                        }
                        DbMoment localMoment = moments.get(0);
                        if (!localMoment.isPreviewed()) {
                            localMoment.setPreviewed(true);
                            MomentDetailsPresenter.this.iMomentServe.updateMoment(localMoment);
                            MomentBehavior.behaviorOfficialMoment(context,
                                    new PushBehaviorBean(localMoment.getMomentId(), 1));
                            LogUtil.d(TAG, "push preview behavior");
                        } else {
                            LogUtil.d(TAG, "has push preview behavior before");
                        }
                    }
                });
    }

    @Override
    public void onContactUpdate(ContactBean contactBean) {
        getView().contactUpdate(contactBean);
    }

    @Override
    public void onContactRemove(ContactBean contactBean) {
        getView().contactRemove(contactBean);
    }
}
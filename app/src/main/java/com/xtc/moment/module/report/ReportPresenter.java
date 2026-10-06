package com.xtc.moment.module.report;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.httplib.okhttp.WatchHttpResultException;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.ReportInformParam;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.module.report.helper.ReportDataManage;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ProviderNotifyUtils;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.encode.JSONUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;

import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 举报流程的业务处理：查询举报入口状态、提交举报请求、处理资源已被删除的异常。
 */
public class ReportPresenter extends MvpBasePresenter<IReportView> {

    private static final String TAG = "ReportPresenter";

    private final Context context;
    private final IMomentServe iMomentServe;
    private final ReportDataManage reportDataManage = new ReportDataManage();

    public ReportPresenter(Context context) {
        this.context = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
    }

    /** 提交举报请求。 */
    public void launchedReport(final StartReportRequest startReportRequest) {
        this.iMomentServe.launchedReport(startReportRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        HandlerUtil.runOnBackground(new Runnable() {
                            @Override
                            public void run() {
                                ReportPresenter.this.dealAlreadyDeleteResource(throwable, startReportRequest);
                            }
                        });
                    }

                    @Override
                    public void onNext(String result) {
                        LogUtil.d(TAG, "onNext: s=" + result);
                        if (ReportPresenter.this.getView() == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(result)) {
                            ReportPresenter.this.getView().reportFail("s==null");
                        } else {
                            ReportPresenter.this.getView().reportSuccess();
                        }
                    }
                });
    }

    /**
     * 处理举报时发现资源已被删除的情况：服务端返回 000005 表示动态或评论已不存在，
     * 这里同步清理本地数据并提示用户。
     */
    public void dealAlreadyDeleteResource(Throwable throwable, StartReportRequest startReportRequest) {
        if (!(throwable instanceof WatchHttpResultException)) {
            return;
        }
        if ("000005".equals(((WatchHttpResultException) throwable).serverCode())) {
            int reportMomentType = startReportRequest.getReportMomentType();
            String momentId = startReportRequest.getMomentId();
            String commentId = startReportRequest.getCommentId();
            String hint = this.context.getResources().getString(R.string.net_error);
            if (reportMomentType == ReportActivity.MOMENT_TYPE) {
                DbMoment recordDbMoment = ReportDataRecorder.getRecordDbMoment(momentId);
                if (this.iMomentServe.deleteDBMomentByMomentId(recordDbMoment)) {
                    EventBus.getDefault().post(new EventData(EventData.DELETE_MOMENT, recordDbMoment));
                }
                hint = this.context.getResources().getString(R.string.moment_already_deleted);
            } else if (reportMomentType == ReportActivity.COMMENT_TYPE) {
                DbMomentComment recordComment = ReportDataRecorder.getRecordComment(momentId + commentId);
                this.iMomentServe.deleteDBCommentByMomentIdAndCommentId(momentId, commentId);
                EventBus.getDefault().post(new EventData(EventData.DELETE_COMMENT, recordComment));
                ProviderNotifyUtils.notifyCommentDelete(recordComment, this.context);
                hint = this.context.getResources().getString(R.string.comment_delete_hint);
            }
            final String message = hint;
            HandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showLong(ReportPresenter.this.context, message);
                    if (ReportPresenter.this.getView() != null) {
                        ReportPresenter.this.getView().finishView();
                    }
                }
            });
        }
    }

    public void setReportData(String friendWatchId, String momentId, String commentId, int reportMomentType,
            ArrayList<StartReportRequest.Contents> contents, int informSource, String momentWatchId) {
        this.reportDataManage.setReportData(friendWatchId, momentId, commentId, reportMomentType, contents,
                informSource, momentWatchId);
    }

    public void setInformType(int informType) {
        this.reportDataManage.setInformType(informType);
    }

    /** 补充上报时间与举报人后提交。 */
    public void pushReportData() {
        StartReportRequest startReportRequest = this.reportDataManage.getStartReportRequest();
        startReportRequest.setInformTime(new Date().getTime());
        startReportRequest.setWatchId(MomentApp.getWatchId());
        LogUtil.i(TAG, "pushReportData request " + JSONUtil.toJSON(startReportRequest));
        launchedReport(startReportRequest);
    }

    /** 查询当前用户对该动态/评论的举报入口状态。 */
    public void queryReportInform(int informSource) {
        ReportInformParam reportInformParam = new ReportInformParam(this.reportDataManage.getFriendId());
        reportInformParam.setInformSource(informSource);
        this.iMomentServe.queryReportInform(reportInformParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ReportDataBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (ReportPresenter.this.getView() != null) {
                            ReportPresenter.this.getView().getReportDataFail(throwable.toString());
                        }
                    }

                    @Override
                    public void onNext(ReportDataBean reportDataBean) {
                        if (ReportPresenter.this.getView() == null) {
                            return;
                        }
                        if (reportDataBean != null) {
                            ReportPresenter.this.getView().getReportDataSuccess(reportDataBean);
                        } else {
                            ReportPresenter.this.getView().getReportDataFail("reportDataBean == null");
                        }
                    }
                });
    }
}
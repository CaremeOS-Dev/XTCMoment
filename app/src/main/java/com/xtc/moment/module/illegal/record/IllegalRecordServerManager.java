package com.xtc.moment.module.illegal.record;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.db.bean.DbIllegalRecord;
import com.xtc.moment.module.illegal.dao.DbIllegalRecordServe;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.net.IllegalHttpProxy;
import com.xtc.moment.module.illegal.net.bean.request.InitViolationBean;
import com.xtc.moment.module.illegal.net.bean.response.ViolationInfoBean;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 与服务端同步违规处罚记录。
 */
public class IllegalRecordServerManager {

    private static final String TAG = "IllegalRecordServerManager";

    private DbIllegalRecordServe dbIllegalRecordServe;
    private IllegalHttpProxy httpProxy;

    public interface SnapIllegalInfoListener {
        void onSnapIllegalInfo(boolean success);
    }

    public IllegalRecordServerManager(Context context) {
        this.httpProxy = new IllegalHttpProxy(context);
        this.dbIllegalRecordServe = DbIllegalRecordServe.getInstance(context.getApplicationContext());
    }

    /** 拉取服务端违规信息并同步到本地。 */
    public void snapIllegalInfoByServer(final Context context, final SnapIllegalInfoListener listener) {
        final String watchId = MomentApp.getWatchId();
        this.httpProxy.initViolationInfo(new InitViolationBean(watchId))
                .map(new Func1<ViolationInfoBean, Boolean>() {
                    @Override
                    public Boolean call(ViolationInfoBean violationInfoBean) {
                        if (violationInfoBean == null) {
                            return false;
                        }
                        LogUtil.d(TAG, "violationInfoBean = " + violationInfoBean);
                        IllegalMessageHandler.getInstance(context.getApplicationContext()).getConfig()
                                .setDisableSendDeadLine(violationInfoBean.getDisableSendDeadLine());
                        IllegalMessageHandler.getInstance(context)
                                .setHighRiskIllegal(violationInfoBean.getType() == 1);
                        DbIllegalRecord localRecord = IllegalRecordServerManager.this.dbIllegalRecordServe
                                .queryIllegalRecordByWatchId(watchId);
                        IllegalRecordServerManager.this.dealSnapServerInfo(localRecord, violationInfoBean);
                        LogUtil.i(TAG, "snapIllegalInfoByServer dbIllegalRecord:" + localRecord
                                + " initViolationBean:" + violationInfoBean);
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.i(TAG, "snapIllegalInfoByServer error ", throwable);
                        if (listener != null) {
                            listener.onSnapIllegalInfo(false);
                        }
                    }

                    @Override
                    public void onNext(Boolean success) {
                        LogUtil.i(TAG, "snapIllegalInfoByServer finish");
                        if (listener != null) {
                            listener.onSnapIllegalInfo(true);
                        }
                    }
                });
    }

    private void dealSnapServerInfo(DbIllegalRecord localRecord, ViolationInfoBean violationInfoBean) {
        long now = System.currentTimeMillis();
        int status = violationInfoBean.getStatus();
        if (localRecord == null && status != 0) {
            LogUtil.w(TAG, "开始封禁 " + addOrUpdateIllegalRecord(violationInfoBean));
            return;
        }
        long expireTime;
        try {
            expireTime = Long.parseLong(violationInfoBean.getExpireTime());
        } catch (Exception e) {
            expireTime = 0;
        }
        if (localRecord == null) {
            return;
        }
        if (status == 3 && expireTime > now) {
            LogUtil.i(TAG, "dbIllegalRecord != null, set disable state = "
                    + addOrUpdateIllegalRecord(violationInfoBean));
            return;
        }
        if (status == 0 || localRecord.getEndTime() <= now) {
            LogUtil.w(TAG, "解除封禁 " + this.dbIllegalRecordServe.deleteIllegalRecord(localRecord.getWatchId()));
            return;
        }
        if (String.valueOf(localRecord.getEndTime()).equals(violationInfoBean.getExpireTime())
                && localRecord.getSanctionState() == status) {
            return;
        }
        LogUtil.w(TAG, "同步服务器数据 " + addOrUpdateIllegalRecord(violationInfoBean));
    }

    private boolean addOrUpdateIllegalRecord(ViolationInfoBean violationInfoBean) {
        String startTime = violationInfoBean.getStartTime();
        String expireTime = violationInfoBean.getExpireTime();
        String watchId = MomentApp.getWatchId();
        DbIllegalRecord record = new DbIllegalRecord();
        record.setWatchId(watchId);
        record.setStartTime(TextUtils.isEmpty(startTime) ? 0L : Long.parseLong(startTime));
        record.setEndTime(TextUtils.isEmpty(startTime) ? 0L : Long.parseLong(expireTime));
        record.setSanctionState(violationInfoBean.getStatus());
        record.setIllegalCount(violationInfoBean.getDelayCount());
        if (record.getEndTime() < System.currentTimeMillis()) {
            record.setSanctionState(0);
        }
        return this.dbIllegalRecordServe.addOrUpdateIllegalRecord(record);
    }
}
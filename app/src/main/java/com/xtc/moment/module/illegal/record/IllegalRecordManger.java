package com.xtc.moment.module.illegal.record;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.db.bean.DbIllegalRecord;
import com.xtc.moment.module.illegal.config.IllegalSanctionConfig;
import com.xtc.moment.module.illegal.dao.DbIllegalRecordServe;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 本地违规记录管理，负责维护“禁言”状态并在状态变化时通知监听者。
 */
public class IllegalRecordManger {

    private static final String TAG = "IllegalRecordManger";

    private Context mContext;
    private IllegalSanctionConfig config;
    private DbIllegalRecordServe dbIllegalRecordServe;
    private IllegalStateChangeListener listener;
    private boolean isDisableSend;

    public interface IllegalStateChangeListener {
        void onDisableSendStateChange(boolean isDisableSend);
    }

    public IllegalRecordManger(Context context, IllegalSanctionConfig config,
            IllegalStateChangeListener listener) {
        this.listener = listener;
        this.mContext = context;
        this.config = config;
        this.dbIllegalRecordServe = DbIllegalRecordServe.getInstance(this.mContext.getApplicationContext());
    }

    public void setConfig(IllegalSanctionConfig config) {
        this.config = config;
    }

    public boolean isDisableSend() {
        return this.isDisableSend;
    }

    public void setDisableSend(boolean isDisableSend, boolean notifyChange) {
        if (this.isDisableSend != isDisableSend && notifyChange && this.listener != null) {
            this.listener.onDisableSendStateChange(isDisableSend);
        }
        this.isDisableSend = isDisableSend;
    }

    /**
     * 解除处罚：删除本地违规记录并把状态还原为可发送。
     */
    public void deBlockIllegalPunish(IllegalRecordServerManager serverManager,
            final IllegalMessageHandler.ActiveChangeIllegalStateListener activeChangeListener) {
        Observable.just(false)
                .map(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean ignored) {
                        String watchId = MomentApp.getWatchId();
                        DbIllegalRecord record = IllegalRecordManger.this.dbIllegalRecordServe
                                .queryIllegalRecordByWatchId(watchId);
                        IllegalRecordManger.this.dbIllegalRecordServe.deleteIllegalRecord(watchId);
                        IllegalRecordManger.this.setDisableSend(false, false);
                        return record != null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean hadRecord) {
                        if (hadRecord && activeChangeListener != null) {
                            LogUtil.i(TAG, "deBlockIllegalPunish activeChangeIllegalStateListener");
                            activeChangeListener.onActiveChangeIllegalState(0);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.w(TAG, "deBlockIllegalPunish error", throwable);
                    }
                });
    }
}
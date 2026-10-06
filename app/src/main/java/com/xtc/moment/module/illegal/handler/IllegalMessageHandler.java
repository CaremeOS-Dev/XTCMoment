package com.xtc.moment.module.illegal.handler;

import android.content.Context;

import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbIllegalRecord;
import com.xtc.moment.module.illegal.config.IllegalSanctionConfig;
import com.xtc.moment.module.illegal.dao.DbIllegalRecordServe;
import com.xtc.moment.module.illegal.record.IllegalRecordManger;
import com.xtc.moment.module.illegal.record.IllegalRecordServerManager;
import com.xtc.moment.module.illegal.state.DisableState;
import com.xtc.moment.module.illegal.state.IllegalBaseState;
import com.xtc.moment.module.illegal.state.NormalState;
import com.xtc.moment.module.illegal.util.ConfigTimeFormatUtil;
import com.xtc.moment.module.illegal.widget.HintIllegalContentDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 违规处罚的总入口：初始化处罚配置、维护当前违规状态、控制是否禁言。
 */
public class IllegalMessageHandler implements IllegalRecordManger.IllegalStateChangeListener {

    private static final String TAG = "IllegalMessageHandler";

    private static volatile IllegalMessageHandler instance;

    private final String accountWatchId = MomentApp.getWatchId();
    private List<InitIllegalListener> initIllegalListenerList = new ArrayList<>();

    private IllegalSanctionConfig config = new IllegalSanctionConfig.Builder().build();
    private DbIllegalRecordServe dbIllegalRecordServe;
    private IllegalRecordServerManager illegalRecordServerManager;
    private IllegalRecordManger illegalRecordManger;
    private DbIllegalRecord dbIllegalRecord;
    private NormalState normalState;
    private DisableState disableState;
    private IllegalBaseState state;
    private ActiveChangeIllegalStateListener activeChangeIllegalStateListener;
    private volatile boolean isInitHandler;
    private volatile boolean isInitIngHandler;
    private boolean isHighRiskIllegal = false;

    public interface InitIllegalListener {
        void onIIllegalFinish();
    }

    public interface InitIllegalStateListener {
        void onIIllegalStateFinish();
    }

    public interface ActiveChangeIllegalStateListener {
        void onActiveChangeIllegalState(int state);
    }

    public static IllegalMessageHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (IllegalMessageHandler.class) {
                if (instance == null) {
                    instance = new IllegalMessageHandler(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private IllegalMessageHandler(Context context) {
        this.dbIllegalRecordServe = DbIllegalRecordServe.getInstance(context.getApplicationContext());
        this.illegalRecordServerManager = new IllegalRecordServerManager(context.getApplicationContext());
        this.illegalRecordManger = new IllegalRecordManger(context.getApplicationContext(), this.config, this);
        this.dbIllegalRecord = this.dbIllegalRecordServe.queryIllegalRecordByWatchId(this.accountWatchId);
        this.normalState = new NormalState(context.getApplicationContext(), this.illegalRecordManger);
        this.disableState = new DisableState(context.getApplicationContext());
    }

    /** 初始化违规配置，已有初始化在进行时把回调加入等待队列。 */
    public void initIllegalConfig(Context context, IllegalSanctionConfig config,
            final InitIllegalListener initIllegalListener) {
        if (this.isInitHandler) {
            if (initIllegalListener != null) {
                MainHandlerUtil.post(new Runnable() {
                    @Override
                    public void run() {
                        initIllegalListener.onIIllegalFinish();
                    }
                });
            }
            return;
        }
        synchronized (IllegalMessageHandler.class) {
            if (this.isInitIngHandler) {
                if (initIllegalListener != null) {
                    this.initIllegalListenerList.add(initIllegalListener);
                }
                return;
            }
            this.initIllegalListenerList.add(initIllegalListener);
            this.isInitIngHandler = true;
            this.config = config;
            this.illegalRecordManger.setConfig(config);
            LogUtil.i(TAG, "initIllegalConfig config:" + config);
            this.state = this.normalState;
            this.illegalRecordServerManager.snapIllegalInfoByServer(context.getApplicationContext(),
                    new IllegalRecordServerManager.SnapIllegalInfoListener() {
                        @Override
                        public void onSnapIllegalInfo(final boolean success) {
                            LogUtil.i(TAG, "start initCurrentIllegalState");
                            IllegalMessageHandler.this.initCurrentIllegalState(new InitIllegalStateListener() {
                                @Override
                                public void onIIllegalStateFinish() {
                                    LogUtil.i(TAG, "onIIllegalStateFinish");
                                    IllegalMessageHandler.this.dispatcherInitIllegalListener(success);
                                }
                            });
                        }
                    });
        }
    }

    private void dispatcherInitIllegalListener(boolean success) {
        this.isInitHandler = success;
        this.isInitIngHandler = false;
        for (int i = 0; i < this.initIllegalListenerList.size(); i++) {
            InitIllegalListener initIllegalListener = this.initIllegalListenerList.get(i);
            if (initIllegalListener != null) {
                initIllegalListener.onIIllegalFinish();
            }
        }
        this.initIllegalListenerList.clear();
    }

    public boolean isInitHandler() {
        return this.isInitHandler;
    }

    public IllegalSanctionConfig getConfig() {
        return this.config;
    }

    public void setActiveChangeIllegalStateListener(ActiveChangeIllegalStateListener listener) {
        this.activeChangeIllegalStateListener = listener;
    }

    public void removeActiveChangeIllegalStateListener() {
        this.activeChangeIllegalStateListener = null;
    }

    private void setState(IllegalBaseState newState) {
        newState.leave();
        this.state = newState;
        newState.into();
    }

    /** 从本地数据库读取当前违规状态并切换。 */
    public void initCurrentIllegalState(final InitIllegalStateListener initIllegalStateListener) {
        Observable.just(false)
                .map(new Func1<Boolean, DbIllegalRecord>() {
                    @Override
                    public DbIllegalRecord call(Boolean ignored) {
                        IllegalMessageHandler.this.dbIllegalRecord =
                                IllegalMessageHandler.this.dbIllegalRecordServe
                                        .queryIllegalRecordByWatchId(IllegalMessageHandler.this.accountWatchId);
                        LogUtil.i(TAG, "initCurrentIllegalState dbIllegalRecord:"
                                + IllegalMessageHandler.this.dbIllegalRecord);
                        return IllegalMessageHandler.this.dbIllegalRecord;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<DbIllegalRecord>() {
                    @Override
                    public void call(DbIllegalRecord record) {
                        if (record == null) {
                            LogUtil.i(TAG, "dbIllegalRecord is null, current state set normal");
                            if (initIllegalStateListener != null) {
                                initIllegalStateListener.onIIllegalStateFinish();
                            }
                            return;
                        }
                        if (record.getSanctionState() == 3) {
                            IllegalMessageHandler.this.illegalRecordManger.setDisableSend(true, true);
                        } else {
                            IllegalMessageHandler.this.setState(IllegalMessageHandler.this.normalState);
                        }
                        IllegalMessageHandler.this.state.setEndTime(record.getEndTime());
                        if (initIllegalStateListener != null) {
                            initIllegalStateListener.onIIllegalStateFinish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "initCurrentIllegalState error: ", throwable);
                    }
                });
    }

    public IllegalRecordManger getIllegalRecordManger() {
        return this.illegalRecordManger;
    }

    /** 服务端下发禁言处罚后同步状态。 */
    public void disableSendPunish(Context context) {
        LogUtil.i(TAG, "disableSendPunish");
        setState(this.disableState);
        this.illegalRecordServerManager.snapIllegalInfoByServer(context.getApplicationContext(),
                new IllegalRecordServerManager.SnapIllegalInfoListener() {
                    @Override
                    public void onSnapIllegalInfo(boolean success) {
                        LogUtil.i(TAG, "start initCurrentIllegalState");
                        IllegalMessageHandler.this.initCurrentIllegalState(new InitIllegalStateListener() {
                            @Override
                            public void onIIllegalStateFinish() {
                                LogUtil.i(TAG, "onIIllegalStateFinish");
                                if (IllegalMessageHandler.this.activeChangeIllegalStateListener != null) {
                                    IllegalMessageHandler.this.activeChangeIllegalStateListener
                                            .onActiveChangeIllegalState(
                                                    IllegalMessageHandler.this.state.getCurrentState());
                                }
                            }
                        });
                    }
                });
    }

    public void deBlockIllegalPunish() {
        LogUtil.i(TAG, "deBlockIllegalPunish");
        setState(this.normalState);
        this.illegalRecordManger.deBlockIllegalPunish(this.illegalRecordServerManager,
                this.activeChangeIllegalStateListener);
    }

    private DbIllegalRecord checkDbIllegalRecord() {
        DbIllegalRecord record = this.dbIllegalRecord;
        return record == null ? this.dbIllegalRecordServe.queryIllegalRecordByWatchId(this.accountWatchId) : record;
    }

    /** 展示禁言提示弹窗。 */
    public HintIllegalContentDialog showDisableSendMessageHintDialog(Context context,
            HintIllegalContentDialog.HintClickListener hintClickListener) {
        DbIllegalRecord record = checkDbIllegalRecord();
        if (context == null || record == null) {
            LogUtil.d(TAG, "showDisableSendMessageHintDialog() returned: " + record);
            return null;
        }
        long endTime = record.getEndTime();
        HintIllegalContentDialog dialog = new HintIllegalContentDialog(context, endTime);
        dialog.setHintClickListener(hintClickListener);
        LogUtil.d(TAG, "showDisableSendMessageHintDialog() called with: context = [" + context
                + "], hintClickListener = [" + record + "]" + this.config);
        dialog.setContent(null, null, context.getResources()
                .getString(R.string.text_disable_send_message_hint,
                        ConfigTimeFormatUtil.formatDisableSendExpireTime(context, endTime)), true);
        dialog.show();
        return dialog;
    }

    /** 状态过期时自动还原。返回 true 表示状态刚被还原。 */
    public boolean refreshCurrentState() {
        IllegalBaseState currentState = this.state;
        if (currentState == null) {
            return true;
        }
        long endTime = currentState.getEndTime();
        long now = System.currentTimeMillis();
        if (endTime == 0 || now < endTime) {
            return false;
        }
        LogUtil.i(TAG, "state:" + this.state.getCurrentState() + " 状态过期，还原状态");
        deBlockIllegalPunish();
        return true;
    }

    public boolean checkNeedDisableSend() {
        if (refreshCurrentState()) {
            return false;
        }
        return isDisableSend();
    }

    public boolean isDisableSend() {
        IllegalRecordManger recordManger = this.illegalRecordManger;
        if (recordManger == null) {
            return false;
        }
        boolean disableSend = recordManger.isDisableSend();
        if (disableSend) {
            LogUtil.i(TAG, "checkNeedDisableSend is disableSend");
        }
        return disableSend;
    }

    @Override
    public void onDisableSendStateChange(boolean isDisableSend) {
        LogUtil.i(TAG, "onDisableSendStateChange isDisableSend:" + isDisableSend);
        if (isDisableSend) {
            setState(this.disableState);
        } else {
            setState(this.normalState);
        }
    }

    public void showHighRiskHintDialog(Context context, HintIllegalContentDialog.HintClickListener hintClickListener) {
        if (this.state == null) {
            LogUtil.i(TAG, "showHighRiskHintDialog, state or listener is empty");
            return;
        }
        HintIllegalContentDialog dialog = new HintIllegalContentDialog(context);
        dialog.setHintClickListener(hintClickListener);
        LogUtil.d(TAG, "showDisableSendMessageHintDialog() called with: context = [" + context + "]");
        dialog.setContent(null, context.getResources().getString(R.string.high_risk_hint_tittle),
                context.getResources().getString(R.string.high_risk_hint_content), false);
        dialog.show();
    }

    /** 高风险内容触发的禁言处理。 */
    public void highRiskDisableSend(Context context) {
        LogUtil.i(TAG, "highRiskDisableSend");
        setState(this.disableState);
        this.illegalRecordServerManager.snapIllegalInfoByServer(context.getApplicationContext(),
                new IllegalRecordServerManager.SnapIllegalInfoListener() {
                    @Override
                    public void onSnapIllegalInfo(final boolean success) {
                        if (!IllegalMessageHandler.this.config.isEnableReview()) {
                            LogUtil.i(TAG, "当前未打开违规内容审查处理，不初始化当前违规状态");
                            return;
                        }
                        IllegalMessageHandler.this.initCurrentIllegalState(new InitIllegalStateListener() {
                            @Override
                            public void onIIllegalStateFinish() {
                                LogUtil.i(TAG, "onIIllegalStateFinish");
                                IllegalMessageHandler.this.dispatcherInitIllegalListener(success);
                            }
                        });
                    }
                });
    }

    public boolean isHighRiskIllegal() {
        return this.isHighRiskIllegal;
    }

    public void setHighRiskIllegal(boolean highRiskIllegal) {
        this.isHighRiskIllegal = highRiskIllegal;
    }
}
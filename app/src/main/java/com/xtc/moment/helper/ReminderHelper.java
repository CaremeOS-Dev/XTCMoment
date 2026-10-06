package com.xtc.moment.helper;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbReminder;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.net.bean.ReminderConfig;
import com.xtc.moment.push.bean.ImReminderData;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.utils.encode.JSONUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ReminderHelper {

    public static final String M_TAG = "mmReminder";
    public static final long ONE_WEEK = 604800000L;

    private static final AtomicReference<ReminderHelper> HELPER = new AtomicReference<>();

    private final Context mContext;
    private IAccountInfoServe accountInfoServer;
    private IMomentServe iMomentServe;
    private MomentsRepository momentsRepository;
    private int remoteConfigCount = 0;
    private boolean isOnSharePage;

    public static ReminderHelper get(Context context) {
        ReminderHelper helper;
        do {
            ReminderHelper current = HELPER.get();
            if (current != null) {
                return current;
            }
            helper = new ReminderHelper(context.getApplicationContext());
        } while (!HELPER.compareAndSet(null, helper));
        return helper;
    }

    private ReminderHelper(Context context) {
        mContext = context;
        accountInfoServer = AccountInfoServerImpl.getInstance(context.getApplicationContext());
        iMomentServe = MomentServeImpl.getInstance(context.getApplicationContext());
        momentsRepository = MomentsRepository.getInstance(MomentsRemoteDataSource.getInstance(context), MomentsLocalDataSource.getInstance(context));
    }

    public void getReminderConfig(final Constants.RCType rcType, final int eventType) {
        Observable.fromCallable(new Func0<Boolean>() {
            @Override
            public Boolean call() {
                boolean needUpdate = true;
                if (rcType != Constants.RCType.REMOTE) {
                    if (!lastPullTimeOverEightHours() && !iMomentServe.queryAllReminders().isEmpty()) {
                        needUpdate = false;
                    }
                }
                return Boolean.valueOf(needUpdate);
            }
        }).flatMap(new Func1<Boolean, Observable<ReminderConfig>>() {
            @Override
            public Observable<ReminderConfig> call(Boolean needUpdate) {
                if (!needUpdate.booleanValue()) {
                    startCheckReminder(eventType, rcType, "ReminderConfig no need update...");
                    return null;
                }
                LogUtil.d(M_TAG, "start getReminderConfig..");
                return momentsRepository.getReminderConfig();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<ReminderConfig>() {
            @Override
            public void call(ReminderConfig reminderConfig) {
                if (reminderConfig == null) {
                    startCheckReminder(eventType, rcType, "ReminderConfig is null");
                    return;
                }
                List<DbReminder> reminderList = reminderConfig.getReminderList();
                if (CollectionUtil.isEmpty(reminderList)) {
                    startCheckReminder(eventType, rcType, "reminders list is empty");
                    return;
                }
                List<DbReminder> filterReminders = getFilterReminders(reminderList);
                LogUtil.d(M_TAG, "getReminderConfig size=" + filterReminders.size());
                if (iMomentServe.insertReminders(filterReminders)) {
                    LogUtil.d(M_TAG, "insertRemindersConfig success ");
                    SharedTool.savePullReminderConfigTime(mContext, System.currentTimeMillis());
                    if (rcType == Constants.RCType.REMOTE) {
                        remoteCount("remote success..");
                    } else {
                        LogUtil.i(M_TAG, "检查过期推送。。。");
                        iMomentServe.deleteIMReminders(1);
                    }
                } else {
                    LogUtil.e(M_TAG, "insertReminders failed ");
                }
                startCheckReminder(eventType, rcType, "normal getReminderConfig");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (rcType == Constants.RCType.REMOTE) {
                    remoteCount("remote fail..");
                }
                LogUtil.e(M_TAG, "getReminderConfig >>> ", throwable);
            }
        });
    }

    private void remoteCount(String reason) {
        int count = remoteConfigCount;
        if (count < 2) {
            remoteConfigCount = count + 1;
            LogUtil.w(M_TAG, "remoteConfigCount= " + remoteConfigCount + ", by " + reason);
        }
    }

    private void startCheckReminder(int eventType, Constants.RCType rcType, String message) {
        LogUtil.w(M_TAG, message);
        EventBus.getDefault().post(new EventData(eventType, rcType));
    }

    private List<DbReminder> getFilterReminders(List<DbReminder> reminders) {
        ArrayList<DbReminder> result = new ArrayList<>();
        HashSet<String> labels = new HashSet<>();
        for (DbReminder reminder : reminders) {
            if (labels.add(reminder.getLabel())) {
                result.add(reminder);
            }
        }
        return result;
    }

    public void dealImReminder(final boolean onSharePage) {
        isOnSharePage = onSharePage;
        Observable.just(DbIMReminder.UNSET).map(new Func1<String, List<DbIMReminder>>() {
            @Override
            public List<DbIMReminder> call(String status) {
                return iMomentServe.queryIMRemindersByStatus(status);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<List<DbIMReminder>>() {
            @Override
            public void call(List<DbIMReminder> reminders) {
                if (CollectionUtil.isEmpty(reminders)) {
                    LogUtil.e(M_TAG, "dbIMReminders is empty, 是否个人动态=" + onSharePage);
                    return;
                }
                LogUtil.v(M_TAG, "need deal dbIMReminders size: " + reminders.size() + ", 是否个人动态=" + onSharePage);
                HashMap<String, DbReminder> labelConfigMap = getIMLabelConfigMap(reminders);
                boolean hasUnmatched = !labelConfigMap.isEmpty() && reminders.size() != labelConfigMap.size();
                if (labelConfigMap.isEmpty() || hasUnmatched) {
                    if (hasUnmatched) {
                        searchMomentForReminder(labelConfigMap);
                    } else {
                        LogUtil.w(M_TAG, "configMap is Empty");
                    }
                    remoteReminderConfig();
                    return;
                }
                searchMomentForReminder(labelConfigMap);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(M_TAG, "dealImReminder >>> ", throwable);
            }
        });
    }

    private void searchMomentForReminder(HashMap<String, DbReminder> reminderMap) {
        if (reminderMap.isEmpty()) {
            return;
        }
        List<DbMoment> moments = iMomentServe.queryByMomentIds(reminderMap.keySet());
        if (CollectionUtil.isEmpty(moments)) {
            LogUtil.e(M_TAG, "searchMomentForReminder moments is empty");
            return;
        }
        String watchId = accountInfoServer.getWatchAccountInfo().getWatchId(mContext);
        for (DbMoment moment : moments) {
            if (moment != null) {
                DbReminder reminder = reminderMap.get(moment.getMomentId());
                if (Objects.equals(moment.getWatchId(), watchId)) {
                    moment.setReminderContent(reminder.getSendContent());
                    moment.setReminderUrl(reminder.getSendH5Url());
                } else {
                    moment.setReminderContent(reminder.getReceiveContent());
                    moment.setReminderUrl(reminder.getReceiveH5Url());
                }
                iMomentServe.updateMoment(moment);
                iMomentServe.updateIMReminderStatus(moment.getMomentId(), DbIMReminder.DONE);
                EventBus.getDefault().post(new EventData(isOnSharePage ? 22 : 20, moment));
            }
        }
    }

    private void remoteReminderConfig() {
        if (remoteConfigCount == 2) {
            uploadNotMatchLabel(iMomentServe.queryIMRemindersByStatus(DbIMReminder.NOT_MATCH));
            remoteConfigCount = 0;
        } else {
            LogUtil.w(M_TAG, "start remote reminderConfig...");
            HandlerUtil.runOnBackgroundDelay(new Runnable() {
                @Override
                public void run() {
                    getReminderConfig(Constants.RCType.REMOTE, isOnSharePage ? 21 : 19);
                }
            }, com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
        }
    }

    private HashMap<String, DbReminder> getIMLabelConfigMap(List<DbIMReminder> reminders) {
        HashMap<String, DbReminder> result = new HashMap<>();
        for (DbIMReminder reminder : reminders) {
            if (reminder != null && !TextUtils.isEmpty(reminder.getLabel())) {
                DbReminder config = iMomentServe.queryReminderByLabel(reminder.getLabel());
                if (config != null) {
                    result.put(reminder.getMomentId(), config);
                } else if (remoteConfigCount == 2) {
                    iMomentServe.updateIMReminderStatus(reminder.getMomentId(), DbIMReminder.NOT_MATCH);
                }
            }
        }
        return result;
    }

    private boolean lastPullTimeOverEightHours() {
        return System.currentTimeMillis() - SharedTool.getLastPullReminderConfigTime(mContext) > 28800000L;
    }

    public void receivedImReminder(String json) {
        try {
            ImReminderData reminderData = JSONUtil.fromJSON(json, ImReminderData.class);
            if (!dataAvailable(reminderData)) {
                LogUtil.e(M_TAG, "reminderData数据异常不可用");
                return;
            }
            String label = reminderData.getLabel();
            String availableId = getAvailableId(reminderData);
            if (TextUtils.isEmpty(label)) {
                LogUtil.w(M_TAG, "推送label为空，momentId=" + availableId);
                return;
            }
            if (iMomentServe.insertIMReminder(new DbIMReminder(label, availableId))) {
                LogUtil.d(M_TAG, "温馨提醒推送插入成功: label=" + label + ",momentId=" + availableId);
                EventBus.getDefault().post(new EventType(5));
                uploadImReminderInfo(label);
                return;
            }
            LogUtil.e(M_TAG, "温馨提醒推送插入失败: label=" + label + ",momentId=" + availableId);
        } catch (Exception e) {
            LogUtil.e(M_TAG, "温馨提醒推送处理异常：" + e.getMessage());
        }
    }

    private boolean dataAvailable(ImReminderData reminderData) {
        if (reminderData == null) {
            return false;
        }
        return !(TextUtils.isEmpty(reminderData.getMomentId()) && TextUtils.isEmpty(reminderData.getBusinessId()));
    }

    private String getAvailableId(ImReminderData reminderData) {
        return TextUtils.isEmpty(reminderData.getBusinessId()) ? reminderData.getMomentId() : reminderData.getBusinessId();
    }

    private void uploadImReminderInfo(String label) {
        int labelCount = SharedTool.getReminderLabelCount(mContext, label);
        int total = labelCount + 1;
        LogUtil.v(M_TAG, "温馨提示埋点: label=" + label + ",已触发=" + labelCount + ",总次数=" + total);
        SharedTool.saveReminderLabelCount(mContext, label, total);
        MomentBehavior.uploadMomentReminderInfo(label, total);
    }

    private void uploadNotMatchLabel(List<DbIMReminder> reminders) {
        if (CollectionUtil.isEmpty(reminders)) {
            return;
        }
        LogUtil.e(M_TAG, "remote 2 time stop, not match list=" + reminders);
        StringBuilder builder = new StringBuilder();
        for (DbIMReminder reminder : reminders) {
            builder.append("label:");
            builder.append(reminder.getLabel());
            builder.append(",momentId:");
            builder.append(reminder.getMomentId());
            builder.append("_");
        }
        MomentBehavior.uploadNotMatchLabel(builder.toString());
        iMomentServe.deleteIMReminders(2);
    }
}
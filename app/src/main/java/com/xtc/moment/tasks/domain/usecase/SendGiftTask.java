package com.xtc.moment.tasks.domain.usecase;

import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.common.base.Preconditions;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.util.TimeUtils;

import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 送礼物任务：真实赠送走网络请求，本地赠送走空 Observable；两者共用同一套本地频次限制逻辑。
 */
public class SendGiftTask extends AbsTask<SendGiftTask.RequestValues, AbsTask.ResponseValue> {

    public static final int SEND_EXCEPTION = 30;
    public static final int SEND_FAIL = 20;
    public static final int SEND_SUCCESS = 10;
    private static final String TAG = "SendGiftTask";

    private final IMomentsDataSource mMomentsRepository;

    public SendGiftTask(IMomentsDataSource momentsRepository) {
        this.mMomentsRepository = Preconditions.checkNotNull(momentsRepository);
        setThreadType(2);
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        LogUtil.e(TAG, "executeTask: " + requestValues.isSend());
        if (requestValues.isSend()) {
            this.mMomentsRepository.sendGift(requestValues.getSendGiftRequest()).map(new Func1<NormalResultBean, Integer>() {
                @Override
                public Integer call(NormalResultBean resultBean) {
                    return saveGiftRecord(requestValues, resultBean, true);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer result) {
                    TaskCallback<AbsTask.ResponseValue> callback = SendGiftTask.this.getTaskCallback();
                    if (result == null) {
                        callback.uiError();
                    } else if (result.intValue() == SEND_SUCCESS) {
                        callback.uiSuccess(new ResponseValue());
                    } else {
                        callback.uiError();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    SendGiftTask.this.getTaskCallback().uiError();
                }
            });
        } else {
            Observable.just((NormalResultBean) null).map(new Func1<NormalResultBean, Integer>() {
                @Override
                public Integer call(NormalResultBean resultBean) {
                    return saveGiftRecord(requestValues, resultBean, false);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer result) {
                    LogUtil.d(SendGiftTask.TAG, "call: WQH" + result);
                    TaskCallback<AbsTask.ResponseValue> callback = SendGiftTask.this.getTaskCallback();
                    if (result == null) {
                        callback.uiError();
                    } else if (result.intValue() == SEND_SUCCESS) {
                        callback.uiSuccess(new ResponseValue());
                    } else {
                        callback.uiError();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    SendGiftTask.this.getTaskCallback().uiError();
                }
            });
        }
    }

    /**
     * 保存礼物记录：首次赠送直接入库；已有记录时按 10 分钟内最多 5 次的规则累加。
     */
    private Integer saveGiftRecord(RequestValues requestValues, NormalResultBean resultBean, boolean requireNullRecord) {
        SendGiftRequest request = requestValues.getSendGiftRequest();
        DbGiftRecord record = this.mMomentsRepository.queryGiftByMomentId(request.getMomentId());
        LogUtil.d(TAG, "sendGift call: " + record);
        if ((!requireNullRecord || record == null) && resultBean != null && "000001".equals(resultBean.getCode())) {
            return this.mMomentsRepository.addGiftRecordMessage(DbGiftRecord.buildGift(request.getMomentId(), request.getFriendId(), request.getWatchId(), "ces")) ? SEND_SUCCESS : SEND_FAIL;
        }
        long lastGiftTime = record.getLastGiftTime();
        int count = record.getCount() + 1;
        if (count > 5) {
            Integer diffMinutes = TimeUtils.getDateDifferenceToMin(new Date(System.currentTimeMillis()), new Date(lastGiftTime));
            boolean withinLimit = diffMinutes.intValue() <= 10;
            LogUtil.d(TAG, "call: " + diffMinutes + withinLimit);
            if (!withinLimit) {
                record.setCount(1);
                record.setLastGiftTime(System.currentTimeMillis());
            } else {
                record.setCount(count);
                this.mMomentsRepository.updateGift(record);
                return SEND_FAIL;
            }
        } else {
            record.setCount(count);
        }
        return this.mMomentsRepository.updateGift(record) ? SEND_SUCCESS : SEND_FAIL;
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private boolean isSend;
        private SendGiftRequest sendGiftRequest;

        public RequestValues(SendGiftRequest sendGiftRequest, boolean isSend) {
            this.sendGiftRequest = sendGiftRequest;
            this.isSend = isSend;
        }

        public SendGiftRequest getSendGiftRequest() {
            return this.sendGiftRequest;
        }

        public void setSendGiftRequest(SendGiftRequest sendGiftRequest) {
            this.sendGiftRequest = sendGiftRequest;
        }

        public boolean isSend() {
            return this.isSend;
        }

        public void setSend(boolean isSend) {
            this.isSend = isSend;
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private int result;

        public int getResult() {
            return this.result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }
}

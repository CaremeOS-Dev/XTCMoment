package com.xtc.moment.receiver;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.receiver.BaseContactChangeReceiver;
import com.xtc.log.LogUtil;
import com.xtc.moment.helper.UnreadHelper;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;

import org.greenrobot.eventbus.EventBus;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 联系人变化接收器：同步点赞记录昵称，联系人被删时清理对应动态。
 */
public class ContactChangeReceiver extends BaseContactChangeReceiver {

    private static final String TAG = "XTC_MOMENT_ContactChangeReceiver";

    @Override
    protected void onContactAdd(Context context, ContactBean contactBean) {
    }

    @Override
    protected void onContactUpdate(Context context, ContactBean contactBean) {
        LogUtil.i(TAG, "onContactUpdate: " + contactBean);
        if (contactBean == null) {
            return;
        }
        updateLikeMessageName(context, contactBean.getFriendWatchId(), contactBean.getName());
    }

    @Override
    protected void onContactRemove(Context context, ContactBean contactBean) {
        LogUtil.i(TAG, "onContactRemove: " + contactBean);
        if (contactBean == null) {
            return;
        }
        deleteFriendInfo(context, contactBean.getFriendWatchId());
    }

    private void updateLikeMessageName(Context context, final String watchId, final String name) {
        LogUtil.i(TAG, "updateLikeMessageName() called with: context = [" + context + "], watchId = [" + watchId + "], name = [" + name + "]");
        if (context == null || TextUtils.isEmpty(watchId) || TextUtils.isEmpty(name)) {
            return;
        }
        final IMomentServe momentServe = MomentServeImpl.getInstance(context);
        if (momentServe == null) {
            LogUtil.e(TAG, "updateLikeMessageName: serve is null.");
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                momentServe.updateLikeMessageName(watchId, name);
            }
        });
    }

    private void deleteFriendInfo(final Context context, final String friendWatchId) {
        LogUtil.i(TAG, "deleteFriendInfo: friendWatchId = [" + friendWatchId + "]");
        if (TextUtils.isEmpty(friendWatchId)) {
            return;
        }
        final IMomentServe momentServe = MomentServeImpl.getInstance(context);
        if (momentServe == null) {
            LogUtil.e(TAG, "deleteFriendInfo: serve is null.");
            return;
        }
        Observable.just(friendWatchId).map(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String value) {
                return Boolean.valueOf(momentServe.deleteMomentByWatchId(friendWatchId));
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(ContactChangeReceiver.TAG, "deleteFriendInfo#onError: ", throwable);
            }

            @Override
            public void onNext(Boolean deleted) {
                LogUtil.i(ContactChangeReceiver.TAG, "deleteFriendInfo#onNext: " + deleted);
                UnreadHelper.getInstance(context).showUnreadNumber();
                UnreadHelper.getInstance(context).dealUnreadPoint();
                context.getContentResolver().notifyChange(Uri.parse("content://com.xtc.moment.momentProvider/momentunread"), null);
                context.getContentResolver().notifyChange(Uri.parse("content://com.xtc.moment.likeMessageProvider/likeMessageunread"), null);
                if (deleted.booleanValue()) {
                    EventBus.getDefault().post(new EventData(7, friendWatchId));
                }
            }
        });
    }
}
package com.xtc.moment.service;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.aitext.manager.AITextManager;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.event.IMMomentMsgData;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.helper.UnreadHelper;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.StringConstant;
import com.xtc.moment.module.bean.EventType;
import com.xtc.moment.module.bean.NetInvalidMoment;
import com.xtc.moment.module.illegal.config.IllegalConfigHandler;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.net.bean.CommentBean;
import com.xtc.moment.net.bean.ImMyNameBean;
import com.xtc.moment.net.bean.ImMyNameInfoBean;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.push.MomentPushType;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MessageTransitionServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.CommentDeleteBean;
import com.xtc.moment.serve.bean.MomentDeleteBean;
import com.xtc.moment.serve.bean.MomentMessageData;
import com.xtc.moment.serve.bean.MomentsBatchDeleteBean;
import com.xtc.moment.serve.delete.MomentDeleteServe;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.NotifyUtils;
import com.xtc.moment.util.TimeUtils;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class IMMomentService extends IntentService {

    private static final String TAG = "XTC_MOMENT_IMMomentService";

    private IAccountInfoServe accountInfoServer;
    private ContactManager contactManager;
    private IMomentServe iMomentServe;

    public IMMomentService() {
        super("IMMomentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            LogUtil.e(TAG, "onHandleIntent: intent is null.");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            LogUtil.e(TAG, "onHandleIntent: action is null.");
            return;
        }
        init();
        if (StringConstant.MOMENT_IM_ACTION.equals(action)) {
            dealIMPush(intent.getIntExtra(StringConstant.MOMENT_PUSH_TYPE, 0), intent.getStringExtra(StringConstant.MOMENT_PUSH_MSG));
        }
    }

    private void init() {
        iMomentServe = MomentServeImpl.getInstance(this);
        accountInfoServer = AccountInfoServerImpl.getInstance(this);
        contactManager = ContactManager.getInstance(this);
    }

    private void dealIMPush(int type, String content) {
        LogUtil.d(TAG, "dealIMPush: type = [" + type + "], content = [" + content + "]");
        if (type == MomentPushType.WATCH_UPDATE_MY_HEAD_ICON) {
            LogUtil.d(TAG, "update name :" + content);
            if (content.contains("name")) {
                changeMyName(content);
            }
        } else if (type == MomentPushType.WATCH_MOMENT_CANCEl_LIKE) {
            handleCancelLikeMoment(content);
        } else if (type == MomentPushType.WATCH_BATCH_MOMENTS_DELETE) {
            handleMomentsBatchDelete(content);
        } else if (type == MomentPushType.PUSH_TYPE_EQUITY_CHANGE) {
            handlePrerogativeChange(content);
        } else if (type == MomentPushType.MOMENT_REMINDER) {
            receivedImReminder(content);
        } else if (type == MomentPushType.AI_TEXT_TYPE) {
            handleAiTextWork(content);
        } else if (type == MomentPushType.WATCH_REPORTED) {
            handleChangeReportInfoData(content);
        } else if (type == MomentPushType.ILLEGAL_DE_BLOCK) {
            deBlockSendPunish();
        } else if (type == MomentPushType.ILLEGAL_DISABLE_SEND) {
            disableSendPunish();
        } else if (type == MomentPushType.HIGH_RISK_HINT) {
            LogUtil.d(TAG, "HIGH_RISK_HINT: " + content);
            dealHighRiskHint();
        } else if (type == MomentPushType.HIGH_RISK_FORBIDDEN) {
            dealHighRiskForbidden();
        } else {
            switch (type) {
                case MomentPushType.WATCH_COMMENT_PUBLIC:
                    handleCommentMoment(content);
                    break;
                case MomentPushType.WATCH_COMMENT_DELETE:
                    handleCommentDelete(content);
                    break;
                case MomentPushType.WATCH_MOMENT_DELETE:
                    handleMomentDelete(content);
                    break;
                case MomentPushType.CHAT_MSG_INVALID:
                    dealInvalidMsg(content);
                    break;
                case MomentPushType.WATCH_MOMENT_PUBLIC:
                    handleMomentPublished(content);
                    break;
                case MomentPushType.WATCH_MOMENT_LIKE:
                    handleLikeMoment(content);
                    break;
                default:
                    break;
            }
        }
        EventBus.getDefault().post(new IMMomentMsgData(type, content));
    }

    private void handleAiTextWork(String content) {
        Application context = ContextUtils.getContext();
        if (context == null) {
            return;
        }
        AITextManager.getInstance(context).startCreateService(content);
    }

    private void receivedImReminder(final String content) {
        LogUtil.d(ReminderHelper.M_TAG, "温馨提醒推送：" + content);
        HandlerUtil.runOnBackgroundDelay(new Runnable() {
            @Override
            public void run() {
                ReminderHelper.get(IMMomentService.this).receivedImReminder(content);
            }
        }, 1500L);
    }

    private void dealHighRiskHint() {
        LogUtil.d(TAG, "高危用户提示通知：");
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventType(4));
            }
        });
    }

    private void dealHighRiskForbidden() {
        LogUtil.d(TAG, "高危用户封禁通知：");
        final Application context = ContextUtils.getContext();
        if (context == null) {
            return;
        }
        final IllegalMessageHandler handler = IllegalMessageHandler.getInstance(context.getApplicationContext());
        if (!handler.isInitHandler()) {
            handler.initIllegalConfig(context.getApplicationContext(), IllegalConfigHandler.obtainConfig(context.getApplicationContext()), new IllegalMessageHandler.InitIllegalListener() {
                @Override
                public void onIIllegalFinish() {
                    handler.highRiskDisableSend(context.getApplicationContext());
                }
            });
        } else {
            handler.highRiskDisableSend(context.getApplicationContext());
        }
    }

    private void handleMomentsBatchDelete(final String content) {
        LogUtil.i(TAG, "handleMomentsBatchDelete:" + content);
        if (TextUtils.isEmpty(content)) {
            LogUtil.d(TAG, "handleMomentsBatchDelete,content is empty.");
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                batchDeleteMoments(JSONUtil.fromJSON(content, MomentsBatchDeleteBean.class));
            }
        });
    }

    private void batchDeleteMoments(MomentsBatchDeleteBean bean) {
        if (bean == null) {
            LogUtil.i(TAG, "batchDeleteMoments, batchDeleteBean is null");
            return;
        }
        List<String> watchIds = bean.getWatchIds();
        if (CollectionUtil.isEmpty(watchIds)) {
            LogUtil.i(TAG, "batchDeleteMoments, watchId is null");
            return;
        }
        long timePoint = TimeUtils.getTimeByFormat(bean.getTimePoint());
        Iterator<String> iterator = watchIds.iterator();
        while (iterator.hasNext()) {
            List<DbMoment> moments = MomentServeImpl.getInstance(this).queryByLimitTime(iterator.next(), timePoint);
            if (CollectionUtil.isEmpty(moments)) {
                LogUtil.i(TAG, "batchDeleteMoments query dbMoments is empty");
            } else {
                boolean result = MomentServeImpl.getInstance(this).deleteDbMomentForBatch(moments);
                LogUtil.i(TAG, "batchDeleteMoments result = " + result);
                if (result) {
                    UnreadHelper.getInstance(this).dealUnreadPoint();
                    EventBus.getDefault().post(new EventData(17, moments));
                    notifyUnReadPoint();
                }
            }
        }
    }
    private void disableSendPunish() {
        LogUtil.d(TAG, "违规禁止发送推送：");
        final Application context = ContextUtils.getContext();
        if (context == null) {
            return;
        }
        final IllegalMessageHandler handler = IllegalMessageHandler.getInstance(context.getApplicationContext());
        if (!handler.isInitHandler()) {
            handler.initIllegalConfig(context.getApplicationContext(), IllegalConfigHandler.obtainConfig(context.getApplicationContext()), new IllegalMessageHandler.InitIllegalListener() {
                @Override
                public void onIIllegalFinish() {
                    handler.disableSendPunish(context.getApplicationContext());
                }
            });
        } else {
            handler.disableSendPunish(context.getApplicationContext());
        }
    }

    private void deBlockSendPunish() {
        LogUtil.d(TAG, "解除禁言推送：");
        Application context = ContextUtils.getContext();
        if (context == null) {
            return;
        }
        final IllegalMessageHandler handler = IllegalMessageHandler.getInstance(context.getApplicationContext());
        if (!handler.isInitHandler()) {
            handler.initIllegalConfig(context.getApplicationContext(), IllegalConfigHandler.obtainConfig(context.getApplicationContext()), new IllegalMessageHandler.InitIllegalListener() {
                @Override
                public void onIIllegalFinish() {
                    handler.deBlockIllegalPunish();
                }
            });
        } else {
            handler.deBlockIllegalPunish();
        }
    }

    private void handleChangeReportInfoData(String content) {
        iMomentServe.changeReportInfoData(content);
    }

    private void handleCommentDelete(String content) {
        LogUtil.i(TAG, "we begin to delete comment!content:" + content);
        if (TextUtils.isEmpty(content)) {
            LogUtil.d(TAG, "handleCommentDelete#content is empty.");
            return;
        }
        CommentDeleteBean bean = JSONUtil.fromJSON(content, CommentDeleteBean.class);
        if (bean == null || TextUtils.isEmpty(bean.getMomentId()) || TextUtils.isEmpty(bean.getCommentId())) {
            LogUtil.i(TAG, "handleCommentDelete idle. commentDeleteData is empty");
        } else {
            dealCommentDelete(bean);
        }
    }

    private void dealCommentDelete(final CommentDeleteBean bean) {
        LogUtil.i(TAG, "dealCommentDelete:" + bean);
        Observable.just(bean).map(new Func1<CommentDeleteBean, DbMomentComment>() {
            @Override
            public DbMomentComment call(CommentDeleteBean value) {
                return updateMomentCommentDeleteStateInDB(BeanConverterUtil.convertToDbMomentComment(value));
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Observer<DbMomentComment>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "dealCommentDelete#onError: ", throwable);
            }

            @Override
            public void onNext(DbMomentComment comment) {
                if (comment == null) {
                    return;
                }
                deleteMomentComment(comment);
                notifyCommentDelete(comment);
            }
        });
    }

    private void notifyCommentDelete(DbMomentComment comment) {
        Uri.Builder builder = Uri.parse("content://com.xtc.moment.commentProvider/comment").buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.DELETE_COMMENT_OR_CANCEL_LIKE);
        builder.appendQueryParameter("data", JSONUtil.toJSON(comment));
        getContentResolver().notifyChange(builder.build(), null);
        notifyUnReadCommentNum();
    }

    private void notifyUnReadCommentNum() {
        LogUtil.d(TAG, "notifyUnReadCommentNum--");
        getContentResolver().notifyChange(Uri.parse("content://com.xtc.moment.commentProvider/commentunread"), null);
    }

    private DbMomentComment updateMomentCommentDeleteStateInDB(DbMomentComment comment) {
        List<DbMomentComment> comments = iMomentServe.queryCommentByMomentIdAndCommentId(comment.getMomentId(), comment.getCommentId());
        if (comments == null || comments.isEmpty()) {
            LogUtil.d(TAG, "updateMomentCommentDeleteStateInDB#dbMomentComments == null || dbMomentComments.isEmpty()");
            return null;
        }
        DbMomentComment dbComment = comments.get(0);
        dbComment.setDeleted(true);
        dbComment.setComment(getString(R.string.comment_delete_hint));
        iMomentServe.updateMomentComment(dbComment);
        return dbComment;
    }

    private void deleteMomentComment(DbMomentComment comment) {
        LogUtil.d(TAG, "deleteMomentComment#dbMomentComment:" + comment);
        EventBus.getDefault().post(new EventData(2, comment));
    }

    private void changeMyName(String content) {
        LogUtil.d(TAG, "changeMyName");
        ImMyNameBean nameBean = JSONUtil.fromJSON(content, ImMyNameBean.class);
        if (nameBean == null || nameBean.getData() == null || nameBean.getData().getData() == null) {
            return;
        }
        ImMyNameInfoBean nameInfoBean = JSONUtil.fromJSON(nameBean.getData().getData(), ImMyNameInfoBean.class);
        LogUtil.d(TAG, "imMyNameInfoBean:" + nameInfoBean);
        if (nameInfoBean == null) {
            return;
        }
        String name = nameInfoBean.getName();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        if (accountInfoServer == null) {
            accountInfoServer = AccountInfoServerImpl.getInstance(this);
        }
        updateLikeMessageName(accountInfoServer.getWatchAccountInfo().getWatchId(this), name);
    }

    private void updateLikeMessageName(final String watchId, final String name) {
        if (TextUtils.isEmpty(watchId) || TextUtils.isEmpty(name)) {
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                iMomentServe.updateLikeMessageName(watchId, name);
            }
        });
    }

    private void handleMomentDelete(String content) {
        LogUtil.i(TAG, "we begin to delete!content:" + content);
        if (TextUtils.isEmpty(content)) {
            LogUtil.d(TAG, "handleMomentDelete#content is empty.");
            return;
        }
        MomentDeleteBean bean = JSONUtil.fromJSON(content, MomentDeleteBean.class);
        if (bean == null || TextUtils.isEmpty(bean.getParentId())) {
            LogUtil.i(TAG, "handleMomentDelete idle. momentData is empty");
        } else {
            dealMomentDelete(bean);
        }
    }

    private void dealMomentDelete(MomentDeleteBean bean) {
        LogUtil.i(TAG, "dealMomentDelete:" + bean);
        DbMoment deleteMoment = BeanConverterUtil.convertToDbMoment(bean);
        if (deleteMoment == null || TextUtils.isEmpty(deleteMoment.getMomentId())) {
            LogUtil.i(TAG, "dealMomentDelete convertToDbMoment fail:" + bean);
            return;
        }
        List<DbMoment> moments = MomentServeImpl.getInstance(this).getMomentById(deleteMoment.getMomentId());
        if (CollectionUtil.isEmpty(moments)) {
            LogUtil.i(TAG, "dealMomentDelete getMomentById fail:" + deleteMoment);
            return;
        }
        DbMoment moment = moments.get(0);
        boolean result = MomentDeleteServe.getInstance(this).delete(moment);
        LogUtil.i(TAG, "dealMomentDelete: result: " + result);
        if (result) {
            UnreadHelper.getInstance(this).dealUnreadPoint();
            EventBus.getDefault().post(new EventData(1, deleteMoment));
            notifyFriendMomentDeleted(moment);
            notifyUnReadPoint();
        }
    }

    private void notifyUnReadPoint() {
        LogUtil.d(TAG, "notifyUnReadPoint");
        getContentResolver().notifyChange(Uri.parse("content://com.xtc.moment.momentProvider/momentunread"), null);
    }

    private void notifyFriendMomentDeleted(DbMoment moment) {
        Uri uri = getMomentChangeNotifyUri(moment);
        LogUtil.i(TAG, "notifyFriendMomentDeleted: " + uri.toString());
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.FRIEND_DELETE_MOMENT);
        builder.appendQueryParameter("data", JSONUtil.toJSON(moment));
        getContentResolver().notifyChange(builder.build(), null);
    }

    private Uri getMomentChangeNotifyUri(DbMoment moment) {
        LogUtil.i(TAG, "getMomentChangeNotifyUri: " + moment);
        int type = moment.getType().intValue();
        if (type == 0) {
            return Uri.parse("content://com.xtc.moment.momentProvider/mood");
        }
        if (type == 1) {
            return Uri.parse("content://com.xtc.moment.momentProvider/state");
        }
        if (type == 2) {
            return Uri.parse("content://com.xtc.moment.momentProvider/location");
        }
        if (type == 3) {
            return Uri.parse("content://com.xtc.moment.momentProvider/word");
        }
        if (type == 5) {
            return Uri.parse("content://com.xtc.moment.momentProvider/photo");
        }
        if (type == 6) {
            return Uri.parse("content://com.xtc.moment.momentProvider/video");
        }
        return Uri.parse("content://com.xtc.moment.momentProvider/item");
    }

    private void handleMomentPublished(String content) {
        MomentMessageData messageData = JSONUtil.fromJSON(content, MomentMessageData.class);
        LogUtil.i(TAG, "handleMomentPublished:" + messageData);
        MomentMessageData transitionData = MessageTransitionServe.dealMsgTypeTransition(messageData);
        DbMoment moment = BeanConverterUtil.convertToDbMoment(transitionData);
        if (moment == null) {
            LogUtil.d(TAG, "handleMomentPublished: moment == null");
            return;
        }
        if (MomentServeImpl.getInstance(this).getMomentByMomentIdWithoutComment(moment.getMomentId()) != null) {
            LogUtil.i("MomentServeImpl", "moment exist,return :" + moment);
            return;
        }
        String watchName;
        String watchId = accountInfoServer.getWatchAccountInfo().getWatchId(this);
        if (watchId != null && watchId.equals(moment.getWatchId())) {
            watchName = accountInfoServer.getWatchAccountInfo().getName(this);
            if (TextUtils.isEmpty(watchName)) {
                watchName = getString(R.string.unknown_watch);
            }
        } else if (TextUtils.isEmpty(transitionData.getWatchName())) {
            ContactBean contact = contactManager.getContactByWatchIdSync(moment.getWatchId());
            if (contact != null) {
                watchName = contact.getName();
            } else {
                watchName = getString(R.string.unknown_watch);
            }
        } else {
            watchName = transitionData.getWatchName();
        }
        if (messageData.getEmotionId() != 0 && TextUtils.isEmpty(moment.getMomentBgPath())) {
            DbMomentPrerogativeBackground background = MomentPrerogativeServeImpl.getInstance(this).getPrerogativeBackgroundByEmotionId(messageData.getEmotionId());
            if (background != null && !TextUtils.isEmpty(background.getLocalEmotionPath())) {
                moment.setMomentBgPath(background.getLocalEmotionPath());
                LogUtil.i(TAG, "updateLocalMoment : " + background.getLocalEmotionPath());
            }
        }
        moment.setName(watchName);
        addMoment(moment);
        UnreadHelper.getInstance(this).showUnreadPoint();
        notifyMomentPublished(moment);
        notifyUnReadPoint();
    }

    private void notifyMomentPublished(DbMoment moment) {
        Uri uri = getMomentChangeNotifyUri(moment);
        LogUtil.i(TAG, "notifyMomentPublished: " + uri.toString());
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.FRIEND_PUBLISH_MOMENT);
        builder.appendQueryParameter("data", JSONUtil.toJSON(moment));
        getContentResolver().notifyChange(builder.build(), null);
    }

    public void addMoment(final DbMoment moment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                iMomentServe.insertMomentByMomentId(moment);
            }
        });
    }
    public void dealInvalidMsg(String content) {
        LogUtil.i(TAG, "we begin to invalidMoment!content:" + content);
        if (TextUtils.isEmpty(content)) {
            LogUtil.i(TAG, "invalidMoment but im content is empty!");
            return;
        }
        NetInvalidMoment invalidMoment = JSONUtil.fromJSON(content, NetInvalidMoment.class);
        if (invalidMoment == null || TextUtils.isEmpty(invalidMoment.getAction())) {
            LogUtil.i(TAG, "invalidMoment but invalidMoment idle. invalidMoment:" + invalidMoment);
            return;
        }
        LogUtil.d(TAG, "invalidMoment:" + invalidMoment.toString());
        Observable.just(invalidMoment).map(new Func1<NetInvalidMoment, DbMoment>() {
            @Override
            public DbMoment call(NetInvalidMoment value) {
                if (value == null || TextUtils.isEmpty(value.getAction())) {
                    LogUtil.i(TAG, "netInvalidChatMsg is useless,invalidMoment:" + value);
                    return null;
                }
                SystemClock.sleep(com.xtc.virtualselfapi.constants.Constants.DEFAULT_INIT_DELAY_TIME);
                DbMoment invalidateMoment = deleteSexyMsgInDB(value);
                if (invalidateMoment != null) {
                    return invalidateMoment;
                }
                ArrayList<DbSexyPhotoDistinguish> records = new ArrayList<>();
                if (NetInvalidMoment.MOMENTIDS.equals(value.getAction())) {
                    for (String momentId : value.getMomentIds()) {
                        DbSexyPhotoDistinguish record = new DbSexyPhotoDistinguish();
                        record.setMsgId(momentId);
                        record.setKey(momentId);
                        records.add(record);
                    }
                } else if (NetInvalidMoment.PARENTIDS.equals(value.getAction())) {
                    for (String parentId : value.getParentIds()) {
                        DbSexyPhotoDistinguish record = new DbSexyPhotoDistinguish();
                        record.setMsgId(parentId);
                        record.setKey(parentId);
                        records.add(record);
                    }
                }
                LogUtil.i(TAG, "dealInvalidMsg but nothing to delete. insert momentIds:" + value.getMomentIds()
                        + "insert parentIds:" + value.getParentIds() + " result:" + addSexyPhotoRecord(records));
                return null;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<DbMoment>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "dealInvalidMsg#onError: ", throwable);
            }

            @Override
            public void onNext(DbMoment moment) {
                if (moment == null) {
                    return;
                }
                dealDeleteInvalidateMoment(moment);
            }
        });
    }

    private void dealDeleteInvalidateMoment(DbMoment moment) {
        LogUtil.d(TAG, "dealDeleteInvalidateMoment#dbMoment:" + moment);
        EventBus.getDefault().post(new EventData(9, moment));
    }

    public boolean addSexyPhotoRecord(List<DbSexyPhotoDistinguish> records) {
        return iMomentServe.addSexyPhotoRecord(records);
    }

    public DbMoment deleteSexyMsgInDB(NetInvalidMoment invalidMoment) {
        long allMoment = iMomentServe.getAllMoment();
        LogUtil.d("MomentPresenter", "allMsgCount:" + allMoment);
        long pageCount = (allMoment / 50) + 1;
        for (int i = 0; i < pageCount; i++) {
            List<DbMoment> moments = iMomentServe.queryMessageForPages(i * pageCount, 50L, false);
            if (moments == null || CollectionUtil.isEmpty(moments)) {
                LogUtil.i("MomentPresenter", "need to deleteSexyMsgInDB but messageList is empty.");
                break;
            }
            for (DbMoment moment : moments) {
                if (moment != null && isSexyPhotoType(moment.getType().intValue())) {
                    if (NetInvalidMoment.MOMENTIDS.equals(invalidMoment.getAction())) {
                        Iterator<String> iterator = invalidMoment.getMomentIds().iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().equals(moment.getMomentId())) {
                                iMomentServe.deleteDBMomentByMomentId(moment);
                                LogUtil.d("MomentPresenter", "delete invalid Moment success!moment:" + moment);
                                return moment;
                            }
                        }
                    } else if (NetInvalidMoment.PARENTIDS.equals(invalidMoment.getAction())) {
                        Iterator<String> iterator = invalidMoment.getParentIds().iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().equals(moment.getMomentId())) {
                                iMomentServe.deleteDBMomentByMomentId(moment);
                                LogUtil.d("MomentPresenter", "delete invalid Moment success!moment:" + moment);
                                return moment;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isSexyPhotoType(int type) {
        return type == 5 || type == 4 || type == 6 || type == 8 || type == 22 || type == 23
                || type == 24 || type == 26 || type == 27;
    }

    private void handleLikeMoment(final String content) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DbLikeMessage likeMessage = JSONUtil.fromJSON(content, DbLikeMessage.class);
                if (likeMessage == null) {
                    LogUtil.d(TAG, "run: dbLikeMessage == null");
                    return;
                }
                if (!CollectionUtil.isEmpty(MomentServeImpl.getInstance(IMMomentService.this)
                        .getLikeMessageByMomentIdAndWatchId(likeMessage.getMomentId(), likeMessage.getWatchId()))) {
                    LogUtil.d(TAG, "数据库已经有点赞数据，不再插入数据库");
                    return;
                }
                LogUtil.i(TAG, "handleLikeMoment:" + likeMessage);
                addLikeMessage(likeMessage);
                EventBus.getDefault().post(new EventData(4, likeMessage));
                notifyLikeMessageChange(likeMessage, true);
            }
        });
    }

    private void handleCancelLikeMoment(final String content) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DbLikeMessage likeMessage = JSONUtil.fromJSON(content, DbLikeMessage.class);
                if (likeMessage == null) {
                    LogUtil.d(TAG, "run: dbLikeMessage == null");
                    return;
                }
                String watchId = likeMessage.getWatchId();
                List<DbMoment> moments = MomentServeImpl.getInstance(IMMomentService.this).getMomentById(likeMessage.getMomentId());
                if (CollectionUtil.isEmpty(moments) || moments.size() <= 0) {
                    return;
                }
                DbMoment moment = moments.get(0);
                List<DbLikeMessage> dbLikeMessages = MomentServeImpl.getInstance(IMMomentService.this)
                        .getLikeMessageByMomentIdAndWatchId(likeMessage.getMomentId(), watchId);
                boolean isDelete = MomentServeImpl.getInstance(IMMomentService.this)
                        .deleteDBMomentByMomentIdAndWatchId(likeMessage.getMomentId(), watchId);
                moment.setDataUrl(watchId);
                moment.setLikeTotal(Integer.valueOf(moment.getLikeTotal().intValue() - 1));
                LogUtil.i(TAG, "handleCancelLikeMoment:" + likeMessage + " isDelete:" + isDelete);
                dealLikeMessageReddot(likeMessage);
                EventBus.getDefault().post(new EventData(12, likeMessage));
                EventBus.getDefault().post(new EventData(13, moment));
                if (CollectionUtil.isEmpty(dbLikeMessages)) {
                    return;
                }
                notifyLikeMessageChange(dbLikeMessages.get(0), false);
            }
        });
    }

    private void notifyLikeMessageChange(DbLikeMessage likeMessage, boolean isNew) {
        Uri.Builder builder = Uri.parse("content://com.xtc.moment.likeMessageProvider/likeMessage").buildUpon();
        builder.appendQueryParameter("type", isNew ? Constants.QueryParameter.NEW_COMMENT_OR_LIKE_MESSAGE : Constants.QueryParameter.DELETE_COMMENT_OR_CANCEL_LIKE);
        builder.appendQueryParameter("data", JSONUtil.toJSON(likeMessage));
        getContentResolver().notifyChange(builder.build(), null);
        NotifyUtils.notifyLikeUnReadNum(this);
    }

    public void addLikeMessage(DbLikeMessage likeMessage) {
        if (likeMessage == null) {
            LogUtil.w("MomentPresenter", "addLikeMessage:dbLikeMessage is null");
        } else if (iMomentServe.increaseLikeTotal(likeMessage.getMomentId())) {
            iMomentServe.addLikeMessage(likeMessage);
            dealLikeMessageReddot(likeMessage);
        } else {
            LogUtil.w("MomentPresenter", "addLikeMessage failed because of increaseLikeTotal return false");
        }
    }

    private void dealCommentReddot(DbMomentComment comment) {
        String watchId = MomentApp.getWatchId();
        if (watchId == null) {
            LogUtil.i(TAG, "dealCommentReddot: my watch id is null!");
        } else if (watchId.equals(comment.getReplyId()) || watchId.equals(comment.getMomentWatchId())) {
            UnreadHelper.getInstance(this).showUnreadNumber();
        }
    }

    private void dealLikeMessageReddot(DbLikeMessage likeMessage) {
        if (likeMessage.getMomentWatchId().equals(MomentApp.getWatchId())) {
            UnreadHelper.getInstance(this).showUnreadNumber();
        }
    }

    private void handleCommentMoment(final String content) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                DbMomentComment comment;
                if (content.contains("advertId")) {
                    comment = BeanConverterUtil.convertToDbMomentCommentBean(JSONUtil.fromJSON(content, CommentBean.class));
                    LogUtil.i(TAG, "handleCommentMoment:convertToDbMomentCommentBean" + comment);
                } else {
                    comment = JSONUtil.fromJSON(content, DbMomentComment.class);
                }
                LogUtil.i(TAG, "handleCommentMoment:" + comment);
                if (comment == null) {
                    return;
                }
                if (!CollectionUtil.isEmpty(MomentServeImpl.getInstance(IMMomentService.this)
                        .queryCommentByCommentId(comment.getCommentId()))) {
                    LogUtil.d(TAG, "run: dbMomentComments exist, return ");
                    return;
                }
                comment.setType(TextUtils.isEmpty(comment.getReplyId()) ? 1 : 2);
                addMomentComment(comment);
                dealCommentReddot(comment);
                EventBus.getDefault().post(new EventData(3, comment));
                if (CollectionUtil.isEmpty(MomentServeImpl.getInstance(IMMomentService.this).getMomentById(comment.getMomentId()))) {
                    return;
                }
                notifyCommentAdded(comment);
            }
        });
    }

    private void notifyCommentAdded(DbMomentComment comment) {
        String watchId = AccountInfoServerImpl.getInstance(this).getWatchAccountInfo().getWatchId(this);
        if (!TextUtils.isEmpty(comment.getReplyId()) && comment.getReplyId().equals(watchId)) {
            comment.setReplyName(getResources().getString(R.string.me));
        }
        Uri.Builder builder = Uri.parse("content://com.xtc.moment.commentProvider/comment").buildUpon();
        builder.appendQueryParameter("type", Constants.QueryParameter.NEW_COMMENT_OR_LIKE_MESSAGE);
        builder.appendQueryParameter("data", JSONUtil.toJSON(comment));
        getContentResolver().notifyChange(builder.build(), null);
        notifyUnReadCommentNum();
    }

    public void addMomentComment(DbMomentComment comment) {
        if (comment == null) {
            LogUtil.w(TAG, "addMomentComment:dbMomentComment is null");
        } else {
            iMomentServe.addCommentMoment(comment);
        }
    }

    private void handlePrerogativeChange(String content) {
        String prerogatives;
        try {
            prerogatives = new JSONObject(content).getString("prerogatives");
        } catch (JSONException e) {
            LogUtil.e(TAG, "handlePrerogativeChange: " + e);
            prerogatives = null;
        }
        List<EmotionsEntity> emotions = (List<EmotionsEntity>) JSONUtil.fromJSON(prerogatives, ArrayList.class, EmotionsEntity.class);
        if (CollectionUtil.isEmpty(emotions)) {
            LogUtil.d(TAG, "handlePrerogativeChange data invalid");
            return;
        }
        LogUtil.d(TAG, "handlePrerogativeChange");
        for (EmotionsEntity emotion : emotions) {
            if (emotion != null && (emotion.isMomentBgEquity() || emotion.isMomentLikeEquity())) {
                MomentPrerogativeServeImpl.isNeedRefreshPersonalPrerogative = true;
                LogUtil.d(TAG, "handlePrerogativeChange isNeedRefreshPersonalPrerogative true");
                return;
            }
        }
    }
}
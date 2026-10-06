package com.xtc.moment.module.share;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.MomentApp;
import com.xtc.moment.base.BaseInteractPresenter;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.net.bean.MomentLikeVo;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.MessageTransitionServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 分享到好友圈页面的 Presenter，负责拉取动态、点赞记录并做本地过滤。
 */
public class SharePresenter extends BaseInteractPresenter<IShareView> {

    private static final String TAG = LogTag.tag("SharePresenter");
    private static final int MAX_LOCAL_QUERY_ALERT_SIZE = 20;

    private volatile boolean loading;
    private LikeDrawableCache mLikeDrawableCache;
    private LinkedHashMap<String, DbMoment> momentsMap;
    private final String watchId;

    public SharePresenter(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.watchId = MomentApp.getWatchId();
        this.mLikeDrawableCache = new LikeDrawableCache(context);
        this.momentsMap = new LinkedHashMap<>();
    }

    /** 只保留当前账号或好友发布的动态。 */
    private List<DbMoment> filter(List<DbMoment> moments) {
        List<Friend> allFriends;
        if (moments == null || moments.isEmpty() || (allFriends = getAllFriend()) == null || allFriends.isEmpty()) {
            return null;
        }
        ArrayList<DbMoment> filtered = new ArrayList<>();
        String selfWatchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        for (DbMoment moment : moments) {
            if (selfWatchId != null && selfWatchId.equals(moment.getWatchId())) {
                moment.setChecked(true);
                filtered.add(MessageTransitionServe.dealMsgTypeTransition(moment));
                continue;
            }
            for (Friend friend : allFriends) {
                if (friend.getWatchId() != null && friend.getWatchId().equals(moment.getWatchId())) {
                    moment.setChecked(true);
                    filtered.add(MessageTransitionServe.dealMsgTypeTransition(moment));
                    break;
                }
            }
        }
        LogUtil.i(TAG, "filter data,before:" + moments.size() + ",after:" + filtered.size());
        return filtered;
    }

    public void loadMomentsFromNet(final long offset, final long count, final String targetWatchId,
            DbMoment lastMoment) {
        synchronized (this) {
            if (this.loading) {
                return;
            }
            this.loading = true;
            final long startTime = lastMoment == null ? System.currentTimeMillis() : lastMoment.getCreateTime().longValue();
            LogUtil.i(TAG, "loadMomentsFromNet,offset:" + offset + ",count:" + count);
            this.iMomentServe.getMomentsFromNet(count, 0, targetWatchId, startTime)
                    .map(new Func1<List<DbMoment>, List<DbMoment>>() {
                        @Override
                        public List<DbMoment> call(List<DbMoment> moments) {
                            List<DbMoment> filtered = filter(moments);
                            if (filtered != null && filtered.size() > 0) {
                                for (int i = 0; i < filtered.size(); i++) {
                                    DbMoment moment = filtered.get(i);
                                    if (moment.getEmotionId() != 0) {
                                        DbMomentPrerogativeBackground prerogativeBackground =
                                                MomentPrerogativeServeImpl.getInstance(mContext)
                                                        .getPrerogativeBackgroundByEmotionId(moment.getEmotionId());
                                        if (prerogativeBackground != null
                                                && !TextUtils.isEmpty(prerogativeBackground.getLocalEmotionPath())) {
                                            moment.setMomentBgPath(prerogativeBackground.getLocalEmotionPath());
                                        }
                                    }
                                }
                                Comparator<DbMoment> comparator = new Comparator<DbMoment>() {
                                    @Override
                                    public int compare(DbMoment first, DbMoment second) {
                                        return Long.compare(second.getCreateTime().longValue(),
                                                first.getCreateTime().longValue());
                                    }
                                };
                                Collections.sort(filtered, comparator);
                                Iterator<DbMoment> iterator = filtered.iterator();
                                while (iterator.hasNext()) {
                                    DbMoment moment = iterator.next();
                                    if (momentsMap.containsKey(moment.getMomentId())) {
                                        iterator.remove();
                                    } else if (!MomentTypeUtil.isOfficialType(moment)) {
                                        momentsMap.put(moment.getMomentId(), moment);
                                    }
                                }
                                ArrayList<DbMoment> allMoments = new ArrayList<>(momentsMap.values());
                                Collections.sort(allMoments, comparator);
                                long timeStart;
                                long timeEnd;
                                if (CollectionUtil.isEmpty(filtered) || CollectionUtil.isEmpty(allMoments)) {
                                    timeStart = 0;
                                    timeEnd = 0;
                                } else {
                                    timeStart = getStartTime(filtered);
                                    long firstCreateTime = filtered.get(0).getCreateTime().longValue();
                                    if (offset == 0) {
                                        timeEnd = startTime;
                                        LogUtil.d(TAG, "offset == 0, timeEnd = " + timeEnd);
                                    } else {
                                        if (firstCreateTime < allMoments.get(allMoments.size() - 1).getCreateTime().longValue()) {
                                            timeEnd = allMoments.get(allMoments.size() - 1).getCreateTime().longValue();
                                            LogUtil.d(TAG, "offset != 0, timeEnd = " + timeEnd);
                                        } else {
                                            timeEnd = firstCreateTime;
                                        }
                                    }
                                }
                                List<DbMoment> localMoments = iMomentServe.queryDbMoments(targetWatchId, timeStart, timeEnd);
                                LogUtil.d(TAG, "localList size: " + localMoments.size());
                                if (localMoments.size() > MAX_LOCAL_QUERY_ALERT_SIZE) {
                                    BehaviorEvent.recordLocalSize(mContext, localMoments.size());
                                }
                                ArrayList<DbMoment> toDelete = new ArrayList<>();
                                if (!CollectionUtil.isEmpty(localMoments)) {
                                    for (DbMoment localMoment : localMoments) {
                                        if (!momentsMap.containsKey(localMoment.getMomentId())) {
                                            LogUtil.d(TAG, "需要被删除的动态：" + localMoment);
                                            toDelete.add(localMoment);
                                        }
                                    }
                                }
                                iMomentServe.deleteDbMomentForBatch(toDelete);
                                iMomentServe.addMoments(filtered);
                                iMomentServe.addCommentMoment(BeanConverterUtil.mapCommentList(filtered));
                            }
                            return iMomentServe.getMomentsFromDbSync(offset, count, 0, targetWatchId);
                        }
                    })
                    .map(new Func1<List<DbMoment>, List<DbMoment>>() {
                        @Override
                        public List<DbMoment> call(List<DbMoment> moments) {
                            boolean lbsSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext,
                                    ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
                            for (DbMoment moment : moments) {
                                moment.setLbsSwitch(lbsSwitch);
                            }
                            return moments;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<DbMoment>>() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            if (isViewAttached() && getView() != null) {
                                ((IShareView) getView()).startLoad();
                            }
                        }

                        @Override
                        public void onCompleted() {
                            loading = false;
                            LogUtil.d(TAG, "loadMomentsFromNet onCompleted");
                            if (isViewAttached() && getView() != null) {
                                ((IShareView) getView()).loadComplete();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LogUtil.e(TAG, "loadMomentsFromNet failed:" + throwable);
                            if (isViewAttached() && getView() != null) {
                                ((IShareView) getView()).loadLocalData(offset, count, targetWatchId);
                            }
                            loading = false;
                        }

                        @Override
                        public void onNext(List<DbMoment> moments) {
                            if (isViewAttached() && getView() != null) {
                                ((IShareView) getView()).loadSuccess(moments);
                            }
                        }
                    });
        }
    }

    private long getStartTime(List<DbMoment> moments) {
        for (int index = moments.size() - 1; index >= 0; index--) {
            DbMoment moment = moments.get(index);
            if (!MomentTypeUtil.isOfficialType(moment)) {
                return moment.getCreateTime().longValue();
            }
        }
        return System.currentTimeMillis();
    }

    public void loadMomentsFromDb(final long offset, final long count, final String targetWatchId,
            final boolean loadUntilMoment) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final ArrayList<DbMoment> moments = new ArrayList<>();
                if (!loadUntilMoment) {
                    List<DbMoment> dbMoments = iMomentServe.getMomentsFromDbSync(offset, count, 0, targetWatchId);
                    if (dbMoments != null && dbMoments.size() > 0) {
                        moments.addAll(dbMoments);
                    }
                } else {
                    long queryOffset = offset;
                    boolean found = false;
                    do {
                        List<DbMoment> dbMoments = iMomentServe.getMomentsFromDbSync(queryOffset, count, 0, targetWatchId);
                        if (dbMoments != null && dbMoments.size() > 0) {
                            moments.addAll(dbMoments);
                            for (DbMoment moment : dbMoments) {
                                if (targetWatchId != null && targetWatchId.equals(moment.getMomentId())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        queryOffset += count;
                        if (found) {
                            break;
                        }
                    } while (queryOffset <= iMomentServe.getMomentCountInDb());
                }
                boolean lbsSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(mContext,
                        ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
                for (DbMoment moment : moments) {
                    moment.setLbsSwitch(lbsSwitch);
                }
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached() && getView() != null) {
                            ((IShareView) getView()).loadSuccess(moments);
                        }
                    }
                });
            }
        });
    }

    public void getPraiseRecord(List<String> momentIds, String targetWatchId) {
        this.iMomentServe.getPraiseRecord(momentIds, targetWatchId)
                .map(new Func1<PraiseResponse, Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public Map<String, List<DbLikeMessage>> call(PraiseResponse praiseResponse) {
                        Map<String, List<MomentLikeVo>> momentLikeMap = praiseResponse.getMomentLikeMap();
                        ArrayList<String> momentIdList = new ArrayList<>();
                        if (momentLikeMap != null) {
                            for (Map.Entry<String, List<MomentLikeVo>> entry : momentLikeMap.entrySet()) {
                                iMomentServe.addLikeMessage(BeanConverterUtil.convertToDbLikeMessage(entry.getValue()));
                                momentIdList.add(entry.getKey());
                            }
                        }
                        return getPraiseRecordFromDb(momentIdList);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (isViewAttached() && getView() != null) {
                            ((IShareView) getView()).loadPraiseRecordFailed();
                        }
                    }

                    @Override
                    public void onNext(Map<String, List<DbLikeMessage>> likeMap) {
                        if (isViewAttached() && getView() != null) {
                            ((IShareView) getView()).loadPraiseRecordSuccess(likeMap);
                        }
                    }
                });
    }

    public List<DbMoment> getMomentById(String momentId) {
        return this.iMomentServe.getMomentById(momentId);
    }

    public List<DbLikeMessage> getLikeMessageByMomentId(String momentId) {
        List<DbLikeMessage> likeMessages = this.iMomentServe.getLikeMessageByMomentId(momentId);
        if (com.xtc.utils.common.CollectionUtil.isEmpty(likeMessages)) {
            return new ArrayList<>();
        }
        dealLikeDrawable(likeMessages);
        return likeMessages;
    }

    private void dealLikeDrawable(List<DbLikeMessage> likeMessages) {
        for (int index = 0; index < likeMessages.size(); index++) {
            DbLikeMessage likeMessage = likeMessages.get(index);
            if (likeMessage.getEmotionId() == 0) {
                continue;
            }
            DbMomentPrerogativeLike prerogativeLike = MomentPrerogativeServeImpl.getInstance(this.mContext)
                    .getPrerogativeLikeByEmotionId(likeMessage.getEmotionId());
            if (prerogativeLike == null || TextUtils.isEmpty(this.watchId)) {
                continue;
            }
            if (Objects.equals(this.watchId, likeMessage.getWatchId())) {
                if (IllegalMessageHandler.getInstance(this.mContext).isDisableSend()) {
                    String praisedDisablePic = prerogativeLike.getPraisedDisablePic();
                    this.mLikeDrawableCache.loadImageFromNetToCache(praisedDisablePic);
                    likeMessage.setLikedPic(praisedDisablePic);
                } else {
                    String praisedPic = prerogativeLike.getPraisedPic();
                    this.mLikeDrawableCache.loadImageFromNetToCache(praisedPic);
                    likeMessage.setLikedPic(praisedPic);
                }
            } else {
                String praisedPic = prerogativeLike.getPraisedPic();
                this.mLikeDrawableCache.loadImageFromNetToCache(praisedPic);
                likeMessage.setLikedPic(praisedPic);
            }
        }
    }

    public Map<String, List<DbLikeMessage>> getPraiseRecordFromDb(List<String> momentIds) {
        if (momentIds == null || momentIds.isEmpty()) {
            return null;
        }
        HashMap<String, List<DbLikeMessage>> likeMap = new HashMap<>();
        for (String momentId : momentIds) {
            if (TextUtils.isEmpty(momentId)) {
                continue;
            }
            List<DbLikeMessage> likeMessages = getLikeMessageByMomentId(momentId);
            if (likeMessages != null && likeMessages.size() > 0) {
                likeMap.put(momentId, likeMessages);
            }
        }
        return likeMap;
    }

    public List<Friend> getAllFriend() {
        return FriendInfoServeImpl.getInstance(this.mContext).getAllFriendInfo();
    }

    public LikeDrawableCache getLikeDrawableCache() {
        return this.mLikeDrawableCache;
    }

    @Override
    public String getLogTag() {
        return TAG;
    }
}
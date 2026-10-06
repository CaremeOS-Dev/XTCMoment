package com.xtc.moment.module.main;

import android.content.Context;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.base.BaseInteractPresenter;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.helper.UnreadHelper;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.NetInvalidMoment;
import com.xtc.moment.module.illegal.config.IllegalConfigHandler;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.net.bean.MomentLikeVo;
import com.xtc.moment.net.bean.OfficialCommentResultBean;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.net.bean.ReportMomentReq;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.IFriendInfoServe;
import com.xtc.moment.serve.MessageTransitionServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.bean.WatchAccountInfo;
import com.xtc.moment.share.other.BehaviorEvent;
import com.xtc.moment.third.bean.PushBehaviorBean;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.MomentTypeUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.utils.system.SystemProperty;
import com.xtc.utils.system.SystemPropertyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 好友圈主页 Presenter。
 *
 * <p>负责动态的拉取（网络 + 本地）、过滤与落库、点赞记录加载、评论/点赞/已读状态维护，
 * 以及举报、失效动态删除等业务逻辑。
 */
public class MomentPresenter extends BaseInteractPresenter<IMomentActivityView> {

    private static final String TAG = LogTag.tag("MomentPresenter");

    private IAccountInfoServe accountInfoServer;
    private NormalIconDialog deleteDialog;
    private List<DbMoment> deletedList;
    private IFriendInfoServe friendInfoServe;
    /** 手表是否已绑定（影响是否允许发布）。 */
    private boolean isBind;
    /** 是否正在从网络加载动态，避免并发重复请求。 */
    private volatile boolean loading;
    /** 本次会话已加载的动态，用于合并去重。 */
    private LinkedHashMap<String, DbMoment> momentsMap;
    private List<String> skipMomentIdData = new ArrayList<>();
    private List<String> previewMomentIdData = new ArrayList<>();

    public MomentPresenter(Context context) {
        this.mContext = context;
        this.isBind = SystemPropertyUtil.getBoolean(SystemProperty.BIND_STATUS, false);
        Context applicationContext = context.getApplicationContext();
        this.accountInfoServer = AccountInfoServerImpl.getInstance(applicationContext);
        this.iMomentServe = MomentServeImpl.getInstance(applicationContext);
        this.momentsMap = new LinkedHashMap<>();
        this.deletedList = new ArrayList<>();
    }

    /** 初始化好友信息服务（依赖联系人模块）。 */
    public void initContactManager() {
        if (this.mContext == null) {
            return;
        }
        this.friendInfoServe = FriendInfoServeImpl.getInstance(this.mContext.getApplicationContext());
    }

    /** 过滤出好友或自己的动态，并做消息类型转换。 */
    private List<DbMoment> filter(List<DbMoment> moments) {
        if (moments == null || moments.isEmpty()) {
            return null;
        }
        List<Friend> friends = getFriendInfo();
        if (friends == null) {
            friends = new ArrayList<>();
        }
        ArrayList<DbMoment> result = new ArrayList<>();
        String selfId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        for (DbMoment moment : moments) {
            if (selfId != null && selfId.equals(moment.getWatchId())) {
                moment.setChecked(true);
                result.add(typeTransition(moment));
            } else {
                for (Friend friend : friends) {
                    if (friend.getWatchId() != null && friend.getWatchId().equals(moment.getWatchId())) {
                        moment.setChecked(true);
                        result.add(typeTransition(moment));
                        break;
                    }
                }
            }
            if (!result.contains(moment)) {
                result.add(typeTransition(moment));
            }
        }
        LogUtil.i(TAG, "filter data,before:" + moments.size() + ",after:" + result.size());
        return result;
    }

    private DbMoment typeTransition(DbMoment moment) {
        return MessageTransitionServe.dealMsgTypeTransition(moment);
    }

    /** 从网络拉取动态：先做类型转换与广告补充，再写入本地库。 */
    public void loadMomentsFromNet(final long offset, final long count, DbMoment lastMoment, boolean isPullRefresh) {
        final String watchId = this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext);
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.w(TAG, "loadMomentsFromNet watchId is null");
            return;
        }
        synchronized (this) {
            if (this.loading) {
                return;
            }
            this.loading = true;
            LogUtil.i(TAG, "loadMomentsFromNet,offset:" + offset + ",count:" + count + ", dbMoment = " + lastMoment);
            long endTime;
            if (lastMoment != null) {
                endTime = lastMoment.getCreateTime();
                if (isPullRefresh) {
                    endTime--;
                }
            } else {
                endTime = System.currentTimeMillis();
            }
            try {
                final long finalEndTime = endTime;
                this.iMomentServe.getMomentsFromNet(count, 1, watchId, endTime)
                        .flatMap(new Func1<List<DbMoment>, Observable<List<DbMoment>>>() {
                            @Override
                            public Observable<List<DbMoment>> call(List<DbMoment> moments) {
                                if (moments == null) {
                                    return Observable.just(null);
                                }
                                if (offset != 0) {
                                    return getMomentsWithoutAdvertise(moments, offset, count, watchId, finalEndTime);
                                }
                                // 首页需要判断是否含官方动态：含则额外拉取官方评论。
                                ArrayList<String> advertIds = new ArrayList<>();
                                for (int i = 0; i < moments.size(); i++) {
                                    if (MomentTypeUtil.isOfficialType(moments.get(i))) {
                                        advertIds.add(moments.get(i).getMomentId());
                                    }
                                }
                                if (CollectionUtil.isEmpty(advertIds)) {
                                    return getMomentsWithoutAdvertise(moments, offset, count, watchId, finalEndTime);
                                }
                                return getMomentsWithAdvertise(moments, offset, count, watchId, advertIds, finalEndTime);
                            }
                        })
                        .map(new Func1<List<DbMoment>, List<DbMoment>>() {
                            @Override
                            public List<DbMoment> call(List<DbMoment> moments) {
                                if (CollectionUtil.isEmpty(moments)) {
                                    return moments;
                                }
                                boolean lbsSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(
                                        MomentPresenter.this.mContext, ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
                                List<String> lbsAnimList = SharedTool.getLbsAnimList(MomentPresenter.this.mContext);
                                for (DbMoment moment : moments) {
                                    moment.setLbsSwitch(lbsSwitch);
                                    moment.setShowLbsAnim(!(lbsAnimList.contains(moment.getMomentId())
                                            || watchId.equals(moment.getWatchId())));
                                }
                                return moments;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<DbMoment>>() {
                            @Override
                            public void onCompleted() {
                                LogUtil.d(TAG, "getMomentsFromNet onCompleted");
                                MomentPresenter.this.loading = false;
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                LogUtil.e(TAG, throwable);
                                if (MomentPresenter.this.isViewAttached()) {
                                    MomentPresenter.this.getView().loadLocalData(offset, count);
                                }
                                MomentPresenter.this.loading = false;
                            }

                            @Override
                            public void onNext(List<DbMoment> moments) {
                                if (!MomentPresenter.this.isViewAttached() || MomentPresenter.this.getView() == null) {
                                    return;
                                }
                                MomentPresenter.this.getView().loadSuccess(moments);
                                if (offset == 0) {
                                    UnreadHelper.getInstance(MomentPresenter.this.mContext).hideUnreadPoint();
                                }
                                MomentPresenter.this.notifyUnReadPoint();
                            }
                        });
            } catch (Exception e) {
                LogUtil.e(TAG, "loadMomentsFromNet: ", e);
            }
        }
    }

    /** 非首页：直接落库并回读本地数据。 */
    private Observable<List<DbMoment>> getMomentsWithoutAdvertise(List<DbMoment> moments, final long offset,
            final long count, final String watchId, final long endTime) {
        return Observable.just(moments)
                .map(new Func1<List<DbMoment>, List<DbMoment>>() {
                    @Override
                    public List<DbMoment> call(List<DbMoment> list) {
                        MomentPresenter.this.updateLocalMoment(list, watchId, offset, endTime);
                        return MomentPresenter.this.iMomentServe.getMomentsFromDbSync(offset, count, 1, watchId);
                    }
                });
    }

    /** 首页含官方动态：先补充官方评论再落库。 */
    private Observable<List<DbMoment>> getMomentsWithAdvertise(final List<DbMoment> moments, final long offset,
            final long count, final String watchId, List<String> advertIds, final long endTime) {
        return this.iMomentServe.getOfficialCommentFromNet(watchId, advertIds)
                .map(new Func1<List<OfficialCommentResultBean>, List<DbMoment>>() {
                    @Override
                    public List<DbMoment> call(List<OfficialCommentResultBean> officialComments) {
                        for (int i = 0; i < moments.size(); i++) {
                            for (int j = 0; j < officialComments.size(); j++) {
                                if (moments.get(i).getMomentId().equals(officialComments.get(j).getAdvertId())) {
                                    moments.get(i).setCommentsTotalCount(officialComments.get(j).getTotalCount());
                                    moments.get(i).setComments(BeanConverterUtil.convertToDbMomentCommentList(
                                            officialComments.get(j).getList(), moments.get(i).getWatchId()));
                                }
                            }
                        }
                        MomentPresenter.this.updateLocalMoment(moments, watchId, offset, endTime);
                        return MomentPresenter.this.iMomentServe.getMomentsFromDbSync(offset, count, 1, watchId);
                    }
                });
    }
    /** 删除本地所有官方动态（开关变更等场景）。 */
    private void deleteLocalOfficialData() {
        List<DbMoment> localAdvertiseList = getMomentsLocalDataSource().getLocalAdvertiseList(this.mContext);
        if (CollectionUtil.isEmpty(localAdvertiseList)) {
            return;
        }
        ArrayList<String> momentIds = new ArrayList<>();
        for (DbMoment moment : localAdvertiseList) {
            if (MomentTypeUtil.isOfficialType(moment)) {
                momentIds.add(moment.getMomentId());
            }
        }
        if (CollectionUtil.isEmpty(momentIds)) {
            LogUtil.i(TAG, "deleteLocalOfficialData: momentIdList is empty");
        } else {
            deleteLocalOfficialData(momentIds);
        }
    }

    /** 删除本地已过期的官方动态（保留仍在下发的 advertId）。 */
    private void deleteExpiredOfficialData(List<String> advertIds) {
        List<DbMoment> localAdvertiseList = getMomentsLocalDataSource().getLocalAdvertiseList(this.mContext);
        if (CollectionUtil.isEmpty(localAdvertiseList)) {
            return;
        }
        ArrayList<String> expiredIds = new ArrayList<>();
        for (DbMoment moment : localAdvertiseList) {
            boolean stillValid = false;
            Iterator<String> iterator = advertIds.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(moment.getMomentId())) {
                    stillValid = true;
                    break;
                }
            }
            if (!stillValid && MomentTypeUtil.isOfficialType(moment)) {
                expiredIds.add(moment.getMomentId());
            }
        }
        if (CollectionUtil.isEmpty(expiredIds)) {
            LogUtil.i(TAG, "deleteExpiredOfficialData: momentIdList is empty");
        } else {
            deleteLocalOfficialData(expiredIds);
        }
    }

    private void deleteLocalOfficialData(List<String> momentIds) {
        LogUtil.d(TAG, "deleteLocalOfficialData " + momentIds);
        if (momentIds.size() > 0) {
            for (int i = 0; i < momentIds.size(); i++) {
                getMomentsLocalDataSource().deleteMomentByMomentId(TAG, momentIds.get(i));
                getMomentsLocalDataSource().deleteLikeDaoByMomentId(momentIds.get(i));
                getMomentsLocalDataSource().deleteCommentDaoByMomentId(momentIds.get(i));
            }
        }
    }

    private void deleteLocalOfficialData(String momentId) {
        LogUtil.d(TAG, "deleteLocalOfficialData " + momentId);
        if (momentId == null || momentId.length() >= 40) {
            return;
        }
        boolean momentDeleted = getMomentsLocalDataSource().deleteMomentByMomentId(TAG, momentId);
        boolean likeDeleted = getMomentsLocalDataSource().deleteLikeDaoByMomentId(momentId);
        boolean commentDeleted = getMomentsLocalDataSource().deleteCommentDaoByMomentId(momentId);
        LogUtil.d(TAG, "deleteMomentByMomentId：" + momentDeleted + ", deleteLikeDaoByMomentId：" + likeDeleted
                + ", deleteCommentDaoByMomentId：" + commentDeleted);
    }

    /**
     * 合并网络动态与本地缓存：写入新动态、清理本地已下线的动态，并刷新点赞记录。
     */
    private void updateLocalMoment(List<DbMoment> moments, String watchId, long offset, long endTime) {
        LogUtil.d(TAG, "updateLocalMoment() called with: offset = [" + offset + "], endTime = [" + endTime + "]");
        List<DbMoment> filteredMoments = filter(moments);
        Comparator<DbMoment> createTimeDesc = new Comparator<DbMoment>() {
            @Override
            public int compare(DbMoment first, DbMoment second) {
                return Long.compare(second.getCreateTime(), first.getCreateTime());
            }
        };
        Collections.sort(filteredMoments, createTimeDesc);

        // 剔除已存在于本次会话的重复动态，并把新动态加入会话缓存。
        Iterator<DbMoment> iterator = filteredMoments.iterator();
        while (iterator.hasNext()) {
            DbMoment moment = iterator.next();
            if (this.momentsMap.containsKey(moment.getMomentId())) {
                iterator.remove();
            } else if (!MomentTypeUtil.isOfficialType(moment)) {
                this.momentsMap.put(moment.getMomentId(), moment);
            }
        }

        ArrayList<DbMoment> allMoments = new ArrayList<>(this.momentsMap.values());
        Collections.sort(allMoments, createTimeDesc);
        long startTime;
        if (CollectionUtil.isEmpty(filteredMoments) || CollectionUtil.isEmpty(allMoments)) {
            endTime = 0;
            startTime = 0;
        } else {
            startTime = getStartTime(filteredMoments);
            long newestTime = filteredMoments.get(0).getCreateTime();
            if (offset == 0) {
                LogUtil.d(TAG, "offset == 0, timeEnd = " + endTime);
            } else if (newestTime < allMoments.get(allMoments.size() - 1).getCreateTime()) {
                endTime = allMoments.get(allMoments.size() - 1).getCreateTime();
                LogUtil.d(TAG, "offset != 0, timeEnd = " + endTime);
            } else {
                endTime = newestTime;
            }
        }
        LogUtil.d(TAG, "timeStart = " + startTime);

        // 本地库里不在本次结果中的动态视为已被删除。
        List<DbMoment> localMoments = this.iMomentServe.queryDbMoments(startTime, endTime);
        LogUtil.d(TAG, "localList size: " + localMoments.size());
        if (localMoments.size() > 20) {
            BehaviorEvent.recordLocalSize(this.mContext, localMoments.size());
        }
        ArrayList<DbMoment> needDeleteMoments = new ArrayList<>();
        if (!CollectionUtil.isEmpty(localMoments)) {
            for (DbMoment moment : localMoments) {
                if (!this.momentsMap.containsKey(moment.getMomentId())
                        && !MomentTypeUtil.isOfficialType(moment)) {
                    LogUtil.d(TAG, "需要被删除的动态：" + moment.getMomentId());
                    needDeleteMoments.add(moment);
                }
            }
        }
        this.iMomentServe.batchUpdateIMReminderStatus(needDeleteMoments, DbIMReminder.UNSET);
        if (this.iMomentServe.deleteDbMomentForBatch(needDeleteMoments)) {
            setDeleteList(needDeleteMoments);
        }
        if (CollectionUtil.isEmpty(filteredMoments)) {
            return;
        }
        // 补充特权装扮的背景图本地路径。
        for (int i = 0; i < filteredMoments.size(); i++) {
            DbMoment moment = filteredMoments.get(i);
            LogUtil.d(TAG, "updateLocalMoment " + moment);
            if (moment.getEmotionId() != 0 && TextUtils.isEmpty(moment.getMomentBgPath())) {
                DbMomentPrerogativeBackground background = MomentPrerogativeServeImpl.getInstance(this.mContext)
                        .getPrerogativeBackgroundByEmotionId(moment.getEmotionId());
                if (background != null && !TextUtils.isEmpty(background.getLocalEmotionPath())) {
                    moment.setMomentBgPath(background.getLocalEmotionPath());
                }
            }
        }
        this.iMomentServe.addMoments(filteredMoments);
        this.iMomentServe.addCommentMoment(BeanConverterUtil.mapCommentList(filteredMoments));
        loadMomentLike(getMomentIds(filteredMoments), watchId);
        dealReportBrowseMoment(filteredMoments.get(0), offset);
    }

    /** 取列表中最旧的非官方动态时间，作为本地查询起点。 */
    private long getStartTime(List<DbMoment> moments) {
        for (int i = moments.size() - 1; i >= 0; i--) {
            DbMoment moment = moments.get(i);
            if (!MomentTypeUtil.isOfficialType(moment)) {
                return moment.getCreateTime();
            }
        }
        return System.currentTimeMillis();
    }

    /** 首页曝光时上报浏览行为，同一条动态只上报一次。 */
    private void dealReportBrowseMoment(DbMoment moment, long offset) {
        if (moment == null || offset != 0) {
            return;
        }
        final String momentId = moment.getMomentId();
        if (Objects.equals(momentId, SharedTool.getLastReportMomentId(this.mContext))) {
            LogUtil.d(TAG, "dealReportBrowseMoment: no need report");
            return;
        }
        String watchId = this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext);
        ReportMomentReq request = new ReportMomentReq();
        request.setMomentId(momentId);
        request.setWatchId(watchId);
        getMomentsRepository().reportMoment(request)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String result) {
                        SharedTool.saveLastReportMomentId(MomentPresenter.this.mContext, momentId);
                        LogUtil.d(TAG, "dealReportBrowseMoment call: " + result + ";");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "dealReportBrowseMoment call: ", throwable);
                    }
                });
    }

    private List<String> getMomentIds(List<DbMoment> moments) {
        ArrayList<String> momentIds = new ArrayList<>();
        for (DbMoment moment : moments) {
            if (!TextUtils.isEmpty(moment.getMomentId())) {
                momentIds.add(moment.getMomentId());
            }
        }
        return momentIds;
    }

    /** 加载一批动态的点赞记录，并缓存到本地库。 */
    public void loadMomentLike(List<String> momentIds, String watchId) {
        this.iMomentServe.getPraiseRecord(momentIds, watchId)
                .map(new Func1<PraiseResponse, Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public Map<String, List<DbLikeMessage>> call(PraiseResponse response) {
                        Map<String, List<MomentLikeVo>> momentLikeMap = response.getMomentLikeMap();
                        HashMap<String, List<DbLikeMessage>> result = new HashMap<>();
                        MomentPresenter.this.iMomentServe.clearLikeDbData();
                        if (momentLikeMap != null) {
                            for (Map.Entry<String, List<MomentLikeVo>> entry : momentLikeMap.entrySet()) {
                                List<DbLikeMessage> likeMessages = BeanConverterUtil.convertToDbLikeMessage(entry.getValue());
                                MomentPresenter.this.iMomentServe.addLikeMessage(likeMessages);
                                result.put(entry.getKey(), likeMessages);
                            }
                        }
                        LogUtil.d(TAG, "praiseRecordMap:" + result.toString());
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(Map<String, List<DbLikeMessage>> likeMap) {
                        if (MomentPresenter.this.isViewAttached()) {
                            MomentPresenter.this.getView().refreshMomentLikes(likeMap);
                        }
                    }
                });
    }
    /** 从本地数据库分页加载动态；可指定一条动态作为加载截止标记。 */
    public void loadMomentsFromDb(final long offset, final long count, final boolean paging, final String stopMomentId) {
        final String watchId = this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext);
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.w(TAG, "loadMomentsFromDb watchId is null");
            return;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final ArrayList<DbMoment> result = new ArrayList<>();
                if (!paging) {
                    List<DbMoment> moments = MomentPresenter.this.iMomentServe.getMomentsFromDbSync(
                            offset, count, 1, watchId);
                    if (!CollectionUtil.isEmpty(moments)) {
                        result.addAll(moments);
                    }
                } else {
                    // 分页直到命中目标动态或遍历完本地库。
                    long pageOffset = offset;
                    boolean found = false;
                    do {
                        // 注意：分页查询始终使用原始 offset，仅用 pageOffset 控制循环次数。
                        List<DbMoment> moments = MomentPresenter.this.iMomentServe.getMomentsFromDbSync(
                                offset, count, 1, watchId);
                        if (!CollectionUtil.isEmpty(moments)) {
                            result.addAll(moments);
                            for (DbMoment moment : moments) {
                                if (!TextUtils.isEmpty(stopMomentId) && stopMomentId.equals(moment.getMomentId())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        pageOffset += count;
                        if (found) {
                            break;
                        }
                    } while (pageOffset <= MomentPresenter.this.iMomentServe.getMomentCountInDb());
                }
                boolean lbsSwitch = ModuleSwitchUtil.queryModuleSwitchByBoolean(MomentPresenter.this.mContext,
                        ModuleSwitchConstant.MODULE_SWITCH_LBS_PUBLISH, false);
                List<String> lbsAnimList = SharedTool.getLbsAnimList(MomentPresenter.this.mContext);
                for (DbMoment moment : result) {
                    moment.setLbsSwitch(lbsSwitch);
                    moment.setShowLbsAnim(!(lbsAnimList.contains(moment.getMomentId())
                            || watchId.equals(moment.getWatchId())));
                }
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (MomentPresenter.this.isViewAttached()) {
                            MomentPresenter.this.getView().loadSuccess(result);
                        }
                    }
                });
            }
        });
    }

    public boolean isWatchBind() {
        return this.isBind;
    }

    /** SIM 卡缺失状态（未插卡或 PIN 锁定）。 */
    public boolean isSimStateAbsent() {
        return getSimState() == 1 || getSimState() == 6;
    }

    private int getSimState() {
        return ((TelephonyManager) this.mContext.getSystemService(Constants.PHONE)).getSimState();
    }

    @Override
    public String getLogTag() {
        return TAG;
    }

    public List<Friend> getFriendInfo() {
        IFriendInfoServe serve = this.friendInfoServe;
        if (serve == null) {
            return new ArrayList<>();
        }
        return serve.getAllFriendInfo();
    }

    public List<DbMoment> getMomentById(String momentId) {
        return this.iMomentServe.getMomentById(momentId);
    }

    public List<DbLikeMessage> loadNewLikeMessageAboutMine() {
        return this.iMomentServe.loadAllUncheckedLikeMessageByWatchId(
                this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext));
    }

    public long loadNewLikeMessageCountAboutMine() {
        return this.iMomentServe.getUncheckedLikeMessageCountByWatchId(
                this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext));
    }

    public void addLikeMessage(DbLikeMessage likeMessage) {
        if (likeMessage == null) {
            LogUtil.w(TAG, "addLikeMessage:dbLikeMessage is null");
        } else if (this.iMomentServe.increaseLikeTotal(likeMessage.getMomentId())) {
            this.iMomentServe.addLikeMessage(likeMessage);
        } else {
            LogUtil.w(TAG, "addLikeMessage failed because of increaseLikeTotal return false");
        }
    }

    public void updateLikeMessageName(String watchId, String watchName) {
        if (TextUtils.isEmpty(watchId) || TextUtils.isEmpty(watchName)) {
            return;
        }
        this.iMomentServe.updateLikeMessageName(watchId, watchName);
    }

    public boolean isAboutMyMoment(String watchId) {
        return !TextUtils.isEmpty(watchId) && watchId.equals(
                this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext));
    }

    public void setMyName(String name) {
        this.accountInfoServer.getWatchAccountInfo().setName(name);
    }

    public String getMyName(Context context) {
        return !this.accountInfoServer.isInit() ? "" : this.accountInfoServer.getWatchAccountInfo().getName(context);
    }

    public String getMyHeadIconPath() {
        return this.accountInfoServer.getMyHeadIconPath();
    }

    public WatchAccountInfo getWatchAccountInfo() {
        return this.accountInfoServer.getWatchAccountInfo();
    }

    public List<DbMoment> getOthersUnCheckedMomentFromDb(String watchId) {
        return this.iMomentServe.getOthersUncheckedMoment(watchId);
    }

    /** 统计他人发布但本地尚未标记已读的动态数量。 */
    public long getUnCheckPublishedMomentCount() {
        LogUtil.d(TAG, "getUnCheckPublishedMomentCount");
        String watchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        if (TextUtils.isEmpty(watchId)) {
            return 0L;
        }
        return this.iMomentServe.getOthersUncheckedMomentCount(watchId);
    }

    /** 把他人发布的动态全部标记为已读。 */
    public void checkAllPublishedMoment() {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                List<DbMoment> unCheckedMoments = MomentPresenter.this.getOthersUnCheckedMomentFromDb(
                        AccountInfoServerImpl.getInstance(MomentPresenter.this.mContext).getWatchAccountInfo()
                                .getWatchId(MomentPresenter.this.mContext));
                if (unCheckedMoments == null || unCheckedMoments.isEmpty()) {
                    return;
                }
                for (DbMoment moment : unCheckedMoments) {
                    if (moment != null) {
                        moment.setChecked(true);
                    }
                }
                MomentPresenter.this.iMomentServe.updateMomentsByMomentId(unCheckedMoments);
                UnreadHelper.getInstance(MomentPresenter.this.mContext).hideUnreadPoint();
                MomentPresenter.this.notifyUnReadPoint();
            }
        });
    }

    private void notifyUnReadPoint() {
        LogUtil.d(TAG, "notifyUnReadPoint");
        this.mContext.getContentResolver().notifyChange(
                Uri.parse("content://com.xtc.moment.momentProvider/momentunread"), null);
    }
    /**
     * 根据服务端下发的失效动态信息，在本地库中定位并删除对应的图片/视频动态。
     *
     * @return 被删除的动态；未找到时返回 null
     */
    public DbMoment deleteSexyMsgInDB(NetInvalidMoment netInvalidMoment) {
        long allMomentCount = this.iMomentServe.getAllMoment();
        LogUtil.d(TAG, "allMsgCount:" + allMomentCount);
        long pageCount = (allMomentCount / 50) + 1;
        int pageIndex = 0;
        while (true) {
            long pageOffset = pageIndex;
            if (pageOffset >= pageCount) {
                break;
            }
            List<DbMoment> moments = this.iMomentServe.queryMessageForPages(pageOffset * pageCount, 50L, false);
            if (moments == null || CollectionUtil.isEmpty(moments)) {
                LogUtil.i(TAG, "need to deleteSexyMsgInDB but messageList is empty.");
                break;
            }
            for (DbMoment moment : moments) {
                if (moment == null || (moment.getType().intValue() != 5 && moment.getType().intValue() != 4)) {
                    continue;
                }
                if (NetInvalidMoment.MOMENTIDS.equals(netInvalidMoment.getAction())) {
                    Iterator<String> iterator = netInvalidMoment.getMomentIds().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(moment.getMomentId())) {
                            this.iMomentServe.deleteDBMomentByMomentId(moment);
                            LogUtil.d(TAG, "delete invalid Moment success!moment:" + moment);
                            return moment;
                        }
                    }
                } else if (NetInvalidMoment.PARENTIDS.equals(netInvalidMoment.getAction())) {
                    Iterator<String> iterator = netInvalidMoment.getParentIds().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(moment.getMomentId())) {
                            this.iMomentServe.deleteDBMomentByMomentId(moment);
                            LogUtil.d(TAG, "delete invalid Moment success!moment:" + moment);
                            return moment;
                        }
                    }
                }
            }
            pageIndex++;
        }
        return null;
    }

    public boolean addSexyPhotoRecord(List<DbSexyPhotoDistinguish> records) {
        return this.iMomentServe.addSexyPhotoRecord(records);
    }

    public void deleteMomentInDB(DbMoment moment) {
        this.iMomentServe.deleteDBMomentByMomentId(moment);
    }

    public void addMomentComment(DbMomentComment comment) {
        if (comment == null) {
            LogUtil.w(TAG, "addMomentComment:dbMomentComment is null");
        } else {
            this.iMomentServe.addCommentMoment(comment);
        }
    }

    public List<DbMomentComment> loadNewCommentAboutMine() {
        return this.iMomentServe.loadAllUncheckedCommentByWatchId(
                this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext));
    }

    public long loadNewCommentCountAboutMine() {
        return this.iMomentServe.getUncheckedCommentCountByWatchId(
                this.accountInfoServer.getWatchAccountInfo().getWatchId(this.mContext));
    }

    public void deleteMomentCommentInDB(DbMomentComment comment) {
        this.iMomentServe.deleteDBCommentByMomentIdAndCommentId(comment.getMomentId(), comment.getCommentId());
    }

    public boolean isAboutMyComment(String replyId) {
        String watchId = AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo()
                .getWatchId(this.mContext);
        LogUtil.d(TAG, "watchId:" + watchId + ";replyId:" + replyId);
        return !TextUtils.isEmpty(replyId) && replyId.equals(watchId);
    }

    /** 官方动态滑出屏幕时上报跳过行为，同一条只上报一次。 */
    public void checkSkipBehavior(final Context context, DbMoment moment) {
        if (moment == null || !MomentTypeUtil.isOfficialType(moment.getType().intValue())) {
            return;
        }
        if (this.skipMomentIdData == null) {
            this.skipMomentIdData = new ArrayList<>();
        }
        if (this.skipMomentIdData.size() > 0 && this.skipMomentIdData.contains(moment.getMomentId())) {
            LogUtil.d(TAG, "moment has skip");
            return;
        }
        this.skipMomentIdData.add(moment.getMomentId());
        Observable.just(moment.getMomentId())
                .map(new Func1<String, List<DbMoment>>() {
                    @Override
                    public List<DbMoment> call(String momentId) {
                        List<DbMoment> moments = MomentPresenter.this.iMomentServe.getMomentById(momentId);
                        if (moments == null || moments.size() <= 0) {
                            return new ArrayList<>();
                        }
                        DbMoment local = moments.get(0);
                        if (local.isSkiped()) {
                            LogUtil.d(TAG, "has push skip behavior before.MomentId:" + local.getMomentId());
                        } else {
                            local.setSkiped(true);
                            MomentPresenter.this.iMomentServe.updateMoment(local);
                            MomentBehavior.behaviorOfficialMoment(context, new PushBehaviorBean(local.getMomentId(), 0));
                            LogUtil.d(TAG, "push skip behavior.MomentId:" + local.getMomentId());
                        }
                        return moments;
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /** 官方动态进入可见区域时上报曝光行为，同一条只上报一次。 */
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
                        return MomentPresenter.this.iMomentServe.getMomentById(momentId);
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
                        DbMoment local = moments.get(0);
                        if (local.isPreviewed()) {
                            LogUtil.d(TAG, "has push preview behavior before");
                            return;
                        }
                        local.setPreviewed(true);
                        MomentPresenter.this.iMomentServe.updateMoment(local);
                        MomentBehavior.behaviorOfficialMoment(context, new PushBehaviorBean(local.getMomentId(), 1));
                        LogUtil.d(TAG, "push preview behavior");
                    }
                });
    }

    /** 初始化违规（禁言）状态，并监听状态变化回调给界面。 */
    public void dealIllegal() {
        final IllegalMessageHandler illegalMessageHandler = IllegalMessageHandler.getInstance(this.mContext);
        illegalMessageHandler.setActiveChangeIllegalStateListener(
                new IllegalMessageHandler.ActiveChangeIllegalStateListener() {
                    @Override
                    public void onActiveChangeIllegalState(int state) {
                        if (MomentPresenter.this.getView() != null) {
                            MomentPresenter.this.getView().dealIllegal();
                        }
                    }
                });
        if (illegalMessageHandler.isInitHandler() && getView() != null) {
            getView().dealIllegal();
        } else {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    MomentPresenter.this.initIllegalConfig(illegalMessageHandler);
                }
            });
        }
    }

    private void initIllegalConfig(IllegalMessageHandler illegalMessageHandler) {
        illegalMessageHandler.initIllegalConfig(this.mContext,
                IllegalConfigHandler.obtainConfig(this.mContext.getApplicationContext()),
                new IllegalMessageHandler.InitIllegalListener() {
                    @Override
                    public void onIIllegalFinish() {
                        if (MomentPresenter.this.getView() != null) {
                            MomentPresenter.this.getView().dealIllegal();
                        }
                    }
                });
    }

    public void clearRecordMap() {
        LinkedHashMap<String, DbMoment> map = this.momentsMap;
        if (map != null && map.size() > 0) {
            this.momentsMap.clear();
        }
        List<DbMoment> list = this.deletedList;
        if (list == null || list.size() <= 0) {
            return;
        }
        this.deletedList.clear();
    }

    public List<DbMoment> getDeleteList() {
        return this.deletedList;
    }

    private void setDeleteList(List<DbMoment> list) {
        this.deletedList = list;
    }
}
package com.xtc.moment.module.search;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.LogTag;
import com.xtc.moment.MomentApp;
import com.xtc.moment.base.BaseInteractPresenter;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.MessageTransitionServe;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.LikeDrawableCache;
import com.xtc.moment.util.MomentSearchTextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 搜索动态的 Presenter：在本地 moment 表里分块粗筛 + 内存精确校验，分页返回结果。
 *
 * <p>手表内存有限，因此不把整张表读进内存，而是按 {@link #CHUNK_SIZE} 行分块扫描；
 * 每页攒够 {@link #RESULT_PAGE_SIZE} 条就交给界面，滚动到底再继续扫。
 */
public class SearchMomentPresenter extends BaseInteractPresenter<ISearchMomentView> {

    private static final String TAG = LogTag.tag("SearchMomentPresenter");

    /** 每次向数据库取多少行候选。 */
    private static final int CHUNK_SIZE = 200;

    /** 每页返回给界面的结果条数。 */
    private static final int RESULT_PAGE_SIZE = 20;

    /** 单次搜索最多扫描多少行，避免在手表上长时间占用数据库。 */
    private static final int MAX_SCAN_ROWS = 2000;

    private final LikeDrawableCache likeDrawableCache;

    /** 已去重命中的动态 id，避免同一动态被重复展示。 */
    private final Set<String> seenMomentIds = new HashSet<>();

    private boolean loading;
    private String keyword = "";
    private boolean reachedEnd = true;
    private long scanOffset;
    private int scannedRows;

    /** 搜索代次：翻页/重新搜索时递增，用于丢弃过期的后台结果。 */
    private int searchToken;

    public SearchMomentPresenter(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.likeDrawableCache = new LikeDrawableCache(context);
    }

    /** 发起一次新的搜索。 */
    public void search(String rawKeyword) {
        if (!MomentSearchTextUtil.isValidKeyword(rawKeyword)) {
            if (isViewAttached() && getView() != null) {
                getView().onSearchKeywordEmpty();
            }
            return;
        }
        this.keyword = MomentSearchTextUtil.normalize(rawKeyword);
        this.scanOffset = 0L;
        this.scannedRows = 0;
        this.reachedEnd = false;
        this.loading = false;
        this.seenMomentIds.clear();
        this.searchToken++;
        LogUtil.i(TAG, "search: keyword = " + this.keyword);
        if (isViewAttached() && getView() != null) {
            getView().onSearchStart(rawKeyword.trim());
        }
        loadMore(true);
    }

    /** 继续加载下一页（滚动到底时调用）。 */
    public void loadMore() {
        loadMore(false);
    }

    /** 本次搜索是否已经扫完（界面据此避免无效的续搜）。 */
    public boolean isReachedEnd() {
        return this.reachedEnd;
    }

    /** 放弃当前搜索（输入框被清空时）：让在跑的后台扫描结果作废。 */
    public void cancelSearch() {
        this.searchToken++;
        this.keyword = "";
        this.reachedEnd = true;
        this.loading = false;
        this.scanOffset = 0L;
        this.scannedRows = 0;
        this.seenMomentIds.clear();
    }

    /**
     * 扫描并返回下一页结果。
     *
     * @param firstPage 是否是本次搜索的第一页
     */
    private void loadMore(final boolean firstPage) {
        if (this.loading || this.reachedEnd) {
            return;
        }
        this.loading = true;
        final String currentKeyword = this.keyword;
        final int token = this.searchToken;
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                final List<DbMoment> page = new ArrayList<>();
                boolean end = false;
                while (page.size() < RESULT_PAGE_SIZE) {
                    if (SearchMomentPresenter.this.scannedRows >= MAX_SCAN_ROWS) {
                        LogUtil.i(TAG, "loadMore: reach max scan rows " + MAX_SCAN_ROWS);
                        end = true;
                        break;
                    }
                    List<DbMoment> candidates = SearchMomentPresenter.this.iMomentServe
                            .searchMomentCandidates(currentKeyword, SearchMomentPresenter.this.scanOffset, CHUNK_SIZE);
                    if (candidates == null || candidates.isEmpty()) {
                        end = true;
                        break;
                    }
                    SearchMomentPresenter.this.scanOffset += candidates.size();
                    SearchMomentPresenter.this.scannedRows += candidates.size();
                    for (DbMoment candidate : candidates) {
                        if (isUsable(candidate) && MomentSearchTextUtil.matches(candidate, currentKeyword)
                                && SearchMomentPresenter.this.seenMomentIds.add(candidate.getMomentId())) {
                            page.add(candidate);
                        }
                    }
                    if (candidates.size() < CHUNK_SIZE) {
                        end = true;
                        break;
                    }
                }
                final boolean reachedEnd = end;
                // 归一化动态类型、补上本地评论与点赞记录，界面可直接渲染。
                final List<DbMoment> normalized = new ArrayList<>(page.size());
                for (DbMoment moment : page) {
                    normalized.add(MessageTransitionServe.dealMsgTypeTransition(moment));
                }
                final List<DbMoment> withComments = SearchMomentPresenter.this.iMomentServe
                        .attachCommentsForMoments(normalized);
                final Map<String, List<DbLikeMessage>> praiseRecords = loadPraiseRecords(withComments);
                // 点赞头像要走网络缓存，放到主线程（列表侧渲染依赖它）。
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLikeDrawable(praiseRecords);
                        SearchMomentPresenter.this.loading = false;
                        if (token != SearchMomentPresenter.this.searchToken
                                || !SearchMomentPresenter.this.isViewAttached()
                                || SearchMomentPresenter.this.getView() == null) {
                            LogUtil.d(TAG, "loadMore: drop stale result, token = " + token);
                            return;
                        }
                        SearchMomentPresenter.this.reachedEnd = reachedEnd;
                        SearchMomentPresenter.this.getView().onSearchPage(
                                withComments == null ? new ArrayList<DbMoment>() : withComments,
                                praiseRecords, firstPage, reachedEnd);
                    }
                });
            }
        });
    }

    /** 本地点赞记录；没有点赞时返回空 Map（界面侧不接受 null）。 */
    private Map<String, List<DbLikeMessage>> loadPraiseRecords(List<DbMoment> moments) {
        Map<String, List<DbLikeMessage>> records = new HashMap<>();
        if (moments == null || moments.isEmpty()) {
            return records;
        }
        for (DbMoment moment : moments) {
            List<DbLikeMessage> likeMessages = this.iMomentServe
                    .getLikeMessageByMomentId(moment.getMomentId());
            if (likeMessages != null && !likeMessages.isEmpty()) {
                records.put(moment.getMomentId(), likeMessages);
            }
        }
        return records;
    }

    /** 预加载「装扮点赞」的头像资源，供列表侧同步取用。 */
    private void loadLikeDrawable(Map<String, List<DbLikeMessage>> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        String selfWatchId = MomentApp.getWatchId();
        for (List<DbLikeMessage> likeMessages : records.values()) {
            for (DbLikeMessage likeMessage : likeMessages) {
                if (likeMessage.getEmotionId() == 0 || TextUtils.isEmpty(selfWatchId)) {
                    continue;
                }
                DbMomentPrerogativeLike prerogativeLike = MomentPrerogativeServeImpl.getInstance(this.mContext)
                        .getPrerogativeLikeByEmotionId(likeMessage.getEmotionId());
                if (prerogativeLike == null) {
                    continue;
                }
                // 自己被限制发送时，服务端下发的可用图与常态不同。
                boolean isSelf = selfWatchId.equals(likeMessage.getWatchId());
                String likedPic = isSelf && IllegalMessageHandler.getInstance(this.mContext).isDisableSend()
                        ? prerogativeLike.getPraisedDisablePic()
                        : prerogativeLike.getPraisedPic();
                this.likeDrawableCache.loadImageFromNetToCache(likedPic);
                likeMessage.setLikedPic(likedPic);
            }
        }
    }

    /** 脏数据防御：缺关键字段的动态不进列表（否则列表侧会 NPE）。 */
    private static boolean isUsable(DbMoment moment) {
        return moment != null
                && !TextUtils.isEmpty(moment.getMomentId())
                && !TextUtils.isEmpty(moment.getWatchId())
                && moment.getType() != null
                && moment.getCreateTime() != null;
    }

    public LikeDrawableCache getLikeDrawableCache() {
        return this.likeDrawableCache;
    }

    /** 读取某条动态的最新本地数据（点赞回调后刷新卡片用）。 */
    public List<DbMoment> getMomentById(String momentId) {
        return this.iMomentServe.getMomentById(momentId);
    }

    /** 读取某条动态的本地点赞记录，并预加载点赞头像。 */
    public List<DbLikeMessage> getLikeMessageByMomentId(String momentId) {
        List<DbLikeMessage> likeMessages = this.iMomentServe.getLikeMessageByMomentId(momentId);
        if (likeMessages == null || likeMessages.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, List<DbLikeMessage>> records = new HashMap<>();
        records.put(momentId, likeMessages);
        loadLikeDrawable(records);
        return likeMessages;
    }

    @Override
    public String getLogTag() {
        return TAG;
    }
}

package com.xtc.moment.tasks.domain.usecase;

import android.content.Context;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.common.base.Preconditions;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.CommentServeImpl;
import com.xtc.moment.serve.ICommentServe;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * 查询礼物任务：同时拉取礼物列表与最新评论，合并成评论区数据并回调。
 */
public class SearchGiftTask extends AbsTask<SearchGiftTask.RequestValues, AbsTask.ResponseValue> {

    private static final int COUNT = 12;
    private static final int PAGE_NUM = 1;

    private ContactManager contactManager;
    private Context context;
    private final ICommentServe iCommentServe;
    private final IMomentServe iMomentServe;
    private final IMomentsDataSource mMomentsRepository;

    public SearchGiftTask(IMomentsDataSource momentsRepository, Context context) {
        this.mMomentsRepository = Preconditions.checkNotNull(momentsRepository);
        setThreadType(2);
        this.contactManager = ContactManager.getInstance(context);
        this.iCommentServe = CommentServeImpl.getInstance(context);
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.context = context;
    }

    @Override
    protected void executeTask(final RequestValues requestValues) {
        Observable.combineLatest(
                this.mMomentsRepository.searchGift(requestValues.getRequest()),
                this.iCommentServe.searchCommentFromNet(PAGE_NUM, COUNT, requestValues.moment.getMomentId(), requestValues.moment.getWatchId(), false),
                new Func2<SearchGiftResponse, List<DbMomentComment>, SearchGiftResponse>() {
                    @Override
                    public SearchGiftResponse call(SearchGiftResponse searchGiftResponse, List<DbMomentComment> comments) {
                        if (!CollectionUtil.isEmpty(comments)) {
                            SearchGiftTask.this.iMomentServe.addCommentMoment(comments);
                            requestValues.getMoment().setComments(comments);
                        }
                        return searchGiftResponse;
                    }
                })
                .map(new Func1<SearchGiftResponse, SearchGiftResponse>() {
                    @Override
                    public SearchGiftResponse call(SearchGiftResponse searchGiftResponse) {
                        if (searchGiftResponse != null) {
                            List<SearchGiftResponse.GiftsEntity> gifts = searchGiftResponse.getGifts();
                            List<DbMomentComment> comments = requestValues.getMoment().getComments();
                            if (requestValues.getRequest().getCount() == 0) {
                                comments = new ArrayList<>();
                            }
                            if (CollectionUtil.isEmpty(comments)) {
                                comments = new ArrayList<>();
                            }
                            if (!CollectionUtil.isEmpty(gifts) && gifts.size() > 0) {
                                for (int i = 0; i < gifts.size(); i++) {
                                    SearchGiftResponse.GiftsEntity gift = gifts.get(i);
                                    DbMomentComment comment = new DbMomentComment();
                                    String replyId = gift.getReplyId();
                                    if (AccountInfoServerImpl.getInstance(SearchGiftTask.this.context).getWatchAccountInfo().getWatchId(SearchGiftTask.this.context).equals(replyId)) {
                                        comment.setWatchName(SearchGiftTask.this.context.getString(R.string.me));
                                    } else {
                                        ContactBean contact = SearchGiftTask.this.contactManager.getContactByWatchIdSync(replyId);
                                        if (contact == null) {
                                            comment.setWatchName(SearchGiftTask.this.context.getString(R.string.unknown_watch));
                                        } else {
                                            LogUtil.w("AbsTask", "call: " + contact.getName() + contact);
                                            comment.setWatchName(contact.getName());
                                            comment.setWatchIcon(contact.getPhotoPath());
                                        }
                                    }
                                    comment.setReplyId(gift.getReplyId());
                                    comment.setCreateTime(gift.getCreateTime());
                                    comment.setCommentFlag(false);
                                    comment.setGiftType(gift.getGift());
                                    LogUtil.w("AbsTask", "call: addToComment : " + comment);
                                    comments.add(comment);
                                }
                            }
                            Collections.sort(comments, new Comparator<DbMomentComment>() {
                                @Override
                                public int compare(DbMomentComment left, DbMomentComment right) {
                                    if (right.getCreateTime().longValue() < left.getCreateTime().longValue()) {
                                        return -1;
                                    }
                                    return right.getCreateTime().longValue() > left.getCreateTime().longValue() ? 1 : 0;
                                }
                            });
                            DbMoment moment = requestValues.getMoment();
                            moment.setComments(comments);
                            requestValues.setMoment(moment);
                            for (int i = 0; i < comments.size(); i++) {
                                LogUtil.d("AbsTask", "call: sort " + comments.get(i));
                            }
                        }
                        LogUtil.d("AbsTask", "call: " + searchGiftResponse);
                        return searchGiftResponse;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SearchGiftResponse>() {
                    @Override
                    public void call(SearchGiftResponse searchGiftResponse) {
                        TaskCallback<AbsTask.ResponseValue> callback = SearchGiftTask.this.getTaskCallback();
                        if (searchGiftResponse != null) {
                            ResponseValue responseValue = new ResponseValue();
                            responseValue.setMomentComments(requestValues.getMoment().getComments());
                            responseValue.setSearchGiftResponse(searchGiftResponse);
                            callback.uiSuccess(responseValue);
                            return;
                        }
                        callback.uiError();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e("AbsTask", "searchGift executeTask call: ", throwable);
                        SearchGiftTask.this.getTaskCallback().uiError();
                    }
                });
    }

    public static class RequestValues implements AbsTask.RequestValues {

        private DbMoment moment;
        private SearchGiftRequest request;

        public RequestValues(SearchGiftRequest request) {
            this.request = request;
        }

        public SearchGiftRequest getRequest() {
            return this.request;
        }

        public void setRequest(SearchGiftRequest request) {
            this.request = request;
        }

        public DbMoment getMoment() {
            return this.moment;
        }

        public void setMoment(DbMoment moment) {
            this.moment = moment;
        }
    }

    public static class ResponseValue implements AbsTask.ResponseValue {

        private List<DbMomentComment> momentComments;
        private SearchGiftResponse searchGiftResponse;

        public SearchGiftResponse getSearchGiftResponse() {
            return this.searchGiftResponse;
        }

        public void setSearchGiftResponse(SearchGiftResponse searchGiftResponse) {
            this.searchGiftResponse = searchGiftResponse;
        }

        public List<DbMomentComment> getMomentComments() {
            return this.momentComments;
        }

        public void setMomentComments(List<DbMomentComment> momentComments) {
            this.momentComments = momentComments;
        }
    }
}
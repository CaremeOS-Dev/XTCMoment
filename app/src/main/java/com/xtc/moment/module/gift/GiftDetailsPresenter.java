package com.xtc.moment.module.gift;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;
import com.xtc.log.LogUtil;
import com.xtc.moment.data.IMomentsDataSource;
import com.xtc.moment.data.MomentsRepository;
import com.xtc.moment.data.local.MomentsLocalDataSource;
import com.xtc.moment.data.remote.MomentsRemoteDataSource;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.net.bean.GiftDataBean;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.tasks.domain.usecase.SearchGiftTask;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物详情业务处理：查询某条动态下赠送礼物的好友。
 */
class GiftDetailsPresenter extends MvpBasePresenter<IGiftDetailsActivityView> {

    private static final String TAG = "GiftDetailsPresenter";

    private Context mContext;
    private IMomentsDataSource momentsDataSource;
    private final MomentsLocalDataSource momentsLocalDataSource;
    private final MomentsRemoteDataSource momentsRemoteDataSource;
    private final String watchId;

    public GiftDetailsPresenter(Context context) {
        this.mContext = context;
        this.momentsLocalDataSource = MomentsLocalDataSource.getInstance(context);
        this.momentsRemoteDataSource = MomentsRemoteDataSource.getInstance(context);
        this.momentsDataSource = MomentsRepository.getInstance(this.momentsRemoteDataSource,
                this.momentsLocalDataSource);
        this.watchId = AccountInfoServerImpl.getInstance(context).getWatchAccountInfo().getWatchId(context);
    }

    public void getGiftList(int giftType, final DbMoment moment) {
        SearchGiftRequest request = new SearchGiftRequest();
        request.setFriendId(this.watchId);
        request.setMomentId(moment.getMomentId());
        request.setWatchId(moment.getWatchId());
        request.setGift(giftType);
        SearchGiftTask searchGiftTask = new SearchGiftTask(this.momentsDataSource, this.mContext);
        SearchGiftTask.RequestValues requestValues = new SearchGiftTask.RequestValues(request);
        requestValues.setMoment(moment);
        searchGiftTask.setRequestValues(requestValues);
        searchGiftTask.setTaskCallback(new AbsTask.TaskCallback<AbsTask.ResponseValue>() {
            @Override
            protected void onSuccess(AbsTask.ResponseValue responseValue) {
                if (GiftDetailsPresenter.this.getView() == null) {
                    return;
                }
                SearchGiftTask.ResponseValue searchResponse = (SearchGiftTask.ResponseValue) responseValue;
                LogUtil.e(TAG, "onSuccess: " + searchResponse.getMomentComments());
                LogUtil.e(TAG, "onSuccess: " + searchResponse.getSearchGiftResponse());
                GiftDetailsPresenter.this.getView()
                        .getGiftDataSuccess(castGiftBeans(searchResponse.getMomentComments()));
            }

            @Override
            protected void onError(AbsTask.ResponseValue responseValue) {
                if (GiftDetailsPresenter.this.getView() == null) {
                    return;
                }
                GiftDetailsPresenter.this.getView().getGiftDataFail(castGiftBeans(moment.getComments()));
            }

            @Override
            protected void onError() {
                if (GiftDetailsPresenter.this.getView() == null) {
                    return;
                }
                GiftDetailsPresenter.this.getView().getGiftDataFail(castGiftBeans(moment.getComments()));
            }

            /** 把礼物评论转换成列表数据。 */
            private List<GiftDataBean> castGiftBeans(List<DbMomentComment> comments) {
                ArrayList<GiftDataBean> giftBeans = new ArrayList<>();
                if (CollectionUtil.isEmpty(comments)) {
                    return giftBeans;
                }
                for (DbMomentComment comment : comments) {
                    if (!comment.isCommentFlag()) {
                        GiftDataBean giftDataBean = new GiftDataBean();
                        giftDataBean.setGiftType(comment.getGiftType());
                        giftDataBean.setWatchIcon(comment.getWatchIcon());
                        giftDataBean.setWatchId(comment.getWatchId());
                        giftDataBean.setWatchName(comment.getWatchName());
                        giftBeans.add(giftDataBean);
                    }
                }
                return giftBeans;
            }
        });
        TaskDispatcher.dispatchImmediately(searchGiftTask);
    }
}
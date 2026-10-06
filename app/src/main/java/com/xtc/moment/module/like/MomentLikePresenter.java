package com.xtc.moment.module.like;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpBasePresenter;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.net.bean.MomentLikeVo;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.BeanConverterUtil;
import com.xtc.moment.util.Utils;
import com.xtc.utils.common.CollectionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * 点赞列表业务处理：拉取点赞记录并写入本地库。
 */
public class MomentLikePresenter extends MvpBasePresenter<IMomentLikeView> {

    private static final String TAG = "MomentLikePresenter";

    private Context mContext;
    private final IMomentServe mServe;

    public MomentLikePresenter(Context context) {
        this.mContext = context;
        this.mServe = MomentServeImpl.getInstance(context);
    }

    public void loadMomentLike(final List<String> momentIds, String watchId) {
        this.mServe.getPraiseRecord(momentIds, watchId)
                .map(new Func1<PraiseResponse, Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public Map<String, List<DbLikeMessage>> call(PraiseResponse praiseResponse) {
                        if (!CollectionUtil.isEmpty(momentIds)) {
                            MomentLikePresenter.this.mServe.deleteLikeMessageByMomentId(momentIds.get(0));
                        }
                        Map<String, List<MomentLikeVo>> momentLikeMap = praiseResponse.getMomentLikeMap();
                        HashMap<String, List<DbLikeMessage>> resultMap = new HashMap<>();
                        if (momentLikeMap != null) {
                            for (Map.Entry<String, List<MomentLikeVo>> entry : momentLikeMap.entrySet()) {
                                List<DbLikeMessage> likeMessages =
                                        BeanConverterUtil.convertToDbLikeMessage(entry.getValue());
                                Utils.logSize(TAG, "likeMessageList size", likeMessages);
                                MomentLikePresenter.this.mServe.addLikeMessage(likeMessages);
                                resultMap.put(entry.getKey(), likeMessages);
                            }
                        }
                        return resultMap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, List<DbLikeMessage>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (!MomentLikePresenter.this.isViewAttached()
                                || MomentLikePresenter.this.getView() == null) {
                            return;
                        }
                        LogUtil.i(TAG, "loadMomentLike#onError: " + throwable.getMessage());
                        MomentLikePresenter.this.getView().showGetMomentLikesError();
                    }

                    @Override
                    public void onNext(Map<String, List<DbLikeMessage>> likeMessageMap) {
                        if (!MomentLikePresenter.this.isViewAttached()
                                || MomentLikePresenter.this.getView() == null) {
                            return;
                        }
                        MomentLikePresenter.this.getView().showMomentLikes(likeMessageMap);
                    }
                });
    }

    public List<Friend> getAllFriend() {
        return FriendInfoServeImpl.getInstance(this.mContext).getAllFriendInfo();
    }

    public List<DbLikeMessage> getLikeMessageFromDb(String momentId) {
        return this.mServe.getLikeMessageByMomentId(momentId);
    }

    public String getDefault() {
        return FriendInfoServeImpl.getInstance(this.mContext).getDefaultHead();
    }
}
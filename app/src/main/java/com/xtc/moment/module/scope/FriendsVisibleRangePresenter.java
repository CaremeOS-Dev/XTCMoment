package com.xtc.moment.module.scope;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbVisible;
import com.xtc.moment.module.bean.Friend;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.FriendsVisibleBeanReq;
import com.xtc.moment.module.publish.visible.VisibleTypeActivity;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.util.ContactUtil;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * 好友可见范围业务处理：优先取服务端，失败时回退到本地记录。
 */
public class FriendsVisibleRangePresenter extends MvpPresenter<IFriendsVisibleRangeView> {

    private static final String TAG = "FriendsVisibleRangePresenter";

    private Context context;
    private IMomentServe iMomentServe;
    private MomentHttpServiceProxy momentHttpServiceProxy;
    private ContactManager contactManager;
    private volatile FriendsVisibleBean momentVisibleBean = new FriendsVisibleBean();

    public FriendsVisibleRangePresenter(Context context) {
        this.context = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.momentHttpServiceProxy = (MomentHttpServiceProxy) ServerCache
                .getHttpService(context, MomentHttpServiceProxy.class);
        this.contactManager = ContactManager.getInstance(context);
    }

    public void getAllVisibleFriends(final String momentId) {
        FriendsVisibleBeanReq request = new FriendsVisibleBeanReq();
        ArrayList<String> momentIds = new ArrayList<>();
        momentIds.add(momentId);
        request.setMomentIds(momentIds);
        this.momentHttpServiceProxy.getLookUpFriends(request)
                .subscribe(new Subscriber<List<FriendsVisibleBean>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getAllVisibleFriends error ", throwable);
                        FriendsVisibleRangePresenter.this.showDbLookUps(momentId, throwable.getMessage());
                    }

                    @Override
                    public void onNext(List<FriendsVisibleBean> visibleBeans) {
                        if (CollectionUtil.isEmpty(visibleBeans) || visibleBeans.get(0) == null) {
                            FriendsVisibleRangePresenter.this.showDbLookUps(momentId, null);
                            return;
                        }
                        FriendsVisibleRangePresenter.this.setMomentVisibleBean(visibleBeans.get(0));
                        if (FriendsVisibleRangePresenter.this.getView() != null) {
                            FriendsVisibleRangePresenter.this.getView()
                                    .showPermissons(FriendsVisibleRangePresenter.this.momentVisibleBean, null);
                        }
                        FriendsVisibleRangePresenter.this.iMomentServe
                                .saveVisibleRecord(FriendsVisibleRangePresenter.this.momentVisibleBean);
                    }
                });
    }

    private void showDbLookUps(String momentId, String errorMessage) {
        List<DbVisible> visibleFriends = this.iMomentServe.getVisibleFriendsFromDb(momentId);
        if (CollectionUtil.isEmpty(visibleFriends)) {
            if (getView() != null) {
                getView().showPermissons(null, errorMessage);
            }
            return;
        }
        int permissionType = visibleFriends.get(0).getPermissionType().intValue();
        ArrayList<String> watchIds = new ArrayList<>();
        for (DbVisible visible : visibleFriends) {
            if (visible != null) {
                visible.setPermissionType(Integer.valueOf(permissionType));
                String watchId = visible.getWatchId();
                if (TextUtils.isEmpty(watchId)) {
                    break;
                }
                watchIds.add(watchId);
            }
        }
        this.momentVisibleBean.setType(permissionType);
        this.momentVisibleBean.setMomentId(momentId);
        this.momentVisibleBean.setFriends(watchIds);
        if (getView() != null) {
            getView().showPermissons(this.momentVisibleBean, errorMessage);
        }
    }

    public List<Friend> getFriends(List<String> watchIds) {
        ArrayList<Friend> friends = new ArrayList<>();
        for (String watchId : watchIds) {
            if (!TextUtils.isEmpty(watchId)) {
                friends.add(ContactUtil.convertToFriend(this.contactManager
                        .getContactWithoutShortNumberByWatchIdSync(watchId)));
            }
        }
        return friends;
    }

    public void resetVisibleRange() {
        VisibleTypeActivity.startForResult((Activity) this.context, this.momentVisibleBean, 2);
    }

    public FriendsVisibleBean getMomentVisibleBean() {
        return this.momentVisibleBean;
    }

    public void setMomentVisibleBean(FriendsVisibleBean momentVisibleBean) {
        this.momentVisibleBean = momentVisibleBean;
    }
}
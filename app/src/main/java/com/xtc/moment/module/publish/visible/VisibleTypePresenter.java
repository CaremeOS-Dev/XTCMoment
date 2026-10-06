package com.xtc.moment.module.publish.visible;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.third.behavior.MomentBehavior;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.ToastUtil;
import com.xtc.utils.common.CollectionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import rx.Subscriber;

/**
 * Presenter of the visible range page: keeps a local cache of the picked friend lists and pushes
 * the new range to the server.
 */
public class VisibleTypePresenter extends MvpPresenter<IVisibleTypeView> {

    private static final String TAG = "VisibleTypePresenter";

    /** Error code returned when the visible range was changed too often. */
    private static final String CHANGE_LIMIT = "000060";
    /** Error code returned when the user has no permission to change the range. */
    private static final String CODE_FREQUENT_REQUEST = "1003";
    /** Error code returned when the request could not reach the server. */
    private static final String CODE_NETWORK_EXCEPTION = "1005";

    private final Context context;
    private final IMomentServe iMomentServe;
    private final MomentHttpServiceProxy momentHttpServiceProxy;
    /** Last picked friend list per visible range type. */
    private final HashMap<Integer, List<String>> localMap = new HashMap<Integer, List<String>>();

    public VisibleTypePresenter(Context context) {
        this.context = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
        this.momentHttpServiceProxy = (MomentHttpServiceProxy) ServerCache.getHttpService(context,
                MomentHttpServiceProxy.class);
    }

    public void recordAdd(FriendsVisibleBean visibleBean) {
        this.localMap.put(Integer.valueOf(visibleBean.getType()), visibleBean.getFriends());
    }

    public List<String> getRecord(int type) {
        return this.localMap.get(Integer.valueOf(type));
    }

    /** Sends the new visible range and updates the cached moment on success. */
    public void changeRange(final FriendsVisibleBean visibleBean) {
        this.momentHttpServiceProxy.permissionUpdate(visibleBean).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(TAG, "getAllVisibleFriends error ", throwable);
                if (throwable.getMessage().contains(CHANGE_LIMIT)) {
                    ToastUtil.showShort(context, context.getString(R.string.change_visible_limit));
                } else if (throwable.getMessage().contains(CODE_FREQUENT_REQUEST)) {
                    ToastUtil.showShort(context, context.getString(R.string.frequent_request));
                } else if (throwable.getMessage().contains(CODE_NETWORK_EXCEPTION)) {
                    ToastUtil.showShort(context, context.getString(R.string.net_work_exception));
                } else {
                    ToastUtil.showShort(context, context.getString(R.string.change_defate));
                }
                if (getView() != null) {
                    getView().changeResult(false);
                }
            }

            @Override
            public void onNext(String result) {
                if (getView() != null) {
                    getView().changeResult(true);
                }
                List<DbMoment> moments = iMomentServe.getMomentById(visibleBean.getMomentId());
                if (!CollectionUtil.isEmpty(moments) && moments.get(0) != null) {
                    DbMoment moment = moments.get(0);
                    if (visibleBean != null) {
                        moment.setPermissionType(visibleBean.getType());
                    }
                    EventBus.getDefault().post(new EventData(18, moment));
                    iMomentServe.updateMoment(moment);
                }
                MomentBehavior.upLoadVisibleRange(context, 2, visibleBean.getType());
                iMomentServe.saveVisibleRecord(visibleBean);
            }
        });
    }
}
package com.xtc.moment.module.publish.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.event.IMMomentMsgData;
import com.xtc.moment.module.bean.NearPois;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.widget.LoadingViewHolder;
import com.xtc.moment.util.ToastUtil;
import com.xtc.system.location.LocationClient;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * POI picker shown when the user wants to attach a precise location to a moment.
 */
public class PublishPoiActivity extends Activity implements View.OnClickListener {

    public static final String EXTRA_POI = "poi";

    private static final String TAG = "PublishLBSActivity";
    /** Request code used when the location page opens this picker. */
    private static final int REQUEST_CODE = 5;
    /** Location callback type reported by the location service. */
    private static final int MSG_TYPE_LOCATION = 11009;
    /** Milliseconds before the location lookup is considered failed. */
    private static final long LOCATION_TIMEOUT_MS = 20000L;
    /** Lifetime of the cached POI list, in minutes. */
    private static final long POI_CACHE_MINUTES = 1L;

    private static List<PoiBean> historyPois;
    private static long expiredTime;

    private final Handler handler = new Handler();

    private RecyclerView rvPoi;
    private PoiAdapter poiAdapter;
    private Button btnConfirm;
    private LoadingViewHolder loadingViewHolder;
    private boolean hasInit;

    /** Opens the POI picker and returns the picked POI as an activity result. */
    public static void startForResult(Activity activity, PoiBean poiBean) {
        Intent intent = new Intent(activity, PublishPoiActivity.class);
        intent.putExtra(EXTRA_POI, poiBean);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_poi);
        this.rvPoi = (RecyclerView) findViewById(R.id.rv_poi);
        RecyclerView.ItemAnimator itemAnimator = this.rvPoi.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        this.rvPoi.setLayoutManager(new LinearLayoutManager(this));
        this.btnConfirm = (Button) findViewById(R.id.btn_confirm);
        this.btnConfirm.setOnClickListener(this);
        this.loadingViewHolder = new LoadingViewHolder(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.d(TAG, "onWindowFocusChanged() called with: hasFocus = [" + hasFocus
                + "] , hasInit = " + this.hasInit);
        if (!hasFocus || this.hasInit) {
            return;
        }
        this.hasInit = true;
        initData();
    }

    private void initData() {
        EventBus.getDefault().register(this);
        this.poiAdapter = new PoiAdapter(this, (PoiBean) getIntent().getParcelableExtra(EXTRA_POI));
        this.rvPoi.setAdapter(this.poiAdapter);
        if (!CollectionUtil.isEmpty(historyPois) && SystemClock.elapsedRealtime() <= expiredTime) {
            onSuccess(historyPois);
            return;
        }
        String watchId = MomentApp.getWatchId();
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.e(TAG, "initData: watchId is null!");
            showLocationError();
            return;
        }
        this.loadingViewHolder.showLoading(getWindow().getDecorView());
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "超时了");
                showLocationError();
            }
        }, LOCATION_TIMEOUT_MS);
        triggerLocation(watchId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMMomentMsgData msgData) {
        int type = msgData.getType();
        LogUtil.i(TAG, "Location callback, type = " + type);
        if (type != MSG_TYPE_LOCATION) {
            return;
        }
        refreshPoiView(msgData.getContent());
    }

    private void refreshPoiView(final String content) {
        Observable.just(content)
                .map(new Func1<String, NearPois>() {
                    @Override
                    public NearPois call(String json) {
                        return (NearPois) JSONUtil.fromJSON(content, NearPois.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<NearPois>() {
                    @Override
                    public void call(NearPois nearPois) {
                        if (nearPois == null) {
                            LogUtil.i(TAG, "refreshPoiView, nearPois is null");
                            return;
                        }
                        List<PoiBean> pois = nearPois.getNearPois();
                        if (!pois.isEmpty()) {
                            historyPois = pois;
                            expiredTime = SystemClock.elapsedRealtime()
                                    + TimeUnit.MINUTES.toMillis(POI_CACHE_MINUTES);
                        }
                        onSuccess(pois);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "refreshPoiView: ", throwable);
                    }
                });
    }

    private void onSuccess(List<PoiBean> pois) {
        this.handler.removeCallbacksAndMessages(null);
        if (CollectionUtil.isEmpty(pois)) {
            showLocationError();
            return;
        }
        LogUtil.d(TAG, "data size = " + pois.size());
        this.poiAdapter.setDataItem(pois);
        this.loadingViewHolder.dismissLoading();
        this.btnConfirm.setVisibility(View.VISIBLE);
    }

    private void triggerLocation(String watchId) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
            this.loadingViewHolder.dismissLoading();
            this.handler.removeCallbacksAndMessages(null);
            return;
        }
        if (LocationClient.requestLocation(this, watchId, 116)) {
            LogUtil.i(TAG, "triggerLocation successfully.");
            return;
        }
        LogUtil.w(TAG, "triggerLocation failed");
        this.handler.removeCallbacksAndMessages(null);
        showLocationError();
    }

    private void showLocationError() {
        this.loadingViewHolder.dismissLoading();
        ToastUtil.showShortCover(this, R.string.locate_failed);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() != R.id.btn_confirm) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_POI, this.poiAdapter.getCheckedPoi());
        setResult(RESULT_OK, intent);
        finish();
    }
}
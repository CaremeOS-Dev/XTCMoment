package com.xtc.moment.module.publish.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.architecture.mvp.PermissionListener;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.behavior.DigitalManager;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.event.IMMomentMsgData;
import com.xtc.moment.module.bean.NearPois;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.text.PushTextActivity;
import com.xtc.moment.module.widget.LoadingPupWindowHolder;
import com.xtc.moment.module.widget.LoadingViewHolder;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.PublishErrorUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.moment.util.ToastUtil;
import com.xtc.system.location.LocationClient;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Publish entry that resolves the current location and then continues to the text publish page.
 */
public class PublishLocationActivity extends BaseActivity<IPublishLocationView, PublishLocationPresenter>
        implements IPublishLocationView {

    private static final String TAG = "XTC_MOMENT_PublishLocationActivity";

    /** Request code used when this page opens the POI picker. */
    private static final int REQUEST_CODE = 5;
    /** Location callback type reported by the location service. */
    private static final int MSG_TYPE_LOCATION = 11009;
    /** Milliseconds before the location lookup is considered failed. */
    private static final long LOCATION_TIMEOUT_MS = 20000L;
    /** Delay before the "first location publish" hint is shown, in milliseconds. */
    private static final long FIRST_LBS_TIP_DELAY_MS = 1500L;
    /** Location request type sent to the location client. */
    private static final int LOCATION_REQUEST_TYPE = 116;

    private RelativeLayout mRlAll;
    private RecyclerView mRv;
    private LocationAdapter locationAdapter;
    private LongSolidButton certenBtn;
    private LoadingPupWindowHolder loadingPupWindowHolder;
    private LoadingViewHolder loadingViewHolder;
    private Handler handler;
    private boolean init;
    private boolean locateSuccessful;

    @Override
    public PublishLocationPresenter createPresenter() {
        return new PublishLocationPresenter(this);
    }

    /** Opens the location flow; {@code type} 2 opens the full location page, otherwise the POI list. */
    public static void startForResult(Activity activity, int type) {
        Intent intent;
        if (type == 2) {
            intent = new Intent(activity, PublishLocationActivity.class);
        } else {
            intent = new Intent(activity, PublishPoiActivity.class);
        }
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_publish_location);
        getWindow().setBackgroundDrawable(null);
        EventBus.getDefault().register(this);
        this.handler = new Handler();
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus || this.init) {
            return;
        }
        this.init = true;
        initData();
    }

    @Override
    public void initView() {
        this.mRlAll = (RelativeLayout) findViewById(R.id.layout);
        this.loadingPupWindowHolder = new LoadingPupWindowHolder(this);
        this.loadingPupWindowHolder.setOnSuccessAction(new Runnable() {
            @Override
            public void run() {
                setResult(RESULT_OK);
                finish();
            }
        });
        this.loadingViewHolder = new LoadingViewHolder(this);
        this.mRv = (RecyclerView) findViewById(R.id.rv);
        this.mRv.setLayoutManager(new LinearLayoutManager(this));
        this.locationAdapter = new LocationAdapter(this);
        this.mRv.setAdapter(this.locationAdapter);
        this.certenBtn = (LongSolidButton) findViewById(R.id.certen_btn);
        this.certenBtn.getTv().setText(R.string.certen_btn_text);
        this.certenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DigitalManager.getInstance().clearDigitalEntity();
                DigitalManager.getInstance().getDigitalEntity().startPushTime = SystemClock.elapsedRealtime();
                PoiBean poiBean = locationAdapter.getPublishContent();
                if (!TextUtils.isEmpty(poiBean.getAddressDesc())) {
                    if (NetworkUtils.isNetworkAvailable(PublishLocationActivity.this)) {
                        jumpToPublicActivity(poiBean);
                        DigitalManager.getInstance().getDigitalEntity().momentContent = poiBean.getAddressDesc();
                    } else {
                        ToastUtil.showShortCover(PublishLocationActivity.this,
                                getString(R.string.net_work_exception));
                    }
                } else {
                    ToastUtil.showShortCover(PublishLocationActivity.this,
                            getString(R.string.content_is_null));
                }
            }
        });
    }

    @Override
    public void initData() {
        final String watchId = MomentApp.getWatchId();
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.e(TAG, "initData: watchId is null!");
            showLocationError();
            return;
        }
        requestRunTimePermission(PermissionStringUtils.LOCATION_PERMISSIONS, new PermissionListener() {
            @Override
            public void onGranted() {
                LogUtil.i(TAG, "onGranted");
                SharedTool.saveLocationPermission(PublishLocationActivity.this, true);
                loadingViewHolder.showLoading(getWindow().getDecorView());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLocationError();
                    }
                }, LOCATION_TIMEOUT_MS);
                triggerLocation(watchId);
            }

            @Override
            public void onPartPermissionDenied(List<String> granted, List<String> denied) {
                LogUtil.d(TAG, "onPartPermissionDenied: requestPermission");
                SharedTool.saveLocationPermission(PublishLocationActivity.this, false);
                finish();
            }
        });
    }

    private void jumpToPublicActivity(final PoiBean poiBean) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PublishLocationActivity.this, PushTextActivity.class);
                intent.putExtra(PublishPoiActivity.EXTRA_POI, poiBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void showLocationError() {
        this.loadingViewHolder.dismissLoading();
        if (this.locateSuccessful) {
            return;
        }
        ToastUtil.showShortCover(this, R.string.locate_failed);
    }

    private void triggerLocation(String watchId) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShortCover(this, getString(R.string.net_work_exception));
            this.loadingViewHolder.dismissLoading();
            this.handler.removeCallbacksAndMessages(null);
            return;
        }
        if (LocationClient.requestLocation(this, watchId, LOCATION_REQUEST_TYPE)) {
            LogUtil.i(TAG, "triggerLocation successfully.");
            return;
        }
        LogUtil.w(TAG, "triggerLocation failed");
        this.handler.removeCallbacksAndMessages(null);
        showLocationError();
    }

    @Override
    public void publishSuccess(DbMoment moment) {
        if (!TextUtils.isEmpty(moment.getLocation()) && !SaveDynamic.hasFirstPublished(this)) {
            SaveDynamic.saveFirstPublished(this);
            HandlerUtil.runOnUIThreadDelay(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PublishLocationActivity.this,
                            getString(R.string.first_lbs_tip), Toast.LENGTH_SHORT).show();
                }
            }, FIRST_LBS_TIP_DELAY_MS);
        }
        this.loadingPupWindowHolder.showSuccess();
        this.mRlAll.setVisibility(View.GONE);
        EventBus.getDefault().post(moment);
        EventBus.getDefault().post(new EventData(5, moment));
    }

    @Override
    public void publishFail(String message) {
        this.loadingPupWindowHolder.dismissLoading();
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtil.showShortCover(this, R.string.net_work_exception);
        } else {
            PublishErrorUtil.showFailMessage(this, message);
        }
    }

    @Override
    public void publishLimited() {
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_limit));
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(IMMomentMsgData msgData) {
        if (msgData.getType() != MSG_TYPE_LOCATION) {
            return;
        }
        NearPois nearPois = (NearPois) JSONUtil.fromJSON(msgData.getContent(), NearPois.class);
        if (nearPois == null) {
            return;
        }
        final List<PoiBean> pois = nearPois.getNearPois();
        if (pois.isEmpty()) {
            return;
        }
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                refreshLocationInfo(pois.get(0));
            }
        });
    }

    private void refreshLocationInfo(PoiBean poiBean) {
        this.locateSuccessful = true;
        this.loadingViewHolder.dismissLoading();
        this.handler.removeCallbacksAndMessages(null);
        this.locationAdapter.addDataItem(poiBean);
        this.certenBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void publishInvalidate() {
        this.loadingPupWindowHolder.dismissLoading();
        ToastUtil.showShortCover(this, getString(R.string.publish_sensitive));
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        this.loadingViewHolder.dismissLoading();
        this.loadingPupWindowHolder.dismissLoading();
        this.handler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
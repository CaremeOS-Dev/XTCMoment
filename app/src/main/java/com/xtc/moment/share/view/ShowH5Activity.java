package com.xtc.moment.share.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.Constants;
import com.xtc.moment.receiver.StartWebReceiver;
import com.xtc.moment.util.LaunchWebViewUtil;
import com.xtc.shareapi.share.bean.SerializableMap;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.web.client.WebClientManager;
import com.xtc.web.core.WebManager;
import com.xtc.web.core.manager.LifecycleDispatcher;

import java.util.HashMap;
import java.util.List;

/**
 * 展示 H5 页面的 Activity，支持按 url 或按 webType 请求。
 */
public class ShowH5Activity extends Activity {

    private static final String CLASS_NAME_SHARE_WEB_ACTIVITY = "com.xtc.moment.share.view.ShowH5Activity";
    public static final String H5_URL = "h5_url";
    private static final String PARAMS_MAP = "params_map";
    private static final String REQUEST_WEB_TYPE = "request_web_type";
    private static final String TAG = "ShowH5Activity";

    private AnimationDrawable animDrawable;
    private boolean isKillProcess = LaunchWebViewUtil.isAutoKillWebProcess();
    private WebClientManager mWebClientManager;
    private WebManager mWebManager;
    private TextView tvLoading;

    /** 启动 H5 页面。 */
    public static void start(Context context, int webType, HashMap<String, String> params) {
        Intent intent;
        if (LaunchWebViewUtil.isLaunchMultiProcess()) {
            intent = new Intent(context, ShowH5Activity.class);
        } else {
            intent = new Intent(context, SinglePShowH5Activity.class);
        }
        intent.putExtra(REQUEST_WEB_TYPE, webType);
        if (params != null) {
            SerializableMap serializableMap = new SerializableMap();
            serializableMap.setMap(params);
            intent.putExtra(PARAMS_MAP, serializableMap);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_h5);
        ImageView loadingView = (ImageView) findViewById(R.id.iv_loading_view);
        this.tvLoading = (TextView) findViewById(R.id.tv_loading);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rl_webview);
        this.animDrawable = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.7f, 100);
        loadingView.setBackground(this.animDrawable);
        this.animDrawable.start();
        String url = getIntent().getStringExtra(H5_URL);
        this.mWebManager = WebManager.getInstance(this, rootView);
        if (url != null) {
            this.mWebClientManager = new WebClientManager(this.mWebManager, getApplicationContext(),
                    Constants.DATABASE_NAME);
            this.mWebClientManager.requestFromUrl(url);
            this.mWebManager.setLoadSuccessListener(new WebManager.LoadSuccessListener() {
                @Override
                public void onLoadSuccess(String loadedUrl) {
                    LogUtil.e(TAG, "onLoadSuccess " + loadedUrl);
                    if (animDrawable != null) {
                        animDrawable.stop();
                        tvLoading.setVisibility(View.GONE);
                    }
                }
            });
            this.mWebManager.setLoadFailListener(new WebManager.LoadFailListener() {
                @Override
                public void onLoadFail(String failedUrl) {
                    LogUtil.e(TAG, "onLoadFail " + failedUrl);
                    if (animDrawable != null) {
                        animDrawable.stop();
                        tvLoading.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Intent intent = getIntent();
            int webType = intent.getIntExtra(REQUEST_WEB_TYPE, -1);
            if (webType == -1) {
                return;
            }
            HashMap<String, String> params = intent.hasExtra(PARAMS_MAP)
                    ? ((SerializableMap) intent.getParcelableExtra(PARAMS_MAP)).getMap() : null;
            LogUtil.d(TAG, "webType = " + webType + ", params = " + params);
            this.mWebClientManager = new WebClientManager(this.mWebManager, getApplicationContext(),
                    Constants.DATABASE_NAME);
            if (params == null) {
                this.mWebClientManager.requestFromType(webType);
            } else {
                this.mWebClientManager.requestFromType(webType, params);
            }
        }
        LifecycleDispatcher.getInstance().disPatchOnCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction(StartWebReceiver.ACTION_FLAG_START_WEB_PROCESS);
        sendBroadcast(intent);
        LifecycleDispatcher.getInstance().disPatchOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            release();
        }
    }

    public void setKillProcess(boolean killProcess) {
        this.isKillProcess = killProcess;
    }

    @Override
    protected void onDestroy() {
        release();
        LifecycleDispatcher.getInstance().dispatchOnDestroy();
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = getRunningTaskInfos(this);
        if (!CollectionUtil.isEmpty(runningTaskInfos)) {
            ComponentName topActivity = runningTaskInfos.get(0).topActivity;
            if (topActivity != null && CLASS_NAME_SHARE_WEB_ACTIVITY.equals(topActivity.getClassName())) {
                super.onDestroy();
                return;
            }
        }
        if (this.isKillProcess) {
            Process.killProcess(Process.myPid());
        } else {
            LogUtil.i(TAG, "don't need kill process");
        }
        super.onDestroy();
    }

    private static List<ActivityManager.RunningTaskInfo> getRunningTaskInfos(Context context) {
        return ((ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningTasks(1);
    }

    private void release() {
        if (this.mWebClientManager != null) {
            LogUtil.i(TAG, "mWebClientManager release");
            this.mWebClientManager.release();
            this.mWebClientManager = null;
        } else if (this.mWebManager != null) {
            LogUtil.i(TAG, "mWebManager release");
            this.mWebManager.release();
            this.mWebManager = null;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            if (!"Can not perform this action after onSaveInstanceState".equals(e.getMessage())) {
                throw e;
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LifecycleDispatcher.getInstance().dispatchActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LifecycleDispatcher.getInstance().dispatchOnNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LifecycleDispatcher.getInstance().dispatchOnStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LifecycleDispatcher.getInstance().dispatchOnStart();
    }
}

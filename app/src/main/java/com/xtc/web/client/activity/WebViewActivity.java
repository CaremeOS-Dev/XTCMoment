package com.xtc.web.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.xtc.log.LogUtil;
import com.xtc.web.client.R;
import com.xtc.web.client.WebClientManager;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.loading.SmallLoadingView;
import com.xtc.web.core.WebManager;
import com.xtc.web.core.manager.LifecycleDispatcher;

/** 通用的 H5 容器页面。 */
public class WebViewActivity extends Activity {

    private static final String TAG = Constants.TAG + WebViewActivity.class.getSimpleName();
    public static long startTime;

    private String packageName;
    private RelativeLayout rlRoot;
    private RelativeLayout rlWebView;
    private String url;
    private WebClientManager webClientManager;
    private WebManager webManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        this.url = getIntent().getStringExtra("web_url");
        this.packageName = getIntent().getStringExtra(Constants.PACKAGE_NAME);
        Log.d(TAG, "web url = " + this.url + ",packageName:" + this.packageName);
        this.rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        this.rlWebView = (RelativeLayout) findViewById(R.id.rl_webView);
        this.webManager = WebManager.getInstance(this, this.rlWebView);
        this.webManager.setLoadingView(new SmallLoadingView(this));
        this.webClientManager = new WebClientManager(this.webManager, this, "demo.db");
        this.webClientManager.requestFromUrl(this.url);
        LifecycleDispatcher.getInstance().disPatchOnCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LifecycleDispatcher.getInstance().dispatchActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.i(TAG, "WebviewActivity onStart");
        LifecycleDispatcher.getInstance().dispatchOnStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "WebviewActivity onResume");
        Intent intent = new Intent(Constants.WEB_APP_ACTION);
        intent.putExtra(Constants.PACKAGE_NAME, this.packageName);
        intent.putExtra(Constants.TYPE, 1);
        sendBroadcast(intent);
        LifecycleDispatcher.getInstance().disPatchOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "WebviewActivity onPause");
        Intent intent = new Intent(Constants.WEB_APP_ACTION);
        intent.putExtra(Constants.PACKAGE_NAME, this.packageName);
        intent.putExtra(Constants.TYPE, 2);
        sendBroadcast(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LifecycleDispatcher.getInstance().dispatchOnNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "WebviewActivity onStop");
        LifecycleDispatcher.getInstance().dispatchOnStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "WebviewActivity onDestroy");
        Intent intent = new Intent(Constants.WEB_APP_ACTION);
        intent.putExtra(Constants.PACKAGE_NAME, this.packageName);
        intent.putExtra(Constants.TYPE, 6);
        sendBroadcast(intent);
        LifecycleDispatcher.getInstance().dispatchOnDestroy();
        WebClientManager manager = this.webClientManager;
        if (manager != null) {
            manager.release();
        }
    }
}
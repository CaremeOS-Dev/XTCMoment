package com.xtc.web.core.jump;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.web.core.CoreConstants;
import com.xtc.moment.R;
import com.xtc.web.core.provider.JumpContentProvider;

import java.net.URISyntaxException;

/** 透明的中转 Activity：替 H5 拉起需要 onActivityResult 的第三方页面，并把结果通过 ContentProvider 回传。 */
public class JumpManager extends FragmentActivity {

    private static final String TAG = "XTC";
    private static Bundle data;
    boolean isStart;
    private int requestCode;
    private String requestKey;

    public static Bundle getData() {
        return data;
    }

    private static void setData(Bundle bundle) {
        data = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_empty);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getWindow().getDecorView().setBackgroundColor(0);
        ((ViewGroup) getWindow().getDecorView()).removeAllViews();
    }

    @Override
    protected void onResume() {
        Intent targetIntent;
        super.onResume();
        if (this.isStart) {
            return;
        }
        String intentUri = getIntent().getStringExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT);
        this.requestKey = getIntent().getStringExtra(CoreConstants.TakePhotoConstant.REQUEST_KEY);
        this.requestCode = getIntent().getIntExtra("requestCode", 100);
        if (TextUtils.isEmpty(this.requestKey)) {
            return;
        }
        try {
            targetIntent = Intent.parseUri(intentUri, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            LogUtil.e(TAG, e);
            targetIntent = null;
        }
        if (targetIntent == null) {
            LogUtil.d(TAG, "failed：targetIntent is null");
            getContentResolver().notifyChange(Uri.parse(Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER
                    + JumpContentProvider.getFileProviderName(this) + "/jumpActivityResult"), null);
            return;
        }
        startActivityForResult(targetIntent, this.requestCode);
        this.isStart = true;
    }

    /** 启动中转页面。 */
    public static void start(Context context, Intent intent, String requestKey, int requestCode) {
        String intentUri = intent.toUri(Intent.URI_INTENT_SCHEME);
        Intent jumpIntent = new Intent(context, JumpManager.class);
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.TARGET_INTENT, intentUri);
        jumpIntent.putExtra(CoreConstants.TakePhotoConstant.REQUEST_KEY, requestKey);
        jumpIntent.putExtra("requestCode", requestCode);
        context.startActivity(jumpIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        if (data != null) {
            Uri uri = Uri.parse(Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER
                    + JumpContentProvider.getFileProviderName(this) + "/jumpActivityResult");
            setData(data.getExtras());
            LogUtil.d(TAG, "intent bundle result");
            getContentResolver().notifyChange(uri, null);
        }
    }
}
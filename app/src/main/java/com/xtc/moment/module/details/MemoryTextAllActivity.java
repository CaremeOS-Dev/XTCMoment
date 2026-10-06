package com.xtc.moment.module.details;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;

import java.util.concurrent.Callable;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 展示完整文本内容的页面（文本过长时从列表跳入）。
 */
public class MemoryTextAllActivity extends Activity {

    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_ICON = "extra_icon";
    public static final String EXTRA_NAME = "extra_name";

    private static final String TAG = "MemoryTextAllActivity";

    public ImageView mIcon;
    private TextView mTvName;
    private TextView tvContent;

    private ContactManager contactManager;
    private String iconPath;
    private String accountName;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_all);
        this.contactManager = ContactManager.getInstance(this);
        initView();
        initData();
    }

    private void initView() {
        this.mIcon = (ImageView) findViewById(R.id.iv_account_icon);
        this.mTvName = (TextView) findViewById(R.id.tv_account_name);
        this.tvContent = (TextView) findViewById(R.id.tv_content);
    }

    private void initData() {
        AITextRxUtils.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return MemoryTextAllActivity.this.parseIntent();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean success) {
                        if (!success) {
                            LogUtil.d(TAG, "call: parse intent error ...");
                            MemoryTextAllActivity.this.finish();
                        }
                        MemoryTextAllActivity.this.setHead();
                        MemoryTextAllActivity.this.setName();
                        MemoryTextAllActivity.this.setContent();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        this.iconPath = intent.getStringExtra(EXTRA_ICON);
        this.accountName = intent.getStringExtra(EXTRA_NAME);
        this.content = intent.getStringExtra(EXTRA_CONTENT);
        return true;
    }

    private void setName() {
        this.mTvName.setText(this.accountName);
    }

    private void setContent() {
        this.tvContent.setText(this.content);
    }

    private void setHead() {
        Glide.with(this)
                .load(TextUtils.isEmpty(this.iconPath)
                        ? this.contactManager.getDefaultPortraitPath(this) : this.iconPath)
                .apply(new RequestOptions().circleCrop()
                        .placeholder(R.drawable.default_custom_default))
                .into(this.mIcon);
    }
}
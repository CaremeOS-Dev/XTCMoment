package com.xtc.moment.module.like.likerule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.personalinfo.bigdata.PersonalCenterBehavior;
import com.xtc.moment.module.personalinfo.net.bean.LikeRule;
import com.xtc.moment.util.SharedTool;
import com.xtc.ui.widget.button.LongSolidButton;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 点赞规则说明页面。
 */
public class LikeRuleShowActivity extends BaseActivity<LikeRuleView, LikeRulePresenter> implements LikeRuleView {

    private static final String TAG = "LikeRuleShowActivity";

    public static final int[] BLUE_BUTTON = {R.color.color_04deff, R.color.color_4876fc};

    private RecyclerView mRecyclerView;
    private LikeRuleAdapter mLikeRuleAdapter;
    private LongSolidButton mLsbGotIt;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LikeRuleShowActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_rule_show);
        initView();
        initData();
    }

    @Override
    public void initView() {
        this.mLsbGotIt = (LongSolidButton) findViewById(R.id.lsb_got_it);
        this.mLsbGotIt.setBgColorIdArray(BLUE_BUTTON);
        this.mLsbGotIt.getTv().setText(getString(R.string.i_known));
        this.mLsbGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeRuleShowActivity.this.finish();
            }
        });
        this.mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mLikeRuleAdapter = new LikeRuleAdapter(this);
        this.mRecyclerView.setAdapter(this.mLikeRuleAdapter);
        PersonalCenterBehavior.likeFriendLimit();
    }

    @Override
    public void initData() {
        Observable.just(true)
                .map(new Func1<Boolean, List<LikeRule>>() {
                    @Override
                    public List<LikeRule> call(Boolean ignored) {
                        String likesRule = SharedTool.getLikesRule(LikeRuleShowActivity.this);
                        if (TextUtils.isEmpty(likesRule)) {
                            return null;
                        }
                        return JSONUtil.fromJSON(likesRule, List.class, LikeRule.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<LikeRule>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, throwable);
                    }

                    @Override
                    public void onNext(List<LikeRule> likeRules) {
                        if (!CollectionUtil.isEmpty(likeRules)) {
                            LikeRuleShowActivity.this.mLikeRuleAdapter.addData(likeRules);
                        } else {
                            LogUtil.d(TAG, "likeRuleList is null");
                            LikeRuleShowActivity.this.finish();
                        }
                    }
                });
    }

    @Override
    public LikeRulePresenter createPresenter() {
        return new LikeRulePresenter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onPause: ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "onNewIntent: ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogUtil.i(TAG, "onWindowFocusChanged: " + hasFocus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
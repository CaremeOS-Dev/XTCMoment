package com.xtc.moment.module.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.widget.BaseMomentActivity;
import com.xtc.moment.net.CommunityConversationProxy;
import com.xtc.moment.net.bean.CommunityConversationResponse;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.ui.DimenUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 社区会话列表页面。
 */
public class CommunityConversationActivity
        extends BaseMomentActivity<MvpView, MvpPresenter<MvpView>> implements MvpView {

    private static final String TAG = "CommunityConversationActivity";

    private RecyclerView mRecyclerView;
    private CommunityConversationAdapter mCommunityConversationAdapter;
    private CommunityConversationProxy mCommunityConversationProxy;
    private AnimationDrawable mLoadingAnim;
    private ImageView mIvLoadView;
    private ImageView mIvError;
    private FrameLayout mFlLoading;
    private TextView mTvLoadHint;

    public static void start(Context context) {
        LogUtil.d(TAG, "startCommunityConversationActivity");
        context.startActivity(new Intent(context, CommunityConversationActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_conversation);
        initView();
        initData();
    }

    @Override
    public MvpPresenter createPresenter() {
        return new MvpPresenter();
    }

    public void initView() {
        this.mRecyclerView = (RecyclerView) findViewById(R.id.rv_content);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = DimenUtil.dp2px(CommunityConversationActivity.this, 5.0f);
            }
        });
    }

    public void initData() {
        this.mCommunityConversationProxy = new CommunityConversationProxy(getApplicationContext());
        getRemoteData();
    }

    private void getRemoteData() {
        initLoadView();
        this.mCommunityConversationProxy.getConventionHome()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommunityConversationResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable.getMessage().contains("1003")) {
                            CommunityConversationActivity.this.showLoadError(R.string.frequent_request_new);
                        } else {
                            CommunityConversationActivity.this.showLoadError(R.string.net_error);
                        }
                    }

                    @Override
                    public void onNext(CommunityConversationResponse response) {
                        LogUtil.d(TAG, "onNext : communityConversationResponse = [" + response + "]");
                        if (response == null) {
                            CommunityConversationActivity.this.showLoadError(R.string.net_error);
                        } else {
                            CommunityConversationActivity.this.finishFirstLoad();
                            CommunityConversationActivity.this.setAdapter(response);
                        }
                    }
                });
    }

    private void setAdapter(CommunityConversationResponse response) {
        this.mCommunityConversationAdapter = new CommunityConversationAdapter(this, response);
        this.mRecyclerView.setAdapter(this.mCommunityConversationAdapter);
        this.mCommunityConversationAdapter.setOnItemClickListener(new CommunityConversationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                LogUtil.d(TAG, "onItemClick : position = [" + position + "]");
                CommunityConversationDetailActivity.start(CommunityConversationActivity.this, position);
            }
        });
    }

    private void initLoadView() {
        this.mLoadingAnim = new LoadingAnim(this).createAnim(R.color.color_ffffff, 0.6f, 100);
        this.mIvLoadView = (ImageView) findViewById(R.id.iv_load_anim);
        this.mIvError = (ImageView) findViewById(R.id.iv_error);
        this.mFlLoading = (FrameLayout) findViewById(R.id.fl_loading);
        this.mTvLoadHint = (TextView) findViewById(R.id.tv_load_hint);
        this.mTvLoadHint.setText(getResources().getString(R.string.text_loading));
        this.mIvLoadView.setBackground(this.mLoadingAnim);
        this.mLoadingAnim.start();
    }

    private void finishFirstLoad() {
        this.mLoadingAnim.stop();
        this.mFlLoading.setVisibility(View.GONE);
        this.mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoadError(int messageResId) {
        this.mLoadingAnim.stop();
        this.mIvLoadView.setVisibility(View.GONE);
        this.mIvError.setImageResource(messageResId == R.string.frequent_request_new
                ? R.drawable.ic_frequest_operate : R.drawable.ic_os_no_net);
        this.mIvLoadView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        this.mTvLoadHint.setText(getResources().getString(messageResId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLoadingAnim.isRunning()) {
            this.mLoadingAnim.stop();
        }
    }
}
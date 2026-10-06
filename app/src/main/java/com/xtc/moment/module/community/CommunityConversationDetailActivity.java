package com.xtc.moment.module.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.widget.BaseMomentActivity;
import com.xtc.moment.module.widget.IndicatorContainerView;
import com.xtc.moment.net.CommunityConversationProxy;
import com.xtc.moment.net.bean.CommunityDetailResponse;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 社区会话详情页面，按章节分页展示。
 */
public class CommunityConversationDetailActivity
        extends BaseMomentActivity<MvpView, MvpPresenter<MvpView>> implements MvpView {

    private static final String TAG = "CommunityConversationDetailActivity";
    private static final String INDEX = "index";

    private ViewPager mViewPager;
    private IndicatorContainerView mIndicatorContainerView;
    private CommunityConversationProxy mCommunityConversationProxy;
    private Fragment[] mFragmentArray;
    private AnimationDrawable mLoadingAnim;
    private ImageView mIvLoadView;
    private ImageView mIvError;
    private FrameLayout mFlLoading;
    private TextView mTvLoadHint;
    private int index;
    private boolean isIndicatorShow = true;

    public static void start(Context context, int index) {
        LogUtil.d(TAG, "startCommunityConversationDetailActivity : index = [" + index + "]");
        Intent intent = new Intent(context, CommunityConversationDetailActivity.class);
        intent.putExtra(INDEX, index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_conversation_detail);
        initView();
        initData();
    }

    @Override
    public MvpPresenter createPresenter() {
        return new MvpPresenter();
    }

    public void initView() {
        this.mViewPager = (ViewPager) findViewById(R.id.vp_content);
        this.mIndicatorContainerView = (IndicatorContainerView) findViewById(R.id.indicatorContainerView);
    }

    private void initViewPager(List<CommunityDetailResponse> detailList) {
        this.mFragmentArray = new Fragment[detailList.size()];
        for (int i = 0; i < detailList.size(); i++) {
            this.mFragmentArray[i] = CommunityDetailFragment.newInstance(detailList.get(i));
        }
        this.mViewPager.setOffscreenPageLimit(this.mFragmentArray.length);
        this.mViewPager.setAdapter(new CommunityPagerAdapter(getSupportFragmentManager(), this.mFragmentArray));
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                LogUtil.d(TAG, "onPageSelected : position = " + position);
                CommunityConversationDetailActivity.this.mIndicatorContainerView.selectIndicator(position);
            }
        });
        this.mIndicatorContainerView.initIndicatorView(this.mFragmentArray.length);
    }

    public void initData() {
        this.mCommunityConversationProxy = new CommunityConversationProxy(getApplicationContext());
        getRemoteData();
    }

    private void getRemoteData() {
        initLoadView();
        if (getIntent() == null) {
            finish();
            return;
        }
        this.index = getIntent().getIntExtra(INDEX, 0);
        this.mCommunityConversationProxy.getConventionContent(this.index)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CommunityDetailResponse>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable.getMessage().contains("1003")) {
                            CommunityConversationDetailActivity.this
                                    .showLoadError(R.string.frequent_request_new);
                        } else {
                            CommunityConversationDetailActivity.this.showLoadError(R.string.net_error);
                        }
                    }

                    @Override
                    public void onNext(List<CommunityDetailResponse> detailList) {
                        LogUtil.d(TAG, "onNext : communityDetailResponseList = [" + detailList + "]");
                        if (CollectionUtil.isEmpty(detailList)) {
                            CommunityConversationDetailActivity.this.showLoadError(R.string.net_error);
                        } else {
                            CommunityConversationDetailActivity.this.finishFirstLoad();
                            CommunityConversationDetailActivity.this.initViewPager(detailList);
                        }
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
        this.mViewPager.setVisibility(View.VISIBLE);
        this.mIndicatorContainerView.setVisibility(View.VISIBLE);
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
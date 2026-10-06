package com.xtc.moment.module.community;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xtc.architecture.mvp.BaseFragment;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.net.bean.CommunityDetailResponse;

/**
 * 社区章节详情 Fragment。
 */
public class CommunityDetailFragment
        extends BaseFragment<MvpView, MvpPresenter<MvpView>> implements MvpView {

    private static final String TAG = "CommunityDetailFragment";
    private static final String DETAIL = "detail";

    private TextView mTvTop;
    private TextView mTvTitle;
    private TextView mTvContent;
    private NestedScrollView scrollView;
    private CommunityDetailResponse mCommunityDetailResponse;

    public static CommunityDetailFragment newInstance(CommunityDetailResponse detailResponse) {
        CommunityDetailFragment fragment = new CommunityDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(DETAIL, detailResponse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public MvpPresenter<MvpView> createPresenter() {
        return new MvpPresenter<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_community_detail, container, false);
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        this.mTvTop = (TextView) contentView.findViewById(R.id.tv_top);
        this.mTvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        this.mTvContent = (TextView) contentView.findViewById(R.id.tv_content);
        this.scrollView = (NestedScrollView) contentView.findViewById(R.id.scrollView);
    }

    @Override
    public void initData() {
        if (this.presenter == null) {
            return;
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mCommunityDetailResponse = (CommunityDetailResponse) arguments.getSerializable(DETAIL);
        }
        if (this.mCommunityDetailResponse == null) {
            return;
        }
        this.mTvTop.setText(this.mCommunityDetailResponse.getChapter() + " "
                + this.mCommunityDetailResponse.getChapterTitle());
        String title = this.mCommunityDetailResponse.getTitle();
        if (!TextUtils.isEmpty(title)) {
            this.mTvTitle.setText(title);
            this.mTvTitle.setVisibility(View.VISIBLE);
        }
        this.mTvContent.setText(this.mCommunityDetailResponse.getContent());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.d(TAG, "setUserVisibleHint() called with: isVisibleToUser = [" + isVisibleToUser + "]");
        if (isVisibleToUser || this.scrollView == null) {
            return;
        }
        this.scrollView.scrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause() called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
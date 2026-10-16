package com.xtc.moment.module.publish.topic;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.module.Constants;
import com.xtc.moment.net.bean.RespTopicConfig;
import com.xtc.moment.net.bean.TopicListBean;
import com.xtc.moment.util.SharedTool;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.utils.common.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 「小括号」选择页：展示推荐话题，选中后回传给发布页。
 */
public class TopicActivity extends BaseActivity<ITopicView, TopicPresenter> implements ITopicView {

    public static final String INTENT_TOPIC_DATA = "intent_topic_data";
    public static final String INTENT_MAX_TOPIC_COUNT = "intent_max_topic_count";
    public static final String INTENT_CHOOSE_DATA = "intent_choose_data";

    private static final String TAG = "TopicActivity";

    private RecyclerView recyclerView;
    private LinearLayout llLoading;
    private LinearLayout llNoData;
    private TopicAdapter topicAdapter;
    private AnimationDrawable animationDrawable;
    private ArrayList<TopicListBean> topicList;
    private int maxTopicCount = 0;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    @Override
    public TopicPresenter createPresenter() {
        return new TopicPresenter(this);
    }

    @Override
    public void initView() {
        AsyncLayoutLoader.getInstance().setContentView(this, R.layout.activity_topic);
        initTopicList();
        this.llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        this.llNoData = (LinearLayout) findViewById(R.id.ll_no_data);
    }

    @Override
    public void initData() {
        startLoading();
        this.topicList = getIntent().getParcelableArrayListExtra(INTENT_CHOOSE_DATA);
        this.presenter.getTopicConfigData();
    }

    private void initTopicList() {
        this.recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.topicAdapter = new TopicAdapter(this);
        this.recyclerView.setAdapter(this.topicAdapter);
        this.topicAdapter.setOnTopicClickListener(new TopicAdapter.OnTopicClickListener() {
            @Override
            public void onTopicClick(int position) {
                TopicStateBean topicStateBean = TopicActivity.this.topicAdapter.getTopicState(position);
                Intent intent = buildResultIntent();
                intent.putExtra(INTENT_TOPIC_DATA, topicStateBean.getTopicListBean());
                intent.putExtra(INTENT_MAX_TOPIC_COUNT, TopicActivity.this.maxTopicCount);
                TopicActivity.this.isFinished = true;
                TopicActivity.this.setResult(RESULT_OK, intent);
                TopicActivity.this.finish();
            }
        });
    }

    @Override
    public void showTopicList(RespTopicConfig respTopicConfig) {
        List<TopicStateBean> topicStateList = new ArrayList<>();
        if (respTopicConfig != null && !CollectionUtil.isEmpty(respTopicConfig.getTopicList())) {
            this.maxTopicCount = respTopicConfig.getConfig().getMaxTopicCount();
            SharedTool.saveMaxTopicCount(this, this.maxTopicCount);
            for (TopicListBean topicListBean : respTopicConfig.getTopicList()) {
                topicListBean.setTopicName(Constants.TOPIC_PREFIX + topicListBean.getTopicName() + Constants.TOPIC_SUFFIX);
                boolean selected = false;
                if (!CollectionUtil.isEmpty(this.topicList)) {
                    for (TopicListBean selectedBean : this.topicList) {
                        if (selectedBean.getTopicName().equals(topicListBean.getTopicName())) {
                            selected = true;
                            selectedBean.setTopicType(topicListBean.getTopicType());
                            selectedBean.setColor(topicListBean.getColor());
                            selectedBean.setJumpUrl(topicListBean.getJumpUrl());
                            selectedBean.setSortValue(topicListBean.getSortValue());
                            break;
                        }
                    }
                }
                topicStateList.add(new TopicStateBean(topicListBean, selected));
            }
            this.recyclerView.setVisibility(View.VISIBLE);
            this.llNoData.setVisibility(View.GONE);
        } else {
            this.recyclerView.setVisibility(View.GONE);
            this.llNoData.setVisibility(View.VISIBLE);
        }
        this.topicAdapter.setData(topicStateList);
        stopLoading();
    }

    @Override
    public void showTopicError() {
        LogUtil.d(TAG, "showTopicError");
        stopLoading();
        this.recyclerView.setVisibility(View.GONE);
        this.llNoData.setVisibility(View.VISIBLE);
    }

    private void startLoading() {
        LogUtil.d(TAG, "startLoading");
        this.llLoading.setVisibility(View.VISIBLE);
        View loadingView = findViewById(R.id.loading_ll);
        this.animationDrawable = new LoadingAnim(this).createAnim();
        loadingView.setBackground(this.animationDrawable);
        this.animationDrawable.start();
    }

    private void stopLoading() {
        LogUtil.d(TAG, "stop loading");
        this.llLoading.setVisibility(View.GONE);
        if (this.animationDrawable != null && this.animationDrawable.isRunning()) {
            this.animationDrawable.stop();
        }
    }

    private Intent buildResultIntent() {
        Intent intent = new Intent();
        if (!CollectionUtil.isEmpty(this.topicList)) {
            intent.putParcelableArrayListExtra(INTENT_CHOOSE_DATA, this.topicList);
        }
        return intent;
    }

    private void setResultIfNeeded() {
        if (this.isFinished) {
            return;
        }
        Intent intent = buildResultIntent();
        this.isFinished = true;
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finish() {
        setResultIfNeeded();
        super.finish();
    }
}


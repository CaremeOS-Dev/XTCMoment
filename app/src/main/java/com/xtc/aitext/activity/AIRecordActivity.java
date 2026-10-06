package com.xtc.aitext.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.aitext.R;
import com.xtc.aitext.activity.view.IAIRecordView;
import com.xtc.aitext.adapter.AIRecordAdapter;
import com.xtc.aitext.bean.AIRecordDetailBean;
import com.xtc.aitext.presenter.AIRecordPresenter;
import com.xtc.aitext.weight.AISuccessDialog;
import com.xtc.aitext.weight.callback.ActivityControllListener;
import com.xtc.aitext.weight.callback.ActivityFinishCallback;
import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 创作记录页。
 */
public class AIRecordActivity extends BaseActivity<IAIRecordView, AIRecordPresenter>
        implements IAIRecordView, ActivityFinishCallback {

    private static final String TAG = "ai_text_AIRecordActivity";

    private RecyclerView recordRv;
    private AIRecordAdapter adapter;
    private LinearLayout recordLlNone;
    private LinearLayoutManager layoutManager;
    private AISuccessDialog successDialog;

    private boolean isLoadingMore;

    @Override
    public void showEmpty() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        initData();
    }

    @Override
    public AIRecordPresenter createPresenter() {
        return new AIRecordPresenter(this);
    }

    @Override
    public void initView() {
        this.recordLlNone = findViewById(R.id.record_ll_none);
        this.recordRv = findViewById(R.id.record_rv);
        this.layoutManager = new LinearLayoutManager(this);
        this.recordRv.setLayoutManager(this.layoutManager);
        this.adapter = new AIRecordAdapter(this, new ArrayList<AIRecordDetailBean>());
        this.recordRv.setAdapter(this.adapter);
        this.recordRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                if (getPresenter().isNoMore()) {
                    return;
                }
                if (adapter.getItemCount() - 1 != layoutManager.findLastVisibleItemPosition() || isLoadingMore) {
                    return;
                }
                LogUtil.d(TAG, "onScrolled: 滑动加载更多");
                isLoadingMore = true;
                getPresenter().queryRecord(false);
            }
        });
        this.adapter.setClickItem(new AIRecordAdapter.ClickItem() {
            @Override
            public void onItemClick(AIRecordDetailBean recordBean) {
                if (recordBean == null) {
                    return;
                }
                DialogUtil.dismissDialog(successDialog);
                successDialog = new AISuccessDialog(AIRecordActivity.this);
                successDialog.setTvContent(recordBean.getResultText());
                successDialog.setInRecord(true);
                DialogUtil.showDialog(successDialog);
            }
        });
        ActivityControllListener.getInstance().addListener(this);
    }

    @Override
    public void initData() {
        getPresenter().queryRecord(true);
    }

    @Override
    public void showRecords(List<AIRecordDetailBean> recordList) {
        this.recordLlNone.setVisibility(View.GONE);
        this.recordRv.setVisibility(View.VISIBLE);
        this.adapter.refreshData(recordList, getPresenter().isNoMore());
        this.isLoadingMore = false;
    }

    @Override
    protected void onDestroy() {
        ActivityControllListener.getInstance().removeListener(this);
        DialogUtil.dismissDialog(this.successDialog);
        super.onDestroy();
    }

    @Override
    public void finishActivity() {
        LogUtil.d(TAG, "finishActivity: ");
        finish();
    }
}
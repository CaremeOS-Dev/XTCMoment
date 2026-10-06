package com.xtc.moment.module.report.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xtc.moment.R;
import com.xtc.moment.module.report.adapter.ReportReasonsAdapter;
import com.xtc.moment.module.report.interfaces.IJumpView;
import com.xtc.moment.module.report.interfaces.OnItemClickListener;

import java.util.ArrayList;

/**
 * 举报原因选择页面。
 */
public class SelectReportReasonView extends AbsSelectReportView {

    private RecyclerView selectReportRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_report_select;
    }

    @Override
    protected void loadData() {
    }

    public SelectReportReasonView(Context context, IJumpView iJumpView) {
        super(context, iJumpView);
    }

    public SelectReportReasonView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void findViewId() {
        this.selectReportRecyclerView = (RecyclerView) findId(R.id.selectReportRecyclerView);
    }

    @Override
    protected void initData() {
        this.selectReportRecyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        ReportReasonsAdapter reasonsAdapter = new ReportReasonsAdapter(this.mContext);
        reasonsAdapter.setmHeaderView(LayoutInflater.from(this.mContext)
                .inflate(R.layout.item_report_reason_header, (ViewGroup) this.selectReportRecyclerView, false));
        ArrayList<String> reasons = new ArrayList<>();
        reasons.add(this.mContext.getResources().getString(R.string.report_calling));
        reasons.add(this.mContext.getResources().getString(R.string.report_rumor));
        reasons.add(this.mContext.getResources().getString(R.string.report_yellow));
        reasons.add(this.mContext.getResources().getString(R.string.other));
        reasonsAdapter.setDataString(reasons);
        this.selectReportRecyclerView.setAdapter(reasonsAdapter);
        reasonsAdapter.setOnItemClickListener(new OnItemClickListener<String>() {
            @Override
            public void onItemClick(String reason, int position) {
                if (reason.equals(SelectReportReasonView.this.mContext.getResources().getString(R.string.report_calling))) {
                    SelectReportReasonView.this.reportPresenter.setInformType(1);
                } else if (reason.equals(SelectReportReasonView.this.mContext.getResources().getString(R.string.report_rumor))) {
                    SelectReportReasonView.this.reportPresenter.setInformType(3);
                } else if (reason.equals(SelectReportReasonView.this.mContext.getResources().getString(R.string.report_yellow))) {
                    SelectReportReasonView.this.reportPresenter.setInformType(0);
                } else if (reason.equals(SelectReportReasonView.this.mContext.getResources().getString(R.string.other))) {
                    SelectReportReasonView.this.reportPresenter.setInformType(2);
                }
                SelectReportReasonView.this.showNextView();
            }
        });
    }
}
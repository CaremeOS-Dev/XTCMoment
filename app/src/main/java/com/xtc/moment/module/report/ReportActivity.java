package com.xtc.moment.module.report;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.architecture.mvp.BaseActivity;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.manager.ReportDataRecorder;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.module.report.view.AbsBaseJumpView;
import com.xtc.moment.module.report.view.HintView;
import com.xtc.moment.module.report.view.LoadDataView;
import com.xtc.moment.module.report.view.SelectReportReasonView;
import com.xtc.moment.module.report.view.SubmitReportView;
import com.xtc.moment.util.ToastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 举报流程页面，按“加载状态 -> 选择原因 -> 提交提示 -> 提交结果”顺序逐步展示子 View。
 */
public class ReportActivity extends BaseActivity<IReportView, ReportPresenter> implements IReportView {

    private static final String TAG = "ReportActivity";
    private static final int FINISH_TIME = 3000;

    public static final String MOMENT_ID = "momentId";
    public static final String COMMENT_ID = "commentId";
    public static final String FRIEND_WATCH_ID = "friendWatchId";
    public static final String REPORT_TYPE = "reportType";
    public static final String CONTENT = "content";
    public static final String CONTENT_TYPE = "content_type";
    public static final String IN_FORM_SOURCE = "informSource";
    public static final String MOMENT_WATCH_ID = "momentWatchId";
    public static final String MOMENT_CONTENT = "moment_content";
    public static final String CONTENTS = "contents";

    public static final int MOMENT_TYPE = 1;
    public static final int COMMENT_TYPE = 2;
    public static final int CONTENT_TEXT_TYPE = 1;
    public static final int CONTENT_PHOTO_TYPE = 2;
    public static final int CONTENT_VIDEO_TYPE = 3;

    private LinearLayout llParentLayout;
    private List<AbsBaseJumpView> absBaseJumpViewList;
    private LoadDataView loadDataView;
    private SubmitReportView submitReportView;

    private String friendId;
    private String momentId;
    private String commentId;
    private String momentWatchId;
    private int reportType;
    private int informSource;
    private String content;
    private ArrayList<StartReportRequest.Contents> photoKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initData();
        initView();
    }

    @Override
    public ReportPresenter createPresenter() {
        return new ReportPresenter(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            LogUtil.i(TAG, "initData error");
            return;
        }
        this.friendId = intent.getStringExtra(FRIEND_WATCH_ID);
        this.momentId = intent.getStringExtra(MOMENT_ID);
        this.commentId = intent.getStringExtra(COMMENT_ID);
        this.reportType = intent.getIntExtra(REPORT_TYPE, MOMENT_TYPE);
        this.content = intent.getStringExtra(CONTENT);
        this.informSource = intent.getIntExtra(IN_FORM_SOURCE, 0);
        this.momentWatchId = intent.getStringExtra(MOMENT_WATCH_ID);
        if (!TextUtils.isEmpty(this.content)) {
            this.photoKey = new ArrayList<>();
            int contentType = intent.getIntExtra(CONTENT_TYPE, CONTENT_PHOTO_TYPE);
            StartReportRequest.Contents contents = new StartReportRequest.Contents();
            contents.setType(contentType);
            contents.setContent(this.content);
            contents.setMessageDate(new Date().getTime());
            this.photoKey.add(contents);
        }
        this.presenter.setReportData(this.friendId, this.momentId, this.commentId, this.reportType, this.photoKey,
                this.informSource, this.momentWatchId);
    }

    @Override
    public void initView() {
        this.llParentLayout = (LinearLayout) findViewById(R.id.view_add);
        this.absBaseJumpViewList = new ArrayList<>();
        initLoadDataView();
        replaceShowView(this.absBaseJumpViewList.get(0));
    }

    private void initLoadDataView() {
        this.loadDataView = new LoadDataView(this, this);
        this.loadDataView.setReportPresenter(this.presenter);
        this.absBaseJumpViewList.add(this.loadDataView);
    }

    private void initReportFlow() {
        initReportReasonsView();
        initSubmitReportHint();
        initSuccessReportHint();
    }

    private void initSuccessReportHint() {
        this.submitReportView = new SubmitReportView(this, this);
        this.submitReportView.setReportPresenter(this.presenter);
        this.absBaseJumpViewList.add(this.submitReportView);
    }

    private void initSubmitReportHint() {
        HintView hintView = new HintView(this, this);
        hintView.setCenterHintText(getResources().getString(R.string.report_submit_hint));
        this.absBaseJumpViewList.add(hintView);
    }

    private void initReportReasonsView() {
        SelectReportReasonView selectReportReasonView = new SelectReportReasonView(this, this);
        selectReportReasonView.setReportPresenter(this.presenter);
        this.absBaseJumpViewList.add(selectReportReasonView);
    }

    @Override
    public void getReportDataSuccess(ReportDataBean reportDataBean) {
        this.loadDataView.loadFinish();
        int state = reportDataBean.getState();
        if (state == ReportDataBean.NORMAL_STATE) {
            initReportFlow();
            showNextView(this.loadDataView);
            this.absBaseJumpViewList.remove(this.loadDataView);
            return;
        }
        if (state == ReportDataBean.NOT_QUALIFIED_STATE) {
            initComeReportHint(getResources().getString(R.string.report_cancel_status));
        } else if (state == ReportDataBean.REPORT_TODAY_STATE) {
            initComeReportHint(getResources().getString(R.string.report_today_not));
        } else if (state == ReportDataBean.REPORT_PERSON_STATE) {
            initComeReportHint(getResources().getString(R.string.report_other_person_hint));
        } else if (state == ReportDataBean.REPORT_PERSON_BANNER) {
            initComeReportHint(getResources().getString(R.string.report_banner_person_hint));
        }
    }

    @Override
    public void getReportDataFail(String message) {
        LogUtil.e(TAG, "getReportDataFail: message=" + message);
        ToastUtil.showLong(this, getResources().getString(R.string.net_error));
        this.loadDataView.loadFinish();
        finish();
    }

    @Override
    public void reportSuccess() {
        this.submitReportView.pushSuccess();
    }

    @Override
    public void reportFail(String message) {
        getReportDataFail(message);
    }

    @Override
    public void showNextView(AbsBaseJumpView view) {
        int index = this.absBaseJumpViewList.indexOf(view);
        if (index < 0 || index >= this.absBaseJumpViewList.size()) {
            return;
        }
        if (index == this.absBaseJumpViewList.size() - 1) {
            finish();
        } else {
            replaceShowView(this.absBaseJumpViewList.get(index + 1));
        }
    }

    @Override
    public void showPreviousView(AbsBaseJumpView view) {
        int index = this.absBaseJumpViewList.indexOf(view);
        if (index < 0 || index >= this.absBaseJumpViewList.size()) {
            return;
        }
        if (index == 0) {
            finish();
        } else {
            replaceShowView(this.absBaseJumpViewList.get(index - 1));
        }
    }

    @Override
    public void finishView() {
        finish();
    }

    private void replaceShowView(View view) {
        if (view instanceof AbsBaseJumpView) {
            ((AbsBaseJumpView) view).viewShow(this.informSource);
        }
        this.llParentLayout.removeAllViews();
        this.llParentLayout.addView(view, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void initComeReportHint(String hintText) {
        HintView hintView = new HintView(this, this);
        hintView.setCenterHintText(hintText);
        hintView.setHintSure();
        this.absBaseJumpViewList.add(hintView);
        replaceShowView(hintView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "momentId = " + this.momentId + ", commentId = " + this.commentId);
        ReportDataRecorder.removeRecord(this.momentId, this.commentId);
    }
}
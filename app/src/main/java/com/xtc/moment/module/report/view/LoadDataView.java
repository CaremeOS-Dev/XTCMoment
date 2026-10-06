package com.xtc.moment.module.report.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xtc.moment.R;
import com.xtc.moment.module.report.interfaces.IJumpView;
import com.xtc.ui.widget.animation.indicator.LoadingAnim;

/**
 * 举报数据加载中页面，展示旋转 loading 并延时查询举报入口状态。
 */
public class LoadDataView extends AbsSelectReportView {

    private static final int SHOW_TIME = 500;

    private ImageView ivLoad;
    private AnimationDrawable animDrawable;

    @Override
    protected int getLayoutId() {
        return R.layout.layout_load_report;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void loadData() {
    }

    public LoadDataView(Context context, IJumpView iJumpView) {
        super(context, iJumpView);
    }

    public LoadDataView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void findViewId() {
        this.ivLoad = (ImageView) findId(R.id.loading_animation_view);
        this.animDrawable = new LoadingAnim(getContext()).createAnim(R.color.color_ffffff, 1.2f, 100);
        this.ivLoad.setImageDrawable(this.animDrawable);
    }

    public void loadFinish() {
        this.animDrawable.stop();
    }

    @Override
    public void viewShow(final int informSource) {
        super.viewShow(informSource);
        this.animDrawable.start();
        this.ivLoad.postDelayed(new Runnable() {
            @Override
            public void run() {
                LoadDataView.this.reportPresenter.queryReportInform(informSource);
            }
        }, SHOW_TIME);
    }
}
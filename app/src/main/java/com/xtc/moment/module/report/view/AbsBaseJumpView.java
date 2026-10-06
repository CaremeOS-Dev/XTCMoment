package com.xtc.moment.module.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xtc.moment.module.report.interfaces.IJumpView;

/**
 * 举报流程中每一步页面的基类。
 *
 * <p>每个子类通过 {@link #getLayoutId()} 声明布局，通过 {@link #findViewId()} 绑定控件，
 * 并在 {@link #initData()} 中完成初始化。页面之间的切换交给 {@link IJumpView} 处理。
 */
public abstract class AbsBaseJumpView extends LinearLayout {

    protected Context mContext;
    protected View view;
    protected IJumpView iJumpView;

    protected abstract int getLayoutId();

    protected abstract void findViewId();

    protected abstract void initData();

    /** 页面展示时的回调，子类可覆写做数据加载或动画。 */
    public void viewShow(int informSource) {
    }

    public AbsBaseJumpView(Context context, IJumpView iJumpView) {
        super(context);
        setiJumpView(iJumpView);
        initView(context);
    }

    public AbsBaseJumpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public void setiJumpView(IJumpView iJumpView) {
        this.iJumpView = iJumpView;
    }

    private void initView(Context context) {
        this.mContext = context;
        this.view = LayoutInflater.from(context).inflate(getLayoutId(), this);
        findViewId();
        initData();
    }

    protected View findId(int viewId) {
        if (this.view == null) {
            this.view = LayoutInflater.from(this.mContext).inflate(getLayoutId(), this);
        }
        return this.view.findViewById(viewId);
    }

    protected void showNextView() {
        IJumpView jumpView = this.iJumpView;
        if (jumpView != null) {
            jumpView.showNextView(this);
        }
    }

    protected void finishView() {
        IJumpView jumpView = this.iJumpView;
        if (jumpView != null) {
            jumpView.finishView();
        }
    }

    protected void showPreviousView() {
        IJumpView jumpView = this.iJumpView;
        if (jumpView != null) {
            jumpView.showPreviousView(this);
        }
    }
}
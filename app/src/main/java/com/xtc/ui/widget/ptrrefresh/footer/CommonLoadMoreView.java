package com.xtc.ui.widget.ptrrefresh.footer;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xtc.ui.widget.R;

/** 默认的加载更多视图：文字 + 进度条。 */
public class CommonLoadMoreView implements LoadMoreView {
    protected ProgressBar footerBar;
    protected TextView footerTv;
    protected View footerView;
    protected View.OnClickListener onClickRefreshListener;

    @Override
    public void init(FootViewAdder footViewAdder, View.OnClickListener onClickListener) {
        this.footerView = footViewAdder.addFootView(R.layout.loadmore_default_footer);
        this.footerTv = (TextView) this.footerView.findViewById(R.id.load_more_default_footer_tv);
        this.footerBar = (ProgressBar) this.footerView.findViewById(R.id.load_more_default_footer_progressbar);
        this.onClickRefreshListener = onClickListener;
        showNormal();
    }

    @Override
    public void showNormal() {
        this.footerTv.setText("点击加载更多");
        this.footerBar.setVisibility(8);
        this.footerView.setOnClickListener(this.onClickRefreshListener);
    }

    @Override
    public void showLoading() {
        this.footerTv.setText("正在加载中...");
        this.footerBar.setVisibility(0);
        this.footerView.setOnClickListener(null);
    }

    @Override
    public void showFail(Exception exception) {
        this.footerTv.setText("加载失败，点击重新");
        this.footerBar.setVisibility(8);
        this.footerView.setOnClickListener(this.onClickRefreshListener);
    }

    @Override
    public void showNoMore() {
        this.footerTv.setText("已经加载完毕");
        this.footerBar.setVisibility(8);
        this.footerView.setOnClickListener(null);
    }
}
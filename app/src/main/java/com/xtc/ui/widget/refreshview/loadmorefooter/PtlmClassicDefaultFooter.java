package com.xtc.ui.widget.refreshview.loadmorefooter;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.xtc.moment.R;
import com.xtc.ui.widget.refreshview.interfaces.PtlmUIHandler;

/** 经典的加载更多页脚实现：文字 + 进度条。 */
public class PtlmClassicDefaultFooter implements PtlmUIHandler {

    @Override
    public PtlmUIHandler.ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    /** 页脚状态控制器。 */
    private class LoadMoreHelper implements PtlmUIHandler.ILoadMoreView {
        private ProgressBar footerBar;
        private TextView footerTv;
        private View footerView;
        private View.OnClickListener onClickRefreshListener;

        private LoadMoreHelper() {
        }

        @Override
        public void init(PtlmUIHandler.FootViewAdder footViewAdder, View.OnClickListener onClickListener) {
            this.footerView = footViewAdder.addFootView(R.layout.loadmore_default_footer);
            this.footerTv = (TextView) this.footerView.findViewById(R.id.load_more_default_footer_tv);
            this.footerBar = (ProgressBar) this.footerView.findViewById(R.id.load_more_default_footer_progressbar);
            this.onClickRefreshListener = onClickListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            this.footerTv.setText(R.string.click_to_load_more);
            this.footerBar.setVisibility(8);
            this.footerView.setOnClickListener(this.onClickRefreshListener);
        }

        @Override
        public void showLoading() {
            this.footerTv.setText(R.string.loading_now);
            this.footerBar.setVisibility(0);
            this.footerView.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception exception) {
            this.footerTv.setText(R.string.load_fail);
            this.footerBar.setVisibility(8);
            this.footerView.setOnClickListener(this.onClickRefreshListener);
        }

        @Override
        public void showNoMore() {
            this.footerTv.setText(R.string.load_complete);
            this.footerBar.setVisibility(8);
            this.footerView.setOnClickListener(null);
        }

        @Override
        public void setFooterVisibility(boolean visible) {
            this.footerView.setVisibility(visible ? 0 : 8);
        }

        @Override
        public void showView() {
            this.footerView.setVisibility(0);
        }

        @Override
        public void hideView() {
            this.footerView.setVisibility(8);
        }
    }
}
package com.xtc.ui.widget.ptrrefresh.layout;

import android.content.Context;
import android.util.AttributeSet;
import com.xtc.ui.widget.ptrrefresh.header.NewSuccRefreshHeader;
import com.xtc.ui.widget.ptrrefresh.header.SuccRefreshHeader;

/** 预设了成功刷新头部的下拉刷新容器。 */
public class PullRefreshFrameLayout extends BaseFrameLayout {
    private static final int DEFAULT_REFRESH_COLOR = -5066062;
    private static final int DEFAULT_NORMAL_COLOR = 452984832;
    private static final int DEFAULT_TEXT_COLOR = -7829368;
    private NewSuccRefreshHeader mNewSuccRefreshHeader;
    private SuccRefreshHeader mSuccRefreshHeader;

    public PullRefreshFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PullRefreshFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PullRefreshFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        this.mNewSuccRefreshHeader = new NewSuccRefreshHeader(getContext());
        this.mNewSuccRefreshHeader.setColors(DEFAULT_REFRESH_COLOR, DEFAULT_NORMAL_COLOR, DEFAULT_TEXT_COLOR);
        setHeaderView(this.mNewSuccRefreshHeader);
        addUIRefreshHandler(this.mNewSuccRefreshHeader);
    }
}
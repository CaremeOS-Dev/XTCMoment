package com.xtc.architecture.mvp;

import android.os.Bundle;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.architecture.mvp.core.MvpView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * {@link BaseFragment} that also tracks the host pager's scroll state and page
 * index.
 */
public abstract class BaseFragmentEx<V extends MvpView, P extends MvpPresenter<V>> extends BaseFragment<V, P> {

    public static final int IDLE = 0;
    public static final int DRAGGING = 1;
    public static final int SETTLING = 2;

    protected int scrollState;
    protected int pageIndex;

    /** Pager scroll-state values. */
    @Retention(RetentionPolicy.SOURCE)
    public @interface PageScrollState {
    }

    public void onPageScrollStateChanged(int state, int position, Bundle bundle) {
    }

    public void setPageState(int scrollState, int pageIndex) {
        setScrollState(scrollState);
        setPageIndex(pageIndex);
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setScrollState(int scrollState) {
        this.scrollState = scrollState;
    }
}

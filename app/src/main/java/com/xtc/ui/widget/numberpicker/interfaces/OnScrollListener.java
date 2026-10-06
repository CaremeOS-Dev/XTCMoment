package com.xtc.ui.widget.numberpicker.interfaces;

import com.xtc.ui.widget.numberpicker.view.NumberPickerView;

/** 滚轮滚动状态监听。 */
public interface OnScrollListener {
    int SCROLL_STATE_FLING = 2;
    int SCROLL_STATE_IDLE = 0;
    int SCROLL_STATE_TOUCH_SCROLL = 1;

    void onScrollStateChange(NumberPickerView picker, int scrollState);
}
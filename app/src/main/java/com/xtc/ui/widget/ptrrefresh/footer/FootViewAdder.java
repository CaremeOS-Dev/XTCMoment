package com.xtc.ui.widget.ptrrefresh.footer;

import android.view.View;

/** 向列表尾部追加视图的能力。 */
public interface FootViewAdder {
    View addFootView(int layoutResId);

    View addFootView(View view);
}
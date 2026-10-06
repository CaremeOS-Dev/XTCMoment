package com.xtc.ui.widget.dialog;

import android.content.Context;

import com.xtc.moment.R;

/** Variant of {@link DoubleFlatBtnWithTitleDialog} whose content area scrolls. */
public class ScrollableDoubleFlatBtnWithTitleDialog extends DoubleFlatBtnWithTitleDialog {

    private static final String TAG = "ScrollableDoubleFlatBtnWithTitleDialog";

    public ScrollableDoubleFlatBtnWithTitleDialog(Context context, boolean forbidSwipeToDismiss) {
        this(context, forbidSwipeToDismiss ? R.style.dialog_default_style_forbidSwipe : R.style.dialog_default_style);
    }

    public ScrollableDoubleFlatBtnWithTitleDialog(Context context, int themeResId) {
        super(context, themeResId, R.layout.layout_scrollable_double_flat_button_with_title_dialog);
    }
}

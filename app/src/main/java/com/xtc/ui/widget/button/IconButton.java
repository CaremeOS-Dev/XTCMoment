package com.xtc.ui.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiIconUtil;

/** Round icon button; defaults its size to {@code round_btn_size} when wrap_content. */
public class IconButton extends ImageView {

    private static final String TAG = "IconButton";

    public IconButton(Context context) {
        this(context, null);
    }

    public IconButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        checkSize();
    }

    private void checkSize() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = layoutParams.width;
        int height = layoutParams.height;
        LogUtil.d(TAG, "src size = ( " + width + " , " + height + " )");
        if (width == ViewGroup.LayoutParams.WRAP_CONTENT || height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int defaultSize = (int) getResources().getDimension(R.dimen.round_btn_size);
            LogUtil.w(TAG, "width or height is wrap_content , set default size = " + defaultSize);
            layoutParams.width = defaultSize;
            layoutParams.height = defaultSize;
        }
    }

    public IconButton setIcon(int iconConstant) {
        setImageResource(UiIconUtil.getIconResourceId(iconConstant));
        return this;
    }

    public IconButton setBtnBackground(int[] colorResArray) {
        return setBtnBackground(colorResArray, UiConstants.Color.MASK, 30);
    }

    public IconButton setBtnBackground(int[] colorResArray, int[] maskColorResArray, int radius) {
        setBackground(UiBgUtil.getGradientRoundStateDrawable(getContext(), colorResArray, maskColorResArray, radius));
        return this;
    }
}

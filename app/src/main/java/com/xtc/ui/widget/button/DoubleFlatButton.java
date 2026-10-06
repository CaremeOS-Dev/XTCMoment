package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;
import com.xtc.ui.widget.util.UiCommonUtil;

/**
 * Two flat (rounded-rect) buttons side by side; the left button gets a
 * small-corner radius on the inner edge and the right one a pill edge, which is
 * what gives the pair its "split capsule" look.
 */
public class DoubleFlatButton extends LinearLayout {

    private static final String TAG = "DoubleFlatButton";

    private boolean backgroundUpdated;
    private int[] leftBgColorIdArray;
    private int[] rightBgColorIdArray;
    private final int margin;
    private final TextView tvLeft;
    private final TextView tvRight;
    private final LinearLayout llRight;
    private final View loadingRight;

    public DoubleFlatButton(Context context) {
        this(context, null);
    }

    public DoubleFlatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleFlatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.backgroundUpdated = false;
        setGravity(16);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_double_flat_button, (ViewGroup) this, true);
        this.tvLeft = (TextView) findViewById(R.id.btn_left);
        this.tvRight = (TextView) findViewById(R.id.btn_right);
        this.llRight = (LinearLayout) findViewById(R.id.ll_right);
        this.loadingRight = findViewById(R.id.loading_right);
        this.margin = (int) getContext().getResources().getDimension(R.dimen.double_flat_btn_margin);
        int[] defaultLeft = UiConstants.Color.GRAY2;
        int[] defaultRight = UiConstants.Color.GREED;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleFlatButton);
        if (typedArray != null) {
            int leftIndex = typedArray.getInt(R.styleable.DoubleFlatButton_leftBgColorArray, 4);
            if (leftIndex == 0) {
                this.leftBgColorIdArray = UiConstants.Color.RED;
            } else if (leftIndex == 1) {
                this.leftBgColorIdArray = UiConstants.Color.GREED;
            } else if (leftIndex == 2) {
                this.leftBgColorIdArray = UiConstants.Color.WHITE;
            } else if (leftIndex == 3) {
                this.leftBgColorIdArray = UiConstants.Color.YELLOW;
            } else {
                this.leftBgColorIdArray = UiConstants.Color.GRAY;
            }
            int rightIndex = typedArray.getInt(R.styleable.DoubleFlatButton_rightBgColorArray, 1);
            if (rightIndex == 0) {
                this.rightBgColorIdArray = UiConstants.Color.RED;
            } else if (rightIndex == 2) {
                this.rightBgColorIdArray = UiConstants.Color.WHITE;
            } else if (rightIndex == 3) {
                this.rightBgColorIdArray = UiConstants.Color.YELLOW;
            } else if (rightIndex != 4) {
                this.rightBgColorIdArray = UiConstants.Color.GREED;
            } else {
                this.rightBgColorIdArray = UiConstants.Color.GRAY;
            }
            typedArray.recycle();
        } else {
            LogUtil.e(TAG, "TypedArray = null !");
            this.leftBgColorIdArray = defaultLeft;
            this.rightBgColorIdArray = defaultRight;
        }
        setClickable(false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed || !this.backgroundUpdated) {
            int[] size = computeOneButtonSize();
            int widthPx = size[0];
            int heightPx = size[1];
            int smallRadius = (int) getResources().getDimension(R.dimen.double_flat_btn_small_radii);
            int pillRadius = heightPx / 2;
            int[] leftColors = this.leftBgColorIdArray;
            if (leftColors == null) {
                leftColors = UiConstants.Color.GRAY2;
            }
            this.tvLeft.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(getContext(), widthPx, heightPx, pillRadius, smallRadius, leftColors));
            int[] rightColors = this.rightBgColorIdArray;
            if (rightColors == null) {
                rightColors = UiConstants.Color.GREED;
            }
            this.llRight.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(getContext(), widthPx, heightPx, smallRadius, pillRadius, rightColors));
            this.backgroundUpdated = true;
        }
    }

    public void setEntireSize(int widthPx, int heightPx) {
        LogUtil.i(TAG, "setEntireSize widthPx --> " + widthPx + " , heightPx = " + heightPx);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = widthPx;
        layoutParams.height = heightPx;
        setLayoutParams(layoutParams);
        LogUtil.d(TAG, "set one root LayoutParams done !");
        invalidBackground();
    }

    public void setLeftBgColorIdArray(int[] leftBgColorIdArray) {
        this.leftBgColorIdArray = leftBgColorIdArray;
        invalidBackground();
    }

    public void setRightBgColorIdArray(int[] rightBgColorIdArray) {
        this.rightBgColorIdArray = rightBgColorIdArray;
        invalidBackground();
    }

    private void invalidBackground() {
        this.backgroundUpdated = false;
        requestLayout();
        invalidate();
    }

    public TextView getLeftButton() {
        return this.tvLeft;
    }

    public TextView getRightButton() {
        return this.tvRight;
    }

    public View getLoadingRight() {
        return this.loadingRight;
    }

    public LinearLayout getRightArea() {
        return this.llRight;
    }

    public AnimationDrawable getDefaultSizeLoadingAnim(Context context) {
        return UiBgUtil.getLoadingAnimForBtn(context, 17.0f);
    }

    public void setRightLoadingVisible(boolean showLoading) {
        LogUtil.d(TAG, "setRightLoadingVisible = " + showLoading);
        if (showLoading) {
            UiCommonUtil.showView(this.loadingRight);
            UiCommonUtil.hideView(this.tvRight);
        } else {
            UiCommonUtil.hideView(this.loadingRight);
            UiCommonUtil.showView(this.tvRight);
        }
    }

    @Override
    @Deprecated
    public void setOnClickListener(View.OnClickListener listener) {
        LogUtil.w(TAG, "setOnClickListener 空实现 , 请 不要使用 此方法 !!!");
    }

    private int[] computeOneButtonSize() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int width = layoutParams.width;
        return new int[]{((width > 0 ? width : getWidth()) - this.margin) / 2, width > 0 ? layoutParams.height : getHeight()};
    }
}

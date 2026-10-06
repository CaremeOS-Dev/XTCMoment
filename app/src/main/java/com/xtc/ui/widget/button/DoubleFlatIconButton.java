package com.xtc.ui.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.util.UiBgUtil;

/** Icon variant of {@link DoubleFlatButton}: two image buttons side by side. */
public class DoubleFlatIconButton extends LinearLayout {

    private static final String TAG = "DoubleFlatIconButton";

    private boolean backgroundUpdated;
    private final ImageView ivLeft;
    private final ImageView ivRight;
    private int[] leftBgColorIdArray;
    private int[] rightBgColorIdArray;
    private final int margin;

    public DoubleFlatIconButton(Context context) {
        this(context, null);
    }

    public DoubleFlatIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleFlatIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.backgroundUpdated = false;
        setGravity(16);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.layout_double_flat_icon_button, (ViewGroup) this, true);
        this.ivLeft = (ImageView) findViewById(R.id.btn_left);
        this.ivRight = (ImageView) findViewById(R.id.btn_right);
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
            this.ivLeft.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(getContext(), widthPx, heightPx, pillRadius, smallRadius, leftColors));
            int[] rightColors = this.rightBgColorIdArray;
            if (rightColors == null) {
                rightColors = UiConstants.Color.GREED;
            }
            this.ivRight.setBackground(UiBgUtil.getGradientRoundRectStateDrawable(getContext(), widthPx, heightPx, smallRadius, pillRadius, rightColors));
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

    public ImageView getLeftButton() {
        return this.ivLeft;
    }

    public ImageView getRightButton() {
        return this.ivRight;
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

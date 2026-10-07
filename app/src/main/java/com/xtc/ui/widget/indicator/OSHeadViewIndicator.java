package com.xtc.ui.widget.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.moment.R;

/** 自绘的圆角条状页面指示器，选中项更宽且为白色。 */
public class OSHeadViewIndicator extends View {
    private static final String TAG = "OSHeadViewIndicator";
    private final int defaultColor;
    private final int defaultWidth;
    private final int gapWidth;
    private final int itemHeight;
    private int mCount;
    private int mIndex;
    private final Paint paint;
    private final RectF rectF;
    private final float rx;
    private final float ry;
    private final int selectWidth;
    private final int top;

    public OSHeadViewIndicator(Context context, int index, int count) {
        this(context, null);
        updateIndicator(index, count);
    }

    public OSHeadViewIndicator(Context context, AttributeSet attrs, int index, int count) {
        this(context, attrs);
        updateIndicator(index, count);
    }

    public OSHeadViewIndicator(Context context) {
        this(context, null);
    }

    public OSHeadViewIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OSHeadViewIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources resources = getResources();
        setLayoutParams(new ViewGroup.LayoutParams(-1, resources.getDimensionPixelSize(R.dimen.dp_9)));
        this.paint = new Paint(1);
        this.selectWidth = resources.getDimensionPixelSize(R.dimen.dp_9);
        this.defaultWidth = resources.getDimensionPixelSize(R.dimen.dp_6);
        this.gapWidth = resources.getDimensionPixelSize(R.dimen.dp_4);
        this.top = resources.getDimensionPixelSize(R.dimen.dp_4);
        this.itemHeight = resources.getDimensionPixelSize(R.dimen.dp_4);
        float cornerRadius = resources.getDimensionPixelSize(R.dimen.dp_2);
        this.ry = cornerRadius;
        this.rx = cornerRadius;
        this.defaultColor = resources.getColor(R.color.color_66ffffff);
        this.rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.mCount < 1) {
            return;
        }
        float left = (getWidth() - (((this.mCount - 1) * (this.gapWidth + this.defaultWidth)) + this.selectWidth)) / 2.0f;
        int index = 0;
        while (index < this.mCount) {
            boolean selected = index == this.mIndex;
            this.paint.setColor(selected ? -1 : this.defaultColor);
            int itemWidth = selected ? this.selectWidth : this.defaultWidth;
            RectF rectF = this.rectF;
            int top = this.top;
            rectF.set(left, top, itemWidth + left, top + this.itemHeight);
            canvas.drawRoundRect(this.rectF, this.rx, this.ry, this.paint);
            left += itemWidth + this.gapWidth;
            index++;
        }
    }

    public void updateIndicator(int index, int count) {
        this.mIndex = index;
        this.mCount = count;
        invalidate();
    }
}
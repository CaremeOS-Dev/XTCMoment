package com.xtc.ui.widget.toggleswitch;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** 自绘的简易开关按钮：圆形滑块 + 圆角背景。 */
public class ToggleButton extends View {
    // 反编译常量：0xFF888888 与 0xFF00FF00
    private static final int DEFAULT_NORMAL_COLOR = -7829368;
    private static final int DEFAULT_SELECT_COLOR = -16711936;
    private ValueAnimator animatorClose;
    private ValueAnimator animatorOpen;
    private boolean choice;
    private float[] destination;
    private boolean inner;
    private float mCircleRadio;
    private int mHeight;
    private boolean mIsSelect;
    private float mRoundHeight;
    private int mRoundNormalColor;
    private int mRoundSelectColor;
    private float mRoundWidth;
    private int mWidth;
    private Matrix matrix;
    private Paint paintCircle;
    private Paint paintRoundNormal;
    private Paint paintRoundSelect;
    private Path pathCircle;
    private Path pathRoundNormal;
    private Path pathRoundSelect;
    private Region regionClick;
    private Region regionClip;
    private float varySet;

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRoundWidth = 72.0f;
        this.mRoundHeight = 44.0f;
        this.mCircleRadio = 20.0f;
        this.choice = false;
        this.inner = false;
        this.destination = new float[2];
        this.mIsSelect = true;
        this.mRoundNormalColor = DEFAULT_NORMAL_COLOR;
        this.mRoundSelectColor = DEFAULT_SELECT_COLOR;
        initPaint();
        initValueAnimator();
        this.matrix = new Matrix();
    }

    private void initPaint() {
        this.paintCircle = new Paint();
        this.paintCircle.setAntiAlias(true);
        this.paintCircle.setStyle(Paint.Style.FILL);
        this.paintCircle.setColor(-1);
        this.paintRoundNormal = new Paint();
        this.paintRoundNormal.setAntiAlias(true);
        this.paintRoundNormal.setStyle(Paint.Style.FILL);
        this.paintRoundNormal.setColor(DEFAULT_NORMAL_COLOR);
        this.paintRoundSelect = new Paint();
        this.paintRoundSelect.setStyle(Paint.Style.FILL);
        this.paintRoundSelect.setAntiAlias(true);
        this.paintRoundSelect.setColor(DEFAULT_SELECT_COLOR);
    }

    public void setSelect(boolean select) {
        this.mIsSelect = select;
    }

    public boolean isSelect() {
        return this.mIsSelect;
    }

    private void initPath() {
        this.pathCircle = new Path();
        this.pathRoundNormal = new Path();
        this.pathRoundSelect = new Path();
        float circleCenterX = (((this.mRoundWidth / 2.0f) - this.mCircleRadio) - 1.0f) - this.varySet;
        if (!this.mIsSelect) {
            circleCenterX = -circleCenterX;
        }
        if (this.mIsSelect) {
            this.paintRoundNormal.setColor(DEFAULT_SELECT_COLOR);
            this.paintRoundSelect.setColor(DEFAULT_NORMAL_COLOR);
        } else {
            this.paintRoundNormal.setColor(DEFAULT_NORMAL_COLOR);
            this.paintRoundSelect.setColor(DEFAULT_SELECT_COLOR);
        }
        this.pathCircle.addCircle(circleCenterX, 0.0f, this.mCircleRadio, Path.Direction.CCW);
        Path normalPath = this.pathRoundNormal;
        float roundWidth = this.mRoundWidth;
        float varySet = this.varySet;
        float roundHeight = this.mRoundHeight;
        RectF normalBounds = new RectF(((-roundWidth) / 2.0f) + varySet, ((-roundHeight) / 2.0f) + (varySet / 2.0f),
                (roundWidth / 2.0f) - varySet, (roundHeight / 2.0f) - (varySet / 2.0f));
        float circleRadius = this.mCircleRadio;
        normalPath.addRoundRect(normalBounds, circleRadius, circleRadius, Path.Direction.CW);
        Path selectPath = this.pathRoundSelect;
        float selectWidth = this.mRoundWidth;
        float selectHeight = this.mRoundHeight;
        RectF selectBounds = new RectF((-selectWidth) / 2.0f, (-selectHeight) / 2.0f, selectWidth / 2.0f, selectHeight / 2.0f);
        float selectRadius = this.mCircleRadio;
        selectPath.addRoundRect(selectBounds, selectRadius, selectRadius, Path.Direction.CW);
        this.regionClick = new Region();
        this.regionClick.setPath(this.pathRoundSelect, this.regionClip);
    }

    private void initValueAnimator() {
        this.animatorOpen = ValueAnimator.ofFloat(0.0f, (this.mRoundWidth - 2.0f) / 2.0f);
        this.animatorOpen.setRepeatCount(0);
        this.animatorOpen.setDuration(500L);
        this.animatorOpen.setInterpolator(new LinearInterpolator());
        this.animatorOpen.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ToggleButton.this.varySet = ((Float) animation.getAnimatedValue()).floatValue();
                ToggleButton.this.invalidate();
            }
        });
        this.animatorClose = ValueAnimator.ofFloat((this.mRoundWidth - 2.0f) / 2.0f, 0.0f);
        this.animatorClose.setRepeatCount(0);
        this.animatorClose.setDuration(500L);
        this.animatorClose.setInterpolator(new LinearInterpolator());
        this.animatorClose.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ToggleButton.this.varySet = ((Float) animation.getAnimatedValue()).floatValue();
                ToggleButton.this.invalidate();
            }
        });
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.mHeight = height;
        this.mWidth = width;
        int currentWidth = this.mWidth;
        int currentHeight = this.mHeight;
        this.regionClip = new Region(-currentWidth, -currentHeight, currentWidth, currentHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(this.mWidth / 2, this.mHeight / 2);
        initPath();
        this.matrix.reset();
        if (this.matrix.isIdentity()) {
            canvas.getMatrix().invert(this.matrix);
        }
        drawPath(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            this.destination[0] = event.getX();
            this.destination[1] = event.getY();
            this.matrix.mapPoints(this.destination);
            Region region = this.regionClick;
            float[] point = this.destination;
            this.inner = region.contains((int) point[0], (int) point[1]);
        } else if (action == 1) {
            this.destination[0] = event.getX();
            this.destination[1] = event.getY();
            this.matrix.mapPoints(this.destination);
            if (this.inner) {
                Region region = this.regionClick;
                float[] point = this.destination;
                if (region.contains((int) point[0], (int) point[1])) {
                    if (this.animatorOpen.isRunning()) {
                        this.animatorOpen.cancel();
                    }
                    if (this.animatorClose.isRunning()) {
                        this.animatorClose.cancel();
                    }
                    this.choice = !this.choice;
                    if (this.choice) {
                        this.animatorOpen.start();
                    } else {
                        this.animatorClose.start();
                    }
                }
            }
        }
        return true;
    }

    private void drawPath(Canvas canvas) {
        canvas.drawPath(this.pathRoundSelect, this.paintRoundSelect);
        canvas.drawPath(this.pathRoundNormal, this.paintRoundNormal);
        canvas.drawPath(this.pathCircle, this.paintCircle);
    }
}
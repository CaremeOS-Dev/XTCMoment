package com.xtc.ui.widget.toggleswitch;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import com.xtc.ui.widget.R;
import com.xtc.ui.widget.util.UiCommonUtil;

/** 带动画与拖拽交互的开关按钮。 */
public class SwitchButton extends View implements Checkable {
    private final int ANIMATE_STATE_DRAGGING;
    private final int ANIMATE_STATE_NONE;
    private final int ANIMATE_STATE_PENDING_DRAG;
    private final int ANIMATE_STATE_PENDING_RESET;
    private final int ANIMATE_STATE_PENDING_SETTLE;
    private final int ANIMATE_STATE_SWITCH;
    private ViewState afterState;
    private int animateState;
    private Animator.AnimatorListener animatorListener;
    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener;
    private final ArgbEvaluator argbEvaluator;
    private int background;
    private ViewState beforeState;
    private int borderWidth;
    private float bottom;
    private float buttonBottomMargin;
    private float buttonMaxX;
    private float buttonMinX;
    private float buttonRadius;
    private float buttonTopMargin;
    private float centerX;
    private float centerY;
    private int checkedColor;
    private Paint circlePaint;
    private boolean enableAnimationEffect;
    private boolean enableShadowEffect;
    private float height;
    private boolean isChecked;
    private boolean isTouchingDown;
    private boolean isUiInitialize;
    private float left;
    private int mDefaultHeight;
    private int mDefaultWidth;
    private OnToggleListener mOnToggleListener;
    private OnCheckedChangeListener onCheckedChangeListener;
    private Runnable postPendingDrag;
    private RectF rect;
    private float right;
    private Paint roundPaint;
    private int shadowColor;
    private int shadowOffset;
    private int shadowRadius;
    private float top;
    private long touchDownTime;
    private int uncheckColor;
    private ValueAnimator valueAnimator;
    private float viewRadius;
    private ViewState viewState;
    private float width;

    /** 选中状态变化回调。 */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(SwitchButton switchButton, boolean isChecked);
    }

    /** 开关切换回调。 */
    public interface OnToggleListener {
        void onToggle(SwitchButton switchButton);
    }

    /** 开关的可动画绘制状态。 */
    private static class ViewState {
        int checkStateColor;
        float circleX;
        float radius;

        ViewState() {
        }

        private void copy(ViewState viewState) {
            this.circleX = viewState.circleX;
            this.checkStateColor = viewState.checkStateColor;
            this.radius = viewState.radius;
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Deprecated
    public void setOnToggleListener(OnToggleListener onToggleListener) {
        this.mOnToggleListener = onToggleListener;
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ANIMATE_STATE_NONE = 0;
        this.ANIMATE_STATE_PENDING_DRAG = 1;
        this.ANIMATE_STATE_DRAGGING = 2;
        this.ANIMATE_STATE_PENDING_RESET = 3;
        this.ANIMATE_STATE_PENDING_SETTLE = 4;
        this.ANIMATE_STATE_SWITCH = 5;
        this.rect = new RectF();
        this.animateState = 0;
        this.argbEvaluator = new ArgbEvaluator();
        this.isTouchingDown = false;
        this.isUiInitialize = false;
        this.postPendingDrag = new Runnable() {
            @Override
            public void run() {
                if (SwitchButton.this.isAnimating()) {
                    return;
                }
                SwitchButton.this.pendingDragState();
            }
        };
        this.animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = ((Float) animation.getAnimatedValue()).floatValue();
                int state = SwitchButton.this.animateState;
                if (state != 0) {
                    if (state == 1) {
                        SwitchButton.this.viewState.radius = SwitchButton.this.beforeState.radius
                                + ((SwitchButton.this.afterState.radius - SwitchButton.this.beforeState.radius) * fraction);
                        if (SwitchButton.this.animateState != 1) {
                            SwitchButton.this.viewState.circleX = SwitchButton.this.beforeState.circleX
                                    + ((SwitchButton.this.afterState.circleX - SwitchButton.this.beforeState.circleX) * fraction);
                        }
                        SwitchButton.this.viewState.checkStateColor = ((Integer) SwitchButton.this.argbEvaluator.evaluate(fraction,
                                Integer.valueOf(SwitchButton.this.beforeState.checkStateColor),
                                Integer.valueOf(SwitchButton.this.afterState.checkStateColor))).intValue();
                    } else if (state != 2) {
                        if (state == 3 || state == 4) {
                            SwitchButton.this.viewState.radius = SwitchButton.this.beforeState.radius
                                    + ((SwitchButton.this.afterState.radius - SwitchButton.this.beforeState.radius) * fraction);
                            if (SwitchButton.this.animateState != 1) {
                                SwitchButton.this.viewState.circleX = SwitchButton.this.beforeState.circleX
                                        + ((SwitchButton.this.afterState.circleX - SwitchButton.this.beforeState.circleX) * fraction);
                            }
                            SwitchButton.this.viewState.checkStateColor = ((Integer) SwitchButton.this.argbEvaluator.evaluate(fraction,
                                    Integer.valueOf(SwitchButton.this.beforeState.checkStateColor),
                                    Integer.valueOf(SwitchButton.this.afterState.checkStateColor))).intValue();
                        } else if (state == 5) {
                            SwitchButton.this.viewState.circleX = SwitchButton.this.beforeState.circleX
                                    + ((SwitchButton.this.afterState.circleX - SwitchButton.this.beforeState.circleX) * fraction);
                            float percent = (SwitchButton.this.viewState.circleX - SwitchButton.this.buttonMinX)
                                    / (SwitchButton.this.buttonMaxX - SwitchButton.this.buttonMinX);
                            SwitchButton.this.viewState.checkStateColor = ((Integer) SwitchButton.this.argbEvaluator.evaluate(percent,
                                    Integer.valueOf(SwitchButton.this.uncheckColor),
                                    Integer.valueOf(SwitchButton.this.checkedColor))).intValue();
                            SwitchButton.this.viewState.radius = percent * SwitchButton.this.viewRadius;
                        }
                    }
                }
                SwitchButton.this.postInvalidate();
            }
        };
        this.animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int state = SwitchButton.this.animateState;
                if (state != 0) {
                    if (state == 1) {
                        SwitchButton.this.animateState = 2;
                        SwitchButton.this.viewState.radius = SwitchButton.this.viewRadius;
                        SwitchButton.this.postInvalidate();
                        return;
                    }
                    if (state != 2) {
                        if (state == 3) {
                            SwitchButton.this.animateState = 0;
                            SwitchButton.this.postInvalidate();
                        } else if (state == 4) {
                            SwitchButton.this.animateState = 0;
                            SwitchButton.this.postInvalidate();
                        } else if (state == 5) {
                            SwitchButton.this.animateState = 0;
                            SwitchButton.this.postInvalidate();
                        }
                    }
                }
            }
        };
        init(context, attrs);
    }

    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();
        this.mDefaultWidth = (int) resources.getDimension(R.dimen.item_click_scale_switch_width);
        this.mDefaultHeight = (int) resources.getDimension(R.dimen.item_click_scale_switch_height);
        this.buttonBottomMargin = (int) resources.getDimension(R.dimen.item_click_scale_switch_margin);
        this.buttonTopMargin = (int) resources.getDimension(R.dimen.item_click_scale_switch_margin);
        TypedArray attributes = attrs != null ? context.obtainStyledAttributes(attrs, R.styleable.SwitchButton) : null;
        this.enableShadowEffect = optBoolean(attributes, R.styleable.SwitchButton_enable_shadow_effect, false);
        this.shadowRadius = optPixelSize(attributes, R.styleable.SwitchButton_shadow_radius, UiCommonUtil.dp2Px(getContext(), 2.5f));
        this.shadowOffset = optPixelSize(attributes, R.styleable.SwitchButton_shadow_offset, UiCommonUtil.dp2Px(getContext(), 1.5f));
        this.shadowColor = optColor(attributes, R.styleable.SwitchButton_shadow_color, -16777216);
        this.uncheckColor = optColor(attributes, R.styleable.SwitchButton_uncheck_color, UiCommonUtil.getColor(getContext(), R.color.color_9ca0a9));
        this.checkedColor = optColor(attributes, R.styleable.SwitchButton_checked_color, UiCommonUtil.getColor(getContext(), R.color.color_00de71));
        this.background = optColor(attributes, R.styleable.SwitchButton_background_color, UiCommonUtil.getColor(getContext(), R.color.color_9ca0a9));
        this.borderWidth = optPixelSize(attributes, R.styleable.SwitchButton_border_width, UiCommonUtil.dp2Px(getContext(), 0.0f));
        int circleColor = optColor(attributes, R.styleable.SwitchButton_circle_button_color, UiCommonUtil.getColor(getContext(), R.color.color_ffffff));
        int animationDuration = optInt(attributes, R.styleable.SwitchButton_animation_duration, 300);
        this.enableAnimationEffect = optBoolean(attributes, R.styleable.SwitchButton_enable_animation_effect, false);
        this.isChecked = optBoolean(attributes, R.styleable.SwitchButton_enable_checked, true);
        if (attributes != null) {
            attributes.recycle();
        }
        this.roundPaint = new Paint(1);
        this.circlePaint = new Paint(1);
        this.circlePaint.setColor(circleColor);
        if (this.enableShadowEffect) {
            this.circlePaint.setShadowLayer(this.shadowRadius, 0.0f, this.shadowOffset, this.shadowColor);
        }
        this.viewState = new ViewState();
        this.beforeState = new ViewState();
        this.afterState = new ViewState();
        this.valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.valueAnimator.setDuration(animationDuration);
        this.valueAnimator.setRepeatCount(0);
        this.valueAnimator.addUpdateListener(this.animatorUpdateListener);
        this.valueAnimator.addListener(this.animatorListener);
        super.setClickable(true);
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == 0 || widthMode == Integer.MIN_VALUE) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mDefaultWidth, 1073741824);
        }
        if (heightMode == 0 || heightMode == Integer.MIN_VALUE) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mDefaultHeight, 1073741824);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (!this.enableShadowEffect) {
            this.shadowRadius = 0;
            this.shadowOffset = 0;
        }
        float maxShadow = Math.max(this.shadowRadius + this.shadowOffset, this.borderWidth);
        this.mDefaultHeight = height;
        this.mDefaultWidth = width;
        float heightF = height;
        float shadowInset = 2.0f * maxShadow;
        this.height = heightF - shadowInset;
        float widthF = width;
        this.width = widthF - shadowInset;
        this.viewRadius = this.height * 0.5f;
        float viewRadius = this.viewRadius;
        this.buttonRadius = ((viewRadius - shadowInset) - this.buttonBottomMargin) - this.buttonTopMargin;
        this.left = maxShadow;
        this.top = maxShadow;
        this.right = widthF - maxShadow;
        this.bottom = heightF - maxShadow;
        float left = this.left;
        float right = this.right;
        this.centerX = (left + right) * 0.5f;
        this.centerY = (this.top + this.bottom) * 0.5f;
        this.buttonMinX = left + viewRadius;
        this.buttonMaxX = right - viewRadius;
        if (isChecked()) {
            setCheckedViewState(this.viewState);
        } else {
            setUncheckViewState(this.viewState);
        }
        this.isUiInitialize = true;
        postInvalidate();
    }

    private void setUncheckViewState(ViewState viewState) {
        viewState.radius = 0.0f;
        viewState.checkStateColor = this.uncheckColor;
        viewState.circleX = this.buttonMinX;
        OnCheckedChangeListener listener = this.onCheckedChangeListener;
        if (listener != null) {
            listener.onCheckedChanged(this, false);
        }
    }

    private void setCheckedViewState(ViewState viewState) {
        viewState.radius = this.viewRadius;
        viewState.checkStateColor = this.checkedColor;
        viewState.circleX = this.buttonMaxX;
        OnCheckedChangeListener listener = this.onCheckedChangeListener;
        if (listener != null) {
            listener.onCheckedChanged(this, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.roundPaint.setStyle(Paint.Style.FILL);
        this.roundPaint.setColor(this.viewState.checkStateColor);
        this.roundPaint.setStrokeWidth(this.borderWidth);
        drawRoundRect(canvas, this.left, this.top, this.right, this.bottom, this.viewRadius, this.roundPaint);
        drawCircleButton(canvas, this.viewState.circleX, this.centerY);
    }

    private void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom, float radius, Paint paint) {
        if (Build.VERSION.SDK_INT >= 21) {
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, paint);
        } else {
            this.rect.set(left, top, right, bottom);
            canvas.drawRoundRect(this.rect, radius, radius, paint);
        }
    }

    private void drawCircleButton(Canvas canvas, float centerX, float centerY) {
        canvas.drawCircle(centerX, centerY, this.buttonRadius, this.circlePaint);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == isChecked()) {
            return;
        }
        toggle(this.enableAnimationEffect);
    }

    @Override
    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public void toggle() {
        toggle(true);
        OnToggleListener listener = this.mOnToggleListener;
        if (listener != null) {
            listener.onToggle(this);
        }
    }

    private void toggle(boolean withAnimation) {
        if (isEnabled()) {
            if (!this.isUiInitialize) {
                this.isChecked = !this.isChecked;
                return;
            }
            if (this.valueAnimator.isRunning()) {
                this.valueAnimator.cancel();
            }
            if (!this.enableAnimationEffect || !withAnimation) {
                if (isChecked()) {
                    setUncheckViewState(this.viewState);
                } else {
                    setCheckedViewState(this.viewState);
                }
                this.isChecked = !this.isChecked;
                postInvalidate();
                return;
            }
            this.animateState = 5;
            this.beforeState.copy(this.viewState);
            if (isChecked()) {
                setUncheckViewState(this.afterState);
            } else {
                setCheckedViewState(this.afterState);
            }
            this.valueAnimator.start();
            this.isChecked = !this.isChecked;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.isTouchingDown = true;
            this.touchDownTime = System.currentTimeMillis();
            removeCallbacks(this.postPendingDrag);
            postDelayed(this.postPendingDrag, 100L);
        } else if (actionMasked == 1) {
            this.isTouchingDown = false;
            removeCallbacks(this.postPendingDrag);
            if (System.currentTimeMillis() - this.touchDownTime <= 300) {
                toggle();
            } else if (isDragState()) {
                boolean shouldCheck = Math.max(0.0f, Math.min(1.0f, event.getX() / ((float) getWidth()))) > 0.5f;
                if (shouldCheck == isChecked()) {
                    pendingCancelDragState();
                } else {
                    this.isChecked = shouldCheck;
                    pendingSettleState();
                    OnToggleListener listener = this.mOnToggleListener;
                    if (listener != null) {
                        listener.onToggle(this);
                    }
                }
            } else if (isPendingDragState()) {
                pendingCancelDragState();
            }
        } else if (actionMasked == 2) {
            float x = event.getX();
            if (isPendingDragState()) {
                float percent = Math.max(0.0f, Math.min(1.0f, x / getWidth()));
                ViewState viewState = this.viewState;
                float minX = this.buttonMinX;
                viewState.circleX = minX + ((this.buttonMaxX - minX) * percent);
            } else if (isDragState()) {
                float percent = Math.max(0.0f, Math.min(1.0f, x / getWidth()));
                ViewState viewState = this.viewState;
                float minX = this.buttonMinX;
                viewState.circleX = minX + ((this.buttonMaxX - minX) * percent);
                viewState.checkStateColor = ((Integer) this.argbEvaluator.evaluate(percent,
                        Integer.valueOf(this.uncheckColor), Integer.valueOf(this.checkedColor))).intValue();
                postInvalidate();
            }
        } else if (actionMasked == 3) {
            this.isTouchingDown = false;
            removeCallbacks(this.postPendingDrag);
            if (isPendingDragState() || isDragState()) {
                pendingCancelDragState();
            }
        }
        return true;
    }

    private boolean isAnimating() {
        return this.animateState != 0;
    }

    private boolean isPendingDragState() {
        int state = this.animateState;
        return state == 1 || state == 3;
    }

    private boolean isDragState() {
        return this.animateState == 2;
    }

    @Deprecated
    public void setEnableShadowEffect(boolean enableShadowEffect) {
        if (this.enableShadowEffect == enableShadowEffect) {
            return;
        }
        this.enableShadowEffect = enableShadowEffect;
        if (this.enableShadowEffect) {
            this.circlePaint.setShadowLayer(this.shadowRadius, 0.0f, this.shadowOffset, this.shadowColor);
        } else {
            this.circlePaint.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        }
    }

    @Deprecated
    public void setEnableAnimationEffect(boolean enableAnimationEffect) {
        this.enableAnimationEffect = enableAnimationEffect;
    }

    private void pendingDragState() {
        if (!isAnimating() && this.isTouchingDown) {
            if (this.valueAnimator.isRunning()) {
                this.valueAnimator.cancel();
            }
            this.animateState = 1;
            this.beforeState.copy(this.viewState);
            this.afterState.copy(this.viewState);
            if (isChecked()) {
                ViewState afterState = this.afterState;
                afterState.checkStateColor = this.checkedColor;
                afterState.circleX = this.buttonMaxX;
            } else {
                ViewState afterState = this.afterState;
                afterState.checkStateColor = this.uncheckColor;
                afterState.circleX = this.buttonMinX;
                afterState.radius = this.viewRadius;
            }
            this.valueAnimator.start();
        }
    }

    private void pendingCancelDragState() {
        if (isDragState() || isPendingDragState()) {
            if (this.valueAnimator.isRunning()) {
                this.valueAnimator.cancel();
            }
            this.animateState = 3;
            this.beforeState.copy(this.viewState);
            if (isChecked()) {
                setCheckedViewState(this.afterState);
            } else {
                setUncheckViewState(this.afterState);
            }
            this.valueAnimator.start();
        }
    }

    private void pendingSettleState() {
        if (this.valueAnimator.isRunning()) {
            this.valueAnimator.cancel();
        }
        this.animateState = 4;
        this.beforeState.copy(this.viewState);
        if (isChecked()) {
            setCheckedViewState(this.afterState);
        } else {
            setUncheckViewState(this.afterState);
        }
        this.valueAnimator.start();
    }

    private int optInt(TypedArray attributes, int index, int defaultValue) {
        return attributes == null ? defaultValue : attributes.getInt(index, defaultValue);
    }

    private int optPixelSize(TypedArray attributes, int index, int defaultValue) {
        return attributes == null ? defaultValue : attributes.getDimensionPixelOffset(index, defaultValue);
    }

    private int optColor(TypedArray attributes, int index, int defaultValue) {
        return attributes == null ? defaultValue : attributes.getColor(index, defaultValue);
    }

    private boolean optBoolean(TypedArray attributes, int index, boolean defaultValue) {
        return attributes == null ? defaultValue : attributes.getBoolean(index, defaultValue);
    }
}
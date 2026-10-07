package com.github.chrisbanes.photoview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
class CustomGestureDetector {
    private static final int a = -1;
    private int b = -1;
    private int c = 0;
    private final ScaleGestureDetector d;
    private VelocityTracker e;
    private boolean f;
    private float g;
    private float h;
    private final float i;
    private final float j;
    private OnGestureListener k;

    CustomGestureDetector(Context context, OnGestureListener onGestureListener) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.j = viewConfiguration.getScaledMinimumFlingVelocity();
        this.i = viewConfiguration.getScaledTouchSlop();
        this.k = onGestureListener;
        this.d = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() { // from class: com.github.chrisbanes.photoview.CustomGestureDetector.1
            @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
                return true;
            }

            @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            }

            @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float scaleFactor = scaleGestureDetector.getScaleFactor();
                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }
                CustomGestureDetector.this.k.a(scaleFactor, scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
                return true;
            }
        });
    }

    private float b(MotionEvent motionEvent) {
        try {
            return motionEvent.getX(this.c);
        } catch (Exception unused) {
            return motionEvent.getX();
        }
    }

    private float c(MotionEvent motionEvent) {
        try {
            return motionEvent.getY(this.c);
        } catch (Exception unused) {
            return motionEvent.getY();
        }
    }

    public boolean a() {
        return this.d.isInProgress();
    }

    public boolean b() {
        return this.f;
    }

    public boolean a(MotionEvent motionEvent) {
        try {
            this.d.onTouchEvent(motionEvent);
            return d(motionEvent);
        } catch (IllegalArgumentException unused) {
            return true;
        }
    }

    private boolean d(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 0) {
            this.b = motionEvent.getPointerId(0);
            this.e = VelocityTracker.obtain();
            VelocityTracker velocityTracker = this.e;
            if (velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
            }
            this.g = b(motionEvent);
            this.h = c(motionEvent);
            this.f = false;
        } else if (action == 1) {
            this.b = -1;
            if (this.f && this.e != null) {
                this.g = b(motionEvent);
                this.h = c(motionEvent);
                this.e.addMovement(motionEvent);
                this.e.computeCurrentVelocity(1000);
                float xVelocity = this.e.getXVelocity();
                float yVelocity = this.e.getYVelocity();
                if (Math.max(Math.abs(xVelocity), Math.abs(yVelocity)) >= this.j) {
                    this.k.a(this.g, this.h, -xVelocity, -yVelocity);
                }
            }
            VelocityTracker velocityTracker2 = this.e;
            if (velocityTracker2 != null) {
                velocityTracker2.recycle();
                this.e = null;
            }
        } else if (action == 2) {
            float fB = b(motionEvent);
            float fC = c(motionEvent);
            float f = fB - this.g;
            float f2 = fC - this.h;
            if (!this.f) {
                this.f = Math.sqrt((double) ((f * f) + (f2 * f2))) >= ((double) this.i);
            }
            if (this.f) {
                this.k.a(f, f2);
                this.g = fB;
                this.h = fC;
                VelocityTracker velocityTracker3 = this.e;
                if (velocityTracker3 != null) {
                    velocityTracker3.addMovement(motionEvent);
                }
            }
        } else if (action == 3) {
            this.b = -1;
            VelocityTracker velocityTracker4 = this.e;
            if (velocityTracker4 != null) {
                velocityTracker4.recycle();
                this.e = null;
            }
        } else if (action == 6) {
            int iA = Util.a(motionEvent.getAction());
            if (motionEvent.getPointerId(iA) == this.b) {
                int i = iA == 0 ? 1 : 0;
                this.b = motionEvent.getPointerId(i);
                this.g = motionEvent.getX(i);
                this.h = motionEvent.getY(i);
            }
        }
        int i2 = this.b;
        if (i2 == -1) {
            i2 = 0;
        }
        this.c = motionEvent.findPointerIndex(i2);
        return true;
    }
}

package com.github.chrisbanes.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;

/* JADX INFO: loaded from: classes.dex */
public class PhotoViewAttacher implements View.OnLayoutChangeListener, View.OnTouchListener, OnGestureListener {
    private static float a = 3.0f;
    private static float b = 1.75f;
    private static float c = 1.0f;
    private static int d = 200;
    private static final int e = -1;
    private static final int f = 0;
    private static final int g = 1;
    private static final int h = 2;
    private static int i = 1;
    private OnOutsidePhotoTapListener A;
    private View.OnClickListener B;
    private View.OnLongClickListener C;
    private OnScaleChangedListener D;
    private OnSingleFlingListener E;
    private FlingRunnable F;
    private float H;
    private ImageView q;
    private GestureDetector r;
    private CustomGestureDetector s;
    private OnMatrixChangedListener y;
    private OnPhotoTapListener z;
    private Interpolator j = new AccelerateDecelerateInterpolator();
    private int k = d;
    private float l = c;
    private float m = b;
    private float n = a;
    private boolean o = true;
    private boolean p = false;
    private final Matrix t = new Matrix();
    private final Matrix u = new Matrix();
    private final Matrix v = new Matrix();
    private final RectF w = new RectF();
    private final float[] x = new float[9];
    private int G = 2;
    private boolean I = true;
    private ImageView.ScaleType J = ImageView.ScaleType.FIT_CENTER;

    public PhotoViewAttacher(ImageView imageView) {
        this.q = imageView;
        imageView.setOnTouchListener(this);
        imageView.addOnLayoutChangeListener(this);
        if (imageView.isInEditMode()) {
            return;
        }
        this.H = 0.0f;
        this.s = new CustomGestureDetector(imageView.getContext(), this);
        this.r = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.github.chrisbanes.photoview.PhotoViewAttacher.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                if (PhotoViewAttacher.this.C != null) {
                    PhotoViewAttacher.this.C.onLongClick(PhotoViewAttacher.this.q);
                }
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f2, float f3) {
                if (PhotoViewAttacher.this.E == null || PhotoViewAttacher.this.f() > PhotoViewAttacher.c || MotionEventCompat.getPointerCount(motionEvent) > PhotoViewAttacher.i || MotionEventCompat.getPointerCount(motionEvent2) > PhotoViewAttacher.i) {
                    return false;
                }
                return PhotoViewAttacher.this.E.a(motionEvent, motionEvent2, f2, f3);
            }
        });
        this.r.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() { // from class: com.github.chrisbanes.photoview.PhotoViewAttacher.2
            @Override // android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTapEvent(MotionEvent motionEvent) {
                return false;
            }

            @Override // android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (PhotoViewAttacher.this.B != null) {
                    PhotoViewAttacher.this.B.onClick(PhotoViewAttacher.this.q);
                }
                RectF rectFB = PhotoViewAttacher.this.b();
                if (rectFB == null) {
                    return false;
                }
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                if (!rectFB.contains(x, y)) {
                    if (PhotoViewAttacher.this.A == null) {
                        return false;
                    }
                    PhotoViewAttacher.this.A.a(PhotoViewAttacher.this.q);
                    return false;
                }
                float fWidth = (x - rectFB.left) / rectFB.width();
                float fHeight = (y - rectFB.top) / rectFB.height();
                if (PhotoViewAttacher.this.z == null) {
                    return true;
                }
                PhotoViewAttacher.this.z.onPhotoTap(PhotoViewAttacher.this.q, fWidth, fHeight);
                return true;
            }

            @Override // android.view.GestureDetector.OnDoubleTapListener
            public boolean onDoubleTap(MotionEvent motionEvent) {
                try {
                    float f2 = PhotoViewAttacher.this.f();
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    if (f2 < PhotoViewAttacher.this.d()) {
                        PhotoViewAttacher.this.a(PhotoViewAttacher.this.d(), x, y, true);
                    } else if (f2 >= PhotoViewAttacher.this.d() && f2 < PhotoViewAttacher.this.e()) {
                        PhotoViewAttacher.this.a(PhotoViewAttacher.this.e(), x, y, true);
                    } else {
                        PhotoViewAttacher.this.a(PhotoViewAttacher.this.c(), x, y, true);
                    }
                } catch (ArrayIndexOutOfBoundsException unused) {
                }
                return true;
            }
        });
    }

    public void a(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.r.setOnDoubleTapListener(onDoubleTapListener);
    }

    public void a(OnScaleChangedListener onScaleChangedListener) {
        this.D = onScaleChangedListener;
    }

    public void a(OnSingleFlingListener onSingleFlingListener) {
        this.E = onSingleFlingListener;
    }

    public boolean a() {
        return this.I;
    }

    public RectF b() {
        o();
        return e(l());
    }

    public boolean a(Matrix matrix) {
        if (matrix == null) {
            throw new IllegalArgumentException("Matrix cannot be null");
        }
        if (this.q.getDrawable() == null) {
            return false;
        }
        this.v.set(matrix);
        d(l());
        o();
        return true;
    }

    public void a(float f2) {
        this.H = f2 % 360.0f;
        h();
        c(this.H);
        n();
    }

    public void b(float f2) {
        this.v.setRotate(f2 % 360.0f);
        n();
    }

    public void c(float f2) {
        this.v.postRotate(f2 % 360.0f);
        n();
    }

    public float c() {
        return this.l;
    }

    public float d() {
        return this.m;
    }

    public float e() {
        return this.n;
    }

    public float f() {
        return (float) Math.sqrt(((float) Math.pow(a(this.v, 0), 2.0d)) + ((float) Math.pow(a(this.v, 3), 2.0d)));
    }

    public ImageView.ScaleType g() {
        return this.J;
    }

    @Override // com.github.chrisbanes.photoview.OnGestureListener
    public void a(float f2, float f3) {
        if (this.s.a()) {
            return;
        }
        this.v.postTranslate(f2, f3);
        n();
        ViewParent parent = this.q.getParent();
        if (!this.o || this.s.a() || this.p) {
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
                return;
            }
            return;
        }
        int i2 = this.G;
        if ((i2 == 2 || ((i2 == 0 && f2 >= 1.0f) || (this.G == 1 && f2 <= -1.0f))) && parent != null) {
            parent.requestDisallowInterceptTouchEvent(false);
        }
    }

    @Override // com.github.chrisbanes.photoview.OnGestureListener
    public void a(float f2, float f3, float f4, float f5) {
        this.F = new FlingRunnable(this.q.getContext());
        this.F.a(a(this.q), b(this.q), (int) f4, (int) f5);
        this.q.post(this.F);
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        a(this.q.getDrawable());
    }

    @Override // com.github.chrisbanes.photoview.OnGestureListener
    public void a(float f2, float f3, float f4) {
        if (f() < this.n || f2 < 1.0f) {
            if (f() > this.l || f2 > 1.0f) {
                OnScaleChangedListener onScaleChangedListener = this.D;
                if (onScaleChangedListener != null) {
                    onScaleChangedListener.a(f2, f3, f4);
                }
                this.v.postScale(f2, f2, f3, f4);
                n();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0056  */
    /* JADX WARN: Code duplicated, block: B:29:0x0072  */
    /* JADX WARN: Code duplicated, block: B:34:0x007f  */
    /* JADX WARN: Code duplicated, block: B:39:0x0089  */
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z = false;
        CustomGestureDetector customGestureDetector;
        boolean z2;
        GestureDetector gestureDetector;
        boolean zA;
        boolean zB;
        boolean z3;
        boolean z4;
        RectF rectFB;
        boolean z5 = false;
        if (!this.I || !Util.a((ImageView) view)) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
            p();
        } else {
            if ((action == 1 || action == 3) && f() < this.l && (rectFB = b()) != null) {
                view.post(new AnimatedZoomRunnable(f(), this.l, rectFB.centerX(), rectFB.centerY()));
                z = true;
            }
            customGestureDetector = this.s;
            if (customGestureDetector != null) {
                zA = customGestureDetector.a();
                zB = this.s.b();
                boolean zA2 = this.s.a(motionEvent);
                if (!zA || this.s.a()) {
                    z3 = false;
                } else {
                    z3 = true;
                }
                if (!zB || this.s.b()) {
                    z4 = false;
                } else {
                    z4 = true;
                }
                if (z3 && z4) {
                    z5 = true;
                }
                this.p = z5;
                z2 = zA2;
            } else {
                z2 = z;
            }
            z = z2;
            gestureDetector = this.r;
            if (gestureDetector == null && gestureDetector.onTouchEvent(motionEvent)) {
                return true;
            }
        }
        z = false;
        customGestureDetector = this.s;
        if (customGestureDetector != null) {
            zA = customGestureDetector.a();
            zB = this.s.b();
            boolean zA3 = this.s.a(motionEvent);
            if (zA) {
                z3 = false;
            } else {
                z3 = false;
            }
            if (zB) {
                z4 = false;
            } else {
                z4 = false;
            }
            if (z3) {
                z5 = true;
            }
            this.p = z5;
            z2 = zA3;
        } else {
            z2 = z;
        }
        gestureDetector = this.r;
        return gestureDetector == null ? z2 : z2;
    }

    public void a(boolean z) {
        this.o = z;
    }

    public void d(float f2) {
        Util.a(f2, this.m, this.n);
        this.l = f2;
    }

    public void e(float f2) {
        Util.a(this.l, f2, this.n);
        this.m = f2;
    }

    public void f(float f2) {
        Util.a(this.l, this.m, f2);
        this.n = f2;
    }

    public void b(float f2, float f3, float f4) {
        Util.a(f2, f3, f4);
        this.l = f2;
        this.m = f3;
        this.n = f4;
    }

    public void a(View.OnLongClickListener onLongClickListener) {
        this.C = onLongClickListener;
    }

    public void a(View.OnClickListener onClickListener) {
        this.B = onClickListener;
    }

    public void a(OnMatrixChangedListener onMatrixChangedListener) {
        this.y = onMatrixChangedListener;
    }

    public void a(OnPhotoTapListener onPhotoTapListener) {
        this.z = onPhotoTapListener;
    }

    public void a(OnOutsidePhotoTapListener onOutsidePhotoTapListener) {
        this.A = onOutsidePhotoTapListener;
    }

    public void g(float f2) {
        a(f2, false);
    }

    public void a(float f2, boolean z) {
        a(f2, this.q.getRight() / 2, this.q.getBottom() / 2, z);
    }

    public void a(float f2, float f3, float f4, boolean z) {
        if (f2 < this.l || f2 > this.n) {
            throw new IllegalArgumentException("Scale must be within the range of minScale and maxScale");
        }
        if (z) {
            this.q.post(new AnimatedZoomRunnable(f(), f2, f3, f4));
        } else {
            this.v.setScale(f2, f2, f3, f4);
            n();
        }
    }

    public void a(Interpolator interpolator) {
        this.j = interpolator;
    }

    public void a(ImageView.ScaleType scaleType) {
        if (!Util.a(scaleType) || scaleType == this.J) {
            return;
        }
        this.J = scaleType;
        h();
    }

    public void b(boolean z) {
        this.I = z;
        h();
    }

    public void h() {
        if (this.I) {
            a(this.q.getDrawable());
        } else {
            m();
        }
    }

    public void b(Matrix matrix) {
        matrix.set(l());
    }

    public void c(Matrix matrix) {
        matrix.set(this.v);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Matrix l() {
        this.u.set(this.t);
        this.u.postConcat(this.v);
        return this.u;
    }

    public Matrix i() {
        return this.u;
    }

    public void a(int i2) {
        this.k = i2;
    }

    private float a(Matrix matrix, int i2) {
        matrix.getValues(this.x);
        return this.x[i2];
    }

    private void m() {
        this.v.reset();
        c(this.H);
        d(l());
        o();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void d(Matrix matrix) {
        RectF rectFE;
        this.q.setImageMatrix(matrix);
        if (this.y == null || (rectFE = e(matrix)) == null) {
            return;
        }
        this.y.a(rectFE);
    }

    private void n() {
        if (o()) {
            d(l());
        }
    }

    private RectF e(Matrix matrix) {
        Drawable drawable = this.q.getDrawable();
        if (drawable == null) {
            return null;
        }
        this.w.set(0.0f, 0.0f, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        matrix.mapRect(this.w);
        return this.w;
    }

    private void a(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        float fA = a(this.q);
        float fB = b(this.q);
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        this.t.reset();
        float f2 = intrinsicWidth;
        float f3 = fA / f2;
        float f4 = intrinsicHeight;
        float f5 = fB / f4;
        if (this.J == ImageView.ScaleType.CENTER) {
            this.t.postTranslate((fA - f2) / 2.0f, (fB - f4) / 2.0f);
        } else if (this.J == ImageView.ScaleType.CENTER_CROP) {
            float fMax = Math.max(f3, f5);
            this.t.postScale(fMax, fMax);
            this.t.postTranslate((fA - (f2 * fMax)) / 2.0f, (fB - (f4 * fMax)) / 2.0f);
        } else if (this.J == ImageView.ScaleType.CENTER_INSIDE) {
            float fMin = Math.min(1.0f, Math.min(f3, f5));
            this.t.postScale(fMin, fMin);
            this.t.postTranslate((fA - (f2 * fMin)) / 2.0f, (fB - (f4 * fMin)) / 2.0f);
        } else {
            RectF rectF = new RectF(0.0f, 0.0f, f2, f4);
            RectF rectF2 = new RectF(0.0f, 0.0f, fA, fB);
            if (((int) this.H) % 180 != 0) {
                rectF = new RectF(0.0f, 0.0f, f4, f2);
            }
            int i2 = AnonymousClass3.a[this.J.ordinal()];
            if (i2 == 1) {
                this.t.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.CENTER);
            } else if (i2 == 2) {
                this.t.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.START);
            } else if (i2 == 3) {
                this.t.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.END);
            } else if (i2 == 4) {
                this.t.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
            }
        }
        m();
    }

    /* JADX INFO: renamed from: com.github.chrisbanes.photoview.PhotoViewAttacher$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] a = new int[ImageView.ScaleType.values().length];

        static {
            try {
                a[ImageView.ScaleType.FIT_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                a[ImageView.ScaleType.FIT_START.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                a[ImageView.ScaleType.FIT_END.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                a[ImageView.ScaleType.FIT_XY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private boolean o() {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        RectF rectFE = e(l());
        if (rectFE == null) {
            return false;
        }
        float fHeight = rectFE.height();
        float fWidth = rectFE.width();
        float fB = b(this.q);
        float f7 = 0.0f;
        if (fHeight <= fB) {
            int i2 = AnonymousClass3.a[this.J.ordinal()];
            if (i2 != 2) {
                if (i2 == 3) {
                    fB -= fHeight;
                    f3 = rectFE.top;
                } else {
                    fB = (fB - fHeight) / 2.0f;
                    f3 = rectFE.top;
                }
                f2 = fB - f3;
            } else {
                f4 = rectFE.top;
                f2 = -f4;
            }
        } else if (rectFE.top > 0.0f) {
            f4 = rectFE.top;
            f2 = -f4;
        } else if (rectFE.bottom < fB) {
            f3 = rectFE.bottom;
            f2 = fB - f3;
        } else {
            f2 = 0.0f;
        }
        float fA = a(this.q);
        if (fWidth <= fA) {
            int i3 = AnonymousClass3.a[this.J.ordinal()];
            if (i3 != 2) {
                if (i3 == 3) {
                    f5 = fA - fWidth;
                    f6 = rectFE.left;
                } else {
                    f5 = (fA - fWidth) / 2.0f;
                    f6 = rectFE.left;
                }
                f7 = f5 - f6;
            } else {
                f7 = -rectFE.left;
            }
            this.G = 2;
        } else if (rectFE.left > 0.0f) {
            this.G = 0;
            f7 = -rectFE.left;
        } else if (rectFE.right < fA) {
            f7 = fA - rectFE.right;
            this.G = 1;
        } else {
            this.G = -1;
        }
        this.v.postTranslate(f7, f2);
        return true;
    }

    private int a(ImageView imageView) {
        return (imageView.getWidth() - imageView.getPaddingLeft()) - imageView.getPaddingRight();
    }

    private int b(ImageView imageView) {
        return (imageView.getHeight() - imageView.getPaddingTop()) - imageView.getPaddingBottom();
    }

    private void p() {
        FlingRunnable flingRunnable = this.F;
        if (flingRunnable != null) {
            flingRunnable.a();
            this.F = null;
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float b;
        private final float c;
        private final long d = System.currentTimeMillis();
        private final float e;
        private final float f;

        public AnimatedZoomRunnable(float f, float f2, float f3, float f4) {
            this.b = f3;
            this.c = f4;
            this.e = f;
            this.f = f2;
        }

        @Override // java.lang.Runnable
        public void run() {
            float fA = a();
            float f = this.e;
            PhotoViewAttacher.this.a((f + ((this.f - f) * fA)) / PhotoViewAttacher.this.f(), this.b, this.c);
            if (fA < 1.0f) {
                Compat.a(PhotoViewAttacher.this.q, this);
            }
        }

        private float a() {
            return PhotoViewAttacher.this.j.getInterpolation(Math.min(1.0f, ((System.currentTimeMillis() - this.d) * 1.0f) / PhotoViewAttacher.this.k));
        }
    }

    private class FlingRunnable implements Runnable {
        private final OverScroller b;
        private int c;
        private int d;

        public FlingRunnable(Context context) {
            this.b = new OverScroller(context);
        }

        public void a() {
            this.b.forceFinished(true);
        }

        public void a(int i, int i2, int i3, int i4) {
            int i5;
            int iRound;
            int i6;
            int iRound2;
            RectF rectFB = PhotoViewAttacher.this.b();
            if (rectFB == null) {
                return;
            }
            int iRound3 = Math.round(-rectFB.left);
            float f = i;
            if (f < rectFB.width()) {
                iRound = Math.round(rectFB.width() - f);
                i5 = 0;
            } else {
                i5 = iRound3;
                iRound = i5;
            }
            int iRound4 = Math.round(-rectFB.top);
            float f2 = i2;
            if (f2 < rectFB.height()) {
                iRound2 = Math.round(rectFB.height() - f2);
                i6 = 0;
            } else {
                i6 = iRound4;
                iRound2 = i6;
            }
            this.c = iRound3;
            this.d = iRound4;
            if (iRound3 == iRound && iRound4 == iRound2) {
                return;
            }
            this.b.fling(iRound3, iRound4, i3, i4, i5, iRound, i6, iRound2, 0, 0);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.b.isFinished() && this.b.computeScrollOffset()) {
                int currX = this.b.getCurrX();
                int currY = this.b.getCurrY();
                PhotoViewAttacher.this.v.postTranslate(this.c - currX, this.d - currY);
                PhotoViewAttacher photoViewAttacher = PhotoViewAttacher.this;
                photoViewAttacher.d(photoViewAttacher.l());
                this.c = currX;
                this.d = currY;
                Compat.a(PhotoViewAttacher.this.q, this);
            }
        }
    }
}

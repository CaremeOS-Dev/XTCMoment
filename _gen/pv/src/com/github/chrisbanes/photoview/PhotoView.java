package com.github.chrisbanes.photoview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageView;

/* JADX INFO: loaded from: classes.dex */
public class PhotoView extends ImageView {
    private PhotoViewAttacher a;

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PhotoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        b();
    }

    public PhotoView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        b();
    }

    private void b() {
        this.a = new PhotoViewAttacher(this);
        super.setScaleType(ImageView.ScaleType.MATRIX);
    }

    public PhotoViewAttacher getAttacher() {
        return this.a;
    }

    @Override // android.widget.ImageView
    public ImageView.ScaleType getScaleType() {
        return this.a.g();
    }

    @Override // android.widget.ImageView
    public Matrix getImageMatrix() {
        return this.a.i();
    }

    @Override // android.view.View
    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.a.a(onLongClickListener);
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.a.a(onClickListener);
    }

    @Override // android.widget.ImageView
    public void setScaleType(ImageView.ScaleType scaleType) {
        PhotoViewAttacher photoViewAttacher = this.a;
        if (photoViewAttacher != null) {
            photoViewAttacher.a(scaleType);
        }
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        PhotoViewAttacher photoViewAttacher = this.a;
        if (photoViewAttacher != null) {
            photoViewAttacher.h();
        }
    }

    @Override // android.widget.ImageView
    public void setImageResource(int i) {
        super.setImageResource(i);
        PhotoViewAttacher photoViewAttacher = this.a;
        if (photoViewAttacher != null) {
            photoViewAttacher.h();
        }
    }

    @Override // android.widget.ImageView
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        PhotoViewAttacher photoViewAttacher = this.a;
        if (photoViewAttacher != null) {
            photoViewAttacher.h();
        }
    }

    @Override // android.widget.ImageView
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        if (frame) {
            this.a.h();
        }
        return frame;
    }

    public void setRotationTo(float f) {
        this.a.b(f);
    }

    public void setRotationBy(float f) {
        this.a.c(f);
    }

    public boolean a() {
        return this.a.a();
    }

    public RectF getDisplayRect() {
        return this.a.b();
    }

    public void a(Matrix matrix) {
        this.a.b(matrix);
    }

    public boolean b(Matrix matrix) {
        return this.a.a(matrix);
    }

    public float getMinimumScale() {
        return this.a.c();
    }

    public float getMediumScale() {
        return this.a.d();
    }

    public float getMaximumScale() {
        return this.a.e();
    }

    public float getScale() {
        return this.a.f();
    }

    public void setAllowParentInterceptOnEdge(boolean z) {
        this.a.a(z);
    }

    public void setMinimumScale(float f) {
        this.a.d(f);
    }

    public void setMediumScale(float f) {
        this.a.e(f);
    }

    public void setMaximumScale(float f) {
        this.a.f(f);
    }

    public void setScaleLevels(float f, float f2, float f3) {
        this.a.b(f, f2, f3);
    }

    public void setOnMatrixChangeListener(OnMatrixChangedListener onMatrixChangedListener) {
        this.a.a(onMatrixChangedListener);
    }

    public void setOnPhotoTapListener(OnPhotoTapListener onPhotoTapListener) {
        this.a.a(onPhotoTapListener);
    }

    public void setOnOutsidePhotoTapListener(OnOutsidePhotoTapListener onOutsidePhotoTapListener) {
        this.a.a(onOutsidePhotoTapListener);
    }

    public void setScale(float f) {
        this.a.g(f);
    }

    public void setScale(float f, boolean z) {
        this.a.a(f, z);
    }

    public void setScale(float f, float f2, float f3, boolean z) {
        this.a.a(f, f2, f3, z);
    }

    public void setZoomable(boolean z) {
        this.a.b(z);
    }

    public void setZoomTransitionDuration(int i) {
        this.a.a(i);
    }

    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener onDoubleTapListener) {
        this.a.a(onDoubleTapListener);
    }

    public void setOnScaleChangeListener(OnScaleChangedListener onScaleChangedListener) {
        this.a.a(onScaleChangedListener);
    }

    public void setOnSingleFlingListener(OnSingleFlingListener onSingleFlingListener) {
        this.a.a(onSingleFlingListener);
    }
}

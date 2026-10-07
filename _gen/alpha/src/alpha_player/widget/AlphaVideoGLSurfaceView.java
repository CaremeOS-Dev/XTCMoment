package com.ss.ugc.android.alpha_player.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.ss.ugc.android.alpha_player.controller.IPlayerControllerExt;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.ss.ugc.android.alpha_player.render.IRender;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AlphaVideoGLSurfaceView.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000q\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011*\u0001'\u0018\u00002\u00020\u00012\u00020\u0002B\u001b\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0002\u0010\u0007J\u0006\u00102\u001a\u000203J\u0010\u00104\u001a\u0002032\u0006\u00105\u001a\u000206H\u0016J\b\u00107\u001a\u00020\u001bH\u0016J\b\u00108\u001a\u000209H\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\u0018\u0010:\u001a\u0002032\u0006\u0010;\u001a\u00020*2\u0006\u0010<\u001a\u00020*H\u0016J\b\u0010=\u001a\u000203H\u0016J\b\u0010>\u001a\u000203H\u0016J\u0018\u0010?\u001a\u0002032\u0006\u0010@\u001a\u00020\t2\u0006\u0010A\u001a\u00020\tH\u0014J\b\u0010B\u001a\u000203H\u0016J\u0010\u0010C\u001a\u0002032\u0006\u00105\u001a\u000206H\u0016J\u0010\u0010D\u001a\u0002032\u0006\u0010E\u001a\u00020\u000fH\u0016J\u0010\u0010F\u001a\u0002032\u0006\u0010G\u001a\u00020\u001bH\u0016J\u0010\u0010H\u001a\u0002032\u0006\u0010I\u001a\u00020\u0015H\u0016R\u0014\u0010\b\u001a\u00020\tX\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\u001a\u001a\u00020\u001bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001c\u0010 \u001a\u0004\u0018\u00010!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010%R\u0010\u0010&\u001a\u00020'X\u0082\u0004¢\u0006\u0004\n\u0002\u0010(R\u001a\u0010)\u001a\u00020*X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001a\u0010/\u001a\u00020*X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b0\u0010,\"\u0004\b1\u0010.¨\u0006J"}, d2 = {"Lcom/ss/ugc/android/alpha_player/widget/AlphaVideoGLSurfaceView;", "Landroid/opengl/GLSurfaceView;", "Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "context", "Landroid/content/Context;", "attr", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "GL_CONTEXT_VERSION", "", "getGL_CONTEXT_VERSION", "()I", "isSurfaceCreated", "", "mPlayerController", "Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "getMPlayerController", "()Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "setMPlayerController", "(Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;)V", "mRenderer", "Lcom/ss/ugc/android/alpha_player/render/IRender;", "getMRenderer", "()Lcom/ss/ugc/android/alpha_player/render/IRender;", "setMRenderer", "(Lcom/ss/ugc/android/alpha_player/render/IRender;)V", "mScaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "getMScaleType", "()Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "setMScaleType", "(Lcom/ss/ugc/android/alpha_player/model/ScaleType;)V", "mSurface", "Landroid/view/Surface;", "getMSurface", "()Landroid/view/Surface;", "setMSurface", "(Landroid/view/Surface;)V", "mSurfaceListener", "com/ss/ugc/android/alpha_player/widget/AlphaVideoGLSurfaceView$mSurfaceListener$1", "Lcom/ss/ugc/android/alpha_player/widget/AlphaVideoGLSurfaceView$mSurfaceListener$1;", "mVideoHeight", "", "getMVideoHeight", "()F", "setMVideoHeight", "(F)V", "mVideoWidth", "getMVideoWidth", "setMVideoWidth", "addOnSurfacePreparedListener", "", "addParentView", "parentView", "Landroid/view/ViewGroup;", "getScaleType", "getView", "Landroid/view/View;", "measureInternal", "videoWidth", "videoHeight", "onCompletion", "onFirstFrame", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "release", "removeParentView", "setPlayerController", "playerController", "setScaleType", "scaleType", "setVideoRenderer", "renderer", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class AlphaVideoGLSurfaceView extends GLSurfaceView implements IAlphaVideoView {
    private final int a;
    private volatile boolean b;
    private float c;
    private float d;
    private ScaleType e;
    private IRender f;
    private IPlayerControllerExt g;
    private Surface h;
    private final AlphaVideoGLSurfaceView$mSurfaceListener$1 i;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public AlphaVideoGLSurfaceView(Context context) {
        this(context, null, 2, 0 == true ? 1 : 0);
        Intrinsics.g(context, "context");
    }

    public /* synthetic */ AlphaVideoGLSurfaceView(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AlphaVideoGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.g(context, "context");
        this.a = 2;
        this.e = ScaleType.ScaleAspectFill;
        this.i = new AlphaVideoGLSurfaceView$mSurfaceListener$1(this);
        setEGLContextClientVersion(this.a);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(-3);
        b();
        setZOrderOnTop(true);
        setPreserveEGLContextOnPause(true);
    }

    /* JADX INFO: renamed from: getGL_CONTEXT_VERSION, reason: from getter */
    public final int getA() {
        return this.a;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    /* JADX INFO: renamed from: a, reason: from getter */
    public boolean getB() {
        return this.b;
    }

    /* JADX INFO: renamed from: getMVideoWidth, reason: from getter */
    public final float getC() {
        return this.c;
    }

    public final void setMVideoWidth(float f) {
        this.c = f;
    }

    /* JADX INFO: renamed from: getMVideoHeight, reason: from getter */
    public final float getD() {
        return this.d;
    }

    public final void setMVideoHeight(float f) {
        this.d = f;
    }

    /* JADX INFO: renamed from: getMScaleType, reason: from getter */
    public final ScaleType getE() {
        return this.e;
    }

    public final void setMScaleType(ScaleType scaleType) {
        Intrinsics.g(scaleType, "<set-?>");
        this.e = scaleType;
    }

    /* JADX INFO: renamed from: getMRenderer, reason: from getter */
    public final IRender getF() {
        return this.f;
    }

    public final void setMRenderer(IRender iRender) {
        this.f = iRender;
    }

    /* JADX INFO: renamed from: getMPlayerController, reason: from getter */
    public final IPlayerControllerExt getG() {
        return this.g;
    }

    public final void setMPlayerController(IPlayerControllerExt iPlayerControllerExt) {
        this.g = iPlayerControllerExt;
    }

    /* JADX INFO: renamed from: getMSurface, reason: from getter */
    public final Surface getH() {
        return this.h;
    }

    public final void setMSurface(Surface surface) {
        this.h = surface;
    }

    public final void b() {
        IRender iRender = this.f;
        if (iRender != null) {
            iRender.a(this.i);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void a(ViewGroup parentView) {
        Intrinsics.g(parentView, "parentView");
        AlphaVideoGLSurfaceView alphaVideoGLSurfaceView = this;
        if (parentView.indexOfChild(alphaVideoGLSurfaceView) == -1) {
            ViewParent parent = getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(alphaVideoGLSurfaceView);
            }
            parentView.addView(alphaVideoGLSurfaceView);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void b(ViewGroup parentView) {
        Intrinsics.g(parentView, "parentView");
        parentView.removeView(this);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public View getView() {
        return this;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void setPlayerController(IPlayerControllerExt playerController) {
        Intrinsics.g(playerController, "playerController");
        this.g = playerController;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void setVideoRenderer(IRender renderer) {
        Intrinsics.g(renderer, "renderer");
        this.f = renderer;
        setRenderer(renderer);
        b();
        setRenderMode(0);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void setScaleType(ScaleType scaleType) {
        Intrinsics.g(scaleType, "scaleType");
        this.e = scaleType;
        IRender iRender = this.f;
        if (iRender != null) {
            iRender.a(scaleType);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public ScaleType getScaleType() {
        return this.e;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void a(float f, float f2) {
        if (f > 0.0f && f2 > 0.0f) {
            this.c = f;
            this.d = f2;
        }
        final IRender iRender = this.f;
        if (iRender != null) {
            final int measuredWidth = getMeasuredWidth();
            final int measuredHeight = getMeasuredHeight();
            queueEvent(new Runnable() { // from class: com.ss.ugc.android.alpha_player.widget.-$$Lambda$AlphaVideoGLSurfaceView$8ScJvI2EtshRY0vjL47nPu15KIM
                @Override // java.lang.Runnable
                public final void run() {
                    AlphaVideoGLSurfaceView.a(iRender, measuredWidth, measuredHeight, this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void a(IRender it, int i, int i2, AlphaVideoGLSurfaceView this$0) {
        Intrinsics.g(it, "$it");
        Intrinsics.g(this$0, "this$0");
        it.a(i, i2, this$0.c, this$0.d);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void c() {
        IRender iRender = this.f;
        if (iRender != null) {
            iRender.a();
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void d() {
        IRender iRender = this.f;
        if (iRender != null) {
            iRender.b();
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        a(this.c, this.d);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void e() {
        this.i.a();
    }
}

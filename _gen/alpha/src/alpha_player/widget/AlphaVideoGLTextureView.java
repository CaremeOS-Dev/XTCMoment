package com.ss.ugc.android.alpha_player.widget;

import android.content.Context;
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

/* JADX INFO: compiled from: AlphaVideoGLTextureView.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000k\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011*\u0001\u001d\u0018\u00002\u00020\u00012\u00020\u0002B\u001b\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\"\u001a\u00020#H\u0002J\u0010\u0010$\u001a\u00020#2\u0006\u0010%\u001a\u00020&H\u0016J\b\u0010'\u001a\u00020\u0015H\u0016J\b\u0010(\u001a\u00020)H\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010*\u001a\u00020#2\u0006\u0010+\u001a\u00020 2\u0006\u0010,\u001a\u00020 H\u0016J\b\u0010-\u001a\u00020#H\u0016J\b\u0010.\u001a\u00020#H\u0016J\u0018\u0010/\u001a\u00020#2\u0006\u00100\u001a\u00020\t2\u0006\u00101\u001a\u00020\tH\u0014J\b\u00102\u001a\u00020#H\u0016J\u0010\u00103\u001a\u00020#2\u0006\u0010%\u001a\u00020&H\u0016J\u0010\u00104\u001a\u00020#2\u0006\u00105\u001a\u00020\rH\u0016J\u0010\u00106\u001a\u00020#2\u0006\u00107\u001a\u00020\u0015H\u0016J\u0010\u00108\u001a\u00020#2\u0006\u00109\u001a\u00020\u0013H\u0016R\u000e\u0010\b\u001a\u00020\tX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u0010\u0010\u001c\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001eR\u000e\u0010\u001f\u001a\u00020 X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020 X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006:"}, d2 = {"Lcom/ss/ugc/android/alpha_player/widget/AlphaVideoGLTextureView;", "Lcom/ss/ugc/android/alpha_player/widget/GLTextureView;", "Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "context", "Landroid/content/Context;", "attr", "Landroid/util/AttributeSet;", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "GL_CONTEXT_VERSION", "", "isSurfaceCreated", "", "mPlayerController", "Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "getMPlayerController", "()Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;", "setMPlayerController", "(Lcom/ss/ugc/android/alpha_player/controller/IPlayerControllerExt;)V", "mRenderer", "Lcom/ss/ugc/android/alpha_player/render/IRender;", "mScaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "mSurface", "Landroid/view/Surface;", "getMSurface", "()Landroid/view/Surface;", "setMSurface", "(Landroid/view/Surface;)V", "mSurfaceListener", "com/ss/ugc/android/alpha_player/widget/AlphaVideoGLTextureView$mSurfaceListener$1", "Lcom/ss/ugc/android/alpha_player/widget/AlphaVideoGLTextureView$mSurfaceListener$1;", "mVideoHeight", "", "mVideoWidth", "addOnSurfacePreparedListener", "", "addParentView", "parentView", "Landroid/view/ViewGroup;", "getScaleType", "getView", "Landroid/view/View;", "measureInternal", "videoWidth", "videoHeight", "onCompletion", "onFirstFrame", "onMeasure", "widthMeasureSpec", "heightMeasureSpec", "release", "removeParentView", "setPlayerController", "playerController", "setScaleType", "scaleType", "setVideoRenderer", "renderer", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class AlphaVideoGLTextureView extends GLTextureView implements IAlphaVideoView {
    private final int e;
    private volatile boolean f;
    private float g;
    private float h;
    private ScaleType i;
    private IRender j;
    private IPlayerControllerExt k;
    private Surface l;
    private final AlphaVideoGLTextureView$mSurfaceListener$1 m;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public AlphaVideoGLTextureView(Context context) {
        this(context, null, 2, 0 == true ? 1 : 0);
        Intrinsics.g(context, "context");
    }

    public /* synthetic */ AlphaVideoGLTextureView(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AlphaVideoGLTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Intrinsics.g(context, "context");
        this.e = 2;
        this.i = ScaleType.ScaleAspectFill;
        this.m = new AlphaVideoGLTextureView$mSurfaceListener$1(this);
        setEGLContextClientVersion(this.e);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        g();
        setPreserveEGLContextOnPause(true);
        setOpaque(false);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    /* JADX INFO: renamed from: a, reason: from getter */
    public boolean getF() {
        return this.f;
    }

    /* JADX INFO: renamed from: getMPlayerController, reason: from getter */
    public final IPlayerControllerExt getK() {
        return this.k;
    }

    public final void setMPlayerController(IPlayerControllerExt iPlayerControllerExt) {
        this.k = iPlayerControllerExt;
    }

    /* JADX INFO: renamed from: getMSurface, reason: from getter */
    public final Surface getL() {
        return this.l;
    }

    public final void setMSurface(Surface surface) {
        this.l = surface;
    }

    private final void g() {
        IRender iRender = this.j;
        if (iRender != null) {
            iRender.a(this.m);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void a(ViewGroup parentView) {
        Intrinsics.g(parentView, "parentView");
        AlphaVideoGLTextureView alphaVideoGLTextureView = this;
        if (parentView.indexOfChild(alphaVideoGLTextureView) == -1) {
            ViewParent parent = getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(alphaVideoGLTextureView);
            }
            parentView.addView(alphaVideoGLTextureView);
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
        this.k = playerController;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void setVideoRenderer(IRender renderer) {
        Intrinsics.g(renderer, "renderer");
        this.j = renderer;
        setRenderer(renderer);
        g();
        setRenderMode(0);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void setScaleType(ScaleType scaleType) {
        Intrinsics.g(scaleType, "scaleType");
        this.i = scaleType;
        IRender iRender = this.j;
        if (iRender != null) {
            iRender.a(scaleType);
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    /* JADX INFO: renamed from: getScaleType, reason: from getter */
    public ScaleType getI() {
        return this.i;
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void a(float f, float f2) {
        if (f > 0.0f && f2 > 0.0f) {
            this.g = f;
            this.h = f2;
        }
        final IRender iRender = this.j;
        if (iRender != null) {
            final int measuredWidth = getMeasuredWidth();
            final int measuredHeight = getMeasuredHeight();
            a(new Runnable() { // from class: com.ss.ugc.android.alpha_player.widget.-$$Lambda$AlphaVideoGLTextureView$babI1wZ1abGHeyFIkpvILQHFmcI
                @Override // java.lang.Runnable
                public final void run() {
                    AlphaVideoGLTextureView.a(iRender, measuredWidth, measuredHeight, this);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void a(IRender it, int i, int i2, AlphaVideoGLTextureView this$0) {
        Intrinsics.g(it, "$it");
        Intrinsics.g(this$0, "this$0");
        it.a(i, i2, this$0.g, this$0.h);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void c() {
        IRender iRender = this.j;
        if (iRender != null) {
            iRender.a();
        }
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void d() {
        IRender iRender = this.j;
        if (iRender != null) {
            iRender.b();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        a(this.g, this.h);
    }

    @Override // com.ss.ugc.android.alpha_player.widget.IAlphaVideoView
    public void e() {
        this.m.a();
    }
}

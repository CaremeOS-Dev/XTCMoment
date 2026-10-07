package com.ss.ugc.android.alpha_player.widget;

import android.view.Surface;
import com.ss.ugc.android.alpha_player.controller.IPlayerControllerExt;
import com.ss.ugc.android.alpha_player.render.IRender;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AlphaVideoGLSurfaceView.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\u0016J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\u0007"}, d2 = {"com/ss/ugc/android/alpha_player/widget/AlphaVideoGLSurfaceView$mSurfaceListener$1", "Lcom/ss/ugc/android/alpha_player/render/IRender$SurfaceListener;", "onSurfaceDestroyed", "", "onSurfacePrepared", "surface", "Landroid/view/Surface;", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class AlphaVideoGLSurfaceView$mSurfaceListener$1 implements IRender.SurfaceListener {
    final /* synthetic */ AlphaVideoGLSurfaceView a;

    AlphaVideoGLSurfaceView$mSurfaceListener$1(AlphaVideoGLSurfaceView alphaVideoGLSurfaceView) {
        this.a = alphaVideoGLSurfaceView;
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender.SurfaceListener
    public void a(Surface surface) {
        Intrinsics.g(surface, "surface");
        Surface h = this.a.getH();
        if (h != null) {
            h.release();
        }
        this.a.setMSurface(surface);
        this.a.b = true;
        IPlayerControllerExt g = this.a.getG();
        if (g != null) {
            g.a(surface);
        }
        IPlayerControllerExt g2 = this.a.getG();
        if (g2 != null) {
            g2.b();
        }
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender.SurfaceListener
    public void a() {
        Surface h = this.a.getH();
        if (h != null) {
            h.release();
        }
        this.a.setMSurface(null);
        this.a.b = false;
    }
}

package com.ss.ugc.android.alpha_player.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.Surface;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.ss.ugc.android.alpha_player.widget.GLTextureView;
import kotlin.Metadata;

/* JADX INFO: compiled from: IRender.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003:\u0001\u0013J(\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u0007H&J\b\u0010\u000b\u001a\u00020\u0005H&J\b\u0010\f\u001a\u00020\u0005H&J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH&J\u0010\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H&¨\u0006\u0014"}, d2 = {"Lcom/ss/ugc/android/alpha_player/render/IRender;", "Lcom/ss/ugc/android/alpha_player/widget/GLTextureView$Renderer;", "Landroid/opengl/GLSurfaceView$Renderer;", "Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;", "measureInternal", "", "viewWidth", "", "viewHeight", "videoWidth", "videoHeight", "onCompletion", "onFirstFrame", "setScaleType", "scaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "setSurfaceListener", "surfaceListener", "Lcom/ss/ugc/android/alpha_player/render/IRender$SurfaceListener;", "SurfaceListener", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public interface IRender extends SurfaceTexture.OnFrameAvailableListener, GLSurfaceView.Renderer, GLTextureView.Renderer {

    /* JADX INFO: compiled from: IRender.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&¨\u0006\u0007"}, d2 = {"Lcom/ss/ugc/android/alpha_player/render/IRender$SurfaceListener;", "", "onSurfaceDestroyed", "", "onSurfacePrepared", "surface", "Landroid/view/Surface;", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public interface SurfaceListener {
        void a();

        void a(Surface surface);
    }

    void a();

    void a(float f, float f2, float f3, float f4);

    void a(ScaleType scaleType);

    void a(SurfaceListener surfaceListener);

    void b();
}

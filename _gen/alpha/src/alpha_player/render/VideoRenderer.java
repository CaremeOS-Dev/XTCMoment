package com.ss.ugc.android.alpha_player.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.view.Surface;
import com.badlogic.gdx.graphics.GL20;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.ss.ugc.android.alpha_player.utils.ShaderUtil;
import com.ss.ugc.android.alpha_player.utils.TextureCropUtil;
import com.ss.ugc.android.alpha_player.widget.IAlphaVideoView;
import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: VideoRenderer.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\tH\u0002J\b\u0010'\u001a\u00020\u0006H\u0002J\u0018\u0010(\u001a\u00020\u00062\u0006\u0010)\u001a\u00020\u00062\u0006\u0010*\u001a\u00020\tH\u0002J(\u0010+\u001a\u00020%2\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020-2\u0006\u0010/\u001a\u00020-2\u0006\u00100\u001a\u00020-H\u0016J\b\u00101\u001a\u00020%H\u0016J\u0010\u00102\u001a\u00020%2\u0006\u00103\u001a\u000204H\u0016J\b\u00105\u001a\u00020%H\u0016J\u0010\u00106\u001a\u00020%2\u0006\u00107\u001a\u00020\u001dH\u0016J \u00108\u001a\u00020%2\u0006\u00103\u001a\u0002042\u0006\u00109\u001a\u00020\u00062\u0006\u0010:\u001a\u00020\u0006H\u0016J\u0018\u0010;\u001a\u00020%2\u0006\u00103\u001a\u0002042\u0006\u0010<\u001a\u00020=H\u0016J\u0012\u0010>\u001a\u00020%2\b\u0010?\u001a\u0004\u0018\u000104H\u0016J\b\u0010@\u001a\u00020%H\u0002J\u0010\u0010A\u001a\u00020%2\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010B\u001a\u00020%2\u0006\u0010\u001a\u001a\u00020\u001bH\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006C"}, d2 = {"Lcom/ss/ugc/android/alpha_player/render/VideoRenderer;", "Lcom/ss/ugc/android/alpha_player/render/IRender;", "alphaVideoView", "Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "(Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;)V", "FLOAT_SIZE_BYTES", "", "GL_TEXTURE_EXTERNAL_OES", "TAG", "", "TRIANGLE_VERTICES_DATA_POS_OFFSET", "TRIANGLE_VERTICES_DATA_STRIDE_BYTES", "TRIANGLE_VERTICES_DATA_UV_OFFSET", "aPositionHandle", "aTextureHandle", "getAlphaVideoView", "()Lcom/ss/ugc/android/alpha_player/widget/IAlphaVideoView;", "canDraw", "Ljava/util/concurrent/atomic/AtomicBoolean;", "halfRightVerticeData", "", "mVPMatrix", "programID", "sTMatrix", "scaleType", "Lcom/ss/ugc/android/alpha_player/model/ScaleType;", "surfaceListener", "Lcom/ss/ugc/android/alpha_player/render/IRender$SurfaceListener;", "surfaceTexture", "Landroid/graphics/SurfaceTexture;", "textureID", "triangleVertices", "Ljava/nio/FloatBuffer;", "uMVPMatrixHandle", "uSTMatrixHandle", "updateSurface", "checkGlError", "", "op", "createProgram", "loadShader", "shaderType", "source", "measureInternal", "viewWidth", "", "viewHeight", "videoWidth", "videoHeight", "onCompletion", "onDrawFrame", "glUnused", "Ljavax/microedition/khronos/opengles/GL10;", "onFirstFrame", "onFrameAvailable", "surface", "onSurfaceChanged", "width", WatchAccountBase.KEY_HEIGHT, "onSurfaceCreated", "config", "Ljavax/microedition/khronos/egl/EGLConfig;", "onSurfaceDestroyed", "gl", "prepareSurface", "setScaleType", "setSurfaceListener", "alpha_player_release"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class VideoRenderer implements IRender {
    private final IAlphaVideoView a;
    private final String b;
    private final int c;
    private final int d;
    private final int e;
    private final int f;
    private final int g;
    private float[] h;
    private FloatBuffer i;
    private final float[] j;
    private final float[] k;
    private int l;
    private int m;
    private int n;
    private int o;
    private int p;
    private int q;
    private final AtomicBoolean r;
    private final AtomicBoolean s;
    private SurfaceTexture t;
    private IRender.SurfaceListener u;
    private ScaleType v;

    public VideoRenderer(IAlphaVideoView alphaVideoView) {
        Intrinsics.g(alphaVideoView, "alphaVideoView");
        this.a = alphaVideoView;
        this.b = "VideoRender";
        this.c = 4;
        this.d = this.c * 5;
        this.f = 3;
        this.g = 36197;
        this.h = new float[]{-1.0f, -1.0f, 0.0f, 0.5f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
        this.j = new float[16];
        this.k = new float[16];
        this.r = new AtomicBoolean(false);
        this.s = new AtomicBoolean(false);
        this.v = ScaleType.ScaleAspectFill;
        FloatBuffer floatBufferAsFloatBuffer = ByteBuffer.allocateDirect(this.h.length * this.c).order(ByteOrder.nativeOrder()).asFloatBuffer();
        Intrinsics.c(floatBufferAsFloatBuffer, "allocateDirect(halfRight…eOrder()).asFloatBuffer()");
        this.i = floatBufferAsFloatBuffer;
        this.i.put(this.h).position(0);
        Matrix.setIdentityM(this.k, 0);
    }

    /* JADX INFO: renamed from: c, reason: from getter */
    public final IAlphaVideoView getA() {
        return this.a;
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender
    public void a(ScaleType scaleType) {
        Intrinsics.g(scaleType, "scaleType");
        this.v = scaleType;
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender
    public void a(float f, float f2, float f3, float f4) {
        if (f <= 0.0f || f2 <= 0.0f || f3 <= 0.0f || f4 <= 0.0f) {
            return;
        }
        float[] fArrA = TextureCropUtil.a(this.v, f, f2, f3, f4);
        Intrinsics.c(fArrA, "calculateHalfRightVertic… videoWidth, videoHeight)");
        this.h = fArrA;
        FloatBuffer floatBufferAsFloatBuffer = ByteBuffer.allocateDirect(this.h.length * this.c).order(ByteOrder.nativeOrder()).asFloatBuffer();
        Intrinsics.c(floatBufferAsFloatBuffer, "allocateDirect(halfRight…eOrder()).asFloatBuffer()");
        this.i = floatBufferAsFloatBuffer;
        this.i.put(this.h).position(0);
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender
    public void a(IRender.SurfaceListener surfaceListener) {
        Intrinsics.g(surfaceListener, "surfaceListener");
        this.u = surfaceListener;
    }

    @Override // android.opengl.GLSurfaceView.Renderer, com.ss.ugc.android.alpha_player.widget.GLTextureView.Renderer
    public void onDrawFrame(GL10 glUnused) {
        Intrinsics.g(glUnused, "glUnused");
        if (this.s.compareAndSet(true, false)) {
            SurfaceTexture surfaceTexture = null;
            try {
                SurfaceTexture surfaceTexture2 = this.t;
                if (surfaceTexture2 == null) {
                    Intrinsics.d("surfaceTexture");
                    surfaceTexture2 = null;
                }
                surfaceTexture2.updateTexImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SurfaceTexture surfaceTexture3 = this.t;
            if (surfaceTexture3 == null) {
                Intrinsics.d("surfaceTexture");
            } else {
                surfaceTexture = surfaceTexture3;
            }
            surfaceTexture.getTransformMatrix(this.k);
        }
        GLES20.glClear(16640);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        if (!this.r.get()) {
            GLES20.glFinish();
            return;
        }
        GLES20.glEnable(GL20.ac);
        GLES20.glBlendFunc(GL20.r, GL20.s);
        GLES20.glUseProgram(this.l);
        a("glUseProgram");
        GLES20.glActiveTexture(GL20.cR);
        GLES20.glBindTexture(this.g, this.m);
        this.i.position(this.e);
        GLES20.glVertexAttribPointer(this.p, 3, GL20.bz, false, this.d, (Buffer) this.i);
        a("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(this.p);
        a("glEnableVertexAttribArray aPositionHandle");
        this.i.position(this.f);
        GLES20.glVertexAttribPointer(this.q, 3, GL20.bz, false, this.d, (Buffer) this.i);
        a("glVertexAttribPointer aTextureHandle");
        GLES20.glEnableVertexAttribArray(this.q);
        a("glEnableVertexAttribArray aTextureHandle");
        Matrix.setIdentityM(this.j, 0);
        GLES20.glUniformMatrix4fv(this.n, 1, false, this.j, 0);
        GLES20.glUniformMatrix4fv(this.o, 1, false, this.k, 0);
        GLES20.glDrawArrays(5, 0, 4);
        a("glDrawArrays");
        GLES20.glFinish();
    }

    @Override // android.opengl.GLSurfaceView.Renderer, com.ss.ugc.android.alpha_player.widget.GLTextureView.Renderer
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        Intrinsics.g(glUnused, "glUnused");
        GLES20.glViewport(0, 0, width, height);
    }

    @Override // android.opengl.GLSurfaceView.Renderer, com.ss.ugc.android.alpha_player.widget.GLTextureView.Renderer
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        Intrinsics.g(glUnused, "glUnused");
        Intrinsics.g(config, "config");
        this.l = e();
        int i = this.l;
        if (i == 0) {
            return;
        }
        this.p = GLES20.glGetAttribLocation(i, "aPosition");
        a("glGetAttribLocation aPosition");
        if (this.p == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        this.q = GLES20.glGetAttribLocation(this.l, "aTextureCoord");
        a("glGetAttribLocation aTextureCoord");
        if (this.q == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        this.n = GLES20.glGetUniformLocation(this.l, "uMVPMatrix");
        a("glGetUniformLocation uMVPMatrix");
        if (this.n == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        this.o = GLES20.glGetUniformLocation(this.l, "uSTMatrix");
        a("glGetUniformLocation uSTMatrix");
        if (this.o == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
        d();
    }

    @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.Renderer
    public void a(GL10 gl10) {
        IRender.SurfaceListener surfaceListener = this.u;
        if (surfaceListener != null) {
            surfaceListener.a();
        }
    }

    private final void d() {
        int[] iArr = new int[1];
        GLES20.glGenTextures(1, iArr, 0);
        this.m = iArr[0];
        GLES20.glBindTexture(this.g, this.m);
        a("glBindTexture textureID");
        GLES20.glTexParameterf(this.g, GL20.cE, 9728.0f);
        GLES20.glTexParameterf(this.g, GL20.cD, 9729.0f);
        this.t = new SurfaceTexture(this.m);
        SurfaceTexture surfaceTexture = null;
        if (Build.VERSION.SDK_INT >= 15) {
            SurfaceTexture surfaceTexture2 = this.t;
            if (surfaceTexture2 == null) {
                Intrinsics.d("surfaceTexture");
                surfaceTexture2 = null;
            }
            surfaceTexture2.setDefaultBufferSize(this.a.getMeasuredWidth(), this.a.getMeasuredHeight());
        }
        SurfaceTexture surfaceTexture3 = this.t;
        if (surfaceTexture3 == null) {
            Intrinsics.d("surfaceTexture");
            surfaceTexture3 = null;
        }
        surfaceTexture3.setOnFrameAvailableListener(this);
        SurfaceTexture surfaceTexture4 = this.t;
        if (surfaceTexture4 == null) {
            Intrinsics.d("surfaceTexture");
        } else {
            surfaceTexture = surfaceTexture4;
        }
        Surface surface = new Surface(surfaceTexture);
        IRender.SurfaceListener surfaceListener = this.u;
        if (surfaceListener != null) {
            surfaceListener.a(surface);
        }
        this.s.compareAndSet(true, false);
    }

    @Override // android.graphics.SurfaceTexture.OnFrameAvailableListener
    public void onFrameAvailable(SurfaceTexture surface) {
        Intrinsics.g(surface, "surface");
        this.s.compareAndSet(false, true);
        this.a.requestRender();
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender
    public void a() {
        this.r.compareAndSet(false, true);
        LogUtil.i(this.b, "onFirstFrame:    canDraw = " + this.r.get());
        this.a.requestRender();
    }

    @Override // com.ss.ugc.android.alpha_player.render.IRender
    public void b() {
        this.r.compareAndSet(true, false);
        LogUtil.i(this.b, "onCompletion:   canDraw = " + this.r.get());
        this.a.requestRender();
    }

    private final int a(int i, String str) {
        int iGlCreateShader = GLES20.glCreateShader(i);
        if (iGlCreateShader == 0) {
            return iGlCreateShader;
        }
        GLES20.glShaderSource(iGlCreateShader, str);
        GLES20.glCompileShader(iGlCreateShader);
        int[] iArr = new int[1];
        GLES20.glGetShaderiv(iGlCreateShader, GL20.dZ, iArr, 0);
        if (iArr[0] != 0) {
            return iGlCreateShader;
        }
        LogUtil.e(this.b, "Could not compile shader " + i + ':');
        LogUtil.e(this.b, GLES20.glGetProgramInfoLog(iGlCreateShader));
        GLES20.glDeleteShader(iGlCreateShader);
        return 0;
    }

    private final int e() {
        String vertexSource = ShaderUtil.a("vertex.sh", this.a.getView().getResources());
        String fragmentSource = ShaderUtil.a("frag.sh", this.a.getView().getResources());
        Intrinsics.c(vertexSource, "vertexSource");
        int iA = a(GL20.bL, vertexSource);
        if (iA == 0) {
            return 0;
        }
        Intrinsics.c(fragmentSource, "fragmentSource");
        int iA2 = a(GL20.bK, fragmentSource);
        if (iA2 == 0) {
            return 0;
        }
        int iGlCreateProgram = GLES20.glCreateProgram();
        if (iGlCreateProgram != 0) {
            GLES20.glAttachShader(iGlCreateProgram, iA);
            a("glAttachShader");
            GLES20.glAttachShader(iGlCreateProgram, iA2);
            a("glAttachShader");
            GLES20.glLinkProgram(iGlCreateProgram);
            int[] iArr = new int[1];
            GLES20.glGetProgramiv(iGlCreateProgram, GL20.bV, iArr, 0);
            if (iArr[0] != 1) {
                LogUtil.e(this.b, "Could not link programID: ");
                LogUtil.e(this.b, GLES20.glGetProgramInfoLog(iGlCreateProgram));
                GLES20.glDeleteProgram(iGlCreateProgram);
                return 0;
            }
        }
        return iGlCreateProgram;
    }

    private final void a(String str) {
        int iGlGetError = GLES20.glGetError();
        if (iGlGetError != 0) {
            LogUtil.e(this.b, str + ": glError " + iGlGetError);
        }
    }
}

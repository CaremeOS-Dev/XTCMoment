package com.ss.ugc.android.alpha_player.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLDebugHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import com.badlogic.gdx.graphics.GL20;
import com.ss.ugc.android.alpha_player.IMonitor;
import com.xtc.log.LogUtil;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: loaded from: classes2.dex */
public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener, View.OnLayoutChangeListener {
    public static final int a = 0;
    public static final int b = 1;
    public static final int c = 1;
    public static final int d = 2;
    private static final String e = "GLTextureView";
    private static final boolean f = false;
    private static final boolean g = false;
    private static final boolean h = false;
    private static final boolean i = false;
    private static final boolean j = false;
    private static final boolean k = false;
    private static final boolean l = false;
    private static final GLThreadManager n = new GLThreadManager();
    private IMonitor m;
    private final WeakReference<GLTextureView> o;
    private GLThread p;
    private Renderer q;
    private boolean r;
    private EGLConfigChooser s;
    private EGLContextFactory t;
    private EGLWindowSurfaceFactory u;
    private GLWrapper v;
    private int w;
    private int x;
    private boolean y;

    public interface EGLConfigChooser {
        EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay);
    }

    public interface EGLContextFactory {
        EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig);

        void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj);

        void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface);
    }

    public interface GLWrapper {
        GL a(GL gl);
    }

    public interface Renderer {
        void a(GL10 gl10);

        void onDrawFrame(GL10 gl10);

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public GLTextureView(Context context) {
        super(context);
        this.o = new WeakReference<>(this);
        a();
    }

    public GLTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.o = new WeakReference<>(this);
        a();
    }

    protected void finalize() throws Throwable {
        try {
            if (this.p != null) {
                this.p.h();
            }
        } finally {
            super.finalize();
        }
    }

    private void a() {
        setSurfaceTextureListener(this);
    }

    public void setMonitor(IMonitor iMonitor) {
        this.m = iMonitor;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(boolean z, String str) {
        IMonitor iMonitor = this.m;
        if (iMonitor != null) {
            iMonitor.monitor(z, "unknown", 0, 0, str);
        }
    }

    public void setGLWrapper(GLWrapper gLWrapper) {
        this.v = gLWrapper;
    }

    public void setDebugFlags(int i2) {
        this.w = i2;
    }

    public int getDebugFlags() {
        return this.w;
    }

    public void setPreserveEGLContextOnPause(boolean z) {
        this.y = z;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.y;
    }

    public void setRenderer(Renderer renderer) {
        c();
        if (this.s == null) {
            this.s = new SimpleEGLConfigChooser(true);
        }
        if (this.t == null) {
            this.t = new DefaultContextFactory();
        }
        if (this.u == null) {
            this.u = new DefaultWindowSurfaceFactory();
        }
        this.q = renderer;
        this.p = new GLThread(this.o);
        this.p.start();
    }

    public void setEGLContextFactory(EGLContextFactory eGLContextFactory) {
        c();
        this.t = eGLContextFactory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory eGLWindowSurfaceFactory) {
        c();
        this.u = eGLWindowSurfaceFactory;
    }

    public void setEGLConfigChooser(EGLConfigChooser eGLConfigChooser) {
        c();
        this.s = eGLConfigChooser;
    }

    public void setEGLConfigChooser(boolean z) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(z));
    }

    public void setEGLConfigChooser(int i2, int i3, int i4, int i5, int i6, int i7) {
        setEGLConfigChooser(new ComponentSizeChooser(i2, i3, i4, i5, i6, i7));
    }

    public void setEGLContextClientVersion(int i2) {
        c();
        this.x = i2;
    }

    public void setRenderMode(int i2) {
        this.p.a(i2);
    }

    public int getRenderMode() {
        return this.p.b();
    }

    public void requestRender() {
        this.p.c();
    }

    public void a(SurfaceTexture surfaceTexture) {
        this.p.d();
    }

    public void b(SurfaceTexture surfaceTexture) {
        this.p.e();
    }

    public void a(SurfaceTexture surfaceTexture, int i2, int i3, int i4) {
        this.p.a(i3, i4);
    }

    public void onPause() {
        this.p.f();
    }

    public void b() {
        this.p.g();
    }

    public void a(Runnable runnable) {
        this.p.a(runnable);
    }

    @Override // android.view.TextureView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.r && this.q != null) {
            GLThread gLThread = this.p;
            int iB = gLThread != null ? gLThread.b() : 1;
            this.p = new GLThread(this.o);
            if (iB != 1) {
                this.p.a(iB);
            }
            this.p.start();
        }
        this.r = false;
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        GLThread gLThread = this.p;
        if (gLThread != null) {
            gLThread.h();
        }
        this.r = true;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View view, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        a(getSurfaceTexture(), 0, i4 - i2, i5 - i3);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i2, int i3) {
        a(surfaceTexture);
        a(surfaceTexture, 0, i2, i3);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i2, int i3) {
        a(surfaceTexture, 0, i2, i3);
    }

    @Override // android.view.TextureView.SurfaceTextureListener
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        b(surfaceTexture);
        return true;
    }

    private class DefaultContextFactory implements EGLContextFactory {
        private int b;

        private DefaultContextFactory() {
            this.b = 12440;
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.EGLContextFactory
        public EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {this.b, GLTextureView.this.x, 12344};
            EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (GLTextureView.this.x == 0) {
                iArr = null;
            }
            return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.EGLContextFactory
        public void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            if (egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
                return;
            }
            LogUtil.e("DefaultContextFactory", "display:" + eGLDisplay + " context: " + eGLContext);
            EglHelper.a("eglDestroyContex", egl10.eglGetError());
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.EGLWindowSurfaceFactory
        public EGLSurface a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, Object obj) {
            try {
                return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, null);
            } catch (IllegalArgumentException e) {
                LogUtil.e(GLTextureView.e, "eglCreateWindowSurface", e);
                return null;
            }
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.EGLWindowSurfaceFactory
        public void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLSurface eGLSurface) {
            egl10.eglDestroySurface(eGLDisplay, eGLSurface);
        }
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] a;

        abstract EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr);

        public BaseConfigChooser(int[] iArr) {
            this.a = a(iArr);
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.EGLConfigChooser
        public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (!egl10.eglChooseConfig(eGLDisplay, this.a, null, 0, iArr)) {
                GLTextureView.this.a(false, Log.getStackTraceString(new IllegalArgumentException("eglChooseConfig failed")));
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            if (!egl10.eglChooseConfig(eGLDisplay, this.a, eGLConfigArr, i, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig eGLConfigA = a(egl10, eGLDisplay, eGLConfigArr);
            if (eGLConfigA != null) {
                return eGLConfigA;
            }
            throw new IllegalArgumentException("No config chosen");
        }

        private int[] a(int[] iArr) {
            if (GLTextureView.this.x != 2) {
                return iArr;
            }
            int length = iArr.length;
            int[] iArr2 = new int[length + 2];
            int i = length - 1;
            System.arraycopy(iArr, 0, iArr2, 0, i);
            iArr2[i] = 12352;
            iArr2[length] = 4;
            iArr2[length + 1] = 12344;
            return iArr2;
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int c;
        protected int d;
        protected int e;
        protected int f;
        protected int g;
        protected int h;
        private int[] j;

        public ComponentSizeChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
            this.j = new int[1];
            this.c = i;
            this.d = i2;
            this.e = i3;
            this.f = i4;
            this.g = i5;
            this.h = i6;
        }

        @Override // com.ss.ugc.android.alpha_player.widget.GLTextureView.BaseConfigChooser
        public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int iA = a(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int iA2 = a(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (iA >= this.g && iA2 >= this.h) {
                    int iA3 = a(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int iA4 = a(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int iA5 = a(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    int iA6 = a(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.j) ? this.j[0] : i2;
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean z) {
            super(8, 8, 8, 0, z ? 16 : 0, 0);
        }
    }

    private static class EglHelper {
        EGL10 a;
        EGLDisplay b;
        EGLSurface c;
        EGLConfig d;
        EGLContext e;
        private WeakReference<GLTextureView> f;

        public EglHelper(WeakReference<GLTextureView> weakReference) {
            this.f = weakReference;
        }

        public void a() {
            this.a = (EGL10) EGLContext.getEGL();
            this.b = this.a.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.b == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (!this.a.eglInitialize(this.b, new int[2])) {
                throw new RuntimeException("eglInitialize failed");
            }
            GLTextureView gLTextureView = this.f.get();
            if (gLTextureView != null) {
                this.d = gLTextureView.s.a(this.a, this.b);
                this.e = gLTextureView.t.a(this.a, this.b, this.d);
            } else {
                this.d = null;
                this.e = null;
            }
            EGLContext eGLContext = this.e;
            if (eGLContext == null || eGLContext == EGL10.EGL_NO_CONTEXT) {
                this.e = null;
                a("createContext");
            }
            this.c = null;
        }

        public boolean b() {
            if (this.a == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.b == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.d == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            g();
            GLTextureView gLTextureView = this.f.get();
            if (gLTextureView != null) {
                this.c = gLTextureView.u.a(this.a, this.b, this.d, gLTextureView.getSurfaceTexture());
            } else {
                this.c = null;
            }
            EGLSurface eGLSurface = this.c;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                if (this.a.eglGetError() == 12299) {
                    LogUtil.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }
            EGL10 egl10 = this.a;
            EGLDisplay eGLDisplay = this.b;
            EGLSurface eGLSurface2 = this.c;
            if (egl10.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, this.e)) {
                return true;
            }
            a("EGLHelper", "eglMakeCurrent", this.a.eglGetError());
            return false;
        }

        GL c() {
            GL gl = this.e.getGL();
            GLTextureView gLTextureView = this.f.get();
            if (gLTextureView == null) {
                return gl;
            }
            if (gLTextureView.v != null) {
                gl = gLTextureView.v.a(gl);
            }
            if ((gLTextureView.w & 3) != 0) {
                return GLDebugHelper.wrap(gl, (gLTextureView.w & 1) != 0 ? 1 : 0, (gLTextureView.w & 2) != 0 ? new LogWriter() : null);
            }
            return gl;
        }

        public int d() {
            if (this.a.eglSwapBuffers(this.b, this.c)) {
                return 12288;
            }
            return this.a.eglGetError();
        }

        public void e() {
            g();
        }

        private void g() {
            EGLSurface eGLSurface = this.c;
            if (eGLSurface == null || eGLSurface == EGL10.EGL_NO_SURFACE) {
                return;
            }
            this.a.eglMakeCurrent(this.b, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            GLTextureView gLTextureView = this.f.get();
            if (gLTextureView != null) {
                gLTextureView.u.a(this.a, this.b, this.c);
            }
            this.c = null;
        }

        public void f() {
            if (this.e != null) {
                GLTextureView gLTextureView = this.f.get();
                if (gLTextureView != null) {
                    gLTextureView.t.a(this.a, this.b, this.e);
                }
                this.e = null;
            }
            EGLDisplay eGLDisplay = this.b;
            if (eGLDisplay != null) {
                this.a.eglTerminate(eGLDisplay);
                this.b = null;
            }
        }

        private void a(String str) {
            a(str, this.a.eglGetError());
        }

        public static void a(String str, int i) {
            throw new RuntimeException(b(str, i));
        }

        public static void a(String str, String str2, int i) {
            LogUtil.w(str, b(str2, i));
        }

        public static String b(String str, int i) {
            return str + " failed: " + i;
        }
    }

    static class GLThread extends Thread {
        private boolean a;
        private boolean b;
        private boolean c;
        private boolean d;
        private boolean e;
        private boolean f;
        private boolean g;
        private boolean h;
        private boolean i;
        private boolean j;
        private boolean o;
        private EglHelper r;
        private WeakReference<GLTextureView> s;
        private ArrayList<Runnable> p = new ArrayList<>();
        private boolean q = true;
        private int k = 0;
        private int l = 0;
        private boolean n = true;
        private int m = 1;

        GLThread(WeakReference<GLTextureView> weakReference) {
            this.s = weakReference;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            setName("GLThread " + getId());
            try {
                l();
            } catch (InterruptedException unused) {
            } finally {
                GLTextureView.n.a(this);
            }
        }

        private void j() {
            if (this.i) {
                this.i = false;
                this.r.e();
            }
        }

        private void k() {
            if (this.h) {
                this.r.f();
                this.h = false;
                GLTextureView.n.c(this);
            }
        }

        /* JADX WARN: Code duplicated, block: B:107:0x017e A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:109:0x0191 A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:111:0x019b A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:114:0x01a9 A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:116:0x01b3 A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:120:0x01c5 A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:123:0x01d6  */
        /* JADX WARN: Code duplicated, block: B:125:0x01da A[Catch: all -> 0x020c, TryCatch #5 {all -> 0x020c, blocks: (B:4:0x001c, B:5:0x0020, B:92:0x0155, B:94:0x015d, B:96:0x0165, B:97:0x0169, B:104:0x017a, B:107:0x017e, B:109:0x0191, B:111:0x019b, B:114:0x01a9, B:116:0x01b3, B:118:0x01bb, B:120:0x01c5, B:121:0x01cc, B:125:0x01da, B:126:0x01e5, B:133:0x01f4, B:142:0x020b, B:128:0x01e7, B:129:0x01f0, B:99:0x016b, B:100:0x0174, B:6:0x0021, B:8:0x0025, B:17:0x0036, B:19:0x003e, B:90:0x0152, B:20:0x004b, B:22:0x0051, B:24:0x0060, B:26:0x0064, B:28:0x0070, B:30:0x0079, B:32:0x007d, B:34:0x0082, B:36:0x0086, B:41:0x0098, B:43:0x00a2, B:39:0x0092, B:45:0x00a7, B:47:0x00b1, B:48:0x00b6, B:50:0x00ba, B:52:0x00be, B:54:0x00c2, B:55:0x00c5, B:56:0x00d2, B:58:0x00d6, B:60:0x00da, B:62:0x00e6, B:63:0x00f2, B:65:0x00f8, B:69:0x0100, B:71:0x010a, B:73:0x0110, B:75:0x011c, B:76:0x0123, B:77:0x0124, B:79:0x0128, B:81:0x012c, B:83:0x0134, B:85:0x0138, B:87:0x013c, B:89:0x0148, B:139:0x01ff), top: B:163:0x001c, inners: #0, #2, #3 }] */
        /* JADX WARN: Code duplicated, block: B:127:0x01e6  */
        /* JADX WARN: Code duplicated, block: B:134:0x01f5  */
        /* JADX WARN: Code duplicated, block: B:135:0x01f8  */
        /* JADX WARN: Code duplicated, block: B:137:0x01fb  */
        /* JADX WARN: Code duplicated, block: B:169:0x0175 A[SYNTHETIC] */
        private void l() throws InterruptedException {
            GLTextureView gLTextureView;
            int iD;
            GLTextureView gLTextureView2;
            GLTextureView gLTextureView3;
            boolean z;
            boolean z2;
            boolean z3;
            this.r = new EglHelper(this.s);
            this.h = false;
            this.i = false;
            boolean z4 = false;
            boolean z5 = false;
            boolean z6 = false;
            GL10 gl10 = null;
            int i = 0;
            int i2 = 0;
            boolean z7 = false;
            boolean z8 = false;
            boolean z9 = false;
            boolean z10 = false;
            boolean z11 = false;
            while (true) {
                Runnable runnableRemove = null;
                while (true) {
                    try {
                        synchronized (GLTextureView.n) {
                            while (true) {
                                if (this.a) {
                                    synchronized (GLTextureView.n) {
                                        j();
                                        k();
                                    }
                                    return;
                                }
                                if (!this.p.isEmpty()) {
                                    runnableRemove = this.p.remove(0);
                                    break;
                                }
                                if (this.d != this.c) {
                                    z = this.c;
                                    this.d = this.c;
                                    GLTextureView.n.notifyAll();
                                } else {
                                    z = false;
                                }
                                if (this.j) {
                                    j();
                                    k();
                                    this.j = false;
                                    z5 = true;
                                }
                                if (z4) {
                                    j();
                                    k();
                                    z4 = false;
                                }
                                if (z && this.i) {
                                    j();
                                }
                                if (z && this.h) {
                                    GLTextureView gLTextureView4 = this.s.get();
                                    if (!(gLTextureView4 == null ? false : gLTextureView4.y) || GLTextureView.n.a()) {
                                        k();
                                    }
                                }
                                if (z && GLTextureView.n.b()) {
                                    this.r.f();
                                }
                                if (!this.e && !this.g) {
                                    if (this.i) {
                                        j();
                                    }
                                    this.g = true;
                                    this.f = false;
                                    GLTextureView.n.notifyAll();
                                }
                                if (this.e && this.g) {
                                    this.g = false;
                                    GLTextureView.n.notifyAll();
                                }
                                if (z6) {
                                    this.o = true;
                                    GLTextureView.n.notifyAll();
                                    z6 = false;
                                    z11 = false;
                                }
                                if (m()) {
                                    if (!this.h) {
                                        if (z5) {
                                            z5 = false;
                                        } else if (GLTextureView.n.b(this)) {
                                            try {
                                                this.r.a();
                                                this.h = true;
                                                GLTextureView.n.notifyAll();
                                                z7 = true;
                                            } catch (RuntimeException e) {
                                                GLTextureView.n.c(this);
                                                throw e;
                                            }
                                        }
                                    }
                                    if (!this.h || this.i) {
                                        z2 = z8;
                                    } else {
                                        this.i = true;
                                        z2 = true;
                                        z9 = true;
                                        z10 = true;
                                    }
                                    if (this.i) {
                                        if (this.q) {
                                            i = this.k;
                                            i2 = this.l;
                                            z3 = false;
                                            this.q = false;
                                            z2 = true;
                                            z10 = true;
                                            z11 = true;
                                        } else {
                                            z3 = false;
                                        }
                                        this.n = z3;
                                        GLTextureView.n.notifyAll();
                                        z8 = z2;
                                        break;
                                    }
                                    z8 = z2;
                                }
                                GLTextureView.n.wait();
                            }
                        }
                        if (runnableRemove != null) {
                            break;
                        }
                        if (z8) {
                            if (!this.r.b()) {
                                synchronized (GLTextureView.n) {
                                    this.f = true;
                                    GLTextureView.n.notifyAll();
                                }
                            } else {
                                z8 = false;
                                if (z9) {
                                    GL10 gl11 = (GL10) this.r.c();
                                    GLTextureView.n.a(gl11);
                                    gl10 = gl11;
                                    z9 = false;
                                }
                                if (z7) {
                                    gLTextureView3 = this.s.get();
                                    if (gLTextureView3 != null) {
                                        gLTextureView3.q.onSurfaceCreated(gl10, this.r.d);
                                    }
                                    z7 = false;
                                }
                                if (z10) {
                                    gLTextureView2 = this.s.get();
                                    if (gLTextureView2 != null) {
                                        gLTextureView2.q.onSurfaceChanged(gl10, i, i2);
                                    }
                                    z10 = false;
                                }
                                gLTextureView = this.s.get();
                                if (gLTextureView != null) {
                                    gLTextureView.q.onDrawFrame(gl10);
                                }
                                iD = this.r.d();
                                if (iD == 12288) {
                                    if (iD != 12302) {
                                        EglHelper.a("GLThread", "eglSwapBuffers", iD);
                                        synchronized (GLTextureView.n) {
                                            this.f = true;
                                            GLTextureView.n.notifyAll();
                                        }
                                    } else {
                                        z4 = true;
                                    }
                                }
                                if (z11) {
                                    z6 = true;
                                }
                            }
                        } else {
                            if (z9) {
                                GL10 gl12 = (GL10) this.r.c();
                                GLTextureView.n.a(gl12);
                                gl10 = gl12;
                                z9 = false;
                            }
                            if (z7) {
                                gLTextureView3 = this.s.get();
                                if (gLTextureView3 != null) {
                                    gLTextureView3.q.onSurfaceCreated(gl10, this.r.d);
                                }
                                z7 = false;
                            }
                            if (z10) {
                                gLTextureView2 = this.s.get();
                                if (gLTextureView2 != null) {
                                    gLTextureView2.q.onSurfaceChanged(gl10, i, i2);
                                }
                                z10 = false;
                            }
                            gLTextureView = this.s.get();
                            if (gLTextureView != null) {
                                gLTextureView.q.onDrawFrame(gl10);
                            }
                            iD = this.r.d();
                            if (iD == 12288) {
                                if (iD != 12302) {
                                    EglHelper.a("GLThread", "eglSwapBuffers", iD);
                                    synchronized (GLTextureView.n) {
                                        this.f = true;
                                        GLTextureView.n.notifyAll();
                                    }
                                } else {
                                    z4 = true;
                                }
                            }
                            if (z11) {
                                z6 = true;
                            }
                        }
                    } catch (Throwable th) {
                        synchronized (GLTextureView.n) {
                            j();
                            k();
                            throw th;
                        }
                    }
                }
                runnableRemove.run();
            }
        }

        public boolean a() {
            return this.h && this.i && m();
        }

        private boolean m() {
            return !this.d && this.e && !this.f && this.k > 0 && this.l > 0 && (this.n || this.m == 1);
        }

        public void a(int i) {
            if (i >= 0 && i <= 1) {
                synchronized (GLTextureView.n) {
                    this.m = i;
                    GLTextureView.n.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("renderMode");
        }

        public int b() {
            int i;
            synchronized (GLTextureView.n) {
                i = this.m;
            }
            return i;
        }

        public void c() {
            synchronized (GLTextureView.n) {
                this.n = true;
                GLTextureView.n.notifyAll();
            }
        }

        public void d() {
            synchronized (GLTextureView.n) {
                this.e = true;
                GLTextureView.n.notifyAll();
                while (this.g && !this.b) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void e() {
            synchronized (GLTextureView.n) {
                this.e = false;
                GLTextureView.n.notifyAll();
                while (!this.g && !this.b) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void f() {
            synchronized (GLTextureView.n) {
                this.c = true;
                GLTextureView.n.notifyAll();
                while (!this.b && !this.d) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void g() {
            synchronized (GLTextureView.n) {
                this.c = false;
                this.n = true;
                this.o = false;
                GLTextureView.n.notifyAll();
                while (!this.b && this.d && !this.o) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void a(int i, int i2) {
            synchronized (GLTextureView.n) {
                this.k = i;
                this.l = i2;
                this.q = true;
                this.n = true;
                this.o = false;
                GLTextureView.n.notifyAll();
                while (!this.b && !this.d && !this.o && a()) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void h() {
            synchronized (GLTextureView.n) {
                this.a = true;
                GLTextureView.n.notifyAll();
                while (!this.b) {
                    try {
                        GLTextureView.n.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void i() {
            this.j = true;
            GLTextureView.n.notifyAll();
        }

        public void a(Runnable runnable) {
            if (runnable != null) {
                synchronized (GLTextureView.n) {
                    this.p.add(runnable);
                    GLTextureView.n.notifyAll();
                }
                return;
            }
            throw new IllegalArgumentException("r must not be null");
        }
    }

    static class LogWriter extends Writer {
        private StringBuilder a = new StringBuilder();

        LogWriter() {
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            a();
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() {
            a();
        }

        @Override // java.io.Writer
        public void write(char[] cArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                char c = cArr[i + i3];
                if (c == '\n') {
                    a();
                } else {
                    this.a.append(c);
                }
            }
        }

        private void a() {
            if (this.a.length() > 0) {
                LogUtil.v(GLTextureView.e, this.a.toString());
                StringBuilder sb = this.a;
                sb.delete(0, sb.length());
            }
        }
    }

    private void c() {
        if (this.p != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private static class GLThreadManager {
        private static String a = "GLThreadManager";
        private static final int g = 131072;
        private static final String h = "Q3Dimension MSM7500 ";
        private boolean b;
        private int c;
        private boolean d;
        private boolean e;
        private boolean f;
        private GLThread i;

        private GLThreadManager() {
        }

        public synchronized void a(GLThread gLThread) {
            gLThread.b = true;
            if (this.i == gLThread) {
                this.i = null;
            }
            notifyAll();
        }

        public boolean b(GLThread gLThread) {
            GLThread gLThread2 = this.i;
            if (gLThread2 == gLThread || gLThread2 == null) {
                this.i = gLThread;
                notifyAll();
                return true;
            }
            c();
            if (this.e) {
                return true;
            }
            GLThread gLThread3 = this.i;
            if (gLThread3 == null) {
                return false;
            }
            gLThread3.i();
            return false;
        }

        public void c(GLThread gLThread) {
            if (this.i == gLThread) {
                this.i = null;
            }
            notifyAll();
        }

        public synchronized boolean a() {
            return this.f;
        }

        public synchronized boolean b() {
            c();
            return !this.e;
        }

        public synchronized void a(GL10 gl10) {
            if (!this.d) {
                c();
                String strGlGetString = gl10.glGetString(GL20.cu);
                if (this.c < 131072) {
                    this.e = !strGlGetString.startsWith(h);
                    notifyAll();
                }
                this.f = this.e ? false : true;
                this.d = true;
            }
        }

        private void c() {
            if (this.b) {
                return;
            }
            this.b = true;
        }
    }
}

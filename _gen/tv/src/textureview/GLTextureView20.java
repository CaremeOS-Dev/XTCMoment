package com.badlogic.gdx.backends.android.textureview;

import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.badlogic.gdx.backends.android.surfaceview.ResolutionStrategy;
import com.xtc.system.account.constant.NotificationFlag;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: loaded from: classes.dex */
public class GLTextureView20 extends GLTextureView {
    static String e = "GLTextureView20";
    static int g;
    final ResolutionStrategy f;

    public GLTextureView20(Context context, ResolutionStrategy resolutionStrategy, int i) {
        super(context.getApplicationContext());
        g = i;
        this.f = resolutionStrategy;
        a(false, 16, 0);
    }

    public GLTextureView20(Context context, ResolutionStrategy resolutionStrategy) {
        this(context.getApplicationContext(), resolutionStrategy, 2);
    }

    public GLTextureView20(Context context, boolean z, int i, int i2, ResolutionStrategy resolutionStrategy) {
        super(context);
        this.f = resolutionStrategy;
        a(z, i, i2);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        ResolutionStrategy.MeasuredDimension measuredDimensionA = this.f.a(i, i2);
        setMeasuredDimension(measuredDimensionA.a, measuredDimensionA.b);
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        if (editorInfo != null) {
            editorInfo.imeOptions |= NotificationFlag.NOTIFICATION_FLAG_CUSTOM;
        }
        return new BaseInputConnection(this, false) { // from class: com.badlogic.gdx.backends.android.textureview.GLTextureView20.1
            @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
            public boolean deleteSurroundingText(int i, int i2) {
                if (Build.VERSION.SDK_INT >= 16 && i == 1 && i2 == 0) {
                    a(67);
                    return true;
                }
                return super.deleteSurroundingText(i, i2);
            }

            private void a(int i) {
                long jUptimeMillis = SystemClock.uptimeMillis();
                super.sendKeyEvent(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, i, 0, 0, -1, 0, 6));
                super.sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), jUptimeMillis, 1, i, 0, 0, -1, 0, 6));
            }
        };
    }

    private void a(boolean z, int i, int i2) {
        setEGLContextFactory(new ContextFactory());
        setEGLConfigChooser(z ? new ConfigChooser(8, 8, 8, 8, i, i2) : new ConfigChooser(5, 6, 5, 0, i, i2));
        setSurfaceTextureListener(this);
        if (z) {
            setOpaque(false);
        }
    }

    static class ContextFactory implements GLTextureView.EGLContextFactory {
        private static int a = 12440;

        ContextFactory() {
        }

        @Override // com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLContextFactory
        public EGLContext a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            Log.w(GLTextureView20.e, "creating OpenGL ES " + GLTextureView20.g + ".0 context");
            StringBuilder sb = new StringBuilder();
            sb.append("Before eglCreateContext ");
            sb.append(GLTextureView20.g);
            GLTextureView20.a(sb.toString(), egl10);
            EGLContext eGLContextEglCreateContext = egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{a, GLTextureView20.g, 12344});
            if ((!GLTextureView20.a("After eglCreateContext " + GLTextureView20.g, egl10) || eGLContextEglCreateContext == null) && GLTextureView20.g > 2) {
                Log.w(GLTextureView20.e, "Falling back to GLES 2");
                GLTextureView20.g = 2;
                return a(egl10, eGLDisplay, eGLConfig);
            }
            Log.w(GLTextureView20.e, "Returning a GLES " + GLTextureView20.g + " context");
            return eGLContextEglCreateContext;
        }

        @Override // com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLContextFactory
        public void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
            egl10.eglDestroyContext(eGLDisplay, eGLContext);
        }
    }

    static boolean a(String str, EGL10 egl10) {
        boolean z = true;
        while (true) {
            int iEglGetError = egl10.eglGetError();
            if (iEglGetError == 12288) {
                return z;
            }
            Log.e(e, String.format("%s: EGL error: 0x%x", str, Integer.valueOf(iEglGetError)));
            z = false;
        }
    }

    private static class ConfigChooser implements GLTextureView.EGLConfigChooser {
        private static int g = 4;
        private static int[] h = {12324, 4, 12323, 4, 12322, 4, 12352, g, 12344};
        protected int a;
        protected int b;
        protected int c;
        protected int d;
        protected int e;
        protected int f;
        private int[] i = new int[1];

        public ConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            this.a = i;
            this.b = i2;
            this.c = i3;
            this.d = i4;
            this.e = i5;
            this.f = i6;
        }

        @Override // com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLConfigChooser
        public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            egl10.eglChooseConfig(eGLDisplay, h, null, 0, iArr);
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            egl10.eglChooseConfig(eGLDisplay, h, eGLConfigArr, i, iArr);
            return a(egl10, eGLDisplay, eGLConfigArr);
        }

        public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            for (EGLConfig eGLConfig : eGLConfigArr) {
                int iA = a(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int iA2 = a(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (iA >= this.e && iA2 >= this.f) {
                    int iA3 = a(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int iA4 = a(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int iA5 = a(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    int iA6 = a(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (iA3 == this.a && iA4 == this.b && iA5 == this.c && iA6 == this.d) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }

        private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.i) ? this.i[0] : i2;
        }

        private void b(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
            int length = eGLConfigArr.length;
            Log.w(GLTextureView20.e, String.format("%d configurations", Integer.valueOf(length)));
            for (int i = 0; i < length; i++) {
                Log.w(GLTextureView20.e, String.format("Configuration %d:\n", Integer.valueOf(i)));
                a(egl10, eGLDisplay, eGLConfigArr[i]);
            }
        }

        private void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
            int[] iArr = {12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354};
            String[] strArr = {"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT"};
            int[] iArr2 = new int[1];
            for (int i = 0; i < iArr.length; i++) {
                int i2 = iArr[i];
                String str = strArr[i];
                if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i2, iArr2)) {
                    Log.w(GLTextureView20.e, String.format("  %s: %d\n", str, Integer.valueOf(iArr2[0])));
                } else {
                    while (egl10.eglGetError() != 12288) {
                    }
                }
            }
        }
    }
}

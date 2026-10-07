package com.badlogic.gdx.backends.android.textureview;

import android.util.Log;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: loaded from: classes.dex */
public class GdxEglConfigChooser implements GLTextureView.EGLConfigChooser {
    public static final int a = 12512;
    public static final int b = 12513;
    private static final int k = 4;
    private static final String l = "GdxEglConfigChooser";
    protected int c;
    protected int d;
    protected int e;
    protected int f;
    protected int g;
    protected int h;
    protected int i;
    private int[] m = new int[1];
    protected final int[] j = {12324, 4, 12323, 4, 12322, 4, 12352, 4, 12344};

    public GdxEglConfigChooser(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        this.c = i;
        this.d = i2;
        this.e = i3;
        this.f = i4;
        this.g = i5;
        this.h = i6;
        this.i = i7;
    }

    @Override // com.badlogic.gdx.backends.android.textureview.GLTextureView.EGLConfigChooser
    public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay) {
        int[] iArr = new int[1];
        egl10.eglChooseConfig(eGLDisplay, this.j, null, 0, iArr);
        int i = iArr[0];
        if (i <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] eGLConfigArr = new EGLConfig[i];
        egl10.eglChooseConfig(eGLDisplay, this.j, eGLConfigArr, i, iArr);
        return a(egl10, eGLDisplay, eGLConfigArr);
    }

    public EGLConfig a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
        EGLConfig eGLConfig;
        EGLConfig eGLConfig2;
        EGLConfig[] eGLConfigArr2 = eGLConfigArr;
        int length = eGLConfigArr2.length;
        EGLConfig eGLConfig3 = null;
        EGLConfig eGLConfig4 = null;
        EGLConfig eGLConfig5 = null;
        int i = 0;
        while (true) {
            if (i >= length) {
                eGLConfig = eGLConfig4;
                break;
            }
            eGLConfig = eGLConfigArr2[i];
            int iA = a(egl10, eGLDisplay, eGLConfig, 12325, 0);
            int iA2 = a(egl10, eGLDisplay, eGLConfig, 12326, 0);
            if (iA >= this.g && iA2 >= this.h) {
                int iA3 = a(egl10, eGLDisplay, eGLConfig, 12324, 0);
                int iA4 = a(egl10, eGLDisplay, eGLConfig, 12323, 0);
                int iA5 = a(egl10, eGLDisplay, eGLConfig, 12322, 0);
                int iA6 = a(egl10, eGLDisplay, eGLConfig, 12321, 0);
                if (eGLConfig3 == null && iA3 == 5 && iA4 == 6 && iA5 == 5 && iA6 == 0) {
                    eGLConfig3 = eGLConfig;
                }
                if (eGLConfig4 == null && iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                    if (this.i == 0) {
                        break;
                    }
                    eGLConfig4 = eGLConfig;
                }
                int iA7 = a(egl10, eGLDisplay, eGLConfig, 12338, 0);
                EGLConfig eGLConfig6 = eGLConfig3;
                int iA8 = a(egl10, eGLDisplay, eGLConfig, 12337, 0);
                if (eGLConfig5 == null && iA7 == 1 && iA8 >= this.i && iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                    eGLConfig2 = eGLConfig4;
                } else {
                    eGLConfig2 = eGLConfig4;
                    int iA9 = a(egl10, eGLDisplay, eGLConfig, 12512, 0);
                    int iA10 = a(egl10, eGLDisplay, eGLConfig, 12513, 0);
                    if (eGLConfig5 == null && iA9 == 1 && iA10 >= this.i && iA3 == this.c && iA4 == this.d && iA5 == this.e && iA6 == this.f) {
                    }
                    eGLConfig4 = eGLConfig2;
                    eGLConfig3 = eGLConfig6;
                }
                eGLConfig5 = eGLConfig;
                eGLConfig4 = eGLConfig2;
                eGLConfig3 = eGLConfig6;
            }
            i++;
            eGLConfigArr2 = eGLConfigArr;
            length = length;
        }
        if (eGLConfig5 != null) {
            return eGLConfig5;
        }
        return eGLConfig != null ? eGLConfig : eGLConfig3;
    }

    private int a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
        return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.m) ? this.m[0] : i2;
    }

    private void b(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {
        int length = eGLConfigArr.length;
        Log.w(l, String.format("%d configurations", Integer.valueOf(length)));
        for (int i = 0; i < length; i++) {
            Log.w(l, String.format("Configuration %d:\n", Integer.valueOf(i)));
            a(egl10, eGLDisplay, eGLConfigArr[i]);
        }
    }

    private void a(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
        int[] iArr = {12320, 12321, 12322, 12323, 12324, 12325, 12326, 12327, 12328, 12329, 12330, 12331, 12332, 12333, 12334, 12335, 12336, 12337, 12338, 12339, 12340, 12343, 12342, 12341, 12345, 12346, 12347, 12348, 12349, 12350, 12351, 12352, 12354, 12512, 12513};
        String[] strArr = {"EGL_BUFFER_SIZE", "EGL_ALPHA_SIZE", "EGL_BLUE_SIZE", "EGL_GREEN_SIZE", "EGL_RED_SIZE", "EGL_DEPTH_SIZE", "EGL_STENCIL_SIZE", "EGL_CONFIG_CAVEAT", "EGL_CONFIG_ID", "EGL_LEVEL", "EGL_MAX_PBUFFER_HEIGHT", "EGL_MAX_PBUFFER_PIXELS", "EGL_MAX_PBUFFER_WIDTH", "EGL_NATIVE_RENDERABLE", "EGL_NATIVE_VISUAL_ID", "EGL_NATIVE_VISUAL_TYPE", "EGL_PRESERVED_RESOURCES", "EGL_SAMPLES", "EGL_SAMPLE_BUFFERS", "EGL_SURFACE_TYPE", "EGL_TRANSPARENT_TYPE", "EGL_TRANSPARENT_RED_VALUE", "EGL_TRANSPARENT_GREEN_VALUE", "EGL_TRANSPARENT_BLUE_VALUE", "EGL_BIND_TO_TEXTURE_RGB", "EGL_BIND_TO_TEXTURE_RGBA", "EGL_MIN_SWAP_INTERVAL", "EGL_MAX_SWAP_INTERVAL", "EGL_LUMINANCE_SIZE", "EGL_ALPHA_MASK_SIZE", "EGL_COLOR_BUFFER_TYPE", "EGL_RENDERABLE_TYPE", "EGL_CONFORMANT", "EGL_COVERAGE_BUFFERS_NV", "EGL_COVERAGE_SAMPLES_NV"};
        int[] iArr2 = new int[1];
        for (int i = 0; i < iArr.length; i++) {
            int i2 = iArr[i];
            String str = strArr[i];
            if (egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i2, iArr2)) {
                Log.w(l, String.format("  %s: %d\n", str, Integer.valueOf(iArr2[0])));
            } else {
                egl10.eglGetError();
            }
        }
    }
}

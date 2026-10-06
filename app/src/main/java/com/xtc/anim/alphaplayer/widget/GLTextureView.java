package com.xtc.anim.alphaplayer.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLDebugHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.View;

import com.xtc.anim.alphaplayer.IMonitor;

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

/**
 * 基于 TextureView 的 OpenGL 承载视图，内部维护独立的 GL 渲染线程与 EGL 上下文。
 */
public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener, View.OnLayoutChangeListener {

    public static final int RENDERMODE_WHEN_DIRTY = 0;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;

    private static final String TAG = "GLTextureView";
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;

    private static final GLThreadManager glThreadManager = new GLThreadManager();

    private IMonitor monitor;

    private final WeakReference<GLTextureView> viewReference = new WeakReference<>(this);

    GLThread glThread;
    Renderer renderer;
    private boolean detached;

    EGLConfigChooser configChooser;
    EGLContextFactory contextFactory;
    EGLWindowSurfaceFactory windowSurfaceFactory;
    GLWrapper glWrapper;
    int debugFlags;
    int contextClientVersion;
    boolean preserveEGLContextOnPause;

    public GLTextureView(Context context) {
        super(context);
        init();
    }

    public GLTextureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (glThread != null) {
                glThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    public void setMonitor(IMonitor monitor) {
        this.monitor = monitor;
    }

    private void reportError(boolean result, String message) {
        if (monitor != null) {
            monitor.monitor(result, "unknown", 0, 0, message);
        }
    }

    public void setGLWrapper(GLWrapper glWrapper) {
        this.glWrapper = glWrapper;
    }

    public void setDebugFlags(int debugFlags) {
        this.debugFlags = debugFlags;
    }

    public int getDebugFlags() {
        return debugFlags;
    }

    public void setPreserveEGLContextOnPause(boolean preserveEGLContextOnPause) {
        this.preserveEGLContextOnPause = preserveEGLContextOnPause;
    }

    public boolean getPreserveEGLContextOnPause() {
        return preserveEGLContextOnPause;
    }

    public void setRenderer(Renderer renderer) {
        checkRenderThreadState();
        if (configChooser == null) {
            configChooser = new SimpleEGLConfigChooser(true);
        }
        if (contextFactory == null) {
            contextFactory = new DefaultContextFactory();
        }
        if (windowSurfaceFactory == null) {
            windowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.renderer = renderer;
        this.glThread = new GLThread(viewReference);
        this.glThread.start();
    }

    public void setEGLContextFactory(EGLContextFactory factory) {
        checkRenderThreadState();
        contextFactory = factory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        checkRenderThreadState();
        windowSurfaceFactory = factory;
    }

    public void setEGLConfigChooser(EGLConfigChooser configChooser) {
        checkRenderThreadState();
        this.configChooser = configChooser;
    }

    public void setEGLConfigChooser(boolean needDepth) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
        setEGLConfigChooser(new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setEGLContextClientVersion(int version) {
        checkRenderThreadState();
        contextClientVersion = version;
    }

    public void setRenderMode(int renderMode) {
        glThread.setRenderMode(renderMode);
    }

    public int getRenderMode() {
        return glThread.getRenderMode();
    }

    public void requestRender() {
        glThread.requestRender();
    }

    public void onSurfaceTextureCreated(SurfaceTexture surfaceTexture) {
        glThread.surfaceCreated(surfaceTexture);
    }

    public void onSurfaceTextureReleased(SurfaceTexture surfaceTexture) {
        glThread.surfaceDestroyed(surfaceTexture);
    }

    public void onSurfaceTextureSizeUpdated(SurfaceTexture surfaceTexture, int width, int height) {
        glThread.onWindowResize(width, height);
    }

    public void onPause() {
        glThread.onPause();
    }

    public void onResume() {
        glThread.onResume();
    }

    public void queueEvent(Runnable runnable) {
        glThread.queueEvent(runnable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (detached && renderer != null) {
            int renderMode = glThread != null ? glThread.getRenderMode() : RENDERMODE_CONTINUOUSLY;
            glThread = new GLThread(viewReference);
            if (renderMode != RENDERMODE_CONTINUOUSLY) {
                glThread.setRenderMode(renderMode);
            }
            glThread.start();
        }
        detached = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (glThread != null) {
            glThread.requestExitAndWait();
        }
        detached = true;
        super.onDetachedFromWindow();
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        onSurfaceTextureSizeUpdated(getSurfaceTexture(), right - left, bottom - top);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        onSurfaceTextureCreated(surfaceTexture);
        onSurfaceTextureSizeUpdated(surfaceTexture, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        onSurfaceTextureSizeUpdated(surfaceTexture, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        onSurfaceTextureReleased(surfaceTexture);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private void checkRenderThreadState() {
        if (glThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }
    /** EGL 配置选择器。 */
    public interface EGLConfigChooser {
        EGLConfig chooseConfig(EGL10 egl, EGLDisplay display);
    }

    /** EGL 上下文工厂。 */
    public interface EGLContextFactory {
        EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config);

        void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context);
    }

    /** EGL 窗口 Surface 工厂。 */
    public interface EGLWindowSurfaceFactory {
        EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow);

        void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface);
    }

    /** GL 对象包装器。 */
    public interface GLWrapper {
        GL wrap(GL gl);
    }

    /** 渲染器接口。 */
    public interface Renderer {
        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        void onDrawFrame(GL10 gl);

        void onSurfaceDestroyed(GL10 gl);
    }

    private class DefaultContextFactory implements EGLContextFactory {

        private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        @Override
        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attribList = {EGL_CONTEXT_CLIENT_VERSION, contextClientVersion, EGL10.EGL_NONE};
            int[] attributes = contextClientVersion == 0 ? null : attribList;
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attributes);
        }

        @Override
        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (egl.eglDestroyContext(display, context)) {
                return;
            }
            Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
            EglHelper.throwEglException("eglDestroyContex", egl.eglGetError());
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {

        @Override
        public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
            try {
                return egl.eglCreateWindowSurface(display, config, nativeWindow, null);
            } catch (IllegalArgumentException e) {
                Log.e("GLTextureView", "eglCreateWindowSurface", e);
                return null;
            }
        }

        @Override
        public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }

    private abstract class BaseConfigChooser implements EGLConfigChooser {

        protected final int[] configAttributes;

        public BaseConfigChooser(int[] attributes) {
            this.configAttributes = filterConfigSpec(attributes);
        }

        abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs);

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] numConfigs = new int[1];
            if (!egl.eglChooseConfig(display, configAttributes, null, 0, numConfigs)) {
                reportError(false, Log.getStackTraceString(new IllegalArgumentException("eglChooseConfig failed")));
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int count = numConfigs[0];
            if (count <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] configs = new EGLConfig[count];
            if (!egl.eglChooseConfig(display, configAttributes, configs, count, numConfigs)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig config = chooseConfig(egl, display, configs);
            if (config != null) {
                return config;
            }
            throw new IllegalArgumentException("No config chosen");
        }

        private int[] filterConfigSpec(int[] attributes) {
            if (contextClientVersion != 2) {
                return attributes;
            }
            int length = attributes.length;
            int[] extended = new int[length + 2];
            int end = length - 1;
            System.arraycopy(attributes, 0, extended, 0, end);
            extended[end] = 12352;
            extended[length] = 4;
            extended[length + 1] = 12344;
            return extended;
        }
    }

    private class ComponentSizeChooser extends BaseConfigChooser {

        protected int redSizeValue;
        protected int greenSizeValue;
        protected int blueSizeValue;
        protected int alphaSizeValue;
        protected int depthSizeValue;
        protected int stencilSizeValue;

        private final int[] value = new int[1];

        public ComponentSizeChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
            super(new int[]{12324, redSize, 12323, greenSize, 12322, blueSize, 12321, alphaSize, 12325, depthSize, 12326, stencilSize, 12344});
            this.redSizeValue = redSize;
            this.greenSizeValue = greenSize;
            this.blueSizeValue = blueSize;
            this.alphaSizeValue = alphaSize;
            this.depthSizeValue = depthSize;
            this.stencilSizeValue = stencilSize;
        }

        @Override
        EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int depth = findConfigAttrib(egl, display, config, 12325, 0);
                int stencil = findConfigAttrib(egl, display, config, 12326, 0);
                if (depth >= depthSizeValue && stencil >= stencilSizeValue) {
                    int red = findConfigAttrib(egl, display, config, 12324, 0);
                    int green = findConfigAttrib(egl, display, config, 12323, 0);
                    int blue = findConfigAttrib(egl, display, config, 12322, 0);
                    int alpha = findConfigAttrib(egl, display, config, 12321, 0);
                    if (red == redSizeValue && green == greenSizeValue && blue == blueSizeValue && alpha == alphaSizeValue) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            return egl.eglGetConfigAttrib(display, config, attribute, value) ? value[0] : defaultValue;
        }
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean needDepth) {
            super(8, 8, 8, 0, needDepth ? 16 : 0, 0);
        }
    }
    private static class EglHelper {

        private static final int EGL_SUCCESS = 12288;
        private static final int EGL_CONTEXT_LOST = 12302;
        private static final int EGL_BAD_NATIVE_WINDOW = 12299;

        EGL10 egl;
        EGLDisplay eglDisplay;
        EGLSurface eglSurface;
        EGLConfig eglConfig;
        EGLContext eglContext;

        private final WeakReference<GLTextureView> viewReference;

        public EglHelper(WeakReference<GLTextureView> viewReference) {
            this.viewReference = viewReference;
        }

        public void start() {
            this.egl = (EGL10) EGLContext.getEGL();
            this.eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.eglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (!this.egl.eglInitialize(this.eglDisplay, new int[2])) {
                throw new RuntimeException("eglInitialize failed");
            }
            GLTextureView view = this.viewReference.get();
            if (view != null) {
                this.eglConfig = view.configChooser.chooseConfig(this.egl, this.eglDisplay);
                this.eglContext = view.contextFactory.createContext(this.egl, this.eglDisplay, this.eglConfig);
            } else {
                this.eglConfig = null;
                this.eglContext = null;
            }
            if (this.eglContext == null || this.eglContext == EGL10.EGL_NO_CONTEXT) {
                this.eglContext = null;
                throwEglException("createContext");
            }
            this.eglSurface = null;
        }

        public boolean createSurface() {
            if (this.egl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.eglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.eglConfig == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            destroySurface();
            GLTextureView view = this.viewReference.get();
            if (view != null) {
                this.eglSurface = view.windowSurfaceFactory.createWindowSurface(this.egl, this.eglDisplay, this.eglConfig, view.getSurfaceTexture());
            } else {
                this.eglSurface = null;
            }
            EGLSurface surface = this.eglSurface;
            if (surface == null || surface == EGL10.EGL_NO_SURFACE) {
                if (this.egl.eglGetError() == EGL_BAD_NATIVE_WINDOW) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }
            if (this.egl.eglMakeCurrent(this.eglDisplay, surface, surface, this.eglContext)) {
                return true;
            }
            logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.egl.eglGetError());
            return false;
        }

        GL createGL() {
            GL gl = this.eglContext.getGL();
            GLTextureView view = this.viewReference.get();
            if (view == null) {
                return gl;
            }
            if (view.glWrapper != null) {
                gl = view.glWrapper.wrap(gl);
            }
            if ((view.debugFlags & (DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS)) != 0) {
                return GLDebugHelper.wrap(gl,
                        (view.debugFlags & DEBUG_CHECK_GL_ERROR) != 0 ? 1 : 0,
                        (view.debugFlags & DEBUG_LOG_GL_CALLS) != 0 ? new LogWriter() : null);
            }
            return gl;
        }

        public int swap() {
            if (this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface)) {
                return EGL_SUCCESS;
            }
            return this.egl.eglGetError();
        }

        public void finish() {
            destroySurface();
        }

        private void destroySurface() {
            EGLSurface surface = this.eglSurface;
            if (surface == null || surface == EGL10.EGL_NO_SURFACE) {
                return;
            }
            this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            GLTextureView view = this.viewReference.get();
            if (view != null) {
                view.windowSurfaceFactory.destroySurface(this.egl, this.eglDisplay, surface);
            }
            this.eglSurface = null;
        }

        public void destroy() {
            if (this.eglContext != null) {
                GLTextureView view = this.viewReference.get();
                if (view != null) {
                    view.contextFactory.destroyContext(this.egl, this.eglDisplay, this.eglContext);
                }
                this.eglContext = null;
            }
            if (this.eglDisplay != null) {
                this.egl.eglTerminate(this.eglDisplay);
                this.eglDisplay = null;
            }
        }

        private void throwEglException(String function) {
            throwEglException(function, this.egl.eglGetError());
        }

        public static void throwEglException(String function, int error) {
            throw new RuntimeException(formatEglError(function, error));
        }

        public static void logEglErrorAsWarning(String tag, String function, int error) {
            Log.w(tag, formatEglError(function, error));
        }

        public static String formatEglError(String function, int error) {
            return function + " failed: " + error;
        }
    }

    static class LogWriter extends Writer {

        private final StringBuilder builder = new StringBuilder();

        @Override
        public void close() {
            flush();
        }

        @Override
        public void flush() {
            if (builder.length() > 0) {
                Log.v("GLTextureView", builder.toString());
                builder.delete(0, builder.length());
            }
        }

        @Override
        public void write(char[] buffer, int offset, int count) {
            for (int index = 0; index < count; index++) {
                char value = buffer[offset + index];
                if (value == '\n') {
                    flush();
                } else {
                    builder.append(value);
                }
            }
        }
    }
    static class GLThread extends Thread {

        private boolean shouldExit;
        private boolean exited;
        private boolean requestPaused = true;
        private boolean paused;
        private boolean hasSurface;
        private boolean waitingForSurface;
        private boolean surfaceIsBad;
        private boolean haveEglContext;
        private boolean haveEglSurface;
        private boolean askedToReleaseEglContext;
        private boolean wantRenderNotification;
        private boolean renderComplete;
        private boolean renderModeContinuous = true;
        private int renderMode = RENDERMODE_CONTINUOUSLY;
        private int width;
        private int height;
        private boolean sizeChanged = true;

        private EglHelper eglHelper;
        private final WeakReference<GLTextureView> viewReference;
        private final ArrayList<Runnable> eventQueue = new ArrayList<>();

        GLThread(WeakReference<GLTextureView> viewReference) {
            this.viewReference = viewReference;
        }

        @Override
        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException e) {
                // 线程被中断时直接退出
            } finally {
                glThreadManager.threadExiting(this);
            }
        }

        private void stopEglSurfaceLocked() {
            if (haveEglSurface) {
                haveEglSurface = false;
                eglHelper.finish();
            }
        }

        private void stopEglContextLocked() {
            if (haveEglContext) {
                eglHelper.destroy();
                haveEglContext = false;
                glThreadManager.releaseEglContextLocked(this);
            }
        }

        private void guardedRun() throws InterruptedException {
            eglHelper = new EglHelper(viewReference);
            haveEglContext = false;
            haveEglSurface = false;

            boolean createEglContext = false;
            boolean createEglSurface = false;
            boolean createGlInterface = false;
            boolean lostEglContext = false;
            boolean recreateEglContext = false;
            boolean surfaceChanged = false;
            boolean wantRenderNotification = false;
            boolean finishCreatingEglSurface = false;

            GL10 gl = null;
            int surfaceWidth = 0;
            int surfaceHeight = 0;

            while (true) {
                Runnable event = null;
                synchronized (glThreadManager) {
                    while (true) {
                        if (shouldExit) {
                            stopEglSurfaceLocked();
                            stopEglContextLocked();
                            return;
                        }
                        if (!eventQueue.isEmpty()) {
                            event = eventQueue.remove(0);
                            break;
                        }
                        boolean pausing = false;
                        if (paused != requestPaused) {
                            pausing = requestPaused;
                            paused = requestPaused;
                            glThreadManager.notifyAll();
                        }
                        if (askedToReleaseEglContext) {
                            askedToReleaseEglContext = false;
                            stopEglSurfaceLocked();
                            stopEglContextLocked();
                            recreateEglContext = true;
                        }
                        if (lostEglContext) {
                            stopEglSurfaceLocked();
                            stopEglContextLocked();
                            lostEglContext = false;
                        }
                        if (pausing && haveEglSurface) {
                            stopEglSurfaceLocked();
                        }
                        if (pausing && haveEglContext) {
                            GLTextureView view = viewReference.get();
                            if (view == null || !view.preserveEGLContextOnPause
                                    || glThreadManager.shouldReleaseEGLContextWhenPausing()) {
                                stopEglContextLocked();
                            }
                        }
                        if (pausing && glThreadManager.shouldTerminateEglContext() && eglHelper != null) {
                            eglHelper.destroy();
                        }
                        if (!hasSurface && !waitingForSurface) {
                            if (haveEglSurface) {
                                stopEglSurfaceLocked();
                            }
                            waitingForSurface = true;
                            surfaceIsBad = false;
                            glThreadManager.notifyAll();
                        }
                        if (hasSurface && waitingForSurface) {
                            waitingForSurface = false;
                            glThreadManager.notifyAll();
                        }
                        if (finishCreatingEglSurface) {
                            renderComplete = true;
                            glThreadManager.notifyAll();
                            finishCreatingEglSurface = false;
                            wantRenderNotification = false;
                        }
                        if (readyToDraw()) {
                            if (!haveEglContext) {
                                if (recreateEglContext) {
                                    recreateEglContext = false;
                                } else if (glThreadManager.tryAcquireEglContextLocked(this)) {
                                    try {
                                        eglHelper.start();
                                        haveEglContext = true;
                                        glThreadManager.notifyAll();
                                        createEglContext = true;
                                    } catch (RuntimeException e) {
                                        glThreadManager.releaseEglContextLocked(this);
                                        throw e;
                                    }
                                }
                            }
                            if (haveEglContext && !haveEglSurface) {
                                haveEglSurface = true;
                                createGlInterface = true;
                                surfaceChanged = true;
                            }
                            if (haveEglSurface) {
                                if (sizeChanged) {
                                    surfaceWidth = width;
                                    surfaceHeight = height;
                                    sizeChanged = false;
                                    renderModeContinuous = false;
                                    glThreadManager.notifyAll();
                                    createEglSurface = true;
                                    surfaceChanged = true;
                                    wantRenderNotification = true;
                                }
                                break;
                            }
                        }
                        glThreadManager.wait();
                    }
                }

                if (event != null) {
                    event.run();
                    continue;
                }
                if (createEglSurface) {
                    if (!eglHelper.createSurface()) {
                        synchronized (glThreadManager) {
                            surfaceIsBad = true;
                            glThreadManager.notifyAll();
                        }
                        continue;
                    }
                    createEglSurface = false;
                }
                if (createGlInterface) {
                    gl = (GL10) eglHelper.createGL();
                    glThreadManager.checkGLDriver(gl);
                    createGlInterface = false;
                }
                if (createEglContext) {
                    GLTextureView view = viewReference.get();
                    if (view != null) {
                        view.renderer.onSurfaceCreated(gl, eglHelper.eglConfig);
                    }
                    createEglContext = false;
                }
                if (surfaceChanged) {
                    GLTextureView view = viewReference.get();
                    if (view != null) {
                        view.renderer.onSurfaceChanged(gl, surfaceWidth, surfaceHeight);
                    }
                    surfaceChanged = false;
                }
                GLTextureView view = viewReference.get();
                if (view != null) {
                    view.renderer.onDrawFrame(gl);
                }
                int swapError = eglHelper.swap();
                if (swapError == EglHelper.EGL_SUCCESS) {
                    // 交换成功
                } else if (swapError == EglHelper.EGL_CONTEXT_LOST) {
                    lostEglContext = true;
                } else {
                    EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", swapError);
                    synchronized (glThreadManager) {
                        surfaceIsBad = true;
                        glThreadManager.notifyAll();
                    }
                }
                if (wantRenderNotification) {
                    finishCreatingEglSurface = true;
                }
            }
        }

        public boolean readyToDraw() {
            return hasSurface && haveEglContext && haveEglSurface && !surfaceIsBad && !paused
                    && width > 0 && height > 0 && (sizeChanged || renderMode == RENDERMODE_CONTINUOUSLY);
        }

        public void setRenderMode(int renderMode) {
            if (renderMode < 0 || renderMode > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (glThreadManager) {
                this.renderMode = renderMode;
                glThreadManager.notifyAll();
            }
        }

        public int getRenderMode() {
            synchronized (glThreadManager) {
                return renderMode;
            }
        }

        public void requestRender() {
            synchronized (glThreadManager) {
                sizeChanged = true;
                glThreadManager.notifyAll();
            }
        }

        public void surfaceCreated(SurfaceTexture surfaceTexture) {
            synchronized (glThreadManager) {
                hasSurface = true;
                glThreadManager.notifyAll();
                while (waitingForSurface && !exited) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed(SurfaceTexture surfaceTexture) {
            synchronized (glThreadManager) {
                hasSurface = false;
                glThreadManager.notifyAll();
                while (!waitingForSurface && !exited) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onPause() {
            synchronized (glThreadManager) {
                requestPaused = true;
                glThreadManager.notifyAll();
                while (!exited && !paused) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (glThreadManager) {
                requestPaused = false;
                renderModeContinuous = true;
                renderComplete = false;
                glThreadManager.notifyAll();
                while (!exited && paused && !renderComplete) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int width, int height) {
            synchronized (glThreadManager) {
                this.width = width;
                this.height = height;
                sizeChanged = true;
                renderModeContinuous = true;
                renderComplete = false;
                glThreadManager.notifyAll();
                while (!exited && !paused && !renderComplete && readyToDraw()) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestExitAndWait() {
            synchronized (glThreadManager) {
                shouldExit = true;
                glThreadManager.notifyAll();
                while (!exited) {
                    try {
                        glThreadManager.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEglContextLocked() {
            askedToReleaseEglContext = true;
            glThreadManager.notifyAll();
        }

        public void queueEvent(Runnable runnable) {
            if (runnable == null) {
                throw new IllegalArgumentException("r must not be null");
            }
            synchronized (glThreadManager) {
                eventQueue.add(runnable);
                glThreadManager.notifyAll();
            }
        }
    }

    private static class GLThreadManager {

        private static final String TAG = "GLThreadManager";
        private static final int GL_VERSION = 131072;
        private static final String MSM7500_PREFIX = "Q3Dimension MSM7500 ";

        private boolean haveCheckedDriver;
        private int glVersion;
        private boolean haveSetDriver;
        private boolean multipleContextsAllowed;
        private boolean releaseEglContext;
        private GLThread eglContextOwner;

        public synchronized void threadExiting(GLThread thread) {
            thread.exited = true;
            if (this.eglContextOwner == thread) {
                this.eglContextOwner = null;
            }
            notifyAll();
        }

        public boolean tryAcquireEglContextLocked(GLThread thread) {
            GLThread owner = this.eglContextOwner;
            if (owner == thread || owner == null) {
                this.eglContextOwner = thread;
                notifyAll();
                return true;
            }
            checkGLDriver();
            if (this.multipleContextsAllowed) {
                return true;
            }
            GLThread current = this.eglContextOwner;
            if (current == null) {
                return false;
            }
            current.requestReleaseEglContextLocked();
            return false;
        }

        public void releaseEglContextLocked(GLThread thread) {
            if (this.eglContextOwner == thread) {
                this.eglContextOwner = null;
            }
            notifyAll();
        }

        public synchronized boolean shouldReleaseEGLContextWhenPausing() {
            return this.releaseEglContext;
        }

        public synchronized boolean shouldTerminateEglContext() {
            checkGLDriver();
            return !this.multipleContextsAllowed;
        }

        public synchronized void checkGLDriver(GL10 gl) {
            if (this.haveSetDriver) {
                return;
            }
            checkGLDriver();
            String version = gl.glGetString(GL10.GL_VERSION);
            if (this.glVersion < GL_VERSION) {
                this.multipleContextsAllowed = !version.startsWith(MSM7500_PREFIX);
                notifyAll();
            }
            this.releaseEglContext = !this.multipleContextsAllowed;
            this.haveSetDriver = true;
        }

        private void checkGLDriver() {
            if (this.haveCheckedDriver) {
                return;
            }
            this.haveCheckedDriver = true;
        }
    }
}

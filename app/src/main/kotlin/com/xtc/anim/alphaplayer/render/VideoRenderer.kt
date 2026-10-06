package com.xtc.anim.alphaplayer.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Build
import android.view.Surface
import com.xtc.anim.alphaplayer.model.ScaleType
import com.xtc.anim.alphaplayer.utils.ShaderUtil
import com.xtc.anim.alphaplayer.utils.TextureCropUtil
import com.xtc.anim.alphaplayer.widget.IAlphaVideoView
import com.xtc.log.LogUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 透明视频渲染器：负责纹理绘制、Surface 准备以及首帧/结束回调。
 */
class VideoRenderer(private val alphaVideoView: IAlphaVideoView) : IRender {

    private val tag = "VideoRender"

    private val floatSizeBytes = 4
    private val triangleVerticesDataStrideBytes = floatSizeBytes * 5
    private val triangleVerticesDataPosOffset = 3
    private val glTextureExternalOes = 0x8D65
    private val triangleVerticesDataUvOffset = 3

    private var aPositionHandle = 0
    private var aTextureHandle = 0
    private var programId = 0
    private var uMvpMatrixHandle = 0
    private var uStMatrixHandle = 0

    private var triangleVertices = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.5f, 0.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
        -1.0f, 1.0f, 0.0f, 0.5f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 1.0f
    )
    private var triangleVerticesBuffer: FloatBuffer

    private val mvpMatrix = FloatArray(16)
    private val stMatrix = FloatArray(16)

    private val canDraw = AtomicBoolean(false)
    private val updateSurface = AtomicBoolean(false)

    private var surfaceTexture: SurfaceTexture? = null
    private var surfaceListener: IRender.SurfaceListener? = null
    private var scaleType: ScaleType = ScaleType.ScaleAspectFill

    init {
        triangleVerticesBuffer = ByteBuffer.allocateDirect(triangleVertices.size * floatSizeBytes)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        triangleVerticesBuffer.put(triangleVertices).position(0)
        Matrix.setIdentityM(stMatrix, 0)
    }

    override fun setScaleType(scaleType: ScaleType) {
        this.scaleType = scaleType
    }

    override fun measureInternal(viewWidth: Float, viewHeight: Float, videoWidth: Float, videoHeight: Float) {
        if (viewWidth <= 0f || viewHeight <= 0f || videoWidth <= 0f || videoHeight <= 0f) {
            return
        }
        triangleVertices = TextureCropUtil.getTextureCrop(scaleType, viewWidth, viewHeight, videoWidth, videoHeight)
        triangleVerticesBuffer = ByteBuffer.allocateDirect(triangleVertices.size * floatSizeBytes)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        triangleVerticesBuffer.put(triangleVertices).position(0)
    }

    override fun setSurfaceListener(surfaceListener: IRender.SurfaceListener) {
        this.surfaceListener = surfaceListener
    }

    override fun onDrawFrame(glUnused: GL10) {
        if (updateSurface.compareAndSet(true, false)) {
            try {
                surfaceTexture?.updateTexImage()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            surfaceTexture?.getTransformMatrix(stMatrix)
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        if (!canDraw.get()) {
            GLES20.glFinish()
            return
        }
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glUseProgram(programId)
        checkGlError("glUseProgram")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(glTextureExternalOes, textureId)
        triangleVerticesBuffer.position(triangleVerticesDataPosOffset)
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
            triangleVerticesDataStrideBytes, triangleVerticesBuffer)
        checkGlError("glVertexAttribPointer maPosition")
        GLES20.glEnableVertexAttribArray(aPositionHandle)
        checkGlError("glEnableVertexAttribArray aPositionHandle")
        triangleVerticesBuffer.position(triangleVerticesDataUvOffset)
        GLES20.glVertexAttribPointer(aTextureHandle, 3, GLES20.GL_FLOAT, false,
            triangleVerticesDataStrideBytes, triangleVerticesBuffer)
        checkGlError("glVertexAttribPointer aTextureHandle")
        GLES20.glEnableVertexAttribArray(aTextureHandle)
        checkGlError("glEnableVertexAttribArray aTextureHandle")
        Matrix.setIdentityM(mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uMvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uStMatrixHandle, 1, false, stMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        checkGlError("glDrawArrays")
        GLES20.glFinish()
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {
        programId = createProgram()
        if (programId == 0) {
            return
        }
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition")
        checkGlError("glGetAttribLocation aPosition")
        if (aPositionHandle == -1) {
            throw RuntimeException("Could not get attrib location for aPosition")
        }
        aTextureHandle = GLES20.glGetAttribLocation(programId, "aTextureCoord")
        checkGlError("glGetAttribLocation aTextureCoord")
        if (aTextureHandle == -1) {
            throw RuntimeException("Could not get attrib location for aTextureCoord")
        }
        uMvpMatrixHandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        checkGlError("glGetUniformLocation uMVPMatrix")
        if (uMvpMatrixHandle == -1) {
            throw RuntimeException("Could not get attrib location for uMVPMatrix")
        }
        uStMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix")
        checkGlError("glGetUniformLocation uSTMatrix")
        if (uStMatrixHandle == -1) {
            throw RuntimeException("Could not get attrib location for uSTMatrix")
        }
        prepareSurface()
    }

    override fun onSurfaceDestroyed(gl: GL10) {
        surfaceListener?.onSurfaceDestroyed()
    }

    private var textureId = 0

    private fun prepareSurface() {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]
        GLES20.glBindTexture(glTextureExternalOes, textureId)
        checkGlError("glBindTexture textureID")
        GLES20.glTexParameterf(glTextureExternalOes, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST.toFloat())
        GLES20.glTexParameterf(glTextureExternalOes, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
        val texture = SurfaceTexture(textureId)
        surfaceTexture = texture
        if (Build.VERSION.SDK_INT >= 15) {
            texture.setDefaultBufferSize(alphaVideoView.getMeasuredWidth(), alphaVideoView.getMeasuredHeight())
        }
        texture.setOnFrameAvailableListener(this)
        val surface = Surface(texture)
        surfaceListener?.onSurfacePrepared(surface)
        updateSurface.compareAndSet(true, false)
    }

    override fun onFrameAvailable(surface: SurfaceTexture) {
        updateSurface.compareAndSet(false, true)
        alphaVideoView.requestRender()
    }

    override fun onFirstFrame() {
        canDraw.compareAndSet(false, true)
        LogUtil.i(tag, "onFirstFrame:    canDraw = " + canDraw.get())
        alphaVideoView.requestRender()
    }

    override fun onCompletion() {
        canDraw.compareAndSet(true, false)
        LogUtil.i(tag, "onCompletion:   canDraw = " + canDraw.get())
        alphaVideoView.requestRender()
    }

    private fun loadShader(shaderType: Int, source: String): Int {
        val shader = GLES20.glCreateShader(shaderType)
        if (shader == 0) {
            return shader
        }
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] != 0) {
            return shader
        }
        LogUtil.e(tag, "Could not compile shader $shaderType:")
        LogUtil.e(tag, GLES20.glGetProgramInfoLog(shader))
        GLES20.glDeleteShader(shader)
        return 0
    }

    private fun createProgram(): Int {
        val vertexSource = ShaderUtil.loadShaderSource("vertex.sh", alphaVideoView.getView().resources)
        val fragmentSource = ShaderUtil.loadShaderSource("frag.sh", alphaVideoView.getView().resources)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) {
            return 0
        }
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) {
            return 0
        }
        val program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            GLES20.glAttachShader(program, fragmentShader)
            checkGlError("glAttachShader")
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != 1) {
                LogUtil.e(tag, "Could not link programID: ")
                LogUtil.e(tag, GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                return 0
            }
        }
        return program
    }

    private fun checkGlError(operation: String) {
        val error = GLES20.glGetError()
        if (error != 0) {
            LogUtil.e(tag, operation + ": glError " + error)
        }
    }
}
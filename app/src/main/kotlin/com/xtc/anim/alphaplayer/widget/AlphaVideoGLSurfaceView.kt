package com.xtc.anim.alphaplayer.widget

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import com.xtc.anim.alphaplayer.controller.IPlayerControllerExt
import com.xtc.anim.alphaplayer.model.ScaleType
import com.xtc.anim.alphaplayer.render.IRender
import java.util.HashMap

/**
 * 基于 GLSurfaceView 的透明视频承载视图。
 */
class AlphaVideoGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : GLSurfaceView(context, attr), IAlphaVideoView {

    val glContextVersion = 2

    @Volatile
    private var surfaceCreated = false

    var videoWidth: Float = 0f
    var videoHeight: Float = 0f

    private var scaleType: ScaleType = ScaleType.ScaleAspectFill

    private var videoRenderer: IRender? = null
    private var playerController: IPlayerControllerExt? = null
    private var currentSurface: Surface? = null

    private val surfaceListener = object : IRender.SurfaceListener {
        override fun onSurfacePrepared(surface: Surface) {
            currentSurface?.release()
            currentSurface = surface
            surfaceCreated = true
            playerController?.surfacePrepared(surface)
            playerController?.resume()
        }

        override fun onSurfaceDestroyed() {
            currentSurface?.release()
            currentSurface = null
            surfaceCreated = false
        }
    }

    private var viewCache: HashMap<Int, View>? = null

    init {
        setEGLContextClientVersion(glContextVersion)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        addOnSurfacePreparedListener()
        setZOrderOnTop(true)
        setPreserveEGLContextOnPause(true)
    }

    /** 缓存 findViewById 结果，便于频繁取子视图。 */
    fun findCachedViewById(id: Int): View? {
        if (viewCache == null) {
            viewCache = HashMap()
        }
        val cached = viewCache!![id]
        if (cached != null) {
            return cached
        }
        val view = findViewById<View>(id)
        viewCache!![id] = view
        return view
    }

    fun clearViewCache() {
        viewCache?.clear()
    }

    private fun addOnSurfacePreparedListener() {
        videoRenderer?.setSurfaceListener(surfaceListener)
    }

    override fun addParentView(parentView: ViewGroup) {
        if (parentView.indexOfChild(this) == -1) {
            (parent as? ViewGroup)?.removeView(this)
            parentView.addView(this)
        }
    }

    override fun removeParentView(parentView: ViewGroup) {
        parentView.removeView(this)
    }

    override fun getView(): View = this

    override fun setPlayerController(playerController: IPlayerControllerExt) {
        this.playerController = playerController
    }

    override fun setVideoRenderer(renderer: IRender) {
        this.videoRenderer = renderer
        setRenderer(renderer)
        addOnSurfacePreparedListener()
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun setScaleType(scaleType: ScaleType) {
        this.scaleType = scaleType
        videoRenderer?.setScaleType(scaleType)
    }

    override fun getScaleType(): ScaleType = scaleType

    override fun measureInternal(videoWidth: Float, videoHeight: Float) {
        if (videoWidth > 0f && videoHeight > 0f) {
            this.videoWidth = videoWidth
            this.videoHeight = videoHeight
        }
        val currentRenderer = videoRenderer ?: return
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        queueEvent {
            currentRenderer.measureInternal(
                measuredWidth.toFloat(),
                measuredHeight.toFloat(),
                this.videoWidth,
                this.videoHeight
            )
        }
    }

    override fun onFirstFrame() {
        videoRenderer?.onFirstFrame()
    }

    override fun onCompletion() {
        videoRenderer?.onCompletion()
    }

    override fun isSurfaceCreated(): Boolean = surfaceCreated

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureInternal(videoWidth, videoHeight)
    }

    override fun release() {
        surfaceListener.onSurfaceDestroyed()
    }
}
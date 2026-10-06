package com.xtc.anim.alphaplayer.render

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.Surface
import com.xtc.anim.alphaplayer.model.ScaleType
import com.xtc.anim.alphaplayer.widget.GLTextureView

/**
 * 视频渲染器接口，兼容 GLSurfaceView 与 GLTextureView 两种宿主。
 */
interface IRender : GLSurfaceView.Renderer, GLTextureView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    fun measureInternal(viewWidth: Float, viewHeight: Float, videoWidth: Float, videoHeight: Float)

    fun setScaleType(scaleType: ScaleType)

    fun setSurfaceListener(surfaceListener: SurfaceListener)

    fun onCompletion()

    fun onFirstFrame()

    /**
     * Surface 生命周期回调。
     */
    interface SurfaceListener {

        fun onSurfacePrepared(surface: Surface)

        fun onSurfaceDestroyed()
    }
}
package com.xtc.anim.alphaplayer.widget

import android.view.View
import android.view.ViewGroup
import com.xtc.anim.alphaplayer.controller.IPlayerControllerExt
import com.xtc.anim.alphaplayer.model.ScaleType
import com.xtc.anim.alphaplayer.render.IRender

/**
 * 透明视频承载视图接口，暴露渲染与生命周期相关操作。
 */
interface IAlphaVideoView {

    fun addParentView(parentView: ViewGroup)

    fun removeParentView(parentView: ViewGroup)

    fun setPlayerController(playerController: IPlayerControllerExt)

    fun setVideoRenderer(renderer: IRender)

    fun setScaleType(scaleType: ScaleType)

    fun getScaleType(): ScaleType

    fun getView(): View

    fun getMeasuredWidth(): Int

    fun getMeasuredHeight(): Int

    fun setVisibility(visibility: Int)

    fun setLayoutParams(params: ViewGroup.LayoutParams)

    fun measureInternal(videoWidth: Float, videoHeight: Float)

    fun bringToFront()

    fun requestRender()

    fun isSurfaceCreated(): Boolean

    fun onFirstFrame()

    fun onCompletion()

    fun onPause()

    fun release()
}
package com.xtc.anim.alphaplayer.controller

import android.view.View
import android.view.ViewGroup
import com.xtc.anim.alphaplayer.IMonitor
import com.xtc.anim.alphaplayer.IPlayerAction
import com.xtc.anim.alphaplayer.model.DataSource

/**
 * 播放器控制器接口，负责视图挂载与播放生命周期控制。
 */
interface IPlayerController {

    fun attachAlphaView(parentView: ViewGroup)

    fun detachAlphaView(parentView: ViewGroup)

    fun getView(): View

    fun getPlayerType(): String

    fun getCurrentPosition(): Int

    fun setViewDefaultWidthAndHeight(width: Int, height: Int)

    fun setMonitor(monitor: IMonitor)

    fun setPlayerAction(playerAction: IPlayerAction)

    fun setLooperCount(looperCount: Int)

    fun setVisibility(visibility: Int)

    fun showFirstFrameAtEnd(show: Boolean)

    fun start(dataSource: DataSource)

    fun pause()

    fun resume()

    fun reset()

    fun release()

    fun stop()

    fun seekTo(pos: Int)
}
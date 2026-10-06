package com.xtc.anim.alphaplayer

import com.xtc.anim.alphaplayer.model.ScaleType

/**
 * 播放器动作回调，用于把播放状态变化通知给宿主 View。
 */
interface IPlayerAction {

    fun startAction(duration: Int)

    fun endAction()

    fun onPlayerReady()

    fun onFirstFrameStart()

    fun onPlayProcess(pos: Int)

    fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int, scaleType: ScaleType)
}
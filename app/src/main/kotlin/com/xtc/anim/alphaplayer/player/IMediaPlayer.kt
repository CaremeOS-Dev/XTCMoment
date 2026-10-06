package com.xtc.anim.alphaplayer.player

import android.view.Surface
import com.xtc.anim.alphaplayer.model.VideoInfo
import java.io.IOException

/**
 * 媒体播放器抽象接口，屏蔽系统播放器与自定义播放器差异。
 */
interface IMediaPlayer {

    /** 初始化播放器。 */
    fun initMediaPlayer()

    /** 异步准备播放。 */
    fun prepareAsync()

    /** 开始播放。 */
    fun start()

    /** 暂停播放。 */
    fun pause()

    /** 停止播放。 */
    fun stop()

    /** 重置播放器。 */
    fun reset()

    /** 释放播放器。 */
    fun release()

    /** 设置播放数据源。 */
    @Throws(IOException::class)
    fun setDataSource(dataPath: String)

    /** 设置渲染 Surface。 */
    fun setSurface(surface: Surface)

    /** 设置是否循环播放。 */
    fun setLooping(looping: Boolean)

    /** 设置播放中是否保持屏幕常亮。 */
    fun setScreenOnWhilePlaying(onWhilePlaying: Boolean)

    /** 跳转到指定位置。 */
    fun seekTo(pos: Int)

    /** 获取当前播放位置。 */
    fun getCurrentPosition(): Int

    /** 获取视频时长。 */
    fun getDuration(): Int

    /** 获取视频尺寸信息。 */
    fun getVideoInfo(defaultWidth: Int, defaultHeight: Int): VideoInfo

    /** 获取播放器类型标识。 */
    fun getPlayerType(): String

    fun setOnPreparedListener(preparedListener: OnPreparedListener)

    fun setOnFirstFrameListener(firstFrameListener: OnFirstFrameListener)

    fun setOnErrorListener(errorListener: OnErrorListener)

    fun setOnCompletionListener(completionListener: OnCompletionListener)

    /** 播放准备完成回调。 */
    interface OnPreparedListener {
        fun onPrepared()
    }

    /** 首帧渲染回调。 */
    interface OnFirstFrameListener {
        fun onFirstFrame()
    }

    /** 播放错误回调。 */
    interface OnErrorListener {
        fun onError(what: Int, extra: Int, desc: String)
    }

    /** 播放结束回调。 */
    interface OnCompletionListener {
        fun onCompletion()
    }
}
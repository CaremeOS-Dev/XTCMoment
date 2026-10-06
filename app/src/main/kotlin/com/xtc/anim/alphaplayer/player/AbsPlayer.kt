package com.xtc.anim.alphaplayer.player

import android.content.Context

/**
 * 播放器基类，保存各类监听器并提供统一的设置入口。
 */
abstract class AbsPlayer(context: Context? = null) : IMediaPlayer {

    var completionListener: IMediaPlayer.OnCompletionListener? = null

    var preparedListener: IMediaPlayer.OnPreparedListener? = null

    var errorListener: IMediaPlayer.OnErrorListener? = null

    var firstFrameListener: IMediaPlayer.OnFirstFrameListener? = null

    override fun setOnCompletionListener(completionListener: IMediaPlayer.OnCompletionListener) {
        this.completionListener = completionListener
    }

    override fun setOnPreparedListener(preparedListener: IMediaPlayer.OnPreparedListener) {
        this.preparedListener = preparedListener
    }

    override fun setOnErrorListener(errorListener: IMediaPlayer.OnErrorListener) {
        this.errorListener = errorListener
    }

    override fun setOnFirstFrameListener(firstFrameListener: IMediaPlayer.OnFirstFrameListener) {
        this.firstFrameListener = firstFrameListener
    }
}
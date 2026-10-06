package com.xtc.anim.alphaplayer.player

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.text.TextUtils
import android.view.Surface
import com.xtc.anim.alphaplayer.model.VideoInfo
import com.xtc.log.LogUtil
import java.io.IOException

/**
 * 基于系统 MediaPlayer 的默认播放器实现。
 */
class DefaultSystemPlayer : AbsPlayer() {

    private val tag = "DefaultSystemPlayer"

    lateinit var mediaPlayer: MediaPlayer

    lateinit var dataPath: String

    private val retriever = MediaMetadataRetriever()

    override fun getPlayerType(): String = "DefaultSystemPlayer"

    override fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        LogUtil.d("Pet_DefaultSystemPlayer", "initMediaPlayer: $mediaPlayer")
        mediaPlayer.setOnCompletionListener {
            completionListener?.onCompletion()
        }
        mediaPlayer.setOnPreparedListener {
            preparedListener?.onPrepared()
        }
        mediaPlayer.setOnErrorListener { _, what, extra ->
            errorListener?.onError(what, extra, "")
            false
        }
        mediaPlayer.setOnInfoListener { _, what, _ ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                firstFrameListener?.onFirstFrame()
            }
            false
        }
    }

    override fun setSurface(surface: Surface) {
        mediaPlayer.setSurface(surface)
    }

    @Throws(IOException::class)
    override fun setDataSource(dataPath: String) {
        this.dataPath = dataPath
        mediaPlayer.setDataSource(dataPath)
    }

    override fun prepareAsync() {
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        LogUtil.d(tag, "start: ")
        mediaPlayer.start()
    }

    override fun pause() {
        LogUtil.d(tag, "pause: ")
        mediaPlayer.pause()
    }

    override fun stop() {
        LogUtil.d(tag, "stop: ")
        mediaPlayer.stop()
    }

    override fun reset() {
        mediaPlayer.reset()
        dataPath = ""
    }

    override fun release() {
        LogUtil.d(tag, "release: ")
        mediaPlayer.reset()
        mediaPlayer.release()
        dataPath = ""
    }

    override fun setLooping(looping: Boolean) {
        mediaPlayer.isLooping = looping
    }

    override fun setScreenOnWhilePlaying(onWhilePlaying: Boolean) {
        mediaPlayer.setScreenOnWhilePlaying(onWhilePlaying)
    }

    @Throws(Exception::class)
    override fun getVideoInfo(defaultWidth: Int, defaultHeight: Int): VideoInfo {
        if (TextUtils.isEmpty(dataPath)) {
            throw Exception("dataPath is null, please set setDataSource firstly!")
        }
        retriever.setDataSource(dataPath)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
            return VideoInfo(defaultWidth, defaultHeight)
        }
        return VideoInfo(
            Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)),
            Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        )
    }

    override fun getDuration(): Int = mediaPlayer.duration

    override fun seekTo(pos: Int) {
        mediaPlayer.seekTo(pos)
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition
}
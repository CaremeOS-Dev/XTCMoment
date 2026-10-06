package com.xtc.anim.alphaplayer.controller

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import com.xtc.anim.alphaplayer.IMonitor
import com.xtc.anim.alphaplayer.IPlayerAction
import com.xtc.anim.alphaplayer.model.AlphaVideoViewType
import com.xtc.anim.alphaplayer.model.Configuration
import com.xtc.anim.alphaplayer.model.DataSource
import com.xtc.anim.alphaplayer.player.DefaultSystemPlayer
import com.xtc.anim.alphaplayer.player.IMediaPlayer
import com.xtc.anim.alphaplayer.player.PlayerState
import com.xtc.anim.alphaplayer.render.VideoRenderer
import com.xtc.anim.alphaplayer.widget.AlphaVideoGLSurfaceView
import com.xtc.anim.alphaplayer.widget.AlphaVideoGLTextureView
import com.xtc.anim.alphaplayer.widget.IAlphaVideoView
import com.xtc.log.LogUtil
import java.io.File
import java.io.IOException

/**
 * 播放控制器：在独立线程上驱动 MediaPlayer 与渲染视图，并响应生命周期事件。
 */
class PlayerController(
    private val context: Context,
    owner: LifecycleOwner,
    private val alphaVideoViewType: AlphaVideoViewType,
    mediaPlayer: IMediaPlayer
) : IPlayerControllerExt, LifecycleObserver, Handler.Callback {

    companion object {
        const val INIT_MEDIA_PLAYER = 1
        const val SET_DATA_SOURCE = 2
        const val START = 3
        const val PAUSE = 4
        const val RESUME = 5
        const val STOP = 6
        const val DESTROY = 7
        const val SURFACE_PREPARED = 8
        const val RESET = 9
        const val TIMER = 10

        private const val TAG = "PlayerController"
        private const val TIMER_DELAY_MS = 20L
        private const val PLAY_THREAD_PRIORITY = 10

        @JvmStatic
        fun get(configuration: Configuration, mediaPlayer: IMediaPlayer? = null): PlayerController {
            return PlayerController(
                configuration.context,
                configuration.lifecycleOwner,
                configuration.alphaVideoViewType,
                mediaPlayer ?: DefaultSystemPlayer()
            )
        }
    }

    lateinit var alphaVideoView: IAlphaVideoView

    var looperTime: Int = 0

    @Volatile
    var isPlaying: Boolean = false

    var showFirstFrameAtEnd: Boolean = false

    @Volatile
    var playerState: PlayerState = PlayerState.NOT_PREPARED

    var monitorCallback: IMonitor? = null

    var playerActionCallback: IPlayerAction? = null

    lateinit var mediaPlayer: IMediaPlayer

    var videoDefaultWidth: Int = 0

    var videoDefaultHeight: Int = 0

    var workHandler: Handler? = null

    val mainHandler = Handler(Looper.getMainLooper())

    var playThread: HandlerThread? = null

    private var suspendDataSource: DataSource? = null

    private val preparedListener = object : IMediaPlayer.OnPreparedListener {
        override fun onPrepared() {
            sendMessage(getMessage(START, null), 0L)
        }
    }
    private val errorListener = object : IMediaPlayer.OnErrorListener {
        override fun onError(what: Int, extra: Int, desc: String) {
            monitor(false, what, extra, "mediaPlayer error, info: $desc")
            emitEndSignal()
        }
    }


    private val timerRunner = Runnable {
        sendMessage(getMessage(TIMER, null), TIMER_DELAY_MS)
    }

    init {
        this.mediaPlayer = mediaPlayer
        bindLifecycle(owner)
        initAlphaView()
        initPlayer()
    }
    private fun bindLifecycle(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
        playThread = HandlerThread("alpha-play-thread", PLAY_THREAD_PRIORITY)
        playThread!!.start()
        workHandler = Handler(playThread!!.looper, this)
    }

    /** 根据配置的视图类型创建承载视图并绑定渲染器。 */
    private fun initAlphaView() {
        alphaVideoView = when (alphaVideoViewType) {
            AlphaVideoViewType.GL_SURFACE_VIEW -> AlphaVideoGLSurfaceView(context, null)
            AlphaVideoViewType.GL_TEXTURE_VIEW -> AlphaVideoGLTextureView(context, null)
        }
        alphaVideoView.setLayoutParams(ViewGroup.LayoutParams(-1, -1))
        alphaVideoView.setPlayerController(this)
        alphaVideoView.setVideoRenderer(VideoRenderer(alphaVideoView))
    }

    private fun initPlayer() {
        sendMessage(getMessage(INIT_MEDIA_PLAYER, null), 0L)
    }

    override fun setPlayerAction(playerAction: IPlayerAction) {
        this.playerActionCallback = playerAction
    }

    override fun setMonitor(monitor: IMonitor) {
        this.monitorCallback = monitor
    }

    override fun setVisibility(visibility: Int) {
        alphaVideoView.setVisibility(visibility)
        if (visibility == 0) {
            alphaVideoView.bringToFront()
        }
    }

    override fun setLooperCount(looperCount: Int) {
        this.looperTime = looperCount
    }

    override fun setViewDefaultWidthAndHeight(width: Int, height: Int) {
        this.videoDefaultWidth = width
        this.videoDefaultHeight = height
    }

    override fun showFirstFrameAtEnd(show: Boolean) {
        this.showFirstFrameAtEnd = show
    }

    override fun attachAlphaView(parentView: ViewGroup) {
        alphaVideoView.addParentView(parentView)
    }

    override fun detachAlphaView(parentView: ViewGroup) {
        alphaVideoView.removeParentView(parentView)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        release()
    }

    private fun sendMessage(message: Message, delayMillis: Long) {
        val thread = playThread
        if (thread == null || !thread.isAlive || thread.isInterrupted) {
            return
        }
        if (workHandler == null) {
            workHandler = Handler(thread.looper, this)
        }
        workHandler?.sendMessageDelayed(message, delayMillis)
    }

    private fun getMessage(what: Int, obj: Any?): Message {
        val message = Message.obtain()
        message.what = what
        message.obj = obj
        return message
    }

    override fun surfacePrepared(surface: Surface) {
        sendMessage(getMessage(SURFACE_PREPARED, surface), 0L)
    }

    override fun start(dataSource: DataSource) {
        if (dataSource.isValid()) {
            setVisibility(0)
            sendMessage(getMessage(SET_DATA_SOURCE, dataSource), 0L)
        } else {
            emitEndSignal()
            monitor(false, 0, 0, "dataSource is invalid!")
        }
    }

    override fun pause() {
        sendMessage(getMessage(PAUSE, null), 0L)
    }

    override fun resume() {
        sendMessage(getMessage(RESUME, null), 0L)
    }

    override fun reset() {
        sendMessage(getMessage(RESET, null), 0L)
    }

    override fun stop() {
        sendMessage(getMessage(STOP, null), 0L)
    }

    override fun release() {
        sendMessage(getMessage(DESTROY, null), 0L)
    }

    override fun getView(): View = alphaVideoView.getView()

    override fun seekTo(pos: Int) {
        mediaPlayer.seekTo(pos)
    }

    override fun getCurrentPosition(): Int = mediaPlayer.getCurrentPosition()

    override fun getPlayerType(): String = mediaPlayer.getPlayerType()
    private fun initMediaPlayer() {
        try {
            mediaPlayer.initMediaPlayer()
        } catch (e: Exception) {
            mediaPlayer = DefaultSystemPlayer()
            mediaPlayer.initMediaPlayer()
        }
        mediaPlayer.setLooping(false)
        mediaPlayer.setOnFirstFrameListener(object : IMediaPlayer.OnFirstFrameListener {
            override fun onFirstFrame() {
                playerActionCallback?.onFirstFrameStart()
                alphaVideoView.onFirstFrame()
            }
        })
        mediaPlayer.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
            override fun onCompletion() {
            if (looperTime == -1) {
                if (playerState == PlayerState.PREPARED || playerState == PlayerState.STARTED) {
                    playerState = PlayerState.PREPARED
                    startPlay()
                }
                return
            }
            if (looperTime <= 0) {
                emitEndSignal()
                alphaVideoView.onCompletion()
                if (showFirstFrameAtEnd) {
                    alphaVideoView.onFirstFrame()
                }
                playerState = PlayerState.PAUSED
                monitor(true, 0, 0, "PAUSED")
                return
            }
            looperTime -= 1
            playerState = PlayerState.PREPARED
            startPlay()
            }
        })
    }

    private fun setDataSource(dataSource: DataSource) {
        try {
            setVideoFromFile(dataSource)
        } catch (e: Exception) {
            e.printStackTrace()
            monitor(false, 0, 0, "alphaVideoView set dataSource failure: " + Log.getStackTraceString(e))
            emitEndSignal()
        }
    }

    @Throws(IOException::class)
    private fun setVideoFromFile(dataSource: DataSource) {
        mediaPlayer.reset()
        playerState = PlayerState.NOT_PREPARED
        val orientation = context.resources.configuration.orientation
        val path = dataSource.getPath(orientation)
        val scaleType = dataSource.getScaleType(orientation)
        if (TextUtils.isEmpty(path) || !File(path).exists()) {
            monitor(false, -1, 0, "dataPath is empty or File is not exists. path = $path")
            emitEndSignal()
            return
        }
        if (scaleType != null) {
            alphaVideoView.setScaleType(scaleType)
        }
        mediaPlayer.setDataSource(path)
        mediaPlayer.setLooping(dataSource.isLooping)
        if (alphaVideoView.isSurfaceCreated()) {
            try {
                prepareAsync()
                return
            } catch (e: Exception) {
                LogUtil.e(TAG, "setVideoFromFile()", e)
                monitor(false, 0, 0, "prepare and start MediaPlayer failure!")
                emitEndSignal()
                return
            }
        }
        suspendDataSource = dataSource
    }

    @Throws(IOException::class)
    private fun resumeSuspendedDataSource() {
        val dataSource = suspendDataSource
        if (dataSource != null) {
            setVideoFromFile(dataSource)
        }
        suspendDataSource = null
    }

    private fun prepareAsync() {
        if (playerState == PlayerState.NOT_PREPARED || playerState == PlayerState.STOPPED) {
            mediaPlayer.setOnPreparedListener(preparedListener)
            mediaPlayer.setOnErrorListener(errorListener)
            mediaPlayer.prepareAsync()
        }
    }

    private fun startPlay() {
        when (playerState) {
            PlayerState.PREPARED -> {
                mediaPlayer.start()
                isPlaying = true
                workHandler?.postDelayed(timerRunner, TIMER_DELAY_MS)
                playerState = PlayerState.STARTED
                mainHandler.post {
                    try {
                        playerActionCallback?.startAction(mediaPlayer.getDuration())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        monitor(false, 0, 0, "start Action failure!")
                        emitEndSignal()
                    }
                }
            }
            PlayerState.PAUSED -> {
                mediaPlayer.start()
                playerState = PlayerState.STARTED
            }
            PlayerState.NOT_PREPARED, PlayerState.STOPPED -> {
                try {
                    prepareAsync()
                } catch (e: Exception) {
                    e.printStackTrace()
                    monitor(false, 0, 0, "prepare and start MediaPlayer failure!")
                    emitEndSignal()
                }
            }
            else -> {
            }
        }
    }
    @Throws(Exception::class)
    private fun parseVideoSize() {
        val videoInfo = mediaPlayer.getVideoInfo(videoDefaultWidth, videoDefaultHeight)
        alphaVideoView.measureInternal(videoInfo.videoWidth / 2f, videoInfo.videoHeight.toFloat())
        val scaleType = alphaVideoView.getScaleType()
        mainHandler.post {
            playerActionCallback?.onVideoSizeChanged(videoInfo.videoWidth / 2, videoInfo.videoHeight, scaleType)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            INIT_MEDIA_PLAYER -> initMediaPlayer()
            SET_DATA_SOURCE -> setDataSource(msg.obj as DataSource)
            START -> {
                try {
                    parseVideoSize()
                    playerState = PlayerState.PREPARED
                    startPlay()
                } catch (e: Exception) {
                    monitor(false, 0, 0, "start video failure: " + Log.getStackTraceString(e))
                    emitEndSignal()
                }
            }
            PAUSE -> {
                if (playerState == PlayerState.STARTED) {
                    mediaPlayer.pause()
                    playerState = PlayerState.PAUSED
                }
            }
            RESUME -> {
                if (isPlaying || looperTime > 0) {
                    workHandler?.postDelayed(timerRunner, TIMER_DELAY_MS)
                    startPlay()
                }
            }
            STOP -> {
                if (playerState == PlayerState.STARTED || playerState == PlayerState.PAUSED) {
                    mediaPlayer.pause()
                    playerState = PlayerState.PAUSED
                }
            }
            DESTROY -> {
                LogUtil.d("Pet_PlayerController", "DESTROY start")
                alphaVideoView.onPause()
                if (playerState == PlayerState.STARTED) {
                    mediaPlayer.pause()
                    playerState = PlayerState.PAUSED
                }
                if (playerState == PlayerState.PAUSED) {
                    mediaPlayer.stop()
                    playerState = PlayerState.STOPPED
                }
                mediaPlayer.release()
                alphaVideoView.release()
                playerState = PlayerState.RELEASE
                playThread?.let {
                    it.quit()
                    it.interrupt()
                }
                LogUtil.d("Pet_PlayerController", "DESTROY end")
            }
            SURFACE_PREPARED -> {
                try {
                    mediaPlayer.setSurface(msg.obj as Surface)
                    playerActionCallback?.onPlayerReady()
                    resumeSuspendedDataSource()
                } catch (e: Exception) {
                    monitor(false, 0, 0, "SURFACE_PREPARED video failure: " + Log.getStackTraceString(e))
                    emitEndSignal()
                }
            }
            RESET -> {
                mediaPlayer.reset()
                playerState = PlayerState.NOT_PREPARED
                isPlaying = false
                workHandler?.removeCallbacks(timerRunner)
            }
            TIMER -> {
                if (isPlaying && playerState != PlayerState.STOPPED && playerState != PlayerState.PAUSED) {
                    workHandler?.postDelayed(timerRunner, TIMER_DELAY_MS)
                }
                playerActionCallback?.onPlayProcess(getCurrentPosition())
            }
        }
        return true
    }

    private fun emitEndSignal() {
        isPlaying = false
        mainHandler.post {
            workHandler?.removeCallbacks(timerRunner)
            playerActionCallback?.endAction()
        }
    }

    private fun monitor(result: Boolean, what: Int, extra: Int, errorInfo: String) {
        monitorCallback?.monitor(result, getPlayerType(), what, extra, errorInfo)
    }
}

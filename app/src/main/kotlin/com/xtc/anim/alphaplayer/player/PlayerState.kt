package com.xtc.anim.alphaplayer.player

/**
 * 播放器状态机状态。
 */
enum class PlayerState {
    NOT_PREPARED,
    PREPARED,
    STARTED,
    PAUSED,
    STOPPED,
    RELEASE
}
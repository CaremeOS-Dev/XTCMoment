package com.xtc.anim.alphaplayer

/**
 * 播放器监控回调，用于上报播放结果与异常信息。
 */
interface IMonitor {

    fun monitor(result: Boolean, playType: String, what: Int, extra: Int, errorInfo: String)
}
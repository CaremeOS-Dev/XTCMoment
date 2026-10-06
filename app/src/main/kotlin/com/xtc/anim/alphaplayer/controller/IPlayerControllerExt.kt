package com.xtc.anim.alphaplayer.controller

import android.view.Surface

/**
 * 控制器扩展接口，额外接收 Surface 准备完成回调。
 */
interface IPlayerControllerExt : IPlayerController {

    fun surfacePrepared(surface: Surface)
}
package com.xtc.anim.alphaplayer.model

import android.arch.lifecycle.LifecycleOwner
import android.content.Context

/**
 * 播放器配置，描述上下文、生命周期宿主与承载视图类型。
 */
class Configuration(var context: Context, var lifecycleOwner: LifecycleOwner) {

    var alphaVideoViewType: AlphaVideoViewType = AlphaVideoViewType.GL_SURFACE_VIEW
}
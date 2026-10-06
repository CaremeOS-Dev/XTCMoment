package com.xtc.anim.alphaplayer.model

import android.text.TextUtils
import java.io.File

/**
 * 透明视频数据源，按横竖屏分别维护播放路径与缩放模式。
 */
class DataSource {

    lateinit var baseDir: String
    lateinit var portPath: String
    lateinit var landPath: String
    var portScaleType: ScaleType? = null
    var landScaleType: ScaleType? = null
    var isLooping: Boolean = false

    /** 设置根目录，自动补齐路径分隔符。 */
    fun setBaseDir(baseDir: String): DataSource {
        this.baseDir = if (!baseDir.contains(File.separator)) baseDir + File.separator else baseDir
        return this
    }

    /** 设置竖屏路径与缩放模式。 */
    fun setPortraitPath(portraitPath: String, scaleTypeIndex: Int): DataSource {
        this.portPath = portraitPath
        this.portScaleType = ScaleType.convertFrom(scaleTypeIndex)
        return this
    }

    /** 设置横屏路径与缩放模式。 */
    fun setLandscapePath(landscapePath: String, scaleTypeIndex: Int): DataSource {
        this.landPath = landscapePath
        this.landScaleType = ScaleType.convertFrom(scaleTypeIndex)
        return this
    }

    /** 设置是否循环播放。 */
    fun setLooping(isLooping: Boolean): DataSource {
        this.isLooping = isLooping
        return this
    }

    /** 按屏幕方向（1 为竖屏）取完整播放路径。 */
    fun getPath(orientation: Int): String {
        return baseDir + if (1 == orientation) portPath else landPath
    }

    /** 按屏幕方向（1 为竖屏）取缩放模式。 */
    fun getScaleType(orientation: Int): ScaleType? {
        return if (1 == orientation) portScaleType else landScaleType
    }

    /** 数据源是否完整可用。 */
    fun isValid(): Boolean {
        return !TextUtils.isEmpty(portPath) && !TextUtils.isEmpty(landPath)
                && portScaleType != null && landScaleType != null
    }
}
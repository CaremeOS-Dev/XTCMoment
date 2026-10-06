package com.xtc.watch

import com.xtc.utils.system.SystemProperty
import com.xtc.utils.system.SystemPropertyUtil
import com.xtc.utils.system.WatchModelUtil
import com.xtc.utils.system.XtcFunListUtils

/**
 * Typed access to the remote configuration. The values are resolved against the
 * current watch model, language and app version by [ConfigManager].
 */
object WatchConfigManager {

    private const val TAG = "WatchConfigManager"
    private const val OLD_MODEL_KEY = -1

    /**
     * Models that still read the config from the local file instead of the fun list.
     */
    private val OLD_WATCH_MODEL = listOf(
        "I13", "I13-HK", "I13-ID", "I13-MY", "I13-TH", SystemProperty.Model.Inner.I18,
        "I18-CN", "I18-HK", "I18-ID", "I18-TH", SystemProperty.Model.Inner.I20,
        SystemProperty.Model.Inner.I25, SystemProperty.Model.Inner.I25C, SystemProperty.Model.Inner.I25D,
        SystemProperty.Model.Inner.I26, SystemProperty.Model.Inner.I28, SystemProperty.Model.Inner.I32,
        SystemProperty.Model.Inner.ND01, "ND01-SN", "ND07", "I32-QCH", "ND01-HK"
    )

    private val configManager: ConfigManager by lazy { ConfigManager() }

    private var isOldModel = false

    /** Initializes the manager for [context]. */
    @JvmStatic
    fun init(context: android.content.Context) {
        isOldModel = OLD_WATCH_MODEL.contains(WatchModelUtil.getWatchInnerModel())
        configManager.init(context.applicationContext)
    }

    /** Schedules a config refresh after [timerTime] milliseconds. */
    @JvmStatic
    @JvmOverloads
    fun timerUpdateData(timerTime: Long = ConfigManager.UPDATE_TIME) {
        configManager.checkUpdateData(timerTime)
    }

    /** @return the int value configured for [key]. */
    @JvmStatic
    fun getInt(key: String): Int = getInt(key, 0)

    /** @return the int value configured for [key]. */
    @JvmStatic
    fun getInt(key: String, defaultValue: Int): Int = configManager.getInt(key) ?: defaultValue

    /** @return the float value configured for [key]. */
    @JvmStatic
    fun getFloat(key: String): Float = getFloat(key, 0.0f)

    /** @return the float value configured for [key]. */
    @JvmStatic
    fun getFloat(key: String, defaultValue: Float): Float = configManager.getFloat(key) ?: defaultValue

    /** @return the string value configured for [key]. */
    @JvmStatic
    fun getString(key: String): String = getString(key, "")

    /** @return the string value configured for [key]. */
    @JvmStatic
    fun getString(key: String, defaultValue: String): String = configManager.getString(key) ?: defaultValue

    /** @return the boolean value configured for [key]. */
    @JvmStatic
    fun getBoolean(key: String): Boolean = getBoolean(key, 0, false)

    /** @return the boolean value configured for [key] with [defaultValue]. */
    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = getBoolean(key, 0, defaultValue)

    /**
     * @return the boolean value configured for [key]. Old models read the value from the
     * fun list when a valid [funListCode] is supplied.
     */
    @JvmStatic
    fun getBoolean(key: String, funListCode: Int, defaultValue: Boolean): Boolean {
        if (funListCode > OLD_MODEL_KEY && isOldModel) {
            return XtcFunListUtils.isSupported(funListCode)
        }
        return configManager.getBoolean(key) ?: defaultValue
    }

    /** @return the inner model reported by {@code ro.product.innermodel}. */
    private fun getSystemInnerModel(): String = SystemPropertyUtil.get(SystemProperty.PRODUCT_INNER_MODEL, "")
}
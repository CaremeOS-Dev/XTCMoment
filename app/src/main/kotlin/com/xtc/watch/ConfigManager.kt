package com.xtc.watch

import android.content.Context
import android.util.Log
import com.xtc.dataservice.api.HistoryClient
import com.xtc.dataservice.api.domain.DataPoint
import com.xtc.dataservice.api.listener.OnDataInsertListener
import com.xtc.dataservice.api.request.FindLastKnownDataRequest
import com.xtc.dataservice.api.request.RegisterDataInsertListenerRequest
import com.xtc.log.LogUtil
import com.xtc.utils.system.SystemProperty
import com.xtc.utils.system.SystemPropertyUtil
import com.xtc.utils.system.WatchModelUtil
import com.xtc.watch.bean.Condition
import com.xtc.watch.bean.Config
import com.xtc.watch.bean.NetResponseConfig
import com.xtc.watch.bean.NetUpdateParam
import com.xtc.watch.net.WatchConfigServiceImpl
import com.xtc.web.core.CoreConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.LinkedHashMap
import java.util.Locale

/**
 * Loads the remote configuration, matches the conditions for the current environment
 * and refreshes the config from the server when it expires.
 */
class ConfigManager {

    private var contextRef: WeakReference<Context>? = null
    private var config: NetResponseConfig? = null
    private var spManager: SpManager? = null
    private var condition: Condition? = null

    private val keys = LinkedHashMap<String, Config>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val lockObject = Any()

    init {
        checkUpdateData()
    }

    /** Binds the manager to [contextTemp] and loads the config in the background. */
    fun init(contextTemp: Context) {
        contextRef = WeakReference(contextTemp.applicationContext)
        spManager = SpManager(contextTemp)
        coroutineScope.launch(Dispatchers.IO) { initConfig() }
    }

    private fun initConfig() {
        synchronized(lockObject) {
            if (config != null) {
                return
            }
            LogUtil.i(TAG, "initConfig() ")
            val localConfig = readLocalConfig()
            if (localConfig != null) {
                for (entry in localConfig.configs ?: emptyList()) {
                    entry.key?.let { keys[it] = entry }
                    LogUtil.i(TAG, "initConfig() config = $entry")
                }
                val context = contextRef?.get()
                if (context != null) {
                    initCondition(context)
                    listenerData(context)
                    if (localConfig.nextUploadTime < System.currentTimeMillis()) {
                        updateData(context)
                    } else {
                        judgeNeedUpdate(context)
                    }
                }
            }
            config = localConfig
        }
    }

    private fun initCondition(context: Context) {
        val packageName = context.getPackageName()
        val appVersion = Utils.getAppVersion(context)
        var color = 0
        try {
            color = SystemPropertyUtil.getInt(SystemProperty.WATCH_COLOR, 0)
        } catch (e: Exception) {
            Log.e(TAG, "initCondition()", e)
        }
        condition = Condition(
            listOf(WatchModelUtil.getWatchInnerModel()),
            null,
            packageName,
            appVersion,
            WatchModelUtil.getLanguage(),
            color.toString()
        )
        Log.i(TAG, "initCondition() condition = $condition")
    }

    private fun updateData(context: Context) {
        coroutineScope.launch(Dispatchers.IO) { timerUpdate(WatchConfigServiceImpl(context, context.packageName), context) }
    }

    /** Schedules a config refresh after [timerTime] milliseconds. */
    @JvmOverloads
    fun checkUpdateData(timerTime: Long = UPDATE_TIME) {
        LogUtil.i(TAG, "checkUpdateData() timerTime = $timerTime")
        coroutineScope.launch(Dispatchers.IO) {
            delay(timerTime)
            contextRef?.get()?.let { updateData(it) }
        }
    }

    private fun timerUpdate(service: WatchConfigServiceImpl, context: Context) {
        val packageName = context.packageName
        val param = NetUpdateParam("", appPackage = packageName, appVersion = Utils.getAppVersion(context))
        val configVersion = config?.configVersion
        if (configVersion == null) {
            LogUtil.i(TAG, "timerUpdate() mConfig 未初始化 ")
            return
        }
        param.configVersion = configVersion
        service.getUpdate(param).subscribe(
            { success ->
                LogUtil.i(TAG, "onNext() 更新成功 = $success")
                spManager?.saveUpdateTime(System.currentTimeMillis())
                if (success) {
                    val updated = readLocalConfig()
                    updated?.configs?.forEach { entry ->
                        entry.key?.let { keys[it] = entry }
                        LogUtil.i(TAG, "timerUpdate() config = $entry")
                    }
                    config = updated
                }
            },
            { error -> LogUtil.i(TAG, "onError() 更新异常 e = $error") }
        )
    }

    private fun judgeNeedUpdate(context: Context) {
        HistoryClient(context)
            .findLastKnownData(FindLastKnownDataRequest.Builder().addDataType(UPDATE_DATA_TYPE).build())
            .onSuccess { value: List<DataPoint> ->
                LogUtil.i(TAG, "onSuccess() value = $value")
                if (value.isNotEmpty()) {
                    val updateTime = spManager?.getUpdateTime() ?: 0L
                    val dataTime = value[0].getLongField(1)
                    if (dataTime != null && updateTime < dataTime) {
                        updateData(context)
                    }
                }
            }
    }

    private fun listenerData(context: Context) {
        HistoryClient(context).registerDataInsertListener(
            RegisterDataInsertListenerRequest.Builder().addDataType(UPDATE_DATA_TYPE).build(),
            OnDataInsertListener { dataPoint ->
                LogUtil.i(TAG, "listenerData() p0 = $dataPoint")
                val updateTime = spManager?.getUpdateTime() ?: 0L
                val dataTime = dataPoint.getLongField(1)
                if (dataTime != null && updateTime < dataTime) {
                    updateData(context)
                }
            }
        )
    }

    /** @return the int value configured for [key], or null. */
    fun getInt(key: String): Int? = find(key)?.let { config -> resolveValue(config, condition, 4)?.toIntOrNull() }

    /** @return the int value configured for [key] with [versionEqualType], or null. */
    fun getInt(key: String, versionEqualType: Int): Int? =
        find(key)?.let { config -> resolveValue(config, condition, versionEqualType)?.toIntOrNull() }

    /** @return the string value configured for [key], or null. */
    fun getString(key: String): String? = find(key)?.let { config -> resolveValue(config, condition, 4) }

    /** @return the string value configured for [key] with [versionEqualType], or null. */
    fun getString(key: String, versionEqualType: Int): String? =
        find(key)?.let { config -> resolveValue(config, condition, versionEqualType) }

    /** @return the boolean value configured for [key]. */
    fun getBoolean(key: String): Boolean? {
        val value = find(key)?.let { config -> resolveValue(config, condition, 4) }
        return value?.lowercase(Locale.ROOT)?.contains(CoreConstants.SystemProp.IS_TRUE, ignoreCase = false) ?: false
    }

    /** @return the boolean value configured for [key] with [versionEqualType], or null. */
    fun getBoolean(key: String, versionEqualType: Int): Boolean? =
        find(key)?.let { config -> resolveValue(config, condition, versionEqualType) == CoreConstants.SystemProp.IS_TRUE }

    /** @return the float value configured for [key], or null. */
    fun getFloat(key: String): Float? = find(key)?.let { config -> resolveValue(config, condition, 4)?.toFloatOrNull() }

    /** @return the float value configured for [key] with [versionEqualType], or null. */
    fun getFloat(key: String, versionEqualType: Int): Float? =
        find(key)?.let { config -> resolveValue(config, condition, versionEqualType)?.toFloatOrNull() }

    private fun find(key: String): Config? {
        initConfig()
        return keys[key]
    }

    private fun readLocalConfig(): NetResponseConfig? {
        val context = contextRef?.get() ?: return null
        return ConfigFileUtil.readConfigFromLocal(context)
    }

    private fun resolveValue(config: Config, condition: Condition?, versionEqualType: Int): String? {
        val models = condition?.models ?: return config.defaultValue
        val appVersion = ConfigFileUtil.trimPointToInt(condition.appVersion)
        for (candidate in config.conditions ?: emptyList()) {
            val candidateVersion = ConfigFileUtil.trimPointToInt(candidate.appVersion)
            val candidateModels = candidate.models
            if (candidateModels == null || !candidateModels.containsAll(models)) {
                continue
            }
            if (candidate.appPackage?.isNotEmpty() == true && candidate.appPackage != condition.appPackage) {
                continue
            }
            if (candidate.language?.isNotEmpty() == true && candidate.language != condition.language) {
                continue
            }
            if (candidate.color?.isNotEmpty() == true && candidate.color != condition.color) {
                continue
            }
            if (isVersionConform(candidateVersion, appVersion, versionEqualType)) {
                return candidate.value
            }
        }
        return config.defaultValue
    }

    private fun isVersionConform(localVersionCode: Int?, conditionVersionCode: Int?, versionEqualType: Int): Boolean {
        if (localVersionCode == null || conditionVersionCode == null) {
            return true
        }
        return when (versionEqualType) {
            1 -> conditionVersionCode < localVersionCode
            2 -> conditionVersionCode > localVersionCode
            3 -> conditionVersionCode == localVersionCode
            4 -> conditionVersionCode >= localVersionCode
            5 -> conditionVersionCode <= localVersionCode
            else -> false
        }
    }

    companion object {
        private const val TAG = "ConfigManager"

        /** Data type used to store the last config update timestamp. */
        const val UPDATE_DATA_TYPE = 23

        /** Default refresh interval. */
        const val UPDATE_TIME = 7200000L
    }
}
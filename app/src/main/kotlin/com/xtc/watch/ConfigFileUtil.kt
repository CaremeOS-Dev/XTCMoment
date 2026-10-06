package com.xtc.watch

import android.content.Context
import com.xtc.log.LogUtil
import com.xtc.utils.encode.JSONUtil
import com.xtc.watch.bean.Config
import com.xtc.watch.bean.Condition
import com.xtc.watch.bean.NetResponseConfig
import com.google.gson.JsonObject
import java.io.File
import java.io.InputStream

/**
 * Reads and writes the remote-config JSON: the copy shipped in the assets, the one
 * inside the app files dir and the one dropped into the vendor system dir.
 */
object ConfigFileUtil {

    private const val TAG = "ConfigFileUtil"
    private const val CONFIG_FILE_NAME = ConfigConstant.Config.DEFAULT_CONFIG_NAME
    private const val SYSTEM_CONFIG_DIR = ConfigConstant.Config.DEFAULT_SYSTEM_CONFIG_NAME

    /** @return the config version of the stored config, or {@code 1.0.0}. */
    @JvmStatic
    fun getLocalConfigVersion(context: Context): String =
        readConfigFromLocal(context)?.configVersion ?: "1.0.0"

    /** @return the local config, preferring the files-dir copy over the assets copy. */
    @JvmStatic
    fun loadConfig(context: Context): NetResponseConfig? =
        readConfigFromDir(context) ?: readConfigFromDirThenAssets(context)

    /** @return the config bundled in the assets. */
    @JvmStatic
    fun readConfigFromAssets(context: Context): NetResponseConfig? {
        return try {
            val inputStream: InputStream = context.assets.open(CONFIG_FILE_NAME)
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes)
            inputStream.close()
            convertStrToConfig(String(bytes))
        } catch (e: Exception) {
            LogUtil.e(TAG, "readConfigFromAssets", e)
            null
        }
    }

    /**
     * @return the newer of the assets config and the vendor system-dir config.
     */
    @JvmStatic
    fun readConfigFromDirThenAssets(context: Context): NetResponseConfig? {
        val assetsConfig = readConfigFromAssets(context)
        val systemConfig = readConfigFromSystemDir(context)
        if (assetsConfig == null) {
            return systemConfig
        }
        return if (systemConfig != null && assetsConfig.getVersion() < systemConfig.getVersion()) {
            systemConfig
        } else {
            assetsConfig
        }
    }

    /** @return the effective local config, rewriting the stored copy when it is stale. */
    @JvmStatic
    fun readConfigFromLocal(context: Context): NetResponseConfig? {
        val dirConfig = readConfigFromDir(context) ?: return readLocalAndWrite(context)
        val appVersion = dirConfig.appVersion ?: return readLocalAndWrite(context)
        val bestConfig = readConfigFromDirThenAssets(context)
        return if (appVersion >= Utils.getAppVersion(context) || bestConfig == null
            || bestConfig.getVersion() <= dirConfig.getVersion()) {
            dirConfig
        } else {
            writeConfigBeanToDir(context, bestConfig)
            bestConfig
        }
    }

    private fun readLocalAndWrite(context: Context): NetResponseConfig? {
        val config = readConfigFromDirThenAssets(context)
        writeConfigBeanToDir(context, config)
        return config
    }

    /** @return the config stored in the app files dir. */
    @JvmStatic
    fun readConfigFromDir(context: Context): NetResponseConfig? {
        return try {
            val file = File(context.filesDir, CONFIG_FILE_NAME)
            LogUtil.i(TAG, "readConfigFromDir() configFile = " + file.path)
            if (!file.exists()) {
                LogUtil.e(TAG, "readConfigFromDir -> file is not exists")
                return null
            }
            val text = file.readText()
            if (text.isEmpty()) {
                LogUtil.e(TAG, "readConfigFromDir -> readText empty!!!")
                return null
            }
            convertStrToConfig(text)
        } catch (e: Exception) {
            LogUtil.e(TAG, "readConfigFromDir Exception ", e)
            null
        }
    }

    /** @return the config stored in the vendor system dir. */
    @JvmStatic
    fun readConfigFromSystemDir(context: Context): NetResponseConfig? {
        return try {
            val path = SYSTEM_CONFIG_DIR + context.packageName.replace(".", "_") + File.separator + CONFIG_FILE_NAME
            val file = File(path)
            LogUtil.i(TAG, "readConfigFromSystemDir() path = " + file.path)
            if (!file.exists()) {
                LogUtil.e(TAG, "readConfigFromSystemDir -> file is not exists")
                return null
            }
            val text = file.readText()
            if (text.isEmpty()) {
                LogUtil.e(TAG, "readConfigFromSystemDir -> readText empty!!!")
                return null
            }
            convertStrToConfig(text)
        } catch (e: Exception) {
            LogUtil.e(TAG, "readConfigFromSystemDir Exception ", e)
            null
        }
    }

    /** Merges [newConfig] into the stored config and persists it. */
    @JvmStatic
    fun updateConfigDir(context: Context, newConfig: NetResponseConfig?): Boolean {
        if (newConfig == null) {
            LogUtil.w(TAG, "updateConfigDir -> newConfig is empty")
            return false
        }
        val oldConfig = readConfigFromLocal(context)
        val version = newConfig.configVersion
        if (version.isNullOrEmpty() || newConfig.configs.isNullOrEmpty()) {
            LogUtil.w(TAG, "updateConfigDir -> 无需更新")
            return false
        }
        if (oldConfig == null || oldConfig.configs.isNullOrEmpty()) {
            writeConfigBeanToDir(context, newConfig)
            return true
        }
        oldConfig.configVersion = newConfig.configVersion
        oldConfig.nextUploadTime = newConfig.nextUploadTime
        oldConfig.expirationTime = newConfig.expirationTime
        val oldConfigs = oldConfig.configs!!
        val newConfigs = newConfig.configs!!
        val keys = oldConfigs.map { it.key }.toMutableList()
        for (config in newConfigs) {
            val index = keys.indexOf(config.key)
            if (index >= 0) {
                oldConfigs[index] = config
            } else {
                oldConfigs.add(config)
                keys.add(config.key)
            }
        }
        writeConfigBeanToDir(context, oldConfig)
        return true
    }

    /** Keeps only the config entries that target [packageName]. */
    @JvmStatic
    fun filterNetResponse(config: NetResponseConfig?, packageName: String): NetResponseConfig? {
        val source = config ?: return null
        val configs = source.configs
        if (configs.isNullOrEmpty()) {
            return source
        }
        source.updateNextUploadTime()
        val filtered = ArrayList<Config>()
        for (entry in configs) {
            if (entry.conditions?.any { it.appPackage == packageName } == true) {
                LogUtil.i(TAG, "filterNetResponse() $entry")
                filtered.add(entry)
            }
        }
        return NetResponseConfig(source.configVersion, source.expirationTime, source.nextUploadTime, filtered, null)
    }

    /** @return true when [oldVersion] is lower than [newVersion] ignoring the dots. */
    @JvmStatic
    fun isNewVersion(oldVersion: String?, newVersion: String?): Boolean =
        (oldVersion?.replace(".", "") ?: "") < (newVersion?.replace(".", "") ?: "")

    @JvmStatic
    fun convertConfigToStr(config: NetResponseConfig?): String? =
        (JSONUtil.fromJSON(JSONUtil.toJSON(config), JsonObject::class.java))?.toString()

    @JvmStatic
    fun convertStrToConfig(configStr: String?): NetResponseConfig? {
        if (configStr.isNullOrEmpty()) {
            return null
        }
        return JSONUtil.fromJSON(configStr, NetResponseConfig::class.java)
    }

    @JvmStatic
    fun trimPointToInt(versionStr: String?): Int? = versionStr?.replace(".", "")?.toIntOrNull()

    @JvmStatic
    fun writeConfigBeanToDir(context: Context, config: NetResponseConfig?) {
        if (config == null) {
            return
        }
        config.appVersion = Utils.getAppVersion(context)
        writeConfigStrToDir(context, convertConfigToStr(config))
    }

    @JvmStatic
    fun writeConfigStrToDir(context: Context, configStr: String?) {
        LogUtil.i(TAG, "writeConfigStrToDir() context = $context, configStr = $configStr")
        if (configStr.isNullOrEmpty()) {
            return
        }
        try {
            val file = File(context.filesDir, CONFIG_FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
            file.writeText(configStr)
        } catch (e: Exception) {
            LogUtil.e(TAG, "writeConfigStrToDir Exception ", e)
        }
    }

    @JvmStatic
    fun getConditionModels(condition: Condition): List<String>? = condition.models
}
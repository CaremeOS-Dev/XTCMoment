package com.xtc.watch.net

import android.content.Context
import com.xtc.watch.ConfigFileUtil
import com.xtc.watch.INetConfigService
import com.xtc.watch.bean.NetConfigParam
import com.xtc.watch.bean.NetResponseConfig
import com.xtc.watch.bean.NetUpdateParam
import rx.Observable
import rx.schedulers.Schedulers

/** Implementation of the remote-config service backed by [ConfigManagerHttpServiceProxy]. */
class WatchConfigServiceImpl(context: Context, val packageName: String) :
    ConfigBusinessService(context), INetConfigService {

    private val configProxy = ConfigManagerHttpServiceProxy(context)

    override fun getConfigByKeys(param: NetConfigParam): Observable<NetResponseConfig?> =
        configProxy.getConfigByKeys(param)
            .map { filterAndStore(it) }
            .subscribeOn(Schedulers.io())

    override fun getConfigFullFetch(param: NetUpdateParam): Observable<NetResponseConfig?> =
        configProxy.getConfigFullFetch(param)
            .map { filterAndStore(it) }
            .subscribeOn(Schedulers.io())

    override fun getUpdate(param: NetUpdateParam): Observable<Boolean> =
        configProxy.getUpdate(param)
            .retryWhen(RetryWithDelay())
            .map { applyUpdate(it) }
            .subscribeOn(Schedulers.io())

    private fun filterAndStore(config: NetResponseConfig?): NetResponseConfig? {
        val filtered = ConfigFileUtil.filterNetResponse(config, packageName)
        ConfigFileUtil.writeConfigBeanToDir(appContext, filtered)
        return filtered
    }

    private fun applyUpdate(config: NetResponseConfig?): Boolean {
        if (config == null) {
            val current = ConfigFileUtil.readConfigFromLocal(appContext)
            current?.updateNextUploadTime()
            ConfigFileUtil.updateConfigDir(appContext, current)
            return false
        }
        val filtered = ConfigFileUtil.filterNetResponse(config, packageName)
        return ConfigFileUtil.updateConfigDir(appContext, filtered)
    }
}
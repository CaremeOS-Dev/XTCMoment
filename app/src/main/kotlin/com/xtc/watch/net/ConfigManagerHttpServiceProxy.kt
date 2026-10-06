package com.xtc.watch.net

import android.content.Context
import com.xtc.httplib.net.BaseUrlManager
import com.xtc.httplib.net.HttpRxJavaCallback
import com.xtc.httplib.net.HttpServiceProxy
import com.xtc.watch.bean.NetConfigParam
import com.xtc.watch.bean.NetResponseConfig
import com.xtc.watch.bean.NetUpdateParam
import rx.Observable

/** Retrofit proxy of the remote config endpoints. */
class ConfigManagerHttpServiceProxy(private val context: Context) : HttpServiceProxy(context) {

    fun getConfigByKeys(param: NetConfigParam): Observable<NetResponseConfig?> =
        service().getConfigByKeys(param).map(HttpRxJavaCallback())

    fun getConfigFullFetch(param: NetUpdateParam): Observable<NetResponseConfig?> =
        service().getConfigFullFetch(param).map(HttpRxJavaCallback())

    fun getUpdate(param: NetUpdateParam): Observable<NetResponseConfig?> =
        service().getUpdate(param).map(HttpRxJavaCallback())

    private fun service(): IConfigManagerHttpService =
        httpClient.request(BaseUrlManager.getGatewayUrl(context), IConfigManagerHttpService::class.java)
}
package com.xtc.watch

import com.xtc.watch.bean.NetConfigParam
import com.xtc.watch.bean.NetResponseConfig
import com.xtc.watch.bean.NetUpdateParam
import rx.Observable

/** Remote config endpoints. */
interface INetConfigService {

    fun getConfigByKeys(param: NetConfigParam): Observable<NetResponseConfig?>

    fun getConfigFullFetch(param: NetUpdateParam): Observable<NetResponseConfig?>

    fun getUpdate(param: NetUpdateParam): Observable<Boolean>
}
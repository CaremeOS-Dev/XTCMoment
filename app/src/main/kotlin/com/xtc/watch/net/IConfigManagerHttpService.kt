package com.xtc.watch.net

import android.content.Context
import com.xtc.httplib.bean.NetBaseResult
import com.xtc.watch.bean.NetConfigParam
import com.xtc.watch.bean.NetResponseConfig
import com.xtc.watch.bean.NetUpdateParam
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/** Remote config endpoints of the base service. */
interface IConfigManagerHttpService {

    @POST("/base-service/config/getConfigByKeys")
    fun getConfigByKeys(@Body param: NetConfigParam): Observable<NetBaseResult<NetResponseConfig?>>

    @POST("/base-service/config/fullFetch")
    fun getConfigFullFetch(@Body param: NetUpdateParam): Observable<NetBaseResult<NetResponseConfig?>>

    @POST("/base-service/config/getUpdate")
    fun getUpdate(@Body param: NetUpdateParam): Observable<NetBaseResult<NetResponseConfig?>>
}
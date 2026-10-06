package com.xtc.domain.config.http;

import com.xtc.httplib.bean.NetBaseResult;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Single;

/** Route-info endpoint. */
public interface HttpConfigApi {

    @POST("/route-service/route/getRouteInfo")
    Single<NetBaseResult<RouteResp>> getRouteInfo(@Body RouteReq routeReq);
}
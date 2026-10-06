package com.xtc.moment.share.model;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.share.model.bean.DbAppShare;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 应用分享信息查询接口。
 */
public interface INetInterface {

    @POST("/app_share")
    Observable<NetBaseResult<DbAppShare>> queryAppInfo(@Body String body);
}
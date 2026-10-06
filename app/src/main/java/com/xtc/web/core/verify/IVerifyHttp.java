package com.xtc.web.core.verify;

import com.xtc.httplib.bean.NetBaseResult;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/** 白名单拉取接口。 */
public interface IVerifyHttp {

    @GET("/social-service/config/getWhiteH5")
    Observable<NetBaseResult<List<String>>> getVerifyDatas();
}
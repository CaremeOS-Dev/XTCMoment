package com.xtc.moment.module.prerogative.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.prerogative.bean.LoadPersonalReq;
import com.xtc.moment.module.prerogative.bean.PersonalResponse;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 特权资源接口。
 */
public interface IPrerogativeHttp {

    @POST("/social-service/prerogative/getpersonal")
    Observable<NetBaseResult<PersonalResponse>> getPersonalData(@Body LoadPersonalReq request);

    @GET("/social-service/prerogative/getResource")
    Observable<NetBaseResult<List<ResourceNetResponse>>> getResource();
}
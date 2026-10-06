package com.xtc.web.core.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.web.core.data.req.ReqAccount;
import com.xtc.web.core.data.resp.RespAccountInfo;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/** 换取 openId 的接口定义。 */
public interface IAccountInfo {

    @POST("/oauth/openAccount/getOpenId")
    Observable<NetBaseResult<RespAccountInfo>> getOpenIdRequest(@Body ReqAccount reqAccount);
}
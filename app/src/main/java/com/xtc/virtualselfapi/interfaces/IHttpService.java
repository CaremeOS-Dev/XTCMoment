package com.xtc.virtualselfapi.interfaces;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.bean.net.req.BaseReqeust;
import com.xtc.virtualselfapi.bean.net.req.ReqFriendInfo;
import com.xtc.virtualselfapi.bean.net.req.ReqPosition;
import com.xtc.virtualselfapi.bean.net.req.RequestGetCostumeData;
import com.xtc.virtualselfapi.bean.net.req.RespCardInfo;
import com.xtc.virtualselfapi.bean.net.req.RespPostion;
import com.xtc.virtualselfapi.bean.net.req.RespResource;
import com.xtc.virtualselfapi.bean.net.req.RespVersion;
import com.xtc.virtualselfapi.bean.net.resp.RespCurrentCostumeInfo;
import com.xtc.virtualselfapi.bean.net.resp.RespFriendFormat;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 虚拟形象相关的网络接口定义。
 */
public interface IHttpService {

    @POST("/watch-game-service/card/info")
    Observable<NetBaseResult<RespCardInfo>> getCardInfo(@Body BaseReqeust request);

    @POST("/watch-game-service/card/icecostume")
    Observable<NetBaseResult<RespCurrentCostumeInfo>> getCurrentCostumeNew(@Body RequestGetCostumeData request);

    @POST("/watch-game-service/talentshow/getBansource")
    Observable<NetBaseResult<List<DbDecorate>>> getCustomDecorateList();

    @POST("watch-game-service/treasureHunt/getFriendList")
    Observable<NetBaseResult<List<RespFriendFormat>>> getFriendList(@Body ReqFriendInfo request);

    @POST("watch-game-service/treasureHunt/getLayout")
    Observable<NetBaseResult<RespPostion>> getPosition(@Body ReqPosition request);

    @GET("watch-game-service/treasureHunt/getAllOrnament")
    Observable<NetBaseResult<RespResource>> getResource();

    @GET("watch-game-service/treasureHunt/getVersionNumber")
    Observable<NetBaseResult<RespVersion>> getVersion();
}
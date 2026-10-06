package com.xtc.web.client.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.web.client.db.DbWebUrl;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/** H5 入口 url 列表拉取接口。 */
public interface IWebUrl {

    @GET("/smartwatch/server/getWatchH5Url/{packageName}")
    Observable<NetBaseResult<List<DbWebUrl>>> getWebUrlList(@Path("packageName") String packageName);
}
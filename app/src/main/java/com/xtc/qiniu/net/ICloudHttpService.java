package com.xtc.qiniu.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.qiniu.bean.NetDownloadTokenParam;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/** Cloud-storage token endpoints. */
public interface ICloudHttpService {

    @POST("token/download")
    Observable<NetBaseResult<List<String>>> getDownloadToken(@Body NetDownloadTokenParam param);

    @GET("token/upload/{spaceType}")
    Observable<NetBaseResult<String>> getUploadToken(@Path("spaceType") int spaceType);

    @GET("token/upload/{spaceType}/{key}")
    Observable<NetBaseResult<String>> getUploadToken(@Path("spaceType") int spaceType, @Path("key") String key);
}
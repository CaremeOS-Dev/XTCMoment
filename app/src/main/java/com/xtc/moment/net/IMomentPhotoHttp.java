package com.xtc.moment.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PhotoTokenVo;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 动态图片上传/下载 token 接口。
 */
public interface IMomentPhotoHttp {

    @POST("/moment/file/pic/transfer")
    Observable<NetBaseResult<PhotoTokenVo>> getUploadToken(@Body PhotoTokenParam param);

    @POST("/moment/file/pics/transfer")
    Observable<NetBaseResult<List<PhotoTokenVo>>> getUploadTokens(@Body List<PhotoTokenParam> paramList);

    @POST("/moment/file/download")
    Observable<NetBaseResult<DownloadUrlVo>> getDownloadUrl(@Body FileUrlParam param);

    @POST("/moment/file/batchDownload")
    Observable<NetBaseResult<DownloadUrlVo>> getDownloadBatchUrl(@Body FileBatchUrlParam param);
}
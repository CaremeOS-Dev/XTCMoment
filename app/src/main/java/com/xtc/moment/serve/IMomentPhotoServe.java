package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PhotoTokenVo;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;

import java.util.List;

import rx.Observable;

/**
 * 动态图片/视频上传下载服务接口。
 */
public interface IMomentPhotoServe {

    Observable<PhotoTokenVo> getUploadToken(PhotoTokenParam param);

    Observable<List<PhotoTokenVo>> getUploadTokens(List<PhotoTokenParam> paramList);

    Observable<VideoTokenVoResponse> getUploadVideoToken(VideoTokenParam param);

    Observable<PhotoTokenVo> uploadPhoto(String localPath, PhotoTokenVo tokenVo);

    Observable<String> getDownloadUrl(FileUrlParam param, DbMoment moment);

    Observable<String> getDownloadUrl(FileUrlParam param, PhotoMsg photoMsg, DbMoment moment);

    Observable<DownloadUrlVo> getDownloadBatchUrl(FileBatchUrlParam param, DbMoment moment, VideoMsg videoMsg);

    void updatePhotoMsgToDB(PhotoMsg photoMsg, boolean uploaded, DbMoment moment);
}
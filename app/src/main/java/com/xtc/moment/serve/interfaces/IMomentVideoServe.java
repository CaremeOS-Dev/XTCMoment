package com.xtc.moment.serve.interfaces;

import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.bean.VideoTokenVo;

import rx.Observable;

/**
 * 视频上传 token 服务接口。
 */
public interface IMomentVideoServe {
    Observable<VideoTokenVo> getUploadToken(VideoTokenParam param);
}
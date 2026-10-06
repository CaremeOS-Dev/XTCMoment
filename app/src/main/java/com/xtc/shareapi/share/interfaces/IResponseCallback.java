package com.xtc.shareapi.share.interfaces;

import com.xtc.shareapi.share.communication.BaseResponse;
import com.xtc.shareapi.share.communication.ShowMessageFromXTC;

/**
 * 分享请求与响应回调，用于把分享结果回传给调用方。
 */
public interface IResponseCallback {

    /** 收到来自好友圈/微聊的请求。 */
    void onReq(ShowMessageFromXTC.Request request);

    /** 收到分享结果响应。 */
    void onResp(boolean handled, BaseResponse baseResponse);
}
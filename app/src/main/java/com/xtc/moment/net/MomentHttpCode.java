package com.xtc.moment.net;

/**
 * 动态发布相关返回码。
 */
public interface MomentHttpCode {
    String PUBLISH_SUCCESS = "000001";
    String PUBLISH_PHOTO_INVALIDATE = "000008";
    String PUBLISH_LIMIT = "000060";
    String PUBLISH_INVALIDATE = "000061";
}
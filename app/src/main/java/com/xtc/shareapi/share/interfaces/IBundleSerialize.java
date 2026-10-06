package com.xtc.shareapi.share.interfaces;

import android.os.Bundle;

import com.xtc.shareapi.share.communication.BaseResponse;

/**
 * 可在 Bundle 与对象之间互相序列化的实体统一契约。
 * 分享相关的请求、响应与场景对象都通过该接口完成跨进程传输。
 */
public interface IBundleSerialize {

    /** 将自身写入 Bundle。 */
    void toBundle(Bundle bundle);

    /** 从 Bundle 还原自身。 */
    IBundleSerialize fromBundle(Bundle bundle);

    /** 校验参数是否合法。 */
    BaseResponse checkArgs();
}
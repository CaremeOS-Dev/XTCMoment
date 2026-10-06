package com.xtc.contactapi.contact.observable.interfaces;

import com.xtc.contactapi.base.BaseResponse;

import rx.Subscriber;

/**
 * 默认头像数据订阅回调。
 */
public interface IDefaultSubscribe {

    /** 将数据推送给订阅者。 */
    void subscribe(Subscriber<? super BaseResponse> subscriber);
}
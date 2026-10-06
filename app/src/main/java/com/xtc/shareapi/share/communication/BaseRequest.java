package com.xtc.shareapi.share.communication;

import android.os.Bundle;

import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IBundleSerialize;

/**
 * 分享请求基类，保存事务号并负责 Bundle 序列化。
 */
public abstract class BaseRequest implements IBundleSerialize {

    private String transaction;

    /** 请求类型。 */
    abstract int getType();

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    @Override
    public void toBundle(Bundle bundle) {
        bundle.putString(OpenApiConstant.BaseRequestConstant.BASE_REQUEST_TRANSACTION, transaction);
    }

    @Override
    public IBundleSerialize fromBundle(Bundle bundle) {
        this.transaction = bundle.getString(OpenApiConstant.BaseRequestConstant.BASE_REQUEST_TRANSACTION);
        return this;
    }

    @Override
    public BaseResponse checkArgs() {
        SendMessageToXTC.Response response = new SendMessageToXTC.Response();
        response.setCode(1);
        return response;
    }

    @Override
    public String toString() {
        return "BaseRequest{transaction='" + transaction + "'}";
    }
}
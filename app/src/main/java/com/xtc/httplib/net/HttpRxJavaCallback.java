package com.xtc.httplib.net;

import com.xtc.httplib.auth.HttpTokenExpireManager;
import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.httplib.okhttp.WatchHttpResultException;
import com.xtc.log.LogUtil;

import rx.functions.Func1;

/** Unwraps a {@link NetBaseResult} into its payload or throws. */
public class HttpRxJavaCallback<T> implements Func1<NetBaseResult<T>, T> {

    @Override
    public T call(NetBaseResult<T> result) {
        if (result == null) {
            LogUtil.e("terrible error:http response of httpResponse is null!");
            throw new NullPointerException("terrible error:http response of httpResponse is null!");
        }
        String code = result.getCode();
        if (!NetBaseResult.SUCCESS.equals(code)) {
            throw new WatchHttpResultException(code, result.getWatchTips());
        }
        HttpTokenExpireManager.getInstance().resetCount();
        return result.getData();
    }
}
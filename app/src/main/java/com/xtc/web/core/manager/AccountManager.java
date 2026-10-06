package com.xtc.web.core.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqAccount;
import com.xtc.web.core.data.resp.RespAccountInfo;
import com.xtc.web.core.net.AccountProxy;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/** 开放平台账号管理器，负责向服务端换取 openId。 */
public class AccountManager {

    private static final String TAG = "AccountManager";
    private static AccountManager instance;
    AccountProxy proxy;

    public static AccountManager getInstance(Context context) {
        if (instance == null) {
            instance = new AccountManager(context);
        }
        return instance;
    }

    private AccountManager(Context context) {
        this.proxy = new AccountProxy(context);
    }

    /** 请求 openId，失败或结果为空时回调无参 complete()。 */
    public void getOpenId(ReqAccount reqAccount, final CompletionHandler<RespAccountInfo> completionHandler) {
        this.proxy.getOpenIdRequest(reqAccount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RespAccountInfo>() {
                    @Override
                    public void call(RespAccountInfo respAccountInfo) {
                        if (respAccountInfo != null && !TextUtils.isEmpty(respAccountInfo.getOpenId())) {
                            completionHandler.complete(respAccountInfo);
                        } else {
                            completionHandler.complete();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "getOpenId error ", throwable);
                        completionHandler.complete();
                    }
                });
    }
}
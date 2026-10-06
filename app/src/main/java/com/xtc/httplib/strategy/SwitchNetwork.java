package com.xtc.httplib.strategy;

import android.content.Context;

import com.xtc.httplib.netstate.SwitchNetworkManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.RequestBody;
import okhttp3.Response;

/** Retry strategy that switches the active network when a request fails. */
public class SwitchNetwork implements RetryInterface {

    public SwitchNetwork(Context context) {
    }

    @Override
    public void startRequest(Interceptor.Chain chain) {
        SwitchNetworkManager.getInstance().autoReset();
        RequestBody body = chain.request().body();
        if (body != null) {
            try {
                SwitchNetworkManager.getInstance().countUserNum(body.contentLength());
            } catch (IOException ignored) {
                // content length is best effort only
            }
        }
    }

    @Override
    public boolean tryRetry(Response response, Interceptor.Chain chain, Exception exception) {
        return SwitchNetworkManager.getInstance().trySwitchNetwork();
    }
}
package com.xtc.httplib.cache;

import com.xtc.httplib.rxadapter.BaseXtcOnSubscribe;

import retrofit2.Response;
import rx.Subscriber;

/** A request parked while the token is being refreshed. */
class RequestCacheData {
    private long cacheTime;
    private BaseXtcOnSubscribe<Response> onSubscribe;
    private Subscriber<? super Response> subscriber;

    public RequestCacheData(long cacheTime, BaseXtcOnSubscribe<Response> onSubscribe, Subscriber<? super Response> subscriber) {
        this.cacheTime = cacheTime;
        this.onSubscribe = onSubscribe;
        this.subscriber = subscriber;
    }

    public long getCacheTime() {
        return this.cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    public BaseXtcOnSubscribe<Response> getOnSubscribe() {
        return this.onSubscribe;
    }

    public void setOnSubscribe(BaseXtcOnSubscribe<Response> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public Subscriber<? super Response> getSubscriber() {
        return this.subscriber;
    }

    public void setSubscriber(Subscriber<? super Response> subscriber) {
        this.subscriber = subscriber;
    }
}
package com.xtc.aitext.net.http;

import android.content.Context;

import com.xtc.aitext.bean.AIDataBean;
import com.xtc.aitext.bean.AIRecordBean;
import com.xtc.aitext.bean.CreatBackBean;
import com.xtc.aitext.bean.CreatTextBody;
import com.xtc.aitext.bean.MainDataBody;
import com.xtc.aitext.bean.ObtainTimeBean;
import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;

import rx.Observable;

/**
 * AI 文案网络代理。
 */
public class AITextHttpProxy extends HttpServiceProxy {

    private static volatile AITextHttpProxy instance;

    private final IAITextHttpProxy httpProxy;

    private AITextHttpProxy(Context context) {
        super(context);
        this.httpProxy = httpClient.request(BaseUrlManager.getTcxGatewayUrl(context), IAITextHttpProxy.class);
    }

    public static AITextHttpProxy getInstance(Context context) {
        if (instance == null) {
            synchronized (AITextHttpProxy.class) {
                if (instance == null) {
                    instance = new AITextHttpProxy(context);
                }
            }
        }
        return instance;
    }

    /** 查询主页数据。 */
    public Observable<AIDataBean> queryHomeData(MainDataBody body) {
        return this.httpProxy.home(body).map(new HttpRxJavaCallback<AIDataBean>());
    }

    /** 创建 AI 文案。 */
    public Observable<CreatBackBean> createText(CreatTextBody body) {
        return this.httpProxy.creation(body).map(new HttpRxJavaCallback<CreatBackBean>());
    }

    /** 领取次数。 */
    public Observable<ObtainTimeBean> obtainTime(String accessType, MainDataBody body) {
        return this.httpProxy.obtainTime(accessType, body).map(new HttpRxJavaCallback<ObtainTimeBean>());
    }

    /** 查询创作记录。 */
    public Observable<AIRecordBean> queryRecord(MainDataBody body) {
        return this.httpProxy.getUserRecord(body).map(new HttpRxJavaCallback<AIRecordBean>());
    }
}
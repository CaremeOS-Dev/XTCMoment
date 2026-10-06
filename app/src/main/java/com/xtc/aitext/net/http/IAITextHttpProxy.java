package com.xtc.aitext.net.http;

import com.xtc.aitext.bean.AIDataBean;
import com.xtc.aitext.bean.AIRecordBean;
import com.xtc.aitext.bean.CreatBackBean;
import com.xtc.aitext.bean.CreatTextBody;
import com.xtc.aitext.bean.MainDataBody;
import com.xtc.aitext.bean.ObtainTimeBean;
import com.xtc.httplib.bean.NetBaseResult;

import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * AI 文案网络接口定义。
 */
public interface IAITextHttpProxy {

    /** 创建 AI 文案。 */
    @POST("/ai-operation-service/userAiText/creation")
    Observable<NetBaseResult<CreatBackBean>> creation(@Body CreatTextBody creatTextBody);

    /** 查询创作记录。 */
    @POST("/ai-operation-service/userAiText/getUserRecord")
    Observable<NetBaseResult<AIRecordBean>> getUserRecord(@Body MainDataBody mainDataBody);

    /** 查询主页数据。 */
    @POST("/ai-operation-service/aiText/home")
    Observable<NetBaseResult<AIDataBean>> home(@Body MainDataBody mainDataBody);

    /** 领取次数。 */
    @POST("/ai-operation-service/aiTextObtain/obtainTime/{accessType}")
    Observable<NetBaseResult<ObtainTimeBean>> obtainTime(@Path("accessType") String accessType, @Body MainDataBody mainDataBody);
}
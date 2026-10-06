package com.xtc.web.core.callback;

import com.xtc.web.core.data.resp.RespVoiceResult;

/** 语音识别回调：H5 侧 asrStart/asrStop 通过它交给宿主实现。 */
public interface ASRCallback {

    /** 语音识别过程中的中间结果。 */
    void onResult(String result);

    /** 开始识别，识别完成后通过 handler 回传结果。 */
    void startASR(CompletionHandler<RespVoiceResult> completionHandler);

    /** 停止识别。 */
    void stopASR();
}
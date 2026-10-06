package com.xtc.web.core.callback;

import com.xtc.web.core.data.resp.RespVoiceResult;

/** 录音回调：H5 侧 startRecord/stopRecord 通过它交给宿主实现。 */
public interface RecordCallback {

    /** 录音过程中的中间结果。 */
    void onResult(String result);

    /** 开始录音。 */
    void startRecord(CompletionHandler<RespVoiceResult> completionHandler);

    /** 停止录音。 */
    void stopRecord();
}
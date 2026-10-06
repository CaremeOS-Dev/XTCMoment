package com.xtc.qiniu;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;

import java.util.HashMap;

/** Reports the failed uploads to the big-data collector. */
public class QiNiuUploadBehavior {

    private static final String TAG = "QiNiuUploadBehavior";
    private static final String ERROR = "error";
    private static final String STATUS_CODE = "statusCode";
    private static final String WEICHAT_QINIU_UPLOAD_FAIL = "weichat_qiniu_upload_fail";

    private QiNiuUploadBehavior() {
    }

    /** Reports one failed upload. */
    public static void uploadFail(final int statusCode, final String error) {
        LogUtil.d(TAG, "uploadFail() called with: statusCode = [" + statusCode + "], error = [" + error + "]");
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> extras = new HashMap<>();
                extras.put(STATUS_CODE, String.valueOf(statusCode));
                extras.put(ERROR, error);
                BehaviorUtil.customEvent(ContextUtils.getContext(), WEICHAT_QINIU_UPLOAD_FAIL, extras);
            }
        });
    }
}
package com.xtc.httplib.bigdata;

import android.content.Context;

import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;

import java.util.HashMap;

/** Reports RSA key failures to the big-data collector. */
public class RsaKeyBigData {

    private static final String TAG = LogTag.tag("RsaKeyBigData");

    /** Failure categories reported for key handling. */
    public interface KeyErrorType {
        int DB_KEY_INVALID = 5;
        int LOCAL_DECRYPT = 3;
        int RSA_ENCRYPT = 2;
        int SERVER_CODE = 1;
        int SYSTEM_KEY_NULL = 4;
    }

    private RsaKeyBigData() {
    }

    public static void uploadKeyError(Context context, int type) {
        LogUtil.d(TAG, "uploadKeyError: type = [" + type + "]");
        HashMap<String, String> data = new HashMap<>();
        data.put("type", String.valueOf(type));
        BehaviorUtil.customEvent(context, "account_auth_secret_key_error", data);
    }
}
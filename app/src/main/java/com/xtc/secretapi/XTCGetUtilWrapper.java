package com.xtc.secretapi;

import android.content.Context;
import android.util.Log;

/**
 * 签名/密钥获取的 native 桥接层，加载 rubbish 本地库。
 * native 方法名与本地库符号绑定，必须保持一致。
 */
public class XTCGetUtilWrapper {

    static {
        try {
            System.loadLibrary("rubbish");
        } catch (Exception e) {
            Log.e("XTCGetUtilWrapper", "load rubbish library error", e);
        }
    }

    /** 读取内置密钥。 */
    public static native String gete();

    /** 根据包名与内容计算签名。 */
    public static native String init(Context context, String content);
}
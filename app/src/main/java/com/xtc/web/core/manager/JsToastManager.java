package com.xtc.web.core.manager;

import android.content.Context;
import android.widget.Toast;

/** H5 调起原生 Toast 的简易管理器，复用同一个 Toast 实例。 */
public class JsToastManager {

    private static Toast instance;

    private static Toast newInstance(Context context) {
        if (instance == null) {
            instance = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        return instance;
    }

    public static void showToast(Context context, String message) {
        newInstance(context).setText(message);
        newInstance(context).show();
    }
}
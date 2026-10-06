package com.xtc.web.core.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqSp;

/** H5 读写宿主 SharedPreferences 的管理器。 */
public class SpManager {

    private static volatile SpManager instance;
    private static SharedPreferences sp;

    private SpManager(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static synchronized SpManager getInstance(Context context) {
        if (instance == null) {
            instance = new SpManager(context);
        }
        return instance;
    }

    private boolean saveString(String key, String value) {
        return sp.edit().putString(key, value).commit();
    }

    private boolean removeString(String key) {
        return sp.edit().remove(key).commit();
    }

    private String getString(String key) {
        return sp.getString(key, "");
    }

    public void saveSp(ReqSp reqSp, CompletionHandler<Boolean> completionHandler) {
        completionHandler.complete(Boolean.valueOf(saveString(reqSp.getKey(), reqSp.getValue())));
    }

    public void removeSp(ReqSp reqSp, CompletionHandler<Boolean> completionHandler) {
        completionHandler.complete(Boolean.valueOf(removeString(reqSp.getKey())));
    }

    public void getSp(String key, CompletionHandler<String> completionHandler) {
        completionHandler.complete(getString(key));
    }
}
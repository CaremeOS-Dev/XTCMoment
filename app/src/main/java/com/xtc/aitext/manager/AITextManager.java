package com.xtc.aitext.manager;

import android.content.Context;
import android.content.Intent;

import com.xtc.aitext.activity.AIEditedActivity;
import com.xtc.aitext.constant.Constant;
import com.xtc.aitext.net.http.AITextHttpProxy;
import com.xtc.aitext.weight.callback.AISuccessCallback;

/**
 * AI 文案管理器，负责启动 AI 编辑页与创建服务。
 */
public class AITextManager {

    private static final String TAG = "ai_text_AITextManager";

    private static volatile AITextManager instance;

    private final Context context;
    private AITextConfig config;
    private AISuccessCallback successCallback;

    public AITextManager(Context context) {
        this.context = context;
    }

    public static AITextManager getInstance(Context context) {
        if (instance == null) {
            synchronized (AITextHttpProxy.class) {
                if (instance == null) {
                    instance = new AITextManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public AITextManager setConfig(AITextConfig config) {
        this.config = config;
        return this;
    }

    public AITextConfig getConfig() {
        return config;
    }

    public AITextManager setSuccessCallback(AISuccessCallback callback) {
        this.successCallback = callback;
        return this;
    }

    public AISuccessCallback getSuccessCallback() {
        return successCallback;
    }

    /** 打开 AI 编辑页。 */
    public void startEditedActivity() {
        if (this.config == null) {
            return;
        }
        this.context.startActivity(new Intent(this.context, AIEditedActivity.class));
    }

    /** 启动创建服务。 */
    public void startCreateService(String imBean) {
        if (this.config == null) {
            return;
        }
        this.context.startService(new Intent().setAction(Constant.ACTION_AI_TEXT_SERVICE)
                .setPackage(this.context.getPackageName())
                .putExtra(Constant.EXTRA_SERIALIZE_IM_BEAN, imBean));
    }
}
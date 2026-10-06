package com.xtc.aitext.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.aitext.bean.AICreatStatusBean;
import com.xtc.aitext.bean.IMAiResultBean;
import com.xtc.aitext.behavior.AIBehaviorUtil;
import com.xtc.aitext.constant.Constant;
import com.xtc.aitext.manager.AITextManager;
import com.xtc.aitext.util.AIModuleUtil;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.Objects;

/**
 * AI 文案创建服务，接收 IM 通道回传的创作结果并广播状态。
 */
public class AITextCreateService extends IntentService {

    private static final String TAG = "ai_text_AITextCreateService";
    private static final String AI_EDITED_ACTIVITY_CLASS = "com.xtc.aitext.activity.AIEditedActivity";

    public AITextCreateService() {
        this("AITextCreateService");
    }

    public AITextCreateService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate......");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            LogUtil.e(TAG, "Service action is null.");
        } else if (Objects.equals(Constant.ACTION_AI_TEXT_SERVICE, intent.getAction())) {
            handleCreateResult(intent);
        }
    }

    private void handleCreateResult(Intent intent) {
        String extraContent = intent.getStringExtra(Constant.EXTRA_SERIALIZE_IM_BEAN);
        if (TextUtils.isEmpty(extraContent)) {
            LogUtil.i(TAG, "extraContent is empty...");
            return;
        }
        IMAiResultBean resultBean = JSONUtil.fromJSON(extraContent, IMAiResultBean.class);
        if (resultBean == null) {
            return;
        }
        AIBehaviorUtil.reportCreateResult(resultBean.isCallResult());
        if (!AIModuleUtil.isMainProcess(this) || !AIModuleUtil.isTopActivity(this, AI_EDITED_ACTIVITY_CLASS)) {
            LogUtil.i(TAG, " not foreground...");
            return;
        }
        if (!Objects.equals(resultBean.getClientType(), AITextManager.getInstance(this).getConfig().getClientType())) {
            LogUtil.i(TAG, "dealAiRecord: no same clientType...");
            return;
        }
        AICreatStatusBean statusBean = new AICreatStatusBean();
        statusBean.setAIBackContent(resultBean.getResultText());
        statusBean.setStatus(resultBean.isCallResult() ? 2 : 3);
        statusBean.setRemainTimes(resultBean.getRemainTimes());
        AIModuleUtil.notifyCreateStatus(this, statusBean);
    }
}
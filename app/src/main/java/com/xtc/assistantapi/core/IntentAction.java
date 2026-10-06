package com.xtc.assistantapi.core;

import android.net.Uri;

/**
 * 助手指令相关的 Intent Action、meta-data key 与内容提供者地址。
 */
public interface IntentAction {

    String ACTION_DIRECTIVE = "com.xtc.assistantapi.directive";
    String META_DATA_DIRECTIVE = "assistantapi-directive";
    String EXTRA_DIRECTIVE = "assistantapi-directive";
    String METHOD_CALL_ASSISTANT = "callXtcAssistant";
    Uri URI_LAUNCHER_ASSISTANT = Uri.parse("content://com.xtc.i3launcher.assistant");
    Uri URI_OPENAPI_ASSISTANT = Uri.parse("content://com.xtc.openapi.assistant");
}
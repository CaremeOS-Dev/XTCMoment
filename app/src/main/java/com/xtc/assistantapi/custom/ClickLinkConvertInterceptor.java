package com.xtc.assistantapi.custom;

import android.net.Uri;
import android.util.Log;

import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.core.DirectiveCallback;
import com.xtc.assistantapi.core.DirectiveInterceptor;
import com.xtc.assistantapi.custom.message.ClickLinkPayload;
import com.xtc.assistantapi.custom.message.RecoverClickLinkPayload;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;
import com.xtc.assistantapi.message.Header;
import com.xtc.assistantapi.util.DirectiveGsonUtil;

/**
 * 点击链接指令转换拦截器，把 ClickLink 指令的 URL 解析为 RecoverClickLink 指令。
 */
public class ClickLinkConvertInterceptor implements DirectiveInterceptor {

    private static final String TAG = LogTag.of("ClickLinkConvertInterceptor");
    private static final String QUERY_PARAM_ACTION = "action";

    @Override
    public void intercept(DirectiveRequest request, DirectiveCallback callback) {
        if (request == null || request.getContext() == null || request.getDirective() == null) {
            callback.onError(new DirectiveResponse());
            return;
        }
        Directive directive = request.getDirective();
        String name = directive.header.getName();
        if (ApiConstants.NAMESPACE_CUSTOM_USER_INTERACTION.equals(directive.header.getNamespace())
                && ApiConstants.Directives.ClickLink.NAME.equals(name)) {
            String rawPayload = directive.getRawPayload();
            Log.d(TAG, "intercept: rawPayload = " + rawPayload);
            ClickLinkPayload clickLinkPayload = DirectiveGsonUtil.fromJson(rawPayload, ClickLinkPayload.class);
            Log.d(TAG, "intercept: clickLinkPayload = " + clickLinkPayload);
            if (clickLinkPayload != null) {
                String url = clickLinkPayload.getUrl();
                Log.d(TAG, "intercept: url = " + url);
                Uri uri = Uri.parse(Uri.decode(url));
                Directive recovered = new Directive(new Header(ApiConstants.NAMESPACE_CUSTOM_USER_INTERACTION, uri.getScheme()),
                        new RecoverClickLinkPayload(uri.getQueryParameter(QUERY_PARAM_ACTION)));
                request.setDirective(DirectiveGsonUtil.fromJson(DirectiveGsonUtil.toJson(recovered), Directive.class));
            }
        }
        callback.onNext(request);
    }
}
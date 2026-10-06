package com.xtc.assistantapi.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.text.TextUtils;
import android.util.Log;

import com.xtc.assistantapi.DeviceModuleManager;
import com.xtc.assistantapi.DirectiveHandlerManager;
import com.xtc.assistantapi.LogTag;
import com.xtc.assistantapi.R;
import com.xtc.assistantapi.core.DirectiveCallback;
import com.xtc.assistantapi.core.DirectiveHandler;
import com.xtc.assistantapi.core.IntentAction;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.DirectiveRequest;
import com.xtc.assistantapi.message.DirectiveResponse;

import java.util.List;

/**
 * 基于 manifest meta-data 的指令分发处理器，按指令名匹配声明了 assistantapi-directive 的组件。
 */
public class MetaDataHandler extends DirectiveHandler {

    private static final String TAG = LogTag.of("MetaDataHandler");

    private final Context context;

    public MetaDataHandler(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected boolean canHandle(DirectiveRequest request) {
        return true;
    }

    @Override
    protected void handle(DirectiveRequest request, DirectiveCallback callback) {
        handleByDefault(request, callback);
    }

    private void handleByDefault(DirectiveRequest request, DirectiveCallback callback) {
        Directive directive = request.getDirective();
        Log.d(TAG, "handleByDefault: directive = " + directive);
        if (directive == null) {
            callback.onError(new DirectiveResponse(400, "directive is null"));
            return;
        }
        Log.d(TAG, "handleByDefault: header = " + directive.header);
        if (directive.header == null) {
            callback.onError(new DirectiveResponse(400, "directive header is null"));
            return;
        }
        String directiveName = directive.header.getName();
        if (TextUtils.isEmpty(directiveName)) {
            callback.onError(new DirectiveResponse(400, "directive name is empty"));
        } else {
            dispatchDirective(callback, directive, directiveName);
        }
    }

    private void dispatchDirective(DirectiveCallback callback, Directive directive, String directiveName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(IntentAction.ACTION_DIRECTIVE);
        boolean handledByActivity = handleByActivity(directive, directiveName, packageManager, intent);
        boolean handledByService = !handledByActivity
                && handleByService(directive, directiveName, packageManager, intent);
        if (handledByActivity || handledByService) {
            callback.onComplete(new DirectiveResponse(200, "success"));
        } else {
            callback.onError(new DirectiveResponse(404, "not found directive"));
        }
    }

    private boolean handleByService(Directive directive, String directiveName, PackageManager packageManager, Intent intent) {
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(intent, 0);
        if (resolveInfos == null || resolveInfos.size() == 0) {
            return false;
        }
        for (ResolveInfo resolveInfo : resolveInfos) {
            try {
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                ServiceInfo serviceInfo = packageManager.getServiceInfo(componentName, PackageManager.GET_META_DATA);
                if (serviceInfo == null) {
                    continue;
                }
                Log.d(TAG, "serviceInfo.packageName = " + serviceInfo.packageName);
                String directiveMeta = serviceInfo.metaData.getString(IntentAction.META_DATA_DIRECTIVE);
                if (!TextUtils.isEmpty(directiveMeta) && directiveMeta.contains(directiveName)) {
                    Intent serviceIntent = new Intent();
                    serviceIntent.setAction(IntentAction.ACTION_DIRECTIVE);
                    serviceIntent.putExtra(IntentAction.EXTRA_DIRECTIVE, directive.getRawMessage());
                    serviceIntent.setComponent(componentName);
                    return DirectiveHandlerManager.getComponentLaunch()
                            .launchService(context, serviceIntent, serviceInfo.packageName);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean handleByActivity(Directive directive, String directiveName, PackageManager packageManager, Intent intent) {
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfos == null || resolveInfos.size() == 0) {
            return false;
        }
        for (ResolveInfo resolveInfo : resolveInfos) {
            try {
                ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                ActivityInfo activityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA);
                if (activityInfo == null) {
                    continue;
                }
                Log.d(TAG, "activityInfo.packageName = " + activityInfo.packageName);
                Log.d(TAG, "activityInfo.targetActivity = " + activityInfo.targetActivity);
                if (containsDirective(activityInfo.metaData.getString(IntentAction.META_DATA_DIRECTIVE), directiveName)) {
                    String appLabel = resolveInfo.loadLabel(packageManager).toString();
                    DeviceModuleManager.getInstance().getMessageSender().sendMessage(context,
                            String.format(context.getString(R.string.assistant_start_app), appLabel), "000002");
                    Intent activityIntent = new Intent();
                    activityIntent.setAction(IntentAction.ACTION_DIRECTIVE);
                    activityIntent.putExtra(IntentAction.EXTRA_DIRECTIVE, directive.getRawMessage());
                    activityIntent.setComponent(componentName);
                    if (!activityInfo.packageName.equals(context.getPackageName())) {
                        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    }
                    return DirectiveHandlerManager.getComponentLaunch().launchActivity(context, activityIntent,
                            activityInfo.packageName, activityInfo.targetActivity, appLabel);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean containsDirective(String directiveMeta, String directiveName) {
        return !TextUtils.isEmpty(directiveMeta) && directiveMeta.contains(directiveName);
    }
}
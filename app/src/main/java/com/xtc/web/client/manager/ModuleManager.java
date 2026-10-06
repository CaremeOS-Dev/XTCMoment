package com.xtc.web.client.manager;

import android.content.Context;

import com.xtc.system.account.WatchAccountBase;
import com.xtc.web.client.data.request.ReqModule;
import com.xtc.web.core.callback.CompletionHandler;

/** 模块开关查询管理器。 */
public class ModuleManager {

    /** 查询指定模块的开关值并回调给 H5。 */
    public void getModuleSwitch(Context context, ReqModule reqModule, CompletionHandler<Integer> completionHandler) {
        completionHandler.complete(Integer.valueOf(WatchAccountBase.queryModuleSwitchByInt(context,
                reqModule.getKey(), reqModule.isDefaultValue())));
    }
}
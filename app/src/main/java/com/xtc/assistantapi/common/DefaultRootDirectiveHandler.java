package com.xtc.assistantapi.common;

import android.content.Context;

import com.xtc.assistantapi.core.RootDirectiveHandler;
import com.xtc.assistantapi.custom.ClickLinkConvertInterceptor;

/**
 * 默认根指令处理器，注册 meta-data 处理器与预校验/链接转换拦截器。
 */
public class DefaultRootDirectiveHandler extends RootDirectiveHandler {

    private final MetaDataHandler metaDataHandler;

    public DefaultRootDirectiveHandler(Context context) {
        super(context);
        this.metaDataHandler = createMetaDataHandler(context);
        addHandler(this.metaDataHandler, 100);
    }

    private MetaDataHandler createMetaDataHandler(Context context) {
        MetaDataHandler metaDataHandler = new MetaDataHandler(context);
        metaDataHandler.addInterceptor(new PreCheckInterceptor());
        metaDataHandler.addInterceptor(new ClickLinkConvertInterceptor());
        return metaDataHandler;
    }
}
package com.xtc.web.core.manager;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.SystemPropertyUtil;
import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqSystemProperty;
import com.xtc.web.core.data.resp.RespSystemProperty;

/** 读取系统属性并回调给 H5，按请求的 type 决定解析方式。 */
public class SystemPropertyManager {

    private static final String TAG = "SystemPropertyManager";

    public void request(ReqSystemProperty reqSystemProperty, CompletionHandler<RespSystemProperty> completionHandler) {
        String valueText;
        if (reqSystemProperty == null || reqSystemProperty.getPropertyName() == null) {
            RespSystemProperty response = new RespSystemProperty();
            response.setCode(RespSystemProperty.Code.ARG_ERROR);
            completionHandler.complete(response);
            LogUtil.d(TAG, "request: " + response);
            return;
        }
        String propertyName = reqSystemProperty.getPropertyName();
        int type = reqSystemProperty.getType();
        if (type == CoreConstants.SystemPropertyConstant.TYPE_STRING) {
            valueText = String.valueOf(SystemPropertyUtil.get(propertyName, ""));
        } else if (type == CoreConstants.SystemPropertyConstant.TYPE_INT) {
            valueText = String.valueOf(SystemPropertyUtil.getInt(propertyName, 0));
        } else if (type == CoreConstants.SystemPropertyConstant.TYPE_LONG) {
            valueText = String.valueOf(SystemPropertyUtil.getLong(propertyName, 0L));
        } else {
            valueText = String.valueOf(SystemPropertyUtil.getBoolean(propertyName, false));
        }
        RespSystemProperty response = new RespSystemProperty();
        response.setCode(RespSystemProperty.Code.SUCCESS);
        response.setData(valueText);
        LogUtil.d(TAG, "request: " + response);
        completionHandler.complete(response);
    }
}
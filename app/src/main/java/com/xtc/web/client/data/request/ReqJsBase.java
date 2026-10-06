package com.xtc.web.client.data.request;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/** JS 调用 native 的通用请求包装：回调名 + 业务参数。 */
public class ReqJsBase {

    private String callbackName;
    private Object reqArg;

    public String getCallbackName() {
        return this.callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    public Object getReqArg() {
        return this.reqArg;
    }

    public void setReqArg(Object reqArg) {
        this.reqArg = reqArg;
    }

    /** 解析 {"callbackName":..,"data":..} 形式的入参。 */
    public static ReqJsBase parseValue(String json) {
        ReqJsBase request = new ReqJsBase();
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("callbackName")) {
                request.setCallbackName(jsonObject.getString("callbackName"));
            }
            if (jsonObject.has("data")) {
                request.setReqArg(jsonObject.get("data"));
            }
        } catch (JSONException e) {
            LogUtil.e(Constants.TAG, "parse ReqJsBase error = " + e);
        }
        return request;
    }

    @Override
    public String toString() {
        return "ReqJsBase{reqArg=" + this.reqArg + ", callbackName='" + this.callbackName + "'}";
    }
}
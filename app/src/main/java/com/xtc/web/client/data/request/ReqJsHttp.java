package com.xtc.web.client.data.request;

import java.util.HashMap;

/** H5 发起的原生网络请求参数。 */
public class ReqJsHttp {

    private Object body;
    private HashMap<String, String> header;
    private String method;
    private String type;
    private String url;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getHeader() {
        return this.header;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public Object getBody() {
        return this.body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReqJsHttp{url='" + this.url + "', method='" + this.method + "', body='" + this.body + "', type='"
                + this.type + "', header=" + this.header + '}';
    }
}
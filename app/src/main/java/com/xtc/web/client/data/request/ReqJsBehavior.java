package com.xtc.web.client.data.request;

import java.util.HashMap;

/** H5 上报行为事件的请求。 */
public class ReqJsBehavior {

    private String event;
    private HashMap<String, String> obj;

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public HashMap<String, String> getObj() {
        return this.obj;
    }

    public void setObj(HashMap<String, String> obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "ReqJsBehavior{event='" + this.event + "', obj=" + this.obj + '}';
    }
}
package com.xtc.web.core.data.resp;

/** JS 对 native 调用的返回体，completed 表示后续不再有回调。 */
public class RespNativeCallJs {

    private boolean completed;
    private Object data;
    private int id;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespNativeCallJs{id=" + this.id + ", completed=" + this.completed + ", data=" + this.data + '}';
    }
}
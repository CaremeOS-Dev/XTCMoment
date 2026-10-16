package com.xtc.moment.share.model.bean;

/**
 * 第三方分享请求的扩展信息：目前只有「小括号」标签（tags）字段。
 */
public class ReqExtInfo {

    private String tags;

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ReqExtInfo{tags='" + this.tags + "'}";
    }
}

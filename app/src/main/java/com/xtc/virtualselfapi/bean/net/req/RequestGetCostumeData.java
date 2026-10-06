package com.xtc.virtualselfapi.bean.net.req;

/**
 * 获取当前装扮数据请求。
 */
public class RequestGetCostumeData extends BaseReqeust {

    private String gender;

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
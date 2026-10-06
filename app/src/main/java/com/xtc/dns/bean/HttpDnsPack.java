package com.xtc.dns.bean;

import com.xtc.dns.storage.db.DomainModel;

import java.util.List;

/**
 * HTTP DNS 响应数据包。
 */
public class HttpDnsPack {

    private String domain = "";
    private List<DomainModel> domainList = null;
    private String errorMsg = "";
    private String requestId;
    private int resultCode;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<DomainModel> getDomainList() {
        return domainList;
    }

    public void setDomainList(List<DomainModel> domainList) {
        this.domainList = domainList;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
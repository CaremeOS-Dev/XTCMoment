package com.xtc.aitext.bean;

/**
 * 主页数据请求体。
 */
public class MainDataBody {

    private int clientType = 1;
    private int pageSize;
    private int nowPage;

    public void setClientType(int clientType) {
        this.clientType = clientType;
    }

    public int getClientType() {
        return clientType;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Override
    public String toString() {
        return "MainDataBody{clientType=" + clientType + "pageSize=" + pageSize + "nowPage=" + nowPage + '}';
    }
}
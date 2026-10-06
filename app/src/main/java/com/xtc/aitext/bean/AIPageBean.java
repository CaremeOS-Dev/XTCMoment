package com.xtc.aitext.bean;

/**
 * AI 记录分页信息。
 */
public class AIPageBean {

    private int startIndex;
    private int totalPage;
    private int pageSize;
    private int totalRecord;
    private int nowPage;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    @Override
    public String toString() {
        return "AIPageBean{startIndex=" + startIndex + ", totalPage=" + totalPage + ", pageSize=" + pageSize
                + ", totalRecord=" + totalRecord + ", nowPage=" + nowPage + '}';
    }
}
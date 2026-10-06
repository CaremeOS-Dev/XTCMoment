package com.xtc.moment.net.bean;

import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

/** A page of a moment's comments. */
public class SearchCommentResponse {
    private List<DbMomentComment> comments;
    private int pageNum;
    private int pageSize;
    private int totalCount;
    private int totalPage;

    public void setComments(List<DbMomentComment> comments) {
        this.comments = comments;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<DbMomentComment> getComments() {
        return this.comments;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    @Override
    public String toString() {
        return "SearchCommentResponse{totalPage=" + this.totalPage + ", pageSize=" + this.pageSize + ", pageNum=" + this.pageNum + ", comments=" + this.comments + '}';
    }

    public String toString2() {
        return "SearchCommentResponse{totalPage=" + this.totalPage + ", pageSize=" + this.pageSize + ", pageNum=" + this.pageNum + '}';
    }
}

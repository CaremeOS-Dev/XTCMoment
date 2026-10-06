package com.xtc.moment.net.bean;

import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

/** Response carrying every comment of a moment. */
public class SearchAllCommentResponse {
    private List<DbMomentComment> comments;
    private int totalCount;

    public List<DbMomentComment> getComments() {
        return this.comments;
    }

    @Override
    public String toString() {
        return "SearchAllCommentResponse{totalCount=" + this.totalCount + ", comments=" + this.comments + '}';
    }
}

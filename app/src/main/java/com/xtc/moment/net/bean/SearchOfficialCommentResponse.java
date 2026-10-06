package com.xtc.moment.net.bean;

import java.util.List;

/** A page of advert comments. */
public class SearchOfficialCommentResponse {
    private List<CommentBean> comments;
    private int pageNum;
    private int pageSize;
    private int totalCount;
    private int totalPage;

    public void setComments(List<CommentBean> comments) {
        this.comments = comments;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<CommentBean> getComments() {
        return this.comments;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    /** A single advert comment as nested in the response. */
    public class CommentsEntity {
        private String advertId;
        private String comment;
        private String commentId;
        private long createTime;
        private String parentWatchId;
        private String replyCommentId;
        private String replyWatchId;
        private String replyWatchName;
        private String watchId;
        private String watchName;

        public CommentsEntity() {
        }

        public void setReplyWatchName(String replyWatchName) {
            this.replyWatchName = replyWatchName;
        }

        public void setReplyCommentId(String replyCommentId) {
            this.replyCommentId = replyCommentId;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setWatchId(String watchId) {
            this.watchId = watchId;
        }

        public void setWatchName(String watchName) {
            this.watchName = watchName;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public void setParentWatchId(String parentWatchId) {
            this.parentWatchId = parentWatchId;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setReplyWatchId(String replyWatchId) {
            this.replyWatchId = replyWatchId;
        }

        public void setAdvertId(String advertId) {
            this.advertId = advertId;
        }

        public String getReplyWatchName() {
            return this.replyWatchName;
        }

        public String getReplyCommentId() {
            return this.replyCommentId;
        }

        public long getCreateTime() {
            return this.createTime;
        }

        public String getWatchId() {
            return this.watchId;
        }

        public String getWatchName() {
            return this.watchName;
        }

        public String getCommentId() {
            return this.commentId;
        }

        public String getParentWatchId() {
            return this.parentWatchId;
        }

        public String getComment() {
            return this.comment;
        }

        public String getReplyWatchId() {
            return this.replyWatchId;
        }

        public String getAdvertId() {
            return this.advertId;
        }
    }
}

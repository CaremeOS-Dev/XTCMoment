package com.xtc.moment.net.bean;

/** Result of posting a comment. */
public class CommentResultBean {

    public static final String SUCCESS = "1";
    public static final String INVALIDATE = "2";
    public static final String DOWNLOAD_VERSION = "3";
    public static final String UPPER_LIMIT = "4";

    private CommentBean comment;
    private String result;
    private String tips;

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTips() {
        return this.tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public CommentBean getComment() {
        return this.comment;
    }

    public void setComment(CommentBean comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CommentResultBean{tips='" + this.tips + "'result='" + this.result + "', comment=" + this.comment + '}';
    }
}

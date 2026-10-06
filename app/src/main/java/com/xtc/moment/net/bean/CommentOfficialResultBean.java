package com.xtc.moment.net.bean;

/** Result of posting a comment on an advert moment. */
public class CommentOfficialResultBean {

    public static final String SUCCESS = "1";
    public static final String INVALIDATE = "2";
    public static final String DOWNLOAD_VERSION = "3";
    public static final String UPPER_LIMIT = "4";

    private CommentBean advertCommentVo;
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

    public CommentBean getAdvertCommentVo() {
        return this.advertCommentVo;
    }

    public void setAdvertCommentVo(CommentBean advertCommentVo) {
        this.advertCommentVo = advertCommentVo;
    }

    @Override
    public String toString() {
        return "CommentResultBean{tips='" + this.tips + "'result='" + this.result + "', advertCommentVo=" + this.advertCommentVo + '}';
    }
}

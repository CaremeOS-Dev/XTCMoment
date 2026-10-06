package com.xtc.moment.provider.bean;

/**
 * 心情评论数据。
 */
public class MoodComment {

    private String commentContent;
    private String moodCommentContent;

    public String getCommentContent() {
        return this.commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getMoodCommentContent() {
        return this.moodCommentContent;
    }

    public void setMoodCommentContent(String moodCommentContent) {
        this.moodCommentContent = moodCommentContent;
    }

    @Override
    public String toString() {
        return "MoodComment{commentContent='" + this.commentContent + "', moodCommentContent='"
                + this.moodCommentContent + "'}";
    }
}
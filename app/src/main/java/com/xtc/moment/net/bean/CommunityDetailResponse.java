package com.xtc.moment.net.bean;

import java.io.Serializable;

/** Response for a community detail page. */
public class CommunityDetailResponse implements Serializable {
    private String chapter;
    private Integer chapterIndex;
    private String chapterTitle;
    private String content;
    private Long createTime;
    private Integer id;
    private String language;
    private String title;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChapter() {
        return this.chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getChapterTitle() {
        return this.chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public Integer getChapterIndex() {
        return this.chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "CommunityDetailResponse{id=" + this.id + ", chapter='" + this.chapter + "', chapterTitle='" + this.chapterTitle + "', chapterIndex=" + this.chapterIndex + ", title='" + this.title + "', content='" + this.content + "', language='" + this.language + "', createTime=" + this.createTime + '}';
    }
}

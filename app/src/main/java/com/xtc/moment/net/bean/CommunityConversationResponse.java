package com.xtc.moment.net.bean;

import java.util.List;

/** Response for the community conversation screen. */
public class CommunityConversationResponse {
    private List<CatalogBean> catalog;
    private String content;
    private String imageUrl;
    private String language;

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public List<CatalogBean> getCatalog() {
        return this.catalog;
    }

    public void setCatalog(List<CatalogBean> catalog) {
        this.catalog = catalog;
    }

    @Override
    public String toString() {
        return "CommunityConversationResponse{imageUrl='" + this.imageUrl + "', content='" + this.content + "', language='" + this.language + "', catalog=" + this.catalog + '}';
    }

    /** One chapter entry in the conversation. */
    public static class CatalogBean {
        private String chapter;
        private Integer chapterIndex;
        private String chapterTitle;

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
            Integer index = this.chapterIndex;
            return Integer.valueOf(index == null ? 0 : index.intValue());
        }

        public void setChapterIndex(Integer chapterIndex) {
            this.chapterIndex = chapterIndex;
        }

        @Override
        public String toString() {
            return "CatalogBean{chapter='" + this.chapter + "', chapterTitle='" + this.chapterTitle + "', chapterIndex=" + this.chapterIndex + '}';
        }
    }
}

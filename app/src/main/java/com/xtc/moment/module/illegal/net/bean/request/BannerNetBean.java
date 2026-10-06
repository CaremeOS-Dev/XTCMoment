package com.xtc.moment.module.illegal.net.bean.request;

import java.util.List;

/**
 * 违规提示横幅数据。
 */
public class BannerNetBean {

    private List<BannerContentBent> contentVos;

    public List<BannerContentBent> getContentVos() {
        return this.contentVos;
    }

    public void setContentVos(List<BannerContentBent> contentVos) {
        this.contentVos = contentVos;
    }

    /** 横幅内容项。 */
    public static class BannerContentBent {

        private String longDate;
        private int type;
        private String content;

        public String getLongDate() {
            return this.longDate;
        }

        public void setLongDate(String longDate) {
            this.longDate = longDate;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "BannerContentBent{longDate='" + this.longDate + "', type=" + this.type + ", content='" + this.content
                    + "'}";
        }
    }
}
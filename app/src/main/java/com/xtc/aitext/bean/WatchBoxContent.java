package com.xtc.aitext.bean;

/**
 * 手表弹窗内容。
 */
public class WatchBoxContent {

    private String buttonText;
    private int clickStatus;
    private String jumpPage;
    private String title;
    private String content;
    private String remarks;

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public int getClickStatus() {
        return clickStatus;
    }

    public void setClickStatus(int clickStatus) {
        this.clickStatus = clickStatus;
    }

    public String getJumpPage() {
        return jumpPage;
    }

    public void setJumpPage(String jumpPage) {
        this.jumpPage = jumpPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "WacthBoxContent{buttonText='" + buttonText + "', clickStatus=" + clickStatus + ", jumpPage='"
                + jumpPage + "', title='" + title + "', content='" + content + "', remarks='" + remarks + "'}";
    }
}
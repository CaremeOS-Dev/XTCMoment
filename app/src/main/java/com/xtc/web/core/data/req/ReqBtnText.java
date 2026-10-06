package com.xtc.web.core.data.req;

/** 拍照页左右按钮文案与贴纸配置。 */
public class ReqBtnText {

    private CameraStickerData cameraStickerData;
    private String leftText;
    private String rightText;

    public ReqBtnText() {
    }

    public ReqBtnText(String leftText, String rightText) {
        this.leftText = leftText;
        this.rightText = rightText;
    }

    public String getLeftText() {
        return this.leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    public String getRightText() {
        return this.rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public void setCameraStickerData(CameraStickerData cameraStickerData) {
        this.cameraStickerData = cameraStickerData;
    }

    public CameraStickerData getCameraStickerData() {
        return this.cameraStickerData;
    }
}
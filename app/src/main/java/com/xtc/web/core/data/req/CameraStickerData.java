package com.xtc.web.core.data.req;

/** 拍照页贴纸配置：是否使用贴纸、退出动画与贴纸 id。 */
public class CameraStickerData {

    private boolean slideOut;
    private int stickerId;
    private boolean useSticker;

    public void setUseSticker(boolean useSticker) {
        this.useSticker = useSticker;
    }

    public boolean getUseSticker() {
        return this.useSticker;
    }

    public void setSlideOut(boolean slideOut) {
        this.slideOut = slideOut;
    }

    public boolean getSlideOut() {
        return this.slideOut;
    }

    public void setStickerId(int stickerId) {
        this.stickerId = stickerId;
    }

    public int getStickerId() {
        return this.stickerId;
    }

    @Override
    public String toString() {
        return "CameraStickerData{useSticker=" + this.useSticker + ", slideOut=" + this.slideOut + ", stickerId='"
                + this.stickerId + "'}";
    }
}
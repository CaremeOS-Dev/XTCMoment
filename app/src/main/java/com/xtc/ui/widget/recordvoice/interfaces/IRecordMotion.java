package com.xtc.ui.widget.recordvoice.interfaces;

/** 录音手势 UI 回调。 */
public interface IRecordMotion {
    void cancelAnimation();

    void setTopText(String text);

    void whenViewIsClicked();
}
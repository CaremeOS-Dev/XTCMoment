package com.xtc.shareapi.share.interfaces;

/**
 * 场景选择弹窗的点击回调，返回被选中的场景类型。
 */
public interface IChooseSceneCallback {

    /** 用户点击了某个分享场景。 */
    void setOnClickCallback(int sceneType);
}
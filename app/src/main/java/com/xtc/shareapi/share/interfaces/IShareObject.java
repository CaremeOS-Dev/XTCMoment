package com.xtc.shareapi.share.interfaces;

/**
 * 可分享内容对象（文本、图片、音乐、视频、网页、应用扩展等）的统一契约。
 */
public interface IShareObject extends IBundleSerialize {

    /** 未知类型。 */
    int TYPE_UNKNOWN = 0;
    /** 纯文本。 */
    int TYPE_TEXT = 1;
    /** 图片。 */
    int TYPE_IMAGE = 2;
    /** 音乐。 */
    int TYPE_MUSIC = 3;
    /** 视频。 */
    int TYPE_VIDEO = 4;
    /** 应用扩展信息。 */
    int TYPE_APPEXTEND = 5;
    /** 网页。 */
    int TYPE_WEB = 6;

    /** 分享内容类型。 */
    int type();
}
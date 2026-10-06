package com.xtc.shareapi.share.interfaces;

/**
 * 分享目标场景（微聊会话、好友圈、时光记忆、YouTube）的抽象。
 */
public interface Scene extends IBundleSerialize {

    /** 微聊会话。 */
    int TYPE_CHAT = 1;
    /** 好友圈。 */
    int TYPE_MOMENT = 2;
    /** 时光记忆。 */
    int TYPE_TIME_MEMORY = 3;
    /** YouTube。 */
    int TYPE_YOUTUBE = 4;

    /** 场景所属应用名称。 */
    String getAppName();

    /** 场景所属应用包名。 */
    String getPackageName();

    /** 场景目标类名。 */
    String getTargetClassName();

    /** 场景类型。 */
    int getType();
}
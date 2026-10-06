package com.xtc.moment.module.bean;

import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.shareapi.share.bean.MessageBitmapArgs;

/**
 * 实况照片分享到动态的展示模型。
 */
public class ShareLivePhotoMoment extends LivePhotoMsg {

    private String desc;
    private byte[] appIcon;
    private String appName;
    private String transaction;
    private String packageName;
    private String textMsg;
    private MessageBitmapArgs messageBitmapArgs;
    private DialogBitmapArgs dialogBitmapArgs;

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public byte[] getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(byte[] appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTransaction() {
        return this.transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTextMsg() {
        return this.textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    public MessageBitmapArgs getMessageBitmapArgs() {
        return this.messageBitmapArgs;
    }

    public void setMessageBitmapArgs(MessageBitmapArgs messageBitmapArgs) {
        this.messageBitmapArgs = messageBitmapArgs;
    }

    public DialogBitmapArgs getDialogBitmapArgs() {
        return this.dialogBitmapArgs;
    }

    public void setDialogBitmapArgs(DialogBitmapArgs dialogBitmapArgs) {
        this.dialogBitmapArgs = dialogBitmapArgs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShareImageMoment{desc='").append(this.desc).append('\'');
        builder.append(", appIcon!=null").append(this.appIcon != null);
        builder.append(", appName='").append(this.appName).append('\'');
        builder.append(", transaction='").append(this.transaction).append('\'');
        builder.append(", packageName='").append(this.packageName).append('\'');
        builder.append(", textMsg='").append(this.textMsg).append('\'');
        builder.append(", messageBitmapArgs=").append(this.messageBitmapArgs);
        builder.append(", dialogBitmapArgs=").append(this.dialogBitmapArgs);
        builder.append('}');
        return builder.toString();
    }
}
package com.xtc.moment.module.bean;

import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.shareapi.share.bean.MessageBitmapArgs;

/**
 * 视频分享到动态的展示模型。
 */
public class ShareVideoMoment extends VideoMsg {

    public static final String SHARE_VIDEO_FLAGS = "share_video_flags";

    private String desc;
    private byte[] appIcon;
    private String appName;
    private String transaction;
    private String packageName;
    private String textMsg;
    private MessageBitmapArgs messageBitmapArgs;
    private DialogBitmapArgs dialogBitmapArgs;
    private FunVideoParam funVideoParam;

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

    public FunVideoParam getFunVideoParam() {
        return this.funVideoParam;
    }

    public void setFunVideoParam(FunVideoParam funVideoParam) {
        this.funVideoParam = funVideoParam;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ShareVideoMoment{desc='").append(this.desc).append('\'');
        builder.append(", appIcon!=null").append(this.appIcon != null);
        builder.append(", appName='").append(this.appName).append('\'');
        builder.append(", transaction='").append(this.transaction).append('\'');
        builder.append(", packageName='").append(this.packageName).append('\'');
        builder.append(", textMsg='").append(this.textMsg).append('\'');
        builder.append(", messageBitmapArgs=").append(this.messageBitmapArgs);
        builder.append(", dialogBitmapArgs=").append(this.dialogBitmapArgs);
        builder.append(", funVideoParam=").append(this.funVideoParam);
        builder.append('}');
        return builder.toString();
    }

    /**
     * 趣味视频的编辑参数：模板、贴纸、滤镜、背景音乐与播放速度。
     */
    public static class FunVideoParam {

        private int modelType;
        private String point;
        private String sticker;
        private float speed;
        private String filter;
        private String bgm;

        public int getModelType() {
            return this.modelType;
        }

        public void setModelType(int modelType) {
            this.modelType = modelType;
        }

        public String getPoint() {
            return this.point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getSticker() {
            return this.sticker;
        }

        public void setSticker(String sticker) {
            this.sticker = sticker;
        }

        public float getSpeed() {
            return this.speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public String getFilter() {
            return this.filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getBgm() {
            return this.bgm;
        }

        public void setBgm(String bgm) {
            this.bgm = bgm;
        }

        @Override
        public String toString() {
            return "FunVideoParam{modelType=" + this.modelType + ", point='" + this.point + "', sticker='" + this.sticker
                    + "', speed=" + this.speed + ", filter='" + this.filter + "', bgm='" + this.bgm + "'}";
        }
    }
}
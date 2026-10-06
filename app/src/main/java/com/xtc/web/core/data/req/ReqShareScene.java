package com.xtc.web.core.data.req;

import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.sharescene.Moment;

/** 分享场景：聊天或动态。 */
public class ReqShareScene {

    /** 分享场景类型。 */
    public interface SceneType {
        int CHAT = 1;
        int MOMOENT = 2;
    }

    /** 分享面板展示方式。 */
    public interface ShowType {
        int SILENT = 16;
        int STANDARD = 32;
    }

    private Chat chat;
    private Moment moment;
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Chat getChat() {
        return this.chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Moment getMoment() {
        return this.moment;
    }

    public void setMoment(Moment moment) {
        this.moment = moment;
    }

    @Override
    public String toString() {
        return "ReqShareScene{type=" + this.type + ", chat=" + this.chat + ", moment=" + this.moment + '}';
    }
}
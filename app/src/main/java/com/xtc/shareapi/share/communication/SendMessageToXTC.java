package com.xtc.shareapi.share.communication;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.shareobject.XTCShareMessage;
import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.sharescene.Moment;

/**
 * 向 XTC（微聊/好友圈）发送分享内容的通信载体，包含请求与响应。
 */
public class SendMessageToXTC {

    /**
     * 分享请求，携带分享消息、目标场景与跳转标记。
     */
    public static class Request extends BaseRequest {

        private XTCShareMessage message;
        private Scene scene;
        private int flag;

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            bundle.putAll(XTCShareMessage.Builder.toBundle(message));
            if (scene != null) {
                scene.toBundle(bundle);
            }
            bundle.putInt("_xtc_api_share_type", message.getType());
            bundle.putInt(OpenApiConstant.SendMessageFromXTCConstant.BUNDLE_MESSAGE_SHARE_JUMP_FLAG, flag);
        }

        @Override
        public Request fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            Log.d(OpenApiConstant.TAG, "come to fromBundle1");
            int shareType = bundle.getInt(OpenApiConstant.SceneConstant.BUNDLE_SCENE_SHARE_TYPE);
            Log.d(OpenApiConstant.TAG, "come to fromBundle2" + shareType);
            if (shareType == 1) {
                Chat chat = (Chat) new Chat().fromBundle(bundle);
                Log.d(OpenApiConstant.TAG, "come to fromBundle" + chat.toString() + ",var1 = " + bundle.toString());
                setScene(chat);
            } else if (shareType == 2) {
                setScene((Moment) new Moment().fromBundle(bundle));
            }
            this.message = XTCShareMessage.Builder.fromBundle(bundle);
            this.message.setActionType(bundle.getInt("_xtc_api_share_type"));
            Log.d(OpenApiConstant.TAG, "come to fromBundle3 " + this.message);
            this.flag = bundle.getInt(OpenApiConstant.SendMessageFromXTCConstant.BUNDLE_MESSAGE_SHARE_JUMP_FLAG);
            return this;
        }

        @Override
        public BaseResponse checkArgs() {
            XTCShareMessage message = this.message;
            if (message == null) {
                Log.d(OpenApiConstant.TAG, "checkArgs fail ,message is null");
                ShowMessageFromXTC.Response response = new ShowMessageFromXTC.Response();
                response.setCode(6);
                response.setErrorDesc("XTCShareMessage is null");
                return response;
            }
            return message.checkArgs();
        }

        public XTCShareMessage getMessage() {
            return message;
        }

        public void setMessage(XTCShareMessage message) {
            this.message = message;
        }

        public Scene getScene() {
            return scene;
        }

        public void setScene(Scene scene) {
            this.scene = scene;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }
    }

    /**
     * 分享响应，携带结果码、事务号与会话信息。
     */
    public static class Response extends BaseResponse {

        public Response() {
        }

        public Response(int code, String errorDesc) {
            super(code, errorDesc);
        }

        public Response(int code, String errorDesc, String transaction) {
            super(code, errorDesc, transaction);
        }

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public void toBundle(Bundle bundle) {
            bundle.putInt(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE, getCode());
            bundle.putString(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_DESC, getErrorDesc());
            bundle.putString(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_TRANSACTION, getTransaction());
            bundle.putString(OpenApiConstant.ResponseConstant.BUNDLE_CONVERSATION_ID, getConversationId());
            bundle.putString(OpenApiConstant.ResponseConstant.BUNDLE_CONVERSATION_TITLE, getConversationTitle());
        }

        @Override
        public Response fromBundle(Bundle bundle) {
            setCode(bundle.getInt(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE));
            setErrorDesc(bundle.getString(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_DESC));
            setTransaction(bundle.getString(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_TRANSACTION));
            setConversationId(bundle.getString(OpenApiConstant.ResponseConstant.BUNDLE_CONVERSATION_ID));
            setConversationTitle(bundle.getString(OpenApiConstant.ResponseConstant.BUNDLE_CONVERSATION_TITLE));
            setFromType(bundle.getString(OpenApiConstant.ResponseConstant.BUNDLE_SCENE_FROM_TYPE));
            return this;
        }

        @Override
        public BaseResponse checkArgs() {
            Response response = new Response();
            response.setCode(1);
            return response;
        }
    }
}
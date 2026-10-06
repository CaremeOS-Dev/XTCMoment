package com.xtc.shareapi.share.communication;

import android.os.Bundle;
import android.util.Log;

import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.sharescene.Moment;

/**
 * XTC（微聊/好友圈）向外部应用回传消息的通信载体，包含请求与响应。
 */
public class ShowMessageFromXTC {

    /**
     * 回传请求，携带场景、扩展信息与结果码。
     */
    public static class Request extends BaseRequest {

        private Scene scene;
        private String extInfo;
        private int resultCode;

        @Override
        public int getType() {
            return 2;
        }

        @Override
        public void toBundle(Bundle bundle) {
            super.toBundle(bundle);
            if (scene != null) {
                scene.toBundle(bundle);
            }
            bundle.putString("_xtc_api_share_type", extInfo);
            bundle.putInt(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE, resultCode);
        }

        @Override
        public Request fromBundle(Bundle bundle) {
            super.fromBundle(bundle);
            int shareType = bundle.getInt(OpenApiConstant.SceneConstant.BUNDLE_SCENE_SHARE_TYPE);
            Log.d(OpenApiConstant.TAG, "come to fromBundle" + shareType);
            if (shareType == 1) {
                Chat chat = (Chat) new Chat().fromBundle(bundle);
                Log.d(OpenApiConstant.TAG, "come to fromBundle" + chat.toString() + ",var1 = " + bundle.toString());
                setScene(chat);
            } else if (shareType == 2) {
                setScene((Moment) new Moment().fromBundle(bundle));
            }
            setExtInfo(bundle.getString("_xtc_api_share_type"));
            setResultCode(bundle.getInt(OpenApiConstant.ResponseConstant.BUNDLE_ERROR_CODE));
            return this;
        }

        @Override
        public BaseResponse checkArgs() {
            SendMessageToXTC.Response response = new SendMessageToXTC.Response();
            response.setCode(1);
            return response;
        }

        public String getExtInfo() {
            return extInfo;
        }

        public void setExtInfo(String extInfo) {
            this.extInfo = extInfo;
        }

        public Scene getScene() {
            return scene;
        }

        public void setScene(Scene scene) {
            this.scene = scene;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }
    }

    /**
     * 回传响应，携带结果码、事务号与会话信息。
     */
    public static class Response extends BaseResponse {

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
            return this;
        }

        @Override
        public BaseResponse checkArgs() {
            SendMessageToXTC.Response response = new SendMessageToXTC.Response();
            response.setCode(1);
            return response;
        }
    }
}
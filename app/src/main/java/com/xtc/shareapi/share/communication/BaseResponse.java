package com.xtc.shareapi.share.communication;

import com.xtc.shareapi.share.interfaces.IBundleSerialize;

/**
 * 分享响应基类，保存错误码、错误描述、事务号与会话信息。
 */
public abstract class BaseResponse implements IBundleSerialize {

    private int code;
    private String errorDesc;
    private String transaction;
    private String conversationId;
    private String conversationTitle;
    private String fromType;

    public BaseResponse() {
    }

    public BaseResponse(int code, String errorDesc) {
        this.code = code;
        this.errorDesc = errorDesc;
    }

    public BaseResponse(int code, String errorDesc, String transaction) {
        this.code = code;
        this.errorDesc = errorDesc;
        this.transaction = transaction;
    }

    /** 响应类型。 */
    public abstract int getType();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public String getFromType() {
        return fromType;
    }

    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    @Override
    public String toString() {
        return "BaseResponse{code=" + code + ", errorDesc='" + errorDesc + "', transaction='" + transaction
                + "', conversationId='" + conversationId + "', conversationTitle='" + conversationTitle + "'}";
    }

    /** 响应错误码。 */
    public interface Code {
        int NO_SHARE = -1;
        int OK = 1;
        int CANCEL = 2;
        int AUTHOR_FAIL = 3;
        int TIMES_OVER = 4;
        int NON_SUPPORT = 5;
        int ARGUMENT_ERROR = 6;
        int CONVERSION_ERROR = 7;
        int NETWORK_ERROR = 8;
        int TOKEN_ERROR = 9;
        int SCENE_FORBID = 10;
        int NO_FRIEND = 11;
        int VIDEO_IS_SENDING = 12;
        int WEICHAT_NOT_INSTALL = 13;
        int MOMENT_NOT_INSTALL = 14;
        int SHARE_COUNT_TOO_MANY = 15;
        int MOMENT_NOT_SUPPORT_MULTI_IMAGE = 16;
        int TIMEMEMORY_NOT_PERMISSION = 17;
        int APP_NOT_PERMISSION = 18;
        int OTHER = 100;
        int SKIP_CODE = 1000;
    }

    /** 响应错误描述。 */
    public interface Desc {
        String OK = "share_success";
        String CANCEL = "share_cancel";
        String AUTHOR_FAIL = "author_fail";
        String TIMES_OVER = "share_times_over";
        String NON_SUPPORT = "share_non_support";
        String ARGUMENT_ERROR = "share_argument_error";
        String CONVERSION_ERROR = "share_conversation_error";
        String NETWORK_ERROR = "share_network_error";
        String TOKEN_ERROR = "share_token_error";
        String VERSION_ERROR = "share_version_error";
        String VIDEO_IS_SENDING = "video_is_sending";
        String SHARE_COUNT_ERROR = "share_count_too_many_error";
        String MOMENT_NOT_SUPPORT_MULTI_IMAGE_ERROR = "moment_not_support_multi_image_error";
        String OTHER = "share_other_error";
    }
}
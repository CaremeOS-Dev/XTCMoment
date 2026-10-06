package com.xtc.qiniu;

/** Constants of the cloud-storage upload. */
public interface Constants {

    String QINIU_TAG = "qiniu_upload_";

    /** Error codes reported by the upload listener. */
    interface ErrorCode {
        int FILE_NOT_EXIT = 9202;
        int KEY_EMPTY = 9203;
        int PATH_EMPTY = 9201;
        int QINIU_NOT_RESPONSE = 9205;
        int TOKEN_EMPTY = 9204;
    }

    /** Shared-preferences keys of the cloud storage. */
    interface ICloud {
        String UPLOAD_TOKEN = "upload_token_";
    }

    /** Upload methods queued by the limit agent. */
    interface UploadMethod {
        int UPLOAD_COVER = 1;
        int UPLOAD_NO_SPACE = 3;
        int UPLOAD_SPACE = 2;
    }
}
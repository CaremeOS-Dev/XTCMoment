package com.xtc.qiniu;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.common.AutoZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.http.dns.DnsSource;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.FileRecorder;
import com.qiniu.android.storage.GlobalConfiguration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.xtc.log.LogUtil;
import com.xtc.qiniu.bean.NetUploadToken;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.encode.UUIDUtil;
import com.xtc.utils.storage.FolderManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Qiniu-backed {@link ICloudManager}. */
public class QiNiuManager extends ICloudManager {

    private static final String TAG = "qiniu_upload_QiNiuManager";
    private static final String QINIU_UPLOAD_RECORD_FILE = "qiniu_upload_record";

    private static volatile QiNiuManager instance;

    /** Upload tuning parameters. */
    interface UploadParams {
        int CHUNK_SIZE = 512000;
        int CONCURRENT_TASK_COUNT = 5;
        int CONNECT_TIMEOUT = 10;
        int PUT_THRESHOLD = 2097152;
        int RESPONSE_TIMEOUT = 60;
        int RETRY_MAX = 1;
        int WRITE_TIMEOUT = 30;
    }

    private final Context context;
    private final ConcurrentHashMap<String, Boolean> cancelMap = new ConcurrentHashMap<>();
    private final UploadManager uploadManager;
    private volatile XtcDns xtcDns;

    public static QiNiuManager getInstance(Context context, String ucServer) {
        QiNiuManager manager = instance;
        if (manager == null) {
            synchronized (QiNiuManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new QiNiuManager(context, ucServer);
                    instance = manager;
                }
            }
        }
        return manager;
    }

    /** @deprecated use {@link #getInstance(Context, String)}. */
    @Deprecated
    public static QiNiuManager getInstance(Context context) {
        return getInstance(context, null);
    }

    private QiNiuManager(Context context, String ucServer) {
        this.context = context;
        this.uploadManager = createUploadManager(ucServer);
    }

    /** @deprecated use {@link #createUploadManager(String)}. */
    @Deprecated
    public UploadManager getUploadManager() {
        return createUploadManager(null);
    }

    /** Builds an upload manager with the app upload configuration. */
    public UploadManager createUploadManager(String ucServer) {
        FileRecorder recorder;
        FolderManager.getInstance().checkFreeSpace();
        try {
            File recordFile = File.createTempFile(QINIU_UPLOAD_RECORD_FILE, ".tmp");
            String parent = recordFile.getParent();
            LogUtil.d(TAG, "HXQ-qiniu, create record file = " + recordFile.getAbsolutePath());
            recorder = new FileRecorder(parent);
        } catch (Exception e) {
            e.printStackTrace();
            recorder = null;
        }
        KeyGenerator keyGenerator = new KeyGenerator() {
            @Override
            public String gen(String key, File file) {
                return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            }

            @Override
            public String gen(String key, String path) {
                return key + path;
            }
        };
        this.xtcDns = new XtcDns(this.context);
        GlobalConfiguration.getInstance().dns = this.xtcDns;
        AutoZone zone = new AutoZone();
        zone.setUcServer(ucServer);
        return new UploadManager(new Configuration.Builder()
                .useHttps(true)
                .useConcurrentResumeUpload(true)
                .resumeUploadVersion(Configuration.RESUME_UPLOAD_VERSION_V2)
                .putThreshold(UploadParams.PUT_THRESHOLD)
                .chunkSize(UploadParams.CHUNK_SIZE)
                .concurrentTaskCount(UploadParams.CONCURRENT_TASK_COUNT)
                .connectTimeout(UploadParams.CONNECT_TIMEOUT)
                .responseTimeout(UploadParams.RESPONSE_TIMEOUT)
                .writeTimeout(UploadParams.WRITE_TIMEOUT)
                .retryMax(UploadParams.RETRY_MAX)
                .recorder(recorder, keyGenerator)
                .zone(zone)
                .build());
    }

    @Override
    public void cancle(String tag) {
        LogUtil.d(TAG, "cancle: cancelKey = [" + tag + "]");
        if (this.cancelMap.containsKey(tag)) {
            this.cancelMap.put(tag, Boolean.TRUE);
        }
        this.cancelMap.remove(tag);
    }

    /** Clears the cancelled entries of the upload map. */
    public void clearCancel() {
        ArrayList<String> cancelled = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : this.cancelMap.entrySet()) {
            if (entry.getValue()) {
                cancelled.add(entry.getKey());
            } else {
                LogUtil.i(TAG, "entry.getValue() is false");
            }
        }
        for (String key : cancelled) {
            this.cancelMap.remove(key);
            LogUtil.d(TAG, "remove key = " + key);
        }
        cancelled.clear();
    }

    @Override
    public String uploadData(int spaceType, String key, byte[] data, final OnUpLoadListener listener) {
        final String tag = UUIDUtil.randomUUID();
        this.cancelMap.put(tag, Boolean.FALSE);
        upload(spaceType, key, data, new UpCompletionHandler() {
            @Override
            public void complete(String key2, ResponseInfo responseInfo, JSONObject response) {
                QiNiuManager.this.cancelMap.remove(tag);
                QiNiuManager.this.handlerComplete(listener, key2, responseInfo, response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key2, double percent) {
                if (listener != null) {
                    listener.onProgress(key2, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return checkIsCancelled(tag);
            }
        }));
        return tag;
    }

    @Override
    protected String uploadFile(final String tag, String filePath, String key, String token,
            final OnUpLoadListener listener) {
        if (listener == null) {
            LogUtil.d(TAG, "onUpLoadListener is null");
            return "";
        }
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.d(TAG, "filePath is empty");
            listener.onFailure(key, Constants.ErrorCode.PATH_EMPTY, "filePath is empty");
            return "";
        }
        if (!new File(filePath).exists()) {
            LogUtil.d(TAG, "file is not exists！filePath:" + filePath);
            listener.onFailure(key, Constants.ErrorCode.FILE_NOT_EXIT, "file is not exists！filePath:" + filePath);
            return "";
        }
        if (TextUtils.isEmpty(key)) {
            LogUtil.d(TAG, "key is empty");
            listener.onFailure(key, Constants.ErrorCode.KEY_EMPTY, "key is empty");
            return "";
        }
        if (TextUtils.isEmpty(token)) {
            LogUtil.d(TAG, "token is empty");
            listener.onFailure(key, Constants.ErrorCode.TOKEN_EMPTY, "token is empty");
            return "";
        }
        this.cancelMap.put(tag, Boolean.FALSE);
        this.uploadManager.put(filePath, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key2, ResponseInfo responseInfo, JSONObject response) {
                QiNiuManager.this.cancelMap.remove(tag);
                QiNiuManager.this.handlerComplete(listener, key2, responseInfo, response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key2, double percent) {
                LogUtil.i(TAG, "progress: " + percent);
                if (listener == null) {
                    LogUtil.d(TAG, "onUpLoadListener is null");
                } else {
                    listener.onProgress(key2, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return checkIsCancelled(tag);
            }
        }));
        return tag;
    }

    /** @deprecated use {@link #uploadFileByCover(String, int, String, File, OnUpLoadListener)}. */
    @Override
    @Deprecated
    public String uploadFileByCover(int spaceType, String key, File file, OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().upLoadFileByCover(this.context, spaceType, key, file, listener);
    }

    /** @deprecated use {@link #uploadFile(String, int, String, File, OnUpLoadListener)}. */
    @Override
    @Deprecated
    public String uploadFile(int spaceType, String key, File file, OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().upLoadFile(this.context, spaceType, key, file, listener);
    }

    /** @deprecated use {@link #uploadFile(String, String, String, String, OnUpLoadListener)}. */
    @Override
    @Deprecated
    public String uploadFile(String filePath, String key, String token, OnUpLoadListener listener) {
        return UploadLimitAgent.getInstance().uploadFile(this.context, filePath, key, token, listener);
    }

    @Override
    protected String uploadFileByCover(final String tag, int spaceType, String key, File file,
            final OnUpLoadListener listener) {
        this.cancelMap.put(tag, Boolean.FALSE);
        uploadByCover(spaceType, key, file, new UpCompletionHandler() {
            @Override
            public void complete(String key2, ResponseInfo responseInfo, JSONObject response) {
                QiNiuManager.this.cancelMap.remove(tag);
                QiNiuManager.this.handlerComplete(listener, key2, responseInfo, response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key2, double percent) {
                if (listener != null) {
                    listener.onProgress(key2, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return checkIsCancelled(tag);
            }
        }));
        return tag;
    }

    private void uploadByCover(int spaceType, final String key, final File file,
            final UpCompletionHandler handler, final UploadOptions options) {
        if (file != null && file.exists()) {
            TokenManager.getUploadToken(this.context, spaceType, key, new TokenManager.OnNewUpLoadTokenListener() {
                @Override
                public void onSuccess(String token) {
                    LogUtil.i(TAG, "upLoadByCover() get token success : " + token + "file length is :"
                            + file.length());
                    QiNiuManager.this.uploadManager.put(file, key, token, handler, options);
                }

                @Override
                public void onFailure(String message, Throwable throwable) {
                    LogUtil.w(TAG, "upLoadByCover() get token failed :" + message);
                    handler.complete(key, null, null);
                }
            });
        } else {
            LogUtil.w(TAG, "upLoadByCover() file is not exist.");
            handler.complete(key, null, null);
        }
    }

    @Override
    public String uploadFile(final String tag, int spaceType, String key, File file,
            final OnUpLoadListener listener) {
        this.cancelMap.put(tag, Boolean.FALSE);
        upload(spaceType, key, file, new UpCompletionHandler() {
            @Override
            public void complete(String key2, ResponseInfo responseInfo, JSONObject response) {
                QiNiuManager.this.cancelMap.remove(tag);
                QiNiuManager.this.handlerComplete(listener, key2, responseInfo, response);
            }
        }, new UploadOptions(null, null, false, new UpProgressHandler() {
            @Override
            public void progress(String key2, double percent) {
                if (listener != null) {
                    listener.onProgress(key2, percent);
                }
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return checkIsCancelled(tag);
            }
        }));
        return tag;
    }

    private boolean checkIsCancelled(String tag) {
        Boolean cancelled = this.cancelMap.get(tag);
        boolean isCancelled = cancelled == null || cancelled;
        if (isCancelled) {
            LogUtil.d(TAG, "checkIsCancelled: tag = [" + tag + "], isTagCancel = [" + cancelled + "]");
        }
        return isCancelled;
    }

    private void upload(int spaceType, final String key, final File file, final UpCompletionHandler handler,
            final UploadOptions options) {
        if (file == null || !file.exists()) {
            return;
        }
        NetUploadToken token = JSONUtil.fromJSON(SharedTool.getUploadToken(this.context, spaceType),
                NetUploadToken.class);
        if (token != null && token.token != null) {
            this.uploadManager.put(file, key, token.token, handler, options);
        } else {
            TokenManager.getUploadToken(this.context, spaceType, new TokenManager.OnNewUpLoadTokenListener() {
                @Override
                public void onSuccess(String token2) {
                    LogUtil.i(TAG, "upLoad()_File get token success: " + token2 + " file length is :"
                            + file.length());
                    QiNiuManager.this.uploadManager.put(file, key, token2, handler, options);
                }

                @Override
                public void onFailure(String message, Throwable throwable) {
                    LogUtil.w(TAG, "upLoad()_File get token failed: " + message);
                    handler.complete(key, null, null);
                }
            });
        }
    }

    private void upload(int spaceType, final String key, final byte[] data, final UpCompletionHandler handler,
            final UploadOptions options) {
        if (data == null) {
            return;
        }
        NetUploadToken token = JSONUtil.fromJSON(SharedTool.getUploadToken(this.context, spaceType),
                NetUploadToken.class);
        if (token != null && token.token != null) {
            this.uploadManager.put(data, key, token.token, handler, options);
        } else {
            TokenManager.getUploadToken(this.context, spaceType, new TokenManager.OnNewUpLoadTokenListener() {
                @Override
                public void onSuccess(String token2) {
                    LogUtil.i(TAG, "upLoad()_byte[] get token success: " + token2 + "data length is :" + data.length);
                    QiNiuManager.this.uploadManager.put(data, key, token2, handler, options);
                }

                @Override
                public void onFailure(String message, Throwable throwable) {
                    LogUtil.i(TAG, "upLoad()_byte[] get token failed: " + message);
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.cancelMap.clear();
        LogUtil.d(TAG, "finalize");
    }

    /** Maps the qiniu completion callback onto the {@link OnUpLoadListener}. */
    public void handlerComplete(OnUpLoadListener listener, String key, ResponseInfo responseInfo,
            JSONObject response) {
        LogUtil.d(TAG, "complete() called with: key = [" + key + "], respInfo = [" + responseInfo + "], response = ["
                + response + "]");
        if (listener == null) {
            LogUtil.d(TAG, "onUpLoadListener is null");
            return;
        }
        if (responseInfo == null) {
            LogUtil.w(TAG, "uploadFile() respInfo is null,Qiniu server not response");
            listener.onFailure(key, Constants.ErrorCode.QINIU_NOT_RESPONSE, "七牛服务器未响应");
            checkNeedClearDns(null);
            return;
        }
        if (response == null) {
            LogUtil.w(TAG, "uploadFile() response is null");
        } else {
            try {
                key = response.getString("key");
                LogUtil.d(TAG, "response key = " + key);
            } catch (JSONException e) {
                LogUtil.e(TAG, e);
            }
        }
        if (!responseInfo.isOK()) {
            LogUtil.w(TAG, key + "uploadFile() upload failed ,status:" + responseInfo.statusCode
                    + ",error info is :" + responseInfo.error);
            listener.onFailure(key, responseInfo.statusCode, responseInfo.error);
            checkNeedClearDns(responseInfo);
            return;
        }
        LogUtil.i(TAG, "upload Success");
        listener.onSuccess(key);
    }

    private void checkNeedClearDns(ResponseInfo responseInfo) {
        if (responseInfo == null) {
            QiNiuUploadBehavior.uploadFail(-1, "null");
            return;
        }
        LogUtil.d(TAG, "checkNeedClearDns");
        QiNiuUploadBehavior.uploadFail(responseInfo.statusCode, responseInfo.error);
        if (!TextUtils.isEmpty(responseInfo.error) && responseInfo.error.toLowerCase().contains(DnsSource.Udp)) {
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    if (QiNiuManager.this.xtcDns == null) {
                        return;
                    }
                    QiNiuManager.this.xtcDns.clear();
                }
            });
        }
    }
}
package com.xtc.qiniu;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.qiniu.bean.NetDownloadTokenParam;
import com.xtc.qiniu.bean.NetUploadToken;
import com.xtc.qiniu.net.ICloudHttpServiceProxy;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/** Requests and caches the cloud-storage upload/download tokens. */
public class TokenManager {

    private static final String TAG = "TokenManager";

    private static ICloudHttpServiceProxy cloudHttpServiceProxy;

    /** Callback of a download-token request. */
    public interface OnDownLoadTokenListener {
        void onFailure(String message);

        void onSuccess(List<String> tokens);
    }

    /** Callback of a download-token request that carries the throwable. */
    public interface OnNewDownLoadTokenListener {
        void onFailure(String message, Throwable throwable);

        void onSuccess(List<String> tokens);
    }

    /** Callback of an upload-token request that carries the throwable. */
    public interface OnNewUpLoadTokenListener {
        void onFailure(String message, Throwable throwable);

        void onSuccess(String token);
    }

    /** Callback of an upload-token request. */
    public interface OnUpLoadTokenListener {
        void onFailure(String message);

        void onSuccess(String token);
    }

    private TokenManager() {
    }

    public static void getUploadToken(Context context, int spaceType, String key, final OnUpLoadTokenListener listener) {
        getUploadToken(context, spaceType, key, new OnNewUpLoadTokenListener() {
            @Override
            public void onSuccess(String token) {
                if (listener != null) {
                    listener.onSuccess(token);
                }
            }

            @Override
            public void onFailure(String message, Throwable throwable) {
                if (listener != null) {
                    listener.onFailure(message);
                }
            }
        });
    }

    public static void getUploadToken(Context context, int spaceType, String key,
            final OnNewUpLoadTokenListener listener) {
        proxy(context);
        LogUtil.i(TAG, "getUploadToken()_4args start uploading token");
        cloudHttpServiceProxy.getUploadToken(spaceType, key)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "getUploadToken() onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getUploadToken()_4args request failed ：", throwable);
                        if (listener != null) {
                            listener.onFailure("The server is not responding.", throwable);
                        }
                    }

                    @Override
                    public void onNext(String token) {
                        LogUtil.i(TAG, "getUploadToken()_4args request success " + token);
                        if (listener != null) {
                            listener.onSuccess(token);
                        }
                    }
                });
    }

    public static void getUploadToken(Context context, int spaceType, final OnUpLoadTokenListener listener) {
        getUploadToken(context, spaceType, new OnNewUpLoadTokenListener() {
            @Override
            public void onSuccess(String token) {
                if (listener != null) {
                    listener.onSuccess(token);
                }
            }

            @Override
            public void onFailure(String message, Throwable throwable) {
                if (listener != null) {
                    listener.onFailure(message);
                }
            }
        });
    }

    public static void getUploadToken(final Context context, final int spaceType,
            final OnNewUpLoadTokenListener listener) {
        proxy(context);
        LogUtil.i(TAG, "getUploadToken()_3args start uploading token");
        cloudHttpServiceProxy.getUploadToken(spaceType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "getUploadToken()_3args onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getUploadToken()_3args request failed ", throwable);
                        if (listener != null) {
                            listener.onFailure("The server is not responding.", throwable);
                        }
                    }

                    @Override
                    public void onNext(String token) {
                        LogUtil.i(TAG, "getUploadToken()_3args request success " + token);
                        NetUploadToken uploadToken = new NetUploadToken();
                        uploadToken.setToken(token);
                        uploadToken.setTimestamp(System.currentTimeMillis());
                        SharedTool.saveUploadToken(context, spaceType, JSONUtil.toJSON(uploadToken));
                        if (listener != null) {
                            listener.onSuccess(token);
                        }
                    }
                });
    }

    public static void getDownloadToken(Context context, int fileType, List<String> keys, Integer longEdge,
            Integer shortEdge, final OnDownLoadTokenListener listener) {
        getDownloadToken(context, fileType, keys, longEdge, shortEdge, new OnNewDownLoadTokenListener() {
            @Override
            public void onSuccess(List<String> tokens) {
                if (listener != null) {
                    listener.onSuccess(tokens);
                }
            }

            @Override
            public void onFailure(String message, Throwable throwable) {
                if (listener != null) {
                    listener.onFailure(throwable.toString());
                }
            }
        });
    }

    public static void getDownloadToken(Context context, int fileType, List<String> keys, Integer longEdge,
            Integer shortEdge, final OnNewDownLoadTokenListener listener) {
        proxy(context);
        NetDownloadTokenParam param = new NetDownloadTokenParam();
        param.setFileType(fileType);
        param.setKeys(keys);
        param.setLongEdge(longEdge);
        param.setShortEdge(shortEdge);
        LogUtil.i(TAG, "getDownloadToken()_6args start downloading token");
        cloudHttpServiceProxy.getDownloadToken(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "getDownloadToken()_6args onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getDownloadToken()_6args download failed：", throwable);
                        if (listener != null) {
                            listener.onFailure(throwable.toString(), throwable);
                        }
                    }

                    @Override
                    public void onNext(List<String> tokens) {
                        LogUtil.e(TAG, "getDownloadToken()_6args download success " + tokens);
                        if (listener != null) {
                            listener.onSuccess(tokens);
                        }
                    }
                });
    }

    public static void getDownloadToken(Context context, int fileType, List<String> keys, Integer longEdge,
            Integer shortEdge, int quality, String format, final OnDownLoadTokenListener listener) {
        getDownloadToken(context, fileType, keys, longEdge, shortEdge, quality, format,
                new OnNewDownLoadTokenListener() {
                    @Override
                    public void onSuccess(List<String> tokens) {
                        if (listener != null) {
                            listener.onSuccess(tokens);
                        }
                    }

                    @Override
                    public void onFailure(String message, Throwable throwable) {
                        if (listener != null) {
                            listener.onFailure(message);
                        }
                    }
                });
    }

    public static void getDownloadToken(Context context, int fileType, List<String> keys, Integer longEdge,
            Integer shortEdge, int quality, String format, final OnNewDownLoadTokenListener listener) {
        proxy(context);
        NetDownloadTokenParam param = new NetDownloadTokenParam();
        param.setFileType(fileType);
        param.setKeys(keys);
        param.setLongEdge(longEdge);
        param.setShortEdge(shortEdge);
        param.setQuality(quality);
        param.setFormat(format);
        LogUtil.i(TAG, "getDownloadToken()_8args start downloading token");
        cloudHttpServiceProxy.getDownloadToken(param)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "getDownloadToken()_8args onCompleted");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getDownloadToken()_8args download failed: ", throwable);
                        if (listener != null) {
                            listener.onFailure(throwable.toString(), throwable);
                        }
                    }

                    @Override
                    public void onNext(List<String> tokens) {
                        LogUtil.i(TAG, "getDownloadToken()_8args download success：" + tokens);
                        if (listener != null) {
                            listener.onSuccess(tokens);
                        }
                    }
                });
    }

    private static void proxy(Context context) {
        if (cloudHttpServiceProxy == null) {
            cloudHttpServiceProxy = new ICloudHttpServiceProxy(context);
        }
    }
}
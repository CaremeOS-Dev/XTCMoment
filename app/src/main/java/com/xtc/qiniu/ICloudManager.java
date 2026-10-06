package com.xtc.qiniu;

import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.encode.UUIDUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** Base of the cloud-storage managers, providing the shared download logic. */
public abstract class ICloudManager {

    private static final String TAG = "ICloudManager";

    /** Error codes of the download listener. */
    public interface ErrorCode {
        int CONNECT_FAIL = 4;
        int DOWNLOAD_ERROR = 1;
        int EMPTY_DATA = 3;
        int TOKEN_OUTDATE = 2;
    }

    /** Progress/finish callback of a download. */
    public interface OnDownLoadListener {
        void onError(int code, String message);

        void onFinish(byte[] data);

        void onProgress(long current, long total);
    }

    /** Progress/finish callback of an upload. */
    public interface OnUpLoadListener {
        void onFailure(String key, int code, String message);

        void onProgress(String key, double percent);

        void onSuccess(String key);
    }

    private final OkHttpClient client = new OkHttpClient();

    public abstract void cancle(String tag);

    public abstract String uploadData(int spaceType, String key, byte[] data, OnUpLoadListener listener);

    /** @deprecated use {@link #uploadFile(String, int, String, File, OnUpLoadListener)}. */
    @Deprecated
    public abstract String uploadFile(int spaceType, String key, File file, OnUpLoadListener listener);

    protected abstract String uploadFile(String tag, int spaceType, String key, File file, OnUpLoadListener listener);

    /** @deprecated use {@link #uploadFile(String, String, String, String, OnUpLoadListener)}. */
    @Deprecated
    public abstract String uploadFile(String filePath, String key, String token, OnUpLoadListener listener);

    protected abstract String uploadFile(String tag, String filePath, String key, String token,
            OnUpLoadListener listener);

    /** @deprecated use {@link #uploadFileByCover(String, int, String, File, OnUpLoadListener)}. */
    @Deprecated
    public abstract String uploadFileByCover(int spaceType, String key, File file, OnUpLoadListener listener);

    protected abstract String uploadFileByCover(String tag, int spaceType, String key, File file,
            OnUpLoadListener listener);

    /** Downloads [url] into the {@code directory/name} file. */
    public void downloadForFile(String url, String directory, String name, final OnDownLoadListener listener) {
        if (TextUtils.isEmpty(name)) {
            name = UUIDUtil.randomUUID();
        }
        final File target = new File(directory, name);
        Call call = this.client.newCall(new Request.Builder().url(url).build());
        LogUtil.i(TAG, "downloadForFile() starting download");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "downloadForFile()  download error:" + e);
                downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error", listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.code() == 401) {
                    LogUtil.e(TAG, "downloadForFile() download error,response code is 401");
                    downloadFail(ErrorCode.TOKEN_OUTDATE, "download token out of data!!!", listener);
                    return;
                }
                if (response.code() != 200) {
                    LogUtil.e(TAG, "downloadForFile() download error，response code is :" + response.code());
                    downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error ,http state:" + response.code(), listener);
                    return;
                }
                InputStream inputStream = body.byteStream();
                byte[] buffer = new byte[1024];
                long total = body.contentLength();
                long current = 0;
                if (total == 0) {
                    LogUtil.e(TAG, "downloadForFile() download error，date is empty,response code is:" + response.code());
                    downloadFail(ErrorCode.EMPTY_DATA, "date is empty,http state:" + response.code(), listener);
                    return;
                }
                FileOutputStream outputStream = new FileOutputStream(target);
                try {
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                        outputStream.flush();
                        current += read;
                        if (listener != null) {
                            listener.onProgress(current, total);
                        }
                    }
                    outputStream.flush();
                    if (listener != null) {
                        LogUtil.i(TAG, "downloadForFile() onDownloadListener.onFinish download totalSize is : " + total);
                        listener.onFinish(null);
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "downloadForFile() download error：" + e);
                    downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error", listener);
                } finally {
                    closeQuietly(inputStream);
                    closeQuietly(outputStream);
                }
            }
        });
    }

    /** Downloads [url] into memory. */
    public void downloadForBytes(String url, final OnDownLoadListener listener) {
        Call call = this.client.newCall(new Request.Builder().url(url).build());
        LogUtil.i(TAG, "downloadForBytes() starting download");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "downloadForBytes() download error：" + e);
                downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error", listener);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.code() == 401) {
                    LogUtil.e(TAG, "downloadForBytes() download error，response code is : 401");
                    downloadFail(ErrorCode.TOKEN_OUTDATE, "download token out of data!!!!", listener);
                    return;
                }
                if (response.code() != 200) {
                    LogUtil.e(TAG, "downloadForBytes() download error，response code is ：" + response.code());
                    downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error,http state:" + response.code(), listener);
                    return;
                }
                InputStream inputStream = body.byteStream();
                byte[] buffer = new byte[1024];
                long total = body.contentLength();
                long current = 0;
                if (total == 0) {
                    LogUtil.e(TAG, "downloadForBytes() error, date is empty ,response code is ：" + response.code());
                    downloadFail(ErrorCode.EMPTY_DATA, "date is empty,http state:" + response.code(), listener);
                    return;
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                        outputStream.flush();
                        current += read;
                        if (listener != null) {
                            listener.onProgress(current, total);
                        }
                    }
                    if (listener != null) {
                        LogUtil.i(TAG, "downloadForBytes() onProgressListener.onFinish downloadtotalSize is :" + total);
                        listener.onFinish(outputStream.toByteArray());
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "downloadForBytes() download error：" + e);
                    downloadFail(ErrorCode.DOWNLOAD_ERROR, "download error", listener);
                } finally {
                    closeQuietly(inputStream);
                    closeQuietly(outputStream);
                }
            }
        });
    }

    private void downloadFail(int code, String message, OnDownLoadListener listener) {
        if (listener != null) {
            listener.onError(code, message);
        } else {
            LogUtil.i(TAG, "listener is null");
        }
    }

    private static void closeQuietly(java.io.Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "close error: " + e);
        }
    }
}
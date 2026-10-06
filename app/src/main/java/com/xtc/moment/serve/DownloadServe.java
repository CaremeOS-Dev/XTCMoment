package com.xtc.moment.serve;

import android.app.DownloadManager;
import android.text.TextUtils;

import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.util.FileManager;
import com.xtc.utils.storage.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadServe {

    private static final String TAG = DownloadServe.class.getSimpleName();

    private static volatile DownloadServe instance;

    private final OkHttpClient okHttpClient = new OkHttpClient();

    public interface DownLoadInfoListener {
        void onFail(String str);

        void onSuccess(String str);
    }

    public interface OnDownloadListener {
        void onDownloadFailed(Exception exc);

        void onDownloadSuccess(File file);

        void onDownloading(int progress);
    }

    private DownloadServe() {
    }

    public static DownloadServe getInstance() {
        if (instance == null) {
            synchronized (DownloadServe.class) {
                if (instance == null) {
                    instance = new DownloadServe();
                }
            }
        }
        return instance;
    }

    public void startDownload(String url, final String destPath, final DownLoadInfoListener listener) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(destPath) || listener == null) {
            return;
        }
        download(url, FileManager.getLivePhotoCachePath(), System.currentTimeMillis() + FileManager.MP4_FORMAT, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                FileUtils.copy(file.getPath(), destPath);
                file.delete();
                MainHandlerUtil.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(destPath);
                    }
                });
            }

            @Override
            public void onDownloading(int progress) {
                LogUtil.i(DownloadManager.class.getSimpleName(), " download progress:" + progress);
            }

            @Override
            public void onDownloadFailed(Exception exception) {
                exception.printStackTrace();
                MainHandlerUtil.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFail(destPath);
                    }
                });
            }
        });
    }

    public boolean startDownload(String url, String destPath, String suffix) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(destPath)) {
            return false;
        }
        String cachePath = FileManager.getLivePhotoCachePath();
        String fileName = System.currentTimeMillis() + suffix;
        boolean success = download(url, cachePath, fileName);
        File file = new File(cachePath + fileName);
        if (success) {
            FileUtils.copy(file.getPath(), destPath);
            file.delete();
        } else if (file.exists()) {
            file.delete();
        }
        return success;
    }

    public void download(String url, final String dirPath, final String fileName, final OnDownloadListener listener) {
        okHttpClient.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onDownloadFailed(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                byte[] buffer = new byte[4096];
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, fileName);
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    long contentLength = response.body().contentLength();
                    outputStream = new FileOutputStream(file);
                    long total = 0L;
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                        total += read;
                        if (listener != null) {
                            listener.onDownloading((int) (((total * 1.0f) / contentLength) * 100.0f));
                        }
                    }
                    outputStream.flush();
                    if (listener != null) {
                        listener.onDownloadSuccess(file);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onDownloadFailed(e);
                    }
                } finally {
                    closeQuietly(inputStream);
                    closeQuietly(outputStream);
                }
            }
        });
    }

    public boolean download(String url, String dirPath, String fileName) {
        try {
            Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
            byte[] buffer = new byte[4096];
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = response.body().byteStream();
                response.body().contentLength();
                outputStream = new FileOutputStream(file);
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                closeQuietly(inputStream);
                closeQuietly(outputStream);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "download fail :", e);
            e.printStackTrace();
            return false;
        }
    }

    private static void closeQuietly(java.io.Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }
}
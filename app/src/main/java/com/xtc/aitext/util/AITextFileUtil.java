package com.xtc.aitext.util;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.utils.storage.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * AI 文案文件工具，负责资源拷贝与视频目录获取。
 */
public class AITextFileUtil {

    private static final String TAG = "FileUtils";
    /** 文件读写缓冲区大小（原实现使用 gdx GL20 常量）。 */
    private static final int BUFFER_SIZE = 10240;

    /** 进度回调。 */
    public interface OnProgressUpdateListener {
        void onProgressUpdate(double progress);
    }

    /** 获取视频缓存目录。 */
    public static String getVideoDir(Context context) {
        String dir = context.getExternalFilesDir("") + File.separator + "video" + File.separator;
        FileUtils.makeDirs(dir);
        return dir;
    }

    /** 从 assets 拷贝文件或目录。 */
    public static boolean copyFromAssets(Context context, String assetPath, String destPath) {
        try {
            String[] children = context.getAssets().list(assetPath);
            if (children != null && children.length > 0) {
                boolean allSuccess = true;
                for (String child : children) {
                    allSuccess &= copyFromAssets(context, assetPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child,
                            destPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child);
                }
                return allSuccess;
            }
            return writeFileFromStream(FileUtils.toFile(destPath), context.getAssets().open(assetPath), false, null);
        } catch (Exception e) {
            LogUtil.e(TAG, "copyFileFromAssets error ", e);
            return false;
        }
    }

    private static boolean writeFileFromStream(File file, InputStream inputStream, boolean append,
                                               OnProgressUpdateListener progressListener) throws IOException {
        if (inputStream == null || !FileUtils.createFile(file)) {
            LogUtil.e(TAG, "create file <" + file + "> failed.");
            return false;
        }
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file, append), BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            if (progressListener != null) {
                int available = inputStream.available();
                progressListener.onProgressUpdate(0.0d);
                int written = 0;
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    written += read;
                    progressListener.onProgressUpdate((double) written / available);
                }
            } else {
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "writeFileFromIS error ", e);
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                LogUtil.e(TAG, "writeFileFromIS is close error ", e);
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    LogUtil.e(TAG, "writeFileFromIS os error ", e);
                }
            }
        }
    }
}
package com.xtc.moment.util;

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
 * 动态资源文件工具：资源目录、assets 拷贝与带进度的文件写入。
 */
public class MomentFileUtils {

    private static final String TAG = "MomentFileUtils";

    private static final int BUFFER_SIZE = 4096;

    public interface OnProgressUpdateListener {
        void onProgressUpdate(double progress);
    }

    public static String getResourcePath(Context context) {
        String path = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "video";
        FileUtils.makeDirs(path);
        return path;
    }

    public static String getAnimPathByCate(Context context, String category) {
        return getResourcePath(context) + File.separator + category;
    }

    public static boolean copyFileFromAssets(Context context, String assetPath, String targetPath) {
        try {
            String[] children = context.getAssets().list(assetPath);
            if (children != null && children.length > 0) {
                boolean allCopied = true;
                for (String child : children) {
                    allCopied &= copyFileFromAssets(context,
                            assetPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child,
                            targetPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child);
                }
                return allCopied;
            }
            return writeFileFromIS(FileUtils.toFile(targetPath), context.getAssets().open(assetPath), false, null);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean writeFileFromIS(File file, InputStream inputStream, boolean append,
            OnProgressUpdateListener listener) throws IOException {
        if (inputStream == null || !FileUtils.recreateFile(file)) {
            LogUtil.e(TAG, "create file <" + file + "> failed.");
            return false;
        }
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file, append), BUFFER_SIZE);
            if (listener != null) {
                double total = inputStream.available();
                listener.onProgressUpdate(0.0d);
                byte[] buffer = new byte[BUFFER_SIZE];
                int written = 0;
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    written += read;
                    listener.onProgressUpdate(written / total);
                }
            } else {
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }
            return true;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
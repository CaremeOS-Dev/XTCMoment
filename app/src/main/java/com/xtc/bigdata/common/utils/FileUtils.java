package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.log.LogUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** File helpers used by the big-data library. */
public class FileUtils {

    public static final int BYTE = 1;
    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int GB = 1073741824;

    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    public static String getUriRealyPath(Context context, Uri uri) {
        return null;
    }

    public static String getUriReallyPath(Context context, Uri uri) {
        return null;
    }

    public static boolean isFileExists(String path) {
        return !TextUtils.isEmpty(path) && new File(path).exists();
    }

    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    public static boolean isDir(String path) {
        return isDir(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean isDir(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static boolean isFile(String path) {
        return isFile(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean isFile(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean createDirOrExists(String path) {
        return createDirOrExists(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean createDirOrExists(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean createFileOrExists(String path) {
        return createFileOrExists(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean createFileOrExists(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createDirOrExists(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    public static boolean createFileByDeleteOld(String path) {
        return createFileByDeleteOld(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean createFileByDeleteOld(File file) {
        if (file == null) {
            return false;
        }
        if ((file.exists() && file.isFile() && !file.delete()) || !createDirOrExists(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Deletes the directory tree. */
    public static boolean deleteDir(File file) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            return false;
        }
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isFile()) {
                    if (!deleteFile(child)) {
                        return false;
                    }
                } else if (child.isDirectory() && !deleteDir(child)) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    public static boolean deleteFile(String path) {
        return deleteFile(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || (file.isFile() && file.delete()));
    }

    public static List<File> listFilesInDir(File file, boolean recursive) {
        if (!isDir(file)) {
            return null;
        }
        ArrayList<File> files = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                files.add(child);
                if (recursive && child.isDirectory()) {
                    files.addAll(listFilesInDir(child, true));
                }
            }
        }
        return files;
    }

    public static List<File> listFilesInDir(File file) {
        return listFilesInDir(file, false);
    }

    public static List<File> listFilesInDirWithFilter(File file, String suffix, boolean recursive) {
        return listFilesInDirWithFilter(file, new SuffixFilter(suffix), recursive);
    }

    public static List<File> listFilesInDirWithFilter(File file, String suffix) {
        return listFilesInDirWithFilter(file, new SuffixFilter(suffix), false);
    }

    public static List<File> listFilesInDirWithFilter(File file, FilenameFilter filter, boolean recursive) {
        if (!isDir(file)) {
            return null;
        }
        ArrayList<File> files = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if (filter.accept(child.getParentFile(), child.getName())) {
                    files.add(child);
                }
                if (recursive && child.isDirectory()) {
                    files.addAll(listFilesInDirWithFilter(child, filter, true));
                }
            }
        }
        return files;
    }

    public static List<File> listFilesInDirWithFilter(File file, FilenameFilter filter) {
        return listFilesInDirWithFilter(file, filter, false);
    }

    public static boolean writeFile(File file, InputStream inputStream, boolean append) {
        if (file == null || inputStream == null || !createFileOrExists(file)) {
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);
            byte[] buffer = new byte[KB];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return false;
        } finally {
            CloseableUtils.closeAllQuietly(inputStream, outputStream);
        }
    }

    public static boolean writeFile(File file, String content, boolean append) {
        if (file == null || content == null || !createFileOrExists(file)) {
            return false;
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file, append), "UTF-8");
            writer.write(content);
            writer.flush();
            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return false;
        } finally {
            CloseableUtils.closeAllQuietly(writer);
        }
    }

    public static boolean writeFile(File file, byte[] data, boolean append) {
        if (file == null || data == null || !createFileOrExists(file)) {
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);
            outputStream.write(data);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return false;
        } finally {
            CloseableUtils.closeAllQuietly(outputStream);
        }
    }

    public static void transfer(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[KB];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
    }

    public static byte[] readFile2Bytes(String path) throws FileNotFoundException {
        return readFile2Bytes(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static byte[] readFile2Bytes(File file) throws FileNotFoundException {
        if (file == null) {
            return null;
        }
        return readFile2Bytes(new FileInputStream(file));
    }

    public static byte[] readFile2Bytes(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[KB];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            LogUtil.e(TAG, e);
            return null;
        } finally {
            CloseableUtils.closeAllQuietly(inputStream);
        }
    }

    public static long getFileSize(String path) {
        return getFileSize(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static long getFileSize(File file) {
        return (file == null || !file.exists()) ? 0 : file.length();
    }

    public static long getFileAvaiableSize(String path) {
        return getFileAvaiableSize(TextUtils.isEmpty(path) ? null : new File(path));
    }

    public static long getFileAvaiableSize(File file) {
        return file == null ? 0 : file.getUsableSpace();
    }

    public static void closeIO(Closeable closeable) {
        CloseableUtils.closeAllQuietly(closeable);
    }

    public static String getDirName(File file) {
        if (file == null) {
            return null;
        }
        String path = file.getPath();
        int index = path.lastIndexOf(File.separator);
        return index == -1 ? "" : path.substring(0, index + 1);
    }

    public static String getFileName(File file) {
        if (file == null) {
            return null;
        }
        String path = file.getPath();
        int index = path.lastIndexOf(File.separator);
        return index == -1 ? path : path.substring(index + 1);
    }

    public static String getFileExtension(File file) {
        if (file == null) {
            return null;
        }
        String name = file.getName();
        int index = name.lastIndexOf(46);
        return index == -1 ? "" : name.substring(index + 1);
    }

    /** Formats a byte count with the B/K/M/G suffix. */
    public static String byte2FitSize(long size) {
        if (size < KB) {
            return String.format(Locale.getDefault(), "%.3fB", Double.valueOf(size));
        }
        if (size < MB) {
            return String.format(Locale.getDefault(), "%.3fKB", Double.valueOf(size / 1024.0d));
        }
        if (size < GB) {
            return String.format(Locale.getDefault(), "%.3fMB", Double.valueOf(size / 1048576.0d));
        }
        return String.format(Locale.getDefault(), "%.3fGB", Double.valueOf(size / 1.073741824E9d));
    }

    /** Writes {@code content} into {@code dirPath/name}. */
    public static void saveFile(String dirPath, String content, String name) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtil.e(TAG, e);
            }
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes());
            LogUtil.i(TAG, "save file to sdcard successful:" + file.getAbsolutePath());
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        } finally {
            CloseableUtils.closeAllQuietly(outputStream);
        }
    }

    /** Reads the file at {@code path} as UTF-8, or null. */
    public static String readFromFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[KB];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return new String(outputStream.toByteArray(), "UTF-8");
        } catch (IOException e) {
            if (Constants.isDebug) {
                LogUtil.e(TAG, e);
            }
            return null;
        } finally {
            CloseableUtils.closeAllQuietly(inputStream);
        }
    }

    /** Writes {@code content} into the file at {@code path} as UTF-8. */
    public static void saveToFile(String content, String path) {
        File file = new File(path);
        FileOutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file, false);
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(content);
            writer.flush();
            outputStream.flush();
        } catch (Exception e) {
            if (Constants.isDebug) {
                LogUtil.e(TAG, e);
            }
        } finally {
            CloseableUtils.closeAllQuietly(outputStream, writer);
        }
    }

    /** Ensures the SD-card root path is configured. */
    public static void ensureSdCardPath() {
        if (TextUtils.isEmpty(ConfigAgent.getBehaviorConfig().sdcardRootPath)
                && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ConfigAgent.getBehaviorConfig().sdcardRootPath = Environment.getExternalStorageDirectory().getPath();
        }
    }

    /** Base directory of the big-data files. */
    public static String getBigDataDirPath() {
        ensureSdCardPath();
        return ConfigAgent.getBehaviorConfig().sdcardRootPath + DeviceInfo.BIG_DATA_DIR;
    }

    public static File getUploadDir(Context context, String type) {
        File externalFilesDir = context.getExternalFilesDir(type);
        if (externalFilesDir == null || !externalFilesDir.exists()) {
            return context.getFilesDir();
        }
        return TextUtils.isEmpty(externalFilesDir.getAbsolutePath()) ? context.getFilesDir() : externalFilesDir;
    }

    /** Filename filter matching a case-insensitive suffix. */
    private static class SuffixFilter implements FilenameFilter {
        private final String suffix;

        SuffixFilter(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.toUpperCase().endsWith(this.suffix.toUpperCase());
        }
    }
}
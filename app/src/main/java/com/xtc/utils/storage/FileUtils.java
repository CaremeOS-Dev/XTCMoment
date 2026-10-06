package com.xtc.utils.storage;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Xml;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;

import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * File-system helpers: existence/mkdir/delete, recursive listing, text and
 * binary IO, asset access, zip handling and a small XML binder.
 */
public class FileUtils {

    public static final int KB = 1024;
    public static final int MB = 1048576;
    public static final int GB = 1073741824;

    /** Copy buffer size shared by the stream helpers. */
    private static final int BUFFER_SIZE = 1024;

    private static final String TAG = "FileUtil";

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Path of the external storage root, or an empty string when missing. */
    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().exists()
                ? Environment.getExternalStorageDirectory().getPath() : "";
    }

    /** Total size of external storage, formatted for display. */
    private String getExternalTotalSize(Context context) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return Formatter.formatFileSize(context, ((long) statFs.getBlockSize()) * ((long) statFs.getBlockCount()));
    }

    /** Free size of external storage, formatted for display. */
    private String getExternalFreeSize(Context context) {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return Formatter.formatFileSize(context, ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks()));
    }

    /** Total size of internal storage, formatted for display. */
    private String getInternalTotalSize(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return Formatter.formatFileSize(context, ((long) statFs.getBlockSize()) * ((long) statFs.getBlockCount()));
    }

    /** Free size of internal storage, formatted for display. */
    private String getInternalFreeSize(Context context) {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return Formatter.formatFileSize(context, ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks()));
    }

    /** Wraps the path in a {@link File}, or null when empty. */
    public static File toFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        return new File(path);
    }

    /** @return true when the path exists. */
    public static boolean exists(String path) {
        return exists(toFile(path));
    }

    /** @return true when the file exists. */
    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    /** @return true when the path is an existing directory. */
    public static boolean isDirectory(String path) {
        return isDirectory(toFile(path));
    }

    /** @return true when the file is an existing directory. */
    public static boolean isDirectory(File file) {
        return exists(file) && file.isDirectory();
    }

    /** @return true when the path is an existing file. */
    public static boolean isFile(String path) {
        return isFile(toFile(path));
    }

    /** @return true when the file is an existing file. */
    public static boolean isFile(File file) {
        return exists(file) && file.isFile();
    }

    /** Creates the directory (and parents); true when it exists afterwards. */
    public static boolean makeDirs(String path) {
        return makeDirs(toFile(path));
    }

    /** Creates the directory (and parents); true when it exists afterwards. */
    public static boolean makeDirs(File file) {
        return file != null && (!file.exists() ? !file.mkdirs() : !file.isDirectory());
    }

    /** Creates the file (and parents) if missing; true when it exists afterwards. */
    public static boolean createFile(String path) {
        return createFile(toFile(path));
    }

    /** Creates the file (and parents) if missing; true when it exists afterwards. */
    public static boolean createFile(File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!makeDirs(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Recreates the file from scratch; true when it exists afterwards. */
    public static boolean recreateFile(String path) {
        return recreateFile(toFile(path));
    }

    /** Recreates the file from scratch; true when it exists afterwards. */
    public static boolean recreateFile(File file) {
        if (file == null) {
            return false;
        }
        if ((file.exists() && file.isFile() && !file.delete()) || !makeDirs(file.getParentFile())) {
            return false;
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }
    private static boolean copyDirectory(String srcPath, String destPath, boolean deleteSource) {
        return copyDirectory(toFile(srcPath), toFile(destPath), deleteSource);
    }

    /** Recursively copies a directory, optionally deleting the source. */
    private static boolean copyDirectory(File srcDir, File destDir, boolean deleteSource) {
        if (srcDir == null || destDir == null) {
            return false;
        }
        String srcPrefix = srcDir.getPath() + File.separator;
        String destPrefix = destDir.getPath() + File.separator;
        if (destPrefix.contains(srcPrefix) || !srcDir.exists() || !srcDir.isDirectory() || !makeDirs(destDir)) {
            return false;
        }
        for (File child : srcDir.listFiles()) {
            File dest = new File(destPrefix + child.getName());
            if (child.isFile()) {
                if (!copyFile(child, dest, deleteSource)) {
                    return false;
                }
            } else if (child.isDirectory() && !copyDirectory(child, dest, deleteSource)) {
                return false;
            }
        }
        return !deleteSource || deleteDirectory(srcDir);
    }

    /** Moves a single file, optionally deleting the source on success. */
    private static boolean copyFile(File srcFile, File destFile, boolean deleteSource) {
        if (srcFile != null && destFile != null && srcFile.exists() && srcFile.isFile()) {
            if ((destFile.exists() && destFile.isFile()) || !makeDirs(destFile.getParentFile())) {
                return false;
            }
            try {
                if (writeFile(destFile, new FileInputStream(srcFile), false)) {
                    return !deleteSource || deleteFile(srcFile);
                }
                return false;
            } catch (FileNotFoundException e) {
                LogUtil.e(TAG, e);
            }
        }
        return false;
    }

    /** Copies a file or directory tree. */
    public static boolean copy(String srcPath, String destPath) {
        return copy(toFile(srcPath), toFile(destPath));
    }

    /** Copies a file or directory tree. */
    public static boolean copy(File srcFile, File destFile) {
        return copyDirectory(srcFile, destFile, false);
    }

    /** Copies a single file. */
    public static boolean copyFile(String srcPath, String destPath) {
        return copyFile(toFile(srcPath), toFile(destPath));
    }

    /** Copies a single file. */
    public static boolean copyFile(File srcFile, File destFile) {
        return copyFile(srcFile, destFile, false);
    }

    /** Copies and deletes the source. */
    public static boolean move(String srcPath, String destPath) {
        return move(toFile(srcPath), toFile(destPath));
    }

    /** Copies and deletes the source. */
    public static boolean move(File srcFile, File destFile) {
        return copyDirectory(srcFile, destFile, true);
    }

    /** Moves a single file, deleting the source. */
    public static boolean moveFile(String srcPath, String destPath) {
        return moveFile(toFile(srcPath), toFile(destPath));
    }

    /** Moves a single file, deleting the source. */
    public static boolean moveFile(File srcFile, File destFile) {
        return copyFile(srcFile, destFile, true);
    }

    /** Deletes a directory tree. */
    public static boolean deleteDirectory(String path) {
        return deleteDirectory(toFile(path));
    }

    /** Deletes a directory tree. */
    public static boolean deleteDirectory(File file) {
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
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.isFile()) {
                    if (!deleteFile(child)) {
                        return false;
                    }
                } else if (child.isDirectory() && !deleteDirectory(child)) {
                    return false;
                }
            }
        }
        try {
            return file.delete();
        } catch (SecurityException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Deletes a single file. */
    public static boolean deleteFile(String path) {
        return deleteFile(toFile(path));
    }

    /** Deletes a single file. */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return false;
        }
        try {
            return !file.exists() || (file.isFile() && file.delete());
        } catch (SecurityException e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Deletes the directory contents but keeps the directory itself. */
    public static boolean cleanDirectory(String path) {
        return cleanDirectory(toFile(path));
    }

    /** Deletes the directory contents but keeps the directory itself. */
    public static boolean cleanDirectory(File file) {
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
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.isFile()) {
                    if (!deleteFile(child)) {
                        return false;
                    }
                } else if (child.isDirectory() && !deleteDirectory(child)) {
                    return false;
                }
            }
        }
        return true;
    }
    /** Lists the direct or recursive children of the path. */
    public static List<File> listFiles(String path, boolean recursive) {
        return listFiles(toFile(path), recursive);
    }

    /** Lists the direct or recursive children of the directory. */
    public static List<File> listFiles(File file, boolean recursive) {
        if (recursive) {
            return listFilesRecursive(file);
        }
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        Collections.addAll(result, file.listFiles());
        return result;
    }

    /** Lists all children recursively. */
    public static List<File> listFilesRecursive(String path) {
        return listFilesRecursive(toFile(path));
    }

    /** Lists all children recursively. */
    public static List<File> listFilesRecursive(File file) {
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                result.add(child);
                if (child.isDirectory()) {
                    result.addAll(listFilesRecursive(child));
                }
            }
        }
        return result;
    }

    /** Lists children whose name ends with {@code suffix}, optionally recursively. */
    public static List<File> listFilesBySuffix(String path, String suffix, boolean recursive) {
        return listFilesBySuffix(toFile(path), suffix, recursive);
    }

    /** Lists children whose name ends with {@code suffix}, optionally recursively. */
    public static List<File> listFilesBySuffix(File file, String suffix, boolean recursive) {
        if (recursive) {
            return listFilesBySuffixRecursive(file, suffix);
        }
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.getName().toUpperCase().endsWith(suffix.toUpperCase())) {
                    result.add(child);
                }
            }
        }
        return result;
    }

    /** Recursively lists children whose name ends with {@code suffix}. */
    public static List<File> listFilesBySuffix(String path, String suffix) {
        return listFilesBySuffixRecursive(toFile(path), suffix);
    }

    /** Recursively lists children whose name ends with {@code suffix}. */
    public static List<File> listFilesBySuffixRecursive(File file, String suffix) {
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.getName().toUpperCase().endsWith(suffix.toUpperCase())) {
                    result.add(child);
                }
                if (child.isDirectory()) {
                    result.addAll(listFilesBySuffixRecursive(child, suffix));
                }
            }
        }
        return result;
    }

    /** Lists children accepted by the filter, optionally recursively. */
    public static List<File> listFiles(String path, FilenameFilter filter, boolean recursive) {
        return listFiles(toFile(path), filter, recursive);
    }

    /** Lists children accepted by the filter, optionally recursively. */
    public static List<File> listFiles(File file, FilenameFilter filter, boolean recursive) {
        if (recursive) {
            return listFilesRecursive(file, filter);
        }
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (filter.accept(child.getParentFile(), child.getName())) {
                    result.add(child);
                }
            }
        }
        return result;
    }

    /** Recursively lists children accepted by the filter. */
    public static List<File> listFilesRecursive(String path, FilenameFilter filter) {
        return listFilesRecursive(toFile(path), filter);
    }

    /** Recursively lists children accepted by the filter. */
    public static List<File> listFilesRecursive(File file, FilenameFilter filter) {
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (filter.accept(child.getParentFile(), child.getName())) {
                    result.add(child);
                }
                if (child.isDirectory()) {
                    result.addAll(listFilesRecursive(child, filter));
                }
            }
        }
        return result;
    }

    /** Recursively lists children whose name equals {@code name}. */
    public static List<File> listFilesByName(String path, String name) {
        return listFilesByName(toFile(path), name);
    }

    /** Recursively lists children whose name equals {@code name}. */
    public static List<File> listFilesByName(File file, String name) {
        if (file == null || !isDirectory(file)) {
            return null;
        }
        ArrayList<File> result = new ArrayList<>();
        File[] children = file.listFiles();
        if (children != null && children.length != 0) {
            for (File child : children) {
                if (child.getName().toUpperCase().equals(name.toUpperCase())) {
                    result.add(child);
                }
                if (child.isDirectory()) {
                    result.addAll(listFilesByName(child, name));
                }
            }
        }
        return result;
    }
    /** Writes bytes to the file, optionally appending. */
    public static boolean writeBytes(File file, byte[] data, boolean append) {
        if (file == null || data == null || !createFile(file)) {
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);
            outputStream.write(data);
            outputStream.flush();
            PrivateUtils.close(outputStream);
            return true;
        } catch (Exception e) {
            PrivateUtils.close(outputStream);
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Writes an input stream to the path, optionally appending. */
    public static boolean writeFile(String path, InputStream inputStream, boolean append) {
        return writeFile(toFile(path), inputStream, append);
    }

    /** Writes an input stream to the file, optionally appending. */
    public static boolean writeFile(File file, InputStream inputStream, boolean append) {
        if (file == null || inputStream == null || !createFile(file)) {
            return false;
        }
        BufferedOutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer, 0, BUFFER_SIZE);
                if (read == -1) {
                    PrivateUtils.close(inputStream, outputStream);
                    return true;
                }
                outputStream.write(buffer, 0, read);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(inputStream, outputStream);
            return false;
        }
    }

    /** Copies the whole input stream into the output stream. */
    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer, 0, BUFFER_SIZE);
                if (read == -1) {
                    bufferedOutputStream.flush();
                    PrivateUtils.close(inputStream);
                    PrivateUtils.close(bufferedOutputStream);
                    return;
                }
                bufferedOutputStream.write(buffer, 0, read);
            }
        } catch (Exception e) {
            PrivateUtils.close(inputStream);
            PrivateUtils.close(bufferedOutputStream);
            throw e;
        }
    }

    /** Writes text to the path, optionally appending. */
    public static boolean writeString(String path, String content, boolean append) {
        return writeString(toFile(path), content, append);
    }

    /** Writes text to the file, optionally appending. */
    public static boolean writeString(File file, String content, boolean append) {
        if (file == null || content == null || !createFile(file)) {
            return false;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(content);
            PrivateUtils.close(writer);
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(writer);
            return false;
        }
    }

    /** Reads all lines of the path using the given charset. */
    public static List<String> readLines(String path, String charsetName) {
        return readLines(toFile(path), charsetName);
    }

    /** Reads all lines of the file using the given charset. */
    public static List<String> readLines(File file, String charsetName) {
        return readLines(file, 0, Integer.MAX_VALUE, charsetName);
    }

    /** Reads the given line range of the path. */
    public static List<String> readLines(String path, int startLine, int endLine, String charsetName) {
        return readLines(toFile(path), startLine, endLine, charsetName);
    }

    /** Reads the given line range (1-based, inclusive) of the file. */
    public static List<String> readLines(File file, int startLine, int endLine, String charsetName) {
        BufferedReader reader = null;
        if (file == null || startLine > endLine) {
            return null;
        }
        try {
            ArrayList<String> lines = new ArrayList<>();
            if (TextUtils.isEmpty(charsetName)) {
                reader = new BufferedReader(new FileReader(file));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            int lineNumber = 1;
            while (true) {
                String line = reader.readLine();
                if (line == null || lineNumber > endLine) {
                    break;
                }
                if (startLine <= lineNumber) {
                    lines.add(line);
                }
                lineNumber++;
            }
            PrivateUtils.close(reader);
            return lines;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(reader);
            return null;
        }
    }

    /** Reads the whole path into a string, joining lines with CRLF. */
    public static String readString(String path, String charsetName) {
        return readString(toFile(path), charsetName);
    }

    /** Reads the whole file into a string, joining lines with CRLF. */
    public static String readString(File file, String charsetName) {
        BufferedReader reader = null;
        if (file == null) {
            return null;
        }
        try {
            StringBuilder builder = new StringBuilder();
            if (TextUtils.isEmpty(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
                builder.append("\r\n");
            }
            if (builder.length() < 2) {
                PrivateUtils.close(reader);
                return "";
            }
            String result = builder.delete(builder.length() - 2, builder.length()).toString();
            PrivateUtils.close(reader);
            return result;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(reader);
            return null;
        }
    }

    /** Reads the file into a byte array. */
    public static byte[] readBytes(String path) {
        return readBytes(toFile(path));
    }

    /** Reads the file into a byte array. */
    public static byte[] readBytes(File file) {
        if (file == null) {
            return null;
        }
        try {
            return PrivateUtils.readStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }
    /** Decodes the file into a bitmap. */
    public static Bitmap readBitmap(File file) {
        return readBitmap(file, (BitmapFactory.Options) null);
    }

    /** Decodes the file into a bitmap with the given options. */
    public static Bitmap readBitmap(File file, BitmapFactory.Options options) {
        return readBitmap(file.getPath(), options);
    }

    /** Decodes the path into a bitmap. */
    public static Bitmap readBitmap(String path) {
        return readBitmap(path, (BitmapFactory.Options) null);
    }

    /** Decodes the path into a bitmap with the given options. */
    public static Bitmap readBitmap(String path, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(path, options);
    }

    /** Writes the bitmap into the file. */
    public static boolean writeBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int quality, File file) {
        if (bitmap == null || format == null || file == null) {
            LogUtil.e(TAG, "writeBitmap failed with bitmap=" + bitmap + ", format=" + format + ", file=" + file);
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            boolean success = bitmap.compress(format, quality, outputStream);
            outputStream.close();
            return success;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(outputStream);
            return false;
        }
    }

    /** Writes the bitmap into the path. */
    public static boolean writeBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int quality, String path) {
        return writeBitmap(bitmap, format, quality, new File(path));
    }

    /** Reads the whole asset into a byte array. */
    public static byte[] readAssets(Context context, String assetName) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer);
                if (read <= 0) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            byte[] result = outputStream.toByteArray();
            outputStream.close();
            inputStream.close();
            return result;
        } catch (Exception e) {
            PrivateUtils.close(inputStream);
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** Reads the whole asset as a UTF-8 string. */
    public static String readAssetsString(Context context, String assetName) {
        return readAssetsString(context, assetName, StandardCharsets.UTF_8);
    }

    /** Reads the whole asset as a string using the given charset. */
    public static String readAssetsString(Context context, String assetName, Charset charset) {
        byte[] data = readAssets(context, assetName);
        if (data == null) {
            return null;
        }
        return new String(data, charset);
    }

    /** Decodes an asset into a bitmap. */
    public static Bitmap readAssetsBitmap(Context context, String assetName) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        } catch (Exception e) {
            PrivateUtils.close(inputStream);
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** Extracts a zip archive into {@code destDir}. */
    public static boolean unzipFile(File zipFile, File destDir) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                ZipEntry entry = zipInputStream.getNextEntry();
                while (entry != null) {
                    File outFile = resolveZipEntry(destDir, entry);
                    if (!outFile.exists()) {
                        if (entry.isDirectory()) {
                            if (!outFile.mkdir()) {
                                throw new IOException("mkdir failed zipEntry=" + entry);
                            }
                        } else {
                            FileOutputStream outputStream = new FileOutputStream(outFile);
                            while (true) {
                                int read = zipInputStream.read(buffer);
                                if (read <= 0) {
                                    break;
                                }
                                outputStream.write(buffer, 0, read);
                            }
                            outputStream.close();
                        }
                    }
                    entry = zipInputStream.getNextEntry();
                }
                zipInputStream.closeEntry();
                zipInputStream.close();
                return true;
            } catch (Exception e) {
                zipInputStream.close();
                throw e;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            if (destDir.isDirectory()) {
                deleteDirectory(destDir);
            } else {
                deleteFile(destDir);
            }
            return false;
        }
    }

    /** Resolves a zip entry below {@code destDir}, rejecting path traversal. */
    private static File resolveZipEntry(File destDir, ZipEntry entry) throws IOException {
        File outFile = new File(destDir, entry.getName());
        String canonicalPath = destDir.getCanonicalPath();
        if (outFile.getCanonicalPath().startsWith(canonicalPath + File.separator)) {
            return outFile;
        }
        throw new IOException("Entry is outside of the target dir: " + entry.getName());
    }

    /** Compresses a path into a zip archive. */
    public static boolean zipFile(String srcPath, String zipPath) {
        return zipFile(new File(srcPath), new File(zipPath));
    }

    /** Compresses a path into a zip archive. */
    public static boolean zipFile(File srcFile, File zipFile) {
        return zipFile(srcFile, zipFile, true);
    }

    /** Compresses a path into a zip archive, optionally keeping the root folder. */
    public static boolean zipFile(File srcFile, File zipFile, boolean includeRoot) {
        if (srcFile == null) {
            LogUtil.e(TAG, "compressZip failed with src== null");
            return false;
        }
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            if (srcFile.isDirectory()) {
                for (File child : srcFile.listFiles()) {
                    if (includeRoot) {
                        addZipEntry(zipOutputStream, child, srcFile.getName() + File.separator);
                    } else {
                        addZipEntry(zipOutputStream, child, "");
                    }
                }
            } else {
                addZipEntry(zipOutputStream, srcFile, "");
            }
            zipOutputStream.close();
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Alias of {@link #zipFile(String, String)} kept for legacy call sites. */
    public static boolean compressZip(String srcPath, String zipPath) {
        return zipFile(new File(srcPath), new File(zipPath));
    }

    /** Recursively adds a file or directory to the archive. */
    private static void addZipEntry(ZipOutputStream zipOutputStream, File file, String parentPath) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                addZipEntry(zipOutputStream, child, parentPath + file.getName() + File.separator);
            }
            return;
        }
        FileInputStream inputStream = new FileInputStream(file);
        zipOutputStream.putNextEntry(new ZipEntry(parentPath + file.getName()));
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int read = inputStream.read(buffer);
            if (read != -1) {
                zipOutputStream.write(buffer, 0, read);
            } else {
                inputStream.close();
                return;
            }
        }
    }
    /** Detects the charset name of the file from its byte-order mark. */
    public static String getFileCharsetName(String path) {
        return getFileCharsetName(toFile(path));
    }

    /** Detects the charset name of the file from its byte-order mark. */
    public static String getFileCharsetName(File file) {
        BufferedInputStream inputStream = null;
        int head;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            head = (inputStream.read() << 8) + inputStream.read();
            PrivateUtils.close(inputStream);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(inputStream);
            head = 0;
        }
        if (head == 61371) {
            return "UTF-8";
        }
        if (head == 65279) {
            return "UTF-16BE";
        }
        return head == 65534 ? "Unicode" : "GBK";
    }

    /** Counts the lines of the file. */
    public static int getLineCount(String path) {
        return getLineCount(toFile(path));
    }

    /** Counts the lines of the file. */
    public static int getLineCount(File file) {
        BufferedInputStream inputStream = null;
        int count = 1;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer, 0, BUFFER_SIZE);
                if (read == -1) {
                    break;
                }
                for (int i = 0; i < read; i++) {
                    if (buffer[i] == '\n') {
                        count++;
                    }
                }
            }
            PrivateUtils.close(inputStream);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            PrivateUtils.close(inputStream);
            return count;
        }
        return count;
    }

    /** Human readable size of the file at {@code path}. */
    public static String getFileSizeString(String path) {
        return getFileSizeString(toFile(path));
    }

    /** Human readable size of the file. */
    public static String getFileSizeString(File file) {
        return !exists(file) ? "" : PrivateUtils.formatSize(file.length());
    }

    /** Parent directory of the file, or null. */
    public static String getParentPath(File file) {
        if (file == null) {
            return null;
        }
        return getParentPath(file.getPath());
    }

    /** Parent directory of the path, or an empty string. */
    public static String getParentPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        int lastSeparator = path.lastIndexOf(File.separator);
        return lastSeparator == -1 ? "" : path.substring(0, lastSeparator + 1);
    }

    /** File name (with extension) of the file, or null. */
    public static String getFileName(File file) {
        if (file == null) {
            return null;
        }
        return getFileName(file.getPath());
    }

    /** File name (with extension) of the path. */
    public static String getFileName(String path) {
        int lastSeparator;
        return (TextUtils.isEmpty(path) || (lastSeparator = path.lastIndexOf(File.separator)) == -1)
                ? path : path.substring(lastSeparator + 1);
    }

    /** Base name (without extension) of the file, or null. */
    public static String getBaseName(File file) {
        if (file == null) {
            return null;
        }
        return getBaseName(file.getPath());
    }

    /** Base name (without extension) of the path. */
    public static String getBaseName(String path) {
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        int lastDot = path.lastIndexOf(46);
        int lastSeparator = path.lastIndexOf(File.separator);
        if (lastSeparator == -1) {
            return lastDot == -1 ? path : path.substring(0, lastDot);
        }
        if (lastDot == -1 || lastSeparator > lastDot) {
            return path.substring(lastSeparator + 1);
        }
        return path.substring(lastSeparator + 1, lastDot);
    }

    /** Extension of the file, or null. */
    public static String getExtension(File file) {
        if (file == null) {
            return null;
        }
        return getExtension(file.getPath());
    }

    /** Extension of the path, or an empty string. */
    public static String getExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        int lastDot = path.lastIndexOf(46);
        return (lastDot == -1 || path.lastIndexOf(File.separator) >= lastDot) ? "" : path.substring(lastDot + 1);
    }

    /** Formats a byte count with the B/K/M/G suffix. */
    public static String formatSize(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (size < KB) {
            return decimalFormat.format(size) + "B";
        }
        if (size < MB) {
            return decimalFormat.format(size / 1024.0d) + "K";
        }
        if (size < GB) {
            return decimalFormat.format(size / 1048576.0d) + "M";
        }
        return decimalFormat.format(size / 1.073741824E9d) + "G";
    }
    /** Parses an XML document into a list of {@code tagName} elements. */
    public static List parseXmlList(InputStream inputStream, Class<?> clazz, String tagName) {
        XmlPullParser parser = Xml.newPullParser();
        ArrayList result = null;
        try {
            parser.setInput(inputStream, "UTF-8");
            Object current = null;
            for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    result = new ArrayList();
                } else if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (tagName.equals(name)) {
                        Object instance = clazz.newInstance();
                        int attributeCount = parser.getAttributeCount();
                        for (int i = 0; i < attributeCount; i++) {
                            setField(instance, parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                        current = instance;
                    } else if (current != null) {
                        setField(current, name, parser.nextText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (tagName.equals(parser.getName())) {
                        result.add(current);
                        current = null;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return result;
        }
        return result;
    }

    /** Parses an XML document into an instance of {@code clazz}. */
    public static Object parseXmlObject(InputStream inputStream, Class<?> clazz) {
        XmlPullParser parser = Xml.newPullParser();
        Object root = null;
        try {
            parser.setInput(inputStream, "UTF-8");
            Object current = null;
            String listTagName = null;
            ArrayList list = null;
            for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    root = clazz.newInstance();
                } else if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    Field[] fields = current == null ? root.getClass().getDeclaredFields() : current.getClass().getDeclaredFields();
                    if (current == null) {
                        int attributeCount = parser.getAttributeCount();
                        for (int i = 0; i < attributeCount; i++) {
                            setField(root, parser.getAttributeName(i), parser.getAttributeValue(i));
                        }
                    }
                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase(name)) {
                            if (!"java.util.List".equals(field.getType().getName())) {
                                if (current != null) {
                                    setField(current, name, parser.nextText());
                                    break;
                                }
                                setField(root, name, parser.nextText());
                                break;
                            }
                            Type genericType = field.getGenericType();
                            if (!(genericType instanceof ParameterizedType)) {
                                break;
                            }
                            Object item = ((Class) ((ParameterizedType) genericType).getActualTypeArguments()[0]).newInstance();
                            String fieldName = field.getName();
                            int attributeCount = parser.getAttributeCount();
                            for (int i = 0; i < attributeCount; i++) {
                                setField(item, parser.getAttributeName(i), parser.getAttributeValue(i));
                            }
                            if (list == null) {
                                list = new ArrayList();
                                field.setAccessible(true);
                                field.set(root, list);
                            }
                            listTagName = fieldName;
                            current = item;
                            break;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && current != null) {
                    if (listTagName.equalsIgnoreCase(parser.getName())) {
                        list.add(current);
                        current = null;
                        listTagName = null;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return root;
        }
        return root;
    }

    /** Assigns {@code value} to the field of {@code target} matching {@code name}. */
    private static void setField(Object target, String name, String value) {
        try {
            Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase(name)) {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        field.set(target, value);
                    } else if (type == Integer.TYPE) {
                        field.set(target, Integer.valueOf(Integer.parseInt(value)));
                    } else if (type == Float.TYPE) {
                        field.set(target, Float.valueOf(Float.parseFloat(value)));
                    } else if (type == Double.TYPE) {
                        field.set(target, Double.valueOf(Double.parseDouble(value)));
                    } else if (type == Long.TYPE) {
                        field.set(target, Long.valueOf(Long.parseLong(value)));
                    } else if (type == Short.TYPE) {
                        field.set(target, Short.valueOf(Short.parseShort(value)));
                    } else if (type == Boolean.TYPE) {
                        field.set(target, Boolean.valueOf(Boolean.parseBoolean(value)));
                    } else {
                        field.set(target, value);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    /** Reads an asset into a string. */
    public static String readAssetString(Context context, String assetName) {
        try {
            InputStream inputStream = context.getAssets().open(assetName);
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            String text = new String(data);
            Log.d(TAG, text);
            return text;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** Recursively copies an asset file or directory to {@code destPath}. */
    public static void copyAsset(Context context, String assetPath, String destPath) {
        try {
            String[] children = context.getAssets().list(assetPath);
            if (children.length > 0) {
                new File(destPath).mkdirs();
                for (String child : children) {
                    copyAsset(context, assetPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child,
                            destPath + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child);
                }
                return;
            }
            InputStream inputStream = context.getAssets().open(assetPath);
            FileOutputStream outputStream = new FileOutputStream(new File(destPath));
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int read = inputStream.read(buffer);
                if (read != -1) {
                    outputStream.write(buffer, 0, read);
                } else {
                    outputStream.flush();
                    inputStream.close();
                    outputStream.close();
                    return;
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }
    /** Resolves a content URI into a file-system path. */
    public static String getPathFromUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return null;
        }
        LogUtil.d(TAG, "Authority: " + uri.getAuthority() + ", Fragment: " + uri.getFragment() + ", Port: "
                + uri.getPort() + ", Query: " + uri.getQuery() + ", Scheme: " + uri.getScheme() + ", Host: "
                + uri.getHost() + ", Segments: " + uri.getPathSegments().toString());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String authority = uri.getAuthority();
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(authority)) {
                String type = documentId.split(":")[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    contentUri = null;
                }
                return queryPath(context, contentUri, null, null);
            }
            if ("com.android.providers.downloads.documents".equals(authority)) {
                return queryPath(context, ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId)), null, null);
            }
            if ("com.android.externalstorage.documents".equals(authority)) {
                String[] split = documentId.split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    return Environment.getExternalStorageDirectory() + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + split[1];
                }
                return Environment.getExternalStorageDirectory() + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + split[1];
            }
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            return uri.getPath();
        }
        if ("file".equals(scheme)) {
            return uri.getPath();
        }
        if ("content".equals(scheme)) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("_data");
                    if (columnIndex > -1) {
                        return cursor.getString(columnIndex);
                    }
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

    /** Queries the {@code _data} column of a content URI. */
    private static String queryPath(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /** Formats a byte count with three decimals (B/KB/MB/GB). */
    public static String formatFileSize(long size) {
        if (size < 0) {
            return "shouldn't be less than zero!";
        }
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

    /** Usable space of the file-system holding {@code path}. */
    public static long getUsableSpace(String path) {
        return getUsableSpace(new File(path));
    }

    /** Usable space of the file-system holding {@code file}. */
    public static long getUsableSpace(File file) {
        return file.getUsableSpace();
    }

    /** Recursively sums the size of a file or directory. */
    public static long getTotalSize(String path) {
        return getTotalSize(new File(path));
    }

    /** Recursively sums the size of a file or directory. */
    public static long getTotalSize(File file) {
        long total = 0;
        if (!file.exists()) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        }
        if (!file.isDirectory()) {
            return 0;
        }
        for (File child : file.listFiles()) {
            total += getTotalSize(child);
        }
        return total;
    }
}
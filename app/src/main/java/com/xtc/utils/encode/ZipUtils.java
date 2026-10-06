package com.xtc.utils.encode;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/** Zip archive helpers: compress, extract and list entries. */
public class ZipUtils {

    private ZipUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Compresses the given files into {@code zipPath}. */
    public static boolean zipFiles(Collection<File> files, String zipPath) throws IOException {
        return zipFiles(files, zipPath, null);
    }

    /** Compresses the given files into {@code zipPath} with a comment. */
    public static boolean zipFiles(Collection<File> files, String zipPath, String comment) throws IOException {
        return zipFiles(files, PrivateUtils.toFile(zipPath), comment);
    }

    /** Compresses the given files into {@code zipFile}. */
    public static boolean zipFiles(Collection<File> files, File zipFile) throws IOException {
        return zipFiles(files, zipFile, null);
    }

    /** Compresses the given files into {@code zipFile} with a comment. */
    public static boolean zipFiles(Collection<File> files, File zipFile, String comment) throws IOException {
        if (files == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            Iterator<File> iterator = files.iterator();
            while (iterator.hasNext()) {
                if (!zipEntry(iterator.next(), "", zipOutputStream, comment)) {
                    zipOutputStream.finish();
                    PrivateUtils.close(zipOutputStream);
                    return false;
                }
            }
            zipOutputStream.finish();
            PrivateUtils.close(zipOutputStream);
            return true;
        } catch (Exception e) {
            if (zipOutputStream != null) {
                zipOutputStream.finish();
                PrivateUtils.close(zipOutputStream);
            }
            throw e;
        }
    }

    /** Compresses a single path into {@code zipPath}. */
    public static boolean zipFile(String srcPath, String zipPath) throws IOException {
        return zipFile(srcPath, zipPath, null);
    }

    /** Compresses a single path into {@code zipPath} with a comment. */
    public static boolean zipFile(String srcPath, String zipPath, String comment) throws IOException {
        return zipFile(PrivateUtils.toFile(srcPath), PrivateUtils.toFile(zipPath), comment);
    }

    /** Compresses a single file or directory into {@code zipFile}. */
    public static boolean zipFile(File srcFile, File zipFile) throws IOException {
        return zipFile(srcFile, zipFile, null);
    }

    /** Compresses a single file or directory into {@code zipFile} with a comment. */
    public static boolean zipFile(File srcFile, File zipFile, String comment) throws IOException {
        if (srcFile == null || zipFile == null) {
            return false;
        }
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            boolean success = zipEntry(srcFile, "", zipOutputStream, comment);
            PrivateUtils.close(zipOutputStream);
            return success;
        } catch (Exception e) {
            PrivateUtils.close(zipOutputStream);
            throw e;
        }
    }

    /** Recursively writes a file or directory into the archive. */
    private static boolean zipEntry(File file, String parentPath, ZipOutputStream zipOutputStream, String comment) throws IOException {
        if (!file.exists()) {
            return true;
        }
        String entryPath = parentPath + (TextUtils.isEmpty(parentPath.trim()) ? "" : File.separator) + file.getName();
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null || children.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(entryPath + '/');
                if (!TextUtils.isEmpty(comment)) {
                    zipEntry.setComment(comment);
                }
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.closeEntry();
            } else {
                for (File child : children) {
                    if (!zipEntry(child, entryPath, zipOutputStream, comment)) {
                        return false;
                    }
                }
            }
        } else {
            BufferedInputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(file));
                ZipEntry zipEntry = new ZipEntry(entryPath);
                if (!TextUtils.isEmpty(comment)) {
                    zipEntry.setComment(comment);
                }
                zipOutputStream.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = inputStream.read(buffer, 0, 1024);
                    if (read == -1) {
                        break;
                    }
                    zipOutputStream.write(buffer, 0, read);
                }
                zipOutputStream.closeEntry();
                PrivateUtils.close(inputStream);
            } catch (Exception e) {
                PrivateUtils.close(inputStream);
                throw e;
            }
        }
        return true;
    }

    /** Extracts each archive into the same parent directory. */
    public static boolean unzipFiles(Collection<File> files, String destDir) throws IOException {
        return unzipFiles(files, PrivateUtils.toFile(destDir));
    }

    /** Extracts each archive into {@code destDir}. */
    public static boolean unzipFiles(Collection<File> files, File destDir) throws IOException {
        if (files == null || destDir == null) {
            return false;
        }
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            if (!unzipFile(iterator.next(), destDir)) {
                return false;
            }
        }
        return true;
    }

    /** Extracts one archive into {@code destDir}. */
    public static boolean unzipFile(String zipPath, String destDir) throws IOException {
        return unzipFile(PrivateUtils.toFile(zipPath), PrivateUtils.toFile(destDir));
    }

    /** Extracts one archive into {@code destDir}. */
    public static boolean unzipFile(File zipFile, File destDir) throws IOException {
        return unzipFileFiltered(zipFile, destDir, null) != null;
    }

    /** Extracts one archive into {@code destDir}, keeping only matching entries. */
    public static List<File> unzipFileFiltered(String zipPath, String destDir, String filter) throws IOException {
        return unzipFileFiltered(PrivateUtils.toFile(zipPath), PrivateUtils.toFile(destDir), filter);
    }

    /** Extracts one archive into {@code destDir}, keeping only matching entries. */
    public static List<File> unzipFileFiltered(File zipFile, File destDir, String filter) throws IOException {
        if (zipFile == null || destDir == null) {
            return null;
        }
        FileInputStream fileInputStream = new FileInputStream(zipFile);
        try {
            List<File> result = unzipInputStream(fileInputStream, destDir, filter);
            fileInputStream.close();
            return result;
        } catch (Exception e) {
            fileInputStream.close();
            throw e;
        }
    }

    /** Extracts the given stream into {@code destDir}, keeping matching entries. */
    public static List<File> unzipInputStream(InputStream inputStream, File destDir, String filter) throws IOException {
        ArrayList<File> extractedFiles = new ArrayList<>();
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        try {
            for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
                String name = zipEntry.getName();
                if ((TextUtils.isEmpty(filter) || PrivateUtils.getFileName(name).toLowerCase().contains(filter.toLowerCase()))
                        && !name.contains("..")) {
                    File outFile = new File(destDir, name);
                    extractedFiles.add(outFile);
                    if (zipEntry.isDirectory()) {
                        if (!PrivateUtils.makeDirsIfNeeded(outFile)) {
                            zipInputStream.close();
                            return null;
                        }
                    } else {
                        if (!PrivateUtils.createFile(outFile)) {
                            zipInputStream.close();
                            return null;
                        }
                        BufferedOutputStream outputStream = null;
                        try {
                            outputStream = new BufferedOutputStream(new FileOutputStream(outFile));
                            byte[] buffer = new byte[1024];
                            while (true) {
                                int read = zipInputStream.read(buffer);
                                if (read == -1) {
                                    break;
                                }
                                outputStream.write(buffer, 0, read);
                            }
                            PrivateUtils.close(outputStream);
                        } catch (Exception e) {
                            PrivateUtils.close(outputStream);
                            throw e;
                        }
                    }
                }
            }
            zipInputStream.close();
            return extractedFiles;
        } catch (Exception e) {
            zipInputStream.close();
            throw e;
        }
    }

    /** Lists the entry names of an archive. */
    public static List<String> getEntriesName(String zipPath) throws IOException {
        return getEntriesName(PrivateUtils.toFile(zipPath));
    }

    /** Lists the entry names of an archive. */
    public static List<String> getEntriesName(File zipFile) throws IOException {
        if (zipFile == null) {
            return null;
        }
        ArrayList<String> names = new ArrayList<>();
        Enumeration<?> entries = getEntries(zipFile);
        while (entries.hasMoreElements()) {
            names.add(((ZipEntry) entries.nextElement()).getName());
        }
        return names;
    }

    /** Lists the entry comments of an archive. */
    public static List<String> getEntriesComment(String zipPath) throws IOException {
        return getEntriesComment(PrivateUtils.toFile(zipPath));
    }

    /** Lists the entry comments of an archive. */
    public static List<String> getEntriesComment(File zipFile) throws IOException {
        if (zipFile == null) {
            return null;
        }
        ArrayList<String> comments = new ArrayList<>();
        Enumeration<?> entries = getEntries(zipFile);
        while (entries.hasMoreElements()) {
            comments.add(((ZipEntry) entries.nextElement()).getComment());
        }
        return comments;
    }

    /** Opens an archive and returns its entry enumeration. */
    public static Enumeration<?> getEntries(String zipPath) throws IOException {
        return getEntries(PrivateUtils.toFile(zipPath));
    }

    /** Opens an archive and returns its entry enumeration. */
    public static Enumeration<?> getEntries(File zipFile) throws IOException {
        if (zipFile == null) {
            return null;
        }
        return new ZipFile(zipFile).entries();
    }
}
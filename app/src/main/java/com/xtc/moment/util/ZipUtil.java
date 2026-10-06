package com.xtc.moment.util;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.system.model.I18n;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * zip 解压与压缩工具。
 */
public class ZipUtil {

    public static final String TAG = ZipUtil.class.getSimpleName();

    public static void unZipFolder(String sourceDir, String zipPath, String targetDir) throws Exception {
        long startTime = System.currentTimeMillis();
        LogUtil.i(TAG, "cost:" + (System.currentTimeMillis() - startTime) + I18n.Language.MALAY);
        for (File file : FileUtils.listFiles(sourceDir, false)) {
            LogUtil.e(TAG, "目录存在的文件：file:" + file.getPath());
        }
        if (!FileUtils.exists(zipPath)) {
            LogUtil.e(TAG, "un zip file is not exist");
            return;
        }
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath));
        while (true) {
            ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                zipInputStream.close();
                LogUtil.i(TAG, " UnZipFolder cost:" + (System.currentTimeMillis() - startTime) + "  ms");
                return;
            }
            String name = entry.getName();
            if (entry.isDirectory()) {
                File dir = new File(targetDir + File.separator + name.substring(0, name.length() - 1));
                dir.mkdirs();
                LogUtil.e(TAG, "Create the directory:" + dir.getPath());
            } else {
                LogUtil.e(TAG, targetDir + File.separator + name);
                File file = new File(targetDir + File.separator + name);
                if (!file.exists()) {
                    LogUtil.e(TAG, "Create the file:" + targetDir + name);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = zipInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                    outputStream.flush();
                }
                outputStream.close();
            }
        }
    }

    public static void ZipFolder(String folderPath, String zipPath) throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipPath));
        File folder = new File(folderPath);
        LogUtil.i(TAG, "---->" + folder.getParent() + "===" + folder.getAbsolutePath());
        ZipFiles(folder.getParent() + File.separator, folder.getName(), zipOutputStream);
        zipOutputStream.finish();
        zipOutputStream.close();
    }

    private static void ZipFiles(String parentPath, String fileName, ZipOutputStream zipOutputStream) throws Exception {
        LogUtil.i(TAG, "folderString:" + parentPath + "\nfileString:" + fileName + "\n==========================");
        if (zipOutputStream == null) {
            return;
        }
        File file = new File(parentPath + fileName);
        if (file.isFile()) {
            ZipEntry entry = new ZipEntry(fileName);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputStream.putNextEntry(entry);
            byte[] buffer = new byte[4096];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    zipOutputStream.closeEntry();
                    return;
                }
                zipOutputStream.write(buffer, 0, read);
            }
        } else {
            String[] children = file.list();
            if (children.length <= 0) {
                zipOutputStream.putNextEntry(new ZipEntry(fileName + File.separator));
                zipOutputStream.closeEntry();
            }
            for (String child : children) {
                ZipFiles(parentPath + fileName + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER, child,
                        zipOutputStream);
            }
        }
    }

    public static InputStream UpZip(String zipPath, String entryName) throws Exception {
        ZipFile zipFile = new ZipFile(zipPath);
        return zipFile.getInputStream(zipFile.getEntry(entryName));
    }

    public static List<File> getFileList(String zipPath, boolean includeDirectory, boolean includeFile) throws Exception {
        List<File> fileList = new ArrayList<>();
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath));
        while (true) {
            ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                zipInputStream.close();
                return fileList;
            }
            String name = entry.getName();
            if (entry.isDirectory()) {
                if (includeDirectory) {
                    fileList.add(new File(name.substring(0, name.length() - 1)));
                }
            } else if (includeFile) {
                fileList.add(new File(name));
            }
        }
    }
}
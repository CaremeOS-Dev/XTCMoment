package com.xtc.utils.storage;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/** External storage state, path and capacity helpers. */
public class SDCardUtils {

    private static final String SDCARD_UNAVAILABLE = "sdcard unable!";

    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** @return true when external storage is mounted. */
    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** Internal data directory path, or a hint when the card is missing. */
    public static String getDataDirectoryPath() {
        if (!isMounted()) {
            return SDCARD_UNAVAILABLE;
        }
        return Environment.getDataDirectory().getPath() + File.separator;
    }

    /** External storage root path, or a hint when the card is missing. */
    public static String getExternalStoragePath() {
        if (!isMounted()) {
            return SDCARD_UNAVAILABLE;
        }
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /** Locates the removable SD card by inspecting {@code /proc/mounts}. */
    public static String getSdCardPath() throws Exception {
        BufferedReader reader = null;
        if (!isMounted()) {
            return SDCARD_UNAVAILABLE;
        }
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/mounts");
            reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getInputStream())));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    PrivateUtils.close(reader);
                    break;
                }
                if (line.contains("sdcard") && line.contains(".android_secure")) {
                    String[] parts = line.split(" ");
                    if (parts.length >= 5) {
                        String path = parts[1].replace("/.android_secure", "") + File.separator;
                        PrivateUtils.close(reader);
                        return path;
                    }
                }
                if (process.waitFor() != 0 && process.exitValue() == 1) {
                    PrivateUtils.close(reader);
                    return " 命令执行失败";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            PrivateUtils.close(reader);
        }
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /** Formatted available bytes on the external storage. */
    public static String getAvailableSize() {
        if (!isMounted()) {
            return SDCARD_UNAVAILABLE;
        }
        StatFs statFs = new StatFs(getExternalStoragePath());
        return PrivateUtils.formatSize(statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
    }

    /** Detailed external storage statistics. */
    public static SDCardInfo getSDCardInfo() {
        SDCardInfo info = new SDCardInfo();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            info.isExist = true;
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            info.totalBlocks = statFs.getBlockCountLong();
            info.blockByteSize = statFs.getBlockSizeLong();
            info.availableBlocks = statFs.getAvailableBlocksLong();
            info.availableBytes = statFs.getAvailableBytes();
            info.freeBlocks = statFs.getFreeBlocksLong();
            info.freeBytes = statFs.getFreeBytes();
            info.totalBytes = statFs.getTotalBytes();
        }
        return info;
    }

    /** External storage statistics snapshot. */
    private static class SDCardInfo {
        boolean isExist;
        long totalBlocks;
        long freeBlocks;
        long availableBlocks;
        long blockByteSize;
        long totalBytes;
        long freeBytes;
        long availableBytes;

        private SDCardInfo() {
        }

        @Override
        public String toString() {
            return "SDCardInfo{isExist=" + this.isExist + ", totalBlocks=" + this.totalBlocks
                    + ", freeBlocks=" + this.freeBlocks + ", availableBlocks=" + this.availableBlocks
                    + ", blockByteSize=" + this.blockByteSize + ", totalBytes=" + this.totalBytes
                    + ", freeBytes=" + this.freeBytes + ", availableBytes=" + this.availableBytes + '}';
        }
    }
}
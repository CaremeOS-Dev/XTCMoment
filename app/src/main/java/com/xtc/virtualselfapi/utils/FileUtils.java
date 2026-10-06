package com.xtc.virtualselfapi.utils;

import android.os.Environment;

import java.io.File;

/**
 * 虚拟形象资源路径工具。
 */
public class FileUtils {

    private static final String TAG = "Virtual_Self_Api_FileUtils";

    private static final String spineResourceRootPath =
            Environment.getExternalStorageDirectory() + File.separator + "xtcSpine" + File.separator;
    private static final String virtualSelfResourceRootPath =
            spineResourceRootPath + "virtualSelf" + File.separator;

    public static String getCharacterDynamicResourcePath(String resourceName) {
        return virtualSelfResourceRootPath + resourceName + File.separator;
    }
}
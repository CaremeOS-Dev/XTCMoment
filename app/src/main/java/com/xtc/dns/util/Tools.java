package com.xtc.dns.util;

import com.xtc.dns.LogTag;
import com.xtc.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * DNS 配置属性文件读写工具。
 */
public class Tools {

    private static final String TAG = LogTag.tag("Tools");
    private static final String CONFIG_PATH = "/mnt/sdcard/xtc/ibwatch/common/httpdnsinfo.properties";

    /** 读取配置。 */
    public static Properties readProperties() {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(new File(CONFIG_PATH))) {
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            LogUtil.w(TAG, "loadConfig", e);
            return null;
        }
    }

    /** 写入配置。 */
    public static boolean writeProperties(Properties properties) {
        try {
            File file = new File(CONFIG_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                properties.store(outputStream, "");
                return true;
            }
        } catch (Exception e) {
            LogUtil.w(TAG, "saveConfig", e);
            return false;
        }
    }

    /** 删除配置文件。 */
    public static boolean deleteConfig() {
        File file = new File(CONFIG_PATH);
        return file.exists() && file.delete();
    }
}
package com.xtc.system.account;

import android.os.Environment;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/** Persists the registration info and RSA public key on the SD card. */
class SDCardUtil {

    private static final String PUBLIC_KEY_FILE_NAME = "public_key.txt";
    private static final String REGIST_INFO_FILE_NAME = "regist_info.txt";
    private static final String SD_CARD_DIR = Environment.getExternalStorageDirectory().getPath() + "/sync/";
    private static final String TAG = "SDCardUtil";

    SDCardUtil() {
    }

    /** Writes the registration id/token pair, replacing an existing one. */
    public static boolean saveRegistInfo(long registId, String registToken) {
        File dir = new File(SD_CARD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getAbsolutePath() + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + REGIST_INFO_FILE_NAME);
        if (file.exists()) {
            if (isRegisted(registId, registToken)) {
                return false;
            }
            if (file.delete()) {
                LogUtil.i(TAG, "delete " + file.getName() + " success.");
            } else {
                LogUtil.e(TAG, "delete " + file.getName() + " fail.");
            }
        }
        JSONObject json = new JSONObject();
        try {
            json.put("registId", registId);
            json.put("registToken", registToken);
        } catch (JSONException e) {
            LogUtil.e(TAG, e);
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.toString().getBytes());
            LogUtil.i(TAG, "save data to file success,data:" + json.toString());
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, e);
            closeQuietly(outputStream);
            return false;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            closeQuietly(outputStream);
            return false;
        }
    }

    /** Reads the registration id, or 0 when absent. */
    public static long getRegistId() {
        JSONObject json = readRegistInfo();
        if (json == null) {
            return 0L;
        }
        try {
            return json.getLong("registId");
        } catch (JSONException e) {
            LogUtil.e(TAG, e);
            return 0L;
        }
    }

    /** Reads the registration token, or null when absent. */
    public static String getRegistToken() {
        JSONObject json = readRegistInfo();
        if (json == null) {
            return null;
        }
        try {
            return json.getString("registToken");
        } catch (JSONException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    private static JSONObject readRegistInfo() {
        File file = new File(SD_CARD_DIR + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + REGIST_INFO_FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            return new JSONObject(outputStream.toString());
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            closeQuietly(inputStream);
            return null;
        }
    }

    /** @return true when the stored registration matches the given values. */
    public static boolean isRegisted(long registId, String registToken) {
        return registId == getRegistId() && TextUtils.equals(registToken, getRegistToken());
    }

    /** @return true when a registration is stored. */
    public static boolean isRegisted() {
        return getRegistId() != 0;
    }

    /** Deletes the registration file. */
    public static boolean deleteRegistInfo() {
        return deleteFile(SD_CARD_DIR + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + REGIST_INFO_FILE_NAME);
    }

    /** Deletes the public key file. */
    public static boolean deletePublicKey() {
        return deleteFile(SD_CARD_DIR + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + PUBLIC_KEY_FILE_NAME);
    }

    /** Deletes the file at {@code path}. */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return !file.exists() || file.delete();
    }

    /** Writes the public key bytes. */
    public static boolean savePublicKey(byte[] data) {
        File dir = new File(SD_CARD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getAbsolutePath() + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + PUBLIC_KEY_FILE_NAME);
        if (file.exists() && !file.delete()) {
            return false;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            closeQuietly(outputStream);
            return false;
        }
    }

    /** Reads the public key bytes, or null. */
    public static byte[] getPublicKey() {
        File file = new File(SD_CARD_DIR + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + PUBLIC_KEY_FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            closeQuietly(inputStream);
            return null;
        }
    }

    /** @return true when a public key is stored. */
    public static boolean hasPublicKey() {
        return new File(SD_CARD_DIR + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + PUBLIC_KEY_FILE_NAME).exists();
    }

    private static void closeQuietly(java.io.Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
                // ignored
            }
        }
    }
}
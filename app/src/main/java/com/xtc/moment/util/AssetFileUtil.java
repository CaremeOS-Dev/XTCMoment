package com.xtc.moment.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 读取 assets 下的文本配置文件。
 */
public class AssetFileUtil {

    private static final String TAG = "AssetFileUtil";

    public static String decodeConfigFile(Resources resources, String assetName) throws Throwable {
        if (TextUtils.isEmpty(assetName)) {
            return "";
        }
        AssetManager assets = resources.getAssets();
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(assets.open(assetName));
            reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (Exception e) {
            Log.e(TAG, "open file error: ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "close bufferedReader error: ", e);
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                    Log.e(TAG, "close inputStreamReader error: ", e);
                }
            }
        }
        return content.toString();
    }
}
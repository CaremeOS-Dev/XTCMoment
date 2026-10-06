package com.xtc.moment.share.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * 分享模块的通用工具方法：token 生成与分享图片落盘。
 */
public class ShareUtils {

    /** 分享到微聊的模块开关 id。 */
    public static final int CHAT_MODULE_SWITCH = 114;
    /** AES 加解密口令。 */
    public static final String PASSWORD = "XTC_SHARE_181017";
    /** 日志统一前缀。 */
    public static final String LOG = "Share_Msg_";

    private static final String TAG = LOG + ShareUtils.class.getSimpleName();

    private ShareUtils() {
    }

    /** 生成一个新的分享 token，MD5 失败时回退为原始字符串。 */
    public static String createToken() {
        String rawToken = "XTC_SHARE_" + System.currentTimeMillis();
        String md5Token = encodeByMD5(rawToken);
        return md5Token == null ? rawToken : md5Token;
    }

    /** 计算字符串的 MD5 十六进制摘要。 */
    private static String encodeByMD5(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(text.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder hexBuilder = new StringBuilder();
            for (byte value : digest) {
                String hex = Integer.toHexString(value & 0xFF);
                if (hex.length() == 1) {
                    hexBuilder.append("0");
                }
                hexBuilder.append(hex);
            }
            return hexBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 把图片字节数组保存为分享目录下的 jpg 文件，返回保存路径；失败时返回 null。
     */
    public static String saveBitmapToSdcard(byte[] imageData) {
        File shareFolder = new File(FileManager.getShareFolderPath());
        if (!shareFolder.exists()) {
            boolean created = shareFolder.mkdirs();
            LogUtil.d(TAG, "create share folder = " + created);
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        LogUtil.d(TAG, "new pic file name = " + fileName);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        FileOutputStream outputStream = null;
        try {
            File imageFile = new File(FileManager.getShareFolderPath() + fileName);
            if (!imageFile.exists()) {
                boolean created = imageFile.createNewFile();
                LogUtil.d(TAG, "create share image file = " + created);
            }
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return FileManager.getShareFolderPath() + fileName;
        } catch (IOException e) {
            LogUtil.e(TAG, "save bitmap to sdcard error = " + e);
            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException flushError) {
                    flushError.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException closeError) {
                    closeError.printStackTrace();
                }
            }
            return null;
        }
    }
}
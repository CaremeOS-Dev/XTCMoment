package com.xtc.web.core.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.xtc.web.core.CoreConstants;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqObtainImage;
import com.xtc.web.core.data.req.ReqSaveImage;
import com.xtc.web.core.data.resp.RespImage;
import com.xtc.web.core.utils.WebUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/** 本地图片读写：按目标尺寸采样压缩后回传 base64，以及把 base64 图片写入本地文件。 */
public class BitmapManager {

    private static final String TAG = CoreConstants.TAG + BitmapManager.class.getSimpleName();

    /** 采样压缩本地图片并回传 base64（WEBP，质量 50）。 */
    public void getLocalImage(Context context, ReqObtainImage reqObtainImage,
            CompletionHandler<RespImage> completionHandler) {
        if (!new File(reqObtainImage.getPath()).exists()) {
            RespImage response = new RespImage();
            response.setCode(RespImage.Code.NOT_EXIST);
            completionHandler.complete(response);
            return;
        }
        int targetWidth = reqObtainImage.getWidth();
        int targetHeight = reqObtainImage.getHeight();
        if (targetWidth == 0 || targetHeight == 0) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            targetHeight = displayMetrics.heightPixels;
            targetWidth = displayMetrics.widthPixels;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(reqObtainImage.getPath(), options);
        int sourceHeight = options.outHeight;
        int sourceWidth = options.outWidth;
        Log.d(TAG, "src width = " + sourceWidth + " src height = " + sourceHeight);
        int maxWidth = (int) (targetWidth * 1.5d);
        int maxHeight = (int) (targetHeight * 1.5d);
        int sampleSize = 1;
        while (maxWidth < sourceWidth || maxHeight < sourceHeight) {
            sourceHeight /= 2;
            sourceWidth /= 2;
            sampleSize *= 2;
        }
        Log.d(TAG, "bitmap sample size = " + sampleSize);
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(reqObtainImage.getPath(), options);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        RespImage response = new RespImage();
        response.setCode(RespImage.Code.SUCCESS);
        response.setData(Base64.encodeToString(imageBytes, Base64.NO_WRAP));
        completionHandler.complete(response);
    }

    /** 把 base64 图片保存到本地路径，未指定时使用相机目录。 */
    public void saveLocalImage(Context context, ReqSaveImage reqSaveImage,
            CompletionHandler<Boolean> completionHandler) throws Throwable {
        String directoryPath = reqSaveImage.getPath();
        if (directoryPath == null) {
            directoryPath = WebUtils.getSdcardPath() + CoreConstants.BitmapConstant.CAMERA_PATH;
        }
        if (reqSaveImage.getName() == null) {
            reqSaveImage.setName(System.currentTimeMillis() + ".jpg");
        }
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File imageFile = new File(directoryPath, reqSaveImage.getName());
        if (imageFile.exists()) {
            imageFile.delete();
        }
        byte[] imageBytes = Base64.decode(reqSaveImage.getImage(), Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream);
            outputStream.flush();
            completionHandler.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "save image error = " + e);
            completionHandler.complete(false);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
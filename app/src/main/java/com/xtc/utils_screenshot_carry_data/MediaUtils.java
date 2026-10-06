package com.xtc.utils_screenshot_carry_data;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Base64;

import com.bumptech.glide.load.Key;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.XmlBox;
import com.google.gson.Gson;
import com.xtc.log.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Reads and writes the {@link MediaExifBean} payload that travels with a shared media
 * file. Videos carry the JSON in an ISO {@link XmlBox}, photos in the EXIF maker note
 * and GIFs in a trailing {@code <GifDetail>} block.
 */
public class MediaUtils {

    /** Source type identifiers carried in {@link MediaExifBean#getSourceType()}. */
    public static final int SOURCE_TYPE_1 = 1;
    public static final int SOURCE_TYPE_2 = 2;
    public static final int SOURCE_TYPE_3 = 3;
    public static final int SOURCE_TYPE_4 = 4;
    public static final int SOURCE_TYPE_5 = 5;
    public static final int SOURCE_TYPE_6 = 6;
    public static final int SOURCE_TYPE_7 = 7;
    public static final int SOURCE_TYPE_8 = 8;
    public static final int SOURCE_TYPE_9 = 9;
    public static final int SOURCE_TYPE_10 = 10;
    public static final int SOURCE_TYPE_11 = 11;

    private static final String TAG = "MediaUtils";

    private static final String MIME_GIF = "image/gif";
    private static final String MIME_JPEG = "image/jpeg";
    private static final String MIME_WEBP = "image/webp";

    private static final String GIF_DETAIL_START = "<GifDetail>";
    private static final String GIF_DETAIL_END = "</GifDetail>";

    /** Bytes read from the tail of a GIF when looking for the detail block. */
    private static final int GIF_TAIL_BYTES = 1024;

    /** Writes {@code mediaExifBean} into the media file at {@code localPath}. */
    public static void setMediaExternalInfo(String localPath, MediaExifBean mediaExifBean) {
        if (!isFilePathValid(localPath)) {
            LogUtil.i(TAG, "setMediaExternalInfo, localPath is unvalid");
        } else {
            writeMediaExternalInfo(localPath, decodeFileOptions(localPath), mediaExifBean);
        }
    }

    /** Reads the {@link MediaExifBean} carried by the media file at {@code localPath}. */
    public static MediaExifBean getMediaExternalInfo(String localPath) {
        String mediaExifJson;
        if (!isFilePathValid(localPath)) {
            LogUtil.i(TAG, "setMediaExternalInfo, localPath is unvalid");
            return null;
        }
        BitmapFactory.Options options = decodeFileOptions(localPath);
        LogUtil.i(TAG, "fileOption outMimeType = " + options.outMimeType);
        if (TextUtils.isEmpty(options.outMimeType)) {
            mediaExifJson = getVideoMetadata(localPath);
        } else if (Objects.equals(options.outMimeType, MIME_JPEG)) {
            mediaExifJson = getPhotoNoteData(localPath);
        } else {
            mediaExifJson = Objects.equals(options.outMimeType, MIME_GIF) ? getGifNoteValue(localPath) : null;
        }
        LogUtil.i(TAG, "mediaExifJson = " + mediaExifJson);
        try {
            return new Gson().fromJson(mediaExifJson, MediaExifBean.class);
        } catch (Exception ignored) {
            LogUtil.e(TAG, "mediaExifJson from json error");
            return null;
        }
    }

    /** Decodes the bounds of the media file to learn its mime type. */
    private static BitmapFactory.Options decodeFileOptions(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options;
    }

    /** Serializes the bean and writes it into the media file on a background thread. */
    private static void writeMediaExternalInfo(final String filePath, final BitmapFactory.Options options, final MediaExifBean mediaExifBean) {
        ScreenShotHandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                String json = new Gson().toJson(mediaExifBean);
                if (json == null) {
                    LogUtil.i(TAG, "chooseMediaPattern, mediaInfo is null");
                    return;
                }
                LogUtil.i(TAG, "outMimeType = " + options.outMimeType + " mediaInfo = " + json);
                if (TextUtils.isEmpty(options.outMimeType)) {
                    writeVideoMetadata(filePath, json);
                    return;
                }
                if (Objects.equals(options.outMimeType, MIME_GIF)) {
                    appendGifNoteValue(filePath, json);
                } else if (Objects.equals(options.outMimeType, MIME_JPEG)) {
                    savePhotoNoteValue(filePath, json);
                } else {
                    LogUtil.i(TAG, "chooseMediaPattern end");
                }
            }
        });
    }

    /** Writes {@code text} into the ISO {@link XmlBox} of the video at {@code videoFilePath}. */
    private static void writeVideoMetadata(String videoFilePath, String text) {
        LogUtil.i(TAG, "writeVideoMetadata, videoFilePath = " + videoFilePath + " text = " + text);
        if (TextUtils.isEmpty(text)) {
            LogUtil.i(TAG, "writeVideoMetadata, text is empty");
            return;
        }
        if (!isFilePathValid(videoFilePath)) {
            LogUtil.i(TAG, "videoFilePath is unvalid");
            return;
        }
        try {
            File file = new File(videoFilePath);
            IsoFile isoFile = new IsoFile(file.getAbsolutePath());
            List<XmlBox> xmlBoxes = isoFile.getBoxes(XmlBox.class);
            XmlBox xmlBox = isEmpty(xmlBoxes) ? new XmlBox() : xmlBoxes.get(0);
            xmlBox.setXml(text);
            List<Box> finalBoxes = new ArrayList<>();
            finalBoxes.add(xmlBox);
            LogUtil.i(TAG, "finalBoxes = " + finalBoxes);
            isoFile.setBoxes(finalBoxes);
            FileOutputStream outputStream = new FileOutputStream(file, true);
            try {
                new FileInputStream(file);
                isoFile.getBox(outputStream.getChannel());
            } finally {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    LogUtil.e(TAG, "close videoFileOutputStream error", e);
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "writeVideoMetadata error", e);
        }
    }

    /** Reads the ISO {@link XmlBox} payload of the video at {@code videoFilePath}. */
    private static String getVideoMetadata(String videoFilePath) {
        LogUtil.i(TAG, "getVideoMetadata,videoFilePath = " + videoFilePath);
        String xmlData = null;
        if (!isFilePathValid(videoFilePath)) {
            LogUtil.i(TAG, "videoFilePath is unvalid");
            return null;
        }
        try {
            List<XmlBox> xmlBoxes = new IsoFile(new File(videoFilePath).getAbsolutePath()).getBoxes(XmlBox.class);
            if (isEmpty(xmlBoxes)) {
                return null;
            }
            XmlBox xmlBox = xmlBoxes.get(0);
            if (xmlBox == null) {
                return null;
            }
            xmlData = xmlBox.getXml();
            LogUtil.i(TAG, "xml data = " + xmlData);
            return xmlData;
        } catch (Exception e) {
            LogUtil.i(TAG, "getVideoMetadata error ", e);
            return xmlData;
        }
    }

    /** Stores the base64 payload in the EXIF maker note of the photo. */
    private static void savePhotoNoteValue(String imageFilePath, String value) {
        LogUtil.d(TAG, "saveNoteValue mImageFilePath=" + imageFilePath + " value = " + value);
        if (TextUtils.isEmpty(value)) {
            LogUtil.i(TAG, "savePhotoNoteValue, value is empty");
            return;
        }
        if (!isFilePathValid(imageFilePath)) {
            LogUtil.i(TAG, "savePhotoNoteValue, imageFilePath is unvalid");
            return;
        }
        try {
            LogUtil.i(TAG, "START");
            ExifInterface exifInterface = new ExifInterface(imageFilePath);
            String encoded = Base64.encodeToString(value.getBytes(StandardCharsets.UTF_8), 0);
            LogUtil.i(TAG, "encode = " + encoded);
            exifInterface.setAttribute(android.support.media.ExifInterface.TAG_MAKER_NOTE, encoded);
            exifInterface.saveAttributes();
        } catch (Exception e) {
            LogUtil.e(TAG, "saveNoteValue IOException ", e);
        }
    }

    /** Reads the base64 payload from the EXIF maker note of the photo. */
    private static String getPhotoNoteData(String imageFilePath) {
        LogUtil.i(TAG, "getPhotoNoteData, imageFilePath = " + imageFilePath);
        if (!isFilePathValid(imageFilePath)) {
            LogUtil.i(TAG, "getPhotoNoteData, imageFilePath is unvalid");
            return "";
        }
        try {
            String attribute = new ExifInterface(imageFilePath).getAttribute(android.support.media.ExifInterface.TAG_MAKER_NOTE);
            LogUtil.i(TAG, "attribute = " + attribute);
            String photoNote = new String(Base64.decode(attribute, 0), Key.STRING_CHARSET_NAME);
            LogUtil.i(TAG, "photoNote = " + photoNote);
            return photoNote;
        } catch (Exception e) {
            LogUtil.e(TAG, "saveNoteValue IOException= ", e);
            return "";
        }
    }

    /** Appends the detail block to the end of the GIF when it is not present yet. */
    private static void appendGifNoteValue(String gifFilePath, String text) {
        LogUtil.i(TAG, "appendGifNoteValue, gifFilePath = " + gifFilePath + " text = " + text);
        if (TextUtils.isEmpty(text)) {
            LogUtil.i(TAG, "appendGifNoteValue, text is empty");
            return;
        }
        if (!isFilePathValid(gifFilePath)) {
            LogUtil.i(TAG, "appendGifNoteValue, gifFilePath is unvalid");
            return;
        }
        File gifFile = new File(gifFilePath);
        if (!TextUtils.isEmpty(getGifString(gifFile))) {
            LogUtil.i(TAG, " gifFile already write external info ");
            return;
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(gifFile, true);
            outputStream.write(GIF_DETAIL_START.getBytes(StandardCharsets.UTF_8));
            outputStream.write(text.getBytes(StandardCharsets.UTF_8));
            outputStream.write(GIF_DETAIL_END.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "appendString2Gif error ", e);
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception closeError) {
                    closeError.printStackTrace();
                }
            }
        }
    }

    /** Reads the trailing detail block of the GIF. */
    private static String getGifNoteValue(String gifFilePath) {
        LogUtil.i(TAG, "getGifNoteValue, gifFilePath = " + gifFilePath);
        if (!isFilePathValid(gifFilePath)) {
            LogUtil.i(TAG, "getGifNoteValue, gifFilePath is unvalid");
            return "";
        }
        return getGifString(new File(gifFilePath));
    }

    /** Scans the tail of the GIF for the {@code <GifDetail>} payload. */
    private static String getGifString(File gifFile) {
        RandomAccessFile randomAccessFile = null;
        try {
            long length = gifFile.length();
            byte[] buffer = new byte[GIF_TAIL_BYTES];
            randomAccessFile = new RandomAccessFile(gifFile, "rw");
            randomAccessFile.seek(length - PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
            randomAccessFile.read(buffer);
            String content = new String(buffer);
            LogUtil.i(TAG, "length = " + content.length() + "resultString = " + content);
            if (content.contains(GIF_DETAIL_START) && content.contains(GIF_DETAIL_END)) {
                int startIndex = content.indexOf(GIF_DETAIL_START) + 11;
                int endIndex = content.indexOf(GIF_DETAIL_END);
                if (startIndex < content.length() && endIndex < content.length()) {
                    String detail = content.substring(startIndex, endIndex);
                    LogUtil.i(TAG, "substring = " + detail);
                    try {
                        randomAccessFile.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return detail;
                }
                LogUtil.i(TAG, "index not valid");
                try {
                    randomAccessFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }
            LogUtil.i(TAG, "getGifString not write value");
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        } catch (Exception e) {
            LogUtil.e(TAG, "getGifString error ", e);
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (Exception closeError) {
                    closeError.printStackTrace();
                }
            }
            return "";
        }
    }

    /** @return true when {@code filePath} exists and is readable and writable. */
    private static boolean isFilePathValid(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            LogUtil.i(TAG, "isFilePathValid, filePath is null");
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            LogUtil.i(TAG, " filePath not exists " + filePath);
            return false;
        }
        if (!file.canWrite()) {
            LogUtil.i(TAG, " filePath not canWrite " + filePath);
            return false;
        }
        if (file.canRead()) {
            return true;
        }
        LogUtil.i(TAG, " filePath not canRead " + filePath);
        return false;
    }

    /** @return true when the collection is null or empty. */
    private static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}
package com.xtc.utils.encode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** GZIP compression helpers returning {@code null} for empty input. */
public class GzipUtil {

    private GzipUtil() {
    }

    /** Compresses {@code text} using the given charset; null for empty input. */
    public static byte[] compress(String text, String charsetName) {
        if (text == null || text.length() == 0) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
            try {
                gzipOutputStream.write(text.getBytes(charsetName));
                gzipOutputStream.close();
                byte[] result = outputStream.toByteArray();
                outputStream.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                byte[] result = outputStream.toByteArray();
                outputStream.close();
                gzipOutputStream.close();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return outputStream.toByteArray();
        }
    }

    /** Decompresses gzip data; null for empty input. */
    public static byte[] decompress(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            try {
                byte[] buffer = new byte[256];
                while (true) {
                    int read = gzipInputStream.read(buffer);
                    if (read < 0) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                }
                gzipInputStream.close();
                byte[] result = outputStream.toByteArray();
                outputStream.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                byte[] result = outputStream.toByteArray();
                outputStream.close();
                try {
                    gzipInputStream.close();
                } catch (Exception ignored) {
                    // ignored, mirroring the original behaviour
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return outputStream.toByteArray();
        }
    }
}
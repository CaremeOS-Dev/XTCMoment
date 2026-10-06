package com.xtc.moment.util;

import com.xtc.moment.module.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.BitSet;

/**
 * 文本文件编码识别与转码工具。
 */
public class EncodeUtil {

    private static final int BYTE_SIZE = 8;

    public static String CODE_GBK = "GBK";
    public static String CODE_UTF8 = "UTF-8";
    public static String CODE_UTF8_BOM = "UTF-8_BOM";

    public static String getEncode(String filePath, boolean returnUtf8ForBom) throws Exception {
        return getEncode(new BufferedInputStream(new FileInputStream(filePath)), returnUtf8ForBom);
    }

    public static String getEncode(BufferedInputStream inputStream, boolean returnUtf8ForBom) throws Exception {
        byte[] head = new byte[3];
        inputStream.read(head);
        if (head[0] == -1 && head[1] == -2) {
            return "UTF-16";
        }
        if (head[0] == -2 && head[1] == -1) {
            return "Unicode";
        }
        if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
            return returnUtf8ForBom ? CODE_UTF8 : CODE_UTF8_BOM;
        }
        return isUTF8(inputStream) ? CODE_UTF8 : CODE_GBK;
    }

    private static boolean isUTF8(BufferedInputStream inputStream) throws Exception {
        inputStream.mark(0);
        inputStream.reset();
        int read = inputStream.read();
        do {
            BitSet bits = convert2BitSet(read);
            if (bits.get(0) && !checkMultiByte(inputStream, bits)) {
                return false;
            }
            read = inputStream.read();
        } while (read != -1);
        return true;
    }

    private static boolean checkMultiByte(BufferedInputStream inputStream, BitSet bits) throws Exception {
        byte[] followBytes = new byte[getCountOfSequential(bits) - 1];
        inputStream.read(followBytes);
        for (byte followByte : followBytes) {
            if (!checkUtf8Byte(followByte)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkUtf8Byte(byte value) throws Exception {
        BitSet bits = convert2BitSet(value);
        return bits.get(0) && !bits.get(1);
    }

    private static int getCountOfSequential(BitSet bits) {
        int count = 0;
        for (int index = 0; index < BYTE_SIZE && bits.get(index); index++) {
            count++;
        }
        return count;
    }

    private static BitSet convert2BitSet(int value) {
        BitSet bits = new BitSet(BYTE_SIZE);
        for (int index = 0; index < BYTE_SIZE; index++) {
            if (((value >> ((BYTE_SIZE - index) - 1)) & 1) == 1) {
                bits.set(index);
            }
        }
        return bits;
    }

    public static void convert(String sourcePath, String sourceCharset, String targetPath, String targetCharset)
            throws Exception {
        StringBuffer content = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourcePath), sourceCharset));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            content.append(line);
            content.append(System.getProperty("line.separator"));
        }
        String normalizedPath = targetPath.replace("\\", Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
        File parent = new File(normalizedPath.substring(0,
                normalizedPath.lastIndexOf(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER)));
        if (!parent.exists()) {
            parent.mkdirs();
        }
        new OutputStreamWriter(new FileOutputStream(normalizedPath), targetCharset).write(content.toString());
    }
}
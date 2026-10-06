package com.xtc.utils.encode;

/**
 * Standalone Base64 codec used by the network layer.
 *
 * <p>This is a faithful re-implementation of the original hand-rolled codec:
 * whitespace is ignored while decoding, invalid characters and malformed
 * padding produce {@code null} rather than throwing.
 */
public final class Base64Util {

    private static final int MAX_ASCII = 128;
    private static final int ALPHABET_SIZE = 64;
    private static final char PAD = '=';

    /** Reverse lookup table: ASCII value to 6-bit group, or -1. */
    private static final byte[] DECODE_TABLE = new byte[MAX_ASCII];
    /** Forward lookup table: 6-bit group to ASCII character. */
    private static final char[] ENCODE_TABLE = new char[ALPHABET_SIZE];

    static {
        for (int i = 0; i < MAX_ASCII; i++) {
            DECODE_TABLE[i] = -1;
        }
        for (int c = 'Z'; c >= 'A'; c--) {
            DECODE_TABLE[c] = (byte) (c - 'A');
        }
        for (int c = 'z'; c >= 'a'; c--) {
            DECODE_TABLE[c] = (byte) ((c - 'a') + 26);
        }
        for (int c = '9'; c >= '0'; c--) {
            DECODE_TABLE[c] = (byte) ((c - '0') + 52);
        }
        DECODE_TABLE['+'] = 62;
        DECODE_TABLE['/'] = 63;

        for (int i = 0; i <= 25; i++) {
            ENCODE_TABLE[i] = (char) (i + 'A');
        }
        int index = 0;
        for (int i = 26; i <= 51; i++) {
            ENCODE_TABLE[i] = (char) (index + 'a');
            index++;
        }
        index = 0;
        for (int i = 52; i <= 61; i++) {
            ENCODE_TABLE[i] = (char) (index + '0');
            index++;
        }
        ENCODE_TABLE[62] = '+';
        ENCODE_TABLE[63] = '/';
    }

    private Base64Util() {
    }

    /** Returns {@code true} for the whitespace characters skipped while decoding. */
    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\r' || c == '\n' || c == '\t';
    }

    /** Returns {@code true} for the base64 padding character. */
    private static boolean isPadding(char c) {
        return c == PAD;
    }

    /** Returns {@code true} when {@code c} maps to a 6-bit group. */
    private static boolean isDecodable(char c) {
        return c < MAX_ASCII && DECODE_TABLE[c] != -1;
    }

    /** Encodes a byte array, or null when {@code data} is null. */
    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        int bitLength = data.length * 8;
        if (bitLength == 0) {
            return "";
        }
        int remainderBits = bitLength % 24;
        int fullGroups = bitLength / 24;
        char[] out = new char[(remainderBits != 0 ? fullGroups + 1 : fullGroups) * 4];
        int group = 0;
        int outIndex = 0;
        int inIndex = 0;
        while (group < fullGroups) {
            int i0 = inIndex + 1;
            byte b0 = data[inIndex];
            int i1 = i0 + 1;
            byte b1 = data[i0];
            int i2 = i1 + 1;
            byte b2 = data[i1];
            byte lowB1 = (byte) (b1 & 15);
            byte lowB0 = (byte) (b0 & 3);
            int highB0 = b0 & 0x80;
            int g0 = b0 >> 2;
            if (highB0 != 0) {
                g0 ^= 192;
            }
            byte group0 = (byte) g0;
            int highB1 = b1 & 0x80;
            int g1 = b1 >> 4;
            if (highB1 != 0) {
                g1 ^= 240;
            }
            byte group1 = (byte) g1;
            int g2 = (b2 & 0x80) == 0 ? b2 >> 6 : (b2 >> 6) ^ 192;
            int o0 = outIndex + 1;
            out[outIndex] = ENCODE_TABLE[group0];
            int o1 = o0 + 1;
            out[o0] = ENCODE_TABLE[(lowB0 << 4) | group1];
            int o2 = o1 + 1;
            out[o1] = ENCODE_TABLE[(lowB1 << 2) | ((byte) g2)];
            out[o2] = ENCODE_TABLE[b2 & 63];
            group++;
            outIndex = o2 + 1;
            inIndex = i2;
        }
        if (remainderBits == 8) {
            byte b0 = data[inIndex];
            byte lowB0 = (byte) (b0 & 3);
            int highB0 = b0 & 0x80;
            int g0 = b0 >> 2;
            if (highB0 != 0) {
                g0 ^= 192;
            }
            int o0 = outIndex + 1;
            out[outIndex] = ENCODE_TABLE[(byte) g0];
            int o1 = o0 + 1;
            out[o0] = ENCODE_TABLE[lowB0 << 4];
            out[o1] = PAD;
            out[o1 + 1] = PAD;
        } else if (remainderBits == 16) {
            byte b0 = data[inIndex];
            byte b1 = data[inIndex + 1];
            byte lowB1 = (byte) (b1 & 15);
            byte lowB0 = (byte) (b0 & 3);
            int highB0 = b0 & 0x80;
            int g0 = b0 >> 2;
            if (highB0 != 0) {
                g0 ^= 192;
            }
            byte group0 = (byte) g0;
            int highB1 = b1 & 0x80;
            int g1 = b1 >> 4;
            if (highB1 != 0) {
                g1 ^= 240;
            }
            int o0 = outIndex + 1;
            out[outIndex] = ENCODE_TABLE[group0];
            int o1 = o0 + 1;
            out[o0] = ENCODE_TABLE[((byte) g1) | (lowB0 << 4)];
            out[o1] = ENCODE_TABLE[lowB1 << 2];
            out[o1 + 1] = PAD;
        }
        return new String(out);
    }

    /** Decodes a base64 string, or null when it is malformed. */
    public static byte[] decode(String text) {
        if (text == null) {
            return null;
        }
        char[] chars = text.toCharArray();
        int length = stripWhitespace(chars);
        if (length % 4 != 0) {
            return null;
        }
        int groups = length / 4;
        if (groups == 0) {
            return new byte[0];
        }
        byte[] out = new byte[groups * 3];
        int inIndex = 0;
        int outIndex = 0;
        int group = 0;
        while (group < groups - 1) {
            int i0 = inIndex + 1;
            char c0 = chars[inIndex];
            if (!isDecodable(c0)) {
                return null;
            }
            int i1 = i0 + 1;
            char c1 = chars[i0];
            if (!isDecodable(c1)) {
                return null;
            }
            int i2 = i1 + 1;
            char c2 = chars[i1];
            if (!isDecodable(c2)) {
                return null;
            }
            int i3 = i2 + 1;
            char c3 = chars[i2];
            if (!isDecodable(c3)) {
                return null;
            }
            byte v0 = DECODE_TABLE[c0];
            byte v1 = DECODE_TABLE[c1];
            byte v2 = DECODE_TABLE[c2];
            byte v3 = DECODE_TABLE[c3];
            int o0 = outIndex + 1;
            out[outIndex] = (byte) ((v0 << 2) | (v1 >> 4));
            int o1 = o0 + 1;
            out[o0] = (byte) (((v1 & 15) << 4) | ((v2 >> 2) & 15));
            outIndex = o1 + 1;
            out[o1] = (byte) ((v2 << 6) | v3);
            group++;
            inIndex = i3;
        }
        int i0 = inIndex + 1;
        char c0 = chars[inIndex];
        if (!isDecodable(c0)) {
            return null;
        }
        int i1 = i0 + 1;
        char c1 = chars[i0];
        if (!isDecodable(c1)) {
            return null;
        }
        byte v0 = DECODE_TABLE[c0];
        byte v1 = DECODE_TABLE[c1];
        int i2 = i1 + 1;
        char c2 = chars[i1];
        char c3 = chars[i2];
        if (!isDecodable(c2) || !isDecodable(c3)) {
            if (isPadding(c2) && isPadding(c3)) {
                if ((v1 & 15) != 0) {
                    return null;
                }
                int completeLength = group * 3;
                byte[] result = new byte[completeLength + 1];
                System.arraycopy(out, 0, result, 0, completeLength);
                result[outIndex] = (byte) ((v0 << 2) | (v1 >> 4));
                return result;
            }
            if (isPadding(c2) || !isPadding(c3)) {
                return null;
            }
            byte v2 = DECODE_TABLE[c2];
            if ((v2 & 3) != 0) {
                return null;
            }
            int completeLength = group * 3;
            byte[] result = new byte[completeLength + 2];
            System.arraycopy(out, 0, result, 0, completeLength);
            result[outIndex] = (byte) ((v0 << 2) | (v1 >> 4));
            result[outIndex + 1] = (byte) (((v2 >> 2) & 15) | ((v1 & 15) << 4));
            return result;
        }
        byte v2 = DECODE_TABLE[c2];
        byte v3 = DECODE_TABLE[c3];
        int o0 = outIndex + 1;
        out[outIndex] = (byte) ((v0 << 2) | (v1 >> 4));
        out[o0] = (byte) (((v1 & 15) << 4) | ((v2 >> 2) & 15));
        out[o0 + 1] = (byte) (v3 | (v2 << 6));
        return out;
    }

    /** Removes whitespace in place and returns the remaining length. */
    private static int stripWhitespace(char[] chars) {
        if (chars == null) {
            return 0;
        }
        int writeIndex = 0;
        for (int readIndex = 0; readIndex < chars.length; readIndex++) {
            if (!isWhitespace(chars[readIndex])) {
                chars[writeIndex] = chars[readIndex];
                writeIndex++;
            }
        }
        return writeIndex;
    }
}
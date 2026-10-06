package com.xtc.im.core.common.tlv;

/** TLV 字节序与整数之间的转换工具（大端）。 */
public class TLVUtils {

    private static final int BYTE_MASK = 0xFF;

    /** 大端字节数组转 long，按数组长度选择 1/2/4/8 字节。 */
    public static long byteArrayToLong(byte[] bytes) {
        int length = bytes.length;
        if (length == 1) {
            return (byte) (bytes[0] & BYTE_MASK);
        }
        if (length == 2) {
            return (short) ((bytes[1] & BYTE_MASK) | ((bytes[0] & BYTE_MASK) << 8));
        }
        if (length == 4) {
            return (bytes[3] & BYTE_MASK) | ((bytes[0] & BYTE_MASK) << 24)
                    | ((bytes[1] & BYTE_MASK) << 16) | ((bytes[2] & BYTE_MASK) << 8);
        }
        if (length == 8) {
            return ((long) (bytes[0] & BYTE_MASK) << 56) | ((long) (bytes[1] & BYTE_MASK) << 48)
                    | ((long) (bytes[2] & BYTE_MASK) << 40) | ((long) (bytes[3] & BYTE_MASK) << 32)
                    | ((long) (bytes[4] & BYTE_MASK) << 24) | ((long) (bytes[5] & BYTE_MASK) << 16)
                    | ((long) (bytes[6] & BYTE_MASK) << 8) | (long) (bytes[7] & BYTE_MASK);
        }
        throw new IllegalArgumentException("the length of byte array is uncorrected.");
    }

    /** long 转最小长度的大端字节数组。 */
    public static byte[] longToByteArray(long value) {
        Long boxedValue = Long.valueOf(value);
        if (boxedValue.longValue() == boxedValue.byteValue()) {
            return toBytes(value, 1);
        }
        if (boxedValue.longValue() == boxedValue.shortValue()) {
            return toBytes(value, 2);
        }
        if (boxedValue.longValue() == boxedValue.intValue()) {
            return toBytes(value, 4);
        }
        if (boxedValue.longValue() == boxedValue.longValue()) {
            return toBytes(value, 8);
        }
        throw new IllegalArgumentException("the value [" + value + "] is too large.");
    }

    private static byte[] toBytes(long value, int size) {
        byte[] bytes = new byte[size];
        for (int index = 0; index < size; index++) {
            bytes[index] = (byte) (value >>> (((size - index) - 1) * 8));
        }
        return bytes;
    }
}
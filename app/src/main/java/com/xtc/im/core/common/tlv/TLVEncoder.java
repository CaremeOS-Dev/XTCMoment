package com.xtc.im.core.common.tlv;

/** TLV 编码器：把 tag / dataType / frameType / value 编码成字节数组。 */
public class TLVEncoder {

    public static final int ConstructedData = 32;
    public static final int PrimitiveData = 0;
    public static final int PrimitiveFrame = 0;
    public static final int PrivateFrame = 64;

    private static final int TAG_VALUE_LOW_BITS = 0x1F;
    private static final int SEVEN_BITS_MASK = 0x7F;
    private static final int CONTINUE_FLAG = 0x80;
    private static final int BYTE_MASK = 0xFF;
    private static final double BASE_128 = 128.0d;

    public static TLVEncodeResult encode(int frameType, int dataType, int tagValue, byte[] value) {
        byte[] tagBytes = encodeTag(frameType, dataType, tagValue);
        byte[] lengthBytes = encodeLength(value == null ? 0 : value.length);
        TLVEncodeResult result = new TLVEncodeResult();
        result.setTagBytes(tagBytes);
        result.setTagSize(tagBytes.length);
        result.setLengthBytes(lengthBytes);
        result.setLengthSize(lengthBytes.length);
        result.setValueBytes(value);
        result.setValueSize(value != null ? value.length : 0);
        return result;
    }

    public static TLVEncodeResult encode(int frameType, int dataType, int tagValue, String value) {
        if (value != null) {
            return encode(frameType, dataType, tagValue, value.getBytes());
        }
        return encode(frameType, dataType, tagValue, (byte[]) null);
    }

    public static TLVEncodeResult encode(int frameType, int dataType, int tagValue, long value) {
        return encode(frameType, dataType, tagValue, TLVUtils.longToByteArray(value));
    }

    /** 编码 tag 段，tagValue 大于等于 31 时使用多字节形式。 */
    public static byte[] encodeTag(int frameType, int dataType, int tagValue) {
        int header = frameType | dataType;
        int encodedValue;
        int extraDigits;
        if (tagValue >= 31) {
            extraDigits = (int) computeTagDigit(tagValue);
            encodedValue = encodeValueFromLowToHighBit((header | CONTINUE_FLAG) << (extraDigits * 8), extraDigits,
                    tagValue);
        } else {
            encodedValue = header | tagValue;
            extraDigits = 0;
        }
        return intToByteArrayForTag(encodedValue, extraDigits);
    }

    private static byte[] intToByteArrayForTag(int value, int extraDigits) {
        byte[] bytes = new byte[extraDigits + 1];
        bytes[0] = (byte) ((value >> (extraDigits * 8)) & BYTE_MASK);
        for (int index = 0; index < bytes.length; index++) {
            bytes[index] = (byte) ((value >> ((extraDigits - index) * 8)) & BYTE_MASK);
        }
        return bytes;
    }

    private static byte[] intToByteArrayForLength(int value, int digits) {
        byte[] bytes = new byte[digits];
        bytes[0] = (byte) ((value >> ((digits - 1) * 8)) & BYTE_MASK);
        for (int index = 1; index < bytes.length; index++) {
            bytes[index] = (byte) ((value >> (((digits - index) - 1) * 8)) & BYTE_MASK);
        }
        return bytes;
    }

    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private static double computeTagDigit(double tagValue) {
        if (tagValue < 31.0d) {
            throw new IllegalArgumentException("the tag value must not less than 31.");
        }
        return Math.ceil(log(tagValue + 1.0d, BASE_128));
    }

    /** 编码 length 段，小于 128 时使用单字节短格式。 */
    public static byte[] encodeLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("the length must not less than 0.");
        }
        if (length < 128) {
            return new byte[]{(byte) (length & SEVEN_BITS_MASK)};
        }
        int digits = (int) computeLengthDigit(length);
        return intToByteArrayForLength(encodeValueFromLowToHighBit(0, digits, length), digits);
    }

    private static double computeLengthDigit(int length) {
        return Math.ceil(log(length + 1, BASE_128));
    }

    /** 把 value 按每 7 位一段从低位到高位编码进多字节整数。 */
    private static int encodeValueFromLowToHighBit(int header, int digits, int value) {
        int result = 0;
        int index = 0;
        while (true) {
            int lastIndex = digits - 1;
            if (index >= lastIndex) {
                return header | ((value >> (lastIndex * 7)) & SEVEN_BITS_MASK);
            }
            result |= (((value >> (index * 7)) & SEVEN_BITS_MASK) | CONTINUE_FLAG) << ((lastIndex - index) * 8);
            index++;
        }
    }

    /** 把 value 按每 7 位一段从高位到低位编码进多字节整数。 */
    private static int encodeValueFromHighToLowBit(int header, int digits, int value) {
        int result = header;
        for (int index = digits - 1; index > 0; index--) {
            result |= (((value >> (index * 7)) & SEVEN_BITS_MASK) | CONTINUE_FLAG) << (index * 8);
        }
        return result | (value & SEVEN_BITS_MASK);
    }
}
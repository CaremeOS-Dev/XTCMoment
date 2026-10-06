package com.xtc.im.core.common.tlv;

import com.xtc.log.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** TLV 解码器：解析 tag / length / value 三段，构造类型递归解析子节点。 */
public class TLVDecoder {

    private static final int CONTINUE_FLAG = 0x80;
    private static final int BYTE_MASK = 0xFF;
    private static final int SEVEN_BITS_MASK = 0x7F;
    private static final int TAG_VALUE_LOW_BITS = 0x1F;
    private static boolean printLog = false;

    public static TLVDecodeResult decode(byte[] data) throws Throwable {
        return decodeImpl(data);
    }

    private static TLVDecodeResult decodeImpl(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }
        int tagSize = getTagBytesSize(data);
        byte[] tagBytes = new byte[tagSize];
        System.arraycopy(data, 0, tagBytes, 0, tagSize);
        int lengthSize = getLengthBytesSize(data, tagSize);
        byte[] lengthBytes = new byte[lengthSize];
        System.arraycopy(data, tagSize, lengthBytes, 0, lengthSize);
        int valueLength = decodeLength(lengthBytes);
        byte[] valueBytes = new byte[valueLength];
        System.arraycopy(data, tagSize + lengthSize, valueBytes, 0, valueLength);
        Object value;
        if (decodeDataType(tagBytes) == TLVEncoder.ConstructedData) {
            if ((data.length - tagSize) - lengthSize != valueLength) {
                LogUtil.e("tlv data may happen error because of data size is incorrect");
            }
            value = decodeMulti(valueBytes);
        } else {
            value = valueBytes;
        }
        TLVDecodeResult result = new TLVDecodeResult();
        result.setFrameType(decodeFrameType(tagBytes));
        result.setDataType(decodeDataType(tagBytes));
        result.setTagValue(decodeTagValue(tagBytes));
        result.setLength(decodeLength(lengthBytes));
        result.setValue(value);
        return result;
    }

    private static List<TLVDecodeResult> decodeMulti(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }
        TLVByteBuffer buffer = new TLVByteBuffer();
        buffer.write(data);
        ArrayList<TLVDecodeResult> results = new ArrayList<>();
        while (buffer.hasNextTLVData()) {
            results.add(decodeImpl(buffer.cutNextTLVData()));
        }
        return results;
    }

    private static void printLog(String message) {
        if (printLog) {
            System.out.print(message);
        }
    }

    /** 计算一段 TLV 数据的整体长度（含子节点）。 */
    public static int getTLVSize(byte[] data) {
        int tagSize = getTagBytesSize(data);
        byte[] tagBytes = new byte[tagSize];
        System.arraycopy(data, 0, tagBytes, 0, tagSize);
        int lengthSize = getLengthBytesSize(data, tagSize);
        byte[] lengthBytes = new byte[lengthSize];
        System.arraycopy(data, tagSize, lengthBytes, 0, lengthSize);
        int valueLength = decodeLength(lengthBytes);
        byte[] valueBytes = new byte[valueLength];
        int valueStart = tagSize + lengthSize;
        System.arraycopy(data, valueStart, valueBytes, 0, valueLength);
        int dataType = decodeDataType(tagBytes);
        int tlvSize = dataType == TLVEncoder.ConstructedData ? 1 + getTLVSize(valueBytes) : 1;
        int consumed = valueStart + valueLength;
        if (data.length <= consumed) {
            return tlvSize;
        }
        int remainLength = data.length - consumed;
        byte[] remainBytes = new byte[remainLength];
        System.arraycopy(data, consumed, remainBytes, 0, remainLength);
        return tlvSize + getTLVSize(remainBytes);
    }

    /** tag 段字节数：遇到最高位为 0 的字节结束。 */
    public static int getTagBytesSize(byte[] data) {
        int size = 0;
        for (byte value : data) {
            size++;
            if ((value & CONTINUE_FLAG) == 0) {
                return size;
            }
        }
        return 0;
    }

    /** length 段字节数。 */
    public static int getLengthBytesSize(byte[] data, int startIndex) {
        int size = 0;
        int index = startIndex;
        while (index < data.length) {
            size++;
            if ((data[index] & CONTINUE_FLAG) == 0) {
                return size;
            }
            index++;
        }
        return 0;
    }

    public static int decodeFrameType(byte[] tagBytes) {
        return tagBytes[0] & TLVEncoder.PrivateFrame;
    }

    public static int decodeDataType(byte[] tagBytes) {
        return tagBytes[0] & TLVEncoder.ConstructedData;
    }

    public static int decodeTagValue(byte[] tagBytes) {
        if ((tagBytes[0] & CONTINUE_FLAG) != CONTINUE_FLAG) {
            return tagBytes[0] & TAG_VALUE_LOW_BITS;
        }
        return decodeValueFromLowToHighBit(tagBytes);
    }

    private static int decodeValueFromHighToLowBit(byte[] bytes) {
        int value = 0;
        for (int index = 1; index < bytes.length; index++) {
            value |= (bytes[index] & SEVEN_BITS_MASK) << (((bytes.length - index) - 1) * 7);
        }
        return value;
    }

    private static int decodeValueFromLowToHighBit(byte[] bytes) {
        int value = 0;
        for (int index = 1; index < bytes.length; index++) {
            value |= (bytes[index] & SEVEN_BITS_MASK) << ((index - 1) * 7);
        }
        return value;
    }

    public static int decodeLength(byte[] lengthBytes) {
        if ((lengthBytes[0] & CONTINUE_FLAG) != CONTINUE_FLAG) {
            return (int) TLVUtils.byteArrayToLong(lengthBytes);
        }
        int length = 0 | (lengthBytes[0] & SEVEN_BITS_MASK);
        for (int index = 1; index < lengthBytes.length; index++) {
            length |= (lengthBytes[index] & SEVEN_BITS_MASK) << (index * 7);
        }
        return length;
    }
}
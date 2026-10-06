package com.xtc.im.core.common.tlv;

import java.util.Arrays;

/** 一次 TLV 编码的中间结果：tag / length / value 三段。 */
public class TLVEncodeResult {

    private byte[] lengthBytes;
    private int lengthSize;
    private byte[] tagBytes;
    private int tagSize;
    private byte[] valueBytes;
    private int valueSize;

    /** 拼出完整的 TLV 字节数组。 */
    public byte[] toByteArray() {
        byte[] result = new byte[this.tagSize + this.lengthSize + this.valueSize];
        System.arraycopy(this.tagBytes, 0, result, 0, this.tagSize);
        System.arraycopy(this.lengthBytes, 0, result, this.tagSize, this.lengthSize);
        byte[] value = this.valueBytes;
        if (value != null) {
            System.arraycopy(value, 0, result, this.tagSize + this.lengthSize, this.valueSize);
        }
        return result;
    }

    public int getTagSize() {
        return this.tagSize;
    }

    public void setTagSize(int tagSize) {
        this.tagSize = tagSize;
    }

    public int getLengthSize() {
        return this.lengthSize;
    }

    public void setLengthSize(int lengthSize) {
        this.lengthSize = lengthSize;
    }

    public int getValueSize() {
        return this.valueSize;
    }

    public void setValueSize(int valueSize) {
        this.valueSize = valueSize;
    }

    public byte[] getTagBytes() {
        return this.tagBytes;
    }

    public void setTagBytes(byte[] tagBytes) {
        this.tagBytes = tagBytes;
    }

    public byte[] getLengthBytes() {
        return this.lengthBytes;
    }

    public void setLengthBytes(byte[] lengthBytes) {
        this.lengthBytes = lengthBytes;
    }

    public byte[] getValueBytes() {
        return this.valueBytes;
    }

    public void setValueBytes(byte[] valueBytes) {
        this.valueBytes = valueBytes;
    }

    @Override
    public String toString() {
        return "TLVEncodeResult [tagSize=" + this.tagSize + ", lengthSize=" + this.lengthSize + ", valueSize="
                + this.valueSize + ", tagBytes=" + Arrays.toString(this.tagBytes) + ", lengthBytes="
                + Arrays.toString(this.lengthBytes) + ", valueBytes=" + Arrays.toString(this.valueBytes) + "]";
    }
}
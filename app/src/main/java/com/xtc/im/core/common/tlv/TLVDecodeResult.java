package com.xtc.im.core.common.tlv;

import java.util.Arrays;
import java.util.List;

/** 一次 TLV 解码的结果。 */
public class TLVDecodeResult {

    private int dataType;
    private int frameType;
    private int length;
    private int tagValue;
    private Object value;

    /** 在构造类型（dataType=32）的子节点中按 tag 查找。 */
    public TLVDecodeResult getResultByTagValue(int tagValue) {
        if (this.dataType == TLVEncoder.ConstructedData) {
            for (TLVDecodeResult result : (List<TLVDecodeResult>) this.value) {
                if (result.getTagValue() == tagValue) {
                    return result;
                }
            }
        }
        return null;
    }

    public int getIntValue() {
        Object value = this.value;
        if (value instanceof byte[]) {
            return (int) TLVUtils.byteArrayToLong((byte[]) value);
        }
        return 0;
    }

    public long getLongValue() {
        Object value = this.value;
        if (value instanceof byte[]) {
            return TLVUtils.byteArrayToLong((byte[]) value);
        }
        return 0L;
    }

    public String getStringValue() {
        Object value = this.value;
        if (value instanceof byte[]) {
            return new String((byte[]) value);
        }
        return null;
    }

    public int getFrameType() {
        return this.frameType;
    }

    public void setFrameType(int frameType) {
        this.frameType = frameType;
    }

    public int getDataType() {
        return this.dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getTagValue() {
        return this.tagValue;
    }

    public void setTagValue(int tagValue) {
        this.tagValue = tagValue;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        String valueText;
        if (this.dataType == TLVEncoder.ConstructedData) {
            Object value = this.value;
            valueText = value != null ? value.toString() : null;
        } else {
            valueText = Arrays.toString((byte[]) this.value);
        }
        return "TLVDecodeResult [frameType=" + this.frameType + ", dataType=" + this.dataType + ", tagValue="
                + this.tagValue + ", length=" + this.length + ", value=" + valueText + "]";
    }
}
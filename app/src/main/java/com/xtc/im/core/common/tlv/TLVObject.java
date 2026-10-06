package com.xtc.im.core.common.tlv;

import com.xtc.im.core.common.LogTag;
import com.xtc.log.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

/** 链式构造 TLV 数据（tag -> value），支持嵌套。 */
public class TLVObject {

    static final String TAG = LogTag.tag("TLVObject");
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public TLVObject put(int tagValue, long value) throws IOException {
        writeValue(tagValue, TLVUtils.longToByteArray(value));
        return this;
    }

    public TLVObject put(int tagValue, String value) throws IOException {
        if (value != null) {
            writeValue(tagValue, value.getBytes());
        } else {
            writeValue(tagValue, null);
        }
        return this;
    }

    public TLVObject put(int tagValue, byte[] value) throws IOException {
        writeValue(tagValue, value);
        return this;
    }

    public TLVObject put(int tagValue, TLVObject value) {
        writeTLV(tagValue, value);
        return this;
    }

    private void writeValue(int tagValue, byte[] value) throws IOException {
        this.baos.write(TLVEncoder.encode(TLVEncoder.PrimitiveFrame, TLVEncoder.PrimitiveData, tagValue, value)
                .toByteArray());
    }

    private void writeTLV(int tagValue, TLVObject value) {
        if (value == null || value.size() <= 0) {
            return;
        }
        try {
            this.baos.write(TLVEncoder.encode(TLVEncoder.PrimitiveFrame, TLVEncoder.ConstructedData, tagValue,
                    value.toByteArray()).toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int size() {
        return this.baos.size();
    }

    public byte[] toByteArray() {
        return this.baos.toByteArray();
    }

    public String toBinaryString() {
        return new BigInteger(1, this.baos.toByteArray()).toString(2);
    }

    @Override
    public String toString() {
        try {
            return TLVDecoder.decode(this.baos.toByteArray()).toString();
        } catch (Throwable throwable) {
            LogUtil.e(TAG, throwable);
            return null;
        }
    }
}
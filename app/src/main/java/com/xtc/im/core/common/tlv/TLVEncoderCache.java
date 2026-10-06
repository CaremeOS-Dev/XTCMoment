package com.xtc.im.core.common.tlv;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/** 编码结果缓存项。 */
public class TLVEncoderCache {

    private int dataType;
    private int frameType;
    private AtomicInteger hitCount = new AtomicInteger(0);
    private int tagValue;
    private TLVEncodeResult tlvEncodeResult;
    private byte[] value;

    public TLVEncoderCache() {
    }

    public TLVEncoderCache(int frameType, int dataType, int tagValue, byte[] value, TLVEncodeResult result) {
        this.frameType = frameType;
        this.dataType = dataType;
        this.tagValue = tagValue;
        this.value = value;
        this.tlvEncodeResult = result;
    }

    /** 命中时返回缓存结果并累加命中次数，否则返回 null。 */
    public TLVEncodeResult get(int frameType, int dataType, int tagValue, byte[] value) {
        if (frameType != this.frameType || dataType != this.dataType || tagValue != this.tagValue
                || !Arrays.equals(value, this.value)) {
            return null;
        }
        this.hitCount.incrementAndGet();
        return this.tlvEncodeResult;
    }

    public int getHitCount() {
        return this.hitCount.get();
    }
}
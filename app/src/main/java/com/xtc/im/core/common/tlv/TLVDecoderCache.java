package com.xtc.im.core.common.tlv;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/** 解码结果缓存项。 */
public class TLVDecoderCache {

    private AtomicInteger hitCount = new AtomicInteger(0);
    private byte[] tlvBytes;
    private TLVDecodeResult tlvDecodeResult;

    public TLVDecoderCache() {
    }

    public TLVDecoderCache(byte[] tlvBytes, TLVDecodeResult tlvDecodeResult) {
        this.tlvBytes = tlvBytes;
        this.tlvDecodeResult = tlvDecodeResult;
    }

    /** 命中时返回缓存结果并累加命中次数，否则返回 null。 */
    public TLVDecodeResult get(byte[] data) {
        if (data == null || data.length <= 0 || !Arrays.equals(data, this.tlvBytes)) {
            return null;
        }
        this.hitCount.incrementAndGet();
        return this.tlvDecodeResult;
    }

    public int getHitCount() {
        return this.hitCount.get();
    }
}
package com.xtc.im.core.common.request;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

/** 加密包装：承载需要整体加解密的 payload。 */
@CommandValue(Command.ENCRYPT_WAPPER)
public class EncryptWapper extends Entity {

    @TagValue(10)
    private byte[] payload;

    public byte[] getPayload() {
        return this.payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
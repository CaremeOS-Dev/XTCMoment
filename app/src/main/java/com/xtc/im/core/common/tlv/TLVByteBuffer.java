package com.xtc.im.core.common.tlv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/** 可增量写入、按 TLV 边界切分的字节缓冲区。 */
public class TLVByteBuffer extends ByteArrayOutputStream {

    private static boolean printLog = false;
    private volatile int firstTotalSize = 0;
    private volatile int firstTagSize = 0;
    private volatile int firstLengthSize = 0;

    /** 缓冲区中是否还有完整的 TLV 数据。 */
    public synchronized boolean hasNextTLVData() {
        if (this.count == 0) {
            return false;
        }
        compute();
        return this.firstTotalSize > 0 && this.count > 0 && this.firstTotalSize <= this.count;
    }

    @Override
    public synchronized void reset() {
        super.reset();
        this.firstTotalSize = 0;
        this.firstTagSize = 0;
        this.firstLengthSize = 0;
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int length) {
        super.write(buffer, offset, length);
    }

    /** 切出缓冲区开头的第一段完整 TLV 数据。 */
    public synchronized byte[] cutNextTLVData() {
        byte[] tlvData;
        if (this.firstTotalSize == this.count) {
            tlvData = toByteArray();
            reset();
        } else if (this.firstTotalSize < this.count) {
            byte[] remainBytes = new byte[this.count - this.firstTotalSize];
            byte[] firstBytes = new byte[this.firstTotalSize];
            System.arraycopy(toByteArray(), this.firstTotalSize, remainBytes, 0, remainBytes.length);
            System.arraycopy(toByteArray(), 0, firstBytes, 0, firstBytes.length);
            reset();
            write(remainBytes, 0, remainBytes.length);
            tlvData = firstBytes;
        } else {
            System.err.println("firstTotalSize:" + this.firstTotalSize + ",count:" + this.count
                    + ",firstTotalSize must smaller than count!");
            tlvData = null;
        }
        return tlvData;
    }

    private void compute() {
        if (this.count > 0) {
            computeTagSize();
            computeLengthSize();
            computeTotalSize();
        }
    }

    private void computeTagSize() {
        if (this.firstTagSize == 0) {
            this.firstTagSize = TLVDecoder.getTagBytesSize(toByteArray());
            print("firstTagSize:" + this.firstTagSize);
        }
    }

    private void computeLengthSize() {
        if (this.firstLengthSize != 0 || this.firstTagSize == 0) {
            return;
        }
        this.firstLengthSize = TLVDecoder.getLengthBytesSize(toByteArray(), this.firstTagSize);
        print("firstLengthSize:" + this.firstLengthSize);
    }

    private void computeTotalSize() {
        if (this.firstTagSize <= 0 || this.firstLengthSize <= 0 || this.firstTotalSize != 0) {
            return;
        }
        byte[] lengthBytes = new byte[this.firstLengthSize];
        System.arraycopy(toByteArray(), this.firstTagSize, lengthBytes, 0, this.firstLengthSize);
        this.firstTotalSize = this.firstTagSize + this.firstLengthSize + TLVDecoder.decodeLength(lengthBytes);
        print("firstTotalSize:" + this.firstTotalSize);
    }

    private void print(String message) {
        if (printLog) {
            System.out.print(message);
        }
    }
}
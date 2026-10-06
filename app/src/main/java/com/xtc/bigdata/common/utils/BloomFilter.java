package com.xtc.bigdata.common.utils;

import android.text.TextUtils;

import java.util.BitSet;
import java.util.Random;

/**
 * 布隆过滤器实现，用于快速判断元素是否可能存在。
 */
public class BloomFilter {

    private static final double LN2 = 0.6931471805599453d;

    private BitSet bitSet;
    private int bitSetSize;
    private int expectedNumberOfElements;
    private int k;
    private int numberOfHashFunctions;
    private Random randomGenerator;

    public BloomFilter(int expectedElements, double falsePositiveRate) {
        if (expectedElements <= 0 || falsePositiveRate <= 0.0d || falsePositiveRate >= 1.0d) {
            throw new IllegalArgumentException("Invalid arguments provided");
        }
        this.expectedNumberOfElements = expectedElements;
        this.k = (int) Math.round((Math.log(falsePositiveRate) * (-LN2)) / Math.pow(Math.log(2.0d), 2.0d));
        this.numberOfHashFunctions = this.k;
        double expected = expectedElements;
        double bitsPerElement = (-Math.log(falsePositiveRate)) / (Math.log(2.0d) * Math.log(2.0d));
        this.bitSetSize = (int) Math.ceil(expected * bitsPerElement);
        this.bitSet = new BitSet(this.bitSetSize);
        this.randomGenerator = new Random();
    }

    public void add(String value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        for (int i = 0; i < this.k; i++) {
            this.bitSet.set(Math.abs(getHash(value.getBytes(), i) % this.bitSetSize), true);
        }
    }

    public boolean contains(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        for (int i = 0; i < this.k; i++) {
            if (!this.bitSet.get(Math.abs(getHash(value.getBytes(), i) % this.bitSetSize))) {
                return false;
            }
        }
        return true;
    }

    public int getNumberOfHashFunctions() {
        return this.numberOfHashFunctions;
    }

    public int getBitSetSize() {
        return this.bitSetSize;
    }

    public int getExpectedNumberOfElements() {
        return this.expectedNumberOfElements;
    }

    private int getHash(byte[] bytes, int index) {
        this.randomGenerator.setSeed(index * 1000);
        int hash = 0;
        for (byte value : bytes) {
            hash = (hash * 31) + value;
        }
        return Math.abs(this.randomGenerator.nextInt() ^ hash);
    }
}
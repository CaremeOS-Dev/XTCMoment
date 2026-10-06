package com.xtc.httplib.constant;

import java.util.ArrayList;

/** Sampling configuration for the big-data collector. */
public class BigdataConfig {
    private float sampleRate;
    private int samplingPollSize;
    private ArrayList<String> whitePackages;
    private ArrayList<String> whiteCodes;

    public float getSampleRate() {
        return this.sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSamplingPollSize() {
        return this.samplingPollSize;
    }

    public void setSamplingPollSize(int samplingPollSize) {
        this.samplingPollSize = samplingPollSize;
    }

    public ArrayList<String> getWhitePackages() {
        return this.whitePackages;
    }

    public void setWhitePackages(ArrayList<String> whitePackages) {
        this.whitePackages = whitePackages;
    }

    public ArrayList<String> getWhiteCodes() {
        return this.whiteCodes;
    }

    public void setWhiteCodes(ArrayList<String> whiteCodes) {
        this.whiteCodes = whiteCodes;
    }
}
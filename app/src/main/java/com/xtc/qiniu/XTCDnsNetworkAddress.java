package com.xtc.qiniu;

import com.qiniu.android.http.dns.IDnsNetworkAddress;

/** {@link IDnsNetworkAddress} backed by a resolved IP address. */
class XTCDnsNetworkAddress implements IDnsNetworkAddress {

    private final String hostValue;
    private final String ipValue;
    private final String sourceValue;
    private final Long timestampValue;
    private final Long ttlValue;

    XTCDnsNetworkAddress(String hostValue, String ipValue, Long ttlValue, String sourceValue, Long timestampValue) {
        this.hostValue = hostValue;
        this.ipValue = ipValue;
        this.ttlValue = ttlValue;
        this.sourceValue = sourceValue;
        this.timestampValue = timestampValue;
    }

    @Override
    public String getHostValue() {
        return this.hostValue;
    }

    @Override
    public String getIpValue() {
        return this.ipValue;
    }

    @Override
    public Long getTtlValue() {
        return this.ttlValue;
    }

    @Override
    public String getSourceValue() {
        return this.sourceValue;
    }

    @Override
    public Long getTimestampValue() {
        return this.timestampValue;
    }
}
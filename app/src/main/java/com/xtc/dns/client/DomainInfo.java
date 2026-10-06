package com.xtc.dns.client;

/** One resolved HTTP-DNS entry. */
public class DomainInfo {
    private String url;
    private String host;
    private String ip;
    private String data = null;
    private String stopTime = null;
    private String code = null;
    private String startTime = String.valueOf(System.currentTimeMillis());

    public DomainInfo(String host, String ip) {
        this.host = host;
        this.ip = ip;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return this.stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "DomainInfo{url=\'" + this.url + "\', host=\'" + this.host + "\', ip=\'" + this.ip + "\', data=\'"
                + this.data + "\', startTime=\'" + this.startTime + "\', stopTime=\'" + this.stopTime + "\', code=\'"
                + this.code + "\'}";
    }
}
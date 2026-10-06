package com.xtc.httplib.bean;

/** Base request parameters attached to every gateway call. */
public class NetBaseRequestParam {

    /** App id values. */
    public interface AppId {
        String WATCH = "2";
    }

    /** IM flag values. */
    public interface ImFlag {
        String No = "";
        String Yes = "1";
    }

    /** Program values. */
    public interface Program {
        String Watch = "watch";
    }

    private String accountId;
    private String appId;
    private String deviceId;
    private String imFlag;
    private String mac;
    private String machineId;
    private String program;
    private Long registId;
    private String requestId;
    private String timestamp;
    private String token;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMachineId() {
        return this.machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProgram() {
        return this.program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getImFlag() {
        return this.imFlag;
    }

    public void setImFlag(String imFlag) {
        this.imFlag = imFlag;
    }

    public Long getRegistId() {
        return this.registId;
    }

    public void setRegistId(Long registId) {
        this.registId = registId;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "NetBaseRequestParam{appId=\'" + this.appId + "\', token=\'" + this.token + "\', timestamp=\'"
                + this.timestamp + "\', machineId=\'" + this.machineId + "\', mac=\'" + this.mac
                + "\', accountId=\'" + this.accountId + "\', deviceId=\'" + this.deviceId + "\', program=\'"
                + this.program + "\', imFlag=\'" + this.imFlag + "\', registId=" + this.registId
                + ", requestId=\'" + this.requestId + "\'}";
    }
}
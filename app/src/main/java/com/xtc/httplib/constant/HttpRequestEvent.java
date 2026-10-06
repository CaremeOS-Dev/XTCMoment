package com.xtc.httplib.constant;

/** Timing / routing details collected for one HTTP request. */
public class HttpRequestEvent {
    String id = "id";
    String pkgName = "pkg_name";
    String appVer = "app_ver";
    String url = "url";
    String optUrl = "opt_url";
    String host = "host";
    String reqDate = "req_date";
    String reqHour = "req_hour";
    String dnsResult = "false";
    String dnsProvider = String.valueOf(-1);
    String dnsCostTime = "0";
    String reqContentLength = "0";
    String respContentLength = "0";
    String interceptorLogTime = "0";
    String interceptorShutdownLctTime = "0";
    String interceptorTimeoutTime = "0";
    String interceptorMonitorTime = "0";
    String interceptorPreprocessorTime = "0";
    String interceptorReqTime = "0";
    String interceptorRespTime = "0";
    String callServerCostTime = "0";
    String totalCostTime = "0";
    String code = "200";
    String errorReason = "unknown";
    String netTag = "unknown";

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppVer() {
        return this.appVer;
    }

    public void setAppVer(String appVer) {
        this.appVer = appVer;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOptUrl() {
        return this.optUrl;
    }

    public void setOptUrl(String optUrl) {
        this.optUrl = optUrl;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getReqDate() {
        return this.reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqHour() {
        return this.reqHour;
    }

    public void setReqHour(String reqHour) {
        this.reqHour = reqHour;
    }

    public String getDnsProvider() {
        return this.dnsProvider;
    }

    public void setDnsProvider(String dnsProvider) {
        this.dnsProvider = dnsProvider;
    }

    public String getDnsCostTime() {
        return this.dnsCostTime;
    }

    public void setDnsCostTime(String dnsCostTime) {
        this.dnsCostTime = dnsCostTime;
    }

    public String getReqContentLength() {
        return this.reqContentLength;
    }

    public void setReqContentLength(String reqContentLength) {
        this.reqContentLength = reqContentLength;
    }

    public String getRespContentLength() {
        return this.respContentLength;
    }

    public void setRespContentLength(String respContentLength) {
        this.respContentLength = respContentLength;
    }

    public String getInterceptorLogTime() {
        return this.interceptorLogTime;
    }

    public void setInterceptorLogTime(String interceptorLogTime) {
        this.interceptorLogTime = interceptorLogTime;
    }

    public String getInterceptorShutdownLctTime() {
        return this.interceptorShutdownLctTime;
    }

    public void setInterceptorShutdownLctTime(String interceptorShutdownLctTime) {
        this.interceptorShutdownLctTime = interceptorShutdownLctTime;
    }

    public String getInterceptorTimeoutTime() {
        return this.interceptorTimeoutTime;
    }

    public void setInterceptorTimeoutTime(String interceptorTimeoutTime) {
        this.interceptorTimeoutTime = interceptorTimeoutTime;
    }

    public String getInterceptorMonitorTime() {
        return this.interceptorMonitorTime;
    }

    public void setInterceptorMonitorTime(String interceptorMonitorTime) {
        this.interceptorMonitorTime = interceptorMonitorTime;
    }

    public String getInterceptorPreprocessorTime() {
        return this.interceptorPreprocessorTime;
    }

    public void setInterceptorPreprocessorTime(String interceptorPreprocessorTime) {
        this.interceptorPreprocessorTime = interceptorPreprocessorTime;
    }

    public String getInterceptorReqTime() {
        return this.interceptorReqTime;
    }

    public void setInterceptorReqTime(String interceptorReqTime) {
        this.interceptorReqTime = interceptorReqTime;
    }

    public String getInterceptorRespTime() {
        return this.interceptorRespTime;
    }

    public void setInterceptorRespTime(String interceptorRespTime) {
        this.interceptorRespTime = interceptorRespTime;
    }

    public String getTotalCostTime() {
        return this.totalCostTime;
    }

    public void setTotalCostTime(String totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorReason() {
        return this.errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getNetTag() {
        return this.netTag;
    }

    public void setNetTag(String netTag) {
        this.netTag = netTag;
    }

    public String getDnsResult() {
        return this.dnsResult;
    }

    public void setDnsResult(String dnsResult) {
        this.dnsResult = dnsResult;
    }

    public String getCallServerCostTime() {
        return this.callServerCostTime;
    }

    public void setCallServerCostTime(String callServerCostTime) {
        this.callServerCostTime = callServerCostTime;
    }

    @Override
    public String toString() {
        return "HttpRequestEvent{id=\'" + this.id + "\'\n, pkgName=\'" + this.pkgName + "\'\n, appVer=\'" + this.appVer
                + "\'\n, url=\'" + this.url + "\'\n, opt_url=\'" + this.optUrl + "\'\n, host=\'" + this.host
                + "\'\n, req_date=\'" + this.reqDate + "\'\n, req_hour=\'" + this.reqHour + "\'\n, dns_result=\'"
                + this.dnsResult + "\'\n, dns_provider=\'" + this.dnsProvider + "\'\n, dns_cost_time=\'"
                + this.dnsCostTime + "\'\n, req_content_length=\'" + this.reqContentLength
                + "\'\n, resp_content_length=\'" + this.respContentLength + "\'\n, interceptor_log_time=\'"
                + this.interceptorLogTime + "\'\n, interceptor_shutdown_lct_time=\'" + this.interceptorShutdownLctTime
                + "\'\n, interceptor_timeout_time=\'" + this.interceptorTimeoutTime + "\'\n, interceptor_monitor_time=\'"
                + this.interceptorMonitorTime + "\'\n, interceptor_preprocessor_time=\'" + this.interceptorPreprocessorTime
                + "\'\n, interceptor_req_time=\'" + this.interceptorReqTime + "\'\n, interceptor_resp_time=\'"
                + this.interceptorRespTime + "\'\n, total_cost_time=\'" + this.totalCostTime
                + "\'\n, call_server_cost_time=\'" + this.callServerCostTime + "\'\n, code=\'" + this.code
                + "\'\n, error_reason=\'" + this.errorReason + "\'\n, net_tag=\'" + this.netTag + "\'\n}";
    }
}
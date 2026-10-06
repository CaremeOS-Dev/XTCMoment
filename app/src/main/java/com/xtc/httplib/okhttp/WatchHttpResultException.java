package com.xtc.httplib.okhttp;

/** Thrown when the server returns a non-success business code. */
public class WatchHttpResultException extends RuntimeException {

    private final int appCode;
    private final String serverCode;
    private String tips;

    /** Maps a server code to an app-level code (currently always 0). */
    private int convert(String serverCode) {
        return 0;
    }

    public WatchHttpResultException(String serverCode) {
        super(serverCode);
        this.serverCode = serverCode;
        this.appCode = convert(serverCode);
    }

    public WatchHttpResultException(String serverCode, String tips) {
        super(serverCode);
        this.serverCode = serverCode;
        this.appCode = convert(serverCode);
        this.tips = tips;
    }

    public int appCode() {
        return this.appCode;
    }

    public String serverCode() {
        return this.serverCode;
    }

    public String getTips() {
        return this.tips;
    }
}
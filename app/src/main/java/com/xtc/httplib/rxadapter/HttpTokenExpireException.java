package com.xtc.httplib.rxadapter;

import okhttp3.Response;

/** Thrown when a parked request times out while waiting for a new token. */
class HttpTokenExpireException extends RuntimeException {

    private final int code;
    private final String message;
    private final transient Response response;

    private static String getMessage(Response response) {
        if (response == null) {
            return "HTTP null ";
        }
        return "HTTP " + response.code() + " " + response.message();
    }

    public HttpTokenExpireException(Response response) {
        super(getMessage(response));
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public Response response() {
        return this.response;
    }
}
package com.xtc.httplib.util;

import com.xtc.httplib.constant.HttpRequestEvent;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/** Builds synthetic error responses for the monitoring interceptors. */
public class HttpUtil {

    /** Empty body shared by every synthetic response. */
    static final ResponseBody EMPTY_BODY = new ResponseBody() {
        @Override
        public long contentLength() {
            return 0L;
        }

        @Override
        public MediaType contentType() {
            return null;
        }

        @Override
        public BufferedSource source() {
            return new Buffer();
        }
    };

    private HttpUtil() {
    }

    private static String getErrorMessage(int code) {
        switch (code) {
            case 1001:
                return "ERROR_BOOT_LIMIT_TIME";
            case 1002:
                return "ERROR_REQUESTING";
            case 1003:
                return "ERROR_FREQUENT_REQUEST";
            case 1004:
                return "ERROR_HEAVY_TRAFFIC";
            case 1005:
                return "ERROR_NO_NETWORK_CONNECTIVITY";
            case 1006:
                return "ERROR_NO_NETWORK_PERMISSION";
            case 1007:
                return "ERROR_HTTP_TOKEN_INVALID";
            default:
                return "Unknown";
        }
    }

    /** Creates an error response carrying the monitoring error code. */
    public static Response createErrorResponse(Request request, String url, int code, HttpRequestEvent event) {
        if (event != null) {
            event.setDnsProvider(String.valueOf(-1));
            event.setDnsResult(String.valueOf(false));
            event.setDnsCostTime(String.valueOf(0));
        }
        return new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(getErrorMessage(code))
                .body(EMPTY_BODY)
                .sentRequestAtMillis(-1L)
                .receivedResponseAtMillis(System.currentTimeMillis())
                .build();
    }
}
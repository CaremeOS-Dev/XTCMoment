package com.xtc.im.transpond;

/** Bridges HTTP requests over the IM channel. */
public interface TranspondAdapter {
    boolean transpondHttp(String url, int method, byte[] headers, byte[] body, ITranspondCallback callback);
}
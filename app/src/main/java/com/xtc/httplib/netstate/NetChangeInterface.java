package com.xtc.httplib.netstate;

/** Notified when the network connectivity changes. */
public interface NetChangeInterface {
    void netChange(boolean connected);
}
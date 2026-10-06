package com.xtc.httplib.constant;

/** Token validity states reported by the auth layer. */
public interface HttpTokenState {
    int EFFECTIVE = 1;
    int EMPTY = 2;
    int EXPIRE = 3;
    int NOT_SUPPORT = 0;
}
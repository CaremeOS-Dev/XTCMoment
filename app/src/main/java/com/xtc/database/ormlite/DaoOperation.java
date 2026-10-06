package com.xtc.database.ormlite;

/** Codes of the supported dao operations. */
public interface DaoOperation {

    int INSERT = 1;
    int DELETE = 2;
    int UPDATE = 3;
    int QUERY = 4;
    int QUERY_COUNT = 5;
    int QUERY_FIRST = 6;
    int CLEAR = 7;
}
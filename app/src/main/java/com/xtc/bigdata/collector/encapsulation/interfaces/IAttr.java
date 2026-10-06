package com.xtc.bigdata.collector.encapsulation.interfaces;

import android.content.ContentValues;

/** Attribute that can be written into a {@link ContentValues} row. */
public interface IAttr {
    IAttr clone();

    void insert(ContentValues contentValues);
}
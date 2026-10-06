package com.xtc.bigdata.collector.encapsulation.interfaces;

import android.content.ContentValues;

import java.util.List;

/** Event that can be turned into database rows. */
public interface IEvent {
    int eventType();

    ContentValues getContentValues();

    List<IAttr> makeData();
}
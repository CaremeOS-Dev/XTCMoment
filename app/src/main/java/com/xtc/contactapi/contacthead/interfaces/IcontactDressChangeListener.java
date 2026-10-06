package com.xtc.contactapi.contacthead.interfaces;

import android.graphics.drawable.BitmapDrawable;

/**
 * 装扮变化监听。
 */
public interface IcontactDressChangeListener {

    /** 装扮发生变化。 */
    boolean onDressChanged(String watchId, String dressId, BitmapDrawable dressBitmap, boolean isGif);
}
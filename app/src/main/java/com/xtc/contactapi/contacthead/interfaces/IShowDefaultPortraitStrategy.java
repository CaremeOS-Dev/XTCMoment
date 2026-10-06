package com.xtc.contactapi.contacthead.interfaces;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;

/**
 * 默认头像展示策略。
 */
public interface IShowDefaultPortraitStrategy {

    /** 展示默认头像。 */
    void showDefaultPortrait(Context context, ContactHeadManager headManager, ContactBean contactBean, View view, Bitmap bitmap);
}
package com.xtc.contactapi.contacthead.interfaces;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;

/**
 * 联系人头像展示策略。
 */
public interface IShowHeadToViewStrategy {

    /** 展示联系人头像。 */
    void showContactPortrait(Context context, ContactHeadManager headManager, ContactBean contactBean, View view, BitmapDrawable headBitmap);
}
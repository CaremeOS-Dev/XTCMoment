package com.xtc.contactapi.contacthead.interfaces;

import android.content.Context;
import android.view.View;

import com.xtc.contactapi.contacthead.bean.DressBean;
import com.xtc.contactapi.contacthead.impl.ContactHeadManager;

/**
 * 装扮展示策略。
 */
public interface IShowDressToViewStrategy {

    /** 展示装扮。 */
    void showDress(Context context, ContactHeadManager headManager, DressBean dressBean, View view);

    /** 隐藏装扮。 */
    void hideDress(Context context, ContactHeadManager headManager, DressBean dressBean, View view);
}
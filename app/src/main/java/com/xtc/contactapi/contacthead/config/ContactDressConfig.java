package com.xtc.contactapi.contacthead.config;

import android.graphics.Bitmap;

import com.xtc.contactapi.contacthead.interfaces.IShowDressToViewStrategy;

/**
 * 联系人装扮加载配置。
 */
public class ContactDressConfig {

    /** 是否加载装扮。 */
    public boolean loadDress = true;
    /** 是否缓存装扮。 */
    public boolean cacheDress = false;
    /** 位图配置。 */
    public Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;
    /** 装扮展示策略。 */
    public IShowDressToViewStrategy showDressToViewStrategy = null;
}
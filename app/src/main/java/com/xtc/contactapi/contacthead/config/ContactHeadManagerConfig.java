package com.xtc.contactapi.contacthead.config;

import android.content.Context;
import android.graphics.Bitmap;

import com.xtc.contactapi.contacthead.impl.ContactHeadManager;
import com.xtc.contactapi.contacthead.interfaces.IShowDressToViewStrategy;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;

/**
 * 联系人头像管理器配置，支持链式构建。
 */
public class ContactHeadManagerConfig {

    public Context context = null;
    /** 是否缓存头像。 */
    public boolean cacheHead = true;
    /** 是否加载头像。 */
    public boolean loadHead = true;
    public Bitmap.Config bitmapConfig = Bitmap.Config.RGB_565;
    public IShowHeadToViewStrategy showHeadToViewStrategy = null;
    /** 是否启用装扮。 */
    public boolean dressEnabled = true;
    public ContactDressConfig dressConfig;

    /**
     * 构建器。
     */
    public static class Builder {

        private final ContactHeadManagerConfig config = new ContactHeadManagerConfig();
        private final ContactDressConfig dressConfig = new ContactDressConfig();

        public Builder context(Context context) {
            this.config.context = context;
            return this;
        }

        public Builder cacheHead(boolean cacheHead) {
            this.config.cacheHead = cacheHead;
            return this;
        }

        public Builder loadHead(boolean loadHead) {
            this.config.loadHead = loadHead;
            return this;
        }

        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            this.config.bitmapConfig = bitmapConfig;
            return this;
        }

        public Builder showHeadToViewStrategy(IShowHeadToViewStrategy strategy) {
            this.config.showHeadToViewStrategy = strategy;
            return this;
        }

        public Builder dressEnabled(boolean dressEnabled) {
            this.config.dressEnabled = dressEnabled;
            return this;
        }

        public Builder loadDress(boolean loadDress) {
            this.dressConfig.loadDress = loadDress;
            return this;
        }

        public Builder cacheDress(boolean cacheDress) {
            this.dressConfig.cacheDress = cacheDress;
            return this;
        }

        public Builder dressBitmapConfig(Bitmap.Config bitmapConfig) {
            this.dressConfig.bitmapConfig = bitmapConfig;
            return this;
        }

        public Builder showDressToViewStrategy(IShowDressToViewStrategy strategy) {
            this.dressConfig.showDressToViewStrategy = strategy;
            return this;
        }

        public ContactHeadManager build() {
            if (this.config.context == null) {
                throw new NullPointerException("config context is null");
            }
            if (this.config.showHeadToViewStrategy == null) {
                throw new NullPointerException("config showDefaultPortraitStrategy is null,you must implements IShowDefaultPortraitStrategy");
            }
            if (this.config.dressEnabled) {
                this.config.dressConfig = this.dressConfig;
            }
            return new ContactHeadManager(this.config);
        }
    }
}
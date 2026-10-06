package com.xtc.web.core.data.bean;

/** WebView 缓存模块开关的扩展配置，分别对应高通与展讯平台的可缓存大小。 */
public class CacheModuleSwitchExtra {

    private int gtCacheSize;
    private int zxCacheSize;

    public int getGtCacheSize() {
        return this.gtCacheSize;
    }

    public void setGtCacheSize(int gtCacheSize) {
        this.gtCacheSize = gtCacheSize;
    }

    public int getZxCacheSize() {
        return this.zxCacheSize;
    }

    public void setZxCacheSize(int zxCacheSize) {
        this.zxCacheSize = zxCacheSize;
    }

    @Override
    public String toString() {
        return "CacheModuleSwitchExtra{gtCacheSize=" + this.gtCacheSize + ", zxCacheSize=" + this.zxCacheSize + '}';
    }
}
package com.xtc.im.core.common.response;

/** 三方应用的 alias / tag 绑定信息。 */
public class AliasAndTagInfo {

    private String alias;
    private int order;
    private int pageSize;
    private String pkgName;
    private long syncKey;
    private String tag;

    public AliasAndTagInfo(String alias, String pkgName, int order, int pageSize, long syncKey) {
        this.alias = alias;
        this.pkgName = pkgName;
        this.order = order;
        this.pageSize = pageSize;
        this.syncKey = syncKey;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    @Override
    public String toString() {
        return "AliasAndTagInfo{alias='" + this.alias + "'" + ", tag='" + this.tag + "'" + ", pkgName='"
                + this.pkgName + "'" + ", order=" + this.order + ", pageSize=" + this.pageSize + ", syncKey="
                + this.syncKey + "}";
    }
}
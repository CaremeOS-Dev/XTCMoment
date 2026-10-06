package com.xtc.im.core.common.response.entity.third;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.response.entity.ResponseEntity;

@CommandValue(112)
public class ThirdSyncInformResponseEntity extends ResponseEntity {

    @TagValue(10)
    private String pkgName;

    @TagValue(11)
    private String alias;

    @TagValue(12)
    private long syncKey;

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    @Override
    public String toString() {
        return "ThirdSyncInformResponseEntity{pkgName='" + this.pkgName + "'" + ", alias='" + this.alias + "'" + ", syncKey=" + this.syncKey + "}";
    }
}

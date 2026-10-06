package com.xtc.virtualselfapi.bean.net.req;

import com.xtc.virtualselfapi.bean.db.DbCostume;
import com.xtc.virtualselfapi.bean.db.DbDanger;

import java.util.List;

/**
 * 装扮资源响应。
 */
public class RespResource {

    private List<DbDanger> dangerList;
    private List<DbCostume> ornamentList;

    public List<DbCostume> getOrnamentList() {
        return this.ornamentList;
    }

    public void setOrnamentList(List<DbCostume> ornamentList) {
        this.ornamentList = ornamentList;
    }

    public List<DbDanger> getDangerList() {
        return this.dangerList;
    }

    public void setDangerList(List<DbDanger> dangerList) {
        this.dangerList = dangerList;
    }

    @Override
    public String toString() {
        return "RespResource{ornamentList=" + this.ornamentList + ", dangerList=" + this.dangerList + '}';
    }
}
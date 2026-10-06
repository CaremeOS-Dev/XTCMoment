package com.xtc.virtualselfapi.bean.net.req;

import com.xtc.virtualselfapi.bean.db.DbCostume;

import java.util.List;

/**
 * 集卡信息响应。
 */
public class RespCardInfo {

    private List<DbCostume> costumes;

    public List<DbCostume> getCostumes() {
        return this.costumes;
    }

    public void setCostumes(List<DbCostume> costumes) {
        this.costumes = costumes;
    }
}
package com.xtc.virtualselfapi.bean.net.req;

import com.xtc.virtualselfapi.bean.db.DbPosition;

import java.util.List;

/**
 * 装扮布局响应。
 */
public class RespPostion {

    private List<DbPosition> list;

    public List<DbPosition> getList() {
        return this.list;
    }

    public void setList(List<DbPosition> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "RespPostion{list=" + this.list + '}';
    }
}
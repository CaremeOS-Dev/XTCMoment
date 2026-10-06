package com.xtc.moment.serve.bean;

import java.util.List;

/**
 * 批量下载地址请求参数。
 */
public class FileBatchUrlParam {

    private List<String> keys;

    public FileBatchUrlParam(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getKeys() {
        return this.keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "FileBatchUrlParam{keys=" + this.keys + '}';
    }
}
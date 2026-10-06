package com.xtc.aitext.bean;

import java.util.List;

/**
 * 模块包名列表。
 */
public class ModulePackageBean {

    private List<String> packageList;

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }

    public List<String> getPackageList() {
        return packageList;
    }

    @Override
    public String toString() {
        return "ModulePackageBean{packageList=" + packageList + '}';
    }
}
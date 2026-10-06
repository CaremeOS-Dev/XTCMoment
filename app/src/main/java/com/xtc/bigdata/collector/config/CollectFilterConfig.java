package com.xtc.bigdata.collector.config;

import com.xtc.bigdata.common.utils.BloomFilter;
import com.xtc.log.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采集过滤配置：布隆过滤器 + A/C 等级名单 + 功能点映射。
 */
public class CollectFilterConfig {

    private static final String TAG = "CollectFilterConfig";

    private BloomFilter bloomFilter;
    private Map<String, String> filterFunctionItemMap = new HashMap<>();
    private List<String> listGradeA;
    private List<String> listGradeC;

    public Map<String, String> getFilterFunctionItemMap() {
        return this.filterFunctionItemMap;
    }

    public void setFilterFunctionItemMap(Map<String, String> filterFunctionItemMap) {
        LogUtil.d(TAG, "setFilterFunctionItemMap = " + filterFunctionItemMap);
        this.filterFunctionItemMap = filterFunctionItemMap;
    }

    public void setBloomFilter(BloomFilter bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    public BloomFilter getBloomFilter() {
        return this.bloomFilter;
    }

    public void setListGradeA(List<String> listGradeA) {
        this.listGradeA = listGradeA;
    }

    public List<String> getListGradeA() {
        return this.listGradeA;
    }

    public void setListGradeC(List<String> listGradeC) {
        this.listGradeC = listGradeC;
    }

    public List<String> getListGradeC() {
        return this.listGradeC;
    }
}
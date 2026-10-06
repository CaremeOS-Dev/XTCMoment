package com.xtc.bigdata.collector.config;

/** Filter configuration applied before an event is recorded. */
public class CollectFilterConfig {

    private String filterFunctionItemJson;

    public String getFilterFunctionItemJson() {
        return this.filterFunctionItemJson;
    }

    public void setFilterFunctionItemJson(String filterFunctionItemJson) {
        this.filterFunctionItemJson = filterFunctionItemJson;
    }
}
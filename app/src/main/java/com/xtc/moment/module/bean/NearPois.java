package com.xtc.moment.module.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Nearby points of interest returned by the map service. */
public class NearPois {

    @SerializedName("nearPois")
    private List<PoiBean> nearPois;

    public List<PoiBean> getNearPois() {
        return this.nearPois;
    }

    public void setNearPois(List<PoiBean> nearPois) {
        this.nearPois = nearPois;
    }

    @Override
    public String toString() {
        return "NearPois{nearPois=" + this.nearPois + '}';
    }
}
package com.xtc.moment.module.prerogative.bean;

import java.util.List;

/**
 * 个人特权数据返回体。
 */
public class PersonalResponse {

    private List<EmotionsEntity> emotions;
    private List<PackagesEntity> packages;

    public void setEmotions(List<EmotionsEntity> emotions) {
        this.emotions = emotions;
    }

    public void setPackages(List<PackagesEntity> packages) {
        this.packages = packages;
    }

    public List<EmotionsEntity> getEmotions() {
        return this.emotions;
    }

    public List<PackagesEntity> getPackages() {
        return this.packages;
    }
}
package com.xtc.moment.net.bean;

import java.util.List;
import java.util.Map;

/** A batch of like records keyed by moment id. */
public class PraiseResponse {
    private Map<String, List<MomentLikeVo>> momentLikeMap;

    public Map<String, List<MomentLikeVo>> getMomentLikeMap() {
        return this.momentLikeMap;
    }

    public void setMomentLikeMap(Map<String, List<MomentLikeVo>> momentLikeMap) {
        this.momentLikeMap = momentLikeMap;
    }
}

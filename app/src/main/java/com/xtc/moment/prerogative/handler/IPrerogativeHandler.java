package com.xtc.moment.prerogative.handler;

import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;

import java.util.HashMap;
import java.util.List;

/**
 * 特权资源处理器接口：负责动效资源的下载、解压、入库与本地状态刷新。
 */
public interface IPrerogativeHandler<T> {
    List<T> getAllLocalData(boolean refreshCurrentUse);

    int getCurrentUseEmotionId();

    T getPrerogativeByEmotionId(int emotionId);

    boolean hasOverdueData();

    Boolean initPrerogativeResource(ResourceNetResponse resourceNetResponse);

    void refreshLocalPrerogativeData(HashMap<Integer, EmotionsEntity> emotions);
}
package com.xtc.moment.prerogative.handler;

import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;

import java.util.HashMap;
import java.util.List;

/**
 * 特权资源处理器接口。
 */
public interface IPrerogativeHandler<T> {

    Boolean initPrerogativeResource(ResourceNetResponse resource);

    void refreshLocalPrerogativeData(HashMap<Integer, EmotionsEntity> emotions);

    List<T> getAllLocalData(boolean onlyValid);

    T getPrerogativeByEmotionId(int emotionId);

    int getCurrentUseEmotionId();

    boolean hasOverdueData();
}
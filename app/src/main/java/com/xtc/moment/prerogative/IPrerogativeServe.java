package com.xtc.moment.prerogative;

import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;

/**
 * 特权装扮服务接口。
 */
public interface IPrerogativeServe {

    void initPrerogativeResource(InitPrerogativeCallback callback);

    int getCurrentUseLikeEmotionId();

    int getCurrentUseBackgroundEmotionId();

    DbMomentPrerogativeLike getPrerogativeLikeByEmotionId(int emotionId);

    DbMomentPrerogativeBackground getPrerogativeBackgroundByEmotionId(int emotionId);
}
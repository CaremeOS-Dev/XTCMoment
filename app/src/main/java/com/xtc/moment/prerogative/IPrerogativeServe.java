package com.xtc.moment.prerogative;

import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;

/**
 * 特权服务接口：查询当前使用的点赞/背景动效资源。
 */
public interface IPrerogativeServe {
    int getCurrentUseBackgroundEmotionId();

    int getCurrentUseLikeEmotionId();

    DbMomentPrerogativeBackground getPrerogativeBackgroundByEmotionId(int emotionId);

    DbMomentPrerogativeLike getPrerogativeLikeByEmotionId(int emotionId);

    void initPrerogativeResource(InitPrerogativeCallback callback);
}
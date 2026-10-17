package com.xtc.moment.module.favorite;

import com.xtc.moment.base.IBaseInteractView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 收藏的动态页面的视图接口。
 */
public interface IFavoriteMomentView extends IBaseInteractView {

    /**
     * 收藏列表加载完成。
     *
     * @param moments       收藏的动态，按收藏时间倒序；已删除的用快照数据渲染
     * @param praiseRecords 点赞记录，已删除的动态取快照里的记录
     * @param deletedIds    已经被删除的动态 id，界面据此展示「该动态已被删除」并禁用互动
     */
    void onFavoritesLoaded(List<DbMoment> moments, Map<String, List<DbLikeMessage>> praiseRecords,
            Set<String> deletedIds);
}

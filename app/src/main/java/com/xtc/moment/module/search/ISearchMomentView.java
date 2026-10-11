package com.xtc.moment.module.search;

import android.view.View;

import com.xtc.moment.base.IBaseInteractView;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;

import java.util.List;
import java.util.Map;

/**
 * 搜索动态页面的视图接口。
 */
public interface ISearchMomentView extends IBaseInteractView {

    /** 开始一次新的搜索（用于重置列表与展示加载态）。 */
    void onSearchStart(String keyword);

    /**
     * 返回一页搜索结果。
     *
     * @param firstPage  是否是本次搜索的第一页（true 时替换列表）
     * @param reachedEnd 是否已经扫描到底
     */
    void onSearchPage(List<DbMoment> moments, Map<String, List<DbLikeMessage>> praiseRecords,
            boolean firstPage, boolean reachedEnd);

    /** 关键词为空时的提示。 */
    void onSearchKeywordEmpty();

    View getBackgroundView();
}

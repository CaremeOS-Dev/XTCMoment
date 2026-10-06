package com.xtc.aitext.activity.view;

import com.xtc.aitext.bean.AIRecordDetailBean;
import com.xtc.architecture.mvp.core.MvpView;

import java.util.List;

/**
 * AI 创作记录页视图接口。
 */
public interface IAIRecordView extends MvpView {

    /** 展示创作记录。 */
    void showRecords(List<AIRecordDetailBean> recordList);

    /** 展示空数据。 */
    void showEmpty();
}
package com.xtc.aitext.presenter;

import android.content.Context;

import com.xtc.aitext.activity.view.IAIRecordView;
import com.xtc.aitext.bean.AIRecordBean;
import com.xtc.aitext.bean.MainDataBody;
import com.xtc.aitext.manager.AITextManager;
import com.xtc.aitext.net.AINetErrorAction;
import com.xtc.aitext.net.http.AITextHttpProxy;
import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * AI 创作记录页 Presenter，负责分页查询创作记录。
 */
public class AIRecordPresenter extends MvpPresenter<IAIRecordView> {

    private static final String TAG = "ai_text_AIRecordPresenter";
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final Context context;

    private int nowPage;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int totalRecord;
    private int totalPage;

    public AIRecordPresenter(Context context) {
        this.context = context;
    }

    /** 查询创作记录。 */
    public void queryRecord(boolean isFirstLoad) {
        LogUtil.d(TAG, "queryRecord: isFirstLoad = " + isFirstLoad);
        if (!isFirstLoad && isNoMore()) {
            return;
        }
        this.nowPage++;
        MainDataBody body = new MainDataBody();
        body.setNowPage(this.nowPage);
        body.setPageSize(this.pageSize);
        body.setClientType(AITextManager.getInstance(context).getConfig().getClientType());
        AITextHttpProxy.getInstance(context).queryRecord(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AIRecordBean>() {
                    @Override
                    public void call(AIRecordBean recordBean) {
                        LogUtil.d(TAG, "call: queryRecord = " + recordBean);
                        handleRecordResult(recordBean);
                    }
                }, new AINetErrorAction(getClass(), new AINetErrorAction.ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                    }
                }, "queryRecord"));
    }

    private void handleRecordResult(AIRecordBean recordBean) {
        if (recordBean == null || recordBean.getPage() == null) {
            return;
        }
        this.nowPage = recordBean.getPage().getNowPage();
        this.pageSize = recordBean.getPage().getPageSize();
        this.totalRecord = recordBean.getPage().getTotalRecord();
        this.totalPage = recordBean.getPage().getTotalPage();
        if (CollectionUtil.isEmpty(recordBean.getUserAiText()) || getView() == null) {
            return;
        }
        getView().showRecords(recordBean.getUserAiText());
    }

    /** 是否已无更多数据。 */
    public boolean isNoMore() {
        LogUtil.d(TAG, "isNoMore: nowPage = " + this.nowPage + ",pageSize  = " + this.pageSize
                + ",totalRecord = " + this.totalRecord);
        return this.nowPage * this.pageSize >= this.totalRecord;
    }
}
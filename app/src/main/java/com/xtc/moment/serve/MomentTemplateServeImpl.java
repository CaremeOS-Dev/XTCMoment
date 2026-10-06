package com.xtc.moment.serve;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.db.dao.MomentTemplateDao;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.TemplateResponseBean;
import com.xtc.utils.common.CollectionUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 动态模板服务：模板的本地缓存读写与远端拉取。
 */
public class MomentTemplateServeImpl extends BaseServe implements IMomentTemplateServe {

    private static final String TAG = "MomentTemplateServeImpl";

    private MomentHttpServiceProxy momentHttpServiceProxy;
    private MomentTemplateDao momentTemplateDao;

    private MomentTemplateServeImpl(Context context) {
        super(context);
        ServerCache.putBusinessServer(this);
        this.momentTemplateDao = ServerCache.getDao(context, MomentTemplateDao.class);
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
    }

    public static IMomentTemplateServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, MomentTemplateServeImpl.class);
    }

    @Override
    public Observable<TemplateResponseBean> getMomentTemplateFromNet(long from, long size, long updateId) {
        return this.momentHttpServiceProxy.getMomentTemplate(from, size, updateId);
    }

    @Override
    public Observable<List<DbTemplate>> getTemplatesByTypeFromDb(final int type) {
        return Observable.create(new Observable.OnSubscribe<List<DbTemplate>>() {
            @Override
            public void call(Subscriber<? super List<DbTemplate>> subscriber) {
                subscriber.onNext(MomentTemplateServeImpl.this.momentTemplateDao.queryTemlatesByType(type));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public void updateTemplates(List<DbTemplate> templates) {
        if (templates == null || templates.isEmpty()) {
            LogUtil.w(TAG, "updateTemplate:dbTemplateList is null");
            return;
        }
        LogUtil.i(TAG, "updateTemplates size:" + templates.size());
        this.momentTemplateDao.updateTemplates(templates);
    }

    @Override
    public int deleteAllTemplates() {
        return this.momentTemplateDao.deleteAll();
    }

    @Override
    public DbTemplate getTemplateByContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        List<DbTemplate> templates = this.momentTemplateDao.getTemplateByContent(content);
        if (CollectionUtil.isEmpty(templates)) {
            return null;
        }
        return templates.get(0);
    }

    @Override
    public long getTemplatesCount() {
        return this.momentTemplateDao.getCount();
    }
}
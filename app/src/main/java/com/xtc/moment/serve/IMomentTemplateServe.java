package com.xtc.moment.serve;

import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.net.bean.TemplateResponseBean;

import java.util.List;

import rx.Observable;

/**
 * 动态模板服务接口。
 */
public interface IMomentTemplateServe {

    Observable<TemplateResponseBean> getMomentTemplateFromNet(long type, long startTime, long endTime);

    Observable<List<DbTemplate>> getTemplatesByTypeFromDb(int type);

    void updateTemplates(List<DbTemplate> templates);

    DbTemplate getTemplateByContent(String content);

    long getTemplatesCount();

    int deleteAllTemplates();
}
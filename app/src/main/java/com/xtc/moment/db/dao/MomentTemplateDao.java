package com.xtc.moment.db.dao;

import android.content.Context;

import com.j256.ormlite.misc.TransactionManager;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.module.report.ReportActivity;
import com.xtc.moment.serve.ServerCache;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 动态模板 DAO。
 */
public class MomentTemplateDao extends OrmLiteDao<DbTemplate> {

    private static final String TAG = "MomentTemplateDao";

    public MomentTemplateDao(Context context) {
        super(context, DbTemplate.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public List<DbTemplate> queryTemlatesByType(int type) {
        return queryByColumnName("type", Integer.valueOf(type));
    }

    public List<DbTemplate> queryTemplateByResourceId(int resourceId) {
        return queryByColumnName("resourceId", Integer.valueOf(resourceId));
    }

    public void updateTemplates(final List<DbTemplate> templates) {
        try {
            new TransactionManager(this.ormLiteDao.getConnectionSource()).callInTransaction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    for (DbTemplate template : templates) {
                        List<DbTemplate> existing = queryByColumnName("resourceId",
                                Integer.valueOf(template.getResourceId()));
                        if (existing == null || existing.isEmpty()) {
                            insert(template);
                        } else {
                            updateBy(template, "resourceId", Integer.valueOf(template.getResourceId()));
                        }
                    }
                    return null;
                }
            });
            LogUtil.d(TAG, "updateTemplates successfully");
        } catch (SQLException e) {
            LogUtil.e(TAG, e);
        }
    }

    public List<DbTemplate> getTemplateByContent(String content) {
        return queryByColumnName(ReportActivity.CONTENTS, content);
    }
}
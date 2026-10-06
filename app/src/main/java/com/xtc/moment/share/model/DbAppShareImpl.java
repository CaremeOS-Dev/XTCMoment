package com.xtc.moment.share.model;

import android.content.Context;

import com.xtc.database.ormlite.RxDao;
import com.xtc.moment.db.Constants;
import com.xtc.moment.share.model.bean.DbAppShare;

/**
 * 应用分享本地数据访问。
 */
public class DbAppShareImpl {

    private final RxDao<DbAppShare> appShareDao;

    public DbAppShareImpl(Context context) {
        this.appShareDao = new RxDao<>(context, DbAppShare.class, Constants.DATABASE_NAME);
    }

    public DbAppShare queryApp(String packageName) {
        return this.appShareDao.queryForFirst("packageName", packageName);
    }

    public void insertApp(DbAppShare appShare) {
        this.appShareDao.insert(appShare);
    }

    public void updateApp(DbAppShare appShare) {
        this.appShareDao.updateBy(appShare, "packageName", appShare.getPackageName());
    }

    public void updateToken(String packageName, String token) {
        DbAppShare appShare = queryApp(packageName);
        if (appShare != null) {
            appShare.setToken(token);
            updateApp(appShare);
        }
    }
}
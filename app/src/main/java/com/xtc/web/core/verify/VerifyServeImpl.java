package com.xtc.web.core.verify;

import android.content.Context;
import android.util.Log;

import com.xtc.database.ormlite.CollectionUtil;

import java.util.List;

/** 白名单本地存储实现，读写 verify.db 中的白名单表。 */
public class VerifyServeImpl implements IVerifyServe {

    private static final String TAG = "VerifyServeImpl";
    private static VerifyServeImpl instance;
    private Context context;
    private VerifyDao verifyDao;

    public static VerifyServeImpl getInstance(Context context) {
        if (instance == null) {
            instance = new VerifyServeImpl(context);
        }
        return instance;
    }

    public VerifyServeImpl(Context context) {
        this.context = context;
        this.verifyDao = new VerifyDao(context);
    }

    @Override
    public DbVerify queryWhiteDatas() {
        List<DbVerify> verifyList = this.verifyDao.queryForAll();
        if (CollectionUtil.isEmpty(verifyList)) {
            return null;
        }
        return verifyList.get(0);
    }

    @Override
    public DbVerify updateWhiteDatas(DbVerify dbVerify) {
        if (dbVerify == null) {
            Log.i(TAG, "updateWhiteDatas, dbVerify is empty");
            return null;
        }
        this.verifyDao.clearTableData();
        if (this.verifyDao.insert(dbVerify)) {
            return dbVerify;
        }
        Log.i(TAG, "update dbVerify error");
        return null;
    }
}
package com.xtc.web.core.verify;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;

/** 白名单表的 DAO。 */
public class VerifyDao extends OrmLiteDao<DbVerify> {

    public VerifyDao(Context context) {
        super(context, DbVerify.class, Constants.TableName.SHARE_WHITE_TABLE_NAME);
    }
}
package com.xtc.moment.db;

import android.content.Context;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbAdvertCloseRecord;
import com.xtc.moment.db.bean.DbGiftRecord;
import com.xtc.moment.db.bean.DbHead;
import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbIllegalRecord;
import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.db.bean.DbNickname;
import com.xtc.moment.db.bean.DbReminder;
import com.xtc.moment.db.bean.DbSexyPhotoDistinguish;
import com.xtc.moment.db.bean.DbTemplate;
import com.xtc.moment.db.bean.DbVisible;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.share.model.bean.DbAppShare;
import com.xtc.virtualselfapi.helper.VirtualSelfDbHelper;
import com.xtc.web.client.db.DbWebUrl;

/** Owns the moment database and registers every entity table. */
public class MomentDbManager {

    private static final int DATABASE_VERSION = 1;
    private static final String TAG = MomentDbManager.class.getSimpleName();

    private static volatile MomentDbManager instance;

    private final DatabaseHelper databaseHelper;

    private MomentDbManager(Context context) {
        this.databaseHelper = DatabaseHelper.getInstance(context, Constants.DATABASE_NAME);
    }

    public static MomentDbManager getInstance(Context context) {
        MomentDbManager manager = instance;
        if (manager == null) {
            synchronized (MomentDbManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new MomentDbManager(context);
                    manager.registerTable();
                    instance = manager;
                }
            }
        }
        return manager;
    }

    public DatabaseHelper getDatabaseHelper() {
        return this.databaseHelper;
    }

    /** Registers every entity table of the app. */
    protected void registerTable() {
        LogUtil.d(TAG, "registerTable: start");
        this.databaseHelper.registerTable(DbLikeMessage.class);
        this.databaseHelper.registerTable(DbMoment.class);
        this.databaseHelper.registerTable(DbMomentComment.class);
        this.databaseHelper.registerTable(DbSexyPhotoDistinguish.class);
        this.databaseHelper.registerTable(DbTemplate.class);
        this.databaseHelper.registerTable(DbAppShare.class);
        this.databaseHelper.registerTable(DbNickname.class);
        this.databaseHelper.registerTable(DbHead.class);
        this.databaseHelper.registerTable(DbWebUrl.class);
        this.databaseHelper.registerTable(DbGiftRecord.class);
        this.databaseHelper.registerTable(DbIllegalRecord.class);
        this.databaseHelper.registerTable(DbAdvertCloseRecord.class);
        this.databaseHelper.registerTable(DbMomentPrerogativeLike.class);
        this.databaseHelper.registerTable(DbMomentPrerogativeBackground.class);
        this.databaseHelper.registerTable(DbVisible.class);
        this.databaseHelper.registerTable(DbReminder.class);
        this.databaseHelper.registerTable(DbIMReminder.class);
        VirtualSelfDbHelper.registerTable(this.databaseHelper);
        LogUtil.d(TAG, "registerTable: finish");
    }
}
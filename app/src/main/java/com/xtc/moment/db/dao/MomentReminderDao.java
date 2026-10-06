package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbReminder;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.serve.ServerCache;

import java.util.List;

/**
 * 动态提醒配置 DAO。
 */
public class MomentReminderDao extends OrmLiteDao<DbReminder> {

    private static final String TAG = "MomentReminderDao";

    public MomentReminderDao(Context context) {
        super(context, DbReminder.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public boolean insertReminders(List<DbReminder> reminders) {
        List<DbReminder> oldReminders = queryAllReminders();
        if (oldReminders != null && !oldReminders.isEmpty()) {
            LogUtil.w(ReminderHelper.M_TAG, "先删除旧Reminders数据");
            clearTableData();
        }
        return insertForBatch(reminders);
    }

    public DbReminder queryReminderByLabel(String label) {
        return queryForFirst(DbReminder.REMINDER_LABEL, label);
    }

    public List<DbReminder> queryAllReminders() {
        return queryForAll();
    }
}
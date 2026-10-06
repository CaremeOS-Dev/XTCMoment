package com.xtc.moment.db.dao;

import android.content.Context;

import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.database.ormlite.OrmLiteDao;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.bean.DbIMReminder;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.serve.ServerCache;

import java.util.List;

/**
 * IM 动态提醒 DAO。
 */
public class MomentIMReminderDao extends OrmLiteDao<DbIMReminder> {

    public MomentIMReminderDao(Context context) {
        super(context, DbIMReminder.class, Constants.DATABASE_NAME);
        ServerCache.putDao(this);
    }

    public boolean insertIMReminder(DbIMReminder reminder) {
        return insert(reminder);
    }

    public void updateIMReminderStatus(String momentId, String status) {
        DbIMReminder reminder = queryForFirst("momentId", momentId);
        if (reminder == null) {
            return;
        }
        reminder.setStatus(status);
        update(reminder);
        LogUtil.v(ReminderHelper.M_TAG, "updateImReminder: " + reminder.toString());
    }

    public void batchUpdateIMReminderStatus(List<DbMoment> moments, String status) {
        if (CollectionUtil.isEmpty(moments)) {
            return;
        }
        for (DbMoment moment : moments) {
            updateIMReminderStatus(moment.getMomentId(), status);
        }
    }

    public List<DbIMReminder> queryIMRemindersByStatus(String status) {
        return queryByColumnName("status", status);
    }

    public void deleteIMReminders(int type) {
        boolean deleted;
        String message;
        if (type == 1) {
            int count = deleteLtValue(DbIMReminder.IM_REMINDER_INSERT_TIME, Long.valueOf(System.currentTimeMillis()));
            deleted = count > 0;
            message = "delete overtime success: " + count;
        } else {
            deleted = deleteByColumnName("status", DbIMReminder.NOT_MATCH);
            message = "delete not match success";
        }
        if (deleted) {
            LogUtil.w(ReminderHelper.M_TAG, message);
        }
    }
}
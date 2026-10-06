package com.xtc.system.account;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xtc.system.account.bean.StepCounter;
import com.xtc.utils.storage.ShareDBHelper;

/**
 * 运动数据读取：从运动 App 的共享数据库与 ContentProvider 读取步数。
 */
public class MotionData {

    public static StepCounter getMotionData() {
        Cursor cursor = ShareDBHelper.getInstance("com.xtc.motion", "motion.db").rawQuery("select * from motion_state order by id desc limit 1 offset 0");
        StepCounter stepCounter = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                stepCounter = new StepCounter();
                stepCounter.setDateToDay(cursor.getString(cursor.getColumnIndex("dateToday")));
                stepCounter.setTotalStep(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("totalStep"))));
                stepCounter.setTotalDistance(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("totalDistance"))));
            }
            cursor.close();
            ShareDBHelper.getInstance("com.xtc.motion", "motion.db").close();
        }
        return stepCounter;
    }

    public static int getMotionStatus(Context context) {
        try {
            return Integer.valueOf(context.getContentResolver().getType(Uri.parse("content://com.xtc.motion/motion_status"))).intValue();
        } catch (Exception e) {
            return 1;
        }
    }
}
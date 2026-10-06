package com.xtc.web.client.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xtc.web.client.data.response.RespMotion;
import com.xtc.web.core.callback.CompletionHandler;

/** 运动数据查询：今日步数与运动状态。 */
public class MotionManager {

    /** 查询今日步数。 */
    public void requestTodayStep(Context context, CompletionHandler<RespMotion> completionHandler) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Uri.parse("content://com.xtc.motion/step"), null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                int step = cursor.getInt(cursor.getColumnIndex("step"));
                RespMotion response = new RespMotion();
                response.setCode(RespMotion.Code.SUCCESS);
                response.setData(step);
                completionHandler.complete(response);
            }
        } catch (Exception ignored) {
            RespMotion response = new RespMotion();
            response.setCode(RespMotion.Code.FAIL);
            completionHandler.complete(response);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /** 查询运动状态。 */
    public void requestMotionStatus(Context context, CompletionHandler<RespMotion> completionHandler) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Uri.parse("content://com.xtc.motion/motionState"), null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                int motionState = cursor.getInt(cursor.getColumnIndex("motionState"));
                RespMotion response = new RespMotion();
                response.setCode(RespMotion.Code.SUCCESS);
                response.setData(motionState);
                completionHandler.complete(response);
            }
        } catch (Exception ignored) {
            RespMotion response = new RespMotion();
            response.setCode(RespMotion.Code.FAIL);
            completionHandler.complete(response);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
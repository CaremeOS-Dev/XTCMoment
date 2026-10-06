package com.xtc.moment.serve.delete;

import android.content.Context;

import com.xtc.moment.db.bean.DbMoment;

/**
 * 动态删除策略。
 */
public interface IMomentDeleteStrategy {
    boolean delete(Context context, DbMoment moment);
}
package com.xtc.moment.serve.delete;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbMoment;

/**
 * 动态删除入口：按动态类型选择删除策略。
 */
public class MomentDeleteServe {

    private static final String TAG = MomentDeleteServe.class.getSimpleName();
    private static volatile MomentDeleteServe mInstance;

    private Context context;

    public static MomentDeleteServe getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MomentDeleteServe.class) {
                if (mInstance == null) {
                    mInstance = new MomentDeleteServe(context);
                }
            }
        }
        return mInstance;
    }

    private MomentDeleteServe(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean delete(DbMoment moment) {
        LogUtil.i(TAG, "start delete DbMoment: " + moment);
        IMomentDeleteStrategy strategy;
        int type = moment.getType().intValue();
        if (type == 22 || type == 23) {
            strategy = new LivePhotoMomentDeleteStrategy(this.context);
        } else {
            strategy = new CommonMomentDeleteStrategy(this.context);
        }
        return strategy.delete(this.context, moment);
    }
}
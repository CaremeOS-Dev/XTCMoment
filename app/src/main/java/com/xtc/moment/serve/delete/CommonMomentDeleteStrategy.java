package com.xtc.moment.serve.delete;

import android.content.Context;

import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;

/**
 * 普通动态删除策略：直接删除本地数据库记录。
 */
public class CommonMomentDeleteStrategy implements IMomentDeleteStrategy {

    private IMomentServe iMomentServe;
    private Context mContext;

    public CommonMomentDeleteStrategy(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
    }

    @Override
    public boolean delete(Context context, DbMoment moment) {
        return this.iMomentServe.deleteDBMomentByMomentId(moment);
    }
}
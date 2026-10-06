package com.xtc.moment.data.local;

import android.content.Context;

import com.xtc.moment.db.bean.DbLikeMessage;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;

/**
 * 点赞相关的本地数据源，全部转发给 {@link IMomentServe}。
 */
public class MomentLikeLocalDataSource {

    private static volatile MomentLikeLocalDataSource INSTANCE = null;
    private static final String TAG = "MomentLikeLocalDataSour";

    private IMomentServe iMomentServe;
    private Context mContext;

    public MomentLikeLocalDataSource(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
    }

    public static MomentLikeLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MomentsLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentLikeLocalDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public boolean addLikeMessage(DbLikeMessage likeMessage) {
        return this.iMomentServe.addLikeMessage(likeMessage);
    }

    public void updateMomentByMomentId(String momentId) {
        this.iMomentServe.updateMomentByMomentId(momentId);
    }

    public DbMoment deleteLikeDaoByMomentId(String momentId, String watchId) {
        return this.iMomentServe.deleteLikeDaoByMomentId(momentId, watchId);
    }
}
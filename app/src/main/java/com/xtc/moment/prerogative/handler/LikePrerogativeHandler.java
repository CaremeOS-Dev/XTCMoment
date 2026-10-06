package com.xtc.moment.prerogative.handler;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.db.dao.prerogative.IPrerogativeDao;
import com.xtc.moment.db.dao.prerogative.MomentPrerogativeLikeDao;
import com.xtc.moment.module.prerogative.bean.LocalLikeDescInfoData;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.SharedTool;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 点赞动效特权处理器：负责点赞动效资源与数据库记录的同步。
 */
public class LikePrerogativeHandler extends BasePrerogativeHandler<DbMomentPrerogativeLike> {

    private static final String TAG = "LikePrerogativeHandler";

    @Override
    protected String getTag() {
        return TAG;
    }

    public LikePrerogativeHandler(Context context) {
        super(context);
        this.mPrerogativeDao = ServerCache.getDao(context, MomentPrerogativeLikeDao.class);
    }

    @Override
    protected String getPrerogativeRootPath() {
        return FileManager.getMomentLikeRootPath();
    }

    @Override
    protected String getPrerogativeResourceSpData() {
        return SharedTool.getPrerogativeLikeData(this.mContext);
    }

    @Override
    protected void savePrerogativeResourceData(ResourceNetResponse resourceNetResponse) {
        SharedTool.savePrerogativeLikeData(this.mContext, JSONUtil.toJSON(resourceNetResponse));
    }

    @Override
    protected List<DbMomentPrerogativeLike> readDescInfo(File file) {
        LocalLikeDescInfoData infoData;
        try {
            infoData = JSONUtil.fromJSON(FileUtils.readString(file, "UTF-8"), LocalLikeDescInfoData.class);
        } catch (Exception e) {
            LogUtil.e(getTag(), "readDescInfo#error", e);
            infoData = null;
        }
        if (infoData == null) {
            LogUtil.e(TAG, "infoData is null");
            return null;
        }
        return infoData.getEmotions();
    }
}
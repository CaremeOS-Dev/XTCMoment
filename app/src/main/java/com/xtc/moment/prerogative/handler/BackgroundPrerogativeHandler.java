package com.xtc.moment.prerogative.handler;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.dao.prerogative.IPrerogativeDao;
import com.xtc.moment.db.dao.prerogative.MomentPrerogativeBackgroundDao;
import com.xtc.moment.module.prerogative.bean.LocalBackgroundDescInfoData;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.SharedTool;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import java.io.File;
import java.util.List;

/**
 * 动态背景特权处理器：负责背景动效资源与数据库记录的同步。
 */
public class BackgroundPrerogativeHandler extends BasePrerogativeHandler<DbMomentPrerogativeBackground> {

    private static final String TAG = "BackgroundPrerogativeHandler";

    @Override
    protected String getTag() {
        return TAG;
    }

    public BackgroundPrerogativeHandler(Context context) {
        super(context);
        this.mPrerogativeDao = ServerCache.getDao(context, MomentPrerogativeBackgroundDao.class);
    }

    @Override
    protected String getPrerogativeRootPath() {
        return FileManager.getMomentBackgroundRootPath();
    }

    @Override
    protected String getPrerogativeResourceSpData() {
        return SharedTool.getPrerogativeBackgroundData(this.mContext);
    }

    @Override
    protected void savePrerogativeResourceData(ResourceNetResponse resourceNetResponse) {
        SharedTool.savePrerogativeBackgroundData(this.mContext, JSONUtil.toJSON(resourceNetResponse));
    }

    @Override
    protected List<DbMomentPrerogativeBackground> readDescInfo(File file) {
        LocalBackgroundDescInfoData infoData;
        try {
            infoData = JSONUtil.fromJSON(FileUtils.readString(file, "UTF-8"), LocalBackgroundDescInfoData.class);
        } catch (Exception e) {
            LogUtil.e(getTag(), "readDescInfo#error", e);
            infoData = null;
        }
        if (infoData == null) {
            LogUtil.e(TAG, "infoData is null");
            return null;
        }
        return infoData.getBackgrounds();
    }
}
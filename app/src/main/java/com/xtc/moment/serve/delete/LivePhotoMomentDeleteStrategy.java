package com.xtc.moment.serve.delete;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.FileManager;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;

import java.util.Objects;

/**
 * 实况照片删除策略：自己发布的直接删记录；他人发布的需先删掉本地缓存图片与视频再删记录。
 */
public class LivePhotoMomentDeleteStrategy implements IMomentDeleteStrategy {

    private static final String TAG = LivePhotoMomentDeleteStrategy.class.getSimpleName();

    private IMomentServe iMomentServe;
    private Context mContext;

    public LivePhotoMomentDeleteStrategy(Context context) {
        this.mContext = context;
        this.iMomentServe = MomentServeImpl.getInstance(context);
    }

    @Override
    public boolean delete(Context context, DbMoment moment) {
        if (Objects.equals(MomentApp.getWatchId(), moment.getWatchId())) {
            boolean messageDeleted = this.iMomentServe.deleteDBMomentByMomentId(moment);
            LogUtil.i(TAG, "delete: messageDelResult: " + messageDeleted + ", DbMoment: " + moment);
            return messageDeleted;
        }
        LivePhotoMsg livePhotoMsg = JSONUtil.fromJSON(moment.getContent(), LivePhotoMsg.class);
        boolean messageDeleted = false;
        if (livePhotoMsg == null) {
            LogUtil.i(TAG, "LivePhotoMomentDeleteStrategy imVideoMsg == null. DbMoment:" + moment);
            return false;
        }
        String localPath = livePhotoMsg.getLocalPath();
        VideoMsg videoMsg = livePhotoMsg.getVideoMsg();
        boolean thumbnailDeleted = FileUtils.exists(localPath) ? FileUtils.deleteFile(localPath) : true;
        String localVideoPath = videoMsg.getLocalVideoPath();
        if (!TextUtils.isEmpty(localVideoPath) && FileUtils.exists(localVideoPath)) {
            return false;
        }
        CloudFileResource transfer = videoMsg.getTransfer();
        if (transfer != null) {
            localVideoPath = FileManager.getLivePhotoCachePath() + transfer.getKey();
            if (!FileUtils.exists(localVideoPath)) {
                localVideoPath = videoMsg.getTransfer().getDownloadUrl();
            }
        }
        boolean videoDeleted = FileUtils.exists(localVideoPath) ? FileUtils.deleteFile(localVideoPath) : true;
        if (thumbnailDeleted && videoDeleted) {
            messageDeleted = this.iMomentServe.deleteDBMomentByMomentId(moment);
        }
        LogUtil.i(TAG, "delete: deleteThumnailResult: " + thumbnailDeleted + ", deleteVideoResult: " + videoDeleted + " msgDelResult: " + messageDeleted + ", DbMoment： " + moment);
        return messageDeleted;
    }
}
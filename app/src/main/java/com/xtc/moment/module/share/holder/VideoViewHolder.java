package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.ShareVideoMoment;
import com.xtc.moment.module.bean.VideoKeyOrToken;
import com.xtc.moment.module.bean.VideoMsg;
import com.xtc.moment.module.report.adapter.AbsInteractionAdapter;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.utils.encode.JSONUtil;

import java.io.File;

/**
 * 视频动态的 ViewHolder，负责选择本地缩略图或网络缩略图加载路径。
 */
public class VideoViewHolder extends PhotoViewHolder {

    private static final String TAG = "VideoViewHolder";

    private IAccountInfoServe accountInfoServe;
    private boolean isSelf;
    private String selfWatchId;
    public ImageView videoPlayLogo;

    public VideoViewHolder(View view) {
        super(view);
        this.videoPlayLogo = (ImageView) view.findViewById(R.id.iv_share_item_video_logo);
        this.ivContent = (ImageView) view.findViewById(R.id.chat_msg_item_photo_iv);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        if (this.ivContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        this.accountInfoServe = AccountInfoServerImpl.getInstance(context);
        this.selfWatchId = this.accountInfoServe.getWatchAccountInfo().getWatchId(context);
        this.isSelf = this.selfWatchId != null && this.selfWatchId.equals(moment.getWatchId());
        boolean roundedCorner = moment.getType().intValue() != 9;
        if (11 == moment.getType().intValue()) {
            if (TextUtils.isEmpty(moment.getResource())) {
                return;
            }
            configAndLoadAdvantisePhoto(getHolderContext(), moment, this.ivContent);
            return;
        }
        String content = moment.getContent();
        String resource = moment.getResource();
        LogUtil.i(TAG, "loadImage#momentBean: " + moment + "\n videoKeyOrToken :"
                + JSONUtil.fromJSON(moment.getResource(), VideoKeyOrToken.class));
        if (content != null && !TextUtils.isEmpty(content)) {
            if (content.contains("source")) {
                dealLoadView(context, moment, holder, roundedCorner, content, resource);
                return;
            }
            ShareVideoMoment shareVideoMoment = JSONUtil.fromJSON(moment.getContent(), ShareVideoMoment.class);
            if (!TextUtils.isEmpty(shareVideoMoment.getLocalThumbnailPath())) {
                LogUtil.d(TAG, "shareVideoMoment.getLocalPath():" + shareVideoMoment.getLocalThumbnailPath());
                if (!new File(shareVideoMoment.getLocalThumbnailPath()).exists()) {
                    LogUtil.d(TAG, "!photoFile.exists()");
                    getVideoPic(context, moment, holder, roundedCorner, resource, shareVideoMoment.getSource());
                    return;
                }
                LogUtil.d(TAG, "load local image!!");
                PhotoMsg photoMsg = new PhotoMsg();
                photoMsg.setLocalPath(shareVideoMoment.getLocalThumbnailPath());
                loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
                LogUtil.d(TAG, "load local image complete!!!");
                return;
            }
            LogUtil.d(TAG, "photoMsg.getLocalPath()==empty");
            getVideoPic(context, moment, holder, roundedCorner, resource, shareVideoMoment.getSource());
            return;
        }
        loadImageWithKey(getHolderContext(), this.ivContent, resource, "1", moment, roundedCorner, holder);
    }

    private void getVideoPic(Context context, DbMoment moment, AbsViewHolder holder, boolean roundedCorner,
            String key, CloudFileResource source) {
        if (source == null) {
            loadImageWithKey(getHolderContext(), this.ivContent, key, "1", moment, roundedCorner, holder);
        } else {
            dislplayNetPhoto(context, key, this.ivContent, roundedCorner, moment, holder);
        }
    }

    private void dealLoadView(Context context, DbMoment moment, AbsViewHolder holder, boolean roundedCorner,
            String content, String resource) {
        VideoMsg videoMsg = JSONUtil.fromJSON(content, VideoMsg.class);
        if (videoMsg == null) {
            LogUtil.d(TAG, "videoMsg == null");
            loadImageWithKey(getHolderContext(), this.ivContent, resource, "1", moment, roundedCorner, holder);
            return;
        }
        LogUtil.d(TAG, "VideoMsg = " + videoMsg);
        if (!TextUtils.isEmpty(videoMsg.getLocalThumbnailPath())) {
            LogUtil.d(TAG, "videoMsg.getLocalPath():" + videoMsg.getLocalThumbnailPath());
            if (!new File(videoMsg.getLocalThumbnailPath()).exists()) {
                LogUtil.d(TAG, "!videoMsg.exists()");
                getVideoPic(context, moment, holder, roundedCorner, resource, videoMsg.getSource());
                return;
            }
            LogUtil.d(TAG, "load local image!!");
            PhotoMsg photoMsg = new PhotoMsg();
            photoMsg.setLocalPath(videoMsg.getLocalThumbnailPath());
            loadDiskPhoto(photoMsg, roundedCorner, moment, holder);
            LogUtil.d(TAG, "load local image complete!!!");
            return;
        }
        LogUtil.d(TAG, "videoMsg.getLocalPath()==empty");
        getVideoPic(context, moment, holder, roundedCorner, resource, videoMsg.getSource());
    }

    @Override
    public void setContentOnLongClickListener(final DbMoment moment, final ShareAdapter.OnContentOnLongClickListener listener) {
        super.setContentOnLongClickListener(moment, listener);
        this.ivContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                VideoViewHolder.this.toReportDelete(listener, moment);
                return true;
            }
        });
    }

    public void toReportDelete(ShareAdapter.OnContentOnLongClickListener listener, final DbMoment moment) {
        if (!this.isSelf) {
            showReportBtnDialog(new AbsInteractionAdapter.IOnDialogClickLister() {
                @Override
                public void onRightBtnClick() {
                    VideoViewHolder.this.startReportActivity(moment.getWatchId(), moment.getMomentId(), "");
                }
            });
        } else if (listener != null) {
            listener.deleteItem(moment);
        }
    }
}
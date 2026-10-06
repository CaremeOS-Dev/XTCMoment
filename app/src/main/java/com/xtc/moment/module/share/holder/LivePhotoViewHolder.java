package com.xtc.moment.module.share.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.share.adapter.ShareAdapter;
import com.xtc.utils.encode.JSONUtil;

/**
 * 实况照片动态的 ViewHolder，点击预览实况照片。
 */
public class LivePhotoViewHolder extends PhotoViewHolder {

    private static final String TAG = "LivePhotoViewHolder";

    public LivePhotoViewHolder(View view) {
        super(view);
    }

    @Override
    public void loadImage(Context context, DbMoment moment, AbsViewHolder holder) {
        super.loadImage(context, moment, holder);
    }

    @Override
    public void setContentOnClickListener(final DbMoment moment, final ShareAdapter.OnContentOnClickListener listener) {
        if (this.ivContent == null) {
            LogUtil.w(TAG, "LivePhotoViewHolder#setContentOnClickListener: ivContent is null");
            return;
        }
        this.ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moment == null) {
                    LogUtil.w(LivePhotoViewHolder.TAG, "LivePhotoViewHolder#onClick: dbMoment is null");
                    return;
                }
                String content = moment.getContent();
                if (content != null && !TextUtils.isEmpty(content)) {
                    LivePhotoMsg livePhotoMsg = JSONUtil.fromJSON(content, LivePhotoMsg.class);
                    if (listener != null) {
                        listener.previewLivePhoto(livePhotoMsg);
                    }
                    return;
                }
                LivePhotoViewHolder.this.getPhotoUrl(LivePhotoViewHolder.this.getHolderContext(), moment.getResource(),
                        "1", moment, listener);
            }
        });
    }
}
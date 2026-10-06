package com.xtc.moment.module.widget.livephotoView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.ImageView;

/**
 * 实况照片播放用的 TextureView，支持播放前先展示缩略图。
 */
public class PlayVideoTextureView extends TextureView {

    public PlayVideoTextureView(Context context) {
        super(context);
    }

    public PlayVideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayVideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PlayVideoTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setThumbnailBeforePlay(ImageView thumbnail, Handler handler, long delayMillis, Runnable action) {
        setVisibility(GONE);
        thumbnail.setVisibility(VISIBLE);
        handler.postDelayed(action, delayMillis);
    }
}
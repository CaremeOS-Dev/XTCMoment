package com.xtc.anim.alphaplayer.view;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.xtc.anim.alphaplayer.IMonitor;
import com.xtc.anim.alphaplayer.IPlayerAction;
import com.xtc.anim.alphaplayer.controller.IPlayerController;
import com.xtc.anim.alphaplayer.controller.PlayerController;
import com.xtc.anim.alphaplayer.model.AlphaVideoViewType;
import com.xtc.anim.alphaplayer.model.Configuration;
import com.xtc.anim.alphaplayer.model.DataSource;
import com.xtc.anim.alphaplayer.model.ScaleType;
import com.xtc.anim.alphaplayer.player.AbsPlayer;
import com.xtc.anim.alphaplayer.player.DefaultSystemPlayer;
import com.xtc.anim.alphaplayer.widget.IAlphaVideoView;
import com.xtc.log.LogUtil;

/**
 * 透明视频礼物视图，对外提供初始化播放器、播放与释放等能力。
 */
public class VideoGiftView extends RelativeLayout {

    private static final String TAG = "VideoGiftView";

    private final Context context;
    private IPlayerController playerController;

    public VideoGiftView(Context context) {
        this(context, null);
    }

    public VideoGiftView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VideoGiftView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        this.context = context;
    }

    public void initPlayer(LifecycleOwner lifecycleOwner, IPlayerAction playerAction, IMonitor monitor) {
        initPlayer(lifecycleOwner, playerAction, monitor, AlphaVideoViewType.GL_SURFACE_VIEW, new DefaultSystemPlayer());
    }

    public void initPlayer(LifecycleOwner lifecycleOwner, IPlayerAction playerAction, IMonitor monitor,
                           AlphaVideoViewType viewType) {
        initPlayer(lifecycleOwner, playerAction, monitor, viewType, new DefaultSystemPlayer());
    }

    public void initPlayer(LifecycleOwner lifecycleOwner, IPlayerAction playerAction, IMonitor monitor,
                           AbsPlayer player) {
        initPlayer(lifecycleOwner, playerAction, monitor, AlphaVideoViewType.GL_SURFACE_VIEW, player);
    }

    public void initPlayer(LifecycleOwner lifecycleOwner, IPlayerAction playerAction, IMonitor monitor,
                           AlphaVideoViewType viewType, AbsPlayer player) {
        Configuration configuration = new Configuration(this.context, lifecycleOwner);
        configuration.setAlphaVideoViewType(viewType);
        this.playerController = PlayerController.get(configuration, player);
        this.playerController.setPlayerAction(playerAction);
        this.playerController.setMonitor(monitor);
        this.playerController.attachAlphaView(this);
    }

    public void startAnimation(String baseDir, String fileName) {
        startAnimation(baseDir, fileName, ScaleType.ScaleAspectFitCenter.ordinal(), ScaleType.ScaleAspectFitCenter.ordinal());
    }

    public void startAnimation(String baseDir, String fileName, int portraitScaleType, int landscapeScaleType) {
        if (TextUtils.isEmpty(baseDir)) {
            LogUtil.w(TAG, "startAnimation error, filePath is empty!");
            return;
        }
        LogUtil.d(TAG, "startAnimation: " + baseDir + fileName);
        DataSource dataSource = new DataSource();
        dataSource.baseDir = baseDir;
        dataSource.setPortraitPath(fileName, portraitScaleType);
        dataSource.setLandscapePath(fileName, landscapeScaleType);
        startDataSource(dataSource);
    }

    private void startDataSource(DataSource dataSource) {
        if (this.playerController != null) {
            this.playerController.start(dataSource);
        }
    }

    public void release() {
        if (this.playerController != null) {
            LogUtil.d(TAG, "releasePlayerController");
            this.playerController.detachAlphaView(this);
            this.playerController.release();
            this.playerController = null;
        }
    }

    public void setLooping(boolean looping) {
        if (this.playerController == null) {
            return;
        }
        if (looping) {
            this.playerController.setLooperCount(-1);
        } else {
            this.playerController.setLooperCount(0);
        }
    }

    public void setFirstFrameAtEnd(boolean show) {
        if (this.playerController == null) {
            return;
        }
        this.playerController.showFirstFrameAtEnd(show);
    }

    public void setVisibility(int visibility) {
        if (this.playerController == null) {
            return;
        }
        this.playerController.setVisibility(visibility);
    }

    public void onFirstFrame() {
        if (this.playerController != null && (this.playerController.getView() instanceof IAlphaVideoView)) {
            ((IAlphaVideoView) this.playerController.getView()).onFirstFrame();
        }
    }

    public int getCurrentPosition() {
        if (this.playerController == null) {
            return 0;
        }
        return this.playerController.getCurrentPosition();
    }

    public void setViewDefaultWidthAndHeight(int width, int height) {
        if (this.playerController == null) {
            return;
        }
        this.playerController.setViewDefaultWidthAndHeight(width, height);
    }
}
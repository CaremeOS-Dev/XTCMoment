package com.xtc.aitext.weight;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ss.ugc.android.alpha_player.IMonitor;
import com.ss.ugc.android.alpha_player.IPlayerAction;
import com.ss.ugc.android.alpha_player.controller.IPlayerController;
import com.ss.ugc.android.alpha_player.controller.PlayerController;
import com.ss.ugc.android.alpha_player.model.AlphaVideoViewType;
import com.ss.ugc.android.alpha_player.model.Configuration;
import com.ss.ugc.android.alpha_player.model.DataSource;
import com.ss.ugc.android.alpha_player.model.ScaleType;
import com.xtc.log.LogUtil;

/**
 * 透明视频动画视图，封装 alpha player 的播放控制。
 */
public class VideoAnimView extends RelativeLayout {

    private static final String TAG = "VideoAnimView";

    private IPlayerController playerController;

    /** 动画播放监听。 */
    public interface VideoAnimationListener {
        int ERROR_FILE_NOT_FOUND = -1;

        void onEnd();

        void onError(String message, int code, int extraCode, String detail);

        void onFirstFrame();

        void onPlayerReady();
    }

    public VideoAnimView(Context context) {
        super(context);
    }

    public VideoAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 初始化播放控制器。 */
    public void initPlayerController(Context context, LifecycleOwner lifecycleOwner, VideoAnimationListener listener) {
        Configuration configuration = new Configuration(context, lifecycleOwner);
        configuration.setVideoViewType(AlphaVideoViewType.GL_TEXTURE_VIEW);
        this.playerController = PlayerController.INSTANCE.create(configuration, null);
        this.playerController.setPlayerAction(createPlayerAction(listener));
        this.playerController.setMonitor(createMonitor(listener));
        this.playerController.setLooping(true);
        this.playerController.attachView(this);
    }

    /** 开始播放指定资源。 */
    public void startAnimation(String baseDir, String fileName) {
        if (TextUtils.isEmpty(baseDir)) {
            LogUtil.w(TAG, "startAnimation error, filePath is empty!");
            return;
        }
        LogUtil.d(TAG, "startAnimation: " + baseDir + fileName);
        DataSource dataSource = new DataSource();
        dataSource.setBaseDir(baseDir);
        dataSource.setPortraitPath(fileName, ScaleType.ScaleAspectFitCenter.ordinal());
        dataSource.setLandscapePath(fileName, ScaleType.ScaleAspectFitCenter.ordinal());
        startDataSource(dataSource);
    }

    public void setLooping(boolean looping) {
        IPlayerController controller = this.playerController;
        if (controller == null) {
            return;
        }
        controller.setLooping(looping ? -1 : 0);
    }

    private void startDataSource(DataSource dataSource) {
        if (this.playerController != null) {
            LogUtil.d(TAG, "startDataSource: ");
            this.playerController.start(dataSource);
        }
    }

    /** 释放播放控制器。 */
    public void releasePlayerController() {
        if (this.playerController != null) {
            LogUtil.d(TAG, "releasePlayerController");
            this.playerController.detachView(this);
            this.playerController.release();
            this.playerController = null;
        }
    }

    private IPlayerAction createPlayerAction(final VideoAnimationListener listener) {
        return new IPlayerAction() {
            @Override
            public void onVideoSizeChanged(int width, int height, ScaleType scaleType) {
            }

            @Override
            public void startAction(int orientation) {
            }

            @Override
            public void onPlayerReady() {
                if (listener != null) {
                    listener.onPlayerReady();
                }
            }

            @Override
            public void onFirstFrameStart() {
                if (listener != null) {
                    listener.onFirstFrame();
                }
            }

            @Override
            public void endAction() {
                if (listener != null) {
                    listener.onEnd();
                }
            }
        };
    }

    private IMonitor createMonitor(final VideoAnimationListener listener) {
        return new IMonitor() {
            @Override
            public void monitor(boolean success, String message, int code, int extraCode, String detail) {
                if (!success && listener != null) {
                    listener.onError(message, code, extraCode, detail);
                }
            }
        };
    }
}
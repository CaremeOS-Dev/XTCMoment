package com.xtc.moment.module.widget;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.xtc.anim.alphaplayer.IMonitor;
import com.xtc.anim.alphaplayer.IPlayerAction;
import com.xtc.anim.alphaplayer.controller.IPlayerController;
import com.xtc.anim.alphaplayer.controller.PlayerController;
import com.xtc.anim.alphaplayer.model.AlphaVideoViewType;
import com.xtc.anim.alphaplayer.model.Configuration;
import com.xtc.anim.alphaplayer.model.DataSource;
import com.xtc.anim.alphaplayer.model.ScaleType;
import com.xtc.anim.alphaplayer.player.DefaultSystemPlayer;
import com.xtc.log.LogUtil;

/**
 * Frame layout that plays an alpha (transparent) video animation, for example the moment publish
 * success animation.
 */
public class VideoAnimView extends FrameLayout {

    private static final String TAG = "VideoAnimView";

    private IPlayerController playerController;

    /** Callbacks forwarded from the underlying alpha player. */
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

    /** Creates the player controller and attaches it to this view. */
    public void initPlayerController(Context context, LifecycleOwner lifecycleOwner,
                                     VideoAnimationListener listener) {
        Configuration configuration = new Configuration(context, lifecycleOwner);
        configuration.setAlphaVideoViewType(AlphaVideoViewType.GL_TEXTURE_VIEW);
        this.playerController = PlayerController.get(configuration, new DefaultSystemPlayer());
        this.playerController.setPlayerAction(initPlayerAction(listener));
        this.playerController.setMonitor(initMonitor(listener));
        this.playerController.setLooperCount(-1);
        this.playerController.attachAlphaView(this);
    }

    /**
     * Starts the animation located in {@code baseDir} under the name {@code fileName}.
     */
    public void startAnimation(String baseDir, String fileName) {
        if (TextUtils.isEmpty(baseDir)) {
            LogUtil.w(TAG, "startAnimation error, filePath is empty!");
            return;
        }
        LogUtil.d(TAG, "startAnimation: " + baseDir + fileName);
        DataSource dataSource = new DataSource();
        dataSource.withBaseDir(baseDir);
        dataSource.setPortraitPath(fileName, ScaleType.ScaleAspectFitCenter.ordinal());
        dataSource.setLandscapePath(fileName, ScaleType.ScaleAspectFitCenter.ordinal());
        startDataSource(dataSource);
    }

    public void setLooping(boolean looping) {
        IPlayerController controller = this.playerController;
        if (controller == null) {
            return;
        }
        controller.setLooperCount(looping ? -1 : 0);
    }

    private void startDataSource(DataSource dataSource) {
        if (this.playerController != null) {
            this.playerController.start(dataSource);
        }
    }

    /** Detaches and releases the player controller. */
    public void releasePlayerController() {
        if (this.playerController != null) {
            LogUtil.d(TAG, "releasePlayerController");
            this.playerController.detachAlphaView(this);
            this.playerController.release();
            this.playerController = null;
        }
    }

    private IPlayerAction initPlayerAction(final VideoAnimationListener listener) {
        return new IPlayerAction() {
            @Override
            public void onPlayProcess(int position) {
            }

            @Override
            public void onVideoSizeChanged(int videoWidth, int videoHeight, ScaleType scaleType) {
            }

            @Override
            public void startAction(int duration) {
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

    private IMonitor initMonitor(final VideoAnimationListener listener) {
        return new IMonitor() {
            @Override
            public void monitor(boolean success, String playType, int what, int extra, String errorInfo) {
                if (success || listener == null) {
                    return;
                }
                listener.onError(playType, what, extra, errorInfo);
            }
        };
    }
}
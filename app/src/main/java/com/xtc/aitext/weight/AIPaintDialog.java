package com.xtc.aitext.weight;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xtc.moment.R;
import com.xtc.aitext.constant.Constant;
import com.xtc.aitext.util.AITextFileUtil;
import com.xtc.aitext.util.AITextHandlerUtil;
import com.xtc.aitext.util.AITextRxUtils;
import com.xtc.aitext.weight.callback.DialogDismissCallback;
import com.xtc.log.LogUtil;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.ui.widget.viewfilpper.ViewFlipper;
import com.xtc.utils.storage.FileUtils;

/**
 * AI 绘画等待弹窗，展示进度、提示与透明动画。
 */
public class AIPaintDialog extends Dialog implements LifecycleOwner {

    private static final String TAG = "ai_text_FirstTipDialog";
    private static final float SWIPE_THRESHOLD = 40.0f;
    private static final long DEFAULT_WAITING_SECONDS = 5L;
    private static final long PROGRESS_INTERVAL = 1000L;

    private final Context mContext;
    private final LifecycleRegistry lifecycleRegistry;

    private VideoAnimView player;
    private ViewFlipper viewFlipper;
    private ProgressBar progressBar;
    private PaintExitDialog paintExitDialog;
    private DialogDismissCallback dialogDismissCallback;

    private long waitingTime;
    private long progressIncrement;
    private int currentProgress;
    private float startX;

    private final Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (currentProgress < 100) {
                currentProgress += progressIncrement;
                if (100 - currentProgress < progressIncrement) {
                    return;
                }
                progressBar.setProgress(currentProgress);
                AITextHandlerUtil.runOnMainDelay(this, PROGRESS_INTERVAL);
            }
        }
    };

    public AIPaintDialog(Context context) {
        this(context, R.style.dialog_default_style_forbidSwipe);
    }

    public AIPaintDialog(Context context, int themeResId) {
        super(context, themeResId);
        if (getWindow() != null) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        setContentView(R.layout.dialog_paint);
        this.mContext = context;
        this.lifecycleRegistry = new LifecycleRegistry(this);
        this.lifecycleRegistry.markState(Lifecycle.State.RESUMED);
        initView();
    }

    private void initView() {
        this.player = findViewById(R.id.player_painting);
        this.viewFlipper = findViewById(R.id.dialog_vf);
        this.progressBar = findViewById(R.id.pb_painting);
        this.viewFlipper.setInAnimation(mContext, R.anim.anim_bottom_in);
        this.viewFlipper.setOutAnimation(mContext, R.anim.anim_top_out);
        this.player.setVisibility(ViewGroup.VISIBLE);
        initAlphaPlayer();
    }

    /** 开始轮播提示文案。 */
    public void startFlipping() {
        if (this.viewFlipper.isFlipping()) {
            this.viewFlipper.stopFlipping();
            this.viewFlipper.removeAllViews();
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (this.waitingTime > 0) {
            addTextView(inflater, mContext.getString(R.string.string_paint_hint_1, String.valueOf(waitingTime)));
        }
        addTextView(inflater, mContext.getString(R.string.string_paint_hint_2));
        addTextView(inflater, mContext.getString(R.string.string_paint_hint_3));
        this.viewFlipper.setVisibility(ViewGroup.VISIBLE);
        this.viewFlipper.startFlipping();
    }

    /** 启动进度增长。 */
    public void setProgress() {
        long waitingTime = this.waitingTime;
        if (waitingTime == 0) {
            return;
        }
        this.progressIncrement = 100 / waitingTime;
        AITextHandlerUtil.runOnMain(updateProgress);
    }

    /** 设置等待时间（秒），返回修正后的等待时间。 */
    public long setWaitingTime(long targetSecond) {
        long remainSeconds = targetSecond - (System.currentTimeMillis() / 1000);
        LogUtil.d(TAG, "setWaitingTime: time" + remainSeconds);
        if (remainSeconds <= 0) {
            this.waitingTime = DEFAULT_WAITING_SECONDS;
        } else {
            this.waitingTime = remainSeconds;
        }
        return this.waitingTime;
    }

    /** 直接拉满进度。 */
    public void setMaxProgress() {
        AITextHandlerUtil.removeFromMain(updateProgress);
        if (this.progressBar.getProgress() < 100) {
            this.progressBar.setProgress(100);
        }
    }

    private void addTextView(LayoutInflater inflater, String text) {
        TextView textView = (TextView) inflater.inflate(R.layout.item_painting_info, viewFlipper, false);
        textView.setText(text);
        this.viewFlipper.addView(textView);
    }

    private void initAlphaPlayer() {
        AITextRxUtils.runOnIo(new Runnable() {
            @Override
            public void run() {
                if (FileUtils.isFile(AITextFileUtil.getVideoDir(getContext()) + Constant.ASSET_PAINT_VIDEO)) {
                    return;
                }
                AITextFileUtil.copyFromAssets(getContext(), Constant.ASSET_VIDEO_DIR,
                        AITextFileUtil.getVideoDir(getContext()));
            }
        });
        this.player.initPlayerController(getContext().getApplicationContext(), this, new VideoAnimView.VideoAnimationListener() {
            @Override
            public void onError(String message, int code, int extraCode, String detail) {
            }

            @Override
            public void onFirstFrame() {
            }

            @Override
            public void onPlayerReady() {
                AITextRxUtils.runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        player.startAnimation(AITextFileUtil.getVideoDir(getContext()), Constant.ASSET_PAINT_VIDEO);
                    }
                });
            }

            @Override
            public void onEnd() {
                player = null;
            }
        });
        VideoAnimView player = this.player;
        if (player != null) {
            player.setLooping(true);
        }
    }

    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
            window.setBackgroundDrawable(null);
        }
        super.show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.startX = event.getX();
        } else if (action == MotionEvent.ACTION_MOVE && event.getX() - this.startX > SWIPE_THRESHOLD) {
            showMoveDialog();
        }
        return super.dispatchTouchEvent(event);
    }

    private void showMoveDialog() {
        if (DialogUtil.isDialogShowing(this.paintExitDialog)) {
            return;
        }
        this.paintExitDialog = new PaintExitDialog(mContext);
        this.paintExitDialog.setMoveCallback(new PaintExitDialog.MoveCallback() {
            @Override
            public void clickContinue() {
                DialogUtil.dismissDialog(paintExitDialog);
            }

            @Override
            public void clickExit() {
                dismiss();
            }
        });
        DialogUtil.showDialog(this.paintExitDialog);
    }

    @Override
    public void onDetachedFromWindow() {
        release();
        DialogUtil.dismissDialog(this.paintExitDialog);
        LogUtil.i(TAG, "onDetachedFromWindow...");
        super.onDetachedFromWindow();
    }

    @Override
    public void dismiss() {
        LogUtil.d(TAG, "dismiss: ");
        DialogDismissCallback callback = this.dialogDismissCallback;
        if (callback != null) {
            callback.dismiss();
        }
        release();
        DialogUtil.dismissDialog(this.paintExitDialog);
        super.dismiss();
    }

    /** 释放播放器。 */
    public void release() {
        if (this.player != null) {
            LogUtil.i(TAG, "release...");
            this.player.releasePlayerController();
            this.player = null;
        }
    }

    public void setDialogDismissCallback(DialogDismissCallback dialogDismissCallback) {
        this.dialogDismissCallback = dialogDismissCallback;
    }
}
package com.xtc.ui.widget.toast.view;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.xtc.moment.R;
import com.xtc.ui.widget.animation.progressview.HorizontalProgressBar;

/** 支持文本或进度条展示的自定义 Toast。 */
public class ToastView extends Toast {
    private static final String TAG = "ToastView";
    private Context context;
    private int duration;
    private int durationTime;
    private int gravity;
    private int maxProgress;
    private int progress;
    private HorizontalProgressBar progressBar;
    private TextView textView;
    private Toast toast;

    public ToastView(Context context) {
        super(context);
        this.duration = -1;
        this.durationTime = -1;
        this.gravity = -1;
        this.progress = 0;
        this.maxProgress = 0;
        initView(context);
        this.context = context;
    }

    private void initView(Context context) {
        View content = LayoutInflater.from(context).inflate(R.layout.layout_toast, (ViewGroup) null);
        this.textView = (TextView) content.findViewById(R.id.tv_toast);
        this.progressBar = (HorizontalProgressBar) content.findViewById(R.id.pb_toast);
        this.toast = new Toast(context);
        this.toast.setView(content);
    }

    public void showToast() {
        if (this.toast == null) {
            this.toast = new Toast(this.context);
        }
        if (!TextUtils.isEmpty(this.textView.getText())) {
            this.progressBar.setVisibility(8);
        } else {
            this.textView.setVisibility(8);
            if (this.maxProgress != 0 && this.progress != 0) {
                this.progressBar.setVisibility(0);
                this.progressBar.setMax(this.maxProgress);
                this.progressBar.setProgress(this.progress);
            }
        }
        if (this.duration == -1 && this.durationTime == -1) {
            this.toast.setDuration(0);
        }
        if (this.gravity == -1) {
            this.toast.setGravity(48, 0, 0);
        }
        this.toast.show();
        int customDuration = this.durationTime;
        if (customDuration == 1 || customDuration == 0 || customDuration == -1) {
            return;
        }
        cancelToast();
    }

    private void cancelToast() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastView.this.toast.cancel();
            }
        }, this.durationTime);
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
        this.toast.setDuration(duration);
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
        this.toast.setDuration(durationTime);
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        this.toast.setGravity(gravity, 0, 0);
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
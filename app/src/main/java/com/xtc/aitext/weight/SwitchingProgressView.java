package com.xtc.aitext.weight;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.xtc.aitext.R;

/**
 * 旋转进度视图。
 */
public class SwitchingProgressView extends AppCompatImageView {

    private static final String TAG = "SwitchingProgressView";
    private static final long ROTATE_INTERVAL = 50L;
    private static final float ROTATE_STEP = 30.0f;

    private final Runnable rotateTask = new Runnable() {
        @Override
        public void run() {
            setRotation(getRotation() + ROTATE_STEP);
            postDelayed(this, ROTATE_INTERVAL);
        }
    };

    public SwitchingProgressView(Context context) {
        this(context, null);
    }

    public SwitchingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.loading_1);
    }

    public void start() {
        removeCallbacks(this.rotateTask);
        postDelayed(this.rotateTask, ROTATE_INTERVAL);
    }

    public void stop() {
        removeCallbacks(this.rotateTask);
    }
}
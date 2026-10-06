package com.xtc.bigdata.monitor.frame;

import android.view.Choreographer;

import com.xtc.log.LogUtil;

/** 逐帧回调统计掉帧情况。 */
public class FPSFrameCallback implements Choreographer.FrameCallback {

    private static final String TAG = "FPSFrameCallback";
    private static final float TARGET_FRAME = 60.0f;

    private long mLastFrameTimeNanos;
    private float mTargetFrame = TARGET_FRAME;
    private long mFrameIntervalNanos = (long) (1.0E9f / this.mTargetFrame);

    public FPSFrameCallback(long startFrameTimeNanos) {
        this.mLastFrameTimeNanos = 0L;
        this.mLastFrameTimeNanos = startFrameTimeNanos;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (this.mLastFrameTimeNanos == 0) {
            this.mLastFrameTimeNanos = frameTimeNanos;
        }
        long frameInterval = frameTimeNanos - this.mLastFrameTimeNanos;
        long targetInterval = this.mFrameIntervalNanos;
        if (frameInterval >= targetInterval) {
            long skippedFrames = frameInterval / targetInterval;
            float skipped = skippedFrames;
            if (skipped > this.mTargetFrame / 2.0f) {
                LogUtil.i(TAG, "Skipped " + skippedFrames
                        + " frames!  The application may be doing too much work on its main thread !");
            }
            LogUtil.d(TAG, "current Frame = " + ((int) Math.max(this.mTargetFrame - skipped, 0.0f)));
        }
        this.mLastFrameTimeNanos = frameTimeNanos;
        Choreographer.getInstance().postFrameCallback(this);
    }
}
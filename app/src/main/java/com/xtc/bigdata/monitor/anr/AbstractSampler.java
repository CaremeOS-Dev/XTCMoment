package com.xtc.bigdata.monitor.anr;

import java.util.concurrent.atomic.AtomicBoolean;

/** 周期性采样器基类，默认采样间隔 300ms，首次延迟 800ms。 */
@Deprecated
abstract class AbstractSampler {

    private static final int DEFAULT_SAMPLE_INTERVAL = 300;
    private static final long FIRST_SAMPLE_DELAY = 800L;

    protected long mSampleInterval;
    protected AtomicBoolean mShouldSample = new AtomicBoolean(false);

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doSample();
            if (mShouldSample.get()) {
                HandlerThreadFactory.getTimerThreadHandler().postDelayed(mRunnable, mSampleInterval);
            }
        }
    };

    abstract void doSample();

    public AbstractSampler(long sampleInterval) {
        this.mSampleInterval = sampleInterval == 0 ? DEFAULT_SAMPLE_INTERVAL : sampleInterval;
    }

    public void start() {
        if (this.mShouldSample.get()) {
            return;
        }
        this.mShouldSample.set(true);
        HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(this.mRunnable);
        HandlerThreadFactory.getTimerThreadHandler().postDelayed(this.mRunnable, FIRST_SAMPLE_DELAY);
    }

    public void stop() {
        if (this.mShouldSample.get()) {
            this.mShouldSample.set(false);
            HandlerThreadFactory.getTimerThreadHandler().removeCallbacks(this.mRunnable);
        }
    }
}
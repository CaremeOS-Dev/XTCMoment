package com.xtc.ui.widget.ptrrefresh.header;

/** 下拉刷新 UI 流程的接管钩子。 */
public abstract class UIRefreshHandlerHook implements Runnable {

    private static final byte STATUS_PREPARE = 0;
    private static final byte STATUS_IN_HOOK = 1;
    private static final byte STATUS_RESUMED = 2;

    private Runnable mResumeAction;
    private byte mStatus = STATUS_PREPARE;

    public void takeOver() {
        takeOver(null);
    }

    public void takeOver(Runnable resumeAction) {
        if (resumeAction != null) {
            this.mResumeAction = resumeAction;
        }
        byte status = this.mStatus;
        if (status == STATUS_PREPARE) {
            this.mStatus = STATUS_IN_HOOK;
            run();
        } else if (status == STATUS_RESUMED) {
            resume();
        }
    }

    public void reset() {
        this.mStatus = STATUS_PREPARE;
    }

    public void resume() {
        Runnable resumeAction = this.mResumeAction;
        if (resumeAction != null) {
            resumeAction.run();
        }
        this.mStatus = STATUS_RESUMED;
    }

    public void setResumeAction(Runnable resumeAction) {
        this.mResumeAction = resumeAction;
    }
}
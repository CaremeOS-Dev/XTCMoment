package com.xtc.moment.module.illegal.state;

import android.content.Context;

/**
 * 禁止发送状态。
 */
public class DisableState extends IllegalBaseState {

    public DisableState(Context context) {
        super(context);
    }

    @Override
    public int getCurrentState() {
        return 3;
    }

    @Override
    protected String getStateName() {
        return "DisableState";
    }
}
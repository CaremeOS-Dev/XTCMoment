package com.xtc.moment.module.illegal.state;

import android.content.Context;

import com.xtc.moment.module.illegal.record.IllegalRecordManger;

/**
 * 正常状态。
 */
public class NormalState extends IllegalBaseState {

    private final IllegalRecordManger illegalRecordManger;

    public NormalState(Context context, IllegalRecordManger illegalRecordManger) {
        super(context);
        this.illegalRecordManger = illegalRecordManger;
    }

    @Override
    public int getCurrentState() {
        return 0;
    }

    @Override
    protected String getStateName() {
        return "NormalState";
    }
}
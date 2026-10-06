package com.xtc.utils.ui;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.xtc.bigdata.common.constants.Constants;

/** SIM state helpers. */
public class TelephoneUtil {

    /** Returns {@link TelephonyManager#getSimState()}. */
    public static int getSimState(Context context) {
        return ((TelephonyManager) context.getSystemService(Constants.PHONE)).getSimState();
    }

    /** Returns {@code true} when the SIM is ready ({@code SIM_STATE_READY}). */
    public static boolean isSimReady(Context context) {
        return getSimState(context) == TelephonyManager.SIM_STATE_READY;
    }
}
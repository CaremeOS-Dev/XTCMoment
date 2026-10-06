package com.xtc.utils.system;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.log.LogUtil;

/** Telephony/SIM helpers. */
public class SimManager {

    /** Unknown operator. */
    public static final int OPERATOR_UNKNOWN = -1;
    /** China Mobile. */
    public static final int OPERATOR_MOBILE = 0;
    /** China Unicom. */
    public static final int OPERATOR_UNICOM = 1;
    /** China Telecom. */
    public static final int OPERATOR_TELECOM = 2;

    private static final String TAG = "SimManager";
    private static final String UNKNOWN_SIM_TYPE = "unknown sim type";

    private SimManager() {
    }

    /** Logs the SIM identifiers. */
    public static void logSimInfo(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
        LogUtil.d(TAG, "IMEI:" + telephonyManager.getDeviceId());
        LogUtil.d(TAG, "tel:" + telephonyManager.getLine1Number());
        LogUtil.d(TAG, "ICCID:" + telephonyManager.getSimSerialNumber());
        LogUtil.d(TAG, "IMSI:" + telephonyManager.getSubscriberId());
    }

    public static String getPhoneNumber(Context context) {
        return ((TelephonyManager) context.getSystemService(Constants.PHONE)).getLine1Number();
    }

    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService(Constants.PHONE)).getDeviceId();
    }

    public static String getSubscriberId(Context context) {
        return ((TelephonyManager) context.getSystemService(Constants.PHONE)).getSubscriberId();
    }

    /** @return the first five digits of the IMSI, i.e. the MCC/MNC. */
    public static String getMccMnc(Context context) {
        String imsi = getSubscriberId(context);
        if (TextUtils.isEmpty(imsi)) {
            return "";
        }
        try {
            return imsi.substring(0, 5);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }

    /** @return the operator type derived from the IMSI. */
    public static int getOperatorTypeByImsi(Context context) {
        String imsi = getSubscriberId(context);
        if (TextUtils.isEmpty(imsi)) {
            return OPERATOR_UNKNOWN;
        }
        if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46004")
                || imsi.startsWith("46007") || imsi.startsWith("46008")) {
            return OPERATOR_MOBILE;
        }
        if (imsi.startsWith("46001") || imsi.startsWith("46006") || imsi.startsWith("46009")) {
            return OPERATOR_UNICOM;
        }
        if (imsi.startsWith("46003") || imsi.startsWith("46005") || imsi.startsWith("46011")
                || imsi.startsWith("46012") || imsi.startsWith("20404")) {
            return OPERATOR_TELECOM;
        }
        try {
            return Integer.valueOf(imsi.substring(0, 5));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return OPERATOR_UNKNOWN;
        }
    }

    /** @return the operator type derived from the SIM operator code. */
    public static int getOperatorType(Context context) {
        String simOperator = ((TelephonyManager) context.getSystemService(Constants.PHONE)).getSimOperator();
        int operatorType = OPERATOR_UNKNOWN;
        if (TextUtils.isEmpty(simOperator)) {
            LogUtil.d(TAG, "getOperatorType simOperator=" + simOperator + ", operatorType=" + operatorType);
            return operatorType;
        }
        if (simOperator.equals("46000") || simOperator.equals("46002") || simOperator.equals("46004")
                || simOperator.equals("46007") || simOperator.equals("46008")) {
            operatorType = OPERATOR_MOBILE;
        } else if (simOperator.equals("46001") || simOperator.equals("46006") || simOperator.equals("46009")) {
            operatorType = OPERATOR_UNICOM;
        } else if (simOperator.equals("46003") || simOperator.equals("46005") || simOperator.equals("46011")
                || simOperator.equals("46012") || simOperator.equals("20404")) {
            operatorType = OPERATOR_TELECOM;
        }
        LogUtil.d(TAG, "getOperatorType simOperator=" + simOperator + ", operatorType=" + operatorType);
        return operatorType;
    }

    public static int getSimState(Context context) {
        return ((TelephonyManager) context.getSystemService(Constants.PHONE)).getSimState();
    }

    /** @return true when the SIM is ready. */
    public static boolean isSimReady(Context context) {
        return getSimState(context) == TelephonyManager.SIM_STATE_READY;
    }

    /** @return true when the SIM is absent or in an error state. */
    public static boolean isSimAbsentOrError(Context context) {
        int state = getSimState(context);
        return state == TelephonyManager.SIM_STATE_ABSENT || state == TelephonyManager.SIM_STATE_UNKNOWN;
    }

    /** @return true when the SIM is PIN/PUK locked. */
    public static boolean isSimLocked(Context context) {
        return getSimState(context) == TelephonyManager.SIM_STATE_PIN_REQUIRED;
    }

    /** @return the operator display name. */
    public static String getSimOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
        String simOperator = telephonyManager.getSimOperator();
        String simOperatorName = telephonyManager.getSimOperatorName();
        if (!TextUtils.isEmpty(simOperatorName)) {
            simOperator = simOperatorName;
        } else if (simOperator == null) {
            simOperator = UNKNOWN_SIM_TYPE;
        } else if (simOperator.equals("46000") || simOperator.equals("46002")) {
            simOperator = "中国移动";
        } else if (simOperator.equals("46001")) {
            simOperator = "中国联通";
        } else if (simOperator.equals("46003")) {
            simOperator = "中国电信";
        }
        LogUtil.i(TAG, "getSIMType:" + simOperator);
        return simOperator;
    }
}
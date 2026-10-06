package com.xtc.ui.widget.util;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.personalinfo.constant.PersonalInfoConstant;
import com.xtc.moment.R;
import com.xtc.ui.widget.dialog.bean.noIcon.CtaPermissionBean;
import com.xtc.ui.widget.permission.cta.interfaces.ICtaPermissionCallback;
import com.xtc.ui.widget.privacy.PrivacyBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Collects the permission/privacy information a CTA (consent) prompt needs.
 *
 * <p>Reads the requested permissions of a package, maps the interesting ones to
 * human labels from {@code R.array.permission_label}, and reads the privacy URLs
 * out of the package's manifest meta-data.
 */
public class CtaPermissionUtil {

    private static final String TAG = "AuthorityUtil";

    public static final String LAUNCHER_SELF_START_URI = "content://com.xtc.launcher.self.start";
    public static final String METHOD_SELF_START = "METHOD_SELF_START";
    public static final String SELF_START_STR = "SELF_START_STR";
    public static final String SELF_START_SUCCESS_STR = "SUCCESS";
    public static final String META_DATA_PRIVACY_URL = "privacy_url";
    public static final String META_DATA_AGREEMENT_URL = "agreement_url";
    public static final String META_DATA_DISCLAIMER_URL = "disclaimer_url";
    public static final String META_DATA_REQUIRED_PERMISSIONS = "required_permissions";

    private static final String PACKAGE_WEICHAT = "com.xtc.weichat";
    private static final String PACKAGE_MOMENT = "com.xtc.moment";
    private static final String PACKAGE_MESSAGE = "com.xtc.message";

    private static final String[] MULTI_ENTRY_APPS = {PersonalInfoConstant.PersonalMainIntent.SETTING_PACKAGE_NAME, "com.xtc.camera.app"};
    private static final ArrayList<String> MULTI_ENTRY_APP_LIST = new ArrayList<String>(Arrays.asList(MULTI_ENTRY_APPS));

    private static final String[] ANDROID_PERM_NAME = {
            "android.permission.CALL_PHONE", "android.permission.SEND_SMS", "android.permission.INTERNET",
            "android.permission.RECORD_AUDIO", "android.permission.READ_SMS", "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS", "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG",
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.CAMERA", "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.BLUETOOTH_ADMIN",
            "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE", "org.simalliance.openmobileapi.SMARTCARD"};

    public static final String[] DANGEROUS_NAME = {
            "android.permission.CALL_PHONE", "android.permission.SEND_SMS", "android.permission.RECORD_AUDIO",
            "android.permission.READ_SMS", "android.permission.READ_CONTACTS", "android.permission.WRITE_CONTACTS",
            "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.CAMERA", "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final ArrayList<String> PERM_LABELS = new ArrayList<String>(Arrays.asList(ANDROID_PERM_NAME));
    private static final ArrayList<String> DANGEROUS_LABELS = new ArrayList<String>(Arrays.asList(DANGEROUS_NAME));

    public static String getMessageBody(Context context, String permissionName) {
        return getMessageBody(context, permissionName, null);
    }

    public static String getMessageBody(Context context, String permissionName, String reason) {
        String[] labels = context.getResources().getStringArray(R.array.permission_label);
        int index = PERM_LABELS.indexOf(permissionName);
        return TextUtils.isEmpty(reason)
                ? labels[index]
                : String.format(context.getResources().getString(R.string.privacy_list_format_permission_reason), labels[index], reason);
    }

    private static boolean isRuntime(PermissionInfo permissionInfo) {
        if (permissionInfo == null) {
            return false;
        }
        return PERM_LABELS.contains(permissionInfo.name) || (permissionInfo.protectionLevel & 15) == 1;
    }

    private static boolean isDangerous(PermissionInfo permissionInfo) {
        if (Build.VERSION.SDK_INT <= 19) {
            return DANGEROUS_LABELS.contains(permissionInfo.name);
        }
        return (permissionInfo.protectionLevel & 15) == 1;
    }

    public static List<String> getAuthorityInfos(Context context, String packageName) {
        ArrayList<String> labels = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 4096);
            if (packageInfo != null && packageInfo.requestedPermissions != null) {
                for (String permissionName : packageInfo.requestedPermissions) {
                    PermissionInfo permissionInfo = null;
                    try {
                        permissionInfo = packageManager.getPermissionInfo(permissionName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (isRuntime(permissionInfo) && PERM_LABELS.contains(permissionInfo.name)) {
                        String messageBody = getMessageBody(context, permissionInfo.name);
                        if (!labels.contains(messageBody)) {
                            labels.add(messageBody);
                        }
                    }
                }
                if (PACKAGE_WEICHAT.equals(packageName) || PACKAGE_MOMENT.equals(packageName)) {
                    String location = context.getResources().getString(R.string.get_location_info);
                    if (!labels.contains(location)) {
                        labels.add(location);
                    }
                }
                if (PACKAGE_MESSAGE.equals(packageName)) {
                    String contact = context.getResources().getString(R.string.get_contact_info);
                    if (!labels.contains(contact)) {
                        labels.add(contact);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return labels;
    }

    public static PrivacyBean getPrivacy(Context context, String packageName) {
        return getPrivacy(context, packageName, null);
    }

    public static PrivacyBean getPrivacy(Context context, String packageName, Map<String, String> reasonMap) {
        PrivacyBean privacyBean = new PrivacyBean();
        privacyBean.setPackageName(packageName);
        ArrayList<String> list1 = new ArrayList<String>();
        ArrayList<String> list2 = new ArrayList<String>();
        ArrayList<String> list3 = new ArrayList<String>();
        privacyBean.setAppName(getApplicationName(context, packageName));
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 4224);
            if (packageInfo != null && packageInfo.requestedPermissions != null) {
                Bundle metaData = packageInfo.applicationInfo.metaData;
                ArrayList<String> requiredPermissions = new ArrayList<String>();
                if (metaData != null) {
                    String privacyUrl = metaData.getString(META_DATA_PRIVACY_URL);
                    if (privacyUrl != null) {
                        privacyBean.setPrivacyUrl(privacyUrl);
                    }
                    String agreementUrl = metaData.getString(META_DATA_AGREEMENT_URL);
                    if (agreementUrl != null) {
                        privacyBean.setAgreementUrl(agreementUrl);
                    }
                    String disclaimerUrl = metaData.getString(META_DATA_DISCLAIMER_URL);
                    if (disclaimerUrl != null) {
                        privacyBean.setDisclaimerUrl(disclaimerUrl);
                    }
                    ArrayList<String> required = new Gson().fromJson(metaData.getString(META_DATA_REQUIRED_PERMISSIONS), new TypeToken<List<String>>() {
                    }.getType());
                    if (required != null) {
                        requiredPermissions.addAll(required);
                    }
                }
                for (String permissionName : packageInfo.requestedPermissions) {
                    PermissionInfo permissionInfo;
                    try {
                        permissionInfo = packageManager.getPermissionInfo(permissionName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        permissionInfo = null;
                    }
                    if (isRuntime(permissionInfo) && PERM_LABELS.contains(permissionInfo.name)) {
                        String reason = reasonMap != null ? reasonMap.get(permissionInfo.name) : null;
                        String messageBody = getMessageBody(context, permissionInfo.name, reason);
                        if (isDangerous(permissionInfo) && !requiredPermissions.contains(permissionInfo.name)) {
                            if (!list2.contains(messageBody)) {
                                list2.add(messageBody);
                            }
                        } else if (!list3.contains(messageBody) && isDangerous(permissionInfo)) {
                            list3.add(messageBody);
                        }
                        if (!isDangerous(permissionInfo) && !list1.contains(messageBody)) {
                            list1.add(messageBody);
                        }
                    }
                }
                if (PACKAGE_WEICHAT.equals(packageName) || PACKAGE_MOMENT.equals(packageName)) {
                    String location = context.getResources().getString(R.string.get_location_info);
                    if (!list3.contains(location)) {
                        list3.add(location);
                    }
                }
                if (PACKAGE_MESSAGE.equals(packageName)) {
                    String contact = context.getResources().getString(R.string.get_contact_info);
                    if (!list3.contains(contact)) {
                        list3.add(contact);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        privacyBean.setList1(list1);
        privacyBean.setList2(list2);
        privacyBean.setList3(list3);
        return privacyBean;
    }

    public static void showPermissionReminder(Context context, CtaPermissionBean bean, ICtaPermissionCallback callback) {
        DialogUtil.makeCtaPermissionDialog(context, bean, callback).show();
    }

    /** Asks the launcher to whitelist this package for self-start. */
    public static void setPackageWhiteListByLauncher(Context context) throws Throwable {
        setPackageWhiteListByLauncher(context, null);
    }

    /** Asks the launcher to whitelist a package for self-start. */
    public static void setPackageWhiteListByLauncher(Context context, String packageName) throws Throwable {
        ContentProviderClient client = null;
        String result = null;
        try {
            client = context.getContentResolver().acquireUnstableContentProviderClient(Uri.parse(LAUNCHER_SELF_START_URI));
            Bundle bundle = client.call(METHOD_SELF_START, packageName, null);
            result = bundle != null ? bundle.getString(SELF_START_STR) : null;
        } catch (Exception e) {
            LogUtil.e(TAG, "setPackageWhiteListByLauncher error", e);
        } finally {
            if (client != null) {
                client.release();
            }
        }
        LogUtil.d(TAG, "set self start success or not " + SELF_START_SUCCESS_STR.equals(result));
    }

    public static String getApplicationName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 128)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

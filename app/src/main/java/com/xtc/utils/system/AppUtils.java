package com.xtc.utils.system;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Installed-application queries, launching, install/uninstall and data cleaning. */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** @return true when {@code packageName} has a launcher intent. */
    public static boolean isInstallApp(Context context, String packageName) {
        return !TextUtils.isEmpty(packageName.trim()) && IntentUtils.getLaunchIntent(context, packageName) != null;
    }

    /** Opens the apk located at {@code filePath}. */
    public static void openApp(Context context, String filePath) {
        openApp(context, PrivateUtils.toFile(filePath));
    }

    /** Opens the apk file. */
    public static void openApp(Context context, File file) {
        if (PrivateUtils.exists(file)) {
            context.startActivity(IntentUtils.openFile(file));
        }
    }

    /** Opens the apk located at {@code filePath}, returning a result. */
    public static void openApp(Activity activity, String filePath, int requestCode) {
        openApp(activity, PrivateUtils.toFile(filePath), requestCode);
    }

    /** Opens the apk file, returning a result. */
    public static void openApp(Activity activity, File file, int requestCode) {
        if (PrivateUtils.exists(file)) {
            activity.startActivityForResult(IntentUtils.openFile(file), requestCode);
        }
    }

    /** Installs the apk at {@code filePath} via the package manager shell. */
    public static boolean installApp(Context context, String filePath) {
        if (!PrivateUtils.exists(PrivateUtils.toFile(filePath))) {
            return false;
        }
        ShellUtils.CommandResult result = ShellUtils.exec(
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath, !isSystemApp(context), true);
        return result.successMsg != null && result.successMsg.toLowerCase().contains("success");
    }

    /** Prompts the user to uninstall {@code packageName}. */
    public static void uninstallApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return;
        }
        context.startActivity(IntentUtils.uninstall(packageName));
    }

    /** Prompts the user to uninstall {@code packageName}, returning a result. */
    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return;
        }
        activity.startActivityForResult(IntentUtils.uninstall(packageName), requestCode);
    }

    /** Uninstalls {@code packageName} via shell, optionally keeping app data. */
    public static boolean uninstallApp(Context context, String packageName, boolean isKeepData) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return false;
        }
        StringBuilder command = new StringBuilder();
        command.append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall ");
        command.append(isKeepData ? "-k " : "");
        command.append(packageName);
        ShellUtils.CommandResult result = ShellUtils.exec(command.toString(), !isSystemApp(context), true);
        return result.successMsg != null && result.successMsg.toLowerCase().contains("success");
    }

    /** Launches {@code packageName}. */
    public static void launchApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return;
        }
        context.startActivity(IntentUtils.getLaunchIntent(context, packageName));
    }

    /** Launches {@code packageName}, returning a result. */
    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return;
        }
        activity.startActivityForResult(IntentUtils.getLaunchIntent(activity, packageName), requestCode);
    }

    /** Package name of this application. */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /** Opens this application's details settings page. */
    public static void openAppDetailsSettings(Context context) {
        openAppDetailsSettings(context, context.getPackageName());
    }

    /** Opens the details settings page of {@code packageName}. */
    public static void openAppDetailsSettings(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return;
        }
        context.startActivity(IntentUtils.appDetailsSettings(packageName));
    }

    /** Application label of this application, or null. */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /** Application label of {@code packageName}, or null. */
    public static String getAppName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Application icon of this application, or null. */
    public static Drawable getAppIcon(Context context) {
        return getAppIcon(context, context.getPackageName());
    }

    /** Application icon of {@code packageName}, or null. */
    public static Drawable getAppIcon(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.applicationInfo.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Source apk path of this application, or null. */
    public static String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /** Source apk path of {@code packageName}, or null. */
    public static String getAppPath(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Version name of this application, or null. */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /** Version name of {@code packageName}, or null. */
    public static String getAppVersionName(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Version code of this application, or -1. */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /** Version code of {@code packageName}, or -1. */
    public static int getAppVersionCode(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return -1;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return -1;
            }
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** @return true when this application is a system app. */
    public static boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /** @return true when {@code packageName} is a system app. */
    public static boolean isSystemApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return false;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            return applicationInfo != null && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Signatures of this application, or null. */
    public static Signature[] getAppSignature(Context context) {
        return getAppSignature(context, context.getPackageName());
    }

    /** Signatures of {@code packageName}, or null. */
    public static Signature[] getAppSignature(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName.trim())) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** @return true when this app has a foreground process. */
    public static boolean isAppForeground(Context context) {
        return ProcessUtils.isAppForeground(context);
    }

    /** @return true when {@code packageName} is the current foreground package. */
    @Deprecated
    public static boolean isForegroundPackage(Context context, String packageName) {
        return ProcessUtils.isForegroundPackage(context, packageName);
    }

    /** Current foreground package. */
    public String getForegroundPackage(Context context) {
        return ProcessUtils.getForegroundPackage(context);
    }
    /** Detailed information about an installed application. */
    public static class AppInfo {

        private String name;
        private Drawable icon;
        private String packageName;
        private String packagePath;
        private String versionName;
        private int versionCode;
        private boolean isSystem;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public boolean isSystem() {
            return isSystem;
        }

        public void setSystem(boolean isSystem) {
            this.isSystem = isSystem;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getPackagePath() {
            return packagePath;
        }

        public void setPackagePath(String packagePath) {
            this.packagePath = packagePath;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public AppInfo(String packageName, String name, Drawable icon, String packagePath,
                       String versionName, int versionCode, boolean isSystem) {
            setName(name);
            setIcon(icon);
            setPackageName(packageName);
            setPackagePath(packagePath);
            setVersionName(versionName);
            setVersionCode(versionCode);
            setSystem(isSystem);
        }

        @Override
        public String toString() {
            return "App包名：" + getPackageName() + "\nApp名称：" + getName() + "\nApp图标：" + getIcon()
                    + "\nApp路径：" + getPackagePath() + "\nApp版本号：" + getVersionName()
                    + "\nApp版本码：" + getVersionCode() + "\n是否系统App：" + isSystem() + "\n";
        }
    }

    /** {@link AppInfo} for this application. */
    public static AppInfo getAppInfo(Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    /** {@link AppInfo} for {@code packageName}. */
    public static AppInfo getAppInfo(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return getBean(packageManager, packageManager.getPackageInfo(packageName, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Builds an {@link AppInfo} from the package manager result. */
    private static AppInfo getBean(PackageManager packageManager, PackageInfo packageInfo) {
        if (packageManager == null || packageInfo == null) {
            return null;
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        return new AppInfo(packageInfo.packageName, applicationInfo.loadLabel(packageManager).toString(),
                applicationInfo.loadIcon(packageManager), applicationInfo.sourceDir,
                packageInfo.versionName, packageInfo.versionCode, (applicationInfo.flags & 1) != 0);
    }

    /** {@link AppInfo} for every installed package. */
    public static List<AppInfo> getAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            AppInfo appInfo = getBean(packageManager, packageInfo);
            if (appInfo != null) {
                list.add(appInfo);
            }
        }
        return list;
    }

    /** Cleans the cache and data directories for every supplied path. */
    public static boolean cleanAppData(Context context, String... dirPaths) {
        File[] dirs = new File[dirPaths.length];
        int length = dirPaths.length;
        int index = 0;
        int position = 0;
        while (index < length) {
            dirs[position] = new File(dirPaths[index]);
            index++;
            position++;
        }
        return cleanAppData(context, dirs);
    }

    /** Cleans the cache and data directories for every supplied file. */
    public static boolean cleanAppData(Context context, File... dirs) {
        boolean result = CleanUtils.cleanExternalCache(context) & CleanUtils.cleanInternalCache(context)
                & CleanUtils.cleanInternalDbs(context) & CleanUtils.cleanInternalSp(context)
                & CleanUtils.cleanInternalFiles(context);
        for (File dir : dirs) {
            result &= CleanUtils.cleanCustomDir(dir);
        }
        return result;
    }

    /** Version name of this application resolved from the application context. */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    /** Version name of {@code packageName} resolved from the application context. */
    public static String getVersionName(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo == null) {
                return null;
            }
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
            return null;
        }
    }

    /** Application label of this application, or an empty string. */
    public static String getModuleName(Context context) {
        String label = "";
        try {
            String resolved = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .applicationInfo.loadLabel(context.getPackageManager()).toString();
            try {
                return TextUtils.isEmpty(resolved) ? "" : resolved;
            } catch (Exception e) {
                label = resolved;
                e.printStackTrace();
                return label;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return label;
        }
    }
}
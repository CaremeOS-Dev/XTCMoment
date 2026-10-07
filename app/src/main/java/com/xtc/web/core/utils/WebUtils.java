package com.xtc.web.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.SystemPropertyUtil;
import com.xtc.utils.system.WatchModelUtil;
import com.xtc.web.core.CoreConstants;
import com.xtc.moment.R;
import com.xtc.web.core.data.bean.CacheModuleSwitchExtra;

import java.io.File;
import java.util.List;

/** Web 容器通用工具：能力检测、错误页构造、UA 解析与缓存配置读取。 */
public class WebUtils {

    private static final String TAG = CoreConstants.TAG + WebUtils.class.getSimpleName();

    /** 当前设备是否具备 H5 渲染能力（高通平台看 WebView 包，展讯平台看系统属性）。 */
    public static boolean checkSupport(Context context) {
        PackageInfo webViewPackage = null;
        if (WatchModelUtil.isGaotong()) {
            try {
                webViewPackage = context.getPackageManager().getPackageInfo("com.android.webview", 0);
            } catch (PackageManager.NameNotFoundException ignored) {
                // WebView 未安装时保持为 null
            }
            boolean supported = webViewPackage != null;
            Log.d(TAG, "check support web result = " + supported);
            return supported;
        }
        String h5Property = SystemPropertyUtil.get(CoreConstants.SystemProp.H5_PROP, null);
        Log.d(TAG, "check support zhanxun web result = " + h5Property);
        return CoreConstants.SystemProp.IS_TRUE.equals(h5Property);
    }

    /** 构造“不支持 H5”提示页。 */
    public static RelativeLayout getUnSupportWebView(Context context, ViewGroup parent) {
        return (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_unsupport_web, parent, false);
    }

    /** 构造“加载失败/无网络”重试页。 */
    public static RelativeLayout getNoNetWork(Context context, ViewGroup parent, View.OnClickListener retryListener) {
        RelativeLayout root = (RelativeLayout) LayoutInflater.from(context)
                .inflate(R.layout.layout_load_fail, parent, false);
        root.findViewById(R.id.ll_retry_load).setOnClickListener(retryListener);
        return root;
    }

    /** 铺满父容器的布局参数。 */
    public static RelativeLayout.LayoutParams getMatchLayoutParams() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    /** 判断指定包名是否已安装。 */
    public static boolean isInstallApp(Context context, String packageName) {
        List<PackageInfo> installedPackages;
        if (!TextUtils.isEmpty(packageName)
                && (installedPackages = context.getPackageManager().getInstalledPackages(0)) != null) {
            for (int index = 0; index < installedPackages.size(); index++) {
                String installedPackage = installedPackages.get(index).packageName;
                LogUtil.d(TAG, "current version has the app " + installedPackage);
                if (installedPackage.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 读取高通平台的 WebView 缓存大小配置。 */
    public static CacheModuleSwitchExtra getGaotongCacheSize(Context context) {
        String extraJson = WatchAccountBase.queryModuleSwitchExtraByInt(context,
                CoreConstants.ModuleSwitch.MODULE_SWITCH_CACHE, "");
        LogUtil.i(TAG, "isCacheOpen:" + extraJson);
        return JSONUtil.fromJSON(extraJson, CacheModuleSwitchExtra.class);
    }

    /** 外置存储根目录，未挂载时返回 null。 */
    public static String getSdcardPath() {
        File externalStorage = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStorageDirectory()
                : null;
        if (externalStorage != null) {
            return externalStorage.toString();
        }
        return null;
    }

    /** 取文件扩展名（不含点），无扩展名时返回空串。 */
    public static String getFileExtension(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int dotIndex = path.lastIndexOf('.');
        return (dotIndex == -1 || path.lastIndexOf(File.separator) >= dotIndex) ? "" : path.substring(dotIndex + 1);
    }

    /** 从 UA 中解析 Chrome 主版本号，解析失败返回 -1。 */
    public static int getChromeVersion(WebSettings webSettings) {
        String userAgent = webSettings.getUserAgentString();
        return interceptVersion(userAgent, findTargetStrStartIndex(userAgent, CoreConstants.ChromeConstant.TARGET_STR));
    }

    /** 在字符串中查找目标子串的起始下标，未找到返回 -1。 */
    public static int findTargetStrStartIndex(String mainString, String targetString) {
        if (mainString.isEmpty()) {
            LogUtil.d(TAG, "mainString is empty!");
            return -1;
        }
        char[] mainChars = mainString.toCharArray();
        char[] targetChars = targetString.toCharArray();
        int searchLength = (mainChars.length - targetChars.length) + 1;
        int matchedLength = 0;
        for (int index = 0; index < searchLength; index++) {
            if (mainChars[index] == targetChars[matchedLength]) {
                int mainIndex = index;
                int targetIndex = matchedLength;
                for (int offset = 0; offset < targetChars.length; offset++) {
                    if (mainChars[mainIndex] != targetChars[targetIndex]) {
                        targetIndex = 0;
                        break;
                    }
                    mainIndex++;
                    targetIndex++;
                }
                if (targetIndex == targetChars.length) {
                    return mainIndex - targetChars.length;
                }
                matchedLength = targetIndex;
            }
        }
        return -1;
    }

    /** 从 "Chrome/xx" 之后截取两位主版本号。 */
    private static int interceptVersion(String userAgent, int targetStartIndex) {
        if (targetStartIndex == -1) {
            LogUtil.d(TAG, "not find targetString!!!");
            return -1;
        }
        int versionStartIndex = targetStartIndex + 7;
        String versionText = userAgent.substring(versionStartIndex,
                versionStartIndex + CoreConstants.ChromeConstant.CHROME_VERSION_LENGTH);
        LogUtil.d(TAG, "Chrome version is: " + versionText);
        return Integer.parseInt(versionText);
    }
}
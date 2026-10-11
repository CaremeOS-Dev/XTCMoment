package com.xtc.moment.module.theme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;

import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.ui.ScreenUtils;

import java.io.File;

/**
 * 主题自定义背景：负责壁纸的保存、读取与窗口背景的构建。
 *
 * <p>壁纸通过 {@code Window#setBackgroundDrawable} 绘制在内容视图之下，配合各页面根布局透明即可
 * 让全部界面统一使用自定义背景；上层叠加半透明黑色蒙版用于降低亮度、保证文字可读性。
 *
 * <p>缓存的位图不会被 {@code recycle()}：已挂到其它窗口背景上的 {@link BitmapDrawable} 仍可能
 * 在下一次绘制时引用它，回收会导致 “trying to use a recycled bitmap”。丢弃引用交给 GC 更安全。
 */
public class ThemeBackgroundHelper {

    private static final String TAG = "ThemeBackgroundHelper";

    /** 蒙版颜色，约 35% 黑，用于降低背景亮度保证可读性。 */
    private static final int DIM_COLOR = 0x59000000;

    /** 未启用自定义背景时的窗口背景色，与原有纯黑观感保持一致。 */
    private static final int DEFAULT_COLOR = 0xFF000000;

    private static final int JPEG_QUALITY = 90;

    private static Bitmap sWallpaperBitmap;
    private static String sCachedWallpaperPath;

    private ThemeBackgroundHelper() {
    }

    /** @return 自定义背景目录；{@link FileManager} 尚未初始化时按同样规则回退计算。 */
    private static String resolveRootPath(Context context) {
        String rootPath = FileManager.getMomentThemeRootPath();
        if (!TextUtils.isEmpty(rootPath)) {
            return rootPath;
        }
        File externalDir = context.getExternalFilesDir(null);
        if (externalDir == null) {
            return null;
        }
        return externalDir.getAbsolutePath() + File.separator + FileManager.NAME_MOMENT_THEME + File.separator;
    }

    /** @return 壁纸文件的默认落盘路径。 */
    public static String getDefaultWallpaperPath(Context context) {
        String rootPath = resolveRootPath(context);
        if (TextUtils.isEmpty(rootPath)) {
            return null;
        }
        return rootPath + FileManager.THEME_WALLPAPER_NAME;
    }

    /**
     * @return 当前自定义背景图片路径；优先取已保存路径，其次回退到默认落盘路径，都没有时返回空串。
     */
    public static String getWallpaperPath(Context context) {
        String path = SharedTool.getCustomBackgroundPath(context);
        if (!TextUtils.isEmpty(path) && FileUtils.exists(path)) {
            return path;
        }
        String defaultPath = getDefaultWallpaperPath(context);
        if (!TextUtils.isEmpty(defaultPath) && FileUtils.exists(defaultPath)) {
            return defaultPath;
        }
        return TextUtils.isEmpty(path) ? (defaultPath == null ? "" : defaultPath) : path;
    }

    /** @return 是否已设置过自定义背景（文件存在）。 */
    public static boolean hasWallpaper(Context context) {
        String path = getWallpaperPath(context);
        return !TextUtils.isEmpty(path) && FileUtils.exists(path);
    }

    /** @return 自定义背景是否处于启用状态且图片可用。 */
    public static boolean isEnabled(Context context) {
        return SharedTool.isEnableCustomBackground(context) && hasWallpaper(context);
    }

    public static void setEnabled(Context context, boolean enabled) {
        SharedTool.saveEnableCustomBackground(context, enabled);
    }

    /**
     * 把相册选中的图片压缩后保存为壁纸。
     *
     * @return 保存成功返回 {@code true}。
     */
    public static boolean saveWallpaper(Context context, String sourcePath) {
        if (TextUtils.isEmpty(sourcePath) || !FileUtils.exists(sourcePath)) {
            LogUtil.e(TAG, "saveWallpaper: source file not exist, path = " + sourcePath);
            return false;
        }
        String targetPath = getDefaultWallpaperPath(context);
        if (TextUtils.isEmpty(targetPath)) {
            LogUtil.e(TAG, "saveWallpaper: target dir unavailable");
            return false;
        }
        Bitmap bitmap = null;
        try {
            FileUtils.makeDirs(new File(targetPath).getParentFile());
            BitmapFactory.Options options = ImageUtil.getCommonBitmapOptions();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(sourcePath, options);
            options.inSampleSize = ImageUtil.caculateInSampleSize(options,
                    ScreenUtils.getScreenWidth(context), ScreenUtils.getScreenHeight(context));
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(sourcePath, options);
            if (bitmap == null) {
                LogUtil.e(TAG, "saveWallpaper: decode failed");
                return false;
            }
            if (!FileUtils.writeBitmap(bitmap, Bitmap.CompressFormat.JPEG, JPEG_QUALITY, targetPath)) {
                LogUtil.e(TAG, "saveWallpaper: write failed");
                return false;
            }
            bitmap.recycle();
            bitmap = null;
            SharedTool.saveCustomBackgroundPath(context, targetPath);
            dropCache();
            LogUtil.i(TAG, "saveWallpaper: success, path = " + targetPath);
            return true;
        } catch (Throwable e) {
            LogUtil.e(TAG, "saveWallpaper: error", e);
            return false;
        } finally {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    /** 清除已保存的壁纸与缓存。 */
    public static void clearWallpaper(Context context) {
        String path = getWallpaperPath(context);
        if (!TextUtils.isEmpty(path)) {
            FileUtils.deleteFile(path);
        }
        SharedTool.saveCustomBackgroundPath(context, "");
        dropCache();
    }

    /**
     * 刷新缓存：仅当壁纸路径变化或文件已不存在时丢弃缓存，避免每次页面回到前台都重新解码。
     */
    public static void refresh(Context context) {
        String path = getWallpaperPath(context);
        if (sWallpaperBitmap != null && TextUtils.equals(path, sCachedWallpaperPath) && FileUtils.exists(path)) {
            return;
        }
        dropCache();
    }

    /** 丢弃位图缓存，交给 GC 回收。 */
    private static void dropCache() {
        sWallpaperBitmap = null;
        sCachedWallpaperPath = null;
    }

    /** @return 壁纸位图，未启用或读取失败时返回 {@code null}。 */
    private static Bitmap getWallpaperBitmap(Context context) {
        String path = getWallpaperPath(context);
        if (TextUtils.isEmpty(path) || !FileUtils.exists(path)) {
            return null;
        }
        if (sWallpaperBitmap != null && !sWallpaperBitmap.isRecycled() && TextUtils.equals(path, sCachedWallpaperPath)) {
            return sWallpaperBitmap;
        }
        try {
            // 长期缓存的位图不能使用 inPurgeable，否则内存紧张时像素被回收会导致绘制崩溃。
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = FileUtils.readBitmap(path, options);
            if (bitmap == null) {
                LogUtil.e(TAG, "getWallpaperBitmap: decode failed, path = " + path);
                return null;
            }
            sWallpaperBitmap = bitmap;
            sCachedWallpaperPath = path;
            return sWallpaperBitmap;
        } catch (Throwable e) {
            LogUtil.e(TAG, "getWallpaperBitmap: error", e);
            return null;
        }
    }

    /** @return 当前应设置的窗口背景：启用时是壁纸加蒙版，否则为纯黑。 */
    public static Drawable buildWindowBackground(Context context) {
        if (!SharedTool.isEnableCustomBackground(context)) {
            return new ColorDrawable(DEFAULT_COLOR);
        }
        Bitmap bitmap = getWallpaperBitmap(context);
        if (bitmap == null) {
            return new ColorDrawable(DEFAULT_COLOR);
        }
        try {
            BitmapDrawable wallpaper = new BitmapDrawable(context.getResources(), bitmap);
            wallpaper.setGravity(Gravity.FILL);
            wallpaper.setFilterBitmap(true);
            return new LayerDrawable(new Drawable[]{wallpaper, new ColorDrawable(DIM_COLOR)});
        } catch (Throwable e) {
            LogUtil.e(TAG, "buildWindowBackground: error", e);
            return new ColorDrawable(DEFAULT_COLOR);
        }
    }

    /**
     * 把当前主题背景应用到 [activity] 的窗口。
     *
     * <p>半透明主题（如 H5 页面）保持原样，避免破坏透明效果。
     */
    public static void apply(Activity activity) {
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        if (window == null || isWindowTranslucent(activity)) {
            return;
        }
        try {
            window.setBackgroundDrawable(buildWindowBackground(activity));
            window.getDecorView().setTag(R.id.theme_background_applied, Boolean.TRUE);
        } catch (Throwable e) {
            LogUtil.e(TAG, "apply: error", e);
        }
    }

    /**
     * 启用自定义背景时应用壁纸；关闭时仅把「曾应用过壁纸」的页面恢复为默认纯黑。
     *
     * <p>未启用过时完全不动窗口背景，保留主题自带的 windowBackground（例如首页的启动图），
     * 使默认状态下的观感与改造前一致。
     */
    public static void applyIfEnabled(Activity activity) {
        if (activity == null) {
            return;
        }
        if (isEnabled(activity)) {
            apply(activity);
            return;
        }
        Window window = activity.getWindow();
        if (window == null) {
            return;
        }
        if (window.getDecorView().getTag(R.id.theme_background_applied) == null) {
            return;
        }
        apply(activity);
    }

    /** @return 当前窗口是否为半透明主题。 */
    private static boolean isWindowTranslucent(Activity activity) {
        TypedValue value = new TypedValue();
        boolean resolved = activity.getTheme()
                .resolveAttribute(android.R.attr.windowIsTranslucent, value, true);
        return resolved && value.data != 0;
    }
}

package com.xtc.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Status-bar / full-screen helpers.
 *
 * <p>Below API 21 the status bar is emulated by inserting a {@link StatusBarView}
 * into the decor view; on API 21+ the real window flags are used.
 */
public class BarUtils {

    /** Default alpha (out of 255) applied to the emulated status bar. */
    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;

    /** Flags used to draw the content below a translucent status bar. */
    private static final int LAYOUT_STABLE_FULLSCREEN =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

    private BarUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Placeholder view drawn in place of the system status bar on API 19/20. */
    public static class StatusBarView extends View {
        public StatusBarView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public StatusBarView(Context context) {
            super(context);
        }
    }

    /** Paints the status bar with the default alpha. */
    public static void setStatusBarColor(Activity activity, int color) {
        setStatusBarColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /** Paints the status bar, applying {@code alpha} on pre-Lollipop devices. */
    public static void setStatusBarColor(Activity activity, int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(applyAlpha(color, alpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            int childCount = decorView.getChildCount();
            if (childCount > 0) {
                int lastIndex = childCount - 1;
                if (decorView.getChildAt(lastIndex) instanceof StatusBarView) {
                    decorView.getChildAt(lastIndex).setBackgroundColor(applyAlpha(color, alpha));
                } else {
                    decorView.addView(createStatusBarView(activity, color, alpha));
                }
            } else {
                decorView.addView(createStatusBarView(activity, color, alpha));
            }
            setFitsSystemWindows(activity);
        }
    }

    /** Paints the content-view background, padding it below the status bar. */
    public static void setContentViewBackgroundColor(Activity activity, int color) {
        setContentViewBackgroundColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /** Variant of {@link #setContentViewBackgroundColor(Activity, int)} with alpha. */
    public static void setContentViewBackgroundColor(Activity activity, int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            contentView.setPadding(0, getStatusBarHeight(activity), 0, 0);
            contentView.setBackgroundColor(applyAlpha(color, alpha));
            setStatusBarTranslucent(activity);
        }
    }

    /** Fully transparent status bar. */
    public static void setStatusBarColorTransparent(Activity activity, int color) {
        setStatusBarColor(activity, color, 0);
    }

    /** @deprecated use {@link #setStatusBarColorTransparent(Activity, int)}. */
    @Deprecated
    public static void setStatusBarColorDeprecated(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        int childCount = decorView.getChildCount();
        if (childCount > 0) {
            int lastIndex = childCount - 1;
            if (decorView.getChildAt(lastIndex) instanceof StatusBarView) {
                decorView.getChildAt(lastIndex).setBackgroundColor(color);
            } else {
                decorView.addView(createStatusBarView(activity, color));
            }
        } else {
            decorView.addView(createStatusBarView(activity, color));
        }
        setFitsSystemWindows(activity);
    }

    /** Paints the status bar with the default alpha. */
    public static void setStatusBarColorDefault(Activity activity) {
        setStatusBarColorAlphaForContent(activity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /** Paints the content-view overlay used when the layout is drawn edge to edge. */
    public static void setStatusBarColorAlphaForContent(Activity activity, int alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setStatusBarTranslucent(activity);
        addContentViewStatusBarOverlay(activity, alpha);
    }

    /** Like {@link #setStatusBarColorAlphaForContent} but for a full-screen window. */
    public static void setStatusBarColorAlphaForFullscreen(Activity activity, int alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setStatusBarTransparentOverlay(activity);
        addContentViewStatusBarOverlay(activity, alpha);
    }

    /** Resets the status bar to the translucent default. */
    public static void resetStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setStatusBarTransparentOverlay(activity);
        setFitsSystemWindows(activity);
    }

    /** @deprecated use {@link #resetStatusBar(Activity)}. */
    @Deprecated
    public static void resetStatusBarDeprecated(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setFitsSystemWindows(activity);
        }
    }

    /** Pads the given view below the status bar and paints it. */
    public static void setStatusBarColorForView(Activity activity, View view) {
        setStatusBarColorForView(activity, 0, view);
    }

    /** Pads the given view below the status bar using the default alpha. */
    public static void setStatusBarColorDefaultForView(Activity activity, View view) {
        setStatusBarColorForView(activity, DEFAULT_STATUS_BAR_ALPHA, view);
    }

    /** Pads the given view below the status bar and paints it with {@code alpha}. */
    public static void setStatusBarColorForView(Activity activity, int alpha, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setStatusBarTranslucent(activity);
        addContentViewStatusBarOverlay(activity, alpha);
        if (view != null) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(0, getStatusBarHeight(activity), 0, 0);
        }
    }

    /** Default-alpha variant of {@link #setStatusBarColorForViewWithCompat}. */
    public static void setStatusBarColorDefaultForViewWithCompat(Activity activity, View view) {
        setStatusBarColorForViewWithCompat(activity, DEFAULT_STATUS_BAR_ALPHA, view);
    }

    /** Transparent variant of {@link #setStatusBarColorForViewWithCompat}. */
    public static void setStatusBarColorTransparentForView(Activity activity, View view) {
        setStatusBarColorForViewWithCompat(activity, 0, view);
    }

    /** Pads the view and, on API 19/20, removes the emulated status-bar view. */
    public static void setStatusBarColorForViewWithCompat(Activity activity, int alpha, View view) {
        setStatusBarColorForView(activity, alpha, view);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        removeEmulatedStatusBarView(activity);
    }

    /** Removes the emulated status-bar view added on API 19/20. */
    private static void removeEmulatedStatusBarView(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        int childCount = decorView.getChildCount();
        if (childCount > 0) {
            int lastIndex = childCount - 1;
            if (decorView.getChildAt(lastIndex) instanceof StatusBarView) {
                decorView.removeViewAt(lastIndex);
                ((ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0)).setPadding(0, 0, 0, 0);
            }
        }
    }

    /** Adds a translucent overlay view to the content view. */
    private static void addContentViewStatusBarOverlay(Activity activity, int alpha) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        if (contentView.getChildCount() > 1) {
            contentView.getChildAt(1).setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        } else {
            contentView.addView(createStatusBarViewWithArgbColor(activity, alpha));
        }
    }

    /** Builds a status-bar placeholder painted with an opaque colour. */
    private static StatusBarView createStatusBarView(Activity activity, int color) {
        StatusBarView statusBarView = new StatusBarView(activity);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));
        statusBarView.setBackgroundColor(color);
        return statusBarView;
    }

    /** Builds a status-bar placeholder painted with {@code color} at {@code alpha}. */
    private static StatusBarView createStatusBarView(Activity activity, int color, int alpha) {
        StatusBarView statusBarView = new StatusBarView(activity);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));
        statusBarView.setBackgroundColor(applyAlpha(color, alpha));
        return statusBarView;
    }

    /** Builds a status-bar placeholder painted with an ARGB alpha value. */
    private static StatusBarView createStatusBarViewWithArgbColor(Activity activity, int alpha) {
        StatusBarView statusBarView = new StatusBarView(activity);
        statusBarView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity)));
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        return statusBarView;
    }

    /** Lets the content view draw below the status bar. */
    private static void setFitsSystemWindows(Activity activity) {
        ViewGroup contentView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        contentView.setFitsSystemWindows(true);
        contentView.setClipToPadding(true);
    }

    /** Makes the window draw below a fully transparent status bar. */
    private static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(0);
            activity.getWindow().getDecorView().setSystemUiVisibility(LAYOUT_STABLE_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /** Makes both system bars translucent. */
    private static void setStatusBarTransparentOverlay(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(0);
            return;
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /** Height of the system status bar in pixels, or -1 when unavailable. */
    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return -1;
    }

    /** Blends {@code color} with black at {@code alpha} (out of 255). */
    private static int applyAlpha(int color, int alpha) {
        float factor = 1.0f - (alpha / 255.0f);
        int red = (int) (((color >> 16) & 255) * factor + 0.5d);
        int green = (int) (((color >> 8) & 255) * factor + 0.5d);
        int blue = (int) ((color & 255) * factor + 0.5d);
        return blue | (red << 16) | 0xFF000000 | (green << 8);
    }

    /** Draws the window below the status bar. */
    public static void setFullscreenLayout(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /** Hides the title bar and enables full screen. */
    public static void setNoTitleFullscreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /** Returns {@code true} when the window is not in full-screen mode. */
    public static boolean isFullscreen(Activity activity) {
        return (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                != WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /** Resolves {@code android.R.attr.actionBarSize} to pixels. */
    public static int getActionBarSize(Activity activity) {
        TypedValue typedValue = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }

    /** Expands the notification shade (or the settings panel). */
    public static void expandStatusBar(Context context, boolean openSettings) {
        String method;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            method = "expand";
        } else {
            method = openSettings ? "expandSettingsPanel" : "expandNotificationsPanel";
        }
        invokeStatusBarMethod(context, method);
    }

    /** Collapses the notification shade. */
    public static void collapseStatusBar(Context context) {
        invokeStatusBarMethod(context, Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN ? "collapse" : "collapsePanels");
    }

    /** Reflectively calls a method on the system StatusBarManager. */
    private static void invokeStatusBarMethod(Context context, String methodName) {
        try {
            Class.forName("android.app.StatusBarManager").getMethod(methodName, new Class[0])
                    .invoke(context.getSystemService("statusbar"), new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
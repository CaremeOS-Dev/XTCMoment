package com.xtc.moment.util;

import android.animation.ValueAnimator;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * 屏幕亮度控制工具。
 */
public class ScreenLightUtil {

    private static final int DEFAULT_BRIFHT_LIGH = 100;

    public static void setWindowBrightness(Window window, float brightness) {
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.screenBrightness = brightness;
        window.setAttributes(attributes);
    }

    public static int getCurrentBrightness(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_brightness", DEFAULT_BRIFHT_LIGH);
    }

    public static void startBrightnessAnimation(final Window window, float fromBrightness, float toBrightness,
            long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromBrightness, toBrightness);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setWindowBrightness(window, ((Float) animation.getAnimatedValue()).floatValue());
            }
        });
        animator.start();
    }
}
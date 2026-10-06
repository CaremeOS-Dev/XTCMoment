package com.xtc.ui.widget.animation.sprite;

import android.animation.Animator;
import android.animation.ValueAnimator;

/** Sprite 动画启动/停止的静态工具方法。 */
public class AnimationUtils {

    private AnimationUtils() {
    }

    public static void start(Animator animator) {
        if (animator == null || animator.isStarted()) {
            return;
        }
        animator.start();
    }

    public static void stop(Animator animator) {
        if (animator == null || animator.isRunning()) {
            return;
        }
        animator.end();
    }

    public static void start(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            sprite.start();
        }
    }

    public static void stop(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            sprite.stop();
        }
    }

    public static boolean isRunning(Sprite... sprites) {
        for (Sprite sprite : sprites) {
            if (sprite.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRunning(ValueAnimator animator) {
        return animator != null && animator.isRunning();
    }

    public static boolean isStarted(ValueAnimator animator) {
        return animator != null && animator.isStarted();
    }
}
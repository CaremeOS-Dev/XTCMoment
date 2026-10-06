package com.xtc.utils.ui;

import android.content.res.ColorStateList;

/**
 * Builds pressed/disabled {@link ColorStateList}s for the button widgets.
 *
 * <p>The decompiled code expresses the lightened/darkened variants as
 * {@code base - (-0xAA000000)} style arithmetic; the same expressions are kept
 * here so the produced colours are bit-for-bit identical.
 */
public class ColorUtil {

    /** {@code android.R.attr.state_enabled}. */
    private static final int STATE_ENABLED = 0x0101009e;
    /** {@code android.R.attr.state_checked}. */
    private static final int STATE_CHECKED = 0x010100a0;
    /** {@code android.R.attr.state_pressed}. */
    private static final int STATE_PRESSED = 0x010100a7;

    /**
     * Solid-button state list: enabled/checked states use the base colour, the
     * remaining states use lightened variants.
     */
    public static ColorStateList createSolidColorStateList(int baseColor) {
        int pressedCheckedColor = baseColor - 0x99000000;
        return new ColorStateList(
                new int[][]{
                        new int[]{-STATE_ENABLED, STATE_CHECKED},
                        new int[]{-STATE_ENABLED},
                        new int[]{STATE_PRESSED, -STATE_CHECKED},
                        new int[]{STATE_PRESSED, STATE_CHECKED},
                        new int[]{STATE_CHECKED},
                        new int[]{-STATE_CHECKED}
                },
                new int[]{
                        baseColor - 0xAA000000,
                        0xFFBABABA,
                        pressedCheckedColor,
                        pressedCheckedColor,
                        baseColor | 0xFF000000,
                        0xFFEEEEEE
                });
    }

    /** Flat-button state list with slightly different disabled colours. */
    public static ColorStateList createFlatColorStateList(int baseColor) {
        int pressedCheckedColor = baseColor - 0xD0000000;
        return new ColorStateList(
                new int[][]{
                        new int[]{-STATE_ENABLED, STATE_CHECKED},
                        new int[]{-STATE_ENABLED},
                        new int[]{STATE_CHECKED, STATE_PRESSED},
                        new int[]{-STATE_CHECKED, STATE_PRESSED},
                        new int[]{STATE_CHECKED},
                        new int[]{-STATE_CHECKED}
                },
                new int[]{
                        baseColor - 0xE1000000,
                        0x10000000,
                        pressedCheckedColor,
                        0x20000000,
                        pressedCheckedColor,
                        0x20000000
                });
    }
}
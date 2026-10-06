package com.xtc.ui.widget.util;

import android.support.animation.FloatPropertyCompat;
import android.view.View;

/** 弹簧动画使用的属性定义。 */
public class SpringAnimationUtils {

    public static final FloatPropertyCompat<View> FLOAT_PROPERTY_TRANSLATION_Y =
            new FloatPropertyCompat<View>("translationY") {
                @Override
                public float getValue(View view) {
                    return view.getTranslationY();
                }

                @Override
                public void setValue(View view, float value) {
                    view.setTranslationY(value);
                }
            };
}
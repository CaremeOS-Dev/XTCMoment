package com.xtc.moment.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xtc.log.LogUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * View 属性设置与 Glide 资源加载辅助。
 */
public class ViewUtils {

    private static final String TAG = "ViewUtils";

    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public static List<View> collectChildren(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        List<View> children = new ArrayList<>();
        for (int index = 0; index < childCount; index++) {
            children.add(viewGroup.getChildAt(index));
        }
        return children;
    }

    public static void setScaleX(View view, float scaleX, float fallback) {
        if (view == null) {
            LogUtil.d(TAG, "setScaleX:view is null");
            return;
        }
        if (Float.isNaN(scaleX)) {
            LogUtil.d(TAG, "setScaleX:scaleX=" + scaleX);
            view.setScaleX(fallback);
            return;
        }
        view.setScaleX(scaleX);
    }

    public static void setScaleX(View view, float scaleX) {
        setScaleX(view, scaleX, 1.0f);
    }

    public static void setScaleY(View view, float scaleY) {
        setScaleY(view, scaleY, 1.0f);
    }

    public static void setScaleY(View view, float scaleY, float fallback) {
        if (view == null) {
            LogUtil.d(TAG, "setScaleX:view is null");
            return;
        }
        if (Float.isNaN(scaleY)) {
            LogUtil.d(TAG, "setScaleX:scaleY=" + scaleY);
            view.setScaleY(fallback);
            return;
        }
        view.setScaleY(scaleY);
    }

    public static void setScale(View view, float scale) {
        setScaleX(view, scale, 1.0f);
        setScaleY(view, scale, 1.0f);
    }

    public static void setAlpha(View view, float alpha, float fallback) {
        if (view == null) {
            LogUtil.d(TAG, "setAlpha:view is null");
            return;
        }
        if (Float.isNaN(alpha)) {
            LogUtil.d(TAG, "setAlpha:alpha=" + alpha);
            view.setAlpha(fallback);
            return;
        }
        view.setAlpha(alpha);
    }

    public static void setTranslationY(View view, float translationY, float fallback) {
        if (view == null) {
            LogUtil.d(TAG, "setTranslationY:view is null");
            return;
        }
        if (Float.isNaN(translationY)) {
            LogUtil.d(TAG, "setTranslationY:translationY=" + translationY);
            view.setTranslationY(fallback);
            return;
        }
        view.setTranslationY(translationY);
    }

    public static void setVisibility(Context context, View view, int visibility) {
        if (checkActivityDestroy(context, view)) {
            LogUtil.d(TAG, "setVisibility:view is null");
        } else if (view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    public static void setGlideResBg(Context context, final View view, int resId) {
        if (checkActivityDestroy(context, view)) {
            return;
        }
        Glide.with(context).load(Integer.valueOf(resId)).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                view.setBackground(resource);
            }
        });
    }

    public static void setGlideResBgWithAlpha(Context context, final View view, int resId) {
        if (checkActivityDestroy(context, view)) {
            return;
        }
        Glide.with(context).load(Integer.valueOf(resId))
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        view.setBackground(resource);
                    }
                });
    }

    public static void setGlideImgWithAlpha(Context context, ImageView imageView, int resId) {
        if (checkActivityDestroy(context, imageView)) {
            return;
        }
        Glide.with(context).load(Integer.valueOf(resId))
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                .into(imageView);
    }

    private static boolean checkActivityDestroy(Context context, View view) {
        if (view != null && context != null && (context instanceof Activity)) {
            Activity activity = (Activity) context;
            if (!activity.isDestroyed() && !activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
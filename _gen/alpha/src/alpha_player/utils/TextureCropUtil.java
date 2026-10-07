package com.ss.ugc.android.alpha_player.utils;

import com.ss.ugc.android.alpha_player.model.ScaleType;

/* JADX INFO: loaded from: classes2.dex */
public class TextureCropUtil {
    private static float[] a(float f, float f2, float f3, float f4) {
        float f5 = (f * 2.0f) - 1.0f;
        float f6 = (f4 * 2.0f) - 1.0f;
        float f7 = 1.0f - (f3 * 2.0f);
        float f8 = 1.0f - (f2 * 2.0f);
        return new float[]{f5, f6, 0.0f, 0.5f, 0.0f, f7, f6, 0.0f, 1.0f, 0.0f, f5, f8, 0.0f, 0.5f, 1.0f, f7, f8, 0.0f, 1.0f, 1.0f};
    }

    private static float[] b(float f, float f2, float f3, float f4) {
        float f5 = (f / 2.0f) + 0.5f;
        float f6 = f4 + 0.0f;
        float f7 = 1.0f - (f3 / 2.0f);
        float f8 = 1.0f - f2;
        return new float[]{-1.0f, -1.0f, 0.0f, f5, f6, 1.0f, -1.0f, 0.0f, f7, f6, -1.0f, 1.0f, 0.0f, f5, f8, 1.0f, 1.0f, 0.0f, f7, f8};
    }

    public static float[] a(ScaleType scaleType, float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        float f7;
        float f8 = f / f2;
        float f9 = f3 / f4;
        float f10 = 0.0f;
        if (f8 > f9) {
            f6 = (1.0f - (f2 / (f / f9))) / 2.0f;
            f5 = 0.0f;
        } else {
            f5 = (1.0f - (f / (f2 * f9))) / 2.0f;
            f6 = 0.0f;
        }
        switch (scaleType) {
            case ScaleAspectFitCenter:
                if (f8 > f9) {
                    f7 = (1.0f - ((f2 * f9) / f)) / 2.0f;
                } else {
                    f10 = (1.0f - ((f / f9) / f2)) / 2.0f;
                    f7 = 0.0f;
                }
                return a(f7, f10, f7, f10);
            case ScaleAspectFill:
                return b(f5, f6, f5, f6);
            case TopFill:
                return b(f5, 0.0f, f5, f6 * 2.0f);
            case BottomFill:
                return b(f5, f6 * 2.0f, f5, 0.0f);
            case LeftFill:
                return b(0.0f, f6, f5 * 2.0f, f6);
            case RightFill:
                return b(f5 * 2.0f, f6, 0.0f, f6);
            case TopFit:
                return a(0.0f, 0.0f, 0.0f, ((1.0f - ((f / f9) / f2)) / 2.0f) * 2.0f);
            case BottomFit:
                return a(0.0f, ((1.0f - ((f / f9) / f2)) / 2.0f) * 2.0f, 0.0f, 0.0f);
            case LeftFit:
                return a(0.0f, 0.0f, ((1.0f - ((f2 * f9) / f)) / 2.0f) * 2.0f, 0.0f);
            case RightFit:
                return a(((1.0f - ((f2 * f9) / f)) / 2.0f) * 2.0f, 0.0f, 0.0f, 0.0f);
            default:
                return b(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
}

package com.xtc.anim.alphaplayer.utils;

import com.xtc.anim.alphaplayer.model.ScaleType;

/**
 * 纹理裁剪工具：按缩放模式计算顶点坐标与纹理坐标。
 */
public class TextureCropUtil {

    /** 生成居中裁剪的顶点与纹理坐标数组。 */
    private static float[] buildCenterCrop(float left, float top, float right, float bottom) {
        float x0 = (left * 2.0f) - 1.0f;
        float y1 = (bottom * 2.0f) - 1.0f;
        float x1 = 1.0f - (right * 2.0f);
        float y0 = 1.0f - (top * 2.0f);
        return new float[]{
                x0, y1, 0.0f, 0.5f, 0.0f,
                x1, y1, 0.0f, 1.0f, 0.0f,
                x0, y0, 0.0f, 0.5f, 1.0f,
                x1, y0, 0.0f, 1.0f, 1.0f};
    }

    /** 生成按比例填充的顶点与纹理坐标数组。 */
    private static float[] buildFill(float u0, float v0, float u1, float v1) {
        float uCenter = (u0 / 2.0f) + 0.5f;
        float vTop = v1 + 0.0f;
        float uEdge = 1.0f - (u1 / 2.0f);
        float vBottom = 1.0f - v0;
        return new float[]{
                -1.0f, -1.0f, 0.0f, uCenter, vTop,
                1.0f, -1.0f, 0.0f, uEdge, vTop,
                -1.0f, 1.0f, 0.0f, uCenter, vBottom,
                1.0f, 1.0f, 0.0f, uEdge, vBottom};
    }

    /** 根据缩放模式与画面/视频尺寸计算纹理坐标。 */
    public static float[] getTextureCrop(ScaleType scaleType, float viewWidth, float viewHeight,
                                         float videoWidth, float videoHeight) {
        float viewRatio = viewWidth / viewHeight;
        float videoRatio = videoWidth / videoHeight;
        float offsetX;
        float offsetY;
        if (viewRatio > videoRatio) {
            offsetY = (1.0f - (viewHeight / (viewWidth / videoRatio))) / 2.0f;
            offsetX = 0.0f;
        } else {
            offsetX = (1.0f - (viewWidth / (viewHeight * videoRatio))) / 2.0f;
            offsetY = 0.0f;
        }
        float fitOffsetX = 0.0f;
        float fitOffsetY = 0.0f;
        switch (scaleType) {
            case ScaleAspectFitCenter:
                if (viewRatio > videoRatio) {
                    fitOffsetX = (1.0f - ((viewHeight * videoRatio) / viewWidth)) / 2.0f;
                } else {
                    fitOffsetY = (1.0f - ((viewWidth / videoRatio) / viewHeight)) / 2.0f;
                    fitOffsetX = 0.0f;
                }
                return buildCenterCrop(fitOffsetX, fitOffsetY, fitOffsetX, fitOffsetY);
            case ScaleAspectFill:
                return buildFill(offsetX, offsetY, offsetX, offsetY);
            case TopFill:
                return buildFill(offsetX, 0.0f, offsetX, offsetY * 2.0f);
            case BottomFill:
                return buildFill(offsetX, offsetY * 2.0f, offsetX, 0.0f);
            case LeftFill:
                return buildFill(0.0f, offsetY, offsetX * 2.0f, offsetY);
            case RightFill:
                return buildFill(offsetX * 2.0f, offsetY, 0.0f, offsetY);
            case TopFit:
                return buildCenterCrop(0.0f, 0.0f, 0.0f,
                        ((1.0f - ((viewWidth / videoRatio) / viewHeight)) / 2.0f) * 2.0f);
            case BottomFit:
                return buildCenterCrop(0.0f,
                        ((1.0f - ((viewWidth / videoRatio) / viewHeight)) / 2.0f) * 2.0f, 0.0f, 0.0f);
            case LeftFit:
                return buildCenterCrop(0.0f, 0.0f,
                        ((1.0f - ((viewHeight * videoRatio) / viewWidth)) / 2.0f) * 2.0f, 0.0f);
            case RightFit:
                return buildCenterCrop(
                        ((1.0f - ((viewHeight * videoRatio) / viewWidth)) / 2.0f) * 2.0f, 0.0f, 0.0f, 0.0f);
            default:
                return buildFill(0.0f, 0.0f, 0.0f, 0.0f);
        }
    }
}
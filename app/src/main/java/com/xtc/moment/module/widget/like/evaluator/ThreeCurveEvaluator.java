package com.xtc.moment.module.widget.like.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 三阶贝塞尔曲线求值器，用于点赞飘心动画路径。
 */
public class ThreeCurveEvaluator implements TypeEvaluator<PointF> {

    private final PointF mControlP1;
    private final PointF mControlP2;

    public ThreeCurveEvaluator(PointF controlP1, PointF controlP2) {
        this.mControlP1 = controlP1;
        this.mControlP2 = controlP2;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        PointF point = new PointF();
        float inverse = 1.0f - fraction;
        float inverseSquare = inverse * inverse;
        float fractionSquare = fraction * fraction;
        float inverseCubeWeight = inverseSquare * inverse;
        float fractionCubeWeight = fractionSquare * fraction;
        float controlWeight1 = inverseSquare * 3.0f * fraction;
        float controlWeight2 = fractionSquare * 3.0f * inverse;
        point.x = inverseCubeWeight * startValue.x + controlWeight1 * this.mControlP1.x
                + controlWeight2 * this.mControlP2.x + fractionCubeWeight * endValue.x;
        point.y = inverseCubeWeight * startValue.y + controlWeight1 * this.mControlP1.y
                + controlWeight2 * this.mControlP2.y + fractionCubeWeight * endValue.y;
        return point;
    }
}
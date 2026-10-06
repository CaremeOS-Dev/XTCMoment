package com.xtc.moment.module.widget.like.evaluator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.SparseArray;

import java.util.Random;

/**
 * 点赞动画路径缓存，超过阈值后随机复用历史路径。
 */
public class CurveEvaluatorRecord {

    private static final int MAX_PATH_COUNTS = 100;

    private final Random mRandom = new Random();
    private final SparseArray<TypeEvaluator<PointF>> mPathArray = new SparseArray<>();
    private int mCurrentPathCounts;

    public TypeEvaluator<PointF> getCurrentPath(PointF start, PointF end) {
        this.mCurrentPathCounts++;
        if (this.mCurrentPathCounts > MAX_PATH_COUNTS) {
            TypeEvaluator<PointF> evaluator =
                    this.mPathArray.get(Math.abs(this.mRandom.nextInt() % MAX_PATH_COUNTS) + 1);
            return evaluator == null ? createEvaluator(start, end) : evaluator;
        }
        return createEvaluator(start, end);
    }

    private TypeEvaluator<PointF> createEvaluator(PointF start, PointF end) {
        TypeEvaluator<PointF> evaluator = createPath(start, end);
        this.mPathArray.put(this.mCurrentPathCounts, evaluator);
        return evaluator;
    }

    private TypeEvaluator<PointF> createPath(PointF start, PointF end) {
        return new ThreeCurveEvaluator(start, end);
    }

    public void destroy() {
        SparseArray<TypeEvaluator<PointF>> pathArray = this.mPathArray;
        if (pathArray != null) {
            pathArray.clear();
            this.mCurrentPathCounts = 0;
        }
    }
}
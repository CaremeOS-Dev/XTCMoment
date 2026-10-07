package com.xtc.web.client.loading;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xtc.ui.widget.animation.indicator.LoadingAnim;
import com.xtc.moment.R;
import com.xtc.web.core.loading.LoadingView;

/** 小号加载动画遮罩（帧动画），H5 页面默认使用。 */
public class SmallLoadingView extends LoadingView {

    private static final int CENTER_ICON_SIZE = 60;

    private AnimationDrawable animDrawable;

    public SmallLoadingView(Context context) {
        this(context, null);
    }

    public SmallLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setBackgroundColor(0xFF000000);
        LayoutInflater.from(context).inflate(R.layout.layout_loading, this);
        ImageView imageView = (ImageView) findViewById(R.id.iv_center);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CENTER_ICON_SIZE,
                CENTER_ICON_SIZE);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(layoutParams);
        this.animDrawable = new LoadingAnim(context).createAnim();
        imageView.setBackground(this.animDrawable);
    }

    @Override
    public void startLoading() {
        this.animDrawable.start();
    }

    @Override
    public void stopLoading() {
        this.animDrawable.stop();
    }
}
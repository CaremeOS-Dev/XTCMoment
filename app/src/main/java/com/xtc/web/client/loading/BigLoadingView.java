package com.xtc.web.client.loading;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.xtc.ui.widget.animation.sprite.LoadingAnimSprite;
import com.xtc.moment.R;
import com.xtc.web.core.loading.LoadingView;

/** 大号加载动画遮罩（精灵动画）。 */
public class BigLoadingView extends LoadingView {

    private LoadingAnimSprite animDrawable;
    private ImageView ivCenter;

    public BigLoadingView(Context context) {
        this(context, null);
    }

    public BigLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_loading, this);
        this.ivCenter = (ImageView) findViewById(R.id.iv_center);
        View animView = findViewById(R.id.view_anim);
        this.animDrawable = new LoadingAnimSprite(context);
        animView.setBackground(this.animDrawable);
    }

    public void setCenterImage(Drawable drawable) {
        this.ivCenter.setImageDrawable(drawable);
    }

    public void setGroupScale(float scale) {
        this.animDrawable.setGroupScale(scale);
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
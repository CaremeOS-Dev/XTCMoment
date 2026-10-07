package com.xtc.ui.widget.animation.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.xtc.moment.R;
import com.xtc.ui.widget.animation.radarview.interfaces.ScannerInterface;
import com.xtc.virtualselfapi.constants.Constants;

/** 雷达扫描组合控件：同心圆背景 + 扫描扇形 + 主/跳转头像。 */
public class RadarScannerView extends RelativeLayout {
    private String TAG;
    private ButtomCircleView bcvRadarview;
    private Context context;
    private ImageView imageView;
    private boolean isRedColor;
    private ImageView ivJump;
    private ImageView ivMain;
    private Drawable jumpPortrait;
    private Drawable mainPortrait;
    private ScannerView scannerView;

    public RadarScannerView(Context context) {
        this(context, null);
    }

    public RadarScannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarScannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.TAG = "RadarScannerView";
        this.context = context;
        initAtrrs(attrs);
        initView();
        initData();
    }

    private void initAtrrs(AttributeSet attrs) {
        TypedArray attributes = this.context.obtainStyledAttributes(attrs, R.styleable.RadarScannerView);
        this.isRedColor = attributes.getBoolean(R.styleable.RadarScannerView_isRed, false);
        this.mainPortrait = attributes.getDrawable(R.styleable.RadarScannerView_mainPortrait);
        this.jumpPortrait = attributes.getDrawable(R.styleable.RadarScannerView_jumpPortrait);
        Log.i(this.TAG, "isRedColor=" + this.isRedColor);
        Log.i(this.TAG, "mainPortrait=" + this.mainPortrait);
        Log.i(this.TAG, "jumpPortrait=" + this.jumpPortrait);
    }

    private void initView() {
        View content = View.inflate(this.context, R.layout.radar_scanner_show, null);
        content.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        addView(content);
        this.scannerView = (ScannerView) content.findViewById(R.id.scv_radarview_scanning);
        this.bcvRadarview = (ButtomCircleView) content.findViewById(R.id.bcv_radarview_buttom);
        this.ivMain = (ImageView) content.findViewById(R.id.iv_radarview_main);
        this.ivJump = (ImageView) content.findViewById(R.id.iv_radarview_jump);
    }

    private void initData() {
        if (this.mainPortrait != null) {
            Log.i(this.TAG, "initDatamainPortrait=: " + this.mainPortrait);
            this.ivMain.setBackgroundDrawable(this.mainPortrait);
        }
        Drawable jumpPortrait = this.jumpPortrait;
        if (jumpPortrait != null) {
            this.ivJump.setBackgroundDrawable(jumpPortrait);
        }
        this.scannerView.setAnimationOnclickListener(new ScannerInterface() {
            @Override
            public void wasStopAni() {
            }

            @Override
            public void scannerStartAlpha() {
                RadarScannerView.this.startAlpha();
            }
        });
        Log.i(this.TAG, "initDatasetColor");
        this.scannerView.setRaderColor(this.isRedColor);
        this.bcvRadarview.setCircleColor(this.isRedColor);
    }

    private void setIvJump() {
        this.ivJump.setVisibility(0);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.1f, 0.0f, 1.1f, 1, 0.5f, 1, 1.0f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(Constants.DEFAULT_INIT_DELAY_TIME);
        this.ivJump.startAnimation(animationSet);
    }

    private void startAlpha() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000L);
        alphaAnimation.setFillAfter(true);
        this.scannerView.startAnimation(alphaAnimation);
    }

    public void stopScannerViewAnimation() {
        ScannerView scannerView = this.scannerView;
        if (scannerView != null) {
            scannerView.stopScannerAnimation();
        }
    }

    public void startScannerViewAnimation() {
        if (this.scannerView != null) {
            this.ivJump.setVisibility(8);
            this.scannerView.startScannerAnimation();
        }
    }

    public void setMainCircle(Drawable drawable) {
        ImageView imageView = this.ivMain;
        if (imageView != null) {
            imageView.setBackgroundDrawable(drawable);
        }
    }
}
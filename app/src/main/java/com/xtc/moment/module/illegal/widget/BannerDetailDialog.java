package com.xtc.moment.module.illegal.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.constant.ColorResource;
import com.xtc.moment.module.illegal.handler.IllegalMessageHandler;
import com.xtc.moment.module.illegal.net.IllegalHttpProxy;
import com.xtc.moment.module.illegal.net.bean.request.BannerNetBean;
import com.xtc.moment.module.illegal.net.bean.request.HighRiskRequestBean;
import com.xtc.moment.module.illegal.net.bean.request.InitViolationBean;
import com.xtc.moment.widget.TouchScrollbarRecyclerView;
import com.xtc.ui.widget.button.LongSolidButton;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 违规详情弹窗：展示违规原因、敏感内容列表以及处罚时间。
 */
public class BannerDetailDialog extends Dialog {

    private static final String TAG = "BannerDetailDialog";
    private static final int PAGE_SIZE = 5;
    private static final long RECORD_BTN_DELAY_TIME = 400;

    private Context mContext;
    private long endTime;
    private TouchScrollbarRecyclerView rvBannerContent;
    private LongSolidButton btnHintSure;
    private BannerDetailAdapter bannerDetailAdapter;
    private IllegalHttpProxy httpProxy;
    private HintIllegalContentDialog.HintClickListener hintClickListener;
    private boolean isScrolling;

    public BannerDetailDialog(Context context, long endTime) {
        this(context, R.style.dialog_default);
        this.mContext = context;
        this.endTime = endTime;
    }

    public BannerDetailDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setHintClickListener(HintIllegalContentDialog.HintClickListener hintClickListener) {
        this.hintClickListener = hintClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_banner_detail);
        this.bannerDetailAdapter = new BannerDetailAdapter(new BannerNetBean(), this.mContext, this.endTime);
        this.rvBannerContent = (TouchScrollbarRecyclerView) findViewById(R.id.rvBannerContent);
        this.btnHintSure = (LongSolidButton) findViewById(R.id.btn_hint_sure);
        this.rvBannerContent.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.rvBannerContent.setAdapter(this.bannerDetailAdapter);
        this.btnHintSure.getTv().setText(this.mContext.getResources().getString(R.string.common_i_know));
        this.btnHintSure.setBgColorIdArray(ColorResource.GRADIENT_BLUE);
        this.btnHintSure.setButtonSize(
                this.mContext.getResources().getDimensionPixelSize(R.dimen.double_button_width),
                this.mContext.getResources().getDimensionPixelSize(R.dimen.double_button_height));
        this.btnHintSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BannerDetailDialog.this.hintClickListener != null) {
                    BannerDetailDialog.this.hintClickListener.onConfirmClick();
                }
                BannerDetailDialog.this.dismiss();
            }
        });
        this.rvBannerContent.setScrollCallback(new TouchScrollbarRecyclerView.OnScrollCallback() {
            @Override
            public void onScrolling() {
                if (BannerDetailDialog.this.isScrolling) {
                    return;
                }
                BannerDetailDialog.this.isScrolling = true;
                BannerDetailDialog dialog = BannerDetailDialog.this;
                dialog.hideInputLayout(dialog.btnHintSure);
            }

            @Override
            public void onScrollIdle(boolean isUp) {
                BannerDetailDialog.this.isScrolling = false;
                BannerDetailDialog dialog = BannerDetailDialog.this;
                dialog.showInputLayout(dialog.btnHintSure);
            }
        });
        initData();
    }

    private void initData() {
        this.httpProxy = new IllegalHttpProxy(getContext());
        boolean highRiskIllegal = IllegalMessageHandler.getInstance(getContext()).isHighRiskIllegal();
        LogUtil.d(TAG, "是否违规" + highRiskIllegal);
        if (highRiskIllegal) {
            getHighRiskBannerContent();
        } else {
            getNormalBannerContent();
        }
    }

    private void getNormalBannerContent() {
        this.httpProxy.getBannerContent(new InitViolationBean(MomentApp.getWatchId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BannerNetBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getNormalBannerContent: " + throwable);
                        BannerDetailDialog.this.bannerDetailAdapter.setBannerNetBeans(null);
                    }

                    @Override
                    public void onNext(BannerNetBean bannerNetBean) {
                        BannerDetailDialog.this.bannerDetailAdapter.setBannerNetBeans(bannerNetBean);
                    }
                });
    }

    private void getHighRiskBannerContent() {
        HighRiskRequestBean requestBean = new HighRiskRequestBean(MomentApp.getWatchId());
        requestBean.setPageSize(PAGE_SIZE);
        requestBean.setPageNum(1);
        this.httpProxy.getHighRiskBannerContent(requestBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BannerNetBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getHighRiskBannerContent: " + throwable);
                        BannerDetailDialog.this.bannerDetailAdapter.setBannerNetBeans(null);
                    }

                    @Override
                    public void onNext(BannerNetBean bannerNetBean) {
                        BannerDetailDialog.this.bannerDetailAdapter.setBannerNetBeans(bannerNetBean);
                    }
                });
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(attributes);
    }

    /** 列表滚动时把确认按钮滑出屏幕。 */
    protected synchronized void hideInputLayout(View view) {
        ViewCompat.setTranslationY(view, 0.0f);
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).alpha(0.0f).translationY(view.getHeight())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationCancel(View view) {
                    }

                    @Override
                    public void onAnimationStart(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.GONE);
                    }
                }).setDuration(RECORD_BTN_DELAY_TIME).start();
    }

    /** 列表停止滚动后把确认按钮滑回。 */
    protected synchronized void showInputLayout(View view) {
        ViewCompat.setTranslationY(view, view.getHeight());
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).alpha(1.0f).translationY(0.0f)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationCancel(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                    }

                    @Override
                    public void onAnimationStart(View view) {
                        view.setVisibility(View.VISIBLE);
                    }
                }).setDuration(RECORD_BTN_DELAY_TIME).start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
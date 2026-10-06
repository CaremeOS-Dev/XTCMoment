package com.xtc.moment.share.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.widget.DialogUtil;
import com.xtc.moment.module.widget.coverflow.FlowCustomAlphaParam;
import com.xtc.moment.module.widget.coverflow.RecyclerCoverFlow;
import com.xtc.moment.share.callback.IShareCallback;
import com.xtc.moment.share.view.playvideo.PlayShareVideoDialog;
import com.xtc.moment.share.view.playvideo.PlayShareVideoPresenter;
import com.xtc.moment.util.ChatVideoUtil;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.ScreenUtils;
import com.xtc.moment.util.SystemUtil;
import com.xtc.moment.util.Utils;
import com.xtc.shareapi.share.bean.DialogBitmapArgs;
import com.xtc.shareapi.share.bean.MessageBitmapArgs;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.utils.ui.DimenUtil;
import com.xtc.utils.ui.ImageUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 分享弹窗管理：负责各类分享内容预览弹窗、加载动画与成功动画。
 */
public class ShareDialogManager {

    private static final String TAG = "ShareDialogManager";
    private static final int TIME_OUT = 60000;
    private static final int SUCCESS_TEXT_DELAY = 300;
    private static final int VIDEO_DURATION_BASE = 60000;

    private final LottieAnimationView animLoading;
    private final LottieAnimationView animSuccess;
    private final Context context;
    private Dialog dialog;
    private final View loadingView;
    private final TextView mTvCanViewedInMoment;
    public final IShareCallback shareCallback;
    private long startLoadingTime = 0;
    private boolean success;
    private final TextView tvSuccess;

    ShareDialogManager(final Context context, IShareCallback shareCallback) {
        this.context = context;
        this.shareCallback = shareCallback;
        this.loadingView = LayoutInflater.from(context).inflate(R.layout.dialog_share_loading, null, false);
        this.animLoading = (LottieAnimationView) this.loadingView.findViewById(R.id.anim_loading);
        this.animSuccess = (LottieAnimationView) this.loadingView.findViewById(R.id.anim_success);
        this.tvSuccess = (TextView) this.loadingView.findViewById(R.id.tv_success);
        this.mTvCanViewedInMoment = (TextView) this.loadingView.findViewById(R.id.tv_canViewedInMoment);
        if (SystemUtil.isHighMachine()) {
            this.animLoading.useHardwareAcceleration();
            this.animSuccess.useHardwareAcceleration();
        }
        this.animLoading.setScaleX(0.9f);
        this.animLoading.setScaleY(0.9f);
        this.animSuccess.setScaleY(0.9f);
        this.animSuccess.setScaleX(0.9f);
        initAnimListener();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                parseLottieAssetFile(context);
            }
        });
    }

    private void parseLottieAssetFile(Context context) {
        if (context == null) {
            return;
        }
        LottieComposition.Factory.fromAssetFileName(context, "shareloading.json",
                new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        if (animLoading == null) {
                            return;
                        }
                        animLoading.setComposition(composition);
                        animLoading.loop(true);
                    }
                });
        LottieComposition.Factory.fromAssetFileName(context, "sharesucceed.json",
                new OnCompositionLoadedListener() {
                    @Override
                    public void onCompositionLoaded(LottieComposition composition) {
                        if (animSuccess == null) {
                            return;
                        }
                        animSuccess.setComposition(composition);
                        animSuccess.loop(false);
                    }
                });
    }

    void showTextShareDialog(String title, String content) {
        showDialog(createTextSharePreview(title, content));
    }

    void showAppShareDial(String title, byte[] thumbData) {
        showDialog(createAppSharePreview(title, thumbData));
    }

    void showWebShareDialog(String title, byte[] thumbData) {
        showDialog(createWebSharePreview(title, thumbData));
    }

    void showImageShareDialog(String title, byte[] imageData, String imagePath, DialogBitmapArgs dialogBitmapArgs,
            MessageBitmapArgs messageBitmapArgs) {
        showDialog(createImageSharePreview(title, imageData, imagePath, dialogBitmapArgs, messageBitmapArgs));
    }

    void showMultiImageShareDialog(String title, List<String> imagePathList) {
        showDialog(createMultiImageSharePreview(title, imagePathList));
    }

    PlayShareVideoPresenter showVideoShareDialog(String title, String thumbnailPath, long duration, String videoPath) {
        PlayShareVideoDialog playShareVideoDialog = new PlayShareVideoDialog(this.context, this, title, thumbnailPath,
                duration, videoPath);
        showDialog(playShareVideoDialog);
        return playShareVideoDialog.getPresenter();
    }

    void cancelDialog() {
        DialogUtil.cancelDialog(this.dialog);
    }

    void shareSuccess() {
        this.success = true;
        this.animLoading.loop(false);
    }

    private void initAnimListener() {
        this.animSuccess.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimator.getCurrentPlayTime() >= SUCCESS_TEXT_DELAY) {
                    tvSuccess.setVisibility(View.VISIBLE);
                    mTvCanViewedInMoment.setVisibility(View.VISIBLE);
                }
            }
        });
        this.animSuccess.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                shareCallback.sendSuccessResult();
            }
        });
        this.animLoading.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                LogUtil.d(TAG, "onAnimationEnd() called with: animation = [" + animator + "]");
                startLoadingTime = 0L;
                if (success) {
                    animSuccess.playAnimation();
                    success = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                super.onAnimationRepeat(animator);
                long duration = SystemClock.elapsedRealtime() - startLoadingTime;
                LogUtil.d(TAG, "onAnimationRepeat() called with: duration = [" + duration + "]");
                if (duration > TIME_OUT) {
                    animLoading.cancelAnimation();
                    startLoadingTime = 0L;
                    shareCallback.sendOtherResponse(new Throwable("time out"));
                }
            }
        });
    }

    public void showLoadingDial() {
        this.tvSuccess.setVisibility(View.GONE);
        this.mTvCanViewedInMoment.setVisibility(View.GONE);
        showDialog(this.loadingView);
        this.animLoading.playAnimation();
        this.startLoadingTime = SystemClock.elapsedRealtime();
    }
    private View createTextSharePreview(String title, String content) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_share_text, null, false);
        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);
        titleView.setText(title);
        contentView.setText(content);
        initListener(view);
        return view;
    }

    private View createWebSharePreview(String title, byte[] thumbData) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_share_web, null, false);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_content);
        contentView.setText(title);
        RequestOptions options = new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random())));
        if (thumbData != null) {
            Glide.with(this.context).load(thumbData).apply(options).into(imageView);
        }
        initListener(view);
        return view;
    }

    private View createImageSharePreview(String title, byte[] imageData, String imagePath,
            DialogBitmapArgs dialogBitmapArgs, MessageBitmapArgs messageBitmapArgs) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_share_image, null, false);
        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_content);
        ImageView imageViewBg = (ImageView) view.findViewById(R.id.iv_content_bg);
        titleView.setText(title);
        if (imageData != null && imageData.length > 0) {
            glideWithInto(imageView, imageViewBg, imageData, dialogBitmapArgs);
        } else {
            loadPreviewImage(imagePath, imageView, imageViewBg, dialogBitmapArgs, messageBitmapArgs);
        }
        initListener(view);
        return view;
    }

    private View createMultiImageSharePreview(String title, List<String> imagePathList) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_multi_share_image, null, false);
        ((TextView) view.findViewById(R.id.tv_title)).setText(title);
        RecyclerCoverFlow recyclerCoverFlow = (RecyclerCoverFlow) view.findViewById(R.id.recyclerCoverFlow);
        recyclerCoverFlow.setFlowCustomAlphaParam(new FlowCustomAlphaParam(R.id.v_cover, 1.6f));
        recyclerCoverFlow.setIntervalDistance(DimenUtil.dp2px(this.context, 83.0f));
        recyclerCoverFlow.setIntervalRatio(0.45f);
        recyclerCoverFlow.setAdapter(new MultiImageShareAdapter(this.context, imagePathList));
        if (recyclerCoverFlow.getAdapter().getItemCount() > 1) {
            recyclerCoverFlow.scrollToPosition(1);
        }
        initListener(view).getRightButton().setText(this.context.getString(R.string.send) + "("
                + imagePathList.size() + ")");
        return view;
    }

    private View createVideoSharePreview(String title, String thumbnailPath, long duration, String videoPath) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_share_video, null, false);
        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        TextView durationView = (TextView) view.findViewById(R.id.share_video_duration);
        DecimalFormat decimalFormat = new DecimalFormat(Constants.Share.VIDEO_DURATION_PATTERN);
        long minutes = duration / VIDEO_DURATION_BASE;
        double durationValue = duration;
        double minutesInMillis = VIDEO_DURATION_BASE * minutes;
        durationView.setText(String.format(this.context.getString(R.string.dialog_share_video_duration),
                decimalFormat.format(minutes),
                decimalFormat.format(Math.round((durationValue / 1000.0d) - minutesInMillis))));
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_content);
        titleView.setText(title);
        loadPreviewVideo(thumbnailPath, imageView, videoPath);
        initListener(view);
        return view;
    }

    private void loadPreviewVideo(String thumbnailPath, ImageView imageView, String videoPath) {
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA);
        if (TextUtils.isEmpty(thumbnailPath)) {
            if (TextUtils.isEmpty(videoPath)) {
                LogUtil.w(TAG, "loadPreviewImage but videoPath is null return");
                return;
            }
            showCreateBitmap(videoPath, options, imageView);
            return;
        }
        Glide.with(ContextUtils.getContext()).load(thumbnailPath).apply(options).into(imageView);
    }

    private void showCreateBitmap(final String videoPath, final RequestOptions options, final ImageView imageView) {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return createBitmap(videoPath);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        if (bitmap == null) {
                            LogUtil.w(TAG, "loadPreviewImage but thumbnail is null return");
                        } else {
                            Glide.with(ContextUtils.getContext()).load(bitmap).apply(options).into(imageView);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "loadPreviewImage but error = " + throwable.getMessage());
                    }
                });
    }

    protected Bitmap createBitmap(String videoPath) {
        Bitmap videoThumbnail = ChatVideoUtil.getVideoThumbnail(videoPath);
        if (videoThumbnail == null || videoThumbnail.isRecycled()) {
            LogUtil.w(TAG, "loadPreviewImage bitmap is null or bitmap is recycled");
            return null;
        }
        int width = videoThumbnail.getWidth();
        int height = videoThumbnail.getHeight();
        float scale = ImageUtil.calculateInSampleSize(videoThumbnail.getWidth(), videoThumbnail.getHeight(),
                ScreenUtils.screenWidth(ContextUtils.getContext()), ScreenUtils.screenHeight(ContextUtils.getContext()));
        int targetWidth = (int) (width / scale);
        int targetHeight = (int) (height / scale);
        LogUtil.d(TAG, "max scale = " + scale + ", width = " + width + ", height = " + height);
        return scale > 0.0f ? ImageUtils.scaleBitmap(videoThumbnail, targetWidth, targetHeight) : videoThumbnail;
    }

    public void glideWithInto(final ImageView imageView, final ImageView imageViewBg, final byte[] imageData,
            final DialogBitmapArgs dialogBitmapArgs) {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap decoded = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                if (dialogBitmapArgs == null) {
                    return decoded;
                }
                Bitmap cropped = Bitmap.createBitmap(decoded, dialogBitmapArgs.getCutStart(),
                        dialogBitmapArgs.getCutTop(), dialogBitmapArgs.getCropWidth(),
                        dialogBitmapArgs.getCropHeight(), (Matrix) null, false);
                imageView.getLayoutParams().height = dialogBitmapArgs.getHeight();
                imageView.getLayoutParams().width = dialogBitmapArgs.getWidth();
                imageViewBg.getLayoutParams().height = dialogBitmapArgs.getHeight();
                imageViewBg.getLayoutParams().width = dialogBitmapArgs.getWidth();
                return cropped;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        Glide.with(ShareDialogManager.this.context).load(bitmap)
                                .transition(DrawableTransitionOptions.withCrossFade(
                                        new DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                                .into(imageView);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "glideWithInto error : ", throwable);
                    }
                });
    }

    private void loadPreviewImage(final String imagePath, final ImageView imageView, final ImageView imageViewBg,
            final DialogBitmapArgs dialogBitmapArgs, final MessageBitmapArgs messageBitmapArgs) {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                Bitmap decoded = BitmapFactory.decodeFile(imagePath);
                if (dialogBitmapArgs == null) {
                    return decoded;
                }
                LogUtil.d(TAG, "dialogBitmapArgs is not null");
                return Bitmap.createBitmap(decoded, dialogBitmapArgs.getCutStart(), dialogBitmapArgs.getCutTop(),
                        dialogBitmapArgs.getCropWidth(), dialogBitmapArgs.getCropHeight(), (Matrix) null, false);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        if (messageBitmapArgs != null) {
                            RelativeLayout.LayoutParams layoutParams =
                                    (RelativeLayout.LayoutParams) imageView.getLayoutParams();
                            layoutParams.width = messageBitmapArgs.getWidth();
                            layoutParams.height = messageBitmapArgs.getHeight();
                            imageView.setLayoutParams(layoutParams);
                            RelativeLayout.LayoutParams bgLayoutParams =
                                    (RelativeLayout.LayoutParams) imageViewBg.getLayoutParams();
                            bgLayoutParams.width = messageBitmapArgs.getWidth();
                            bgLayoutParams.height = messageBitmapArgs.getHeight();
                            imageViewBg.setLayoutParams(bgLayoutParams);
                        }
                        if (dialogBitmapArgs != null) {
                            LogUtil.d(TAG, "dialogBitmapArgs is not null");
                            imageView.getLayoutParams().height = dialogBitmapArgs.getHeight();
                            imageView.getLayoutParams().width = dialogBitmapArgs.getWidth();
                            imageViewBg.getLayoutParams().height = dialogBitmapArgs.getHeight();
                            imageViewBg.getLayoutParams().width = dialogBitmapArgs.getWidth();
                        }
                        Glide.with(ShareDialogManager.this.context).load(bitmap)
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA))
                                .into(imageView);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(TAG, "glideWithInto error : ", throwable);
                    }
                });
    }

    private View createAppSharePreview(String title, byte[] thumbData) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.dialog_moment_share_app, null, false);
        TextView contentView = (TextView) view.findViewById(R.id.tv_content);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_content);
        contentView.setText(title);
        RequestOptions options = new RequestOptions().signature(new ObjectKey(String.valueOf(Math.random())));
        if (thumbData != null && Utils.isContextEffective(this.context)) {
            Glide.with(this.context).load(thumbData).apply(options).into(imageView);
        }
        initListener(view);
        return view;
    }

    private void showDialog(Dialog dialog) {
        this.dialog = dialog;
        if (dialog == null) {
            return;
        }
        dialog.cancel();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = -1;
            attributes.height = -1;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
            dialog.show();
        }
    }

    private void showDialog(View view) {
        Dialog currentDialog = this.dialog;
        if (currentDialog == null) {
            this.dialog = new Dialog(this.context, R.style.dialog_default);
        } else {
            currentDialog.cancel();
        }
        Window window = this.dialog.getWindow();
        if (window != null) {
            window.setContentView(view);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = -1;
            attributes.height = -1;
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setAttributes(attributes);
            this.dialog.show();
        }
    }

    private DoubleFlatButton initListener(View view) {
        DoubleFlatButton doubleFlatButton = (DoubleFlatButton) view.findViewById(R.id.dfb_share);
        doubleFlatButton.setLeftBgColorIdArray(new int[]{R.color.color_3f4d61, R.color.color_222c3b});
        doubleFlatButton.setRightBgColorIdArray(new int[]{R.color.color_2bdbff, R.color.color_1794fa});
        TextView leftButton = doubleFlatButton.getLeftButton();
        leftButton.setText(R.string.cancel);
        doubleFlatButton.getRightButton().setText(R.string.send);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                shareCallback.cancelShare();
                cancelDialog();
            }
        });
        doubleFlatButton.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                showLoadingDial();
                shareCallback.sendShare();
            }
        });
        return doubleFlatButton;
    }
}
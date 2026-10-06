package com.xtc.moment.module.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.ShareImageMoment;
import com.xtc.moment.serve.IMomentServe;
import com.xtc.moment.serve.MomentServeImpl;
import com.xtc.moment.util.EventData;
import com.xtc.moment.util.ImageCompressUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.moment.util.ScreenLightUtil;
import com.xtc.ui.widget.UiConstants;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.FileUtils;
import com.xtc.utils.system.WatchModelUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotCallback;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

/**
 * Full screen photo preview with zoom, used from the moment list and the publish flow.
 */
public class PhotoPreviewActivity extends Activity {

    public static final String EXTRA_PHOTO_PATH = "extra_photo_path";
    public static final String EXTAR_LOCAL_PATH = "extra_local_path";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";
    public static final String EXTRA_MSG_ID = "extra_msg_id";
    public static final String TRACK_MD5_VALUE = "trackMd5Value";
    public static final String WATCH_ID = "watchId";
    public static final int PHOTO_PREVIEW_REQUEST_CODE = 110;

    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 360;

    private static final String TAG = "PhotoPreviewActivity";
    private static final String ENLARGE_PHOTO = "enlarge_photo";
    private static final int WAIT_TIME_QUAL = 20;
    private static final int WAIT_TIME_SPRD = 100;
    /** Corner radius applied to the previewed image, in pixels. */
    private static final int PREVIEW_CORNER_RADIUS = 12;

    private PhotoView mPvPreview;
    private PhotoView mPvSmallPic;
    private ImageView mIvLoading;
    private com.xtc.ui.widget.dialog.NormalIconDialog deleteDialog;

    private IMomentServe momentServe;
    private String mSavePath;
    private String momentId;
    private int waitTime;
    private boolean isBrightnessAnimationPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        initView();
        initData();
    }

    private void initView() {
        this.mPvPreview = (PhotoView) findViewById(R.id.pv_preview);
        this.mIvLoading = (ImageView) findViewById(R.id.iv_preview_loading);
        this.mPvSmallPic = (PhotoView) findViewById(R.id.pv_small_pic);
        this.mPvPreview.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finishPreview();
            }
        });
        this.mPvSmallPic.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finishPreview();
            }
        });
    }

    private void finishPreview() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PHOTO_PATH, this.mSavePath);
        setResult(PHOTO_PREVIEW_REQUEST_CODE, intent);
        finish();
    }

    private void initData() {
        this.waitTime = WatchModelUtil.isWatchModelY(WatchModelUtil.getWatchInnerModel()) ? WAIT_TIME_QUAL : WAIT_TIME_SPRD;
        this.momentServe = MomentServeImpl.getInstance(this);
        Intent intent = getIntent();
        if (intent == null) {
            LogUtil.d(TAG, "initData intent is null");
            return;
        }
        String localPath = intent.getStringExtra(EXTAR_LOCAL_PATH);
        String downloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL);
        String trackMd5 = intent.getStringExtra(TRACK_MD5_VALUE);
        String watchId = intent.getStringExtra(WATCH_ID);
        this.momentId = intent.getStringExtra(EXTRA_MSG_ID);
        String enlargePhoto = intent.getStringExtra(ENLARGE_PHOTO);
        if (enlargePhoto != null) {
            displayPhoto(enlargePhoto);
        }
        if (!TextUtils.isEmpty(localPath) && !localPath.contains(getPackageName())
                && PermissionStringUtils.lacksPermissions(this, PermissionStringUtils.FILE_PERMISSIONS)) {
            localPath = "";
        }
        LogUtil.i(TAG, "initData: localPath: " + localPath + ", downloadUrl: " + downloadUrl
                + ", momentId: " + this.momentId + " watchID:" + watchId);
        if (WatchModelUtil.isWatchModelY(WatchModelUtil.getWatchInnerModel())) {
            RequestOptions options = new RequestOptions()
                    .transform(new RoundedCorners(PREVIEW_CORNER_RADIUS))
                    .override(Integer.MIN_VALUE, Integer.MIN_VALUE);
            if (!TextUtils.isEmpty(localPath) && FileUtils.exists(localPath)) {
                showBig(localPath, options);
            } else if (!TextUtils.isEmpty(downloadUrl)) {
                showBig(downloadUrl, options);
            }
        } else if (!TextUtils.isEmpty(localPath) && FileUtils.exists(localPath)) {
            displayPhoto(localPath);
        } else {
            displayPhoto(downloadUrl);
        }
        setScreenshotMd5(localPath, downloadUrl, trackMd5, watchId);
    }

    /** Registers the data attached to a screenshot of this preview. */
    private void setScreenshotMd5(final String localPath, final String downloadUrl, final String trackMd5,
                                  final String watchId) {
        LogUtil.i(TAG, "setScreenshotMd5() trackMd5Value = [" + trackMd5 + "]");
        ScreenshotUtils.register(this, new ScreenshotCallback() {
            @Override
            public String getScreenshotCarryData() {
                if (!TextUtils.isEmpty(trackMd5)) {
                    return trackMd5;
                }
                if (!TextUtils.isEmpty(downloadUrl)) {
                    return ScreenshotUtils.initScreenshotData(downloadUrl, watchId);
                }
                return ScreenshotUtils.initScreenshotData(localPath, watchId);
            }
        });
    }

    /** Copies the downloaded image into the moment cache directory. */
    private class GetImageCacheAsyncTask extends AsyncTask<String, Void, File> {

        private final Context context;

        GetImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            try {
                File cached = Glide.with(context).downloadOnly().load(params[0])
                        .submit(Integer.MIN_VALUE, Integer.MIN_VALUE).get();
                if (cached == null) {
                    return null;
                }
                File target = new File((context.getExternalFilesDir(null).getAbsolutePath()
                        + File.separator + "Pre_Moment" + File.separator)
                        + System.currentTimeMillis() + PhotoTokenParam.WEBP_FORMAT);
                LogUtil.i(TAG, "onPostExecute,cacheFile = " + cached.getAbsolutePath()
                        + ", targetFile = " + target.getAbsolutePath());
                FileUtils.copyFile(cached, target);
                FileUtils.deleteFile(cached);
                updatePhotoLocalPathToDB(target);
                return target;
            } catch (Exception e) {
                LogUtil.e(TAG, "doInBackground#error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (file == null) {
                LogUtil.d(TAG, "the result file is null");
                return;
            }
            mSavePath = file.getAbsolutePath();
            displayPhoto(mSavePath);
        }
    }

    private void updatePhotoLocalPathToDB(File file) {
        LogUtil.i(TAG, "updatePhotoLocalPathToDB: momentId: " + this.momentId + ", targetFile: " + file);
        if (TextUtils.isEmpty(this.momentId)) {
            return;
        }
        List<DbMoment> moments = this.momentServe.getMomentById(this.momentId);
        if (moments == null || moments.size() <= 0) {
            return;
        }
        DbMoment moment = moments.get(0);
        LogUtil.i(TAG, "updatePhotoLocalPathToDB: " + moment);
        if (moment.getType().intValue() == 5) {
            PhotoMsg photoMsg = (PhotoMsg) JSONUtil.fromJSON(moment.getContent(), PhotoMsg.class);
            if (photoMsg != null) {
                photoMsg.setLocalPath(file.getAbsolutePath());
                moment.setContent(JSONUtil.toJSON(photoMsg));
                this.momentServe.updateMoment(moment);
                EventBus.getDefault().post(new EventData(8, moment));
            }
            return;
        }
        if (moment.getType().intValue() == 8) {
            ShareImageMoment shareImageMoment = (ShareImageMoment) JSONUtil.fromJSON(moment.getContent(), ShareImageMoment.class);
            if (shareImageMoment != null) {
                shareImageMoment.setLocalPath(file.getAbsolutePath());
                moment.setContent(JSONUtil.toJSON(shareImageMoment));
                this.momentServe.updateMoment(moment);
                EventBus.getDefault().post(new EventData(8, moment));
            }
        }
    }

    private void displayPhoto(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            LogUtil.w(TAG, "photoPath is null");
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        boolean isSmall = true;
        if (options.outWidth > 0 && options.outHeight / options.outWidth > screenHeight / screenWidth) {
            isSmall = false;
        }
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCorners(PREVIEW_CORNER_RADIUS))
                .override(Integer.MIN_VALUE, Integer.MIN_VALUE)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (isSmall) {
            showSmall(photoPath, requestOptions);
        } else {
            showBig(photoPath, requestOptions);
        }
    }

    @Override
    public void onBackPressed() {
        LogUtil.d(TAG, "onBackPressed");
        finishPreview();
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            startBrightnessAnimation(this, getWindow());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenshotUtils.unregister(this);
    }

    public void startBrightnessAnimation(Context context, Window window) {
        if (this.isBrightnessAnimationPlay) {
            return;
        }
        this.isBrightnessAnimationPlay = true;
        ScreenLightUtil.startBrightnessAnimation(window,
                ScreenLightUtil.getCurrentBrightness(context) / 255.0f, 1.0f, 1000L);
    }

    public int getScreenWidth() {
        return ImageCompressUtil.getScreenWidth(this);
    }

    public int getScreenHeight() {
        return ImageCompressUtil.getScreenHeight(this);
    }

    private void showSmall(String path, RequestOptions requestOptions) {
        if (this.mPvSmallPic.getContext() == null) {
            return;
        }
        if (this.mPvSmallPic.getContext() instanceof Activity) {
            Activity activity = (Activity) this.mPvSmallPic.getContext();
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
        }
        this.mPvSmallPic.setVisibility(View.VISIBLE);
        Glide.with(this.mPvSmallPic.getContext()).load(path)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        mIvLoading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(this.mPvSmallPic);
    }

    private void showBig(final String path, final RequestOptions requestOptions) {
        LogUtil.d(TAG, "showBig: " + path);
        if (this.mPvPreview.getContext() == null) {
            return;
        }
        if (this.mPvPreview.getContext() instanceof Activity) {
            Activity activity = (Activity) this.mPvPreview.getContext();
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
        }
        Glide.with(this).load(path)
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target,
                                                boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        if (drawable == null) {
                            return false;
                        }
                        if (ImageUtil.checkDrawableSizeInvalid(drawable)) {
                            LogUtil.d(TAG, "onResourceReady drawableSizeInvalid： IntrinsicHeight = ["
                                    + drawable.getIntrinsicHeight() + "], IntrinsicWidth = ["
                                    + drawable.getIntrinsicWidth() + "]");
                            return true;
                        }
                        if (drawable instanceof GifDrawable) {
                            showSmall(path, requestOptions.clone().dontTransform());
                            return false;
                        }
                        if (drawable instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            int screenWidth = getScreenWidth();
                            int screenHeight = getScreenHeight();
                            if (bitmap.getWidth() > 0 && bitmap.getHeight() / bitmap.getWidth() <= screenHeight / screenWidth) {
                                mPvSmallPic.setVisibility(View.VISIBLE);
                                mPvSmallPic.setImageDrawable(drawable);
                                return true;
                            }
                        }
                        mPvPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        MainHandlerUtil.postDelay(new Runnable() {
                            @Override
                            public void run() {
                                Matrix matrix = new Matrix();
                                mPvPreview.getAttacher().b(matrix);
                                float[] values = new float[9];
                                matrix.getValues(values);
                                float translationX = Math.abs(values[2]);
                                float translationY = Math.abs(values[5]);
                                Matrix translate = new Matrix();
                                translate.preTranslate(translationX, translationY);
                                mPvPreview.getAttacher().a(translate);
                                mPvPreview.setVisibility(View.VISIBLE);
                                mIvLoading.setVisibility(View.GONE);
                            }
                        }, waitTime);
                        return false;
                    }
                })
                .into(this.mPvPreview);
        LogUtil.d(TAG, "showBig: end");
    }

    private void showDeleteDialog(Context context) {
        this.deleteDialog = com.xtc.ui.widget.util.DialogUtil.makeDoubleIconBtnDialog(context,
                new com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean(context, true,
                        UiConstants.Color.GRAY, 0, R.string.cancel, UiConstants.Color.RED, 2,
                        R.string.delete, true,
                        new com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean.OnClickListener() {
                            @Override
                            public void onBottomBtnClick(Dialog dialog, View view) {
                            }

                            @Override
                            public void onLeftBtnClick(Dialog dialog, View view) {
                                com.xtc.ui.widget.util.DialogUtil.dismissDialog(dialog);
                                LogUtil.i(TAG, "onLeftBtnClick");
                            }

                            @Override
                            public void onRightBtnClick(Dialog dialog, View view) {
                                com.xtc.ui.widget.util.DialogUtil.dismissDialog(dialog);
                            }
                        }));
        com.xtc.ui.widget.util.DialogUtil.showDialog(this.deleteDialog);
    }
}
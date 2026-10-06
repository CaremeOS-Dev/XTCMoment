package com.xtc.moment.module.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.xtc.httplib.net.HttpSubscriber;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.R;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.CloudFileResource;
import com.xtc.moment.module.bean.LivePhotoMsg;
import com.xtc.moment.module.bean.PhotoMsg;
import com.xtc.moment.module.bean.SmallPicSouce;
import com.xtc.moment.serve.bean.FileUrlParam;
import com.xtc.moment.serve.impl.MomentPhotoServeImpl;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.io.File;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Moment row for a live photo: the still image is loaded here and the paired clip is played by the
 * host when the row is tapped.
 */
public class MomentLivePhotoView extends MomentPhotoView {

    private static final String TAG = "MomentLivePhotoView";

    private static final int IMAGE_CORNER_DP = 4;
    private static final String URL_TYPE_QN = "1";
    private static final String URL_TYPE_WS = "2";

    protected ImageView mContent;
    protected View rootView;

    public MomentLivePhotoView(Context context) {
        this(context, null);
    }

    public MomentLivePhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentLivePhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        this.rootView = LayoutInflater.from(getContext()).inflate(R.layout.view_photo_moment, this);
        this.mIcon = (ImageView) this.rootView.findViewById(R.id.iv_account_icon);
        this.ivAccountIconBg = (ImageView) this.rootView.findViewById(R.id.iv_account_icon_bg);
        this.ivOfficialEnterpriseIcon = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise);
        this.ivOfficialEnterpriseIconBg = (ImageView) this.rootView.findViewById(R.id.iv_official_enterprise_bg);
        this.mTvName = (TextView) this.rootView.findViewById(R.id.tv_account_name);
        this.ivOfficialLabel = (ImageView) this.rootView.findViewById(R.id.iv_official_label);
        this.mContent = (ImageView) this.rootView.findViewById(R.id.chat_msg_item_photo_iv);
        this.mContent.setImageResource(R.drawable.ic_selfie_album_default);
        this.rlMomentSender = (RelativeLayout) this.rootView.findViewById(R.id.rl_moment_sender);
        this.mIvReport = (ImageView) this.rootView.findViewById(R.id.iv_account_report);
    }

    @Override
    public void loadDefaultImage(Context context, int resId) {
        super.loadDefaultImage(context, resId);
        this.mContent.setImageResource(resId);
    }

    @Override
    public void loadImage(Context context, DbMoment moment) {
        if (checkContextIsNull(context)) {
            return;
        }
        super.loadImage(context, moment);
        if (this.mContent == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        String content = moment.getContent();
        String resource = moment.getResource();
        if (content != null && !TextUtils.isEmpty(content) && content.contains("source")) {
            PhotoMsg photoMsg = (PhotoMsg) JSONUtil.fromJSON(content, PhotoMsg.class);
            if (photoMsg == null) {
                loadImageWithKey(context, resource, URL_TYPE_QN, moment);
                return;
            }
            if (!TextUtils.isEmpty(photoMsg.getLocalPath())) {
                if (!new File(photoMsg.getLocalPath()).exists()) {
                    displayNetPhoto(getContext(), photoMsg, resource, moment);
                } else {
                    glideWithInto(context, photoMsg.getLocalPath(), moment);
                }
                return;
            }
            displayNetPhoto(getContext(), photoMsg, resource, moment);
            return;
        }
        loadImageWithKey(context, resource, URL_TYPE_QN, moment);
    }

    @Override
    public void setTags(int key, Object value) {
        this.mContent.setTag(key, value);
    }

    @Override
    public Object getTags(int key) {
        return this.mContent.getTag(key);
    }

    private void displayNetPhoto(final Context context, PhotoMsg photoMsg, final String resource, final DbMoment moment) {
        SmallPicSouce smallPic = photoMsg.getSmallPic();
        CloudFileResource source = photoMsg.getSource();
        long now = System.currentTimeMillis();
        long urlDeadline;
        String downloadUrl;
        String key;
        if (smallPic != null && now < smallPic.getUrlDeadline()) {
            urlDeadline = smallPic.getUrlDeadline();
            downloadUrl = smallPic.getDownloadUrl();
            key = smallPic.getKey();
        } else if (source != null) {
            urlDeadline = source.getUrlDeadline();
            downloadUrl = source.getDownloadUrl();
            key = source.getKey();
        } else {
            downloadUrl = null;
            urlDeadline = Long.MAX_VALUE;
            key = null;
        }
        if (now > urlDeadline) {
            new MomentPhotoServeImpl(context)
                    .getDownloadUrl(new FileUrlParam(key, photoMsg.getType()), photoMsg, moment)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HttpSubscriber<String>() {
                        @Override
                        public void onHttpError(Throwable e) {
                            super.onHttpError(e);
                        }

                        @Override
                        public void onNext(String url) {
                            super.onNext(url);
                            if (resource != null && resource.equals(mContent.getTag(R.id.moment))) {
                                glideWithInto(context, url, moment);
                            } else {
                                LogUtil.d(TAG, "控件被复用了，不加载图片");
                            }
                        }
                    });
            return;
        }
        if (android.text.TextUtils.isEmpty(downloadUrl)) {
            return;
        }
        if (resource != null && resource.equals(this.mContent.getTag(R.id.moment))) {
            glideWithInto(context, downloadUrl, moment);
        } else {
            LogUtil.d(TAG, "控件被复用了，不加载图片");
        }
    }

    private void loadImageWithKey(final Context context, final String key, final String urlType, final DbMoment moment) {
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(key, urlType), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                        LogUtil.d(TAG, "loadImageWithKey#e:" + e);
                        if (URL_TYPE_QN.equals(urlType)) {
                            loadImageWithKey(context, key, URL_TYPE_WS, moment);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        if (key.equals(mContent.getTag(R.id.moment))) {
                            glideWithInto(context, url, moment);
                        } else {
                            LogUtil.d(TAG, "控件被复用了，不加载图片");
                        }
                    }
                });
    }

    @Override
    public void glideWithInto(final Context context, final String url, final DbMoment moment) {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final String resource = moment.getResource();
                Glide.with(context).clear(mContent);
                mContent.setImageDrawable(null);
                RequestOptions options = new RequestOptions()
                        .error(R.drawable.ic_selfie_album_default)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .override(mContent.getWidth(), mContent.getHeight())
                        .dontAnimate()
                        .signature(new ObjectKey(url))
                        .transform(new MultiTransformation<Bitmap>(
                                new CenterCrop(), new RoundedCorners(DimenUtil.dp2px(context, IMAGE_CORNER_DP))));
                mContent.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(context).load(url)
                        .apply(options)
                        .into(new DrawableImageViewTarget(mContent) {
                            @Override
                            public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                                ImageView view = getView();
                                if (view == null || resource == null) {
                                    LogUtil.e(TAG, "onResourceReady: set image error. view: " + view + ", tag: " + resource);
                                    return;
                                }
                                if (resource.equals(view.getTag(R.id.moment))) {
                                    super.onResourceReady(drawable, transition);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void setContentOnClickListener(final Context context, final DbMoment moment,
                                          final AbsMomentView.OnContentOnClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.d(TAG, "setContentOnClickListener#onClick#momentBean:" + moment);
                if (moment == null) {
                    return;
                }
                String content = moment.getContent();
                if (content == null || TextUtils.isEmpty(content)) {
                    getPhotoUrl(context, moment.getResource(), URL_TYPE_QN, moment, listener);
                    return;
                }
                LivePhotoMsg livePhotoMsg = (LivePhotoMsg) JSONUtil.fromJSON(content, LivePhotoMsg.class);
                if (listener != null) {
                    listener.previewLivePhoto(livePhotoMsg);
                }
            }
        });
    }

    @Override
    public void setContentOnLongClickListener(Context context, final DbMoment moment,
                                              final AbsMomentView.OnContentOnLongClickListener listener) {
        ImageView content = this.mContent;
        if (content == null) {
            LogUtil.d(TAG, "mContent == null");
            return;
        }
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                LogUtil.d(TAG, "setContentOnLongClickListener#onClick#momentBean:" + moment);
                if (listener == null) {
                    return true;
                }
                listener.deleteItem(moment);
                return true;
            }
        });
    }

    private void getPhotoUrl(final Context context, String key, final String urlType, final DbMoment moment,
                             final AbsMomentView.OnContentOnClickListener listener) {
        LogUtil.d(TAG, "getPhotoUrl#key:" + key + ";type:" + urlType);
        new MomentPhotoServeImpl(context)
                .getDownloadUrl(new FileUrlParam(key, urlType), moment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpSubscriber<String>() {
                    @Override
                    public void onHttpError(Throwable e) {
                        super.onHttpError(e);
                        LogUtil.d(TAG, "getPhotoUrl#e:" + e);
                        if (URL_TYPE_QN.equals(urlType)) {
                            getPhotoUrl(context, moment.getResource(), URL_TYPE_WS, moment, listener);
                        }
                    }

                    @Override
                    public void onNext(String url) {
                        super.onNext(url);
                        LogUtil.d(TAG, "getPhotoUrl#url:" + url);
                        if (listener != null) {
                            listener.previewPhoto(url);
                        }
                    }
                });
    }
}
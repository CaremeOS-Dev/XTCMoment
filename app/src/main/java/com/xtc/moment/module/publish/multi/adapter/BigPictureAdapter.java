package com.xtc.moment.module.publish.multi.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.xtc.bigdata.collector.utils.CollectionUtil;
import com.xtc.bigdata.collector.utils.MainHandlerUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.publish.multi.SaveDynamic;
import com.xtc.moment.module.publish.multi.bean.PhotoEvent;
import com.xtc.moment.util.ImageCompressUtil;
import com.xtc.moment.util.ImageUtil;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.ui.BitmapUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * 大图预览适配器：分页展示图片，支持点击退出、长按删除（仅在动态图片预览模式下）。
 */
public class BigPictureAdapter extends PagerAdapter {

    public static final String EXTRA_DATA_CHANGE_TAG = "extra_data_change_tag";
    public static final String EXTRA_PHOTO_LISTS = "extra_photo_lists";
    private static final String TAG = "BigPictureAdapter";

    private LinkedList<View> cachePicView = new LinkedList<>();
    private Context context;
    private NormalIconDialog deleteDialog;
    private boolean isDelete;
    private boolean isMomentPhotoView;
    private ArrayList<String> list;

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public BigPictureAdapter(Context context, ArrayList<String> list) {
        this.list = new ArrayList<>();
        this.isMomentPhotoView = SaveDynamic.getIsMomentPhotoView(this.context, false);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
        ((ViewHolder) view.getTag(R.id.tag_view_holder)).preview.setImageBitmap(null);
        this.cachePicView.add(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        ViewHolder viewHolder;
        if (this.cachePicView.size() <= 0) {
            view = LayoutInflater.from(this.context).inflate(R.layout.item_big_picture_view, container, false);
            viewHolder = new ViewHolder((PhotoView) view.findViewById(R.id.iv_preview), (TextView) view.findViewById(R.id.tv_current_page));
            view.setTag(R.id.tag_view_holder, viewHolder);
        } else {
            view = this.cachePicView.removeFirst();
            viewHolder = (ViewHolder) view.getTag(R.id.tag_view_holder);
        }
        container.addView(view);
        view.setTag(position);
        viewHolder.position = position;
        initPage(viewHolder);
        loadImage(viewHolder.preview, position);
        return view;
    }

    private void initPage(ViewHolder viewHolder) {
        viewHolder.currentPage.setText("" + (viewHolder.position + 1) + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + this.list.size());
        viewHolder.currentPage.setShadowLayer(4.0f, 0.0f, 2.0f, this.context.getResources().getColor(R.color.textview_shadow));
    }

    private void loadImage(PhotoView photoView, int position) {
        if (position < 0 || position >= this.list.size()) {
            LogUtil.i(TAG, "loadImage position error");
            return;
        }
        String photoPath = this.list.get(position);
        if (TextUtils.isEmpty(photoPath)) {
            LogUtil.w(TAG, "photoPath is null");
        } else {
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            showPicture(photoPath, photoView, new RequestOptions()
                    .transform((Transformation<Bitmap>) new RoundedCorners(12))
                    .error(R.drawable.ic_selfie_album_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Integer.MIN_VALUE, Integer.MIN_VALUE));
        }
    }

    private void showPicture(final String photoPath, final PhotoView photoView, RequestOptions requestOptions) {
        LogUtil.i(TAG, "showPicture photoPath = " + photoPath);
        Glide.with(this.context).load(photoPath).apply(requestOptions).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                LogUtil.i(BigPictureAdapter.TAG, "onLoadFailed photoPath = " + photoPath);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                LogUtil.i(BigPictureAdapter.TAG, "showPicture onResourceReady");
                if (drawable == null) {
                    return false;
                }
                if (ImageUtil.checkDrawableSizeInvalid(drawable)) {
                    LogUtil.d(BigPictureAdapter.TAG, "onResourceReady drawableSizeInvalid： IntrinsicHeight = [" + drawable.getIntrinsicHeight() + "], IntrinsicWidth = [" + drawable.getIntrinsicWidth() + "]");
                    photoView.setImageResource(R.drawable.ic_selfie_album_default);
                    return true;
                }
                if (drawable instanceof GifDrawable) {
                    return false;
                }
                if (drawable instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    int screenWidth = ImageCompressUtil.getScreenWidth(BigPictureAdapter.this.context);
                    int screenHeight = ImageCompressUtil.getScreenHeight(BigPictureAdapter.this.context);
                    if (bitmap.getWidth() > 0 && bitmap.getHeight() / bitmap.getWidth() <= screenHeight / screenWidth) {
                        photoView.setImageDrawable(drawable);
                        return true;
                    }
                    photoView.setImageBitmap(Bitmap.createScaledBitmap(BitmapUtil.drawableToBitmap(drawable), Math.min(drawable.getMinimumWidth(), screenWidth), Math.min(drawable.getMinimumHeight(), screenHeight), true));
                }
                BigPictureAdapter.this.dealPreViewMatrix(photoView);
                return false;
            }
        }).into(photoView);
    }

    private void dealPreViewMatrix(final PhotoView photoView) {
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        MainHandlerUtil.postDelay(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                photoView.getAttacher().b(matrix);
                float[] values = new float[9];
                matrix.getValues(values);
                float translateX = Math.abs(values[2]);
                float translateY = Math.abs(values[5]);
                Matrix target = new Matrix();
                target.preTranslate(translateX, translateY);
                photoView.getAttacher().a(target);
            }
        }, 20L);
    }

    private void onPreViewClick(int position) {
        LogUtil.i(TAG, "onPreViewClick, isDelete = " + this.isDelete);
        if (this.isDelete) {
            this.isDelete = false;
        }
        finishActivity((Activity) this.context);
    }

    private void finishActivity(Activity activity) {
        activity.finish();
    }

    private void showDeleteDialog(final Context context, final ArrayList<String> list, final int position) {
        this.deleteDialog = DialogUtil.makeDoubleIconBtnDialog(context, new DoubleIconBtnBean(context, true, UiConstants.Color.GRAY, 0, R.string.cancel, UiConstants.Color.RED, 2, R.string.delete, true, new DoubleIconBtnBean.OnClickListener() {
            @Override
            public void onBottomBtnClick(Dialog dialog, View view) {
            }

            @Override
            public void onLeftBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
                LogUtil.i(BigPictureAdapter.TAG, "onLeftBtnClick");
            }

            @Override
            public void onRightBtnClick(Dialog dialog, View view) {
                DialogUtil.dismissDialog(dialog);
                if (!CollectionUtil.isEmpty(list)) {
                    BigPictureAdapter.this.isDelete = true;
                    list.remove(position);
                    BigPictureAdapter.this.notifyDataSetChanged();
                    PhotoEvent photoEvent = new PhotoEvent();
                    photoEvent.setPhotoLists(list);
                    photoEvent.setDataChange(true);
                    EventBus.getDefault().post(photoEvent);
                    if (CollectionUtil.isEmpty(list)) {
                        BigPictureAdapter.this.finishActivity((Activity) context);
                    }
                    return;
                }
                LogUtil.i(BigPictureAdapter.TAG, "delete photo error ");
            }
        }));
        DialogUtil.showDialog(this.deleteDialog);
    }

    public class ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView currentPage;
        int position;
        PhotoView preview;

        public ViewHolder(PhotoView preview, TextView currentPage) {
            this.preview = preview;
            this.currentPage = currentPage;
            preview.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView imageView, float x, float y) {
                    BigPictureAdapter.this.onPreViewClick(ViewHolder.this.position);
                }
            });
            if (BigPictureAdapter.this.isMomentPhotoView) {
                preview.setOnLongClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            BigPictureAdapter.this.onPreViewClick(this.position);
        }

        @Override
        public boolean onLongClick(View view) {
            BigPictureAdapter.this.showDeleteDialog(BigPictureAdapter.this.context, BigPictureAdapter.this.list, this.position);
            return true;
        }
    }
}
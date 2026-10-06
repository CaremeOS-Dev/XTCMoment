package com.xtc.moment.module.publish.multi.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.asynclayout.AsyncLayoutLoader;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.Constants;
import com.xtc.moment.module.publish.multi.BigPicturePageActivity;
import com.xtc.moment.module.publish.multi.util.GlideRoundTransform;
import com.xtc.moment.module.publish.multi.view.PointerViewPager;
import com.xtc.moment.util.HandlerUtil;
import com.xtc.ui.widget.UiConstants;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.ui.DimenUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

/**
 * View pager adapter that shows the photos of a moment as a horizontally overlapping stack.
 *
 * <p>The pages loop indefinitely when more than one photo is present.
 */
public class BaseOverlayPageAdapter extends PagerAdapter {

    private static final String TAG = "BaseOverlayPageAdapter";

    private static final String CURRENT_MOMENT_BEAN = "current_moment_bean";
    private static final String CURRENT_PAGE = "current_page";
    private static final String ENLARGE_PHOTO = "enlarge_photo";
    private static final String ENLARGE_PHOTO_LIST = "enlarge_photo_list";

    /** Page width in dp for the small variant. */
    private static final float SMALL_WIDTH_DP = 80.0f;
    /** Page height in dp for the small variant. */
    private static final float SMALL_HEIGHT_DP = 90.0f;
    /** Page width in dp for the big variant. */
    private static final float BIG_WIDTH_DP = 97.0f;
    /** Page height in dp for the big variant. */
    private static final float BIG_HEIGHT_DP = 107.0f;
    /** Corner radius of a page, in dp. */
    private static final int PAGE_CORNER_DP = 6;
    /** Maximum number of offscreen pages kept alive. */
    private static final int MAX_OFFSCREEN_PAGES = 3;

    private final Context context;
    private final LayoutInflater inflate;
    private final boolean isBigPic;
    private final SparseArray<View> cachePicView = new SparseArray<View>();

    private ArrayList<String> imgUrls = new ArrayList<String>();
    private PointerViewPager vp;
    private OverlayTransformer transformer;
    private DbMoment momentBean;
    private NormalIconDialog deleteDialog;
    private onClickReport onClickReport;
    private onLongClickListener longClickListener;

    /** Notified when a photo is long pressed for reporting. */
    public interface onClickReport {
        void report(int position);
    }

    /** Notified after a photo was deleted. */
    public interface onLongClickListener {
        void onLongClickRefresh();
    }

    public BaseOverlayPageAdapter(Context context, boolean isBigPic) {
        this.context = context;
        this.isBigPic = isBigPic;
        this.inflate = LayoutInflater.from(context);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /** Refreshes the urls only when they actually changed. */
    public void refreshViewWithUrlCheck(PointerViewPager viewPager, ArrayList<String> urls) {
        long start = SystemClock.elapsedRealtime();
        if (!checkUrlChange(urls)) {
            LogUtil.d(TAG, "url no change");
            return;
        }
        this.imgUrls = urls;
        viewPager.setAdapter(this);
        setTransformer(viewPager);
        LogUtil.d(TAG, "refreshViewWithUrlCheck: cost time = [" + (SystemClock.elapsedRealtime() - start) + "ms]");
    }

    private boolean checkUrlChange(ArrayList<String> urls) {
        if (CollectionUtil.isEmpty(urls)) {
            return false;
        }
        if (CollectionUtil.isEmpty(this.imgUrls) || this.imgUrls.size() != urls.size()) {
            return true;
        }
        for (int i = 0; i < this.imgUrls.size(); i++) {
            if (!Objects.equals(this.imgUrls.get(i), urls.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void refreshView(PointerViewPager viewPager, ArrayList<String> urls) {
        this.imgUrls = urls;
        viewPager.setAdapter(this);
        notifyDataSetChanged();
        setTransformer(viewPager);
    }

    public void addImgUrl(PointerViewPager viewPager, String url) {
        if (TextUtils.isEmpty(url)) {
            LogUtil.i(TAG, "addImgUrl, imgurl is null");
            return;
        }
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            this.imgUrls = new ArrayList<String>();
        }
        this.imgUrls.add(url);
        viewPager.setAdapter(this);
        notifyDataSetChanged();
        setTransformer(viewPager);
    }

    public void setTransformer(PointerViewPager viewPager) {
        LogUtil.d(TAG, "setTransformer");
        this.vp = viewPager;
        if (viewPager == null) {
            LogUtil.i(TAG, "setTransformer, viewPager is null");
            return;
        }
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            return;
        }
        int overlayCount = Math.min(this.imgUrls.size(), MAX_OFFSCREEN_PAGES);
        viewPager.setOffscreenPageLimit(overlayCount);
        this.transformer = new OverlayTransformer(overlayCount, -1.0f, -1.0f);
        viewPager.setPageTransformer(true, this.transformer);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            return 0;
        }
        if (this.imgUrls.size() == 1) {
            return this.imgUrls.size();
        }
        return Integer.MAX_VALUE;
    }

    public int getCountSize() {
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            return 0;
        }
        return this.imgUrls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LogUtil.i(TAG, "position = " + position + "isBigPic = " + this.isBigPic);
        View page = this.cachePicView.get(position % getCountSize());
        if (page == null) {
            page = AsyncLayoutLoader.getInstance().inflateView(R.layout.item_viewpager, this.inflate, container, true);
            page.setTag(R.id.tag_view_holder, Integer.valueOf(position % getCountSize()));
        } else {
            this.cachePicView.remove(position % getCountSize());
        }
        ViewHolder holder = new ViewHolder((ImageView) page.findViewById(R.id.iv_item_pic),
                (TextView) page.findViewById(R.id.tv_item_page));
        reSetLayoutParams((ViewPager.LayoutParams) page.getLayoutParams());
        container.addView(page);
        int pageIndex = position % this.imgUrls.size();
        page.setTag(Integer.valueOf(pageIndex));
        holder.position = pageIndex;
        initPage(holder);
        dealPicLayout(holder);
        loadImage(holder);
        return page;
    }

    private void dealPicLayout(ViewHolder holder) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.itemPic.getLayoutParams();
        params.width = (int) DimenUtil.dp2pxFloat(this.context, this.isBigPic ? BIG_WIDTH_DP : SMALL_WIDTH_DP);
        params.height = (int) DimenUtil.dp2pxFloat(this.context, this.isBigPic ? BIG_HEIGHT_DP : SMALL_HEIGHT_DP);
        holder.itemPic.setLayoutParams(params);
    }

    private void loadImage(ViewHolder holder) {
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            LogUtil.i(TAG, "loadImage imgUrls is empty");
            return;
        }
        String path = this.imgUrls.get(holder.position);
        if (TextUtils.isEmpty(path)) {
            LogUtil.i(TAG, "photoPath is null");
            return;
        }
        Glide.with(this.context).load(path)
                .apply(new RequestOptions()
                        .transform(new GlideRoundTransform(this.context, PAGE_CORNER_DP))
                        .override(holder.itemPic.getWidth(), holder.itemPic.getHeight())
                        .dontAnimate()
                        .placeholder(R.drawable.ic_selfie_album_default)
                        .error(R.drawable.ic_selfie_album_default))
                .into(holder.itemPic);
    }

    private void initPage(ViewHolder holder) {
        if (CollectionUtil.isEmpty(this.imgUrls)) {
            LogUtil.i(TAG, "initPage, imgUrls is empty");
            return;
        }
        if (this.imgUrls.size() == 1) {
            holder.itemPage.setVisibility(View.GONE);
        }
        holder.itemPage.setShadowLayer(4.0f, 0.0f, 2.0f,
                this.context.getResources().getColor(R.color.textview_shadow));
        holder.itemPage.setText((holder.position + 1) + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER
                + this.imgUrls.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View page = (View) object;
        container.removeView(page);
        reSetLayoutParams((ViewPager.LayoutParams) page.getLayoutParams());
        if (getCountSize() == 0) {
            this.cachePicView.put(position, page);
        } else if (this.cachePicView.get(position % getCountSize()) == null) {
            this.cachePicView.put(position % getCountSize(), page);
        }
    }

    /** Clears the private layout params fields so the recycled page can be laid out again. */
    private void reSetLayoutParams(ViewPager.LayoutParams params) {
        try {
            Field positionField = getField(ViewPager.LayoutParams.class, "position");
            if (positionField != null) {
                positionField.setInt(params, 0);
            }
            Field widthFactorField = getField(ViewPager.LayoutParams.class, "widthFactor");
            if (widthFactorField != null) {
                widthFactorField.setFloat(params, 0.0f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Shows the confirmation dialog used to delete one photo of the current moment. */
    public void showDeleteDialog(Context context, final int position) {
        this.deleteDialog = DialogUtil.makeDoubleIconBtnDialog(context,
                new DoubleIconBtnBean(context, true, UiConstants.Color.GRAY, 0, R.string.cancel,
                        UiConstants.Color.RED, 2, R.string.delete, true,
                        new DoubleIconBtnBean.OnClickListener() {
                            @Override
                            public void onBottomBtnClick(Dialog dialog, View view) {
                            }

                            @Override
                            public void onLeftBtnClick(Dialog dialog, View view) {
                                DialogUtil.dismissDialog(dialog);
                                LogUtil.i(TAG, "onLeftBtnClick");
                            }

                            @Override
                            public void onRightBtnClick(Dialog dialog, View view) {
                                DialogUtil.dismissDialog(dialog);
                                if (vp == null) {
                                    LogUtil.i(TAG, "delete photo error, viewPager is null");
                                    return;
                                }
                                if (CollectionUtil.isEmpty(imgUrls)) {
                                    LogUtil.i(TAG, "delete photo imgUrls is empty");
                                    return;
                                }
                                imgUrls.remove(position % imgUrls.size());
                                refreshView(vp, imgUrls);
                                vp.setAdapter(BaseOverlayPageAdapter.this);
                                notifyDataSetChanged();
                                if (longClickListener != null) {
                                    longClickListener.onLongClickRefresh();
                                }
                                LogUtil.i(TAG, "onRightBtnClick imgUrls" + imgUrls);
                            }
                        }));
        DialogUtil.showDialog(this.deleteDialog);
    }

    public void setLongClickRefreshListener(onLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnClickReport(onClickReport listener) {
        this.onClickReport = listener;
    }

    /** Opens the big picture page when a page is tapped and reports it when long pressed. */
    public class ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final ImageView itemPic;
        final TextView itemPage;
        int position;

        public ViewHolder(ImageView itemPic, TextView itemPage) {
            this.itemPic = itemPic;
            this.itemPage = itemPage;
            itemPic.setOnClickListener(this);
            itemPic.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            LogUtil.i(TAG, "onLongClick");
            if (onClickReport == null) {
                return true;
            }
            onClickReport.report(this.position);
            return true;
        }

        @Override
        public void onClick(View view) {
            LogUtil.i(TAG, "onClick path = " + imgUrls.get(this.position));
            HandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    go2BigPicturePageActivity();
                }
            });
        }

        private void go2BigPicturePageActivity() {
            final Intent intent = new Intent(context, BigPicturePageActivity.class);
            intent.putExtra(ENLARGE_PHOTO_LIST, imgUrls);
            intent.putExtra(CURRENT_PAGE, this.position);
            if (momentBean != null) {
                intent.putExtra(CURRENT_MOMENT_BEAN, JSONUtil.toJSON(momentBean));
            }
            if (!(context instanceof Activity)) {
                context.startActivity(intent);
            } else {
                HandlerUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Activity) context).startActivity(intent);
                    }
                });
            }
        }
    }

    public ArrayList<String> getDataLists() {
        return this.imgUrls;
    }

    public void setDataLists(ArrayList<String> urls) {
        this.imgUrls = urls;
    }

    public void setCurrentDbMoment(DbMoment moment) {
        this.momentBean = moment;
    }

    public DbMoment getMomentBean() {
        return this.momentBean;
    }
}
package com.xtc.moment.asynclayout;

import android.content.Context;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.module.TagConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 异步布局预加载入口：在后台线程把所有列表项 / 页面布局批量提交给 {@link AsyncLayoutLoader} 预热。
 */
public class AsyncLayoutUtil {

    private static final String TAG = "AsyncLayoutUtil";

    public static void asyncLayout() {
        Observable.just(false).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean value) {
                ArrayList<AsyncInflaterResource> resources = new ArrayList<>();
                resources.addAll(AsyncLayoutUtil.getMomentRecyclerItem(MomentApp.getAppContext()));
                resources.addAll(AsyncLayoutUtil.getMomentPage(MomentApp.getAppContext()));
                AsyncLayoutLoader loader = AsyncLayoutLoader.getInstance();
                for (AsyncInflaterResource resource : resources) {
                    loader.preLoadLayoutResource(resource);
                }
                return true;
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Observer<Boolean>() {
            @Override
            public void onNext(Boolean value) {
            }

            @Override
            public void onCompleted() {
                LogUtil.d(AsyncLayoutUtil.TAG, "onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(AsyncLayoutUtil.TAG, throwable);
            }
        });
    }

    private static List<AsyncInflaterResource> getMomentRecyclerItem(Context context) {
        return Arrays.asList(
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.fragment_moment).setNeedFillContainer(true).setRootLayoutType(0).setFrameLayoutAttr(context).setTag(TagConstant.Layout.ACTIVITY_MOMENT_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.header_recycle_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.HEADER_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.footer_recycle_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.FOOTER_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.DEFAULT_VIEW_LAYOUT).setCapacity(5).setRecycleLayoutAttr(context).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_photo_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.PHOTO_VIEW_LAYOUT).setRecycleLayoutAttr(context).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_photos_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.MULTI_PHOTO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(5).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_viewpager).setNeedFillContainer(true).setRootLayoutType(3).setTag(TagConstant.Layout.MULTI_PHOTO_ITEM_VIEW_LAYOUT).setViewPageAttr(context).setCapacity(10).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_video_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.VIDEO_VIEW_LAYOUT).setRecycleLayoutAttr(context).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_official_photo_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.OFFICIAL_PHOTO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_official_photo_text_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.OFFICIAL_PHOTO_TEXT_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_official_photo_text_moment_h5).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.OFFICIAL_TEXT_H5_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_share_text_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_TEXT_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_share_image_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_IMAGE_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_share_app_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_APP_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_live_photo_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.LIVE_PHOTO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_share_live_photo_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_LIVE_PHOTO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_share_video_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_VIDEO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_official_video_moment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.SHARE_OFFICIAL_VIDEO_VIEW_LAYOUT).setRecycleLayoutAttr(context).setCapacity(1).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_moment_item_comment_footer).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.ITEM_COMMENT_VIEW_LAYOUT).setRecycleLayoutAttr(context).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.item_recycle_moment_item_comment).setNeedFillContainer(true).setRootLayoutType(4).setTag(TagConstant.Layout.ITEM_COMMENT_VIEW_LAYOUT).setRecycleLayoutAttr(context).build());
    }

    private static List<AsyncInflaterResource> getMomentPage(Context context) {
        return Arrays.asList(
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_publish).setNeedFillContainer(true).setFrameLayoutAttr(context).setTag(TagConstant.Layout.ACT_PUBLISH_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_push_picture).setNeedFillContainer(true).setConstraintLayoutAttr(context).setTag(TagConstant.Layout.ACT_PUSH_PIC_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_play_video).setNeedFillContainer(true).setFrameLayoutAttr(context).setTag(TagConstant.Layout.ACT_PLAY_VIDEO_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_big_picture_page).setNeedFillContainer(true).setLinearLayoutAttr(context).setTag(TagConstant.Layout.ACT_BIG_PIC_PAGE_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_new_like).setNeedFillContainer(true).setLinearLayoutAttr(context).setTag(TagConstant.Layout.ACT_NEW_LIKE_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_share).setNeedFillContainer(true).setTag(TagConstant.Layout.ACT_SHARE_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_publish_location).setNeedFillContainer(true).setRelativeLayoutAttr(context).setTag(TagConstant.Layout.ACT_PUBLISH_LOCATION_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_mood_state).setNeedFillContainer(true).setTag(TagConstant.Layout.ACT_MOOD_STATE_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_moment_deails).setNeedFillContainer(true).setFrameLayoutAttr(context).setTag(TagConstant.Layout.ACT_MOMENT_DETAIL_LAYOUT).setCapacity(2).build(),
                new AsyncInflaterResource.Builder().setLayoutIds(R.layout.activity_push_text).setNeedFillContainer(true).setConstraintLayoutAttr(context).setTag(TagConstant.Layout.ACT_PUSH_TEXT_LAYOUT).setCapacity(2).build());
    }
}
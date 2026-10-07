package com.xtc.contactapi.contacthead.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xtc.moment.R;
import com.xtc.contactapi.contact.bean.ContactBean;
import com.xtc.contactapi.contact.manager.ContactApi;
import com.xtc.contactapi.contacthead.bean.DressBean;
import com.xtc.contactapi.contacthead.config.ContactHeadManagerConfig;
import com.xtc.contactapi.contacthead.interfaces.IContactDressCache;
import com.xtc.contactapi.contacthead.interfaces.IContactHeadCache;
import com.xtc.contactapi.contacthead.interfaces.IShowDressToViewStrategy;
import com.xtc.contactapi.contacthead.interfaces.IShowHeadToViewStrategy;
import com.xtc.contactapi.contacthead.util.LoadBitmapUtil;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 联系人头像管理器，负责把联系人头像与装扮加载并展示到 View 上。
 */
public class ContactHeadManager {

    /** 默认头像资源名前缀。 */
    public static final String DEFAULT_PORTRAIT_PREFIX = "default_portrait_";

    private static final String TAG = "ContactHeadManager";

    private IShowHeadToViewStrategy showHeadToViewStrategy;
    private IShowDressToViewStrategy showDressToViewStrategy;
    private final IContactHeadCache headCache;
    private final IContactDressCache dressCache;
    private Context context;
    private ContactHeadManagerConfig config;

    private ContactHeadManager(Context context) {
        this.context = context.getApplicationContext();
        this.headCache = ContactHeadCacheServeImpl.getInstance(this.context);
        this.dressCache = ContactDressCacheServeImpl.getInstance(this.context);
        this.config = null;
    }

    public ContactHeadManager(ContactHeadManagerConfig config) {
        this(config.context);
        this.context = config.context;
        this.showHeadToViewStrategy = config.showHeadToViewStrategy;
        this.config = config;
        if (config.dressEnabled) {
            this.showDressToViewStrategy = config.dressConfig.showDressToViewStrategy;
        }
    }

    /** 将联系人头像展示到 View 上。 */
    public void setContactPortrait(Context context, final ContactBean contactBean, final View view, int width, int height) {
        Log.d(TAG, "setContactPortrait: " + contactBean);
        if (contactBean == null || context == null || view == null || headCache == null) {
            return;
        }
        boolean cacheHead = config.cacheHead;
        view.setTag(R.string.contact_head_dislocation_tag, contactBean.getPhotoPath());
        if (cacheHead) {
            Observable<BitmapDrawable> observable = headCache.loadHeadBitmap(contactBean, width, height, config);
            if (observable != null) {
                observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BitmapDrawable>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, throwable.getMessage());
                    }

                    @Override
                    public void onNext(BitmapDrawable drawable) {
                        if (isPortraitValid(view, contactBean)) {
                            String updateTag = contactBean.getContactServerId() + "_" + contactBean.getLastUpdatedTimestamp();
                            showHeadToViewStrategy.showContactPortrait(ContactHeadManager.this.context,
                                    ContactHeadManager.this, contactBean, view, drawable);
                            view.setTag(R.string.contact_head_last_update_tag, updateTag);
                        }
                    }
                });
                return;
            }
            return;
        }
        if (isPortraitValid(view, contactBean)) {
            String photoPath = contactBean.getPhotoPath();
            String updateTag = contactBean.getContactServerId() + "_" + contactBean.getLastUpdatedTimestamp();
            showHeadToViewStrategy.showContactPortrait(this.context, this, contactBean, view,
                    new BitmapDrawable(this.context.getResources(),
                            LoadBitmapUtil.loadLocalImage(photoPath, width, height, config.loadHead, config.bitmapConfig)));
            view.setTag(R.string.contact_head_last_update_tag, updateTag);
        }
    }

    /** 将联系人装扮展示到 View 上。 */
    public void setContactDress(final DressBean dressBean, final View view, int width, int height) {
        if (dressBean == null || TextUtils.isEmpty(dressBean.getWatchId()) || TextUtils.isEmpty(dressBean.getDressPath())
                || view == null || config.dressConfig == null) {
            return;
        }
        if (!isDefaultHeadLoaded()) {
            Log.w(TAG, "ContactDressMapping is not inti.");
            return;
        }
        final String watchId = dressBean.getWatchId();
        boolean loadDress = config.dressConfig.loadDress;
        String dressPath = dressCache.getDressPath(watchId);
        if (dressCache.removeDressCache(watchId)) {
            dressBean.setGif(true);
            showDressToViewStrategy.hideDress(this.context, this, dressBean, view);
            return;
        }
        if (loadDress) {
            Observable<BitmapDrawable> observable = dressCache.loadDressBitmap(watchId, width, height, config.dressConfig);
            if (observable == null) {
                return;
            }
            view.setTag(R.string.contact_head_dislocation_tag, watchId);
            observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<BitmapDrawable>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(TAG, throwable.getMessage());
                }

                @Override
                public void onNext(BitmapDrawable drawable) {
                    if (isDressValid(view, watchId)) {
                        dressBean.setDressBitmap(drawable);
                        dressBean.setGif(false);
                        showDressToViewStrategy.showDress(ContactHeadManager.this.context, ContactHeadManager.this, dressBean, view);
                    }
                }
            });
            return;
        }
        if (isDressValid(view, watchId)) {
            Bitmap bitmap = LoadBitmapUtil.loadLocalImage(dressPath, width, height,
                    config.dressConfig.cacheDress, config.dressConfig.bitmapConfig);
            BitmapDrawable drawable = null;
            if (bitmap != null) {
                drawable = new BitmapDrawable(this.context.getResources(), bitmap);
            } else {
                Log.w(TAG, " dressBitmap is null");
            }
            dressBean.setDressBitmap(drawable);
            dressBean.setGif(false);
            showDressToViewStrategy.showDress(this.context, this, dressBean, view);
        }
    }

    /** 校验 view 复用时是否可安全展示头像，避免错位与重复加载。 */
    private boolean isPortraitValid(View view, ContactBean contactBean) {
        if (contactBean == null) {
            return false;
        }
        String updateTag = contactBean.getContactServerId() + "_" + contactBean.getLastUpdatedTimestamp();
        if (view.getTag(R.string.contact_head_dislocation_tag) != null
                && !view.getTag(R.string.contact_head_dislocation_tag).equals(contactBean.getPhotoPath())) {
            Log.w(TAG, "头像路径 不相同防止错位不做设置  contact name:" + contactBean.getName()
                    + "  contact contactServerId:" + contactBean.getContactServerId());
            return false;
        }
        if (view.getTag(R.string.contact_head_last_update_tag) != null
                && view.getTag(R.string.contact_head_last_update_tag).equals(updateTag)) {
            Log.w(TAG, " 头像更新时间相同且为同一个联系人  当前item已经显示了此头像，不做重复加载 contact name:"
                    + contactBean.getName() + "  contact contactServerId:" + contactBean.getContactServerId());
            return false;
        }
        if (view.getTag(R.string.contact_head_dislocation_tag) != null
                || view.getTag(R.string.contact_head_last_update_tag) != null) {
            return true;
        }
        Log.w(TAG, "头像viewHolder被复用 头像路径tag和头像更新时间tag都为空 不再加载");
        return false;
    }

    /** 校验 view 复用时是否可安全展示装扮。 */
    private boolean isDressValid(View view, String watchId) {
        if (TextUtils.isEmpty(watchId)) {
            return false;
        }
        if (view.getTag(R.string.contact_head_dislocation_tag) == null
                || view.getTag(R.string.contact_head_dislocation_tag).equals(watchId)) {
            return true;
        }
        Log.w(TAG, "装扮路径 不相同防止错位不做设置  contact watchId:" + watchId);
        return false;
    }

    public boolean isHeadCacheInit() {
        return headCache.isHeadCacheInit();
    }

    private boolean isDefaultHeadLoaded() {
        return ContactApi.isDefaultHeadLoaded();
    }
}
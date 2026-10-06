package com.xtc.moment.asynclayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.xtc.log.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 异步预加载布局的描述对象：记录布局 id、根容器、缓存容量以及根容器所需的 LayoutParams 信息。
 * 该类只描述"要预加载哪个布局"，真正的预加载逻辑在 {@link AsyncLayoutLoader} 中。
 */
public class AsyncInflaterResource {

    private static final String TAG = "AsyncInflaterResource";

    private int capacity;
    private int layoutIds;
    private boolean needFillContainer;
    private String tag;
    private ViewGroup vgRootView;

    public int getLayoutIds() {
        return layoutIds;
    }

    public void setLayoutIds(int layoutIds) {
        this.layoutIds = layoutIds;
    }

    public ViewGroup getRootView() {
        return vgRootView;
    }

    public void setRootView(ViewGroup rootView) {
        this.vgRootView = rootView;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isNeedFillContainer() {
        return needFillContainer;
    }

    public void setNeedFillContainer(boolean needFillContainer) {
        this.needFillContainer = needFillContainer;
    }

    public AsyncInflaterResource(Builder builder) {
        this.layoutIds = builder.layoutIds;
        ViewGroup.LayoutParams layoutParams = null;
        this.vgRootView = builder.viewGroup == null ? null : builder.viewGroup.get();
        this.tag = builder.tag;
        this.capacity = builder.capacity;
        this.needFillContainer = builder.needFillContainer;
        if (this.vgRootView == null) {
            return;
        }
        int rootLayoutType = builder.rootLayoutType;
        if (rootLayoutType == Builder.ROOT_LINEAR_LAYOUT) {
            layoutParams = new LinearLayout.LayoutParams(builder.rootWidth, builder.rootHeight);
            ((LinearLayout.LayoutParams) layoutParams).setMargins(builder.rootMargin, builder.rootMargin, builder.rootMargin, builder.rootMargin);
        } else if (rootLayoutType == Builder.ROOT_RELATIVE_LAYOUT) {
            layoutParams = new RelativeLayout.LayoutParams(builder.rootWidth, builder.rootHeight);
            ((RelativeLayout.LayoutParams) layoutParams).setMargins(builder.rootMargin, builder.rootMargin, builder.rootMargin, builder.rootMargin);
        } else if (rootLayoutType == Builder.ROOT_FRAME_LAYOUT) {
            layoutParams = new FrameLayout.LayoutParams(builder.rootWidth, builder.rootHeight);
            ((FrameLayout.LayoutParams) layoutParams).setMargins(builder.rootMargin, builder.rootMargin, builder.rootMargin, builder.rootMargin);
        } else if (rootLayoutType == Builder.ROOT_RECYCLE_LAYOUT) {
            layoutParams = new RecyclerView.LayoutParams(builder.rootWidth, builder.rootHeight);
            ((RecyclerView.LayoutParams) layoutParams).setMargins(builder.rootMargin, builder.rootMargin, builder.rootMargin, builder.rootMargin);
        } else if (rootLayoutType == Builder.ROOT_CONSTRAINT_LAYOUT) {
            layoutParams = new ConstraintLayout.LayoutParams(builder.rootWidth, builder.rootHeight);
            ((ConstraintLayout.LayoutParams) layoutParams).setMargins(builder.rootMargin, builder.rootMargin, builder.rootMargin, builder.rootMargin);
        }
        this.vgRootView.setBackground(builder.rootBackground);
        if (layoutParams != null) {
            this.vgRootView.setLayoutParams(layoutParams);
        }
        LogUtil.i(TAG, "tag:" + builder.tag + " rootLayoutType:" + builder.rootLayoutType + " layoutParams:" + layoutParams);
    }

    public static class Builder {

        public static final int ROOT_CONSTRAINT_LAYOUT = 5;
        public static final int ROOT_DEFAULT_LAYOUT = -1;
        public static final int ROOT_FRAME_LAYOUT = 2;
        public static final int ROOT_LINEAR_LAYOUT = 0;
        public static final int ROOT_RECYCLE_LAYOUT = 4;
        public static final int ROOT_RELATIVE_LAYOUT = 1;
        public static final int ROOT_VIEWPAGE_LAYOUT = 3;

        private int capacity;
        private int layoutIds;
        private boolean needFillContainer;
        private Drawable rootBackground;
        private int rootMargin;
        private String tag;
        private WeakReference<ViewGroup> viewGroup;
        private int rootWidth = -1;
        private int rootHeight = -1;
        private int rootLayoutType = ROOT_DEFAULT_LAYOUT;

        public Builder setLayoutIds(int layoutIds) {
            this.layoutIds = layoutIds;
            return this;
        }

        public Builder setRootWidth(int rootWidth) {
            this.rootWidth = rootWidth;
            return this;
        }

        public Builder setRootHeight(int rootHeight) {
            this.rootHeight = rootHeight;
            return this;
        }

        public Builder setRootBackground(Drawable rootBackground) {
            this.rootBackground = rootBackground;
            return this;
        }

        public Builder setRootMargin(int rootMargin) {
            this.rootMargin = rootMargin;
            return this;
        }

        public Builder setViewGroup(ViewGroup viewGroup) {
            this.viewGroup = new WeakReference<>(viewGroup);
            return this;
        }

        public Builder setRootLayoutType(int rootLayoutType) {
            this.rootLayoutType = rootLayoutType;
            return this;
        }

        public Builder setNeedFillContainer(boolean needFillContainer) {
            this.needFillContainer = needFillContainer;
            return this;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder setLinearLayoutAttr(Context context) {
            setViewGroup(new LinearLayout(context.getApplicationContext())).setRootLayoutType(ROOT_LINEAR_LAYOUT);
            return this;
        }

        public Builder setFrameLayoutAttr(Context context) {
            setViewGroup(new FrameLayout(context.getApplicationContext())).setRootLayoutType(ROOT_FRAME_LAYOUT);
            return this;
        }

        public Builder setConstraintLayoutAttr(Context context) {
            setViewGroup(new ConstraintLayout(context.getApplicationContext())).setRootLayoutType(ROOT_CONSTRAINT_LAYOUT);
            return this;
        }

        public Builder setRelativeLayoutAttr(Context context) {
            setViewGroup(new RelativeLayout(context.getApplicationContext())).setRootLayoutType(ROOT_RELATIVE_LAYOUT);
            return this;
        }

        public Builder setRecycleLayoutAttr(Context context) {
            RecyclerView recyclerView = new RecyclerView(context.getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
            setViewGroup(recyclerView).setRootLayoutType(ROOT_RECYCLE_LAYOUT);
            return this;
        }

        public Builder setViewPageAttr(Context context) {
            setViewGroup(new ViewPager(context.getApplicationContext())).setRootLayoutType(ROOT_VIEWPAGE_LAYOUT);
            return this;
        }

        public AsyncInflaterResource build() {
            return new AsyncInflaterResource(this);
        }
    }
}
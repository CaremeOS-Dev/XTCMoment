package com.xtc.moment.asynclayout;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.R;
import com.xtc.moment.util.HandlerUtil;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 布局异步预加载器（单例）。
 * <p>
 * 通过一个不可见的 {@link LifeCycleFragment} 绑定 Activity 生命周期，在页面创建前把常用布局
 * 提前 inflate 到缓存队列中，使用时直接从队列取用，减少主线程 inflate 造成的卡顿。
 * 队列空了会在后台自动补充。
 */
public class AsyncLayoutLoader {

    private static final int DEFAULT_CAPACITY = 3;
    private static final String TAG = "AsyncLayoutLoader";

    private static AsyncLayoutLoader mInstance;

    private int capacity;
    private LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(MomentApp.getAppContext(), R.style.CustomTitleBar));
    private HashMap<Integer, Queue<View>> resourceContainer = new HashMap<>();
    private HashMap<Integer, AsyncInflaterResource> inflateInfoContainer = new HashMap<>();
    private HashMap<Integer, AsyncInflaterResource> fillingArray = new HashMap<>();

    public static AsyncLayoutLoader getInstance() {
        if (mInstance == null) {
            synchronized (AsyncLayoutLoader.class) {
                if (mInstance == null) {
                    mInstance = new AsyncLayoutLoader(DEFAULT_CAPACITY);
                }
            }
        }
        return mInstance;
    }

    public static AsyncLayoutLoader getInstance(int capacity) {
        if (mInstance == null) {
            mInstance = new AsyncLayoutLoader(capacity);
        }
        return mInstance;
    }

    private AsyncLayoutLoader(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 为 Activity 绑定生命周期监听，页面销毁时释放 inflater 与所有缓存。
     */
    public void initLoader(Activity activity) {
        LifeCycleFragment lifeCycleFragment = new LifeCycleFragment();
        lifeCycleFragment.setFragmentDestroyListener(new LifeCycleFragment.fragmentDestroyListener() {
            @Override
            public void onDestroy() {
                LogUtil.i(AsyncLayoutLoader.TAG, "page destroy,clear inflater");
                AsyncLayoutLoader.this.inflater = null;
                AsyncLayoutLoader.this.clearCacheLayout();
            }
        });
        FragmentManager fragmentManager = activity.getFragmentManager();
        if (fragmentManager.findFragmentByTag(LifeCycleFragment.TAG) != null) {
            LogUtil.w(TAG, "lifeCycleFragment exist");
            return;
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(lifeCycleFragment, LifeCycleFragment.TAG);
        fragmentTransaction.commitAllowingStateLoss();
        LogUtil.i(TAG, "AsyncLayoutLoader init Loader finish");
    }

    private void clearCacheLayout() {
        this.resourceContainer.clear();
        this.inflateInfoContainer.clear();
        this.fillingArray.clear();
    }

    public void preLoadLayoutResource(AsyncInflaterResource... resources) {
        if (this.inflater == null) {
            LogUtil.i(TAG, "preLoadLayoutResource but inflater is null，check init status");
        }
        preLoadLayoutResource(false, resources);
    }

    /**
     * @param filterFilling true 表示先过滤掉正在补充中的布局，避免重复预加载
     */
    private void preLoadLayoutResource(final boolean filterFilling, final AsyncInflaterResource... resources) {
        AsyncInflaterResource[] filtered = new AsyncInflaterResource[resources.length];
        if (filterFilling) {
            filterFillingResource(filtered, resources);
            resources = filtered;
        }
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                for (AsyncInflaterResource resource : resources) {
                    if (resource == null) {
                        continue;
                    }
                    int layoutId = resource.getLayoutIds();
                    Queue<View> queue = AsyncLayoutLoader.this.resourceContainer.get(layoutId);
                    int targetCapacity = resource.getCapacity();
                    if (targetCapacity <= 0) {
                        targetCapacity = AsyncLayoutLoader.this.capacity;
                    }
                    if (queue == null) {
                        queue = new ArrayBlockingQueue<>(targetCapacity);
                        AsyncLayoutLoader.this.resourceContainer.put(layoutId, queue);
                    }
                    if (queue.size() >= targetCapacity) {
                        LogUtil.i(AsyncLayoutLoader.TAG, "预加载视图资源缓存数满，不进行预加载 tag:" + resource.getTag());
                        continue;
                    }
                    for (int i = 0; i < targetCapacity; i++) {
                        ViewGroup rootView = resource.getRootView();
                        if (AsyncLayoutLoader.this.inflater == null) {
                            LogUtil.i(AsyncLayoutLoader.TAG, "preLoadLayoutResource but inflater is null");
                            return;
                        }
                        try {
                            queue.offer(AsyncLayoutLoader.this.inflater.inflate(layoutId, rootView, false));
                        } catch (NullPointerException e) {
                            LogUtil.i(AsyncLayoutLoader.TAG, "pre inflate view is fail", e);
                            return;
                        }
                    }
                    LogUtil.i(AsyncLayoutLoader.TAG, "预加载视图资源完成 tag:" + resource.getTag());
                    if (AsyncLayoutLoader.this.inflateInfoContainer.get(layoutId) == null) {
                        AsyncLayoutLoader.this.inflateInfoContainer.put(layoutId, resource);
                    }
                    if (filterFilling) {
                        AsyncLayoutLoader.this.fillingArray.remove(layoutId);
                    }
                }
            }
        });
    }

    private void filterFillingResource(AsyncInflaterResource[] out, AsyncInflaterResource... resources) {
        int index = 0;
        for (AsyncInflaterResource resource : resources) {
            if (resource == null) {
                continue;
            }
            AsyncInflaterResource filling = this.fillingArray.get(resource.getLayoutIds());
            if (filling != null) {
                LogUtil.w(TAG, "tag:" + filling.getTag() + " filling,don't repeat pre load");
            } else {
                out[index] = resource;
                this.fillingArray.put(resource.getLayoutIds(), resource);
                index++;
            }
        }
    }

    /**
     * 使用预加载缓存设置 Activity 的 contentView，缓存缺失时回退到同步 inflate。
     */
    public void setContentView(Activity activity, int layoutId) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        AsyncInflaterResource resource = this.inflateInfoContainer.get(layoutId);
        Queue<View> queue = this.resourceContainer.get(layoutId);
        if (queue == null || queue.isEmpty()) {
            activity.setContentView(layoutId);
            if (resource == null) {
                LogUtil.w(TAG, "setContentView resourceContainer not find layout resource");
                return;
            }
            LogUtil.w(TAG, "setContentView resourceContainer not find layout resource name:" + resource.getTag());
            return;
        }
        activity.setContentView(queue.poll());
        if (resource == null) {
            LogUtil.i(TAG, "setContentView pre view, viewQueue size:" + queue.size());
        } else {
            LogUtil.i(TAG, "setContentView pre view, viewQueue size:" + queue.size() + " name:" + resource.getTag());
        }
        fillViewContainer(queue, resource, false);
    }

    public View inflateView(int layoutId, LayoutInflater layoutInflater, ViewGroup root) {
        return inflateView(layoutId, layoutInflater, root, false);
    }

    public View inflateView(int layoutId, LayoutInflater layoutInflater, ViewGroup root, boolean needFillContainer) {
        Queue<View> queue = this.resourceContainer.get(layoutId);
        AsyncInflaterResource resource = this.inflateInfoContainer.get(layoutId);
        if (queue == null || queue.isEmpty()) {
            if (resource == null) {
                LogUtil.w(TAG, "inflateView resourceContainer not find layout resource");
            } else {
                LogUtil.w(TAG, "inflateView resourceContainer not find layout resource name:" + resource.getTag());
            }
            return layoutInflater.inflate(layoutId, root, false);
        }
        if (resource == null) {
            LogUtil.i(TAG, "inflateView pre view, viewQueue size:" + queue.size());
        } else {
            LogUtil.i(TAG, "inflateView pre view, viewQueue size:" + queue.size() + " name:" + resource.getTag());
        }
        View view = queue.poll();
        fillViewContainer(queue, resource, needFillContainer);
        return view;
    }

    private void fillViewContainer(Queue<View> queue, AsyncInflaterResource resource, boolean needFillContainer) {
        if (resource == null || !resource.isNeedFillContainer() || queue == null) {
            return;
        }
        if ((needFillContainer && queue.size() < 3) || queue.isEmpty()) {
            LogUtil.i(TAG, "fillViewContainer view tag:" + resource.getTag());
            preLoadLayoutResource(true, resource);
        }
    }
}
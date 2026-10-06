package com.xtc.moment.asynclayout;

import android.app.Fragment;
import com.xtc.log.LogUtil;

/**
 * 绑定到 Activity 的空 Fragment，用于感知页面销毁并触发缓存清理。
 */
public class LifeCycleFragment extends Fragment {

    public static final String TAG = "LifeCycleFragment";

    private fragmentDestroyListener fragmentDestroyListener;

    public interface fragmentDestroyListener {
        void onDestroy();
    }

    public void setFragmentDestroyListener(fragmentDestroyListener listener) {
        this.fragmentDestroyListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "LifeCycleFragment onDestroy");
        if (fragmentDestroyListener == null) {
            return;
        }
        fragmentDestroyListener.onDestroy();
    }
}
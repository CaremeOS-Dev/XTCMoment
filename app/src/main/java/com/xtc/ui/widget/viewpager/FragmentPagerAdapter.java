package com.xtc.ui.widget.viewpager;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import com.xtc.log.LogUtil;

/** 以 Fragment 为页面的 ViewPager 适配器。 */
public abstract class FragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = FragmentPagerAdapter.class.getSimpleName();
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;
    private final FragmentManager mFragmentManager;

    public abstract Fragment getItem(int position);

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    public FragmentPagerAdapter(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        long itemId = getItemId(position);
        Fragment fragment = this.mFragmentManager.findFragmentByTag(makeFragmentName(container.getId(), itemId));
        if (fragment != null) {
            LogUtil.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
            this.mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            LogUtil.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
            this.mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
        }
        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Detaching item #");
        builder.append(getItemId(position));
        builder.append(": f=");
        builder.append(object);
        builder.append(" v=");
        Fragment fragment = (Fragment) object;
        builder.append(fragment.getView());
        LogUtil.v(TAG, builder.toString());
        this.mCurTransaction.detach(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        Fragment currentPrimaryItem = this.mCurrentPrimaryItem;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            this.mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        FragmentTransaction transaction = this.mCurTransaction;
        if (transaction != null) {
            transaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
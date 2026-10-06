package com.xtc.ui.widget.viewpager;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

/** ViewPager 适配器基类（从 support 库抽取的等价实现）。 */
public abstract class PagerAdapter {
    public static final int POSITION_NONE = -2;
    public static final int POSITION_UNCHANGED = -1;
    private final DataSetObservable mObservable = new DataSetObservable();
    private DataSetObserver mViewPagerObserver;

    public void finishUpdate(View view) {
    }

    public abstract int getCount();

    public int getItemPosition(Object object) {
        return -1;
    }

    public CharSequence getPageTitle(int position) {
        return null;
    }

    public float getPageWidth(int position) {
        return 1.0f;
    }

    public abstract boolean isViewFromObject(View view, Object object);

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public Parcelable saveState() {
        return null;
    }

    public void setPrimaryItem(View view, int position, Object object) {
    }

    public void startUpdate(View view) {
    }

    public void startUpdate(ViewGroup container) {
        startUpdate((View) container);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        return instantiateItem((View) container, position);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        destroyItem((View) container, position, object);
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        setPrimaryItem((View) container, position, object);
    }

    public void finishUpdate(ViewGroup container) {
        finishUpdate((View) container);
    }

    public Object instantiateItem(View container, int position) {
        throw new UnsupportedOperationException("Required method instantiateItem was not overridden");
    }

    public void destroyItem(View container, int position, Object object) {
        throw new UnsupportedOperationException("Required method destroyItem was not overridden");
    }

    public void notifyDataSetChanged() {
        synchronized (this) {
            if (this.mViewPagerObserver != null) {
                this.mViewPagerObserver.onChanged();
            }
        }
        this.mObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this.mObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this.mObservable.unregisterObserver(observer);
    }

    void setViewPagerObserver(DataSetObserver observer) {
        synchronized (this) {
            this.mViewPagerObserver = observer;
        }
    }
}
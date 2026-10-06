package com.xtc.moment.module.community;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 社区详情页 ViewPager 适配器。
 */
public class CommunityPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] mFragmentArray;

    public CommunityPagerAdapter(FragmentManager fragmentManager, Fragment[] fragmentArray) {
        super(fragmentManager);
        this.mFragmentArray = new Fragment[0];
        this.mFragmentArray = fragmentArray;
    }

    @Override
    public Fragment getItem(int position) {
        return this.mFragmentArray[position];
    }

    @Override
    public int getCount() {
        return this.mFragmentArray.length;
    }
}
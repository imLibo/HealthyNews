package com.xieyao.healthynews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by libo on 2016/4/18.
 */
public class AdapterForMainViewPager extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;
    public AdapterForMainViewPager(FragmentManager fm, ArrayList<Fragment> mFragments) {
        super(fm);
        fragments = mFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

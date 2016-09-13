package com.xieyao.healthynews.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by libo on 2016/4/18.
 */
public class AdapterForNewsListFragment extends FragmentPagerAdapter {

    private List<String> mTitles;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments;

    public AdapterForNewsListFragment(FragmentManager fm, List<String> mTitles,List<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
        this.mFragmentManager = fm;
        this.mTitles = mTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}

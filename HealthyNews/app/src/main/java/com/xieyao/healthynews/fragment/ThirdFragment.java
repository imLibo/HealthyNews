package com.xieyao.healthynews.fragment;


import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.xieyao.healthynews.R;
import com.xieyao.healthynews.adapter.AdapterForNewsListFragment;
import com.xieyao.healthynews.entity.EventOfBeginToDeleteFavorite;
import com.xieyao.healthynews.entity.EventOfCancleToDeleteFavorite;
import com.xieyao.healthynews.entity.EventOfDeleteFavoriteFromServer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment implements View.OnClickListener {

    private TabLayout mTabLayout;
    private ViewPager mViewpager;
    private LinearLayout mLinearDelete;
    private ImageButton mImageBtnDelete;
    private ImageButton mImageBtnCancle;

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        EventBus.getDefault().register(this);
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout_thirdfragment);
        mViewpager = (ViewPager) view.findViewById(R.id.viewpager_thirdfragment);
        mLinearDelete = (LinearLayout) view.findViewById(R.id.linearlayout_fourth_edit);
        mImageBtnCancle = (ImageButton) view.findViewById(R.id.imagebutton_fourth_cancle);
        mImageBtnDelete = (ImageButton) view.findViewById(R.id.imagebutton_fourth_done);
        mImageBtnCancle.setOnClickListener(this);
        mImageBtnDelete.setOnClickListener(this);
        setViewPager();
    }

    private void setViewPager() {
        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new FavoriteOfNewsFragment());
        mFragments.add(new FavoriteOfDrugFragment());
        ArrayList<String> mTitles = new ArrayList<>();
        mTitles.add("资讯收藏");
        mTitles.add("药品收藏");
        mViewpager.setAdapter(new AdapterForNewsListFragment(getChildFragmentManager(), mTitles, mFragments));
        mViewpager.setCurrentItem(0, false);
        mViewpager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewpager);
        mTabLayout.setScrollPosition(0, 0, true);
        dynamicSetTablayoutMode(mTabLayout);
    }


    /**
     * 动态修改tab的模式
     *
     * @param tabLayout
     */
    public void dynamicSetTablayoutMode(TabLayout tabLayout) {
        int tabTotalWidth = 0;
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            final View view = tabLayout.getChildAt(i);
            view.measure(0, 0);
            tabTotalWidth += view.getMeasuredWidth();
        }
        if (tabTotalWidth <= getScreenSize(tabLayout.getContext()).x) {
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    public Point getScreenSize(Context context) {

        // 获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    /***
     * 当长按某个item时，触发编辑模式
     *
     * @param beginToDeleteFavorite
     */
    @Subscribe
    public void onEvent(EventOfBeginToDeleteFavorite beginToDeleteFavorite) {
        mTabLayout.setVisibility(View.GONE);
        mLinearDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imagebutton_fourth_cancle:
                mLinearDelete.setVisibility(View.GONE);
                mTabLayout.setVisibility(View.VISIBLE);
                //发布取消更改的事件
                EventBus.getDefault().post(new EventOfCancleToDeleteFavorite());
                break;
            case R.id.imagebutton_fourth_done:
                //发布将更改的收藏同步至云端的事件
                mLinearDelete.setVisibility(View.GONE);
                mTabLayout.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new EventOfDeleteFavoriteFromServer());
                break;
        }
    }
}

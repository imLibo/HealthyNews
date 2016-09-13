package com.xieyao.healthynews.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.gigamole.library.NavigationTabBar;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.adapter.AdapterForMainViewPager;
import com.xieyao.healthynews.base.ActivityManageAndExit;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.fragment.FirstFragment;
import com.xieyao.healthynews.fragment.FourthFragment;
import com.xieyao.healthynews.fragment.SecondeFragment;
import com.xieyao.healthynews.fragment.ThirdFragment;
import com.xieyao.healthynews.util.ToastUtil;

import org.xutils.DbManager;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private NavigationTabBar mTabBar;
    private ViewPager mViewPager;

    @Override
    protected void ColdOpenApp() {
        //冷启动的设置
        setTheme(R.style.AppTheme);
    }


    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected String getmTitle() {
        return "";
    }

    protected void initViews() {
        mViewPager = (ViewPager)findViewById(R.id.viewpager_main);
        mTabBar = (NavigationTabBar)findViewById(R.id.tabbar_main);
        setTabBar();
        createDb();
        IsFirstOpenApp();
    }

    private void createDb() {
        MyApplication.mDaoConfig = new DbManager.DaoConfig()
                .setDbName("healthyIcon.db")
                .setDbVersion(1)
                .setDbDir(this.getFilesDir())
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //// TODO: 2016/4/5
                    }
                });
    }


    /***
     * 是否是第一次打开APP
     */
    private void IsFirstOpenApp() {
        SharedPreferences preferences = getSharedPreferences("preferencesData", 0);
        boolean isFirst = preferences.getBoolean("IsFirst", true);
        //是第一次打开APP
        if (isFirst) {
            //显示引导页
            preferences.edit().putBoolean("IsFirst", false).commit();
            //不是第一次打开APP
        }else {
            //设置欢迎页图片（广告）
            //开始跳过广告的倒计时
        }
    }

    private void setTabBar() {
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new FirstFragment());
        mFragments.add(new SecondeFragment());
        mFragments.add(new ThirdFragment());
        mFragments.add(new FourthFragment());
        mViewPager.setAdapter(new AdapterForMainViewPager(getSupportFragmentManager(),mFragments));
        mViewPager.setOffscreenPageLimit(4);
        final int bgColor = Color.TRANSPARENT;
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model(
                getResources().getDrawable(R.mipmap.tab_first), bgColor,getString(R.string.module_title_first)));
        models.add(new NavigationTabBar.Model(
                getResources().getDrawable(R.mipmap.tab_seconde), bgColor,getString(R.string.module_title_seconde)));
        models.add(new NavigationTabBar.Model(
                getResources().getDrawable(R.mipmap.tab_third), bgColor,getString(R.string.module_title_third)));
        models.add(new NavigationTabBar.Model(
                getResources().getDrawable(R.mipmap.tab_fourth), bgColor,getString(R.string.module_title_fourth)));
        mTabBar.setModels(models);
        mTabBar.setViewPager(mViewPager, 0);
    }

    private long firstTouchTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - firstTouchTime > 2000) {
                ToastUtil.showMyToast(this, "再按一次返回键退出程序");
                firstTouchTime = System.currentTimeMillis();
            } else {
                ActivityManageAndExit.getActivityCollector().AppExit(this);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}

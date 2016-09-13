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
import android.widget.LinearLayout;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.adapter.AdapterForNewsListFragment;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewpager;
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mFenleiId = new ArrayList<>();
    private LinearLayout mLinearLoadingView;

    public FirstFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_first, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        try {
            mTabLayout = (TabLayout) view.findViewById(R.id.tablayout_firstfragment);
            mViewpager = (ViewPager) view.findViewById(R.id.viewpager_firstfragment);
            mLinearLoadingView = (LinearLayout) view.findViewById(R.id.linearlayout_loadingview);
            getNewsFenlei();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViewPager() {
        List<Fragment> mFragments = new ArrayList<>();
        for (String id :
                mFenleiId) {
            LogUtils.i("FenleiId  First>>"+id);
            mFragments.add(new NewListFragment(id));
        }
        mViewpager.setAdapter(new AdapterForNewsListFragment(getChildFragmentManager(),mTitles,mFragments));
        mViewpager.setCurrentItem(0,false);
        mViewpager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewpager);
        mTabLayout.setScrollPosition(0,0,true);
        dynamicSetTablayoutMode(mTabLayout);
    }


    private void getNewsFenlei(){

        Parameters para = new Parameters();
        para.put("apikey",Constanse.BaiduApi_ApiKey);
        ApiStoreSDK.execute(Constanse.Url_Baidu_TgFenlei,ApiStoreSDK.GET,para,
                new ApiCallBack(){
                    @Override
                    public void onSuccess(int i, String s) {
                        LogUtils.i(s);
                        try {
                            JSONObject JsonResult = new JSONObject(s);
                            if(JsonResult.getString("status").equals("true")){
                                JSONArray dataList = JsonResult.getJSONArray("tngou");
                                for (int j = 0; j < dataList.length(); j++) {
                                    JSONObject data = dataList.getJSONObject(j);
                                    mTitles.add(data.getString("name"));
                                    mFenleiId.add(data.getString("id"));
                                }
                                hideProgress();
                                setViewPager();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(int i, String s, Exception e) {
                        super.onError(i, s, e);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });


        /*AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i(result);
                    JSONObject resultJson = new JSONObject(result);
                    if(resultJson.getString("showapi_res_code").equals("0")){
                        JSONArray list = resultJson.getJSONObject("showapi_res_body").getJSONArray("list");
                        for (int j = 0; j < list.length(); j++) {
                            mTitles.add(list.getJSONObject(j).getString("name"));
                            mFenleiId.add(list.getJSONObject(j).getString("id"));
                        }
                        hideProgress();
                        setViewPager();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //ToastUtil.showMyToast(getActivity(),"加载失败");
                LogUtils.i("Error>>"+throwable.getMessage());
            }
        };

        new ShowApiRequest(Constanse.Url_News_Fenlei,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .setResponseHandler(responseHandler)
                .post();*/



    }


    /**
     * 动态修改tab的模式
     *
     * @param tabLayout
     */
    public  void dynamicSetTablayoutMode(TabLayout tabLayout) {
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
    public  Point getScreenSize(Context context) {

        // 获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }

    private void hideProgress(){
        mLinearLoadingView.setVisibility(View.GONE);
    }

}

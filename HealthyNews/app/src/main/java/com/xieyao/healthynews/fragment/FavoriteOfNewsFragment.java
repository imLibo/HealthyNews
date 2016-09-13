package com.xieyao.healthynews.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.activity.LoginActivity;
import com.xieyao.healthynews.activity.NewsDetailActivity;
import com.xieyao.healthynews.adapter.AdapterForFavoriteOfNews;
import com.xieyao.healthynews.adapter.DivierItemDecorationForLinear;
import com.xieyao.healthynews.entity.EventOfBeginToDeleteFavorite;
import com.xieyao.healthynews.entity.EventOfCancleToDeleteFavorite;
import com.xieyao.healthynews.entity.EventOfDeleteFavoriteFromServer;
import com.xieyao.healthynews.entity.EventOfFavoriteIsChange;
import com.xieyao.healthynews.entity.EventOfUnLoginEntity;
import com.xieyao.healthynews.entity.FavoriteOfNewsEntity;
import com.xieyao.healthynews.entity.NewsListEntity;
import com.xieyao.healthynews.entity.UserInfo;
import com.xieyao.healthynews.loadmore.MaterialRefreshLayout;
import com.xieyao.healthynews.loadmore.MaterialRefreshListener;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteOfNewsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<FavoriteOfNewsEntity> mNewsList = new ArrayList<>();
    private MaterialRefreshLayout mRefresh;
    private int mAllPages = 1;
    private int mCurrentPage = 1;
    private AdapterForFavoriteOfNews mAdapter;
    private RelativeLayout mLinearUnlogin;
    private RelativeLayout mLinearEmpty;

    public FavoriteOfNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite_of_news, container, false);
        EventBus.getDefault().register(this);
        initViews(view);
        if(MyApplication.mUserInfo == null){
            mLinearUnlogin.setVisibility(View.VISIBLE);
            mRefresh.setVisibility(View.GONE);
        }else {
            getNewsFavorite(1);
        }
        return view;
    }

    /**
     * 获取收藏列表
     * @param page
     */
    private void getNewsFavorite(final int page) {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes, "utf-8");
                    LogUtils.i("getFavorite>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        //表示是加载更多
                        if(page > mCurrentPage){
                            mRefresh.finishRefreshLoadMore();
                            //表示是刷新
                        }else if(page == 1){
                            //清空列表
                            mRefresh.finishRefresh();
                            mNewsList.clear();
                        }
                        JSONObject pagebean = JsonResult.getJSONObject("showapi_res_body").getJSONObject("pagebean");
                        mAllPages = pagebean.getInt("allPages");
                        mCurrentPage = pagebean.getInt("currentPage");
                        JSONArray dataList = pagebean.getJSONArray("contentlist");
                        if(dataList.length() == 0){
                            mLinearEmpty.setVisibility(View.VISIBLE);
                            mRefresh.setVisibility(View.GONE);
                        }else {
                            mLinearEmpty.setVisibility(View.GONE);
                            mRefresh.setVisibility(View.VISIBLE);
                        }
                        for (int j = 0; j < dataList.length(); j++) {
                            JSONObject data = dataList.getJSONObject(j);
                            FavoriteOfNewsEntity entity = new FavoriteOfNewsEntity();
                            entity.setNewsId(data.getString("newsId"));
                            entity.setCreateTime(data.getString("createTime"));
                            entity.setNewsImgUrl(data.getString("newsImgUrl"));
                            entity.setNewsTitle(data.getString("newsTitle"));
                            entity.setUserPhone("userPhone");
                            mNewsList.add(entity);
                        }

                        mRefresh.finishRefresh();
                        mAdapter.setData(mNewsList);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };

        new ShowApiRequest(Constanse.Url_Db_searchTable, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .setResponseHandler(responseHandler)
                .addTextPara("table", "favoriteForNews")
                .addTextPara("query","{'userPhone':'"+MyApplication.mUserInfo.getUserName()+"'}")
//                .addTextPara("sort", "{'name':-1}")
//                .addTextPara("page", "1")
//                .addTextPara("maxResult", "20")
                .post();
    }

    private void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_favorite_news);
        mRefresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh_favorite_news);
        mLinearUnlogin = (RelativeLayout) view.findViewById(R.id.linearlayout_fourth_unlogin);
        mLinearEmpty = (RelativeLayout) view.findViewById(R.id.linearlayout_empty);
        view.findViewById(R.id.textview_fouth_unlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getNewsFavorite(1);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if(mCurrentPage<mAllPages){
                    getNewsFavorite(mCurrentPage+1);
                }else {
                    mRefresh.finishRefreshLoadMore();
                    //ToastUtil.showMyToast(getActivity(),"没有更多了。");
                }
            }
        });
        setRecycler();
    }

    private void setRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new DivierItemDecorationForLinear(getActivity(), DivierItemDecorationForLinear.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AdapterForFavoriteOfNews(getActivity(), mNewsList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClick(new AdapterForFavoriteOfNews.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),NewsDetailActivity.class);
                NewsListEntity entity = new NewsListEntity();
                entity.setId(mNewsList.get(position).getNewsId());
                entity.setImg(mNewsList.get(position).getNewsImgUrl());
                entity.setImg(mNewsList.get(position).getNewsTitle());
                intent.putExtra("news",entity);
                startActivity(intent);
            }

            @Override
            public void onItemClickOfLong(int position) {
                //发布删除开始的事件，是thirdfragment上方toolbar改变
                EventBus.getDefault().post(new EventOfBeginToDeleteFavorite());
                ToastUtil.showMyToast(getActivity(),"您可以选择一个或多个进行删除。");
                mAdapter.showDelete = true;
                mNewsList.add(new FavoriteOfNewsEntity());
                mNewsList.remove(mNewsList.size()-1);
                mAdapter.setData(mNewsList);
            }
        });
    }

    /**
     * 注销事件的接收
     * @param entity
     */
    @Subscribe
    public void onEvent(EventOfUnLoginEntity entity){
        mLinearUnlogin.setVisibility(View.VISIBLE);
        mRefresh.setVisibility(View.GONE);
        mLinearEmpty.setVisibility(View.GONE);
    }

    /**
     * 登录事件的接收
     * @param info
     */
    @Subscribe
    public void onEvent(UserInfo info){
        mLinearUnlogin.setVisibility(View.GONE);
        mRefresh.setVisibility(View.VISIBLE);
        getNewsFavorite(1);
    }

    /***
     * 收藏更改事件的接收
     * @param isChange
     */
    @Subscribe
    public void onEvent(EventOfFavoriteIsChange isChange){
        getNewsFavorite(1);
    }

    //取消删除收藏的事件接收
    @Subscribe
    public void onEvent(EventOfCancleToDeleteFavorite cancleToDeleteFavorite){
        mAdapter.showDelete = false;
        mAdapter.setData(mNewsList);
    }
    //收藏删除事件接收
    @Subscribe
    public void onEvent(EventOfDeleteFavoriteFromServer deleteFavoriteFromServer){
        //如果选中的条数为零，就隐藏选择框，并不进行删除操作
        if(mAdapter.mDeleteFavorites.size() == 0){
            mAdapter.showDelete = false;
            mNewsList.add(new FavoriteOfNewsEntity());
            mNewsList.remove(mNewsList.size()-1);
            mAdapter.setData(mNewsList);
            return;
        }
        final Dialog dialog = DialogUtil.getOnLoadingDialogCanCancelByBackPress(getActivity());
        dialog.show();
        //循环对每个选中要删除的进行删除
        for (int i = 0; i < mAdapter.mDeleteFavorites.size(); i++) {
            final int finalI = i;
            AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int j, Header[] headers, byte[] bytes) {
                    if(finalI == mAdapter.mDeleteFavorites.size()-1){
                        dialog.dismiss();
                    }
                    try {
                        String result = new String(bytes,"utf-8");
                        LogUtils.i("删除收藏结果："+result);
                        JSONObject jsonResult = new JSONObject(result);
                        if (jsonResult.getString("showapi_res_code").equals("0")) {
                            if(finalI == mAdapter.mDeleteFavorites.size()-1){
                                mAdapter.showDelete = false;
                                getNewsFavorite(1);
                                ToastUtil.showMyToast(getActivity(),"删除成功！");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int j, Header[] headers, byte[] bytes, Throwable throwable) {
                    if(finalI == mAdapter.mDeleteFavorites.size()-1){
                        dialog.dismiss();
                        ToastUtil.showMyToast(getActivity(),"删除失败！");
                    }
                }
            };
            new ShowApiRequest(Constanse.Url_Db_delete, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                    .addTextPara("table", "favoriteForNews")
                    .addTextPara("query", "{'userPhone':'" + MyApplication.mUserInfo.getUserName()
                            + "','newsId':'"+mAdapter.mDeleteFavorites.get(i).getNewsId()+"'}")
                    .setResponseHandler(responseHandler)
                    .post();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

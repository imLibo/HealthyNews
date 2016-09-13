package com.xieyao.healthynews.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.activity.NewsDetailActivity;
import com.xieyao.healthynews.adapter.AdapterForNewsList;
import com.xieyao.healthynews.adapter.DivierItemDecorationForLinear;
import com.xieyao.healthynews.entity.NewsListEntity;
import com.xieyao.healthynews.loadmore.MaterialRefreshLayout;
import com.xieyao.healthynews.loadmore.MaterialRefreshListener;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 新闻列表界面
 */
@SuppressLint("ValidFragment")
public class NewListFragment extends Fragment {

    private RecyclerView mRecylerview;
    private  String mFenleiId;
    private ArrayList<NewsListEntity> mNewsList = new ArrayList<>();
    private AdapterForNewsList mAdapter;
    private MaterialRefreshLayout mRefresh;
    private int mAllPages = 1;
    private int mCurrentPage = 1;

    public NewListFragment(String id) {
        mFenleiId = id;
    }

    /**
     * @return A new instance of fragment NewListFragment.
     */
    /*public static NewListFragment newInstance(String fenleiId) {
        NewListFragment fragment = new NewListFragment();
        mFenleiId = fenleiId;
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_new_list, container, false);
        initViews(view);
        getNews(1);
        return view;
    }

    private void initViews(View view) {
        mRecylerview = (RecyclerView) view.findViewById(R.id.listview_newlist);
        mRefresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh_newlist);
        setRecylerView();

        mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getNews(1);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if(mCurrentPage<mAllPages){
                    getNews(mCurrentPage+1);
                }else {
                    mRefresh.finishRefreshLoadMore();
                    ToastUtil.showMyToast(getActivity(),"没有更多了。");
                }
            }
        });

    }

    private void getNews(final int page){

        Parameters parameters = new Parameters();
        parameters.put("apikey",Constanse.BaiduApi_ApiKey);
        parameters.put("id",mFenleiId);
        parameters.put("page",page+"");
        parameters.put("rows","20");
        ApiStoreSDK.execute(Constanse.Url_Baidu_TgInfo,ApiStoreSDK.GET,parameters,
                new ApiCallBack(){
                    @Override
                    public void onSuccess(int i, String s) {
                        LogUtils.i(s);
                        try {
                            JSONObject JsonResult = new JSONObject(s);
                            if(JsonResult.getString("status").equals("true")){

                                //表示是加载更多
                                if(page > mCurrentPage){
                                    mRefresh.finishRefreshLoadMore();
                                    //表示是刷新
                                }else if(page == 1){
                                    //清空列表
                                    mRefresh.finishRefresh();
                                    mNewsList.clear();
                                }

                                JSONArray dataList = JsonResult.getJSONArray("tngou");
                                mAllPages = JsonResult.getInt("total")/20;
                                mCurrentPage = page;
                                for (int j = 0; j < dataList.length(); j++) {
                                    JSONObject data = dataList.getJSONObject(j);
                                    NewsListEntity entity = new NewsListEntity();
                                    entity.setTitle(data.getString("title"));
                                    entity.setTime(data.getString("time"));
                                    entity.setId(data.getString("id"));
                                    entity.setDescription(data.getString("description"));
                                    entity.setImg(data.getString("img"));
                                    mNewsList.add(entity);
                                }
                                mRefresh.finishRefresh();
                                mAdapter.setData(mNewsList);
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

       /* AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i(result);
                    JSONObject resultJson = new JSONObject(result);
                    if(resultJson.getString("showapi_res_code").equals("0")){
                        //表示是加载更多
                        if(page > mCurrentPage){
                            mRefresh.finishRefreshLoadMore();
                        //表示是刷新
                        }else if(page == 1){
                            //清空列表
                            mRefresh.finishRefresh();
                            mNewsList.clear();
                        }

                        JSONObject pagebean = resultJson.getJSONObject("showapi_res_body").getJSONObject("pagebean");
                        mAllPages = pagebean.getInt("allPages");
                        mCurrentPage = pagebean.getInt("currentPage");
                        JSONArray data = pagebean.getJSONArray("contentlist");
                        for (int j = 0; j < data.length(); j++) {
                            JSONObject news = data.getJSONObject(j);
                            NewsListEntity entity = new NewsListEntity();
                            entity.setId(news.getString("id"));
                            entity.setTid(news.getString("tid"));
                            entity.setAuthor(news.getString("author"));
                            entity.setTime(news.getString("time"));
                            entity.setAuthor(news.getString("author"));
                            entity.setTname(news.getString("tname"));
                            entity.setTitle(news.getString("title"));
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
        LogUtils.i("FenleiId>>"+mFenleiId);
        new ShowApiRequest(Constanse.Url_News_List,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .addTextPara("tid",mFenleiId)
                .addTextPara("page",page+"")
                .setResponseHandler(responseHandler)
                .post();*/
    }

    private void setRecylerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecylerview.addItemDecoration(new DivierItemDecorationForLinear(getActivity(),DivierItemDecorationForLinear.VERTICAL_LIST));
        mRecylerview.setLayoutManager(layoutManager);
        mAdapter = new AdapterForNewsList(getActivity(),mNewsList);
        mRecylerview.setAdapter(mAdapter);
        mAdapter.setOnItemClick(new AdapterForNewsList.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("news",mNewsList.get(position));
                startActivity(intent);
            }
        });

    }

}

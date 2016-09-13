package com.xieyao.healthynews.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.adapter.AdapterForDrugList;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.entity.DrugListEntity;
import com.xieyao.healthynews.entity.DrugSearchResult;
import com.xieyao.healthynews.loadmore.MaterialRefreshLayout;
import com.xieyao.healthynews.loadmore.MaterialRefreshListener;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 药品 搜索结果列表界面
 * Created by bobo1 on 2016/4/30.
 */
public class DrugSearchListActivity extends BaseActivity {
    private DrugSearchResult mDrugSearchResult ;
    private ArrayList<DrugListEntity> mDrugList = new ArrayList<>();
    private MaterialRefreshLayout mRefresh;
    private RecyclerView mRecyclerview;
    private AdapterForDrugList mAdapter;
    private int mAllPage = 1;
    private int mCurrentPage = 1;

    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_drugsearchlist);
    }

    @Override
    protected String getmTitle() {
        return "药品搜索结果";
    }

    @Override
    protected void initViews() throws DbException {
        mDrugSearchResult = getIntent().getParcelableExtra("drugList");
        mDrugList = mDrugSearchResult.getEntityArrayList();
        mAllPage = mDrugSearchResult.getAllPage();
        mCurrentPage = mDrugSearchResult.getCurrentPage();
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview_searchdrug_result);
        mRefresh = (MaterialRefreshLayout) findViewById(R.id.refresh_druglist);

        setRecyclerView();
        mRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                searchYaopin(1);
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if(mCurrentPage<mAllPage){
                    searchYaopin(mCurrentPage+1);
                }else {
                    mRefresh.finishRefreshLoadMore();
                    ToastUtil.showMyToast(DrugSearchListActivity.this,"没有更多了。");
                }
            }
        });
    }

    private void searchYaopin(int page) {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i("SearchResult>>"+result);
                    JSONObject JsonResult = new JSONObject(result);
                    if(JsonResult.getString("showapi_res_code").equals("0")){
                        JSONObject resBody = JsonResult.getJSONObject("showapi_res_body");
                        mCurrentPage = Integer.valueOf(resBody.getString("page"));
                        JSONArray dataList = resBody.getJSONArray("drugList");
                        //每次搜索前都清空列表
                        if(mCurrentPage == 1){
                            mDrugList.clear();
                            mRefresh.finishRefresh();
                        }else {
                            mRefresh.finishRefreshLoadMore();
                        }
                        for (int j = 0; j < dataList.length(); j++) {
                            String json = dataList.getJSONObject(j).toString();
                            Gson gson = new Gson();
                            Type type = new TypeToken<DrugListEntity>(){}.getType();
                            DrugListEntity entity = gson.fromJson(json,type);
                            mDrugList.add(entity);
                        }
                        mRefresh.finishRefresh();
                        mAdapter.setData(mDrugList);
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

        new ShowApiRequest(Constanse.Url_search_yaopin,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .addTextPara("keyword",mDrugSearchResult.getKeyword())
                .addTextPara("limit","5")
                .addTextPara("page",page+"")
                .setResponseHandler(responseHandler)
                .post();

    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerview.addItemDecoration(new DivierItemDecorationForLinear(this,DivierItemDecorationForLinear.VERTICAL_LIST));
        mRecyclerview.setLayoutManager(layoutManager);
        mAdapter = new AdapterForDrugList(this, mDrugList);
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.setOnItemClick(new AdapterForDrugList.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DrugSearchListActivity.this, DrugDetailActivity.class);
                intent.putExtra("drugDetail",mDrugList.get(position));
                startActivity(intent);
            }
        });
    }
}

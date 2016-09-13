package com.xieyao.healthynews.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.activity.DrugSearchListActivity;
import com.xieyao.healthynews.adapter.DivierItemDecorationForGrid;
import com.xieyao.healthynews.adapter.GridRecylerViewAdapter;
import com.xieyao.healthynews.customeview.ClearEditText;
import com.xieyao.healthynews.entity.DrugListEntity;
import com.xieyao.healthynews.entity.DrugSearchResult;
import com.xieyao.healthynews.entity.YaopinFenleiEntity;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ClearEditText mEditSearch;
    private ArrayList<YaopinFenleiEntity> fenleiEntityArrayList = new ArrayList<>();
    private LinearLayout mLinearLoadingView;
    private Button mBtnSearch;
    private ArrayList<DrugListEntity> mDrugList = new ArrayList<>();
    private int mSelectPosition  = 0;
    private TextView mTvSelectClass;

    public SecondeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_seconde, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_search_fenlei);
        mEditSearch = (ClearEditText) view.findViewById(R.id.edittext_search);
        mLinearLoadingView = (LinearLayout) view.findViewById(R.id.linearlayout_loadingview);
        mBtnSearch = (Button) view.findViewById(R.id.button_seconde_search);
        mTvSelectClass = (TextView) view.findViewById(R.id.textview_seconde_selectclass);
        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditSearch.getText().toString().isEmpty()){
                    ToastUtil.showMyToast(getActivity(),"请输入您要搜索的药品名称。");
                }else{
                    searchYaopin();
                }
            }
        });
        getFenlei();
    }

    private void searchYaopin() {
        final Dialog dialog = DialogUtil.getOnLoadingDialogCanCancelByBackPress(getActivity());
        dialog.show();
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i("SearchResult>>"+result);
                    JSONObject JsonResult = new JSONObject(result);
                    if(JsonResult.getString("showapi_res_code").equals("0")){
                        JSONObject resBody = JsonResult.getJSONObject("showapi_res_body");
                        int allresult = Integer.valueOf(resBody.getString("allResults"));
                        int limit = Integer.valueOf(resBody.getString("limit"));
                        int currentPage = Integer.valueOf(resBody.getString("page"));
                        int allPage = 1;
                        if(allresult < limit){
                            allPage = 1;
                        }else {
                            allPage = allresult/limit;
                        }
                        JSONArray dataList = resBody.getJSONArray("drugList");
                        //每次搜索前都清空列表
                        mDrugList.clear();
                        for (int j = 0; j < dataList.length(); j++) {
                            String json = dataList.getJSONObject(j).toString();
                            Gson gson = new Gson();
                            Type type = new TypeToken<DrugListEntity>(){}.getType();
                            DrugListEntity entity = gson.fromJson(json,type);
                            LogUtils.i("DrugItem>>"+entity.toString());
                            mDrugList.add(entity);
                        }
                        DrugSearchResult searchResult = new DrugSearchResult();
                        searchResult.setAllPage(allPage);
                        searchResult.setCurrentPage(currentPage);
                        searchResult.setEntityArrayList(mDrugList);
                        searchResult.setKeyword(mEditSearch.getText().toString());
                        dialog.cancel();
                        Intent intent = new Intent(getActivity(), DrugSearchListActivity.class);
                        intent.putExtra("drugList",searchResult);
                        startActivity(intent);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    dialog.dismiss();
                    ToastUtil.showMyToast(getActivity(),"抱歉，未找到相关数据。");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                dialog.cancel();
            }
        };
        if(fenleiEntityArrayList.size() == 0){
            new ShowApiRequest(Constanse.Url_search_yaopin,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                    .addTextPara("keyword",mEditSearch.getText().toString())
                    .addTextPara("limit","5")
                    .addTextPara("page","1")
                    .setResponseHandler(responseHandler)
                    .post();
        }else {
            new ShowApiRequest(Constanse.Url_search_yaopin,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                    .addTextPara("keyword",mEditSearch.getText().toString())
                    .addTextPara("type",fenleiEntityArrayList.get(mSelectPosition).getType())
                    .addTextPara("limit","5")
                    .addTextPara("page","1")
                    .setResponseHandler(responseHandler)
                    .post();
        }
    }

    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3, LinearLayoutManager.VERTICAL,false));
        mRecyclerView.addItemDecoration(new DivierItemDecorationForGrid(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        GridRecylerViewAdapter adapter = new GridRecylerViewAdapter(getActivity(), fenleiEntityArrayList );
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickAndDataChangedLitener(new GridRecylerViewAdapter.OnItemClickLither() {
            @Override
            public void onItemClick(View view, int position) {
                mSelectPosition = position;
                mTvSelectClass.setText("您选择的分类："+fenleiEntityArrayList.get(position).getType());
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /***
     * 请求分类
     */
    private void getFenlei() {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i(result);
                    JSONObject resultJson = new JSONObject(result);
                    //请求数据成功
                    if(resultJson.getString("showapi_res_code").equals("0")){
                        JSONObject body = resultJson.getJSONObject("showapi_res_body");
                        JSONArray data = body.getJSONArray("data");
                        fenleiEntityArrayList = new ArrayList<>();
                        for (int j = 0; j < data.length(); j++) {
                            YaopinFenleiEntity entity = new YaopinFenleiEntity();
                            entity.setId(data.getJSONObject(j).getString("id"));
                            entity.setType(data.getJSONObject(j).getString("type"));
                            fenleiEntityArrayList.add(entity);
                        }
                        hideProgress();
                        setRecyclerView();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {

            }
        };

        new ShowApiRequest(Constanse.Url_Yaopin_Fenlei,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .setResponseHandler(responseHandler)
                .post();
    }

    private  void hideProgress(){
        mLinearLoadingView.setVisibility(View.GONE);
    }


}

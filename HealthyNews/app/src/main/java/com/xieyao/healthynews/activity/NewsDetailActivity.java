package com.xieyao.healthynews.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.entity.EventOfFavoriteIsChange;
import com.xieyao.healthynews.entity.NewsListEntity;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.TimeUtil;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.io.UnsupportedEncodingException;

/**
 * 新闻详情
 * Created by bobo1 on 2016/4/30.
 */
public class NewsDetailActivity extends BaseActivity {
    private NewsListEntity mNewDetail;
    private TextView mTvTitle;
    private TextView mTvTime;
    private Toolbar mToolbar;
    private WebView mWebview;
    private Dialog dialog;

    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_newsdetail);
    }

    @Override
    protected String getmTitle() {
        return "资讯详情";
    }

    @Override
    protected void initViews() throws DbException {
        dialog = DialogUtil.getOnLoadingDialogCanCancelByBackPress(this);
        mNewDetail = getIntent().getParcelableExtra("news");
        mTvTitle = (TextView) findViewById(R.id.textview_newsdetail_title);
        mTvTime = (TextView )findViewById(R.id.textview_newsdetail_time);
        mWebview = (WebView) findViewById(R.id.webview_newsdetail_content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_menu_favorite:
                        if(MyApplication.mUserInfo == null){
                            startActivity(new Intent(NewsDetailActivity.this,LoginActivity.class));
                        }else {
                            checkIsHasFavorite();
                        }
                        break;
                    case R.id.item_menu_share:
                        share();
                        break;
                }
                return false;
            }
        });

        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        dialog.show();
        getNewsDetail();
    }

    private void share() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.DOUBAN
                };
        new ShareAction(this).setDisplayList( displaylist )
                .withText( mNewDetail.getTitle() )
                .withTitle("title")
                .withTargetUrl("http://www.baidu.com")
                .open();
    }

    private void addThisNewsToFavorite() {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes,"utf-8");
                    LogUtils.i("addFavorite>>"+result);
                    JSONObject JsonResult = new JSONObject(result);
                    if(JsonResult.getString("showapi_res_code").equals("0")){
                        ToastUtil.showMyToast(NewsDetailActivity.this,"收藏成功！");
                        //发布收藏更改的事件
                        EventBus.getDefault().post(new EventOfFavoriteIsChange());
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

        new ShowApiRequest(Constanse.Url_Db_addTable,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .addTextPara("table","favoriteForNews")
                .setResponseHandler(responseHandler)
                .addTextPara("item","{'userPhone':'"+ MyApplication.mUserInfo.getUserName()+"','newsId':'"+mNewDetail.getId()+"','createTime':'"+ TimeUtil.getTimeForSave()+"','newsTitle':'"+mNewDetail.getTitle()+"','newsImgUrl':'"+"http://tnfs.tngou.net/image"+mNewDetail.getImg()+"'}")
                .post();
    }

    private void checkIsHasFavorite(){
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = null;
                try {
                    result = new String(bytes, "utf-8");
                    LogUtils.i("checkIsHasFavorite>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        JSONObject body = JsonResult.getJSONObject("showapi_res_body");
                        if (body.has("list")) {
                            JSONArray list = body.getJSONArray("list");
                            boolean hasFavorite = false;
                            for (int j = 0; j < list.length(); j++) {
                                if(list.getJSONObject(j).getString("userPhone").equals(MyApplication.mUserInfo.getUserName())){
                                    ToastUtil.showMyToast(NewsDetailActivity.this,"您已收藏过了！");
                                    hasFavorite = true;
                                    break;
                                }
                            }
                            if(!hasFavorite){
                                addThisNewsToFavorite();
                                //发布收藏更改的事件
                                EventBus.getDefault().post(new EventOfFavoriteIsChange());
                            }
                        }else {
                            addThisNewsToFavorite();
                            //发布收藏更改的事件
                            EventBus.getDefault().post(new EventOfFavoriteIsChange());
                        }
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

        new ShowApiRequest(Constanse.Url_showapi_query_listdata,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
                .addTextPara("table", "favoriteForNews")
                .addTextPara("query", "{'newsId':'" + mNewDetail.getId() + "'}")
                .setResponseHandler(responseHandler)
                .post();
    }



    private void getNewsDetail() {
        Parameters parameters = new Parameters();
        parameters.put("apikey",Constanse.BaiduApi_ApiKey);
        parameters.put("id", mNewDetail.getId());
        ApiStoreSDK.execute(Constanse.Url_Baidu_TgDetail,ApiStoreSDK.GET,parameters,
                new ApiCallBack(){
                    @Override
                    public void onSuccess(int i, String s) {
                        LogUtils.i(s);
                        dialog.cancel();
                        try {
                            JSONObject JsonResult = new JSONObject(s);
                            if(JsonResult.getString("status").equals("true")){
                                mTvTime.setText(TimeUtil.FormatTimeToYearAndMonth(Long.valueOf(JsonResult.getString("time"))));
                                mTvTitle.setText(JsonResult.getString("title"));
                                String html = JsonResult.getString("message");
                                mWebview.loadDataWithBaseURL(null,html,"text/html","utf-8",null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(int i, String s, Exception e) {
                        super.onError(i, s, e);
                        dialog.cancel();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        dialog.cancel();
                    }
                });

        /*AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String (bytes,"utf-8");
                    LogUtils.i(result);
                    JSONObject jsonResult =  new JSONObject(result);
                    if(jsonResult.getString("showapi_res_code").equals("0")){
                        JSONObject data = jsonResult.getJSONObject("showapi_res_body").getJSONObject("item");
                        mTvTitle.setText(data.getString("title"));
                        mTvTime.setText(data.getString("time"));
                        mTvSource.setText("来源："+data.getString("author"));
                        mTvContent.setText(data.getString("content"));
                        mTvTitle.setText(data.getString("title"));
                        if(data.has("img")){
                            mNewDetail.setImg(data.getString("img"));
                            Glide.with(NewsDetailActivity.this).load(data.getString("img")).into(mImage);
                        }else {
                            mImage.setVisibility(View.GONE);
                        }
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

        new ShowApiRequest(Constanse.Url_News_Detail,Constanse.ShowApi_APPId,Constanse.ShowApi_Secret)
        .addTextPara("id",mNewDetail.getId())
        .setResponseHandler(responseHandler)
        .post();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setShared(){
        String wxAppID = "wx0fd79887c0a8453f";
        String wxAppSecret = "20c982f3ac15c54f4abc56b79ef14726";

    }

}

package com.xieyao.healthynews.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.entity.DrugListEntity;
import com.xieyao.healthynews.entity.EventOfFavoriteIsChange;
import com.xieyao.healthynews.util.Constanse;
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
 * 药品详情
 * Created by bobo1 on 2016/5/2.
 */
public class DrugDetailActivity extends BaseActivity {

    private DrugListEntity mDrugInfo;

    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_drugdetail);
    }

    @Override
    protected String getmTitle() {
        return "药品详情";
    }

    @Override
    protected void initViews() throws DbException {
        mDrugInfo = getIntent().getParcelableExtra("drugDetail");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_menu_favorite:
                        if (MyApplication.mUserInfo != null) {
                            checkIsHasFavorite();
                        } else {
                            startActivity(new Intent(DrugDetailActivity.this, LoginActivity.class));
                        }
                        break;
                    case R.id.item_menu_share:
                        share();
                        break;
                }
                return false;
            }
        });

        ((TextView) findViewById(R.id.textview_drugdetail_name)).setText(mDrugInfo.getDrugName());
        ((TextView) findViewById(R.id.textview_drugdetail_scqy)).setText(mDrugInfo.getManu());
        ((TextView) findViewById(R.id.textview_drugdetail_ggxh)).setText(mDrugInfo.getGgxh());
        ((TextView) findViewById(R.id.textview_drugdetail_ckjg)).setText(mDrugInfo.getPrice());
        ((TextView) findViewById(R.id.textview_drugdetail_yfyl)).setText(mDrugInfo.getYfyl());
        ((TextView) findViewById(R.id.textview_drugdetail_jinji)).setText(mDrugInfo.getJj());
        ((TextView) findViewById(R.id.textview_drugdetail_pzwh)).setText(mDrugInfo.getPzwh());
        ((TextView) findViewById(R.id.textview_drugdetail_syz)).setText(mDrugInfo.getSyz());
        ((TextView) findViewById(R.id.textview_drugdetail_yxq)).setText(mDrugInfo.getYxq());
        ((TextView) findViewById(R.id.textview_drugdetail_zysx)).setText(mDrugInfo.getZysx());
        ((TextView) findViewById(R.id.textview_drugdetail_zzjb)).setText(mDrugInfo.getZzjb());
        ((TextView) findViewById(R.id.textview_drugdetail_zycf)).setText(mDrugInfo.getZycf());
    }

    private void addThisDrugToFavorite() {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes, "utf-8");
                    LogUtils.i("addFavorite>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        ToastUtil.showMyToast(DrugDetailActivity.this, "收藏成功！");
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

        new ShowApiRequest(Constanse.Url_Db_addTable, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "favoriteForDrug")
                .setResponseHandler(responseHandler)
                .addTextPara("item", "{'userPhone':'" + MyApplication.mUserInfo.getUserName() + "','newsId':'" + mDrugInfo.getId() + "','createTime':'" + TimeUtil.getTimeForSave() + "','drugName':'" + mDrugInfo.getDrugName() + "','drugImgUrl':'" + mDrugInfo.getImg() + "'}")
                .post();
    }

    private void share() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.DOUBAN
                };
        new ShareAction(this).setDisplayList( displaylist )
                .withText( mDrugInfo.getDrugName() )
                .withTitle("title")
                .withTargetUrl("http://www.baidu.com")
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {

                    }
                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                })
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        new ShareAction(DrugDetailActivity.this).setPlatform(share_media).setCallback(testmulListener)
                                .withText("多平台分享")
                                .share();
                    }
                })
                .open();
    }

    private UMShareListener testmulListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(DrugDetailActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(DrugDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(DrugDetailActivity.this, platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private void checkIsHasFavorite() {
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
                                if (list.getJSONObject(j).getString("userPhone").equals(MyApplication.mUserInfo.getUserName())) {
                                    ToastUtil.showMyToast(DrugDetailActivity.this, "您已收藏过了！");
                                    hasFavorite = true;
                                    break;
                                }
                            }
                            if (!hasFavorite) {
                                addThisDrugToFavorite();
                                //发布收藏更改的事件
                                EventBus.getDefault().post(new EventOfFavoriteIsChange());
                            }
                        } else {
                            addThisDrugToFavorite();
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

        new ShowApiRequest(Constanse.Url_showapi_query_listdata, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "favoriteForDrug")
                .addTextPara("query", "{'newsId':'" + mDrugInfo.getId() + "'}")
                .setResponseHandler(responseHandler)
                .post();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

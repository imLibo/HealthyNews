package com.xieyao.healthynews;

import android.app.Application;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.umeng.socialize.PlatformConfig;
import com.xieyao.healthynews.entity.UserInfo;
import com.xieyao.healthynews.util.Constanse;

import org.xutils.DbManager;

import cn.smssdk.SMSSDK;

/**
 *
 * Created by libo on 2016/4/18.
 */
public class MyApplication extends Application {

    public static  boolean DEBUG = true;
    public static  boolean isConnectedNet = true;
    public static UserInfo mUserInfo;
    public static DbManager.DaoConfig mDaoConfig;
    @Override
    public void onCreate() {
        super.onCreate();
        //sms sdk的初始化
        SMSSDK.initSDK(this, Constanse.SMSSDK_APP_KEY, Constanse.SMSSDK_APP_SECRET);
        //设置微信分享
        PlatformConfig.setWeixin("wx799928cb9cba127d", "d3d200b7a33cc5f57e1167a147917258");
        //百度API 的初始化
        ApiStoreSDK.init(this,Constanse.BaiduApi_ApiKey);
    }
}

package com.xieyao.healthynews.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.SystemBarTintManager;

import org.xutils.ex.DbException;

/**
 * Created by libo on 2016/3/25.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    private NetWorkStateReceiver netWorkStateReceiver;
    private boolean hasShowNetDialog = true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //冷启动设置
        ColdOpenApp();
        super.onCreate(savedInstanceState);
        try {
            BindUI();
            //将当前activity加入堆栈
            ActivityManageAndExit.getActivityCollector().addActivity(this);
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constanse.ACTION_NETWORK_RECEIVER);
            intentFilter.addAction(Constanse.ACTION_LOGIN_STATE);
            registerReceiver(netWorkStateReceiver,intentFilter);
            setStatus();
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected  void ColdOpenApp(){};

    protected abstract void BindUI();

    protected abstract String getmTitle();

    protected abstract void initViews() throws DbException;

    private void setStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);//通知栏所需颜色
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(getmTitle());
                getSupportActionBar().setHomeButtonEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }

        }
        /*FrameLayout frameLayout = (FrameLayout) findViewById(R.id.toolbar_framelayout);
        TextView title = (TextView) findViewById(R.id.textview_title);
        if (frameLayout != null&&title!=null) {
            title.setText(getmTitle());
            frameLayout.findViewById(R.id.imagebutton_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.i("BackPressed");
                    onBackPressed();
                }
            });
        }*/
    }
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netWorkStateReceiver);
        super.onDestroy();
    }

    /***
     * 网络状态广播接收器
     */
    class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Constanse.ACTION_NETWORK_RECEIVER)){
                handleNetworkChange(context);
            }else if(intent.getAction().equals(Constanse.ACTION_LOGIN_STATE)){

            }
        }
    }

    private void handleNetworkChange(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isAvailable()){
            if(info.getTypeName().equals("WIFI")){
                LogUtils.i("连接至wifi");
                //ToastUtil.ShowToast(context,"已连接至WIFI网络");
            }else if(info.getTypeName().equals("mobile")){
                LogUtils.i("连接至mobile");
                //ToastUtil.ShowToast(context,"已连接至移动数据网络");
            }
            hasShowNetDialog = true;
            MyApplication.isConnectedNet = true;
        }else {
            LogUtils.i("断开网络连接");
            MyApplication.isConnectedNet = false;
            /*if(hasShowNetDialog && BaseActivity.this instanceof MainActivity){
                hasShowNetDialog = false;
                DialogUtil.showNetworkSetDialog(context);
            }*/
            //ToastUtil.ShowToast(context,"已断开网络连接");
        }
    }
    @Override
    public void onClick(View v) {

    }
    public void showProgress(){

    };

    public void hideProgress(){

    }

    public void showEmpty(){

    }

    public void hideEmpty(){

    }
}

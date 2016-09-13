package com.xieyao.healthynews.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.entity.UserInfo;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.SharedPreferenceUtil;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;

import java.io.UnsupportedEncodingException;

/**
 * 登录界面
 * Created by bobo1 on 2016/5/4.
 */
public class LoginActivity extends BaseActivity {
    private EditText mEditUserName;
    private EditText mEditPassword;
    private Button mBtnLogin;
    private TextView mTvRegist;

    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected String getmTitle() {
        return "登录";
    }

    @Override
    protected void initViews() throws DbException {
        mEditUserName = (EditText) findViewById(R.id.edittext_login_username);
        mEditPassword = (EditText) findViewById(R.id.edittext_login_password);
        mBtnLogin = (Button) findViewById(R.id.button_login_login);
        mTvRegist = (TextView) findViewById(R.id.textview_login_regist);
        //填充已登录的用户到编辑框
        mEditUserName.setText(SharedPreferenceUtil.getString(this,"LastLoginUser"));
        mTvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void regist() {
        startActivity(new Intent(this, RegistActivity.class));
    }

    private void Login() {
        final Dialog dialog = DialogUtil.getOnLoadingDialogCanCancelByBackPress(this);
        final String userName = mEditUserName.getText().toString();
        final String password = mEditPassword.getText().toString();
        if (userName.isEmpty() || password.isEmpty()) {
            ToastUtil.showMyToast(this, "请输入正确的账户名或密码！");
        } else if (!userName.isEmpty() && !password.isEmpty()) {
            dialog.show();
            AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    dialog.cancel();
                    try {
                        String result = new String(bytes, "utf-8");
                        LogUtils.i("QueryLoginData>>" + result);
                        JSONObject JsonResult = new JSONObject(result);
                        if (JsonResult.getString("showapi_res_code").equals("0")) {
                            JSONObject body = JsonResult.getJSONObject("showapi_res_body");
                            if (body.has("item")) {
                                JSONObject item = body.getJSONObject("item");
                                if (item.getString("password").equals(password)) {
                                    MyApplication.mUserInfo = new UserInfo();
                                    MyApplication.mUserInfo.setUserName(item.getString("userName"));
                                    MyApplication.mUserInfo.setNickName(item.getString("nickName"));
                                    MyApplication.mUserInfo.setPassword(item.getString("password"));
                                    MyApplication.mUserInfo.setSex(item.getString("sex"));
                                    //登录成功之后
                                    //将用户名保存在本地
                                    SharedPreferenceUtil.saveString(LoginActivity.this,"LastLoginUser",userName);
                                    ToastUtil.showMyToast(LoginActivity.this,"欢迎您，"+userName);
                                    EventBus.getDefault().post(MyApplication.mUserInfo);
                                    finish();
                                } else {
                                    ToastUtil.showMyToast(LoginActivity.this, "密码错误！");
                                }
                            } else {
                                ToastUtil.showMyToast(LoginActivity.this, "该账号未注册！");
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
                    dialog.cancel();
                }
            };

            new ShowApiRequest(Constanse.Url_showapi_query_onedata, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                    .setResponseHandler(responseHandler)
                    .addTextPara("table", "userDb")
                    .addTextPara("query", "{'userName':'" + userName + "'}")
                    .post();
        }

    }
}

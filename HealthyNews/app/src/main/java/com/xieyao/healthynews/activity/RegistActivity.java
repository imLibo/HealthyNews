package com.xieyao.healthynews.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.base.BaseActivity;
import com.xieyao.healthynews.entity.UserIconTable;
import com.xieyao.healthynews.entity.UserInfo;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.DialogUtil;
import com.xieyao.healthynews.util.LogUtils;
import com.xieyao.healthynews.util.TimeUtil;
import com.xieyao.healthynews.util.ToastUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 注册界面
 * Created by bobo1 on 2016/5/4.
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEditUsername;
    private EditText mEditPassword;
    private EditText mEditConfirm;
    private Button mBtnSendSms;
    private EditText mEditSMSCode;
    private Button mBtnRegist;
    private Bitmap mUserBitmap;

    private int HuoQu_SMS = 0;
    private int TiJiao_SMS = 1;
    private Dialog dialog;

    /*private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.getData().getInt("data") == HuoQu_SMS) {
                dialog.dismiss();
                Snackbar.make(mEditSMSCode, "验证码已发送到您的手机上，请将验证码填写在输入框中完成验证", Snackbar.LENGTH_LONG).show();
            } else if (msg.getData().getInt("data") == TiJiao_SMS) {
                LogUtils.i("提交验证码");
                addUserToServer();
            }
        }
    };*/
    private ImageView mImageIcon;
    private EditText mEditNickname;
    private RadioGroup mRadiogroup;

    private void showSuccessDialog() {
        new MaterialDialog.Builder(RegistActivity.this)
                .title("提示")
                .content("注册成功，跳转")
                .negativeText("取消")
                .positiveText("确定")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).cancelable(false).show();
    }

    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_regist);
    }

    @Override
    protected String getmTitle() {
        return "注册";
    }

    @Override
    protected void initViews() throws DbException {
        EventBus.getDefault().register(this);
        dialog = DialogUtil.getOnLoadingDialogCanCancelByBackPress(this);
        mEditUsername = (EditText) findViewById(R.id.edittext_regist_username);
        mEditPassword = (EditText) findViewById(R.id.edittext_regist_password);
        mEditConfirm = (EditText) findViewById(R.id.edittext_regist_confirm);
        //mEditSMSCode = (EditText) findViewById(R.id.edittext_regist_smscode);
        //mBtnSendSms = (Button) findViewById(R.id.button_regist_sendsms);
        mBtnRegist = (Button) findViewById(R.id.button_regist);
        mBtnRegist.setOnClickListener(this);
        mImageIcon = (ImageView) findViewById(R.id.imageview_regist_usericon);
        mImageIcon.setOnClickListener(this);
        mEditNickname = (EditText) findViewById(R.id.edittext_regist_nickname);
        mRadiogroup = (RadioGroup) findViewById(R.id.radiogroup_regist);
       /* mBtnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_regist_usericon:
                chooseIcon();
                break;
            case R.id.button_regist:
                regist();
                break;
        }
    }

    private void regist() {
        try {
            String phone = mEditUsername.getText().toString();
            String password = mEditPassword.getText().toString();
            String confirm = mEditConfirm.getText().toString();
            String nickName = mEditNickname.getText().toString();
            //String userIcon = bitmaptoString(mUserBitmap);
            int id = mRadiogroup.getCheckedRadioButtonId();
            String sex = "男";
            if (R.id.radiobutton_man == id) {
                sex = "男";
            } else if (R.id.radiobutton_women == id) {
                sex = "女";
            }
            if (phone.isEmpty()) {
                ToastUtil.showMyToast(this, "请输入注册电话号码");
            } else if (nickName.isEmpty()) {
                ToastUtil.showMyToast(this, "请输入昵称");
            } else if (password.isEmpty()) {
                ToastUtil.showMyToast(this, "请输入密码");
            } else if (confirm.isEmpty()) {
                ToastUtil.showMyToast(this, "请再次输入密码");
            } else if (!password.equals(confirm)) {
                ToastUtil.showMyToast(this, "两次输入密码不一致");
            } else {
                checkThisUserHasRegist(phone, password, nickName, sex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查服务器里是否已有注册数据
     *
     * @param phone
     * @param password
     * @param nickName
     * @param sex
     */
    private void checkThisUserHasRegist(final String phone, final String password, final String nickName, final String sex) {
        dialog.show();
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                dialog.dismiss();
                try {
                    String result = new String(bytes, "utf-8");
                    LogUtils.i("QueryLoginData>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        JSONObject body = JsonResult.getJSONObject("showapi_res_body");
                        //当查询到有数据时，提示用户已注册
                        if (body.has("item")) {
                            JSONObject item = body.getJSONObject("item");
                            if (item.getString("userName").equals(phone)) {
                                ToastUtil.showMyToast(RegistActivity.this, "该手机号已注册！");
                            }
                        } else {
                            //当服务器查不到该手机号对应的用户时，进行注册
                            addUserToServer(phone, password, nickName, sex);
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
                dialog.dismiss();
            }
        };

        new ShowApiRequest(Constanse.Url_showapi_query_onedata, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .setResponseHandler(responseHandler)
                .addTextPara("table", "userDb")
                .addTextPara("query", "{'userName':'" + phone + "'}")
                .post();
    }

    /**
     * 添加新用户到服务器
     *
     * @param phone
     * @param password
     * @param nickName
     * @param sex
     */
    private void addUserToServer(final String phone, final String password, final String nickName, final String sex) {
        LogUtils.i("RegistToServer");
        dialog.show();
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                dialog.dismiss();
                try {
                    dialog.dismiss();
                    String result = new String(bytes, "utf-8");
                    LogUtils.i("addUser>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        ToastUtil.showMyToast(RegistActivity.this, "注册成功");
                        MyApplication.mUserInfo = new UserInfo();
                        MyApplication.mUserInfo.setSex(sex);
                        MyApplication.mUserInfo.setNickName(nickName);
                        MyApplication.mUserInfo.setPassword(password);
                        MyApplication.mUserInfo.setUserName(phone);
                        saveIconToDB(phone);
                        //保存头像到数据库后，返回到主界面
                        //并发布登录信息的事件，以便主界面更新UI
                        EventBus.getDefault().post(MyApplication.mUserInfo);
                        startActivity(new Intent(RegistActivity.this, MainActivity.class));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                dialog.dismiss();
                LogUtils.i(throwable.getMessage());
            }
        };
        new ShowApiRequest(Constanse.Url_Db_addTable, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "userDb")
                .setResponseHandler(responseHandler)
                .addTextPara("item", "{'userName':'" + phone + "','password':'" + password +
                        "','createTime':'" + TimeUtil.getTimeForSave() +
                        "','userIcon':'" + "" +
                        "','nickName':'" + nickName +
                        "','sex':'" + sex + "'" +
                        "}")
                .post();
    }

    /***
     * 保存用户头像到SQLite数据库
     * @param phone
     */
    private void saveIconToDB(String phone) {
        try {
            UserIconTable table = new UserIconTable();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mUserBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            table.setUserPhone(phone);
            table.setIconBytes(outputStream.toByteArray());
            LogUtils.i(table.toString());
            x.getDb(MyApplication.mDaoConfig).save(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从图片库选取图片
     */
    private void chooseIcon() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            //获取到图片后
            Uri originalUri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(originalUri, proj,
                    null, null, null);
            cursor.moveToFirst();
            //图片路径
            String mImagePath = cursor.getString(0);
            Intent intent = new Intent(this, ChooseIconActivity.class);
            intent.putExtra("imagePath", mImagePath);
            //从手机图片库获取到图片后，转到图片裁剪界面
            startActivity(intent);
        }
    }

    /**
     * 当图片裁剪完成后，EventBus的事件获取
     *
     * @param bitmap
     */
    @Subscribe
    public void onEvent(Bitmap bitmap) {
        mUserBitmap = bitmap;
        mImageIcon.setImageBitmap(bitmap);
    }

    /**
     * 将Bitmap转换成字符串
    public String bitmaptoString(Bitmap bitmap) {
        String string = null;
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            byte[] bytes = bStream.toByteArray();
            string = new String(bytes, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string;
    }
*/
    /**
     * 将字符串转换成Bitmap类型
     */
   /* public Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }*/


    /*private void sendSMS() {
        String phone = mEditUsername.getText().toString();
        if(phone.isEmpty()){
            ToastUtil.showMyToast(this,"请输入注册手机号码！");
        }else{
            SMSSDK.initSDK(this, Constanse.SMSSDK_APP_KEY, Constanse.SMSSDK_APP_SECRET);
            EventHandler eventHandler = new EventHandler() {
                @Override
                public void afterEvent(int event, int result, Object data) {
                    //回馈完成
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        //提交验证码成功
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("data", TiJiao_SMS);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            //获取验证码成功
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("data", HuoQu_SMS);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                            //返回支持发送验证码的国家列表
                        } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                        }
                    } else {
                        ((Throwable) data).printStackTrace();
                        Snackbar.make(mEditSMSCode,"验证失败",Snackbar.LENGTH_SHORT).show();
                    }
                }
            };
            SMSSDK.registerEventHandler(eventHandler);
            SMSSDK.getVerificationCode("86", phone);
        }
    }*/


    /*private void addUserToServer() {
        LogUtils.i("");
        String phone = mEditUsername.getText().toString();
        String password = mEditPassword.getText().toString();
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String result = new String(bytes, "utf-8");
                    LogUtils.i("addUser>>" + result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        //注册成功之后，显示对话框
                        //showSuccessDialog();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtils.i(throwable.getMessage());
            }
        };

        new ShowApiRequest(Constanse.Url_Db_addTable, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "userDb")
                .setResponseHandler(responseHandler)
                .addTextPara("item", "{'userName':'" + phone +
                        "','password':'" + password +
                        "','createTime':'" + TimeUtil.getTimeForSave() +
                        "','userIcon':'" + "头像" +
                        "','nickName':'" + "" +
                        "','sex':'" + "男" +
                        "}")
                .post();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //SMSSDK.unregisterAllEventHandler();
    }
}

package com.xieyao.healthynews.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.show.api.ShowApiRequest;
import com.xieyao.healthynews.MyApplication;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.activity.ChooseIconActivity;
import com.xieyao.healthynews.activity.LoginActivity;
import com.xieyao.healthynews.entity.EventOfUnLoginEntity;
import com.xieyao.healthynews.entity.UserIconTable;
import com.xieyao.healthynews.entity.UserInfo;
import com.xieyao.healthynews.util.Constanse;
import com.xieyao.healthynews.util.LogUtils;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FourthFragment extends Fragment implements View.OnClickListener {

    private CircleImageView mImgUser;
    private TextView mTvName;
    private TextView mTvAge;
    private TextView mTvPhone;
    private ImageButton mImageEdit;
    private Button mBtnUnlogin;

    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        //注册订阅事件
        EventBus.getDefault().register(this);
        mImgUser = (CircleImageView) view.findViewById(R.id.imageView_usericon);
        mTvName = (TextView) view.findViewById(R.id.textview_fourth_name);
        mTvPhone = (TextView) view.findViewById(R.id.textview_fourth_phone);
        mTvAge = (TextView) view.findViewById(R.id.textview_fourth_age);
        mImageEdit = (ImageButton) view.findViewById(R.id.imageView_fourth_edit);
        mBtnUnlogin = (Button) view.findViewById(R.id.button_selfdetail_unlogin);
        mImgUser.setOnClickListener(this);
        mImageEdit.setOnClickListener(this);
        mBtnUnlogin.setOnClickListener(this);
        //deleteOnCount("18581916544");
        //deleteOnCount("18381333579");
    }

    private void deleteOnCount(String userName) {
        new ShowApiRequest(Constanse.Url_Db_delete, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "userDb")
                .addTextPara("query", "{'userName':'" + userName + "'}")
                .post();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_fourth_edit:
                if (MyApplication.mUserInfo == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    showEditUserInfoDialog();
                    //startActivity(new Intent(getActivity(), EditUserInfoActivity.class));
                }
                break;
            case R.id.imageView_usericon:
                if (MyApplication.mUserInfo == null) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else {
                    showChangeIconDialog();
                }
                break;
            case R.id.button_selfdetail_unlogin:
                showUnloginDialog(getActivity());
                break;
        }
    }

    private void showChangeIconDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_restsuccess);
        View view = View.inflate(getActivity(), R.layout.layout_dialog_changeicon, null);
        view.findViewById(R.id.relativeLayout_changeicon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseIcon();
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        dialog.show();
    }

    private void showEditUserInfoDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_restsuccess);
        View view = View.inflate(getActivity(), R.layout.activity_edituserinfo, null);
        view.findViewById(R.id.imagebutton_edit_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        final EditText mEditNickName = (EditText) view.findViewById(R.id.edittext_edit_nickname);
        mEditNickName.setText(MyApplication.mUserInfo.getNickName());
        final RadioGroup mRadioGroup = (RadioGroup) view.findViewById(R.id.radiogroup_edit);
        view.findViewById(R.id.button_edit_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sex = MyApplication.mUserInfo.getSex();
                String nickName = mEditNickName.getText().toString();
                if (mRadioGroup.getCheckedRadioButtonId() == R.id.radiobutton_man_edit) {
                    sex = "男";
                } else if (mRadioGroup.getCheckedRadioButtonId() == R.id.radiobutton_women_edit) {
                    sex = "女";
                }
                updateUserInfo(dialog,sex, nickName);
            }
        });
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateUserInfo(final Dialog dialog, final String sex, final String nickName) {
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                dialog.cancel();
                try {
                    String result = new String(bytes, "utf-8");
                    LogUtils.i(result);
                    JSONObject JsonResult = new JSONObject(result);
                    if (JsonResult.getString("showapi_res_code").equals("0")) {
                        ToastUtil.showMyToast(getActivity(), "修改成功！");
                        //修改成功后更新UI并更新登录状态中的个人信息
                        mTvAge.setText("性别："+sex);
                        mTvName.setText("昵称："+nickName);
                        MyApplication.mUserInfo.setNickName(nickName);
                        MyApplication.mUserInfo.setSex(sex);
                    } else {
                        ToastUtil.showMyToast(getActivity(), "修改失败。");
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

        new ShowApiRequest(Constanse.Url_Db_update, Constanse.ShowApi_APPId, Constanse.ShowApi_Secret)
                .addTextPara("table", "userDb")
                .addTextPara("query", "{'userName':'" + MyApplication.mUserInfo.getUserName() + "'}")
                .addTextPara("toUpdate", "{'nickName':'" + nickName + "','sex':'" + sex + "'}")
                .setResponseHandler(responseHandler)
                .post();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null) {
            //获取到图片后
            Uri originalUri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(originalUri, proj,
                    null, null, null);
            cursor.moveToFirst();
            //图片路径
            String mImagePath = cursor.getString(0);
            Intent intent = new Intent(getActivity(), ChooseIconActivity.class);
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
    public void onEvent(final Bitmap bitmap) {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                saveNewsIconToSQLite(bitmap);
            }
        });
        //修改之后，将头像设置为新头像
        mImgUser.setImageBitmap(bitmap);
    }


    /**
     * 修改头像
     * @param bitmap
     */
    private void saveNewsIconToSQLite(Bitmap bitmap) {
        try {
            UserIconTable table = new UserIconTable();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            table.setUserPhone(MyApplication.mUserInfo.getUserName());
            table.setIconBytes(outputStream.toByteArray());
            LogUtils.i(table.toString());
            //保存或更新头像
            x.getDb(MyApplication.mDaoConfig).saveOrUpdate(table);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录事件接收
     * @param info
     */
    @Subscribe
    public void onEventMainThread(final UserInfo info) {
        mTvName.setText("昵称：" + info.getNickName());
        mTvPhone.setText("手机：" + info.getUserName());
        mTvAge.setText("性别：" + info.getSex());
        mBtnUnlogin.setVisibility(View.VISIBLE);
        //由于在这个方法里面不能做耗时操作，所以查询数据库里面的数据在子线程中进行
        x.task().run(new Runnable() {
            @Override
            public void run() {
                setIconByDb(info.getUserName());
            }
        });

    }

    /**
     * 设置头像
     *
     * @param userName
     */
    private void setIconByDb(String userName) {
        try {
            List<UserIconTable> list = x.getDb(MyApplication.mDaoConfig).findAll(UserIconTable.class);
            if (list != null) {

                byte[] imageBytes = null;
                for (UserIconTable table :
                        list) {
                    if (table.getUserPhone() != null && table.getUserPhone().equals(userName)) {
                        imageBytes = table.getIconBytes();
                    }
                }
                if (imageBytes != null) {
                    LogUtils.i("HasIconBitmap");
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    x.task().post(new Runnable() {
                        @Override
                        public void run() {
                            mImgUser.setImageBitmap(bitmap);
                        }
                    });

                }

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /***
     * 注销登录提醒对话框
     *
     * @param context
     */
    private void showUnloginDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .content("确定要注销当前账户（" + MyApplication.mUserInfo.getUserName() + "）吗？")
                .title("提示")
                .positiveText("注销账户")
                .negativeText("保持登录")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MyApplication.mUserInfo = null;
                        mBtnUnlogin.setVisibility(View.GONE);
                        mTvAge.setText("未登录");
                        mTvPhone.setText("未登录");
                        mTvName.setText("未登录");
                        mImgUser.setImageResource(R.mipmap.icon_person);
                        //发布注销账户登录事件
                        EventBus.getDefault().post(new EventOfUnLoginEntity());
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

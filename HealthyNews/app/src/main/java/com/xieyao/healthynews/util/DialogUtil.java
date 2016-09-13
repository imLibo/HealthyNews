package com.xieyao.healthynews.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.xieyao.healthynews.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by libo on 2016/4/1.
 */
public class DialogUtil {
    /***
     * 加载对话框(可以通过按返回键退出的，主要用于网络请求,并且dialog外获取不到焦点)
     * @param context
     * @return
     */
    public static Dialog getOnLoadingDialogCanCancelByBackPress(Context context){
        Dialog dialog = new Dialog(context, R.style.dialog_loading);
        View view = View.inflate(context, R.layout.loading_dialog_layout,null);
      //  final AVLoadingIndicatorView loadingView = (AVLoadingIndicatorView) view.findViewById(R.id.pointview_loadingdialog);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
      //          loadingView.setVisibility(View.GONE);
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
//                loadingView.setVisibility(View.VISIBLE);
            }
        });
        return dialog;
    }


}

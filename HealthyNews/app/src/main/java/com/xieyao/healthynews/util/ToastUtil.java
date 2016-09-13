package com.xieyao.healthynews.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by libo on 2016/3/28.
 */
public class ToastUtil {

    public static void showMyToast(Context context, String content) {
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

}

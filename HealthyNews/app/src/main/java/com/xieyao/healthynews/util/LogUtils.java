/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.xieyao.healthynews.util;

import android.util.Log;

import com.xieyao.healthynews.MyApplication;


public class LogUtils {

    private static final String TAG = "HealthyNews";

    private LogUtils() {}

    public static void d(String message) {
        if(MyApplication.DEBUG){
            Log.d(TAG, buildMessage(message));
        }
    }

    public static void e(String message) {
        if(MyApplication.DEBUG){
            Log.e(TAG, buildMessage(message));
        }
    }

    public static void i(String message) {
        if(MyApplication.DEBUG){
            Log.i(TAG, buildMessage(message));
        }
    }

    public static void v(String message) {
        if(MyApplication.DEBUG){
            Log.v(TAG, buildMessage(message));
        }
    }

    public static void w(String message) {
        if(MyApplication.DEBUG){
            Log.w(TAG, buildMessage(message));
        }
    }

    public static void wtf(String message) {
        if(MyApplication.DEBUG){
            Log.wtf(TAG, buildMessage(message));
        }
    }

    public static void println(String message) {
        if(MyApplication.DEBUG){
            Log.println(Log.INFO, TAG, message);
        }
    }

    private static String buildMessage(String rawMessage) {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String fullClassName = caller.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return className + "." + caller.getMethodName() + "(): " + rawMessage;
    }
}

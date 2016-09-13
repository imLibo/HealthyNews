package com.xieyao.healthynews.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * 应用程序Activity 管理类，用于管理activity
 * Created by libo on 2016/4/25.
 */
public class ActivityManageAndExit {
    private static Stack<Activity> stackActivities;
    private static ActivityManageAndExit instance;

    private ActivityManageAndExit() {
    }

    /**
     * 单一实例
     */
    public static ActivityManageAndExit getActivityCollector() {
        if (instance == null) {
            instance = new ActivityManageAndExit();
        }
        return instance;
    }

    /**
     * 添加activity到堆栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (stackActivities == null) {
            stackActivities = new Stack<Activity>();
        }
        stackActivities.add(activity);
    }

    /**
     * 结束当前Activity(最后一个压入的即是当前activity)
     *
     */
    public void finishCurrentActivity() {
        Activity activity = stackActivities.lastElement();
        finishAssignActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishAssignActivity(Activity activity) {
        if (activity != null) {
            stackActivities.remove(activity);
            activity.finish();
            activity = null;

        }
    }

    /**
     * 结束所有的activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = stackActivities.size(); i < size; i++) {
            if (null != stackActivities.get(i)) {
                stackActivities.get(i).finish();
            }
        }
        stackActivities.clear();
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        Activity activity = stackActivities.lastElement();
        return activity;
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : stackActivities) {
            if (activity.getClass().equals(cls)) {
                finishAssignActivity(activity);
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

package com.xieyao.healthynews.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/***
 * 偏好文件保存与读取的工具类
 */
public class SharedPreferenceUtil {
	private static final String PREFERENCE_NAME = "preferencesData";
    
	public static boolean getBoolean(Context context,String key) {
		return context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE).getBoolean(key, false);
	}

	public static int getInt(Context context, String key) {
		if (context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE) == null) {
			Log.i(PREFERENCE_NAME, "不存在这个文件");
		}else{
			Log.i(PREFERENCE_NAME, "存在这个文件");
		}
		return context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE).getInt(key, -1);
	}

	public static String getString(Context context, String key) {
		return context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE).getString(key, "");
	}

	public static Map<String, String> getStringValues(Context context,String key) {
		HashMap map = new HashMap();
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
		map.put("key", key);
		map.put("value",preferences.getString(key, ""));
		return map;
	}

	public static void saveBoolean(Context context, String key,Boolean value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
		preferences.edit();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value.booleanValue());
		editor.commit();
	}

	public static void saveInt(Context context, String key,int value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
		preferences.edit();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void saveOBj(Context context, String key,Object value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		if ((value instanceof String))
			editor.putString(key, value.toString());
		else if ((value instanceof Integer))
			editor.putInt(key,((Integer) value).intValue());
		else if ((value instanceof Boolean))
			editor.putBoolean(key,((Boolean) value).booleanValue());
		else if ((value instanceof Float))
			editor.putFloat(key,((Float) value).floatValue());
		else if ((value instanceof Long))
			editor.putLong(key,((Long) value).longValue());
		editor.commit();
	}

	public static void saveString(Context context, String key,String value) {
		SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, context.MODE_PRIVATE);
		preferences.edit();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	class Type<T> {
		private T value;
		Type() {
		}

		public T getValue() {
			return this.value;
		}

		public void setValue(T paramT) {
			this.value = paramT;
		}
	}
}

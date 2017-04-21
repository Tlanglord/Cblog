package com.qiang.cblog.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static SharedPreferences getSharedPreferences(Context context,String name, int mode){
		return context.getSharedPreferences(name, mode);
	}
}
